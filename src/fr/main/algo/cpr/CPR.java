package fr.main.algo.cpr;

import fr.main.algo.Utils;

import java.util.Random;

// CHEMIN DE SOMME MINIMUM ROBOT
public class CPR {
    public static void main(String[] args){
        float[] D = CPR.run(1000, 1000, 5000, 100);
        Utils.save("CPR", D);
    }

    private static float[] run(int Lmax, int Cmax, int Nruns, int Vmax){
        Random rand = new Random();
        float[] D = new float[Nruns];

        for(int r = 0; r < Nruns; r++){
            System.out.println("= Run " + r + "/" + (Nruns-1) + " ==========");
            int L = rand.nextInt(Lmax) + 1;
            int C = rand.nextInt(Cmax) + 1;

            // Génère les grilles de déplacement aléatoirement
            int[][] N = CPR.createSquare(rand, L, C, Vmax);
            int[][] E = CPR.createSquare(rand, L, C, Vmax);
            int[][] NE = CPR.createSquare(rand, L, C, Vmax);

            // Calcule l'ensemble des chemins optimaux
            int[][] M = CPR.calculerM(N, E, NE);

            // Sélectionne le chemin otpimal allant de la case (0, 0) à la case (L - 1, C - 1)
            int v = M[L - 1][C - 1];

            // Affiche le chemin et sa somme
            CPR.acpr(M, L - 1, C - 1, C, L, N, E, NE); // Affiche ce chemin
            System.out.println("Sum of the way: " + v); // Affiche la valeur de ce chemin

            // Calcul la valeur du chemin glouton allant de la case (0, 0) à la case (L - 1, C - 1)
            int g = glouton(N, E, NE, 0, 0);

            // Calcul et ajoute la distance relative de cette run à D
            if(v != 0) D[r] = (g-v) / (float)v;
            else D[r] = 0;
        }

        return D;
    }

    private static int[][] createSquare(Random rand, int l, int c, int maxValue){
        int[][] T = new int[l][c]; // Créer un rectangle de l x c

        for(int i = 0; i < l; i++){
            for(int j = 0; j < c; j++)
                T[i][j] = rand.nextInt(maxValue + 1); // Donne une valeur aléatoire au déplacement de la case (i, j)
        }

        return T;
    }

    private static int[][] calculerM(int[][] N, int[][] E, int[][] NE){
        int L = N.length, C = N[0].length;
        int[][] M = new int[N.length][N[0].length];
        M[0][0] = 0; // M(0, 0) = 0

        // m(0, c) = m(0, c - 1) + E(0, c - 1)
        for(int c=1; c < M[0].length; c++) M[0][c] = M[0][c-1] + E(0, c-1, C, L, E);

        // m(l, 0) = m(l - 1, 0) + N(l - 1, 0)
        for(int l=1; l < M.length; l++) M[l][0] = M[l-1][0] + N(l-1, 0, C, L, N);

        // Cas général
        for(int l = 1; l < L; l++){
            for(int c = 1; c < C; c++){
                // m(l, c) = min( m(l-1, c) + N(l-1, c),
                //                m(l-1, c-1) + E(l-1, c-1),
                //                m(l, c-1) + NE(l, c-1) )
                M[l][c] = min(
                        M[l-1][c] + N(l-1, c, C, L, N),
                        M[l-1][c-1] + E(l-1,c-1, C, L, E),
                        M[l][c-1] + NE(l,c-1, C, L, NE)
                );
            }
        }

        return M;
    } // Theta(L x C)

    // Retourne le coup d'un déplacement EST
    private static int E(int l, int c, int C, int L, int[][] S){
        if(c == C - 1) return 999999;
        return S[l][c+1];
    }

    // Retourne le coup d'un déplacement NORD
    private static int N(int l, int c, int C, int L, int[][] S){
        if(l == L - 1) return 999999;
        return S[l + 1][c];
    }

    // Retourne le coup d'un déplacement NORD-EST
    private static int NE(int l, int c, int C, int L, int[][] S){
        if(l == L-1 || c == C-1) return 999999;
        return S[l + 1][c + 1];
    }

    private static int min(int x, int y, int z){
        if(x <= y && x < z) return x;
        if(y <= z) return y;
        return z;
    }

    // Appel principal : acpr(M, S.length - 1, S[0].length - 1, S[0].length, S.length, S)
    private static void acpr(int[][] M, int l, int c, int C, int L, int[][] N, int[][] E, int[][] NE){
        if(l == 0 && c == 0) { System.out.print("0 "); return; }

        if(l==0){
            acpr(M, 0, c-1, C, L, N, E, NE);
            System.out.printf(" -- %d --> (%d,%d) ", E(0, c-1, C, L, E), l, c);
            return;
        }

        if(c==0){
            acpr(M, l-1, 0, C, L, N, E, NE);
            System.out.printf(" -- %d --> (%d,%d) ", N(l-1, 0, C, L, N), l, c);
            return;
        }

        if(M[l][c] == M[l][c - 1] + E(l,c - 1, C, L, E)){
            acpr(M, l, c-1, C, L, N, E, NE);
            System.out.printf(" -- %d --> (%d,%d) ", E(l, c-1, C, L, E), l, c);
            return;
        }

        if(M[l][c] == M[l - 1][c - 1] + NE(l - 1,c - 1, C, L, NE)){
            acpr(M, l - 1, c - 1, C, L, N, E, NE);
            System.out.printf(" -- %d --> (%d,%d) ", NE(l - 1, c - 1, C, L, NE), l, c);
            return;
        }

        if(M[l][c] == M[l - 1][c] + N(l - 1, c, C, L, N)){
            acpr(M, l - 1, c, C, L, N, E, NE);
            System.out.printf(" -- %d --> (%d,%d) ", N(l-1, c, C, L, N), l, c);
        }
    }

    private static int glouton(int[][] N, int[][] E, int[][] NE, int l, int c){ // Appel principal : glouton(S, 0, 0)
        int g = N[l][c]; // Commence par la case (x,y)
        int L = N.length, C = N[0].length;

        // Tant que le robot n'est pas arrivé à destination (case (L - 1, C - 1))
        while(l < L - 1 || c < C - 1){
            // Prends le coût minimum de déplacement parmi les 3 directions
            int minimum = min(N(l, c, C, L, N), E(l, c, C, L, E), NE(l, c, C, L, NE));

            if(minimum == N(l, c, C, L, N)) l++; // Meilleure direction : Nord
            else if(minimum == E(l, c, C, L, E)) c++; // Meilleure direction : Est
            else { l++; c++; } // Meilleure direction : Nord-Est

            g += minimum; // On ajoute le coût du déplacement que l'on vient d'effectuer
        }

        System.out.println("'Glouton' way is equal to " + g);
        return g;
    } // Theta (L * C)
}
