package Minesweeper;

import java.util.ArrayList;

/**
 * This is the class for the basic agent.
 * @author Aditya Dhawan, Annie Thach
 */
public class BasicAgent {
    private int[][] board;          // The board the agent will be working with.
    private Cell[][] knowledgeBase; // Parallel matrix that stores information about cells.

    // ArrayLists for guesses.
    private ArrayList<Index> safeCells;
    private ArrayList<Index> mineCells;

    /**
     * Constructor method for basic agent.
     * @param board : The board the agent will be working with/querying from.
     */
    public BasicAgent(int[][] board) {
        this.board = board;
        this.knowledgeBase = new Cell[board.length][board.length];

        // Initialize cells.
        for(int row = 0; row < knowledgeBase.length; row++) {
            for(int col = 0; col < knowledgeBase.length; col++) {
                knowledgeBase[row][col] = new Cell(false, -2, -1, -1, -1, 0);
            }
        }

        safeCells = new ArrayList<Index>();
        mineCells = new ArrayList<Index>();
    }

    /**
     * A toString method for a BasicAgent object.
     * @return A string representation of the agent's knowledge base.
     */
    @Override
    public String toString() {
        String knowledgeBase = new String();
        
        for(int row = 0; row < board.length; row++) {
            for(int col = 0; col < board.length; col++) {
                if(this.knowledgeBase[row][col].getRevealed()) {
                    if(this.knowledgeBase[row][col].getClue() < 0) {
                        knowledgeBase += this.knowledgeBase[row][col].getClue() + " ";
                        continue;                        
                    }
                    knowledgeBase += " " + this.knowledgeBase[row][col].getClue() + " ";
                    continue;
                }
                if(this.knowledgeBase[row][col].getAgentsGuess() == -1) {
                    knowledgeBase += " m ";
                } else if(this.knowledgeBase[row][col].getAgentsGuess() == 1) {
                    knowledgeBase += " s ";
                } else {    // Unknown.
                    knowledgeBase += " ? ";
                }
            }
            knowledgeBase += "\n";
        }

        return knowledgeBase;
    }

    /**
     * The agent wants to reveal cell at (row, col).
     * @param row
     * @param col
     */
    public void selectCell(int row, int col) {
        // Mark revealed.
        knowledgeBase[row][col].setRevealed(true);

        // Set clue.
        knowledgeBase[row][col].setClue(board[row][col]);

        // Remove from lists, if in list.
        if(safeCells.contains(new Index(row, col))) {
            safeCells.remove(new Index(row, col));
        }

        if(mineCells.contains(new Index(row, col))) {
            mineCells.remove(new Index(row, col));
        }
    }

    /**
     * Agent gets/updates information for cell at (row, col) on board and makes guesses.
     */
    public void queryCell(int row, int col) {
        // Update knowledge base.
        knowledgeBase[row][col].setNumSafeCells(countSafeNeighbors(row, col));
        knowledgeBase[row][col].setNumMineCells(countMineNeighbors(row, col));
        knowledgeBase[row][col].setNumHiddenCells(countHiddenNeighbors(row, col));

        // Make guesses.
        int clue = knowledgeBase[row][col].getClue();
        int revealed = knowledgeBase[row][col].getNumSafeCells() + knowledgeBase[row][col].getNumMineCells();
        int hidden = knowledgeBase[row][col].getNumHiddenCells();

        int agentsGuess = 0;
        // If clue - revealed = hidden, every hidden is mine.
        if(clue - revealed == hidden) {
            agentsGuess = -1;   // Mark hidden probably mines.
        } else if(8 - clue == hidden) { // If 8 - clue = hidden, every hidden is safe.
            agentsGuess = 1;    // Mark hidden probably safe.
        }

        // Mark guess.
        int dim = board.length;

        // Expand from center.
        int start_row = row - 1 >= 0 ? row - 1 : row;   // If expanding up is possible ...
        int start_col = col - 1 >= 0 ? col - 1 : col;   // If expanding left is possible ...
        int end_row = row + 1 < dim ? row + 1 : row;    // If expanding down is possible ...
        int end_col = col + 1 < dim ? col + 1 : col;    // If expanding right is possible ...
        int current_row = start_row;
        int current_col = start_col;
        while(current_row < end_row + 1) {
            while(current_col < end_col + 1) {
                // If not center and not revealed:
                if(current_row != row && current_col != col
                    && !knowledgeBase[current_row][current_col].getRevealed()) {
                    if(agentsGuess == -1 && !mineCells.contains(new Index(row, col))) {
                        mineCells.add(new Index(row, col));
                    } else if(agentsGuess == 1 && !safeCells.contains(new Index(row, col))) {
                        safeCells.add(new Index(row, col));
                    }
                }
                current_col++;
            }
            current_row++;
            current_col = start_col;
        }
    }

