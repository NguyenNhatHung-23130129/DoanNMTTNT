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
    private static final String HINT_CELL = "-fx-background-color: rgba(60, 179, 113, 0.7);" + "-fx-border-color: #00FF7F;" + "-fx-border-width: 2;" + "-fx-effect: dropshadow(gaussian, #00FF7F, 8, 0.5, 0, 0);";

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
        // Game Menu
        Menu gameMenu = new Menu("Game");
        MenuItem itemNew = new MenuItem("New Game");
        MenuItem itemExit = new MenuItem("Exit");
        itemNew.setOnAction(e -> showGameModeDialog());
        itemExit.setOnAction(e -> Platform.exit());
        gameMenu.getItems().addAll(itemNew, new SeparatorMenuItem(), itemExit);

        // Help Menu
        Menu helpMenu = new Menu("Help");
        MenuItem itemRules = new MenuItem("Rules");
        MenuItem itemAbout = new MenuItem("About");
        itemRules.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Luật chơi");
            alert.setHeaderText("Othello");
            alert.setContentText("• Đen đi trước\n• Nếu một người chơi không thể thực hiện một nước đi hợp lệ, người chơi đó mất lượt và lượt chuyển sang cho đối thủ.\n• Nếu cả hai người chơi đều không thể thực hiện một nước đi hợp lệ, trò chơi sẽ kết thúc. \n• Ai nhiều quân hơn thắng");
            alert.showAndWait();
        });
        itemAbout.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("About");
            alert.setContentText("Othello Game v1.0");
            alert.showAndWait();
        });
        helpMenu.getItems().addAll(itemRules, itemAbout);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(gameMenu, helpMenu);
        return menuBar;
    }

    private HBox createStatusBar() {
        HBox statusBar = new HBox(25);
        statusBar.setPadding(new Insets(12, 20, 12, 20));
        statusBar.setAlignment(Pos.CENTER);
        statusBar.setStyle("-fx-background-color: #34495E;");

        // Lượt chơi
        statusLabel = new Label("⚫ Lượt: ĐEN");
        statusLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Điểm số
        scoreLabel = new Label("⚫ 2  -  2 ⚪");
        scoreLabel.setStyle(
                "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 5 15;" +
                        "-fx-background-color: rgba(255,255,255,0.1);" +
                        "-fx-background-radius: 15;"
        );

        // Nút Reset
        Button resetButton = new Button("New Game");
        resetButton.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-background-color: #27AE60;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 8 15;" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;"
        );
        resetButton.setOnAction(e -> showGameModeDialog());

        statusBar.getChildren().addAll(statusLabel, scoreLabel, resetButton);
        return statusBar;
    }

    private void showGameModeDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Chọn chế độ chơi");
        dialog.setHeaderText(null);

        ButtonType pvpButton = new ButtonType("Người vs Người", ButtonBar.ButtonData.OK_DONE);
        ButtonType pvaButton = new ButtonType("Người vs Máy", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(pvpButton, pvaButton, cancelButton);

        VBox content = new VBox(20);
        content.setPadding(new Insets(25));
        content.setAlignment(Pos.CENTER);

        // Tiêu đề
        Label titleLabel = new Label("Chọn Chế Độ Chơi");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Card PvP
        VBox pvpCard = new VBox(8);
        pvpCard.setPadding(new Insets(15));
        pvpCard.setAlignment(Pos.CENTER);
        pvpCard.setStyle(
                "-fx-background-color: #E8F5E9;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #4CAF50;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-width: 1;"
        );
        Label pvpTitle = new Label("Người vs Người");
        pvpTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        Label pvpDesc = new Label("Hai người chơi với nhau");
        pvpDesc.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        pvpCard.getChildren().addAll( pvpTitle, pvpDesc);

        // Card PvAI
        VBox pvaCard = new VBox(8);
        pvaCard.setPadding(new Insets(15));
        pvaCard.setAlignment(Pos.CENTER);
        pvaCard.setStyle(
                "-fx-background-color: #E3F2FD;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #2196F3;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-width: 1;"
        );
        Label pvaTitle = new Label("Người vs Máy");
        pvaTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        Label pvaDesc = new Label("Chơi với AI thông minh");
        pvaDesc.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        pvaCard.getChildren().addAll( pvaTitle, pvaDesc);

        // Đặt 2 card cạnh nhau
        HBox cardsBox = new HBox(15);
        cardsBox.setAlignment(Pos.CENTER);
        cardsBox.getChildren().addAll(pvpCard, pvaCard);

        content.getChildren().addAll(titleLabel, cardsBox);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefWidth(400);

        dialog.showAndWait().ifPresent(response -> {
            if (response == pvpButton) {
                controller.initializeGame(DEFAULT_SIZE, false, 4, AIPlayer.Algorithm.MINIMAX);
            } else if (response == pvaButton) {
                showDifficultyDialog();
            }
        });
    }

    private void showDifficultyDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Chọn độ khó");
        dialog.setHeaderText(null);

        ButtonType easyButton = new ButtonType("Dễ", ButtonBar.ButtonData.LEFT);
        ButtonType mediumButton = new ButtonType("Trung bình", ButtonBar.ButtonData.LEFT);
        ButtonType hardButton = new ButtonType("Khó", ButtonBar.ButtonData.LEFT);
        ButtonType cancelButton = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(easyButton, mediumButton, hardButton, cancelButton);

        VBox content = new VBox(15);
        content.setPadding(new Insets(25));

        // Tiêu đề
        Label titleLabel = new Label("Chọn Độ Khó AI");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Các mức độ khó
        VBox difficultyBox = new VBox(10);
        difficultyBox.setPadding(new Insets(10));
        difficultyBox.setStyle(
                "-fx-background-color: #F5F5F5;" +
                        "-fx-background-radius: 8;"
        );

        HBox easyRow = createDifficultyRow( "Dễ", "AI suy nghĩ nhanh (Depth 2)", "#4CAF50");
        HBox mediumRow = createDifficultyRow( "Trung bình", "AI suy nghĩ vừa (Depth 4)", "#FF9800");
        HBox hardRow = createDifficultyRow( "Khó", "AI suy nghĩ sâu (Depth 6)", "#F44336");

        difficultyBox.getChildren().addAll(easyRow, mediumRow, hardRow);

        // Separator
        Separator separator = new Separator();
        separator.setPadding(new Insets(5, 0, 5, 0));

        // Thuật toán
        Label algoLabel = new Label("Thuật toán AI:");
        algoLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        ToggleGroup algoGroup = new ToggleGroup();
        RadioButton rbMinimax = new RadioButton("Minimax");
        RadioButton rbAlphaBeta = new RadioButton("Alpha-Beta");
        rbMinimax.setToggleGroup(algoGroup);
        rbAlphaBeta.setToggleGroup(algoGroup);
        rbAlphaBeta.setSelected(true);

        rbMinimax.setStyle("-fx-font-size: 13px;");
        rbAlphaBeta.setStyle("-fx-font-size: 13px;");

        HBox algoBox = new HBox(20);
        algoBox.setPadding(new Insets(10, 0, 0, 0));
        algoBox.getChildren().addAll(rbMinimax, rbAlphaBeta);

        content.getChildren().addAll(titleLabel, difficultyBox, separator, algoLabel, algoBox);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefWidth(380);

        dialog.showAndWait().ifPresent(response -> {
            int depth;
            if (response == easyButton) depth = 2;
            else if (response == mediumButton) depth = 4;
            else if (response == hardButton) depth = 6;
            else return;

            AIPlayer.Algorithm algorithm = rbMinimax.isSelected()
                    ? AIPlayer.Algorithm.MINIMAX
                    : AIPlayer.Algorithm.ALPHABETA;
            controller.initializeGame(DEFAULT_SIZE, true, depth, algorithm);
        });
    }

    private HBox createDifficultyRow( String title, String desc, String color) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8));


        VBox textBox = new VBox(2);
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        Label descLabel = new Label(desc);
        descLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        textBox.getChildren().addAll(titleLabel, descLabel);

        row.getChildren().addAll(textBox);
        return row;
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
        String icon = (currentPiece == Piece.BLACK) ? "⚫" : "⚪";
        String playerName = (currentPiece == Piece.BLACK) ? "ĐEN" : "TRẮNG";
        statusLabel.setText(icon + " Lượt: " + playerName);
        scoreLabel.setText("⚫ " + blackCount + "  -  " + whiteCount + " ⚪");
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



        // Kết quả
        Label resultLabel = new Label(winner);
        resultLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        // Điểm số
        HBox scoreBox = new HBox(30);
        scoreBox.setAlignment(Pos.CENTER);
        scoreBox.setPadding(new Insets(15));
        scoreBox.setStyle("-fx-background-color: #ECF0F1; -fx-background-radius: 10;");

        VBox blackBox = new VBox(5);
        blackBox.setAlignment(Pos.CENTER);
        Label blackIcon = new Label("⚫");
        blackIcon.setStyle("-fx-font-size: 25px;");
        Label blackScore = new Label(String.valueOf(blackCount));
        blackScore.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        Label blackLabel = new Label("Đen");
        blackLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        blackBox.getChildren().addAll(blackIcon, blackScore, blackLabel);

        Label vsLabel = new Label("vs");
        vsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #999;");

        VBox whiteBox = new VBox(5);
        whiteBox.setAlignment(Pos.CENTER);
        Label whiteIcon = new Label("⚪");
        whiteIcon.setStyle("-fx-font-size: 25px;");
        Label whiteScore = new Label(String.valueOf(whiteCount));
        whiteScore.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        Label whiteLabel = new Label("Trắng");
        whiteLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        whiteBox.getChildren().addAll(whiteIcon, whiteScore, whiteLabel);

        scoreBox.getChildren().addAll(blackBox, vsLabel, whiteBox);

        content.getChildren().addAll( resultLabel, scoreBox);
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

    // Thông báo khi không có nước đi hợp lệ, xác nhận trước khi chuyển lượt
    public void showNoValidMoveAlert(String playerName, Runnable onConfirm) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Không có nước đi hợp lệ");
            alert.setHeaderText(null);
            alert.setContentText("Người chơi " + playerName + " không có nước đi hợp lệ và sẽ bị mất lượt.\nNhấn OK để tiếp tục.");
            alert.getButtonTypes().setAll(ButtonType.OK);
            alert.showAndWait();
            if (onConfirm != null) onConfirm.run();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
