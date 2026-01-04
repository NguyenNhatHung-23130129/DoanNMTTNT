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

    // them enum de chon thuat toan
    public enum Algorithm {MINIMAX, ALPHABETA}

    private int defaultDepth = 4;
    private Algorithm algorithm = Algorithm.MINIMAX; // default

    public AIPlayer(String name, Piece piece) {
        super(name, piece);
    }

    public AIPlayer(String name, Piece piece, int depth) {
        super(name, piece);
        this.defaultDepth = depth;
    }


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
        System.out.println("Memory: " + memoryUsed + " KB ");
        System.out.println("--------------------------------------------------");

        if (bestRow != -1 && bestCol != -1) {
            return new int[]{bestRow, bestCol};
        }
        return null;
    }


    private int calculateStability(Board board, Piece p) {
        int stableCount = 0;
        int rows = board.getRows();
        int cols = board.getColumns();


        int[][] edges = {
                {0, 0, 0, 1},   //hang tren
                {rows - 1, 0, 0, 1}, //hang duoi
                {0, 0, 1, 0},  //cot trai
                {0, cols - 1, 1, 0} //cot phai
        };

        for (int[] edge : edges) {
            int rStart = edge[0];
            int cStart = edge[1];
            int dr = edge[2];
            int dc = edge[3];

            int length = (dr == 0) ? cols : rows;// do dai canh

            boolean startCornerOwned = (board.getPiece(rStart, cStart) == p);
            int forwardCount = 0;

            if (startCornerOwned) {
                for (int i = 0; i < length; i++) {
                    if (board.getPiece(rStart + i * dr, cStart + i * dc) == p) {
                        forwardCount++;
                    } else {
                        break;
                    }
                }
            }
            stableCount += forwardCount;
            // kiem tra tu duoi len
            int rEnd = rStart + (length - 1) * dr;
            int cEnd = cStart + (length - 1) * dc;
            boolean endCornerOwned = (board.getPiece(rEnd, cEnd) == p);

            if (endCornerOwned) {
                for (int i = 0; i < length; i++) {
                    int rCurr = rEnd - i * dr;
                    int cCurr = cEnd - i * dc;

                    if (board.getPiece(rCurr, cCurr) == p) {

                        if (forwardCount + i < length) {// tranh dem trung
                            stableCount++;
                        }
                    } else {
                        break;
                    }
                }
            }
        }

        return stableCount;
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

                // tinh diem bang WEIGHTS
                if (p == myPiece) {
                    myScore += WEIGHTS[r][c];
                } else if (p == oppPiece) {
                    oppScore += WEIGHTS[r][c];
                }

                // tinh Mobility (so nuoc di hop le)
                if (board.canPlacePiece(r, c, myPiece)) myMobility++;
                if (board.canPlacePiece(r, c, oppPiece)) oppMobility++;
            }
        }

        //  tinh diem Stable
        int myStable = calculateStability(board, myPiece);
        int oppStable = calculateStability(board, oppPiece);


        return 10 * (myScore - oppScore) + 20 * (myMobility - oppMobility) + 30 * (myStable - oppStable);
    }
}
