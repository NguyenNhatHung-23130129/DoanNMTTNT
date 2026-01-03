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

    // Add algorithm selection
    public enum Algorithm { MINIMAX, ALPHABETA }

    private int defaultDepth = 4;
    private Algorithm algorithm = Algorithm.MINIMAX; // default

    public AIPlayer(String name, Piece piece) {
        super(name, piece);
    }

    public AIPlayer(String name, Piece piece, int depth) {
        super(name, piece);
        this.defaultDepth = depth;
    }

    // new constructor with algorithm
    public AIPlayer(String name, Piece piece, int depth, Algorithm algorithm) {
        super(name, piece);
        this.defaultDepth = depth;
        this.algorithm = algorithm;
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

    private int alphaBeta(boolean maxmin, Board board, int depth, int alpha, int beta) {
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
                        int value = alphaBeta(false, newBoard, depth - 1, alpha, beta);
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
                        int value = alphaBeta(true, newBoard, depth - 1, alpha, beta);
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
    private int minimax(boolean maxmin, Board board, int depth) {
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
        } else {
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
    }

    public int[] calculateBestMove(Board board, int depth) {

        System.gc(); // don rac bo nho
      double startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        double startTime = System.nanoTime();


        int bestValue = Integer.MIN_VALUE;
        int bestRow = -1;
        int bestCol = -1;

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getColumns(); c++) {
                if (board.canPlacePiece(r, c, this.getPiece())) {
                    Board newBoard = board.cloneBoard();
                    newBoard.placePiece(r, c, this.getPiece());

                    int value;
                    // choose algorithm based on setting
                    if (this.algorithm == Algorithm.ALPHABETA) {
                        value = alphaBeta(false, newBoard, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
                    } else {
                        value = minimax(false, newBoard, depth - 1);
                    }

                    if (value > bestValue) {
                        bestValue = value;
                        bestRow = r;
                        bestCol = c;
                    }
                }
            }
        }
        long endTime = System.nanoTime();
        long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        double duration = (endTime - startTime) / 1_000_000;
        double memoryUsed = (endMemory - startMemory) / 1024;

        System.out.println("--------------------------------------------------");
        System.out.println("Algorithm: " + this.algorithm + " | Depth: " + depth);
        System.out.println("Time: " + duration + " ms");
        System.out.println("Memory Diff: " + memoryUsed + " KB ");
        System.out.println("--------------------------------------------------");

        if (bestRow != -1 && bestCol != -1) {
            return new int[]{bestRow, bestCol};
        }
        return null;
    }
    private boolean isStable(int r, int c, Board board) {
        int rows = board.getRows();
        int cols = board.getColumns();

        Piece p = board.getPiece(r, c);
        if (p == null) return false;

        // co nam o goc
        if ((r == 0 && c == 0) || (r == 0 && c == cols - 1) || (r == rows - 1 && c == 0) || (r == rows - 1 && c == cols - 1)) {
            return true;
        }

        //co nam o bien va khong the lat
        boolean onEdge = r == 0 || r == rows - 1 || c == 0 || c == cols - 1;
        if (onEdge) {
            boolean stableRow = (c > 0 && c < cols - 1) && board.getPiece(r, c - 1) == p && board.getPiece(r, c + 1) == p;
            boolean stableCol = (r > 0 && r < rows - 1) && board.getPiece(r - 1, c) == p && board.getPiece(r + 1, c) == p;
            return stableRow || stableCol;
        }

        return false;
    }

    private int heuristic(Board board) {
        Piece myPiece = this.getPiece();
        Piece oppPiece = myPiece.flip();

        int myScore = 0;
        int oppScore = 0;
        int myMobility = 0;
        int oppMobility = 0;
        int myEdgeControl = 0;
        int oppEdgeControl = 0;
        int myStablePieces = 0;
        int oppStablePieces = 0;
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
                // co nam o bien
                if ((r == 0 || r == board.getRows() - 1 || c == 0 || c == board.getColumns() - 1)) {
                    if (p == myPiece) {
                        myEdgeControl++;
                    } else if (p == oppPiece) {
                        oppEdgeControl++;
                    }
                }
                // co khong the lat
                if (isStable(r, c, board)) {
                    if (p == myPiece) {
                        myStablePieces++;
                    } else if (p == oppPiece) {
                        oppStablePieces++;
                    }
                }

            }

        }

        return (myScore - oppScore) + 10 * (myMobility - oppMobility)+ 15 * (myEdgeControl - oppEdgeControl) + 20 * (myStablePieces - oppStablePieces);
    }
}
