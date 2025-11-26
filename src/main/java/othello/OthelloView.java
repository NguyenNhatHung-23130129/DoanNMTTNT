package othello;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class OthelloView extends Application {
    private Stage stage;
    private Scene scene;
    private GridPane boardPane;
    private Button[][] cellButtons;
    private boolean currentBlack = true;
    private final int DEFAULT_SIZE = 8;
    private final int CELL_SIZE = 64;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        showBoardUI(DEFAULT_SIZE, DEFAULT_SIZE);
        placeInitialPieces(); // đặt 4 quân cờ khởi đầu
        stage.setTitle("Othello");
        stage.setScene(scene);
        stage.show();
    }

    public void showBoardUI(int rows, int cols) {
        boardPane = new GridPane();
        boardPane.setPadding(new Insets(8));
        boardPane.setHgap(2);
        boardPane.setVgap(2);
        boardPane.setStyle("-fx-background-color: #006400; -fx-border-color: black; -fx-border-width: 2;");

        cellButtons = new Button[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Button cell = new Button();
                cell.setPrefSize(CELL_SIZE, CELL_SIZE);
                cell.setMinSize(CELL_SIZE, CELL_SIZE);
                cell.setMaxSize(CELL_SIZE, CELL_SIZE);
                cell.setStyle("-fx-background-color: #2E8B57; " + "-fx-border-color: black; " + "-fx-border-width: 1;"
                );

                cellButtons[r][c] = cell;
                boardPane.add(cell, c, r);
            }
        }

        scene = new Scene(boardPane);
    }

    // Đặt 4 quân cờ khởi đầu
    private void placeInitialPieces() {
        updateCellVisual(3, 3, Piece.WHITE);
        updateCellVisual(4, 4, Piece.WHITE);
        updateCellVisual(3, 4, Piece.BLACK);
        updateCellVisual(4, 3, Piece.BLACK);
        // currentBlack = true; // giữ black đi trước
    }

    private void updateCellVisual(int row, int col, Piece piece) {
        if (row < 0 || row >= cellButtons.length || col < 0 || col >= cellButtons[0].length) return;
        Button cell = cellButtons[row][col];

        if (piece == null) {
            cell.setGraphic(null); // Ô trống
        } else {
            Circle circle = new Circle(CELL_SIZE * 0.35);
            circle.setFill(piece.getColor()); // Lấy màu từ Enum
            // Logic viền cho đẹp
            circle.setStroke(piece == Piece.BLACK ? Color.web("#222") : Color.web("#ccc"));
            circle.setStrokeWidth(2);
            cell.setGraphic(circle);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}