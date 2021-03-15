package Minesweeper;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        // Test environment.
        Environment test = new Environment(10, 30);
        //System.out.println(test.getNum_mines());

        /*
        System.out.println(test + "\n\n");
        Agent agent = new Agent(10);
        agent.select_position(test);
        System.out.println(agent);

        */
        // TODO: Test select + query on entire board @ random.
        BasicAgent ag = new BasicAgent(test.getBoard());

        Random rand = new Random();
        int dim = test.getBoard().length;
        int numRevealedCells = 0;

        while(numRevealedCells + ag.getMineCells().size() < dim * dim) {
            int row = rand.nextInt(dim);
            int col = rand.nextInt(dim);

            // Pick from list of safe cells if not empty.
            if(!ag.getSafeCells().isEmpty()) {
                Index index = ag.getSafeCells().pop();
                row = index.getRow();
                col = index.getCol();
            } else {
                // Generate until cell is unrevealed and not in list of mine cells.
                Index index = new Index(row, col);
                while(ag.getKnowledgeBase()[row][col].getRevealed() || ag.getMineCells().contains(index)) {
                    row = rand.nextInt(dim);
                    col = rand.nextInt(dim);
                    index = new Index(row, col);
                }
            }

            ag.selectCell(row, col);
            ag.queryCell(row, col);

            numRevealedCells++;

            // DEBUG
            System.out.println("Original board (unknown to agent):\n" + test);
            System.out.println("Selected (" + row + ", " + col + "); " + ag.getKnowledgeBase()[row][col] + "\n" + ag);
            System.out.println("Safe cells: " + ag.getSafeCells());
            System.out.println("Mine cells: " + ag.getMineCells());
            System.out.println("Revealed: " + numRevealedCells + " + " + ag.getMineCells().size() + " = " +  (numRevealedCells + ag.getMineCells().size()));
            System.out.println();
        }
    }
}