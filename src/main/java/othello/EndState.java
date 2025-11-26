package othello;

public class EndState implements GameState {
    @Override
    public boolean makeMove(Game game, int column, int row) {
        return false;
    }

    @Override
    public String getState() {
        return "d.Game Over";
    }
}
