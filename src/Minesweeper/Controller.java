package Minesweeper;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import javafx.scene.input.MouseEvent;

/**
 * This class contains all the methods required for the nodes in the UI to work.
 * @author Annie Thach
 */
public class Controller {
    @FXML
    private GridPane root_pane;

    @FXML
    private TextArea output_box;

    @FXML
    private TextField dim_entry;

    @FXML
    private TextField num_mines_entry;

    @FXML
    private GridPane board;

    @FXML
    private Label score_text;

    @FXML
    private Button show_mines_button;

    private int BOARD_SIZE = 880;
    private Environment game;
    private boolean[][] board_state;

    private String UNFLIPPED_TEXT = "?";
    private String MINE_TEXT = "X";
    private String UNFLIPPED_COLOR = "#97969d";
    private String MINE_COLOR = "#2f2f32";
    private String ZERO_COLOR = "#63bbb5";
    private String ONE_COLOR = "#63bb88";
    private String TWO_COLOR = "#6bbb63";
    private String THREE_COLOR = "#8ebb63";
    private String FOUR_COLOR = "#abbb63";
    private String FIVE_COLOR = "#bbb563";
    private String SIX_COLOR = "#bb9d63";
    private String SEVEN_COLOR = "#bb8b63";
    private String EIGHT_COLOR = "#bb7363";
    
