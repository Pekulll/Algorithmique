package fr.main.algo.rtt;

import fr.main.algo.Utils;

import java.util.Arrays;
import java.util.Random;

// REPARTITION OPTIMALE TEMPS DE TRAVAIL
public class RTT {
    public static void main(String[] args){
        float[] D = RTT.run(15, 10, 5000);
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

        Utils.save("RTT", D, new float[]{average, mediane, variance, ecart});
    }

    public static float[] run(int Lmax, int Hmax, int Nruns){
        float[] D = new float[Nruns];

        for (int r = 0; r < Nruns; r++){ // r = nombre de runs
            System.out.println("= Run " + r + "/" + (Nruns-1) + " ==========");
            int[][] E = estimate(Lmax, Hmax); // notes aléatoires, croissantes selon Hmax
            int[][][] MA = calculerMA(E);
            int[][] M = MA[0], A = MA[1];

            aro(A, E, Lmax, Hmax);

            float g = glouton(E, Hmax) / (float)Lmax;
            float maxAverage = (float)M[Lmax][Hmax] / Lmax;

            if(maxAverage != 0) D[r] = (maxAverage-g) / maxAverage;
            else D[r] = 0;

            String averageStr = String.format("%.2f", maxAverage);
            System.out.printf("Average of the optimum way: %s/20\n", averageStr);

            if (M[Lmax][Hmax] == 20 * Lmax){
                System.out.println("\n === Useless to work anymore ===");
            }
        }

        return D;
    }

    public static int glouton(int[][] E, int Hmax) {
        int remaining = Hmax, sum = 0;

        for(int l = 0; l < E.length; l++){
            int max = 0, hoursNeeded = 0;

            for(int h = 0; h < E[l].length; h++){
                if(h > remaining) break;

                if(max < E[l][h]){
                    max = E[l][h];
                    hoursNeeded = h;
                }
            }

            sum += max;
            remaining -= hoursNeeded;
        }

        System.out.println("Glouton average: " + (sum / (float)E.length) + "/20");
        return sum;
    }

    public static int[][][] calculerMA(int[][] E){	// E : tableau des notes estimées.
        // E[0:n][0:H+1] est de terme général E[i][h] = e(i,h).
        // Retourne M et A : M[0:n+1][0:H+1] de terme général M[k][h] = m(k,h), somme maximum
        // des notes d'une répartition de h heures sur le sous-ensemble des k premières unités.
        int n = E.length, H = E[0].length - 1;
        int[][] M = new int[n+1][H+1], A = new int[n+1][H+1];
        // base, k = 0.
        int s0 = 0; // somme des notes pour 0 heure travaillée
        for (int i = 0; i < n; i++)
            s0 = s0 + E[i][0];
        // Base : m(0,h) = s0 pour tout h, 0 ≤ h < H+1
        for (int h = 0; h < H+1; h++)
            M[0][h] = s0;
        // Cas général, 1 ≤ k < n+1 pour tout h, h, 0 ≤ h < H+1 :
        // m(k,h) = ( Max m(k-1, h - h_k) + e(k-1,h_k) sur h_k, 0 ≤ h_k < h+1 ) - e(k-1,0)
        // Calcul des valeur m(k,h) par k croissants et mémorisation dans le tableau M.
        // Calcul à la volée des a(k,h) = arg m(k,h) et mémorisation dans le tableau A.
        for (int k = 1; k < n+1; k++) // par tailles k croissantes
            for (int h = 0; h < H+1; h++){ // calcul des valeurs m(k,h), 0 ≤ h < H+1
                // Calcul de M[k][h] =
                // ( Max M[k-1][h-h_k] + e(k-1,h_k), h_k, 0 ≤ h_k < h+1 ) - e(k-1,0)
                M[k][h] = -1;
                for (int h_k = 0; h_k < h+1; h_k++){
                    int mkhh_k = M[k-1][h - h_k] + E[k-1][h_k];
                    if (mkhh_k > M[k][h]){
                        M[k][h] = mkhh_k;
                        A[k][h] = h_k;
                    }
                }
                // M[k][h] = (max M[k-1][h-h_k] + e(k-1,h_k), h_k, 0 ≤ h_k < h+1)
                M[k][h] = M[k][h] - E[k-1][0];  // M[k][h] = m(k,h)
            }
        return new int[][][] {M, A};
    } // complexité Theta(n x H^2).

    public static void aro(int[][] A, int[][] E, int k, int h){
        // affiche ro(k,h) : répartition optimale de h heures sur les k premières unités.
        if (k == 0) return; // sans rien faire, ro(0,h) a été affichée.
        // ici : k > 0
        // ro(k,h) = ro(k-1,h-a(k,h)) union {"k-1 <-- a(k,h)"}
        int akh = A[k][h]; // nombre d'heures allouées à la k-ème unité dans ro(k,h)
        aro(A,E,k-1,h-akh); // ro(k-1,h-akh) a été affichée
        System.out.printf("unité %d, <-- %d heures, note estimée %d\n",
                k-1, akh, E[k-1][akh]);
        // le nombre d'heures allouées à la kème unité a été affiché
        // Ainsi :
        // 1) La répartition optimale ro(k-1,h-akh) a été affichée,
        // 2) "k-1 <-- akh" a été affichée,
        // 3) donc ro(k,h) = ro(k-1,h-akh) union {"k-1 <-- akh"}
        // a été affichée.
    } // Complexité Theta(n).

    public static int[][] estimate(int n, int H){ // retourne E[0:n][0:H+1] de terme général
        // E[i][h] = e(i,h). Les estimations sont aléatoires, croissantes selon h.
        int[][] E = new int[n][H+1];
        Random rand = new Random(); // pour génération aléatoire des notes estimées.
        for (int i = 0; i < n; i++) E[i][0] = 6 + rand.nextInt(5);
        for (int i = 0; i < n; i++)
            for (int h = 1; h < H+1; h++)
                E[i][h] = min( E[i][h-1] + (1+rand.nextInt(5)), 20) ;
        return E;
    }

    public static int min(int x, int y){
        if (x<=y) return x;
        return y;
    }
}
