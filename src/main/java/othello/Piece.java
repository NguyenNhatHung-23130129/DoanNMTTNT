package othello;
import javafx.scene.paint.Color;

public enum Piece {
    BLACK(Color.BLACK),
    WHITE(Color.WHITESMOKE);

    private final Color color;

    Piece(Color color) {
        this.color = color;
    }
    public Piece flip() {
        return this == BLACK ? WHITE : BLACK;
    }
    public Color getColor() {
        return color;
    }
}
