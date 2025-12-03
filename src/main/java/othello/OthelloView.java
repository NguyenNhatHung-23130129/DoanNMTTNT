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
    private boolean gameOver = false;

    // Player types
    private PlayerType blackPlayerType = PlayerType.HUMAN;
    private PlayerType whitePlayerType = PlayerType.HUMAN;

    private enum PlayerType {
        HUMAN("Người"),
        COMPUTER("Máy");

        private final String displayName;

        PlayerType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

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
        showValidMoveHints();
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

        itemNew.setOnAction(e -> resetGame());

        itemSave.setOnAction(e -> {
            showAlert("Lưu game", "Chức năng lưu game chưa được triển khai!");
        });

        itemExit.setOnAction(e -> Platform.exit());

        Menu gameMenu = new Menu("Game");
        gameMenu.getItems().addAll(itemNew, new SeparatorMenuItem(), itemSave, new SeparatorMenuItem(), itemExit);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(gameMenu);

        return menuBar;
    }

    private HBox createStatusBar() {
        HBox statusBar = new HBox(15);
        statusBar.setPadding(new Insets(10));
        statusBar.setAlignment(Pos.CENTER);
        statusBar.setStyle("-fx-background-color: #f0f0f0;");

        // Nút chọn người chơi
        Button playerSelectButton = new Button("Chọn người chơi");
        playerSelectButton.setStyle("-fx-font-size: 12px;");
        playerSelectButton.setOnAction(e -> showPlayerSelectionDialog());

        statusLabel = new Label("Lượt: ĐEN");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        scoreLabel = new Label("Đen: 2 | Trắng: 2");
        scoreLabel.setStyle("-fx-font-size: 14px;");

        // Nút reset
        Button resetButton = new Button("Reset Game");
        resetButton.setStyle("-fx-font-size: 12px;");
        resetButton.setOnAction(e -> resetGame());

        statusBar.getChildren().addAll(playerSelectButton, statusLabel, scoreLabel, resetButton);
        return statusBar;
    }

    private void showPlayerSelectionDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Chọn người chơi");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        HBox whiteBox = new HBox(10);
        whiteBox.setAlignment(Pos.CENTER_LEFT);
        Label whiteLabel = new Label("Quân Trắng:");


        ToggleGroup whiteGroup = new ToggleGroup();
        RadioButton whiteHuman = new RadioButton("Người");
        RadioButton whiteComputer = new RadioButton("Máy");
        whiteHuman.setToggleGroup(whiteGroup);
        whiteComputer.setToggleGroup(whiteGroup);

        if (whitePlayerType == PlayerType.HUMAN) {
            whiteHuman.setSelected(true);
        } else {
            whiteComputer.setSelected(true);
        }

        whiteBox.getChildren().addAll(whiteLabel, whiteHuman, whiteComputer);


        HBox blackBox = new HBox(10);
        blackBox.setAlignment(Pos.CENTER_LEFT);
        Label blackLabel = new Label("Quân Đen:");


        ToggleGroup blackGroup = new ToggleGroup();
        RadioButton blackHuman = new RadioButton("Người");
        RadioButton blackComputer = new RadioButton("Máy");
        blackHuman.setToggleGroup(blackGroup);
        blackComputer.setToggleGroup(blackGroup);

        if (blackPlayerType == PlayerType.HUMAN) {
            blackHuman.setSelected(true);
        } else {
            blackComputer.setSelected(true);
        }

        blackBox.getChildren().addAll(blackLabel, blackHuman, blackComputer);

        content.getChildren().addAll(whiteBox, blackBox);
        dialog.getDialogPane().setContent(content);

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, cancelButton);

        dialog.showAndWait().ifPresent(response -> {
            if (response == okButton) {
                whitePlayerType = whiteHuman.isSelected() ? PlayerType.HUMAN : PlayerType.COMPUTER;
                blackPlayerType = blackHuman.isSelected() ? PlayerType.HUMAN : PlayerType.COMPUTER;
            }
        });
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

                cell.setOnAction(e -> handleCellClick(row, col));

                cell.setOnMouseEntered(e -> {
                    if (!gameOver && board.getPiece(row, col) == null &&
                            board.canPlacePiece(row, col, currentPlayer)) {
                        cell.setStyle("-fx-background-color: #2E8B57; " +
                                "-fx-border-color: black; " +
                                "-fx-border-width: 2;");
                    }
                });

                cell.setOnMouseExited(e -> {
                    if (board.getPiece(row, col) == null) {
                        cell.setStyle("-fx-background-color: #2E8B57; " +
                                "-fx-border-color: black; " +
                                "-fx-border-width: 1;");

                        if (!gameOver && board.canPlacePiece(row, col, currentPlayer)) {
                            showHintAtCell(row, col);
                        }
                    }
                });

                cellButtons[r][c] = cell;
                boardPane.add(cell, c, r);
            }
        }
    }

    private void handleCellClick(int row, int col) {
        if (gameOver) {
            return;
        }

        if (!board.canPlacePiece(row, col, currentPlayer)) {
            return;
        }

        board.placePiece(row, col, currentPlayer);
        updateAllCells();

        currentPlayer = currentPlayer.flip();

        if (!hasValidMove(currentPlayer)) {
            currentPlayer = currentPlayer.flip();

            if (!hasValidMove(currentPlayer)) {
                endGame();
                return;
            }
        }

        showValidMoveHints();
        updateStatusBar();
    }

    private void showValidMoveHints() {

        clearAllHints();


        for (int r = 0; r < DEFAULT_SIZE; r++) {
            for (int c = 0; c < DEFAULT_SIZE; c++) {
                if (board.getPiece(r, c) == null && board.canPlacePiece(r, c, currentPlayer)) {
                    showHintAtCell(r, c);
                }
            }
        }
    }

    private void showHintAtCell(int row, int col) {
        Button cell = cellButtons[row][col];
        if (cell.getGraphic() == null) {
            Circle hint = new Circle(2);
            hint.setFill(Color.BLACK);
            hint.setStroke(Color.BLACK);
            hint.setStrokeWidth(2);
            cell.setGraphic(hint);
        }
    }

    private void clearAllHints() {
        for (int r = 0; r < DEFAULT_SIZE; r++) {
            for (int c = 0; c < DEFAULT_SIZE; c++) {
                if (board.getPiece(r, c) == null) {
                    cellButtons[r][c].setGraphic(null);
                }
            }
        }
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

    private void endGame() {
        gameOver = true;
        clearAllHints();

        int blackCount = countPieces(Piece.BLACK);
        int whiteCount = countPieces(Piece.WHITE);


        Dialog<ButtonType> resultDialog = new Dialog<>();
        resultDialog.setTitle("Kết thúc Game");

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-background-color: white;");


        Label resultLabel = new Label();
        resultLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        if (blackCount > whiteCount) {
            resultLabel.setText("QUÂN ĐEN THẮNG");
            resultLabel.setTextFill(Color.web("#2C3E50"));
        } else if (whiteCount > blackCount) {
            resultLabel.setText("QUÂN TRẮNG THẮNG ");
            resultLabel.setTextFill(Color.web("#2C3E50"));
        } else {
            resultLabel.setText("HÒA️");
            resultLabel.setTextFill(Color.web("#7F8C8D"));
        }

        VBox scoreBox = new VBox(10);
        scoreBox.setAlignment(Pos.CENTER);
        scoreBox.setStyle("-fx-background-color: #ECF0F1; -fx-padding: 20; -fx-background-radius: 10;");

        Label blackScoreLabel = new Label("⚫ Quân Đen: " + blackCount);
        blackScoreLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label whiteScoreLabel = new Label("⚪ Quân Trắng: " + whiteCount);
        whiteScoreLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");



        scoreBox.getChildren().addAll(blackScoreLabel, whiteScoreLabel);

        content.getChildren().addAll(resultLabel, scoreBox);
        resultDialog.getDialogPane().setContent(content);

        ButtonType playAgainButton = new ButtonType("Chơi lại", ButtonBar.ButtonData.OK_DONE);
        ButtonType exitButton = new ButtonType("Thoát", ButtonBar.ButtonData.CANCEL_CLOSE);
        resultDialog.getDialogPane().getButtonTypes().addAll(playAgainButton, exitButton);


        resultDialog.getDialogPane().lookupButton(playAgainButton).setStyle(
                "-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;"
        );

        resultDialog.showAndWait().ifPresent(response -> {
            if (response == playAgainButton) {
                resetGame();
            } else {
                Platform.exit();
            }
        });

        statusLabel.setText("Game kết thúc");
    }

    private void resetGame() {
        board = new Board(DEFAULT_SIZE, DEFAULT_SIZE);
        currentPlayer = Piece.BLACK;
        gameOver = false;

        placeInitialPieces();
        updateAllCells();
        showValidMoveHints();
        updateStatusBar();
    }

    private void placeInitialPieces() {
        board.setPiece(DEFAULT_SIZE/2 - 1, DEFAULT_SIZE/2 - 1, Piece.WHITE);
        board.setPiece(DEFAULT_SIZE/2, DEFAULT_SIZE/2, Piece.WHITE);
        board.setPiece(DEFAULT_SIZE/2, DEFAULT_SIZE/2 - 1, Piece.BLACK);
        board.setPiece(DEFAULT_SIZE/2 - 1, DEFAULT_SIZE/2, Piece.BLACK);
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