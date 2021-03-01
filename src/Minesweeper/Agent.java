package Minesweeper;

import java.util.ArrayList;
import java.util.Random;

/**
 * This is the AI that attempts to clear the environment.
 * @author Aditya Dhawan, Annie Thach
 */
public class Agent {
    /**
     * Let...
     * '?' be a position unknown to the agent,
     * '(int [0-8])' be the number of mines surrounding a spot.
     * 'c' be a position that the agent can visit safely.
     * 'm' be a position that is mined. If the agent visits this, they lose.
     */
    private char[][] board;
    private ArrayList<Index> visited; //keeps track of indices that are either already visited or not safe.
    private ArrayList<Index> canVisit; //keeps track of indices that are safe.

    /**
     * Constructor method for the Agent.
     * @param dim   : The dimension for the agent's version of the board (Should be the same as the environment).
     */
    public Agent(int dim) {
        board = new char[dim][dim];
        visited = new ArrayList<Index>();
        canVisit = new ArrayList<Index>();
    }

    /**
     * Method to determine what position the Agent selects next.
     * Keep generating a random Index until it is something that is safe to visit.
     */
    public void select_position(Environment e) {
        Random rand = new Random();
        Index p;
        int row;
        int col;
        do {
            row = rand.nextInt(e.getDim());
            col = rand.nextInt(e.getDim());
            p = new Index(row, col);
        } while(visited.contains(p));

        int[][] env_board = e.getBoard(); //answer key to check against
        //here we check all the possibilties:
        // - Is this spot mined?
        // - Is this spot clear? Check the spots around it to see how many neighbors are mines.

        //check if this position is mined
        if(env_board[p.getRow()][p.getCol()] == -1) {

        }
    }

    //tester
    public static void main(String[] args) {
        Agent a = new Agent(10);
    }
}