package othello;

public class HumanPlayer extends Player {
    public HumanPlayer(String name, Piece piece) {
        super(name, piece);
    }

    @Override
    public boolean makeMove(Board board, int column, int row) {
        return false;
    }
}
