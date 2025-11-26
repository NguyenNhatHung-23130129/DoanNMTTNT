package othello;

public class PlayingState implements GameState {
    private GameEndStrategy gameEndStrategy;

    public PlayingState(GameEndStrategy gameEndStrategy) {
        this.gameEndStrategy = gameEndStrategy;
    }

    @Override
    //kiểm tra nước đi hợp lệ và thực hiện nước đi
    public boolean makeMove(Game game, int column, int row) {

        return false;
    }

    @Override
    public String getState() {
        return "Playing";
    }

}
