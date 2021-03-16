package Minesweeper;

public class ModifiedCell extends Cell {

    private double probability;
    private int clueSum;

    public ModifiedCell(boolean revealed, int clue, int numSafeCells, int numMineCells, int numHiddenCells, int agentsGuess) {
        super(revealed, clue, numSafeCells, numMineCells, numHiddenCells, agentsGuess);
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public void setClueSum(int clueSum) { this.clueSum = clueSum; }

    public double getProbability() {
        return probability;
    }

    public int getClueSum() { return clueSum; }
}
