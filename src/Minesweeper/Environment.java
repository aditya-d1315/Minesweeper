package Minesweeper;

import java.util.Random;

/**
 * This is the environment for Minesweeper.
 * The board is represented by a 2-dimensional array of integers.
 * Let ...
 * -1 be a mine.
 * 0 be a safe space.
 * n be the number of mines around a space.
 * @author Aditya Dhawan, Annie Thach
 */
public class Environment {
    private int dim;
    private int num_mines;
    private int[][] board;

    /**
     * Constructor method for a new board.
     * @param dim   : The board dimension.
     * @param num_mines : The number of mines to add to the board.
     */
    public Environment(int dim, int num_mines) {
        this.dim = dim;
        this.num_mines = 0;
        this.board = new int[dim][dim];
        scatter_mines(num_mines);   // Scatter mines on board.
    }

    /**
     * A toString method for an Environment object.
     * @return A string representation of the board.
     */
    @Override
    public String toString() {
        String board = new String();
        
        for(int row = 0; row < this.board.length; row++) {
            for(int col = 0; col < this.board[row].length; col++) {
                if(this.board[row][col] < 0) {
                    board += this.board[row][col] + " ";
                    continue;
                }
                board += " " + this.board[row][col] + " ";
            }
            board += "\n";
        }

        return board;
    }

    /**
     * Getter method for the board's dimension.
     * @return The dimension of the board.
     */
    public int getDim() {
        return dim;
    }

    /**
     * Getter method for number of mines.
     * @return The num_mines.
     */
    public int getNum_mines() {
        return num_mines;
    }

    /**
     * Getter method for the environment's board.
     * This is for querying purposes; agent should not get the board.
     * @return The environment's board.
     */
    public int[][] getBoard() {
        return board;
    }

    /**
     * Helper method to determine if an Environment's board is completely mined.
     * Counts the number of mines of the Environment's board and if it is equal to (dim * dim), there is no safe space.
     * @return True if num_mines = (dim * dim). False otherwise.
     */
    private boolean completely_trapped() {
        int num_mines = 0;
        for(int row = 0; row < dim; row++) {
            for(int col = 0; col < dim; col++) {
                num_mines++;
            }
        }

        // No safe space.
        if(num_mines == (dim * dim)) {
            return true;
        }

        return false;
    }

    /**
     * Helper method to scatter mines on the board.
     * Could be used to add additional mines to a board, if necessary.
     * @param num_mines : The number of mines to put out.
     */
    public void scatter_mines(int num_mines) {
        // Generate random position.
        Random rand = new Random();
        int row = rand.nextInt(dim);
        int col = rand.nextInt(dim);
        int placed = 0;

        while(placed < num_mines)
        {
            if(this.num_mines + placed >= (dim * dim)) {
                break;
            }

            // Generate a unique, unmined location.
            while(board[row][col] == -1) {
                row = rand.nextInt(dim);
                col = rand.nextInt(dim);
            }

            board[row][col] = -1;   // Set mine.

            // Increment the unmined spaces around the mine.
            // Expand from center.
            int start_row = row - 1 >= 0 ? row - 1 : row;   // If expanding up is possible ...
            int start_col = col - 1 >= 0 ? col - 1 : col;   // If expanding left is possible ...
            int end_row = row + 1 < dim ? row + 1 : row;    // If expanding down is possible ...
            int end_col = col + 1 < dim ? col + 1 : col;    // If expanding right is possible ...

            int current_row = start_row;
            int current_col = start_col;
            while(current_row < end_row + 1) {
                while(current_col < end_col + 1) {
                    if(board[current_row][current_col] != -1) {
                        board[current_row][current_col]++;  // Increment non-mined space.
                    }
                    current_col++;
                }
                current_row++;
                current_col = start_col;
            }

            placed ++;
        }

        this.num_mines += placed;

        return;
    }
}