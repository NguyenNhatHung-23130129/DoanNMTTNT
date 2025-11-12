import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.List;
import java.util.Observer;

public class Game {
    private Board board;
    private Player currentPlayer;
    private int winPiece;
    private List<GameEndStrategy> winStrategies;
    private final StringProperty gameStatus = new SimpleStringProperty();
    private final ObjectProperty<GameState> state = new SimpleObjectProperty<>();

    public Game(Board board, Player startingPlayer, int winPiece,
                List<GameEndStrategy> winStrategies, List<Observer> observers) {
        this.board = board;
        this.currentPlayer = startingPlayer;
        this.winPiece = winPiece;
        this.winStrategies = winStrategies;
    }
    public StringProperty gameStatusProperty() {
        return gameStatus;
    }

    public void setGameStatus(String status) {
        gameStatus.set(status);
    }

    public String getGameStatus() {
        return gameStatus.get();
    }

    public ObjectProperty<GameState> stateProperty() {
        return state;
    }

    public void setState(GameState state) {
        this.state.set(state);
    }

    public GameState getState() {
        return state.get();
    }

}