    /**
     * Helper method to count + update number of safe neighbors around cell.
     * @param row
     * @param col
     * @return Number of safe neighbors around cell.
     */
    private int countSafeNeighbors(int row, int col) {
        int safeNeighbors = 0;
        int dim = board.length;

        // Expand from center.
        int start_row = row - 1 >= 0 ? row - 1 : row;   // If expanding up is possible ...
        int start_col = col - 1 >= 0 ? col - 1 : col;   // If expanding left is possible ...
        int end_row = row + 1 < dim ? row + 1 : row;    // If expanding down is possible ...
        int end_col = col + 1 < dim ? col + 1 : col;    // If expanding right is possible ...
        int current_row = start_row;
        int current_col = start_col;
        while(current_row < end_row + 1) {
            while(current_col < end_col + 1) {
                // If not center, revealed, and not a mine, increment number of safe neighbors.
                if(current_row != row && current_col != col
                    && knowledgeBase[current_row][current_col].getRevealed()
                    && knowledgeBase[current_row][current_col].getClue() >= 0) {
                    safeNeighbors++;
                }
                current_col++;
            }
            current_row++;
            current_col = start_col;
        }

        return safeNeighbors;
    }

    /**
     * Helper method to count + update number of revealed mines around cell.
     * @param row
     * @param col
     * @return Number of revealed mines neighboring cell.
     */
    private int countMineNeighbors(int row, int col) {
        int mineNeighbors = 0;
        int dim = board.length;

        // Expand from center.
        int start_row = row - 1 >= 0 ? row - 1 : row;   // If expanding up is possible ...
        int start_col = col - 1 >= 0 ? col - 1 : col;   // If expanding left is possible ...
        int end_row = row + 1 < dim ? row + 1 : row;    // If expanding down is possible ...
        int end_col = col + 1 < dim ? col + 1 : col;    // If expanding right is possible ...
        int current_row = start_row;
        int current_col = start_col;
        while(current_row < end_row + 1) {
            while(current_col < end_col + 1) {
                // If not center, revealed, and is a mine, increment number of mine neighbors.
                if(current_row != row && current_col != col
                    && knowledgeBase[current_row][current_col].getRevealed()
                    && knowledgeBase[current_row][current_col].getClue() == -1) {
                        mineNeighbors++;
                }
                current_col++;
            }
            current_row++;
            current_col = start_col;
        }

        return mineNeighbors;
    }

    /**
     * Helper method to count + update number of hidden neighbors around cell.
     * @param row
     * @param col
     * @return Number of hidden neighbors around cell.
     */
    private int countHiddenNeighbors(int row, int col) {
        int hiddenNeighbors = 0;
        int dim = board.length;

        // Expand from center.
        int start_row = row - 1 >= 0 ? row - 1 : row;   // If expanding up is possible ...
        int start_col = col - 1 >= 0 ? col - 1 : col;   // If expanding left is possible ...
        int end_row = row + 1 < dim ? row + 1 : row;    // If expanding down is possible ...
        int end_col = col + 1 < dim ? col + 1 : col;    // If expanding right is possible ...
        int current_row = start_row;
        int current_col = start_col;
        while(current_row < end_row + 1) {
            while(current_col < end_col + 1) {
                // If not center, revealed, and is a mine, increment number of mine neighbors.
                if(current_row != row && current_col != col
                    && !knowledgeBase[current_row][current_col].getRevealed()) {
                        hiddenNeighbors++;
                }
                current_col++;
            }
            current_row++;
            current_col = start_col;
        }

        return hiddenNeighbors;
    }

    /**
     * Getter method for agent's knowledgebase.
     * @return Agent's knowledgebase.
     */
    public Cell[][] getKnowledgeBase() {
        return knowledgeBase;
    }

    /**
     * Getter method for list of safe cells.
     * @return List of safe cells.
     */
    public ArrayList<Index> getSafeCells() {
        return safeCells;
    }

    /**
     * Getter method for list of mine cells.
     * @return List of mine cells.
     */
    public ArrayList<Index> getMineCells() {
        return mineCells;
    }
}