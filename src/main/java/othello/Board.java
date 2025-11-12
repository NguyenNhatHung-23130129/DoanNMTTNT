package othello;

public class Board {
    private int rows;
    private int columns;
    private int[][] grid;

    public Board(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.grid = new int[rows][columns];
    }

}
