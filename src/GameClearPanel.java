package src;

import javax.swing.*;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.awt.event.*;

public class GameClearPanel extends JPanel implements KeyListener {
    protected GameModel model;
    protected GameFrame frame;
    protected JPanel p;
    protected JLabel gameClear;
    protected JLabel image;
    protected JLabel manual;
    protected KeyFunction toMainMenu;

    public GameClearPanel(GameModel m, GameFrame f) {
        this.model = m;
        this.frame = f;
        
        p = new JPanel();
        this.setSize(new Dimension(model.getWindowSize(0), model.getWindowSize(1)));
        this.setLayout(new BorderLayout());
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        //p.setBorder(new LineBorder(Color.BLUE, 2, false));

        int areaSize = (int)(model.getWindowSize(1) * 0.2f);
        gameClear = new JLabel("<html><span style='font-size:36px; color:white;'>GameClear</span></html>");
        gameClear.setHorizontalAlignment(JLabel.CENTER);
        //gameClear.setBorder(new LineBorder(Color.RED, 2, false));
        gameClear.setAlignmentX(Container.CENTER_ALIGNMENT);
        
        manual = new JLabel("<html><span style='color:white'>Esc/SPACE: メインメニューに戻る</span></html>");
        manual.setHorizontalAlignment(JLabel.CENTER);
        //manual.setBorder(new LineBorder(Color.RED, 2, false));
        manual.setAlignmentX(Container.CENTER_ALIGNMENT);

        

        toMainMenu = ()->{
            model.setIsMainMenuEnabled(true);
            model.setIsGameOver(false);
            model.setIsGameClear(false);
            model.setCurrentStageData(0);
        };

        p.setOpaque(false);
        this.setBackground(new Color(60, 60, 65));

        p.add(Box.createRigidArea(new Dimension(model.getWindowSize(0), areaSize)));
        p.add(gameClear);
        p.add(Box.createRigidArea(new Dimension(model.getWindowSize(0), areaSize)));
        p.add(manual);

        this.add(p, BorderLayout.CENTER);
    }

    public void focusThis() {
        addKeyListener(this);
        this.requestFocus();
    }

    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                toMainMenu.func();
                break;
            case KeyEvent.VK_ESCAPE:
                toMainMenu.func();
                break;
            default:
                break;
        }
    }
    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
}
