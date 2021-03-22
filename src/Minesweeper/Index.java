package Minesweeper;

import java.util.Comparator;

/**
 * Abstract data type for holding the coordinates for a position in the board.
 * @author Aditya Dhawan, Annie Thach
 */
public class Index implements Comparable<Index>, Comparator<Index> {
    private int row;
    private int col;
    private double prob;

    /**
     * Constructor method for a new Index.
     * @param row   : The row of the position.
     * @param col   : The col of the position.
     */
    public Index(int row, int col) {
        this.row = row;
        this.col = col;
        this.prob = -1;
    }

    /**
     * Constructor method for a new Index.
     * @param row   : The row of the position.
     * @param col   : The col of the position.
     */
    public Index(int row, int col, double prob) {
        this.row = row;
        this.col = col;
        this.prob = prob;
    }

    /**
     * toString method for printing Index object in (col, row) to match (x, y).
     * @return String representation of Index object.
     */
    @Override
    public String toString() {
        if(prob >= 0) {
            return "(" + row + ", " + col + ", " + prob + ")";
        }
        return "(" + row + ", " + col + ")";
    }

    /**
     * Equals method to compare two indices.
     * @param obj   : The other index object to compare to.
     * @return true if the two indices are the same, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Index) {
            Index other = (Index)obj;
            return ((this.row == other.row) && (this.col == other.col));
        }
        return false;
    }

    /**
     * Compare method for 2 Modified Cell objects.
     * @param obj - Index object.
     * @return -1 if score is less than input,
                0 if score is equal to input,
                1 if score is greater than input.
     */
    public int compareTo(Index obj) {
        if(this.prob == obj.prob) {
            return 0;
        } else if (this.prob > obj.prob) {
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * Compare method for priority queue's comparator.
     * @param a - Item 1.
     * @param b - Item 2.
     * @return (See compareTo method.)
     */
    @Override
    public int compare(Index a, Index b) {
        return a.compareTo(b);
    }

    /**
     * Getter method for the row.
     * @return the row.
     */
    public int getRow() {
        return row;
    }

    /**
     * Getter method for the col.
     * @return the col.
     */
    public int getCol() {
        return col;
    }
}
