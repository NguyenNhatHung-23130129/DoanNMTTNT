public abstract class Player {
    private String name;
    private int piece;

    public Player(String name, int piece) {
        this.name = name;
        this.piece = piece;
    }

    public String getName() {
        return name;
    }

    public int getPiece() {
        return piece;
    }
    public abstract boolean makeMove(Board board, int column, int row);
}