    /**
     * Starts new game with entry in textbox.
     * @param event
     */
    @FXML
    void new_game(ActionEvent event) {
        // Read input.
        int dim = 0;
        int num_mines = 0;
        try {
            dim = Integer.parseInt(dim_entry.getText());

            if(dim < 1) {
                output_box.appendText("The dimension is too small!\n");
                return;
            }

            num_mines = Integer.parseInt(num_mines_entry.getText());
        } catch (Exception e) {
            output_box.appendText("Make sure your inputs are positive integers!\n");
            return;
        }

        // Create the new game.
        game = new Environment(dim, num_mines);

        // Draw the board.
        root_pane.getChildren().remove(board);  // Remove the old board.
        board = new GridPane(); // Initialize new grid.
        board.setMinSize(BOARD_SIZE, BOARD_SIZE);
        board.setMaxSize(BOARD_SIZE, BOARD_SIZE);
        board.setHgap(1.0);
        board.setVgap(1.0);

        // Calculate height and width of the cells.
        for(int i = 0; i < dim; i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(BOARD_SIZE/dim);
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(BOARD_SIZE/dim);
            board.getRowConstraints().add(row);
            board.getColumnConstraints().add(col);
        }

        // Fill grid with labels.
        for(int row = 0; row < dim; row++) {
            for (int col = 0; col < dim; col++) {
                double label_size = (BOARD_SIZE - (BOARD_SIZE / 2)) / dim;
                Label label = new Label(UNFLIPPED_TEXT);
                label.setMaxWidth(Double.MAX_VALUE);
                label.setMaxHeight(Double.MAX_VALUE);
                label.setAlignment(Pos.CENTER);
                label.setFont(new Font("Arial", label_size));
                label.setStyle("-fx-background-color: " + UNFLIPPED_COLOR + ";");
                board.add(label, row, col);
                label.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        Node node = (Node)e.getSource();
                        if(node instanceof Label) {
                            query_space(node);
                        }
                    }
                });
            }
        }

        root_pane.add(board, 0, 1); // Add board to pane to display it.

        // Update score and output text.
        score_text.setText(game.getNum_mines() + " / " + game.getNum_mines());
        show_mines_button.setText("Show Mines");
        output_box.clear(); // Clear output box.
        output_box.appendText("Created a " + dim + " x " + dim + " board and scattered " + game.getNum_mines() + " mines.\n");
        output_box.appendText("Click on the spaces and try not to click the mines.\nGood luck!\n");
    }

    /**
     * Reset the game.
     * @param event
     */
    @FXML
    void reset_game(ActionEvent event) {
        unflip_board(); // Reset cells.
        score_text.setText(game.getNum_mines() + " / " + game.getNum_mines());  // Reset score.
        show_mines_button.setText("Show Mines");
        output_box.appendText("Game reset!\n");
    }

    /**
     * Cheat button for debugging.
     * @param event
     */
    @FXML
    void show_mines(ActionEvent event) {
        if(game == null) {
            output_box.appendText("There is no game to show.\n");
            return;
        }

        if(show_mines_button.getText().equals("Show Mines")) {
            show_mines_button.setText("Hide Mines");
            save_state();   // Store original labels.

            // Change labels to show answer.
            for(Node node : board.getChildren()) {
                ((Label)node).setText(String.valueOf(game.getBoard()[GridPane.getRowIndex(node)][GridPane.getColumnIndex(node)]));
                color_tile((Label)node, game.getBoard()[GridPane.getRowIndex(node)][GridPane.getColumnIndex(node)]);
            }

            output_box.appendText("HEY, no cheating! >:(\n");
        } else {
            hide_mines(event);
        }
    }

    /**
     * For debugging.
     * @param event
     */
    @FXML
    void hide_mines(ActionEvent event) {
        show_mines_button.setText("Show Mines");

        unflip_board(); // Unflip every spaces.

        for(Node node : board.getChildren()) {
            if(board_state[GridPane.getRowIndex(node)][GridPane.getColumnIndex(node)]) {
                query_space((Label)node);
            }
        }
    }

    /**
     * Clears text from output.
     * @param event
     */
    @FXML
    void clear_text(ActionEvent event) {
        output_box.clear();
    }

    /**
     * Helper method to decrease score if user touches a mine.
     */
    private void decrement_score() {
        String[] score = score_text.getText().split(" / "); // Separate score from total.
        int current_score = Integer.parseInt(score[0]);

        current_score--;

        if(current_score <= 0) {
            output_box.appendText("GAME OVER! You found all the mines ... ):\n");
        }

        score_text.setText(current_score + " / " + score[1]);
    }

    /**
     * Helper method to get the value of a space.
     * @return  The value of a space, in string form.
     */
    private void query_space(Node node) {
        int row = GridPane.getRowIndex(node);
        int col = GridPane.getColumnIndex(node);
        int value = game.getBoard()[row][col];

        ((Label)node).setText(String.valueOf(value));

        if(value < 0) {
            output_box.appendText("(" + row + ", " + col + ") = BOOM!\n");
            decrement_score();
        } else {
            output_box.appendText("(" + row + ", " + col + ") = " + value + "\n");
        }

        color_tile(node, value);

        return;
    }

    /**
     * Helper method to color value.
     * @param value : The value to color.
     */
    private void color_tile(Node node, int value) {
        String color = UNFLIPPED_COLOR;

        if(value < 0) {
            ((Label)node).setTextFill(Color.web("#ffffff"));
            color = MINE_COLOR;
        } else if(value == 0) {
            color = ZERO_COLOR;
        } else if (value == 1) {
            color = ONE_COLOR;
        } else if (value == 2) {
            color = TWO_COLOR;
        } else if (value == 3) {
            color = THREE_COLOR;
        } else if (value == 4) {
            color = FOUR_COLOR;
        } else if (value == 5) {
            color = FIVE_COLOR;
        } else if (value == 6) {
            color = SIX_COLOR;
        } else if (value == 7) {
            color = SEVEN_COLOR;
        } else if (value == 8) {
            color = EIGHT_COLOR;
        }

        ((Label)node).setStyle("-fx-background-color: " + color + ";");
    }

    /**
     * Helper method to save flips.
     */
    private void save_state() {
        board_state = new boolean[game.getDim()][game.getDim()];
        for(Node node : board.getChildren()) {
            if(!((Label)node).getText().equals(UNFLIPPED_TEXT)) {
                board_state[GridPane.getRowIndex(node)][GridPane.getColumnIndex(node)] = true;
            }
        }
    }

    /**
     * Helper method to unflip board.
     */
    private void unflip_board() {
        for(Node node : board.getChildren()) {
            ((Label)node).setText(UNFLIPPED_TEXT);
            ((Label)node).setTextFill(Color.web("#000000"));
            ((Label)node).setStyle("-fx-background-color: " + UNFLIPPED_COLOR + ";");
        }
    }
}