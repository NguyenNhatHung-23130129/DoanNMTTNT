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
    private GridPane boardPane;
    private Button[][] cellButtons;
    private final int DEFAULT_SIZE = 8;
    private final int CELL_SIZE = 64;
    private OthelloController controller;
    private Label statusLabel;
    private Label scoreLabel;

    private static final String NORMAL_CELL = "-fx-background-color: #2E8B57; -fx-border-color: black; -fx-border-width: 1;";
    private static final String HINT_CELL = "-fx-background-color: #ADFF2F; -fx-border-color: black; -fx-border-width: 1;";

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        this.controller = new OthelloController(this);

        BorderPane root = new BorderPane();
        MenuBar menuBar = createMenuBar();
        HBox statusBar = createStatusBar();

        showBoardUI(DEFAULT_SIZE, DEFAULT_SIZE);
        root.setCenter(boardPane);
        VBox topContainer = new VBox(menuBar, statusBar);
        root.setTop(topContainer);

        // Hiển thị dialog chọn chế độ khi khởi động
        Platform.runLater(this::showGameModeDialog);

        Scene scene = new Scene(root);
        stage.setTitle("Othello Game");
        stage.setScene(scene);
        stage.show();
    }

    private MenuBar createMenuBar() {
        MenuItem itemNew = new MenuItem("New Game");
        MenuItem itemExit = new MenuItem("Exit");

        itemNew.setOnAction(e -> showGameModeDialog());
        itemExit.setOnAction(e -> Platform.exit());

        Menu gameMenu = new Menu("Game");
        gameMenu.getItems().addAll(itemNew, new SeparatorMenuItem(), itemExit);

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
        statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        scoreLabel = new Label("Đen: 2 | Trắng: 2");
        scoreLabel.setStyle("-fx-font-size: 14px;");

        Button resetButton = new Button("Reset Game");
        resetButton.setStyle("-fx-font-size: 12px;");
        resetButton.setOnAction(e -> showGameModeDialog());

        statusBar.getChildren().addAll(statusLabel, scoreLabel, resetButton);
        return statusBar;
    }

    private void showGameModeDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Chọn chế độ chơi");
        dialog.setHeaderText("Bạn muốn chơi với ai?");

        ButtonType pvpButton = new ButtonType("Người vs Người", ButtonBar.ButtonData.OK_DONE);
        ButtonType pvaButton = new ButtonType("Người vs Máy", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(pvpButton, pvaButton, cancelButton);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);

        Label label1 = new Label("🎮 Người vs Người: Hai người chơi chơi với nhau");
        Label label2 = new Label("🤖 Người vs Máy: Chơi với AI");
        label1.setStyle("-fx-font-size: 13px;");
        label2.setStyle("-fx-font-size: 13px;");

        content.getChildren().addAll(label1, label2);
        dialog.getDialogPane().setContent(content);

        dialog.showAndWait().ifPresent(response -> {
            if (response == pvpButton) {
                // pass a default algorithm when not using AI
                controller.initializeGame(DEFAULT_SIZE, false, 4, AIPlayer.Algorithm.MINIMAX);
            } else if (response == pvaButton) {
                showDifficultyDialog();
            }
        });
    }

    private void showDifficultyDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Chọn độ khó");
        dialog.setHeaderText("Chọn độ khó của AI");

        ButtonType easyButton = new ButtonType("Dễ", ButtonBar.ButtonData.OK_DONE);
        ButtonType mediumButton = new ButtonType("Trung bình", ButtonBar.ButtonData.OK_DONE);
        ButtonType hardButton = new ButtonType("Khó", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(easyButton, mediumButton, hardButton, cancelButton);

        // Add algorithm choice controls
        ToggleGroup algoGroup = new ToggleGroup();
        RadioButton rbMinimax = new RadioButton("Minimax");
        RadioButton rbAlphaBeta = new RadioButton("Alpha-Beta");
        rbMinimax.setToggleGroup(algoGroup);
        rbAlphaBeta.setToggleGroup(algoGroup);
        rbMinimax.setSelected(true); // default

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.getChildren().addAll(
                new Label("Dễ: AI suy nghĩ ít (Depth 2)"),
                new Label("Trung bình: AI suy nghĩ vừa (Depth 4)"),
                new Label("Khó: AI suy nghĩ nhiều (Depth 6)"),
                new Separator(),
                new Label("Chọn thuật toán cho AI:"),
                rbMinimax,
                rbAlphaBeta
        );
        dialog.getDialogPane().setContent(content);

        dialog.showAndWait().ifPresent(response -> {
            int depth;
            if (response == easyButton) {
                depth = 2;
            } else if (response == mediumButton) {
                depth = 4;
            } else if (response == hardButton) {
                depth = 6;
            } else {
                return;
            }
            AIPlayer.Algorithm algorithm = rbMinimax.isSelected() ? AIPlayer.Algorithm.MINIMAX : AIPlayer.Algorithm.ALPHABETA;
            controller.initializeGame(DEFAULT_SIZE, true, depth, algorithm);
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
                cell.setStyle(NORMAL_CELL);

                final int row = r;
                final int col = c;

                cell.setOnAction(e -> controller.handlePlayerMove(row, col));

                cellButtons[r][c] = cell;
                boardPane.add(cell, c, r);
            }
        }
    }

    public void updateBoard(Board board) {
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getColumns(); c++) {
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
            cell.setStyle(NORMAL_CELL);
        } else {
            Circle circle = new Circle(CELL_SIZE * 0.35);
            circle.setFill(piece.getColor());
            circle.setStroke(piece == Piece.BLACK ? Color.web("#222") : Color.web("#ccc"));
            circle.setStrokeWidth(2);
            cell.setGraphic(circle);
            cell.setStyle(NORMAL_CELL);
        }
    }

    public void updateStatus(Piece currentPiece, int blackCount, int whiteCount) {
        String playerName = (currentPiece == Piece.BLACK) ? "ĐEN" : "TRẮNG";
        statusLabel.setText("Lượt: " + playerName);
        scoreLabel.setText("Đen: " + blackCount + " | Trắng: " + whiteCount);
    }

    public void showValidMoveHints(Board board, Piece currentPlayer) {
        clearAllHints();
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getColumns(); c++) {
                if (board.getPiece(r, c) == null && board.canPlacePiece(r, c, currentPlayer)) {
                    showHintAtCell(r, c);
                }
            }
        }
    }

    private void showHintAtCell(int row, int col) {
        cellButtons[row][col].setStyle(HINT_CELL);
    }

    private void clearAllHints() {
        for (int r = 0; r < cellButtons.length; r++) {
            for (int c = 0; c < cellButtons[0].length; c++) {
                if (controller.getBoard() != null &&
                        controller.getBoard().getPiece(r, c) == null) {
                    cellButtons[r][c].setStyle(NORMAL_CELL);
                }
            }
        }
    }

    public void showInvalidMoveAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Nước đi không hợp lệ");
        alert.setHeaderText(null);
        alert.setContentText("Không thể đặt quân cờ tại vị trí này!");
        alert.showAndWait();
    }


    public void showGameOverAlert(String winner, int blackCount, int whiteCount) {
        Dialog<ButtonType> resultDialog = new Dialog<>();
        resultDialog.setTitle("Kết thúc Game");

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-background-color: white;");

        Label resultLabel = new Label(winner);
        resultLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

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

        resultDialog.showAndWait().ifPresent(response -> {
            if (response == playAgainButton) {
                showGameModeDialog();
            } else {
                Platform.exit();
            }
        });
    }

    public void disableBoard() {
        for (int r = 0; r < cellButtons.length; r++) {
            for (int c = 0; c < cellButtons[0].length; c++) {
                cellButtons[r][c].setDisable(true);
            }
        }
    }

    public void enableBoard() {
        for (int r = 0; r < cellButtons.length; r++) {
            for (int c = 0; c < cellButtons[0].length; c++) {
                cellButtons[r][c].setDisable(false);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
