package Minesweeper;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;

/**
 * This class contains the terminal UI for Minesweeper.
 * @author Aditya Dhawan, Annie Thach
 */
public class Main {
    private static Environment env = null;
    private static BasicAgent basicAgent = null;
    private static ModifiedAgent advancedAgent = null;
    private static Scanner sc = new Scanner(System.in);

    /**
     * Method to create environment.
     */
    private static void createEnv() {
        String str = new String();
        try {
            System.out.print("Enter board dimension: ");
            str = sc.nextLine();
            int dim = Integer.parseInt(str);
            System.out.print("Enter number of mines: ");
            str = sc.nextLine();
            int numMines = Integer.parseInt(str);
    
            if(dim > 0 && numMines >= 0) {
                System.out.println("Successfully generated " + dim + " x " + dim + " board and scattered " + numMines + " mines.");
                env = new Environment(dim, numMines);
            } else {
                System.out.println("One of your inputs may be invalid. Try again!");
                env = null;
            }
        } catch(Exception e) {
            System.out.println("One of your inputs may be invalid. Try again!");
            env = null;
        }
    }

    /**
     * Method to create and run basic agent on current board.
     */
    private static void runBasicAgent() {
        if(env != null) {
            // Create the agent.
            basicAgent = new BasicAgent(env.getBoard());
            Random rand = new Random();

            while(true) {
                Cell[][] knowledgeBase = basicAgent.getKnowledgeBase();
                ArrayList<Index> mineCells = basicAgent.getMineCells();

                int row = 0;
                int col = 0;

                // Check safe cells before resorting to random.
                if(!basicAgent.getSafeCells().isEmpty()) {
                    Index index = basicAgent.getSafeCells().pop();
                    row = index.getRow();
                    col = index.getCol();
                } else {
                    // Build a list of unprocessed cells.
                    ArrayList<Index> unprocessed = new ArrayList<Index>();
                    for(int i = 0; i < knowledgeBase.length; i++) {
                        for(int j = 0; j < knowledgeBase.length; j++) {
                            Index index = new Index(i, j);
                            // If not processed and not marked a mine:
                            if(!knowledgeBase[i][j].getRevealed() && !mineCells.contains(index)) {
                                unprocessed.add(index);
                            }
                        }
                    }

                    // If unprocessed is empty, then there are no cells left to be processed.
                    if(unprocessed.isEmpty()) {
                        break;
                    }

                    // Select a cell at random from the list of unprocessed cells.
                    Index index = unprocessed.get(rand.nextInt(unprocessed.size()));
                    row = index.getRow();
                    col = index.getCol();
                }

                basicAgent.selectCell(row, col);    // Select the cell to reveal it.
                basicAgent.queryCell(row, col);     // Query the cell for information.

                System.out.println("Selected (" + row + ", " + col + "); " + basicAgent.getKnowledgeBase()[row][col]);
                if(!basicAgent.getSafeCells().empty()) {
                    System.out.println("Marked safe: " + basicAgent.getSafeCells());
                }
                
                if(!basicAgent.getMineCells().isEmpty()) {
                    System.out.println("Marked mines: " + basicAgent.getMineCells());
                }
            }

            // Check score.
            System.out.println("The basic agent scored: " + basicAgent.calcScore() + " / " + env.getNum_mines());
        } else {
            System.out.println("Please generate a valid board before attempting to create an agent!");
            basicAgent = null;
        }
    }

    /**
     * Method to create and run advanced agent on current board.
     */
    private static void runAdvancedAgent() {
        if(env != null) {
            // Create the agent.
            advancedAgent = new ModifiedAgent(env.getBoard());
            Random rand = new Random();

            while(true) {
                ModifiedCell[][] knowledgeBase = advancedAgent.getKnowledgeBase();
                ArrayList<Index> mineCells = advancedAgent.getMineCells();

                int row = 0;
                int col = 0;
    
                // Pick from list of safe cells if not empty.
                if(!advancedAgent.getSafeCells().isEmpty()) {
                    Index index = advancedAgent.getSafeCells().pop();
                    row = index.getRow();
                    col = index.getCol();
                } else {
                    // Build a min heap of unprocessed cells with probabilities < 0.5.
                    PriorityQueue<Index> unprocessedKnown = new PriorityQueue<Index>();
                    for(int i = 0; i < knowledgeBase.length; i++) {
                        for(int j = 0; j < knowledgeBase.length; j++) {
                            Index index = new Index(i, j, knowledgeBase[i][j].getProbability());
                            if(!knowledgeBase[i][j].getRevealed()
                                && !mineCells.contains(index)
                                && knowledgeBase[i][j].getProbability() > 0
                                && knowledgeBase[i][j].getProbability() < 0.5) {
                                unprocessedKnown.add(index);
                            }
                        }
                    }
    
                    // If heap is empty (no cells with probabilities < 0.5):
                    if(unprocessedKnown.isEmpty()) {
                        // Build a list of unprocessed cells.
                        ArrayList<Index> unprocessed = new ArrayList<Index>();
                        for(int i = 0; i < knowledgeBase.length; i++) {
                            for(int j = 0; j < knowledgeBase.length; j++) {
                                Index index = new Index(i, j);
                                if(!knowledgeBase[i][j].getRevealed() && !mineCells.contains(index)) {
                                    unprocessed.add(index);
                                }
                            }
                        }
                    
                        // If the list is empty, then there is nothing left to process.
                        if(unprocessed.isEmpty()) {
                            break;
                        }
    
                        // Select an unprocessed cell randomly.
                        Index index = unprocessed.get(rand.nextInt(unprocessed.size()));
                        row = index.getRow();
                        col = index.getCol();
                    } else {
                        // Pick the cell with the lowest probability of being a mine from the min heap.
                        row = unprocessedKnown.peek().getRow();
                        col = unprocessedKnown.peek().getCol();
                    }
                }
    
                advancedAgent.selectCell(row, col);             // Select the cell to reveal it.
                advancedAgent.queryCell(row, col);              // Query the cell for information.
                advancedAgent.updateAllKnownProbabilities();    // Update probabilities w/ new clue.

                System.out.println("Selected (" + row + ", " + col + "); " + advancedAgent.getKnowledgeBase()[row][col]);
                if(!advancedAgent.getSafeCells().isEmpty()) {
                    System.out.println("Marked safe: " + advancedAgent.getSafeCells());
                }

                if(!advancedAgent.getMineCells().isEmpty()) {
                    System.out.println("Marked mines: " + advancedAgent.getMineCells());
                }
            }
    
            // Check score.
            System.out.println("The advanced agent scored: " + advancedAgent.calcScore() + " / " + env.getNum_mines());
        } else {
            System.out.println("Please generate a valid board before attempting to create an agent!");
            advancedAgent = null;            
        }
    }

