package othello;

public class AIPlayer extends Player {
    public AIPlayer(String name, Piece piece) {
        super(name, piece);
    }

    @Override
    public boolean makeMove(Board board, int column, int row) {
        return false;
    }

    public int minimax(boolean maxmin, Board board, int depth) {
        if (depth == 0 || board.isOver()) {
            return heuristic(board);
        }

        if (maxmin) {
            int temp = Integer.MIN_VALUE;
            for (int r = 0; r < board.getRows(); r++) {
                for (int c = 0; c < board.getColumns(); c++) {
                    if (board.canPlacePiece(r, c, this.getPiece())) {
                        Board newBoard = board.cloneBoard();
                        newBoard.placePiece(r, c, this.getPiece());
                        int value = minimax(false, newBoard, depth - 1);
                        temp = Math.max(temp, value);
                    }
                }
            }
            return temp;
        }
        if (!maxmin) {
            int temp = Integer.MAX_VALUE;
            Piece opponentPiece = this.getPiece() == Piece.BLACK ? Piece.WHITE : Piece.BLACK;
            for (int r = 0; r < board.getRows(); r++) {
                for (int c = 0; c < board.getColumns(); c++) {
                    if (board.canPlacePiece(r, c, opponentPiece)) {
                        Board newBoard = board.cloneBoard();
                        newBoard.placePiece(r, c, opponentPiece);
                        int value = minimax(true, newBoard, depth - 1);
                        temp = Math.min(temp, value);
                    }
                }
            }
            return temp;
        }
        return 0;

    }

    private int heuristic(Board board) {
        int score = 0;
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getColumns(); c++) {
                Piece piece = board.getPiece(r, c);
                if (piece == this.getPiece()) {
                    score++;
                } else if (piece != null) {
                    score--;
                }
            }
        }


        return score;
    }
}
