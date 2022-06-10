package fr.main.algo.ssm;

import fr.main.algo.Utils;

import java.util.Random;

public class SSM {
    public static void main(String[] args){
        // method='v' ==> glouton par valeur
        float[] V = SSM.run(100, 50, 100, 1000, 5000, 'v');
        Utils.save("SSM_value", V);

        // method='v' ==> glouton par densité de valeur
        float[] D = SSM.run(100, 50, 100, 1000, 5000, 'd');
        Utils.save("SSM_density", D);
    }

    private static float[] run(int Nmax, int Tmax, int Vmax, int Cmax, int Nruns, char method){
        Random rand = new Random();
        float[] D = new float[Nruns];

        for (int r = 0; r < Nruns; r++){ // r = nombre de runs
            System.out.println("= Run " + r + "/" + (Nruns-1) + " ==========");

            int n = rand.nextInt(Nmax - 9) + 10;
            int S = rand.nextInt(Tmax) + 1; // Taille max
            int P = rand.nextInt(Vmax) + 1; // Valeur max

            // Génère aléatoirement la taille des objets
            int[] T = createSize(n, S, rand); // tailles aléatoires (max=S)
            // Génère aléatoirement la valeur des objets
            int[] V = createValues(n, P, rand); // valeur aléatoires (max=P)

            // Génère aléatoirement la contenance du premier et du second sac
            int C = rand.nextInt(Cmax) + 1;

            // Calcul M en fonction de T, V, C
            int[][] M = calculerM(T, V, C);

            // Calcul la valeur du chemin glouton
            int g = glouton(T, V, C, rand, method);
            int v = M[n][C];

            // Calcul et ajoute la distance relative de cette run
            if(v != 0) D[r] = (v - g) / (float)v;
            else D[r] = 0;

            // Affiche la valeur du chemin optimal
            System.out.printf("Average of the optimum way: %d\n", v);
        }

        return D;
    }

    private static int[][] calculerM(int[] V, int[] T, int C) {
        int n = V.length;
        int[][] M = new int[n + 1][C + 1];

        // base
        for (int c = 0; c < C + 1; c++) {
            M[0][c] = 0;
        }

        // cas general
        for (int k = 1; k < n + 1; k++)
            for (int c = 0; c < C + 1; c++) {
                if (c - T[k - 1] < 0) {
                    M[k][c] = M[k - 1][c];
                } else {
                    M[k][c] = max(M[k - 1][c], V[k - 1] + M[k - 1][c - T[k - 1]]);
                }
            }

        return M;
    } // Theta (n * C)

    private static int glouton(int[] T, int[] V, int C, Random rand, char method) {
        int[] indices = new int[T.length];
        for(int i = 0; i < indices.length; i++) indices[i] = i;

        // Tri les indices de chaque objet en fonction de leur ratio valeur/taille dans l'ordre décroissant
        if(method == 'd') Utils.qsRatio(indices, T, V, 0, indices.length, rand);
        // Tri les indices de chaque objet en fonction de leur valeur dans l'ordre décroissant
        else if(method == 'v') Utils.qs(indices, V, 0, indices.length, rand);

        int c = C;
        int g = 0;

        for(int index : indices){
            if(T[index] > c) continue;

            // l'objet rentre dans le sac
            c -= T[index]; // on retire la place que prend l'objet dans le sac
            g += V[index];  // on ajoute la valeur de l'objet
        }

        System.out.printf("Glouton way: %d\n", g);
        return g;
    } // Theta (n)

    private static int[] createSize(int n, int Tmax, Random rand){
        int[] T = new int[n];

        for(int i = 0; i < n; i++){
            T[i] = rand.nextInt(Tmax) + 1;
        }

        return T;
    } // Theta (n)

    private static int[] createValues(int n, int Vmax, Random rand){
        int[] V = new int[n];

        for(int i = 0; i < n; i++){
            V[i] = rand.nextInt(Vmax) + 1;
        }

        return V;
    } // Theta (n)

    private static int max(int x, int y){
        if(x >= y) return x;
        return y;
    }
}
