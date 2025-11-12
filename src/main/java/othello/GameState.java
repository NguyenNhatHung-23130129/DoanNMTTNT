package othello;

public interface GameState {
    public boolean makeMove(Game game, int column, int row);
    public String getState();
}
