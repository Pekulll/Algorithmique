package fr.main.algo.csm;

import fr.main.algo.Utils;

import java.util.Random;

// CHEMIN SOMME MAX DANS UN TRIANGLE
public class CSM {
    public static void main(String[] args){
        float[] D = CSM.run(1000, 5000, 100);
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

        //Utils.save("CSM", D, new float[]{average, mediane, variance, ecart});
    }

    public static float[] run(int Lmax, int Nruns, int Vmax){
        Random rand = new Random();
        float[] D = new float[Nruns];

        for(int r = 0; r < Nruns; r++){
            System.out.println("= Run " + r + "/" + (Nruns-1) + " ==========");
            int[] T = CSM.createTriangle(rand, rand.nextInt(Lmax) + 1, Vmax);
            int[] M = CSM.calculerM(T);
            int v = M[0];
            System.out.println("Sum of the way: " + v);
            int g = glouton(T, 0);
            CSM.acsm(M, T, 0, T.length);
            if(v != 0) D[r] = (v-g) / (float)v;
            else D[r] = 0;
            System.out.println("");
        }

        return D;
    }

    public static int[] createTriangle(Random rand, int m, int maxValue){
        int length = (int)(m*(m+1)/2);
        System.out.println("Triangle with length=" + length + " generated!");

        int[] T = new int[length];

        for(int i = 0; i < length; i++){
            T[i] = rand.nextInt(maxValue + 1);
        }

        return T;
    }

    public static int g(int i){
        if(i == 0) return 1;

        int level = (int)((Math.sqrt(1 + 8 * i) - 1) / 2); // Niveau de l'indice i
        int position = (int)(i - (level * (level + 1) / 2)); // Position de l'indice i dans le niveau 'level'

        // Position du descendant gauche de i
        int left = (int)((level + 2) * (level + 3) / 2f - level + position - 2);
        return left;
    }

    public static int getSum(int[] T, int[] W){
        if(W == null) return 0;

        int sum = 0;
        for(int e : W){
            sum += T[e];
        }
        return sum;
    }

    public static int[] calculerM(int[] T){
        int[] M = new int[T.length];
        int i = T.length - 1;

        while(i >= 0){
            int left = g(i);

            if(left >= T.length){
                M[i] = T[i];
            }else{
                if(M[left] > M[left + 1])
                    M[i] = M[left] + T[i];
                else
                    M[i] = M[left + 1] + T[i];
            }

            i--;
        }

        // System.out.println("Number of ways: " + M.length + ". ");
        return M;
    }

    public static void acsm(int[] M, int[] T, int i, int n){
        if(i >= n) return;
        System.out.printf("T[%d]=%d\n", i, T[i]);

        int left = g(i);

        if(M[left] > M[left + 1])
            acsm(M, T, left, n);
        else acsm(M, T, left + 1, n);
    }

    public static int glouton(int[] T, int i){
        int g = T[i];
        int left = g(i);

        while(left < T.length){
            if(left + 1 >= T.length){
                i = left;
            }else if(T[left] > T[left + 1]){
                i = left;
            }else{
                i = left + 1;
            }

            g += T[i];
            left = g(i);
        }

        System.out.println("'Glouton' way is equal to " + g);
        return g;
    }
}
