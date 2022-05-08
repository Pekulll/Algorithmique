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
            int v = M[0][0];
            System.out.println("Sum of the way: " + v);
            int g = glouton(S, 0, 0);
            //CPR.acpr(M, S, 0, 0);
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
        int[][] M = new int[S.length][S[0].length];
        M[M.length - 1][M[0].length - 1] = S[S.length - 1][S[0].length - 1];

        for(int y = S[0].length - 1; y >= 0; y--){
            for(int x = S.length - 1; x >= 0; x--){
                if(x == S.length - 1) { // Start
                    if(y == S[0].length - 1) continue;

                    M[x][y] = M[x][y+1] + S[x][y+1];
                    continue;
                }

                if(y == S[0].length - 1){
                    M[x][y] = M[x + 1][y] + S[x][y];
                    continue;
                }

                if((M[x + 1][y] <= M[x + 1][y + 1] && M[x + 1][y] <= M[x][y + 1])){ // Best : East
                    M[x][y] = M[x + 1][y] + S[x][y];
                } else if(M[x + 1][y + 1] <= M[x + 1][y] && M[x + 1][y + 1] <= M[x][y + 1]){ // Best : NE
                    M[x][y] = M[x + 1][y + 1] + S[x][y];
                } else if(M[x][y + 1] <= M[x + 1][y] && M[x][y + 1] <= M[x + 1][y + 1]){ // Best : North
                    M[x][y] = M[x][y + 1] + S[x][y];
                }
            }
        }

        return M;
    }

    public static void acpr(int[][] M, int[][] S, int x, int y){
        if(x >= M.length || y >= M[0].length) return;

        System.out.printf("S(%d,%d)=%d  //  M(%d,%d)=%d\n", x, y, S[x][y], x, y, M[x][y]);

        if(x + 1 >= M.length || y + 1 >= M[0].length) return;

        if(M[x + 1][y] <= M[x + 1][y + 1] && M[x + 1][y] <= M[x][y + 1]){
            acpr(M, S, x+1, y);
        } else if(M[x][y + 1] <= M[x + 1][y + 1] && M[x][y + 1] <= M[x + 1][y]){
            acpr(M, S, x, y+1);
        } else if(M[x + 1][y + 1] <= M[x + 1][y] && M[x + 1][y + 1] <= M[x][y + 1]){
            acpr(M, S, x+1, y+1);
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
