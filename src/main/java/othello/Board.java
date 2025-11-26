package othello;

public class Board {
    private int rows;
    private int columns;

    private Piece[][] board;


    public Board(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;

        this.board = new Piece[rows][columns];

    }
    public boolean canPlacePiece(int rowIndex, int colIndex, Piece piece) {
        if (board[rowIndex][colIndex] != null) return false; // Ô phải trống

        int[] dirRow = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dirCol = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int i = 0; i < 8; i++) {
            if (canCaptureAlongDirection(rowIndex, colIndex, dirRow[i], dirCol[i], piece)) {
                return true;
            }
        }
        return false;
    }

    //kiem tra xem co the an quan tren huong do khong
    private boolean canCaptureAlongDirection(int rowIndex, int colIndex, int dRow, int dCol, Piece piece) {
        Piece opponentPiece = piece.flip();
        int curRow = rowIndex + dRow;
        int curCol = colIndex + dCol;

        boolean foundOpponentInBetween = false;

        while (isInsideBoard(curRow, curCol)) {
            Piece currentCell = board[curRow][curCol];// lay quan o hien tai

            if (currentCell == null) return false; //dung o trong khong hop le

            if (currentCell == opponentPiece) {
                foundOpponentInBetween = true; // co quan dich o giua
            } else if (currentCell == piece) {
                return foundOpponentInBetween; // co the an neu co quan dich o giua
            }

            curRow += dRow;
            curCol += dCol;
        }
        return false;
    }

    private boolean isInsideBoard(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < columns;
    }

   
}