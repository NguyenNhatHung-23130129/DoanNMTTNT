package othello;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class OthelloView extends Application {
    private Stage stage;
    private Scene scene;
    private GridPane boardPane;
    private Button[][] cellButtons;
    private Board board;
    private Piece currentPlayer = Piece.BLACK;
    private final int DEFAULT_SIZE = 8;
    private final int CELL_SIZE = 64;

    private Label statusLabel;
    private Label scoreLabel;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        this.board = new Board(DEFAULT_SIZE, DEFAULT_SIZE);


        BorderPane root = new BorderPane();
        MenuBar menuBar = createMenuBar();
        HBox statusBar = createStatusBar();

        showBoardUI(DEFAULT_SIZE, DEFAULT_SIZE);
        root.setCenter(boardPane);
        VBox topContainer = new VBox(menuBar, statusBar);

        root.setTop(topContainer);


        placeInitialPieces();



        updateAllCells();
        updateStatusBar();

        scene = new Scene(root);
        stage.setTitle("Othello Game");
        stage.setScene(scene);
        stage.show();
    }

    private MenuBar createMenuBar() {
        MenuItem itemNew = new MenuItem("New Game");
        MenuItem itemSave = new MenuItem("Save Game");
        MenuItem itemExit = new MenuItem("Exit");

        itemNew.setOnAction(e -> {
            System.out.println("New Game clicked");
            // resetGame();
        });

        itemExit.setOnAction(e -> Platform.exit());

        Menu gameMenu = new Menu("Game");

        gameMenu.getItems().addAll(itemNew, new SeparatorMenuItem(), itemSave, new SeparatorMenuItem(), itemExit);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(gameMenu);

        return menuBar;
    }

    private HBox createStatusBar() {
        HBox statusBar = new HBox(20);
        statusBar.setPadding(new Insets(10));
        statusBar.setAlignment(Pos.CENTER);
        statusBar.setStyle("-fx-background-color: #f0f0f0;");

        statusLabel = new Label("Lượt: ĐEN");


        scoreLabel = new Label("Đen: 2 | Trắng: 2");
        scoreLabel.setStyle("-fx-font-size: 14px;");


        statusBar.getChildren().addAll(statusLabel, scoreLabel);
        return statusBar;
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
                cell.setStyle("-fx-background-color: #2E8B57; " +
                        "-fx-border-color: black; " +
                        "-fx-border-width: 1;");

                final int row = r;
                final int col = c;

                // click
                cell.setOnAction(e -> handleCellClick(row, col));



                cell.setOnMouseExited(e -> {
                    if (board.getPiece(row, col) == null) {
                        cell.setStyle("-fx-background-color: #2E8B57; " +
                                "-fx-border-color: black; " +
                                "-fx-border-width: 1;");
                    }
                });

                cellButtons[r][c] = cell;
                boardPane.add(cell, c, r);
            }
        }
    }

    private void handleCellClick(int row, int col) {


        // Kiểm tra đặt quân
        if (!board.canPlacePiece(row, col, currentPlayer)) {
            showAlert("Nước đi không hợp lệ", "Không thể đặt quân cờ tại vị trí này!");
            return;
        }


        board.placePiece(row, col, currentPlayer);


        updateAllCells();

        // Đổi lượt
        currentPlayer = currentPlayer.flip();


        if (!hasValidMove(currentPlayer)) {
            currentPlayer = currentPlayer.flip(); // Đổi lại lượt

            if (!hasValidMove(currentPlayer)) {


                return;
            } else {
                showAlert("Thông báo", "Đối thủ không có nước đi hợp lệ. Bạn tiếp tục!");
            }
        }

        updateStatusBar();
    }

    private boolean hasValidMove(Piece player) {
        for (int r = 0; r < DEFAULT_SIZE; r++) {
            for (int c = 0; c < DEFAULT_SIZE; c++) {
                if (board.getPiece(r, c) == null && board.canPlacePiece(r, c, player)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void placeInitialPieces() {
        // Đặt 4 quân khởi đầu - cập nhật vào Board
        board.setPiece(3, 3, Piece.WHITE);
        board.setPiece(4, 4, Piece.WHITE);
        board.setPiece(3, 4, Piece.BLACK);
        board.setPiece(4, 3, Piece.BLACK);
    }

    private void updateAllCells() {
        for (int r = 0; r < DEFAULT_SIZE; r++) {
            for (int c = 0; c < DEFAULT_SIZE; c++) {
                updateCellVisual(r, c, board.getPiece(r, c));
            }
        }
    }

    private void updateCellVisual(int row, int col, Piece piece) {
        if (row < 0 || row >= cellButtons.length ||
                col < 0 || col >= cellButtons[0].length) return;

        Button cell = cellButtons[row][col];

        if (piece == null) {
            cell.setGraphic(null);
        } else {
            Circle circle = new Circle(CELL_SIZE * 0.35);
            circle.setFill(piece.getColor());
            circle.setStroke(piece == Piece.BLACK ? Color.web("#222") : Color.web("#ccc"));
            circle.setStrokeWidth(2);
            cell.setGraphic(circle);
        }
    }

    private void updateStatusBar() {
        String playerName = (currentPlayer == Piece.BLACK) ? "ĐEN" : "TRẮNG";
        statusLabel.setText("Lượt: " + playerName);

        int blackCount = countPieces(Piece.BLACK);
        int whiteCount = countPieces(Piece.WHITE);
        scoreLabel.setText("Đen: " + blackCount + " | Trắng: " + whiteCount);
    }

    private int countPieces(Piece piece) {
        int count = 0;
        for (int r = 0; r < DEFAULT_SIZE; r++) {
            for (int c = 0; c < DEFAULT_SIZE; c++) {
                if (board.getPiece(r, c) == piece) {
                    count++;
                }
            }
        }
        return count;
    }





    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}