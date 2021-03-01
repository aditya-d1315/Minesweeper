package Minesweeper;

public class Main {
    public static void main(String[] args) {
        // Test environment.
        Environment test = new Environment(10, 30);
        //System.out.println(test.getNum_mines());

        System.out.println(test + "\n\n");
        Agent agent = new Agent(10);
        agent.select_position(test);
        System.out.println(agent);
    }
}