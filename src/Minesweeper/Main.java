package Minesweeper;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        // Test environment.
        int dim = 10;
        int numMines = 30;
        Environment test = new Environment(dim, numMines);
        //System.out.println(test.getNum_mines());

        /*
        System.out.println(test + "\n\n");
        Agent agent = new Agent(10);
        agent.select_position(test);
        System.out.println(agent);

        */

        ModifiedAgent ag = new ModifiedAgent(test.getBoard());

        Random rand = new Random();
        dim = test.getBoard().length;

        while(true) {
            int row = rand.nextInt(dim);
            int col = rand.nextInt(dim);

            // Pick from list of safe cells if not empty.
            if(!ag.getSafeCells().isEmpty()) {
                Index index = ag.getSafeCells().pop();
                row = index.getRow();
                col = index.getCol();
            } else {
                // Build a min heap of unprocessed cells with probabilities < 0.5.
                PriorityQueue<Index> unprocessedKnown = new PriorityQueue<Index>();
                for(int i = 0; i < dim; i++) {
                    for(int j = 0; j < dim; j++) {
                        Index index = new Index(i, j, ag.getKnowledgeBase()[i][j].getProbability());
                        if(!ag.getKnowledgeBase()[i][j].getRevealed() && !ag.getMineCells().contains(index) && ag.getKnowledgeBase()[i][j].getProbability() > 0 && ag.getKnowledgeBase()[i][j].getProbability() < 0.5) {
                            unprocessedKnown.add(index);
                        }
                    }
                }

                // If heap is empty (no cells with probabilities < 0.5):
                if(unprocessedKnown.isEmpty()) {
                    // Build a list of unprocessed cells.
                    ArrayList<Index> unprocessed = new ArrayList<Index>();
                    for(int i = 0; i < dim; i++) {
                        for(int j = 0; j < dim; j++) {
                            Index index = new Index(i, j);
                            if(!ag.getKnowledgeBase()[i][j].getRevealed() && !ag.getMineCells().contains(index)) {
                                unprocessed.add(index);
                            }
                        }
                    }
                

                    // If the list is empty, then there is nothing left to process.
                    if(unprocessed.isEmpty()) {
                        break;
                    }

                    // Select an unprocessed cell randomly.
                    int itemIndex = rand.nextInt(unprocessed.size());
                    row = unprocessed.get(itemIndex).getRow();
                    col = unprocessed.get(itemIndex).getCol();

                    System.out.println("Unprocessed: " + unprocessed);
                } else {
                    row = unprocessedKnown.peek().getRow();
                    col = unprocessedKnown.peek().getCol();
                }
                System.out.println("Unprocessed known: " + unprocessedKnown); // DEBUG
            }

            ag.selectCell(row, col);
            ag.queryCell(row, col);

            // DEBUG
            System.out.println("Original board (unknown to agent):\n" + test);

            ag.updateAllKnownProbabilities();
            System.out.println("Selected (" + row + ", " + col + "); " + ag.getKnowledgeBase()[row][col] + "\n" + ag);
            System.out.println("Probabilities");
            for(int i = 0; i < dim; i++) {
                for(int j = 0; j < dim; j++) {
                    System.out.print(String.format("%.2f\t", ag.getKnowledgeBase()[i][j].getProbability()));
                }
                System.out.println();
            }

            System.out.println("Safe cells: " + ag.getSafeCells() + " (" + ag.getSafeCells().size() + ")");
            System.out.println("Mine cells: " + ag.getMineCells() + " (" + ag.getMineCells().size() + ")");
            System.out.println();
        }

        System.out.println("Final score: " + ag.calcScore() + " / " + numMines); // DEBUG
    }
}