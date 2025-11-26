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
}
