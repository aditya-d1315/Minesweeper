package Minesweeper;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        // Test environment.
        Environment test = new Environment(10, 30);
        System.out.println("Original board:\n" + test);
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

        while(numRevealedCells < dim * dim) {
            int row = rand.nextInt(dim);
            int col = rand.nextInt(dim);

            // Pick from list of safe cells if not empty.
            if(!ag.getSafeCells().isEmpty()) {
                row = ag.getSafeCells().get(0).getRow();
                col = ag.getSafeCells().get(0).getCol();
            } else {
                // Generate until cell is unrevealed and not in list of mine cells.
                while(ag.getKnowledgeBase()[row][col].getRevealed() && !ag.getMineCells().contains(new Index(row, col))) {
                    row = rand.nextInt(dim);
                    col = rand.nextInt(dim);
                }
            }

            ag.selectCell(row, col);
            ag.queryCell(row, col);

            System.out.println("Selected (" + row + ", " + col + ")\n" + ag);
            System.out.println("Safe cells: " + ag.getSafeCells());
            System.out.println("Mine cells: " + ag.getMineCells() + "\n");
            numRevealedCells++;
        }
    }
}