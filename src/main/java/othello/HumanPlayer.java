package othello;

public class HumanPlayer extends Player {
    public HumanPlayer(String name, Piece piece) {
        super(name, piece);
    }

    @Override
    public boolean makeMove(Board board, int row, int col) {
        if (board.canPlacePiece(row, col, this.getPiece())) {
            board.placePiece(row, col, this.getPiece());
            return true;
        }
        return false;
    }
}
