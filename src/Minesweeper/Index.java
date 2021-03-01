package Minesweeper;

/**
 * Abstract data type for holding the coordinates for a position in the board.
 */
public class Index {
    private int row;
    private int col;

    /**
     * Constructor method for a new Index.
     * @param row   : The row of the position.
     * @param col   : The col of the position.
     */
    public Index(int row, int col) {
        this.row = row;
        this.col = col;
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
