package othello;

import javafx.application.Platform;

public class OthelloController {
    private Board board;
    private OthelloView view;
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private boolean isAIMode = false;
    private int aiDepth = 4;
    private boolean gameOver = false;

    public OthelloController(OthelloView view) {
        this.view = view;
    }

    // change signature to accept algorithm (ignored for PvP)
    public void initializeGame(int size, boolean aiMode, int depth, AIPlayer.Algorithm algorithm) {
        this.isAIMode = aiMode;
        this.aiDepth = depth;
        this.gameOver = false;
        this.board = new Board(size, size);

        // Khởi tạo người chơi
        this.player1 = new HumanPlayer("Player 1", Piece.BLACK);

        if (isAIMode) {
            this.player2 = new AIPlayer("AI", Piece.WHITE, depth, algorithm);
        } else {
            this.player2 = new HumanPlayer("Player 2", Piece.WHITE);
        }

        this.currentPlayer = player1;

        // Đặt 4 quân khởi đầu
        board.setPiece(size / 2 - 1, size / 2 - 1, Piece.WHITE);
        board.setPiece(size / 2, size / 2, Piece.WHITE);
        board.setPiece(size / 2, size / 2 - 1, Piece.BLACK);
        board.setPiece(size / 2 - 1, size / 2, Piece.BLACK);

        view.updateBoard(board);
        view.updateStatus(currentPlayer.getPiece(), board.countPieces(Piece.BLACK), board.countPieces(Piece.WHITE));
        view.enableBoard();
        view.showValidMoveHints(board, currentPlayer.getPiece());
    }

    public void handlePlayerMove(int row, int col) {
        if (gameOver) {
            return;
        }

        Piece piece = currentPlayer.getPiece();

        if (!board.canPlacePiece(row, col, piece)) {
            view.showInvalidMoveAlert();
            return;
        }

        board.placePiece(row, col, piece);
        view.updateBoard(board);

        // Đổi lượt
        switchPlayer();

        // Kiểm tra nếu người chơi hiện tại không có nước đi
        if (!hasValidMove(currentPlayer.getPiece())) {
            endGame();
            return;
        }

        view.updateStatus(currentPlayer.getPiece(), board.countPieces(Piece.BLACK), board.countPieces(Piece.WHITE));
        view.showValidMoveHints(board, currentPlayer.getPiece());

        // Kiểm tra kết thúc game
        if (checkGameEnd()) {
            endGame();
            return;
        }

        // Nếu là chế độ AI và đến lượt AI
        if (isAIMode && currentPlayer instanceof AIPlayer) {
            view.disableBoard();
            // Delay để người chơi thấy nước đi của mình
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                    Platform.runLater(this::makeAIMove);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void makeAIMove() {
        if (gameOver) return;

        if (currentPlayer instanceof AIPlayer) {
            AIPlayer aiPlayer = (AIPlayer) currentPlayer;
            int[] move = aiPlayer.calculateBestMove(board, aiDepth);

            if (move != null) {
                board.placePiece(move[0], move[1], aiPlayer.getPiece());
                view.updateBoard(board);

                // Đổi lượt
                switchPlayer();

                // Kiểm tra nếu người chơi tiếp theo không có nước đi

                    if (!hasValidMove(currentPlayer.getPiece())) {
                        endGame();
                        return;
                    }


                view.updateStatus(currentPlayer.getPiece(), board.countPieces(Piece.BLACK), board.countPieces(Piece.WHITE));
                view.showValidMoveHints(board, currentPlayer.getPiece());
                view.enableBoard();

                // Kiểm tra kết thúc game
                if (checkGameEnd()) {
                    endGame();
                }
            }
        }
    }

    public boolean checkGameEnd() {
        return board.isFull() || (!hasValidMove(Piece.BLACK) && !hasValidMove(Piece.WHITE));
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
    }

    private boolean hasValidMove(Piece piece) {
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getColumns(); c++) {
                if (board.canPlacePiece(r, c, piece)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void endGame() {
        gameOver = true;
        int blackCount = board.countPieces(Piece.BLACK);
        int whiteCount = board.countPieces(Piece.WHITE);

        String winner;
        if (blackCount > whiteCount) {
            winner = "QUÂN ĐEN THẮNG";
        } else if (whiteCount > blackCount) {
            winner = "QUÂN TRẮNG THẮNG";
        } else {
            winner = "HÒA";
        }
    view.updateStatus(currentPlayer.getPiece(), blackCount, whiteCount);
        view.showGameOverAlert(winner, blackCount, whiteCount);
    }

    public Board getBoard() {
        return board;
    }


}
