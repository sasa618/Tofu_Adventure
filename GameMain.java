import javax.swing.*;
import src.*;

//Main Class-----------------------------------------------------------------------------------------
class GameMain {
    public static void main(String argv[]) {
        GameModel model = new GameModel();
        PlayerController c = new PlayerController(model);
        GameFrame frame = new GameFrame(model, c);
        

        frame.setTitle("2D Action Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
