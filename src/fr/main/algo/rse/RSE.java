package fr.main.algo.rse;

import fr.main.algo.Utils;

import java.util.Random;

public class RSE {
    public static void main(String[] args){
        float[] D = RSE.run(1000, 1000, 5000);
        float average = 0, mediane = 0, variance = 0, ecart = 0;
        float max=-1, min=100;

        for(float d : D){
            average += d;

            if(max < d) max = d;
            if(min > d) min = d;
        }

        mediane = (max - min) / 2;
        average /= D.length;

        for(float d : D){
            variance += Math.pow(d - average, 2);
        }

        variance /= D.length;
        ecart = (float)Math.sqrt(variance);

        System.out.println("= Results ==========");
        System.out.println("Moyenne : " + average);
        System.out.println("Mediane : " + mediane);
        System.out.println("Variance : " + variance);
        System.out.println("Ecart-type : " + ecart);

        Utils.save("RSE", D, new float[]{average, mediane, variance, ecart});
    }

    public static float[] run(int Emax, int Smax, int Nruns){
        Random rand = new Random();
        float[] D = new float[Nruns];

        for (int r = 0; r < Nruns; r++){ // r = nombre de runs
            System.out.println("= Run " + r + "/" + (Nruns-1) + " ==========");

            int[][] G = estimate(rand, rand.nextInt(Emax) + 1, rand.nextInt(Smax) + 1); // gains aléatoires
            int[][][] MA = calculerMA(G);
            int[][] M = MA[0], A = MA[1];

            int g = glouton(G);
            int v = 0;

            int s = G[0].length - 1;
            for(int k = G.length; k > 0; k--){
                int aks = A[k][s];
                s -= aks;
                v += G[k-1][aks];
            }

            if(v != 0) D[r] = (v-g) / (float)v;
            else D[r] = 0;

            //aro(A, G, G.length, G[0].length - 1);
            System.out.printf("Sum of the optimum way: %d\n", v);
            System.out.printf("Stock max = %d\n", G[0].length - 1);
        }

        return D;
    }

    public static int glouton(int[][] G) {
        int remaining = G[0].length - 1, sum = 0;

        for(int l = 0; l < G.length; l++){
            int max = G[l][0], stockNeeded = 0;

            for(int s = 1; s < G[l].length; s++){
                if(s > remaining) break;

                if(max < G[l][s]){
                    max = G[l][s];
                    stockNeeded = s;
                }
            }

            sum += max;
            remaining -= stockNeeded;
            //System.out.printf("Entrepot %d : livré = %d, gain = %d\n", l, stockNeeded, max);
        }

        System.out.println("Glouton: " + sum);
        return sum;
    }

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
        for (int i = 0; i < n; i++) G[i][0] = rand.nextInt(10) - 3;

        for (int i = 0; i < n; i++)
            for (int s = 1; s < S+1; s++)
                G[i][s] = max(G[i][s-1] + rand.nextInt(10) - 3, 0);

        return G;
    }

    public static int max(int x, int y){
        if (x>=y) return x;
        return y;
    }
}
