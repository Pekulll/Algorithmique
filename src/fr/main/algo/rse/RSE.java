package fr.main.algo.rse;

import fr.main.algo.Utils;
import fr.main.algo.rtt.RTT;

import java.util.Random;

public class RSE {
    public static void main(String[] args){
        float[] D = RSE.run(20, 100, 5000);
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

    public static float[] run(int Lmax, int Smax, int Nruns){
        Random rand = new Random();
        float[] D = new float[Nruns];

        for (int r = 0; r < Nruns; r++){ // r = nombre de runs
            System.out.println("= Run " + r + "/" + (Nruns-1) + " ==========");

            int[][] G = estimate(rand, Lmax, Smax); // gains aléatoires, croissantes selon Smax
            int[][][] MA = calculerMA(G);
            int[][] M = MA[0], A = MA[1];

            aro(A, G, Lmax, Smax);

            int g = glouton(G, Smax);
            int v = M[Lmax - 1][Smax];

            if(v != 0) D[r] = (v-g) / (float)v;
            else D[r] = 0;

            System.out.printf("Sum of the optimum way: %d\n", v);
        }

        return D;
    }

    public static int glouton(int[][] G, int Smax) {
        int remaining = Smax, sum = 0;

        for(int l = 0; l < G.length; l++){
            int max = 0, stockNeeded = 0;

            for(int s = 0; s < G[l].length; s++){
                if(s > remaining) break;

                if(max < G[l][s]){
                    max = G[l][s];
                    stockNeeded = s;
                }
            }

            sum += max;
            remaining -= stockNeeded;
        }

        System.out.println("Glouton average: " + sum);
        return sum;
    }

    public static int[][][] calculerMA(int[][] G){	// G : tableau des gains estimés.
        // G[0:n][0:H+1] est de terme général G[i][h] = g(i,h).
        // Retourne M et A : M[0:n+1][0:S+1] de terme général M[k][s] = m(k,s), gain maximum
        // des notes d'une répartition de h heures sur le sous-ensemble des k premières unités.
        int n = G.length, S = G[0].length;
        int[][] M = new int[n+1][S], A = new int[n+1][S];

        // base, m(0, s) = 0.
        for (int i = 0; i < S; i++)
            M[0][i] = 0;

        // base, m(k, 0) = 0.
        for (int k = 0; k < n; k++)
            M[k][0] = 0 ;

        // Cas général, 1 ≤ k < n+1 pour tout h, h, 0 ≤ h < H+1 :
        // m(k,h) = ( Max m(k-1, s - s_k) + g(k-1,s_k) sur s_k, 0 ≤ s_k < S+1 ) - e(k-1,0)
        // Calcul des valeur m(k,s) par k croissants et mémorisation dans le tableau M.
        // Calcul à la volée des a(k,s) = arg m(k,s) et mémorisation dans le tableau A.
        for (int k = 1; k < n+1; k++) { // par tailles k croissantes
            for (int s = 0; s < S; s++) { // calcul des valeurs m(k,s), 0 ≤ s < S+1
                // Calcul de M[k][s] =
                // ( Max M[k-1][s-s_k] + g(k-1,s_k), s_k, 0 ≤ s_k < S+1 ) - e(k-1,0)
                M[k][s] = -1;

                for (int s_k = 0; s_k < s + 1; s_k++) {
                    int mkss_k = M[k - 1][s - s_k] + G[k - 1][s_k];
                    if (mkss_k > M[k][s]) {
                        M[k][s] = mkss_k;
                        A[k][s] = s_k;
                    }
                }
                // M[k][s] = (max M[k-1][s-s_k] + g(k-1,s_k), s_k, 0 ≤ s_k < S+1)
                // M[k][s] = M[k][s] + G[k - 1][0];  // M[k][s] = m(k,s) !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            }
        }

        return new int[][][] {M, A};
    } // complexité Theta(n x S^2).

    public static void aro(int[][] A, int[][] E, int k, int h){
        // affiche ro(k,h) : répartition optimale de h heures sur les k premières unités.
        if (k == 0) return; // sans rien faire, ro(0,h) a été affichée.
        // ici : k > 0
        // ro(k,h) = ro(k-1,h-a(k,h)) union {"k-1 <-- a(k,h)"}
        int akh = A[k][h]; // nombre d'heures allouées à la k-ème unité dans ro(k,h)
        aro(A,E,k-1,h-akh); // ro(k-1,h-akh) a été affichée
        System.out.printf("entrepôt %d, <-- %d stocks, gain estimé %d\n",
                k-1, akh, E[k-1][akh]);
        // le nombre d'heures allouées à la kème unité a été affiché
        // Ainsi :
        // 1) La répartition optimale ro(k-1,h-akh) a été affichée,
        // 2) "k-1 <-- akh" a été affichée,
        // 3) donc ro(k,h) = ro(k-1,h-akh) union {"k-1 <-- akh"}
        // a été affichée.
    } // Complexité Theta(n).

    public static int[][] estimate(Random rand, int n, int S){ // retourne G[0:n][0:H+1] de terme général
        // G[i][h] = g(i,h). Les gains sont aléatoires, croissantes selon s.
        int[][] G = new int[n][S+1];
        for (int i = 0; i < n; i++) G[i][0] = rand.nextInt(10);

        for (int i = 0; i < n; i++)
            for (int s = 1; s < S+1; s++)
                G[i][s] = max(G[i][s-1] + rand.nextInt(10) - 5, 0);

        return G;
    }

    public static int max(int x, int y){
        if (x>=y) return x;
        return y;
    }
}
