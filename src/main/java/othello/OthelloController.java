package othello;

public class OthelloController {
    private int[][] game; // 0 empty, 1 black, 2 white
    private OthelloView view;


    public OthelloController(OthelloView view) {
        this.view = view;
    }
    public void initilizeGame(int[] size){

    }
    public void handPlayerMove(int r, int c){

    }
    public boolean checkGameEnd() {
        return false;
    }

}
