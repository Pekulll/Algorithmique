package fr.main.algo.ssm;

import fr.main.algo.Utils;

import java.util.Random;

public class SSM {
    public static void main(String[] args){
        float[] D = SSM.run(100, 50, 100, 1000, 5000);
        Utils.save("SSM", D);
    }

    public static float[] run(int n, int Tmax, int Vmax, int Cmax, int Nruns){
        Random rand = new Random();
        float[] D = new float[Nruns];

        for (int r = 0; r < Nruns; r++){ // r = nombre de runs
            System.out.println("= Run " + r + "/" + (Nruns-1) + " ==========");

            int S = rand.nextInt(Tmax) + 1; // Taille max
            int P = rand.nextInt(Vmax) + 1; // Valeur max

            // Génère aléatoirement la taille des objets
            int[] T = createSize(n, S, rand); // tailles aléatoires (max=S)
            // Génère aléatoirement la valeur des objets
            int[] V = createValues(n, P, rand); // valeur aléatoires (max=P)

            // Génère aléatoirement la contenance du premier et du second sac
            int C1 = rand.nextInt(Cmax) + 1, C2 = rand.nextInt(Cmax) + 1;

            // Calcul M en fonction de T, V, C1, et C2
            int[][][] M = calculerM(T, V, C1, C2);

            // Calcul la valeur du chemin glouton
            int g = glouton(T, V, C1, C2, rand);
            int v = M[n][C1][C2];

            // Calcul et ajoute la distance relative de cette run
            if(v != 0) D[r] = (v - g) / (float)v;
            else D[r] = 0;

            // Affiche la valeur du chemin optimal
            System.out.printf("Average of the optimum way: %d\n", v);
        }

        return D;
    }

    private static int glouton(int[] T, int[] V, int C1, int C2, Random rand) {
        int[] indices = new int[T.length];
        for(int i = 0; i < indices.length; i++) indices[i] = i;
        // Tri les indices de chaque objet en fonction de leur ratio valeur/taille dans l'ordre décroissant
        Utils.qs(indices, T, V, 0, indices.length, rand);

        int c1 = C1, c2 = C2;
        int g = 0;

        for(int i = 0; i < indices.length; i++){
            if(c1 >= T[indices[i]]){ // l'objet rentre dans le premier sac
                c1 -= T[indices[i]]; // on retire la place que prend l'objet dans le sac 1
                g += V[indices[i]];  // on ajoute la valeur de l'objet
            } else if(c2 >= T[indices[i]]){ // l'objet rentre dans le second sac
                c2 -= T[indices[i]];        // on retire la place que prend l'objet dans le sac 2
                g += V[indices[i]];         // on ajoute la valeur de l'objet
            }
        }

        System.out.printf("Glouton way: %d\n", g);
        return g;
    } // Theta (n)

    public static int[] createSize(int n, int Tmax, Random rand){
        int[] T = new int[n];

        for(int i = 0; i < n; i++){
            T[i] = rand.nextInt(Tmax) + 1;
        }

        return T;
    } // Theta (n)

    public static int[] createValues(int n, int Vmax, Random rand){
        int[] V = new int[n];

        for(int i = 0; i < n; i++){
            V[i] = rand.nextInt(Vmax) + 1;
        }

        return V;
    } // Theta (n)

    public static int[][][] calculerM(int[] T, int[] V, int C1, int C2){
        int n = T.length;
        int[][][] M = new int[n + 1][C1 + 1][C2 + 1];

        // Base : m(0, c1, c2) = 0
        for(int c1 = 0; c1 < C1 + 1; c1++){
            for(int c2 = 0; c2 < C2 + 1; c2++){
                M[0][c1][c2] = 0;
            }
        }

        for(int k = 1; k < n + 1; k++){
            for(int c1 = 0; c1 < C1 + 1; c1++){
                for(int c2 = 0; c2 < C2 + 1; c2++){
                    if(c1 - T[k - 1] >= 0 && c2 - T[k - 1] >= 0){
                        M[k][c1][c2] = max(M[k - 1][c1][c2], M[k - 1][c1 - T[k - 1]][c2], M[k - 1][c1][c2 - T[k - 1]]);
                    } else if(c1 - T[k - 1] >= 0){
                        M[k][c1][c2] = max(M[k - 1][c1][c2], M[k - 1][c1 - T[k - 1]][c2], -1);
                    } else if(c2 - T[k - 1] >= 0){
                        M[k][c1][c2] = max(M[k - 1][c1][c2], -1, M[k - 1][c1][c2 - T[k - 1]]);
                    } else{
                        M[k][c1][c2] = M[k - 1][c1][c2];
                    }

                    M[k][c1][c2] += V[k - 1];
                }
            }
        }

        return M;
    } // Theta (n * C1 * C2)

    private static int max(int x, int y, int z){
        if(x >= y && x >= z) return x;
        if(y >= z) return y;
        return z;
    }
}
