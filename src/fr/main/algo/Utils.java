package fr.main.algo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Utils {
    public static int[] copy(int[] tab){
        int[] copy = new int[tab.length];
        System.arraycopy(tab, 0, copy, 0, tab.length);
        return copy;
    }

    public static int[] copyAndAdd(int[] tab, int x){
        int[] copy = new int[tab.length + 1];
        System.arraycopy(tab, 0, copy, 0, tab.length);
        copy[tab.length] = x;
        return copy;
    }

    public static boolean contains(int[] M, int i){
        for(int x : M){
            if(x == i) return true;
        }

        return false;
    }

    public static void save(String filename, float[] D, float[] results){
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

    public static int segmenter(int[] tab, int[] T, int[] V, int i, int j, Random rand){ /* Calcule une permutation des valeurs de T[i:j]
	vérifiant T[i:k] <= T[k] < T[k+1:j], et retourne k. Fonction construite sur la propriété
	I(k,j') : T[i:k] <= T[k] < T[k+1:j']. Arrêt j'=j. Initialisation : k = i, j'=k+1.
	Progression : I(k,j') et j'<j et t_{j'}>t_{k} ==> I(k,j'+1)
	I(k,j') et j'<j et t_{j'}<=t_{k} et T[k]=t_{j'} et T[k+1]=t_{k} et T[j']=t_{k+1}
		==> I(k+1,j'+1) */
        int r = i + rand.nextInt(j-i);
        permuter(tab,i,r); // Ligne expliquée la semaine prochaine
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

    public static void permuter(int[] T, int i, int j){ int ti = T[i];
        T[i] = T[j];
        T[j] = ti;
    }
}
