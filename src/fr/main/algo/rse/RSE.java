package fr.main.algo.rse;

import fr.main.algo.Utils;

import java.util.Random;

public class RSE {
    public static void main(String[] args){
        float[] D = RSE.run(1000, 1000, 5000);
        Utils.save("RSE", D);
    }

    public static float[] run(int Emax, int Smax, int Nruns){
        Random rand = new Random();
        float[] D = new float[Nruns];

        for (int r = 0; r < Nruns; r++){ // r = nombre de runs
            System.out.println("= Run " + r + "/" + (Nruns-1) + " ==========");

            int E = rand.nextInt(Emax) + 1;
            int S = rand.nextInt(Smax) + 1;

            // Créer les gains aléatoirement pour chaque entrepôt en fonction du stock
            int[][] G = estimate(rand, E, S);

            /* VALEURS DE TESTS
            int[][] G = new int[][] // g(k,s) = gain obtenu d'une livraison
                    // d'une quantité de stock s à l'entrepôt k
                    {    { 0, 5, 5, 7, 7,10,10,12,12,13,13}, //
                            { 0, 8,10,10,10,12,12,14,14,14,14},
                            {0,10,10,12,12,13,13,14,15,16,16},
                            {0,14,14,14,16,16,16,16,16,16,16},
                            {0,10,14,14,14,14,14,14,14,16,16},
                            {0,10,12,12,16,16,16,16,16,16,16},
                            {0,12,12,14,14,15,15,15,17,17,17}
                    } ;

            int S = G[0].length - 1;
            int E = G.length;*/

            // Calcul M et A en fonction de G
            int[][][] MA = calculerMA(G);
            int[][] M = MA[0], A = MA[1];

            // Calcul la valeur du chemin glouton
            int g = glouton(G);

            // Récupération de la valeur du chemin optimal
            int v = M[E][S];

            // Affiche le chemin optimal
            RSE.aro(A, G, G.length, G[0].length - 1);

            // Calcul et ajoute la distance relative de cette run
            if(v != 0) D[r] = (v-g) / (float)v;
            else D[r] = 0;

            // Affiche la valeur du chemin optimal
            System.out.printf("Sum of the optimum way: %d\n", v);
        }

        return D;
    }

    public static int glouton(int[][] G) {
        int remaining = G[0].length - 1, sum = 0;
        int[] hoursAllocated = new int[G.length];

        for(int e = 0; e < G.length; e++){
            hoursAllocated[e] = 0;
            sum += G[e][0];
        }

        while(remaining > 0){
            int max = 0, allocatedTo = 0;

            for(int e = 0; e < G.length; e++){
                // Le nombre de stock alloué à cet entrepôt est maximum
                if(hoursAllocated[e] + 1 >= G[e].length) continue;

                if(max < G[e][hoursAllocated[e] + 1] - G[e][hoursAllocated[e]]){ // g(l, h + 1) - g(l, h)
                    max = G[e][hoursAllocated[e] + 1] - G[e][hoursAllocated[e]]; // Nouveau gain maximum
                    allocatedTo = e;
                }
            }

            sum += max; // On ajoute le gain max
            hoursAllocated[allocatedTo]++; // On ajoute 1 au stock envoyé à l'entrepôt qui correspond au gain max
            remaining--; // On enlève 1 au stock restant
        }

        System.out.println("Glouton: " + sum);
        return sum;
    } // Theta (E * S)

    public static int[][][] calculerMA(int[][] G){	// G : tableau des gains estimés.
        int n = G.length, S = G[0].length - 1;
        int[][] M = new int[n + 1][S + 1], A = new int[n + 1][S + 1];

        // base, m(0, s) = 0.
        for (int i = 0; i < S + 1; i++)
            M[0][i] = 0;

        for (int k = 1; k < n+1; k++) {
            for (int s = 0; s < S + 1; s++) {
                M[k][s] = -999999;
                for (int s_k = 0; s_k < s + 1; s_k++) {
                    int mkss_k = M[k - 1][s_k] + G[k - 1][s_k];

                    if (mkss_k > M[k][s]) {
                        M[k][s] = mkss_k;
                        A[k][s] = s_k;
                    }
                }
            }
        }

        return new int[][][] {M, A};
    } // complexité Theta(n x S^2).

    public static void aro(int[][] A, int[][] G, int k, int s){
        if(k == 0) return;
        int aks = A[k][s];
        aro(A, G, k-1, s-aks);
        System.out.printf("Entrepot %d : stock livré = %d, gain = %d\n", k-1, aks, G[k-1][aks]);
    } // Complexité Theta(n).

    public static int[][] estimate(Random rand, int n, int S){ // retourne G[0:n][0:H+1] de terme général
        // G[i][h] = g(i,h). Les gains sont aléatoires, croissantes selon s.
        int[][] G = new int[n][S+1];

        // Initialise les gains pour aucun stock
        for (int i = 0; i < n; i++) G[i][0] = 0;

        // Génère aléatoirement les gains pour des stock > 1, pour tous les entrepôts
        for (int i = 0; i < n; i++)
            for (int s = 1; s < S+1; s++)
                G[i][s] = max(G[i][s-1] + rand.nextInt(5), 0);

        return G;
    }

    public static int max(int x, int y){
        if (x>=y) return x;
        return y;
    }
}
