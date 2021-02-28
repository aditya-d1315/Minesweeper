package Minesweeper;

public class Main {
    public static void main(String[] args) {
        // Test environment.
        Environment test = new Environment(10, 30);
        System.out.println(test.getNum_mines());
    }
}