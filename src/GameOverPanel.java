package src;

import javax.swing.*;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.awt.event.*;

public class GameOverPanel extends JPanel implements KeyListener {
    protected GameModel model;
    protected GameFrame frame;
    protected JPanel p;
    protected JLabel gameOver;
    protected JLabel image;
    protected JLabel log;
    protected JLabel manual;
    protected KeyFunction toMainMenu;

    public GameOverPanel(GameModel m, GameFrame f) {
        this.model = m;
        this.frame = f;
        
        p = new JPanel();
        this.setSize(new Dimension(model.getWindowSize(0), model.getWindowSize(1)));
        this.setLayout(new BorderLayout());
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        //p.setBorder(new LineBorder(Color.BLUE, 2, false));

        int areaSize = (int)(model.getWindowSize(1) * 0.2f);
        gameOver = new JLabel("<html><span style='font-size:36px; color:white;'>GameOver</span></html>");
        gameOver.setHorizontalAlignment(JLabel.CENTER);
        //gameOver.setBorder(new LineBorder(Color.RED, 2, false));
        gameOver.setAlignmentX(Container.CENTER_ALIGNMENT);
        
        log = new JLabel("<html><span style='font-size:14px; color:white;'>とうふは力尽きてしまった・・・</span></html>");
        log.setHorizontalAlignment(JLabel.CENTER);
        //manual.setBorder(new LineBorder(Color.RED, 2, false));
        log.setAlignmentX(Container.CENTER_ALIGNMENT);

        manual = new JLabel("<html><span style='color:white'>Esc/SPACE: メインメニューに戻る</span></html>");
        manual.setHorizontalAlignment(JLabel.CENTER);
        //manual.setBorder(new LineBorder(Color.RED, 2, false));
        manual.setAlignmentX(Container.CENTER_ALIGNMENT);

        ImageIcon icon = new ImageIcon("textures/pictures/defeated.png");
        //画像サイズの調整---------------
        MediaTracker tracker = new MediaTracker(this);
        int iconX = (int)(icon.getIconWidth() * model.getZoom()*2);
        int iconY = (int)(icon.getIconHeight() * model.getZoom()*2);

        //System.out.println("("+buttonFrame.getIconWidth()+","+buttonFrame.getIconHeight()+")->("+iconX+","+iconY+")");

        Image resizedImage = icon.getImage().getScaledInstance(iconX, iconY, Image.SCALE_DEFAULT);

        tracker.addImage(resizedImage, 2);
        ImageIcon resizedIcon = new ImageIcon(resizedImage);

        try {
            tracker.waitForAll();
        } catch(InterruptedException e) {
            System.out.println(e);
        }
        //--------------------------------
        image = new JLabel(resizedIcon);
        image.setAlignmentX(Container.CENTER_ALIGNMENT);

        toMainMenu = ()->{
            model.setIsMainMenuEnabled(true);
            model.setIsGameOver(false);
        };

        p.setOpaque(false);
        this.setBackground(new Color(60, 60, 65));

        p.add(Box.createRigidArea(new Dimension(model.getWindowSize(0), areaSize)));
        p.add(gameOver);
        p.add(Box.createRigidArea(new Dimension(model.getWindowSize(0), areaSize/2)));
        p.add(image);
        p.add(log);
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
