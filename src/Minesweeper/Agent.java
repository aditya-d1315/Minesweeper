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
    private ArrayList<Index> visited; //keeps track of indices that are either already visited
    private ArrayList<Index> canVisit; //keeps track of indices that are safe.
    private ArrayList<Index> doNotVisit; //keeps track of the indices that are NOT safe

    /**
     * Constructor method for the Agent.
     * @param dim   : The dimension for the agent's version of the board (Should be the same as the environment).
     */
    public Agent(int dim) {
        //initialize the board
        this.board = new char[dim][dim];
        for(int row = 0; row < board.length; row ++) {
            for(int col = 0; col < board[row].length; col ++) {
                this.board[row][col] = '?';
            }
        }
        //initialize the lists
        this.visited = new ArrayList<Index>();
        this.canVisit = new ArrayList<Index>();
        this.doNotVisit = new ArrayList<Index>();
    }

    /**
     * A toString method for an Agent object.
     * @return A string representation of the agent's take on the board.
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
     * Method to determine what position the Agent selects next
     * Keep generating a random Index until it is something that is safe to visit.
     * @param e : The environment.
     * @return true if the agent has selected a valid position, false if agent has selected a mine.
     */
    public boolean select_position(Environment e) {
        Index p;
        int row;
        int col;
        if(this.canVisit.size() == 0) {
            Random rand = new Random();
            do {
                row = rand.nextInt(e.getDim());
                col = rand.nextInt(e.getDim());
                p = new Index(row, col);
            } while(this.visited.contains(p));
        }
        else {
            p = this.canVisit.get(0);
            this.canVisit.remove(0);
        }

        int[][] env_board = e.getBoard(); //answer key to check against
        //here we check all the possibilties:
        // - Is this spot mined?
        // - Is this spot clear? Check the spots around it to see how many neighbors are mines.

        switch(env_board[p.getRow()][p.getCol()]) {
            case -1:
                this.board[p.getRow()][p.getCol()] = 'm';
                return false;
            case 0:
                this.board[p.getRow()][p.getCol()] = 'c';
                /*In this case, all surrounding positions are safe (can be visited).*/
                //check if left is in bounds
                if(p.getCol() - 1 >= 0) {
                    this.canVisit.add(new Index(p.getRow(), p.getCol() - 1));
                }
                //check if right is in bounds
                if(p.getCol() + 1 < env_board[p.getRow()].length) {
                    this.canVisit.add(new Index(p.getRow(), p.getCol() + 1));
                }
                //check if up is in bounds
                if(p.getRow() - 1 >= 0) {
                    this.canVisit.add(new Index(p.getRow() - 1, p.getCol()));
                }
                //check if down is in bounds
                if(p.getRow() + 1 < env_board.length) {
                    this.canVisit.add(new Index(p.getRow() + 1, p.getCol()));
                }
                //check if up-left is in bounds
                if(p.getRow() - 1 >= 0 && p.getCol() - 1 >= 0) {
                    this.canVisit.add(new Index(p.getRow() - 1, p.getCol() - 1));
                }
                //check if up-right is in bounds
                if(p.getRow() - 1 >= 0 && p.getCol() + 1 < env_board[p.getRow()].length) {
                    this.canVisit.add(new Index(p.getRow() - 1, p.getCol() + 1));
                }
                //check if down-left is in bounds
                if(p.getRow() + 1 < env_board.length && p.getCol() - 1 >= 0) {
                    this.canVisit.add(new Index(p.getRow() + 1, p.getCol() - 1));
                }
                //check if down-right is in bounds
                if(p.getRow() + 1 < env_board.length && p.getCol() + 1 < env_board[p.getRow()].length) {
                    this.canVisit.add(new Index(p.getRow() + 1, p.getCol() + 1));
                }
                break;
            case 1:
                this.board[p.getRow()][p.getCol()] = '1';
                break;
            case 2:
                this.board[p.getRow()][p.getCol()] = '2';
                break;
            case 3:
                this.board[p.getRow()][p.getCol()] = '3';
                /*If this position is a corner position, all surrounding spaces are mines*/
                //check top-left corner
                if(p.getRow() == 0 && p.getCol() == 0) {
                    this.doNotVisit.add(new Index(p.getRow(), p.getCol() + 1));
                    this.doNotVisit.add(new Index(p.getRow() + 1, p.getCol()));
                    this.doNotVisit.add(new Index(p.getRow() + 1, p.getCol() + 1));
                    this.board[p.getRow()][p.getCol() + 1] = 'm';
                    this.board[p.getRow() + 1][p.getCol()] = 'm';
                    this.board[p.getRow() + 1][p.getCol() + 1] = 'm';
                }
                //check top-right corner
                else if(p.getRow() == 0 && p.getCol() == env_board[p.getRow()].length - 1) {
                    this.doNotVisit.add(new Index(p.getRow(), p.getCol() - 1));
                    this.doNotVisit.add(new Index(p.getRow() + 1, p.getCol()));
                    this.doNotVisit.add(new Index(p.getRow() + 1, p.getCol() - 1));
                    this.board[p.getRow()][p.getCol() - 1] = 'm';
                    this.board[p.getRow() + 1][p.getCol()] = 'm';
                    this.board[p.getRow() + 1][p.getCol() - 1] = 'm';
                }
                //check bottom-left corner
                else if(p.getRow() == env_board.length - 1 && p.getCol() == 0) {
                    this.doNotVisit.add(new Index(p.getRow() - 1, p.getCol()));
                    this.doNotVisit.add(new Index(p.getRow(), p.getCol() + 1));
                    this.doNotVisit.add(new Index(p.getRow() - 1, p.getCol() + 1));
                    this.board[p.getRow() - 1][p.getCol()] = 'm';
                    this.board[p.getRow()][p.getCol() + 1] = 'm';
                    this.board[p.getRow() - 1][p.getCol() + 1] = 'm';
                }
                //check bottom-right corner
                else if(p.getRow() == env_board.length - 1 && p.getCol() == env_board[p.getRow()].length - 1) {
                    this.doNotVisit.add(new Index(p.getRow(), p.getCol() - 1));
                    this.doNotVisit.add(new Index(p.getRow() - 1, p.getCol()));
                    this.doNotVisit.add(new Index(p.getRow() - 1, p.getCol() - 1));
                    this.board[p.getRow()][p.getCol() - 1] = 'm';
                    this.board[p.getRow() - 1][p.getCol()] = 'm';
                    this.board[p.getRow() - 1][p.getCol() - 1] = 'm';
                }
                break;
            case 4:
                this.board[p.getRow()][p.getCol()] = '4';
                break;
            case 5:
                this.board[p.getRow()][p.getCol()] = '5';
                break;
            case 6:
                this.board[p.getRow()][p.getCol()] = '6';
                break;
            case 7:
                this.board[p.getRow()][p.getCol()] = '7';
                break;
            case 8:
                this.board[p.getRow()][p.getCol()] = '8';
                /*In this case, all surrounding spaces are mines.*/
                /*no need to check bounds. if it's 8, then we know for sure that there are 8 positions with mines around this current position*/
                this.doNotVisit.add(new Index(p.getRow() - 1, p.getCol() - 1)); //up-left
                this.doNotVisit.add(new Index(p.getRow() - 1, p.getCol())); //up
                this.doNotVisit.add(new Index(p.getRow() - 1, p.getCol() + 1)); //up-right
                this.doNotVisit.add(new Index(p.getRow(), p.getCol() + 1)); //right
                this.doNotVisit.add(new Index(p.getRow() + 1, p.getCol() + 1)); //down-right
                this.doNotVisit.add(new Index(p.getRow() + 1, p.getCol())); //down
                this.doNotVisit.add(new Index(p.getRow() + 1, p.getCol() - 1)); //down-left
                this.doNotVisit.add(new Index(p.getRow(), p.getCol() - 1)); //left
                this.board[p.getRow() - 1][p.getCol() - 1] = 'm'; //up-left
                this.board[p.getRow() - 1][p.getCol()] = 'm'; //up
                this.board[p.getRow() - 1][p.getCol() + 1] = 'm'; //up-right
                this.board[p.getRow()][p.getCol() + 1] = 'm'; //right
                this.board[p.getRow() + 1][p.getCol() + 1] = 'm'; //down-right
                this.board[p.getRow() + 1][p.getCol()] = 'm'; //down
                this.board[p.getRow() + 1][p.getCol() - 1] = 'm'; //down-left
                this.board[p.getRow()][p.getCol() - 1] = 'm'; //left
                break;
            default:
                System.out.println("Whoops! Something went wrong.");
                break;
        }

        return true;
    }

    //tester
    public static void main(String[] args) {
        Agent a = new Agent(10);
    }
}