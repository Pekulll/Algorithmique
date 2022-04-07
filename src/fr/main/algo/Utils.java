package fr.main.algo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
                myWriter.write("\n" + Utils.tabToString(results));
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
            r += "" + x + "\n";
        }
        return r;
    }
}
