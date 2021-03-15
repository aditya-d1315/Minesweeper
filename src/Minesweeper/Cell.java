package Minesweeper;

/**
 * This class holds information for a cell for the basic agent to work off of.
 * @author Aditya Dhawan, Annie Thach
 */
public class Cell {
    private boolean revealed;
    private int clue;
    private int numSafeCells;
    private int numMineCells;
    private int numHiddenCells;
    private int agentsGuess;    // -1 = mine, 0 = unknown, 1 = safe

    /**
     * Constructor method for a cell.
     * @param revealed : True if revealed, false if not.
     * @param clue : If revealed, tells number of mines touching cell.
     * @param numSafeCells : Number of neighbors revealed to be safe.
     * @param numMineCells : The number of revealed mines.
     * @param numHiddenCells : Number of unrevealed cells.
     * @param agent_guess : Agent's guess on what the cells may be.
     */
    public Cell(boolean revealed, int clue, int numSafeCells, int numMineCells, int numHiddenCells, int agentsGuess) {
        this.revealed = revealed;
        this.clue = clue;
        this.numSafeCells = numSafeCells;
        this.numMineCells = numMineCells;
        this.numHiddenCells = numHiddenCells;
        this.agentsGuess = agentsGuess;
    }

    /**
     * A toString method for a Cell object.
     * @return A string representation of a Cell object.
     */
    @Override
    public String toString() {
        String cellString = new String();
        
        if(revealed) {
            cellString += "revealed ";
            
            if(clue == -1) {
                cellString += "mine, ";
            } else {
                cellString += "safe (" + clue + "), ";
            }
        } else {
            cellString += "hidden ";
        }

        cellString += numSafeCells + " safe neighbors, " + numMineCells + " mines, " + numHiddenCells + " hidden neighbors, ";

        if(agentsGuess == -1) {
            cellString += "probably mine";
        } else if(agentsGuess == 0) {
            cellString += "unknown";
        } else if(agentsGuess == 1) {
            cellString += "probably safe";
        }

        return cellString;
    }

    /**
     * Getter method for cell's state.
     * @return The state of the cell; true if revealed, false otherwise.
     */
    public boolean getRevealed() {
        return revealed;
    }

    /**
     * Getter method for cell's clue.
     * -2 If unkown/not set. -1 if mine. Values >= 0 indicate number of mines adjacent to cell.
     * @return Cell's clue.
     */
    public int getClue() {
        return clue;
    }

    /**
     * Getter method for number of safe cells around cell.
     * @return The number of safe cells around cell.
     */
    public int getNumSafeCells() {
        return numSafeCells;
    }

    /**
     * Getter method for number of revealed mines around cell.
     * @return The number of revealed mines around cell.
     */
    public int getNumMineCells() {
        return numMineCells;
    }

    /**
     * Getter method for number of hidden cells around cell.
     * @return The number of hidden cells around cell.
     */
    public int getNumHiddenCells() {
        return numHiddenCells;
    }

    /**
     * Getter method for agent's guess.
     * @return -1 if probably mine, 0 if unknown, 1 if probably safe.
     */
    public int getAgentsGuess() {
        return agentsGuess;
    }

    /**
     * Setter method for changing cell state.
     * @param revealed : The state of the cell; -1 = mine, 0 = hidden, 1 = revealed safe.
     */
    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }

    /**
     * Setter method for cell's clue.
     * @param clue : Cell's clue.
     */
    public void setClue(int clue) {
        this.clue = clue;
    }

    /**
     * Setter method for changing number of safe cells around cell.
     * @param numSafeCells: The number of safe cells around cell.
     */
    public void setNumSafeCells(int numSafeCells) {
        this.numSafeCells = numSafeCells;
    }

    /**
     * Setter method for changing number of revealed mine cells around cell.
     * @param numMineCells : The number of revealed mine cells around cell.
     */
    public void setNumMineCells(int numMineCells) {
        this.numMineCells = numMineCells;
    }

    /**
     * Setter method for changing number of hidden cells around cell.
     * @param numHiddenCells : The number of hidden cells around cell.
     */
    public void setNumHiddenCells(int numHiddenCells) {
        this.numHiddenCells = numHiddenCells;
    }

    /**
     * Setter method for agent's guess.
     * @param agentsGuess : Agent's guess; -1 if probably mine, 0 if unknown, 1 if probably safe.
     */
    public void setAgentsGuess(int agentsGuess) {
        this.agentsGuess = agentsGuess;
    }
}