    /**
     * Method to get the average final score for each agent, in percentage.
     */
    private static void calcAgentsAvgFinalScore() {
        String str = new String();
        try {
            if(env != null) {
                Environment temp = env;

                System.out.print("Enter the number of trials: ");
                str = sc.nextLine();
                int trials = Integer.parseInt(str);

                System.out.print("Enter the mine density (as a decimal): ");
                str = sc.nextLine();
                double mineDensity = Double.parseDouble(str);

                int dim = 10;
                int numMines = (int)(mineDensity * (dim * dim));
                double basicAgentSum = 0;
                double advancedAgentSum = 0;

                if(trials > 0) {

                    for(int i = 0; i < trials; i++) {
                        env = new Environment(dim, numMines);
                        runBasicAgent();
                        runAdvancedAgent();
                        basicAgentSum += ((double)basicAgent.calcScore() / (double)env.getNum_mines());
                        advancedAgentSum += ((double)advancedAgent.calcScore() / (double)env.getNum_mines());
                    }

                    double basicAgentAvg = (basicAgentSum / (double)trials) * 100;
                    double advancedAgentAvg = (advancedAgentSum / (double)trials) * 100;

                    System.out.println(numMines);
                    System.out.println("Basic agent average score over " + trials + " trials: " + basicAgentAvg);
                    System.out.println("Advanced agent average score over " + trials + " trials: " + advancedAgentAvg);
                } else {
                    System.out.println("One of your inputs may be invalid. Try again!");
                }
                env = temp;
            } else {
                System.out.println("Please generate a valid board before attempting this.");
            }
        } catch(Exception e) {
            System.out.println("One of your inputs may be invalid. Try again!");
        }
    }

    public static void main(String[] args) {
        // Generate a 16x16 board with 40 mines by default.
        env = new Environment(16, 40);

        String commandsList = "'g' to generate new board.\n'b' to run basic agent.\n'a' to run advanced agent.\n'avg' to get the average scores of both agents for current board.\n'pb' to print the original board.\n'pkb' to run and print the basic agent's resulting knowledge base.\n'pka' to run and print the advanced agent's resulting knowledge base.\n'h' to bring this list up again.\n'q' to quit the program.";

        System.out.println("Welcome to Minesweeper!");
        System.out.println("The commands are as follows:");
        System.out.println(commandsList);

        String cmd = new String();

        while(true) {
            System.out.print("Enter a command: ");
            cmd = sc.nextLine().trim();
    
            if(cmd.equalsIgnoreCase("q")) {
                System.out.println("Quitting...");
                break;
            } else if(cmd.equalsIgnoreCase("g")) {
                System.out.println("Generating new environment...");
                createEnv();
            } else if(cmd.equalsIgnoreCase("b")) {
                System.out.println("Running basic agent...");
                runBasicAgent();
            } else if(cmd.equalsIgnoreCase("a")) {
                System.out.println("Running advanced agent...");
                runAdvancedAgent();
                System.out.println("Number of safe cells falsely marked as mine: " + advancedAgent.calcFalsePositive());
            } if(cmd.equalsIgnoreCase("avg")) {
                System.out.println("Getting average scores for both agents...");
                calcAgentsAvgFinalScore();
            } else if(cmd.equalsIgnoreCase("pb")) {
                System.out.println("Original board:\n" + env);
            } else if(cmd.equalsIgnoreCase("pkb")) {
                runBasicAgent();
                System.out.println("Basic agent's knowledge base:\n" + basicAgent);
                System.out.println("Number of mines marked: " + basicAgent.getMineCells().size());
                // System.out.println("The basic agent scored: " + basicAgent.calcScore() + " / " + env.getNum_mines());
            } else if(cmd.equalsIgnoreCase("pka")) {
                runAdvancedAgent();

                System.out.println("Advanced agent's knowledge base:\n" + advancedAgent);
                // System.out.println("The advanced agent scored: " + advancedAgent.calcScore() + " / " + env.getNum_mines());
                System.out.println("Number of mines marked: " + advancedAgent.getMineCells().size());
                System.out.println("Number of safe cells falsely marked as mine: " + advancedAgent.calcFalsePositive());
            } else if(cmd.equalsIgnoreCase("h")) {
                System.out.println(commandsList);
            } else {
                System.out.println("Invalid command! Type 'h' for a list of valid commands.");
            }
            System.out.println();
        }

        sc.close();
    }
}