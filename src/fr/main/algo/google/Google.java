package fr.main.algo.google;

import fr.main.algo.Utils;

import java.util.Random;

public class Google {
    /*
    * A une intersection entre la branche 1 et 2 (la branche 0 étant la suite commune entre la branche 1 et 2)
    * Si la branche 1 à un max plus élevé que la branche 2 alors on choisi d'activer la branche 2 d'abord
    * */

    public static void main(String[] args){
        float[] D = Google.run(1000, 5000, 1000000000);
        Utils.save("CSM", D);
    }

    public static float[] run(int Lmax, int Nruns, int Vmax){
        Random rand = new Random();
        float[] D = new float[Nruns];

        for(int r = 0; r < Nruns; r++){
            System.out.println("= Run " + r + "/" + (Nruns-1) + " ==========");
            int[] P = Google.createPointers(rand, Lmax);
            int[] F = Google.createFun(rand, Lmax, Vmax);
            //int[] M = Google.calculerM(P, F);
            //int v = M[0];
            //System.out.println("Sum of the way: " + v);
            //int g = Google.glouton(T, 0);
            //CSM.acsm(M, T, 0, T.length);
            //if(v != 0) D[r] = (v-g) / (float)v;
            //else D[r] = 0;
            System.out.println("");
        }

        return D;
    }

    public static int[] createPointers(Random rand, int n){
        int[] P = new int[n];

        for(int i = 0; i < n; i++)
            P[i] = rand.nextInt(n);

        return P;
    }

    public static int[] createFun(Random rand, int n, int Vmax){
        int[] F = new int[n];

        for(int i = 0; i < n; i++)
            F[i] = rand.nextInt(Vmax);

        return F;
    }

    /*public static int[] calculerM(int[] P, int[] F){
        int[] M = new int[P.length];
        int currentIndex = 0;

        while(){
            M[currentIndex] =
        }
    }*/
}
