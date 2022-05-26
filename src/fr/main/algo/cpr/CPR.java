package fr.main.algo.cpr;

import fr.main.algo.Utils;

import java.util.Random;

// CHEMIN DE SOMME MINIMUM ROBOT
public class CPR {
    public static void main(String[] args){
        float[] D = CPR.run(1000, 1000, 5000, 100);
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

        Utils.save("CPR", D, new float[]{average, mediane, variance, ecart});
    }

    public static float[] run(int Lmax, int Cmax, int Nruns, int Vmax){
        Random rand = new Random();
        float[] D = new float[Nruns];

        for(int r = 0; r < Nruns; r++){
            System.out.println("= Run " + r + "/" + (Nruns-1) + " ==========");
            int[][] S = CPR.createSquare(rand, rand.nextInt(Lmax) + 1, rand.nextInt(Cmax) + 1, Vmax);
            int[][] M = CPR.calculerM(S);
            int v = M[S.length - 1][S[0].length - 1];
            System.out.println("Sum of the way: " + v);
            int g = glouton(S, 0, 0);
            //CPR.acpr(M, 0, 0, S[0].length, S.length, S);
            if(v != 0) D[r] = (v-g) / (float)v;
            else D[r] = 0;
            System.out.println("");
        }

        return D;
    }

    public static int[][] createSquare(Random rand, int l, int c, int maxValue){
        int[][] T = new int[l][c];

        for(int i = 0; i < l; i++){
            for(int j = 0; j < c; j++)
                T[i][j] = rand.nextInt(maxValue + 1);
        }

        System.out.println("Square (l=" + l + ", c=" + c + ") created!");
        return T;
    }

    public static int[][] calculerM(int[][] S){
        int L = S.length, C = S[0].length;
        int[][] M = new int[S.length][S[0].length];
        M[0][0] = 0;

        for(int c=1; c < M[0].length; c++) M[0][c] = M[0][c-1] + E(0, c-1, C, L, S);
        for(int l=1; l < M.length; l++) M[l][0] = M[l-1][0] + N(l-1, 0, C, L, S);

        for(int l = 1; l < S.length; l++){
            for(int c = 1; c < S[0].length; c++){
                M[l][c] = min(
                        M[l-1][c] + N(l-1, c, C, L, S),
                        M[l-1][c-1] + E(l-1,c-1, C, L, S),
                        M[l][c-1] + NE(l,c-1, C, L, S)
                );
            }
        }

        return M;
    } // Theta(L x C)

    private static int E(int l, int c, int C, int L, int[][] S){
        if(c == C - 1) return 999999;
        return S[l][c+1];
    }

    private static int N(int l, int c, int C, int L, int[][] S){
        if(l == L - 1) return 999999;
        return S[l + 1][c];
    }

    private static int NE(int l, int c, int C, int L, int[][] S){
        if(l == L-1 || c == C-1) return 999999;
        return S[l + 1][c + 1];
    }

    private static int min(int x, int y, int z){
        if(x <= y && x < z) return x;
        if(y <= z) return y;
        return z;
    }

    public static void acpr(int[][] M, int l, int c, int C, int L, int[][] S){
        if(l == 0 || c == C) { System.out.print("0 "); return; }

        if(l==0){
            acpr(M, 0, c-1, C, L, S);
            System.out.printf(" -- %d --> (%d,%d) ", E(0, c-1, C, L, S), l, c);
            return;
        }

        if(c==0){
            acpr(M, l-1, 0, C, L, S);
            System.out.printf(" -- %d --> (%d,%d) ", E(l-1, 0, C, L, S), l, c);
            return;
        }

        if(M[l][c] == M[l][c] + E(l,c - 1, C, L, S)){
            acpr(M, l, c-1, C, L, S);
            System.out.printf(" -- %d --> (%d,%d) ", E(l, c-1, C, L, S), l, c);
            return;
        }

        if(M[l][c] == M[l][c] + NE(l - 1,c - 1, C, L, S)){
            acpr(M, l - 1, c - 1, C, L, S);
            System.out.printf(" -- %d --> (%d,%d) ", NE(l - 1, c - 1, C, L, S), l, c);
            return;
        }

        if(M[l][c] == M[l][c] + N(l - 1, c, C, L, S)){
            acpr(M, l - 1, c, C, L, S);
            System.out.printf(" -- %d --> (%d,%d) ", N(l-1, c, C, L, S), l, c);
            return;
        }
    }

    public static int glouton(int[][] S, int x, int y){
        int g = S[x][y];

        while(x < S.length - 1 || y < S[0].length - 1){
            if(x >= S.length - 1){
                g += S[x][++y];
                continue;
            }else if(y >= S[0].length - 1){
                g += S[++x][y];
                continue;
            }

            if(S[x+1][y] <= S[x+1][y+1] && S[x+1][y] <= S[x][y+1]){ // Best : (x+1, y)
                g += S[++x][y];
            }else if(S[x][y+1] <= S[x+1][y+1] && S[x][y+1] <= S[x+1][y]){ // Best : (x, y+1)
                g += S[x][++y];
            }else{  // Best : (x+1, y+1)
                g += S[++x][++y];
            }
        }

        System.out.println("'Glouton' way is equal to " + g);
        return g;
    }
}
