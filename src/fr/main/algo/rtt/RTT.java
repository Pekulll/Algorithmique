package fr.main.algo.rtt;

import fr.main.algo.Utils;

import java.util.Random;

public class RTT {
    public static void main(String[] args){
        float[] D = RTT.run(1000, 5000, 100);
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

        Utils.save("CSM", D, new float[]{average, mediane, variance, ecart});
    }

    public static float[] run(int Lmax, int Nruns, int Vmax){
        Random rand = new Random();
        float[] D = new float[Nruns];

        for(int r = 0; r < Nruns; r++){
            /*System.out.println("= Run " + r + "/" + (Nruns-1) + " ==========");
            //int[] T = RTT.createTriangle(rand, rand.nextInt(Lmax) + 1, Vmax);
            //int[][] M = RTT.calculerM(T);
            //int v = RTT.getSum(T, M[0]);
            System.out.println("Sum of the way: " + v);
            //int g = glouton(T, 0);
            // CMS.ascm(M, T, 0, T.length);
            if(v != 0) D[r] = (v-g) / (float)v;
            else D[r] = 0;
            System.out.println("");*/
        }

        return D;
    }
}
