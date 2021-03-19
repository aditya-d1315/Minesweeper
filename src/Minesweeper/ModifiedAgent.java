package Minesweeper;

import java.util.ArrayList;
import java.util.Stack;

public class ModifiedAgent {
    private int[][] board;          // The board the agent will be working with.
    private ModifiedCell[][] knowledgeBase; // Parallel matrix that stores information about cells.

    // ArrayLists for guesses.
    private Stack<Index> safeCells;
    private ArrayList<Index> mineCells;

    /**
     * Constructor method for modified agent.
     * @param board : The board the agent will be working with/querying from.
     */
    public ModifiedAgent(int[][] board) {
        this.board = board;
        this.knowledgeBase = new ModifiedCell[board.length][board.length];

        // Initialize cells.
        for(int row = 0; row < knowledgeBase.length; row++) {
            for(int col = 0; col < knowledgeBase.length; col++) {
                knowledgeBase[row][col] = new ModifiedCell(false, -2, -1, -1, -1, 0);
                knowledgeBase[row][col].setProbability(0);
                knowledgeBase[row][col].setClueSum(0);
            }
        }

        safeCells = new Stack<Index>();
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
                } else {
                    Index index = new Index(row, col);
                    if(mineCells.contains(index)) {
                        knowledgeBase += " m ";
                    } else if(safeCells.contains(index)) {
                        knowledgeBase += " s ";
                    } else {    // Unknown.
                        knowledgeBase += " ? ";
                    }
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
        // Mark revealed and set clue.
        knowledgeBase[row][col].setRevealed(true);
        knowledgeBase[row][col].setClue(board[row][col]);

        if(knowledgeBase[row][col].getClue() == -1) {
            knowledgeBase[row][col].setProbability(1.0);
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
        int revealedSafe = knowledgeBase[row][col].getNumSafeCells();
        int revealedMine = knowledgeBase[row][col].getNumMineCells();
        int revealed = revealedSafe + revealedMine;
        int hidden = knowledgeBase[row][col].getNumHiddenCells();
        int numNeighbors = revealed + hidden;   // Total number of neighbors.

        int agentsGuess = 0;
        // If clue - revealed = hidden, every hidden is mine.
        if(clue - revealedMine == hidden) {
            agentsGuess = -1;   // Mark hidden probably mines.
        } else if((numNeighbors - clue) - revealedSafe == hidden) { // If 8 - clue = hidden, every hidden is safe.
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
                if(!(current_row == row && current_col == col) && !knowledgeBase[current_row][current_col].getRevealed()) {
                    Index index = new Index(current_row, current_col);
                    if(agentsGuess == -1 && !mineCells.contains(index)) {
                        mineCells.add(index);
                        // System.out.println(index + " suspected unsafe."); // DEBUG
                    } else if(agentsGuess == 1 && !safeCells.contains(index)) {
                        safeCells.add(index);
                        // System.out.println(index + " should be safe."); // DEBUG
                    }

                    knowledgeBase[current_row][current_col].setAgentsGuess(agentsGuess);

                    // Update probability of current hidden (neighboring) cell.
                    if(knowledgeBase[row][col].getClue() >= 0) {
                        // updateCellProbability(current_row, current_col);
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
                if(!(current_row == row && current_col == col)
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
                if(!(current_row == row && current_col == col)
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
                // If not center, and not revealed, count.
                if(!(current_row == row && current_col == col) && !knowledgeBase[current_row][current_col].getRevealed()) {
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
     * Helper method to count up number of marked safe neighboring a cell.
     */
    private int countMarkedSafe(int row, int col) {
        int markedSafe = 0;
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
                // If not center, not revealed, and is a marked mine, count.
                if(!(current_row == row && current_col == col) 
                    && !knowledgeBase[current_row][current_col].getRevealed()
                    && safeCells.contains(new Index(current_row, current_col))) {
                    markedSafe++;
                }
                current_col++;
            }
            current_row++;
            current_col = start_col;
        }

        return markedSafe;
    }

    /**
     * Helper method to count up number of marked mines neighboring a cell.
     */
    private int countMarkedMines(int row, int col) {
        int markedMines = 0;
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
                // If not center, not revealed, and is a marked mine, count.
                if(!(current_row == row && current_col == col) 
                    && !knowledgeBase[current_row][current_col].getRevealed()
                    && mineCells.contains(new Index(current_row, current_col))) {
                    markedMines++;
                }
                current_col++;
            }
            current_row++;
            current_col = start_col;
        }

        return markedMines;
    }

    /**
     * Helper method to calculate the probability for a cell's neighbors.
     * @param row
     * @param col
     * @return
     */
    private double calcProbability(int row, int col) {
        // Count up number of marked mines around neighbor.
        int markedMines = countMarkedMines(row, col);
        int minesLeft = knowledgeBase[row][col].getClue() - (knowledgeBase[row][col].getNumMineCells() + markedMines);

        // If there are mines left...
        int hiddenCells = knowledgeBase[row][col].getNumHiddenCells();
        int markedSafe = countMarkedSafe(row, col);
        if(minesLeft > 0 && (hiddenCells -  (markedSafe + markedMines)) > 0) {
            System.out.println(new Index(row, col) + " unknowns left = " + (hiddenCells - (markedSafe + markedMines)));
            return (double)minesLeft / (double)(hiddenCells - (markedSafe + markedMines));
        }

        return 0;   // No mines left.
    }

    /**
     * Helper method to update the probabilities for all of a cell's unrevealed neighbors.
     * @param row
     * @param col
     */
    public void updateCellProbability(int row, int col) {
        Index index = new Index(row, col);

        // No point to updating off of tripped mine.
        if(knowledgeBase[row][col].getProbability() >= 1) {
            return;
        }

        double prob = 0;
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
                // If not center, revealed, and not a mine:
                if(!(current_row == row && current_col == col)
                    && knowledgeBase[current_row][current_col].getRevealed()
                    && knowledgeBase[current_row][current_col].getClue() >= 0) {
                    double neighboringProb = knowledgeBase[current_row][current_col].getProbability();
                    if(neighboringProb >= 0) {
                        prob += neighboringProb;
                    }
                }
                current_col++;
            }
            current_row++;
            current_col = start_col;
        }

        // If cell is hidden:
        if(!knowledgeBase[row][col].getRevealed()) {
            if(prob == 0 && knowledgeBase[row][col].getProbability() != prob && !safeCells.contains(index)) {
                safeCells.add(index);
                System.out.println(index + " safe.");
            } else {
                knowledgeBase[row][col].setProbability(prob);
                // System.out.println(new Index(row, col) + " probability = " + prob);
                if(prob >= 1 && !mineCells.contains(index)) {
                    mineCells.add(index);
                    System.out.println(index + " unsafe.");
                }
            }
        }
    }

    /**
     * Helper method to update probabilties based off known cells.
     */
    public void updateAllKnownProbabilities() {
        int dim = board.length;

        // Update probabilities for neighbors of revealed cells.
        for(int row = 0; row < dim; row++) {
            for(int col = 0; col < dim; col++) {
                if(knowledgeBase[row][col].getRevealed() && knowledgeBase[row][col].getClue() >= 0) {
                    // System.out.println("Updating probability for " + new Index(row, col)); // DEBUG
                    queryCell(row, col);
                    knowledgeBase[row][col].setProbability(calcProbability(row, col));
                    // System.out.println(new Index(row, col) + " probability = " + knowledgeBase[row][col].getProbability()); // DEBUG
                }
            }
        }

        //  Assign probabilities to unknown, unmarked cells.
        for(int row = 0; row < dim; row++) {
            for(int col = 0; col < dim; col++) {
                Index index = new Index(row, col);
                if(!knowledgeBase[row][col].getRevealed() && !safeCells.contains(index) && !mineCells.contains(index)) {
                    updateCellProbability(row, col);
                }
            }
        }
    }

    /**
     * Helper method to get the score.
     * @return mines correctly identified / total mines
     */
    public int calcScore() {
        int score = 0;
        for(int i = 0; i < mineCells.size(); i++) {
            Index markedMine = mineCells.get(i);

            if(board[markedMine.getRow()][markedMine.getCol()] == -1) {
                score++;
            }
        }
        return score;
    }

    /**
     * Getter method for agent's knowledgebase.
     * @return Agent's knowledgebase.
     */
    public ModifiedCell[][] getKnowledgeBase() {
        return knowledgeBase;
    }

    /**
     * Getter method for list of safe cells.
     * @return List of safe cells.
     */
    public Stack<Index> getSafeCells() {
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