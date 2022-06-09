package fr.main.algo.csm;

import fr.main.algo.Utils;

import java.util.Random;

// CHEMIN SOMME MAX DANS UN TRIANGLE
public class CSM {
    public static void main(String[] args){
        float[] D = CSM.run(1000, 5000, 100);
        Utils.save("CSM", D);
    }

    public static float[] run(int Lmax, int Nruns, int Vmax){
        Random rand = new Random();
        float[] D = new float[Nruns]; // Créer le tableau des distances relatives

        for(int r = 0; r < Nruns; r++){
            System.out.println("= Run " + r + "/" + (Nruns-1) + " ==========");

            // Créer le triangle
            int[] T = CSM.createTriangle(rand, rand.nextInt(Lmax) + 1, Vmax);

            // Calcul M
            int[] M = CSM.calculerM(T);

            // Prend le chemin de somme maximal commençant en 0
            int v = M[0];

            // Affiche ce chemin et sa somme
            CSM.acsm(M, T, 0, T.length);
            System.out.println("Sum of the way: " + v);

            // Calcul la valeur du chemin glouton
            int g = glouton(T, 0);

            // Ajoute la distance relative de cette run à D
            if(v != 0) D[r] = (v-g) / (float)v;
            else D[r] = 0;
        }

        return D;
    }

    public static int[] createTriangle(Random rand, int m, int maxValue){
        int length = m * (m+1) / 2; // Calcul le nombre d'étages du triangle avec m niveau
        int[] T = new int[length];

        for(int i = 0; i < length; i++){ // Parcours le triangle par indice croissant
            T[i] = rand.nextInt(maxValue + 1); // Assigne une valeur aléatoire à T[i]
        }

        return T; // Retourne le triangle généré aléatoirement
    }

    public static int g(int i){
        if(i == 0) return 1; // Si i = 0, le niveau associé est 0, donc le descendant gauche 1

        // Niveau de l'indice i
        // Utilise une équation du second degré pour trouvé le niveau de l'indice i sans boucle
        // int level = (int)((Math.sqrt(1 + 8 * i) - 1) / 2);

        // Sans utiliser la méthode de calcul avec une équation du second degré
        // On obtient la valeur de l grâce à une boucle while
        int level = 1;
        while((level * (level + 1) / 2) < i - level) level++;

        // Position de l'indice i dans le niveau 'level'
        int position = i - (level * (level + 1) / 2);

        // Position du descendant gauche de i
        // level * (level + 1) / 2 = le nombre d'élément au niveau 'level'
        // position = la position dans la ligne de l'indice i, identique à celle de g(i)
        // level * (level + 1) / 2f + position = l'indice du descendant gauche
        int left = (int)((level + 1) * (level + 2) / 2f + position);
        return left;
    }

    public static int d(int i){
        return g(i) + 1;
    }

    public static int[] calculerM(int[] T){
        int[] M = new int[T.length]; // Initialise M[0:n]

        for(int i = T.length - 1; i >= 0; i--){
            int left = g(i);

            if(left >= T.length){ // Base : le niveau de i est le dernier niveau
                M[i] = T[i]; // m(i) = T[i]
            }else{
                // Cas général : m(i) = max { m(g(i)), m(g(i) + 1) } + T[i]
                M[i] = max(M[left], M[left + 1]) + T[i];
            }
        }

        return M;
    } // Theta ( m * (m + 1) / 2) <=> Theta (n)

    private static int max(int x, int y){
        if(x >= y) return x;
        return y;
    }

    public static void acsm(int[] M, int[] T, int i, int n){
        System.out.printf("T[%d] (M[%d]=%d) ----- %d ----> ", i, i, M[i], T[i]);
        int left = g(i);

        if(left >= n) return; // On est arrivé en bas du triangle : le chemin est fini

        // On regarde quel est le chemin de somme maximum et on l'affiche
        if(M[left] > M[left + 1]) acsm(M, T, left, n);
        else acsm(M, T, left + 1, n);
    }

    public static int glouton(int[] T, int i){ // Appel principal : glouton(T, 0)
        int g = T[i]; // Commence par prendre la première valeur T[i] (le début du chemin)
        int left = g(i); // Prend l'indice du descendant gauche de i

        while(left < T.length){ // Continue tant qu'on est pas en bas du triangle
            if(T[left] > T[left + 1]){ // Regarde la case de valeur maximale entre le descendant gauche et le droit
                i = left; // Le descendant gauche est plus grand
            }else{ // Le descendant droit est plus grand
                i = left + 1;
            }

            g += T[i]; // Ajoute la valeur du descandant choisi
            left = g(i); // Calcul l'indice du descendant gauche de la nouvelle position
        }

        System.out.println("'Glouton' way is equal to " + g); // Affiche la somme du chemin glouton
        return g; // Retourne la valeur du chemin glouton
    } // Theta (m)
}
