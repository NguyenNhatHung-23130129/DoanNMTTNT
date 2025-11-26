package othello;

public class Board {
    private int rows;
    private int columns;
    private Piece[][] grid;

    public Board(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.grid = new Piece[rows][columns];
    }

    public Piece getPieceAt(int r, int c) {
        return grid[r][c];
    }

    public void setPieceAt(int r, int c, Piece piece) {
        grid[r][c] = piece;
    }


   //kiểm tra lật cờ
    public void flipPieces(int row, int col, Piece piece) {
        // 8 hướng
        int[][] directions = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1},
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };

        Piece opponent = (piece == Piece.BLACK) ? Piece.WHITE : Piece.BLACK;

        // kiểm tra duyệt 8 hướng
        for (int[] dir : directions) {
            int dr = dir[0];
            int dc = dir[1];
            int r = row + dr;
            int c = col + dc;
            boolean hasOpponent = false;

            // Kiểm tra có quân ở giữa khác màu không
            while (isValid(r, c) && grid[r][c] == opponent) {
                hasOpponent = true;
                r += dr;
                c += dc;
            }

            // Nếu có quân khác màu ở giữa và kết thúc bằng quân cùng màu
            if (hasOpponent && isValid(r, c) && grid[r][c] == piece) {
                // lật quân
                r -= dr;
                c -= dc;
                while (r != row || c != col) {
                    grid[r][c] = piece;
                    r -= dr;
                    c -= dc;
                }
            }
        }
    }

    private boolean isValid(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < columns;
    }
}