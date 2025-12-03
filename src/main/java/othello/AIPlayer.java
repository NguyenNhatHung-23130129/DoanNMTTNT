package othello;

public class AIPlayer extends Player {
    private static final int[][] WEIGHTS = {
            {20, -3, 11, 8, 8, 11, -3, 20},
            {-3, -7, -4, 1, 1, -4, -7, -3},
            {11, -4, 2, 2, 2, 2, -4, 11},
            {8, 1, 2, -3, -3, 2, 1, 8},
            {8, 1, 2, -3, -3, 2, 1, 8},
            {11, -4, 2, 2, 2, 2, -4, 11},
            {-3, -7, -4, 1, 1, -4, -7, -3},
            {20, -3, 11, 8, 8, 11, -3, 20}
    };

    private int defaultDepth = 4;

    public AIPlayer(String name, Piece piece) {
        super(name, piece);
    }

    public AIPlayer(String name, Piece piece, int depth) {
        super(name, piece);
        this.defaultDepth = depth;
    }

    @Override
    public boolean makeMove(Board board, int row, int col) {
        int[] bestMove = calculateBestMove(board, defaultDepth);
        if (bestMove != null) {
            board.placePiece(bestMove[0], bestMove[1], this.getPiece());
            return true;
        }
        return false;
    }

    public int[] calculateBestMove(Board board, int depth) {
        int bestValue = Integer.MIN_VALUE;
        int bestRow = -1;
        int bestCol = -1;

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getColumns(); c++) {
                if (board.canPlacePiece(r, c, this.getPiece())) {
                    Board newBoard = board.cloneBoard();
                    newBoard.placePiece(r, c, this.getPiece());

                    int value = minimax(false, newBoard, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE);

                    if (value > bestValue) {
                        bestValue = value;
                        bestRow = r;
                        bestCol = c;
                    }
                }
            }
        }

        if (bestRow != -1 && bestCol != -1) {
            return new int[]{bestRow, bestCol};
        }
        return null;
    }

    private int minimax(boolean maxmin, Board board, int depth, int alpha, int beta) {
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
                        int value = minimax(false, newBoard, depth - 1, alpha, beta);
                        temp = Math.max(temp, value);
                        alpha = Math.max(alpha, value);
                        if (beta <= alpha) {
                            break;
                        }
                    }
                }
            }
            return temp;
        } else {
            int temp = Integer.MAX_VALUE;
            Piece opponentPiece = this.getPiece() == Piece.BLACK ? Piece.WHITE : Piece.BLACK;
            for (int r = 0; r < board.getRows(); r++) {
                for (int c = 0; c < board.getColumns(); c++) {
                    if (board.canPlacePiece(r, c, opponentPiece)) {
                        Board newBoard = board.cloneBoard();
                        newBoard.placePiece(r, c, opponentPiece);
                        int value = minimax(true, newBoard, depth - 1, alpha, beta);
                        temp = Math.min(temp, value);
                        beta = Math.min(beta, value);
                        if (beta <= alpha) {
                            break;
                        }
                    }
                }
            }
            return temp;
        }
    }

    private int heuristic(Board board) {
        Piece myPiece = this.getPiece();
        Piece oppPiece = myPiece.flip();

        int myScore = 0;
        int oppScore = 0;
        int myMobility = 0;
        int oppMobility = 0;

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getColumns(); c++) {
                Piece p = board.getPiece(r, c);
                if (p == myPiece) {
                    myScore += WEIGHTS[r][c];
                } else if (p == oppPiece) {
                    oppScore += WEIGHTS[r][c];
                }
                if (board.canPlacePiece(r, c, myPiece)) myMobility++;
                if (board.canPlacePiece(r, c, oppPiece)) oppMobility++;
            }
        }

        return (myScore - oppScore) + 10 * (myMobility - oppMobility);
    }
}
