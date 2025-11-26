package othello;

public class AIPlayer extends Player {
    public AIPlayer(String name, Piece piece) {
        super(name, piece);
    }

    @Override
    public boolean makeMove(Board board, int column, int row) {
        return false;
    }
}
