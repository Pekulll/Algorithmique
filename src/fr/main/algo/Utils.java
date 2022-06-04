package fr.main.algo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Utils {
    public static void save(String filename, float[] D){
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

        float[] results = new float[]{average, mediane, variance, ecart};

        // Ecriture dans le CSV inspiré de
        // https://www.delftstack.com/fr/howto/java/how-to-create-a-file-and-write-data-to-it-in-java/
        try {
            File myObj = new File(filename + ".csv");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }

            try {
                FileWriter myWriter = new FileWriter(filename + ".csv");
                myWriter.write("DATA\n");
                myWriter.write(Utils.tabToString(D));
                myWriter.write("\nResults\n" + Utils.tabToString(results));
                myWriter.close();
                System.out.println("Successfully wrote to the file.");
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static String tabToString(float[] tab){
        String r = "";

        for(float x : tab){
            r += ("" + x + "\n").replace(".", ",");
        }

        return r;
    }

    public static void qs(int[] tab, int[] T, int[] V, int i, int j, Random rand){
        if (j-i <= 1) return ; // le sous-tableau T[i:j] est croissant
        // ici : j-i >= 2
        int k = segmenter(tab, T, V, i, j, rand); // T[i:k] <= T[k] < T[k+1:j]    <<< (1)
        qs(tab, T, V,i,k, rand);   // (1) et T[i:k] croissant
        qs(tab, T, V,k+1,j, rand); // (1) et T[i:k] croissant et T[k+1:j] croissant, donc T[i:j] croissant
    }

    public static int segmenter(int[] tab, int[] T, int[] V, int i, int j, Random rand){
        int r = i + rand.nextInt(j-i);
        permuter(tab,i,r);
        int k = i, jp = k+1; // I(k,j')

        while (jp < j) // I(k,j') et jp < j
            if (V[k] / (float)T[k] >= V[jp] / (float)T[jp]) // I(k,j'+1)
                jp++; // I(k,j')
            else {
                permuter(tab,jp,k+1);
                permuter(tab,k,k+1); // I(k+1,j'+1)
                k++; jp++; // I(k,jp)
            }

        return k;
    }

    public static void permuter(int[] T, int i, int j){
        int ti = T[i];
        T[i] = T[j];
        T[j] = ti;
    }
}
