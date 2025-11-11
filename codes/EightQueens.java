class EightQueens {
    public static void main(String args[]) {
        int N = 8;

          int[] queenPos = {1, 5, 8, 6, 3, 7, 2, 4}; 
        for (int i = 0; i < N; i++) {          
            for (int j = 1; j <= N; j++) {    
                if (queenPos[i] == j) {        
                    System.out.print("Q" + (i + 1) + " ");
                } else {
                    System.out.print(".  ");
                }
            }
            System.out.println();
        }
        System.out.println("\nQueen positions:");
        for (int i = 0; i < N; i++) 
	{
            System.out.println("Row " + (i + 1) + " -> Column " + queenPos[i] + " -> Q" + (i + 1));
        }
    }
}
