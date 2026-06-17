package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

//メインメニュー画面の表示
//このクラスはControllerも兼ねている
public class MainMenuPanel extends JPanel implements KeyListener {
    protected GameModel model;
    protected GameFrame mainFrame;
    protected JLabel title; //タイトルグラフィック
    protected JLabel start; //Startボタン
    protected JLabel exit; //Exitボタン
    protected JLabel arrow1; //選択中の矢印
    protected JLabel arrow2;

    protected JLabel stage1; //stage1ボタン
    protected JLabel stage2; //stage2ボタン
    protected JLabel arrowS1;
    protected JLabel arrowS2;
    protected JPanel p; //メニュー用パネル
    protected Counter counter;

    protected ArrayList<KeyFunction> functions; //キー入力時の処理
    protected int cursorLoc; //カーソル位置
    protected int maxCursorLoc; //カーソル位置の最大値
    protected int maxCursorLocS; //ステージ選択時のカーソル位置の最大値
    protected ArrayList<Image> playerImage; //メインメニュー用プレイヤー画像(拡大済み)
    protected JPanel playerPreview; //プレイヤー表示用パネル
    protected Color titleColor; //メインメニューの背景色
    protected CardLayout layout;
    protected boolean stageSelect; //ステージ選択中か

    public MainMenuPanel(GameModel m, GameFrame f) {
        this.model = m;
        this.mainFrame = f;

        this.setLayout(null);

        JPanel menu = new JPanel();
        JPanel stageS = new JPanel();
        p = new JPanel();

        counter = new Counter();

        menu.setLayout(new BoxLayout(menu, BoxLayout.PAGE_AXIS));
        stageS.setLayout(new BoxLayout(stageS, BoxLayout.PAGE_AXIS));
        float sizeX = model.getWindowSize(0);
        float sizeY = model.getWindowSize(1);
        int areaSize = Math.round(sizeY*0.2f);

        //キー入力時の処理を定義
        this.cursorLoc = 0;
        this.maxCursorLoc = 1;
        this.maxCursorLocS = 1;
        functions = new ArrayList<KeyFunction>();
        //上入力時の処理
        functions.add(() -> {
            if(cursorLoc > 0) {
                cursorLoc -= 1;
                //System.out.println("cLoc: "+cursorLoc);
                arrowMove(cursorLoc, -1);
            }
        });
        //下入力時の処理
        functions.add(() -> {
            if((!stageSelect&&cursorLoc<maxCursorLoc) || (stageSelect&&cursorLoc<maxCursorLocS)) {
                cursorLoc += 1;
                //System.out.println("cLoc: "+cursorLoc);
                arrowMove(cursorLoc, 1);
            }
        });
        //決定入力時の処理
        functions.add(() -> {
            if(stageSelect) {
                if(!counter.check()) return; 
                switch(this.cursorLoc) {
                    case 0:
                        layout.show(p, "menu");
                        stageSelect = false;
                        mainFrame.gameStart(1);
                        break;
                    case 1:
                        //mainFrame.gameStart(2);
                        break;
                    default:
                        break;
                }
            } else {
                switch(this.cursorLoc) {
                    case 0:
                        layout.show(p, "ss");
                        stageSelect = true;
                        counter.start(200);
                        break;
                    case 1:
                        System.exit(0);
                        break;
                    default:
                        break;
                }
            }
        });
        //キャンセル入力時の処理
        functions.add(() -> {
            if(stageSelect) {
                cursorLoc = 0;
                layout.show(p, "menu");
                stageSelect = false;
            }
        });

        ImageIcon buttonFrame = new ImageIcon("textures/gui/ButtonFrame.png");
        ImageIcon arrowIcon = new ImageIcon("textures/gui/cursor.png");
        ImageIcon titleIcon = new ImageIcon("textures/pictures/TitleGraphic.png");

        int xLocation = Math.round(sizeX*0.7f);
        titleColor = new Color(60,60,65);
        //
        playerPreview = new JPanel();
        int playerSizeX = (int)(model.getPlayerSize(0)*model.getZoom()*2);
        int playerSizeY = (int)(model.getPlayerSize(1)*model.getZoom()*2);
        playerImage = new ArrayList<Image>();
        playerImage.add(model.getPlayerTexture(1).getScaledInstance(playerSizeX, playerSizeY, Image.SCALE_FAST));
        playerImage.add(model.getPlayerTexture(2).getScaledInstance(playerSizeX, playerSizeY, Image.SCALE_FAST));
        playerImage.add(model.getPlayerTexture(3).getScaledInstance(playerSizeX, playerSizeY, Image.SCALE_FAST));
        playerImage.add(model.getPlayerTexture(4).getScaledInstance(playerSizeX, playerSizeY, Image.SCALE_FAST));

        //

        //ボタンのサイズの調整---------------
        MediaTracker tracker = new MediaTracker(this);
        int iconX = (int)(buttonFrame.getIconWidth() * model.getZoom());
        int iconY = (int)(buttonFrame.getIconHeight() * model.getZoom());

        //System.out.println("("+buttonFrame.getIconWidth()+","+buttonFrame.getIconHeight()+")->("+iconX+","+iconY+")");

        Image resizedImage = buttonFrame.getImage().getScaledInstance(iconX, iconY, Image.SCALE_DEFAULT);

        tracker.addImage(resizedImage, 2);
        ImageIcon resizedButtonFrame = new ImageIcon(resizedImage);

        try {
            tracker.waitForAll();
        } catch(InterruptedException e) {
            System.out.println(e);
        }
        //--------------------------------
        //矢印のサイズの調整---------------
        tracker = new MediaTracker(this);
        int arrowIconX = (int)(arrowIcon.getIconWidth() * model.getZoom());
        int arrowIconY = (int)(arrowIcon.getIconHeight() * model.getZoom());

        //System.out.println("("+buttonFrame.getIconWidth()+","+buttonFrame.getIconHeight()+")->("+iconX+","+iconY+")");

        Image resizedArrowImage = arrowIcon.getImage().getScaledInstance(arrowIconX, arrowIconY, Image.SCALE_DEFAULT);

        tracker.addImage(resizedArrowImage, 2);
        ImageIcon resizedArrowIcon = new ImageIcon(resizedArrowImage);

        try {
            tracker.waitForAll();
        } catch(InterruptedException e) {
            System.out.println(e);
        }
        //--------------------------------
        //矢印のサイズの調整---------------
        tracker = new MediaTracker(this);
        int titleIconX = (int)(titleIcon.getIconWidth() * model.getZoom() * 2);
        int titleIconY = (int)(titleIcon.getIconHeight() * model.getZoom() * 2);

        //System.out.println("("+buttonFrame.getIconWidth()+","+buttonFrame.getIconHeight()+")->("+iconX+","+iconY+")");

        Image resizedTitleImage = titleIcon.getImage().getScaledInstance(titleIconX, titleIconY, Image.SCALE_DEFAULT);

        tracker.addImage(resizedTitleImage, 2);
        ImageIcon resizedTitleIcon = new ImageIcon(resizedTitleImage);

        try {
            tracker.waitForAll();
        } catch(InterruptedException e) {
            System.out.println(e);
        }
        //--------------------------------
        
        int buttonLoc = (int)((sizeX - xLocation - iconX)/2);

        //ボタンと矢印の設定
        arrow1 = new JLabel(resizedArrowIcon, JLabel.CENTER);
        arrow2 = new JLabel(resizedArrowIcon, JLabel.CENTER);
        arrow2.setVisible(false);
        arrowS1 = new JLabel(resizedArrowIcon, JLabel.CENTER);
        arrowS2 = new JLabel(resizedArrowIcon, JLabel.CENTER);
        arrowS2.setVisible(false);

        title = new JLabel(resizedTitleIcon);
        title.setBounds((xLocation-titleIconX)/2, 0, titleIconX, titleIconY);

        playerPreview.setBackground(titleColor);
        playerPreview.setBounds((xLocation-playerSizeX)/2, titleIconY+10, playerSizeX, playerSizeY);
        
        start = new JLabel("<html><span style='font-size:12px;'>START</span></html>", resizedButtonFrame, JLabel.CENTER);
        start.setHorizontalTextPosition(JLabel.CENTER);
        
        exit = new JLabel("<html><span style='font-size:12px;'>EXIT</span></html>", resizedButtonFrame, JLabel.CENTER);
        exit.setHorizontalTextPosition(JLabel.CENTER);

        stage1 = new JLabel("<html><span style='font-size:12px;'>STAGE 1</span></html>", resizedButtonFrame, JLabel.CENTER);
        stage1.setHorizontalTextPosition(JLabel.CENTER);
        stage2 = new JLabel("<html><span style='font-size:12px;'>STAGE 2</span></html>", resizedButtonFrame, JLabel.CENTER);
        stage2.setHorizontalTextPosition(JLabel.CENTER);

        JPanel buttonPanel1 = new JPanel(), buttonPanel2 = new JPanel();
        JPanel buttonPanelS1 = new JPanel(), buttonPanelS2 = new JPanel();
        SpringLayout buttonLayout1 = new SpringLayout(), buttonLayout2 = new SpringLayout();
        SpringLayout buttonLayoutS1 = new SpringLayout(), buttonLayoutS2 = new SpringLayout();
        buttonPanel1.setLayout(buttonLayout1);
        buttonPanel2.setLayout(buttonLayout2);
        buttonPanelS1.setLayout(buttonLayoutS1);
        buttonPanelS2.setLayout(buttonLayoutS2);

        buttonLayout1.putConstraint(SpringLayout.NORTH, start, 0, SpringLayout.NORTH, buttonPanel1);
        buttonLayout1.putConstraint(SpringLayout.WEST, start, buttonLoc, SpringLayout.WEST, buttonPanel1);

        buttonLayout1.putConstraint(SpringLayout.NORTH, arrow1, 0, SpringLayout.NORTH, buttonPanel1);
        buttonLayout1.putConstraint(SpringLayout.EAST, arrow1, -15, SpringLayout.WEST, start);
        
        buttonLayout2.putConstraint(SpringLayout.NORTH, exit, 0, SpringLayout.NORTH, buttonPanel2);
        buttonLayout2.putConstraint(SpringLayout.WEST, exit, buttonLoc, SpringLayout.WEST, buttonPanel2);
        
        buttonLayout2.putConstraint(SpringLayout.NORTH, arrow2, 0, SpringLayout.NORTH, buttonPanel2);
        buttonLayout2.putConstraint(SpringLayout.EAST, arrow2, -15, SpringLayout.WEST, exit);
        
        buttonLayoutS1.putConstraint(SpringLayout.NORTH, stage1, 0, SpringLayout.NORTH, buttonPanelS1);
        buttonLayoutS1.putConstraint(SpringLayout.WEST, stage1, buttonLoc, SpringLayout.WEST, buttonPanelS1);

        buttonLayoutS1.putConstraint(SpringLayout.NORTH, arrowS1, 0, SpringLayout.NORTH, buttonPanelS1);
        buttonLayoutS1.putConstraint(SpringLayout.EAST, arrowS1, -15, SpringLayout.WEST, stage1);

        buttonLayoutS2.putConstraint(SpringLayout.NORTH, stage2, 0, SpringLayout.NORTH, buttonPanelS2);
        buttonLayoutS2.putConstraint(SpringLayout.WEST, stage2, buttonLoc, SpringLayout.WEST, buttonPanelS2);

        buttonLayoutS2.putConstraint(SpringLayout.NORTH, arrowS2, 0, SpringLayout.NORTH, buttonPanelS2);
        buttonLayoutS2.putConstraint(SpringLayout.EAST, arrowS2, -15, SpringLayout.WEST, stage2);

        buttonPanel1.add(arrow1); buttonPanel1.add(start);
        buttonPanel2.add(arrow2); buttonPanel2.add(exit);
        buttonPanelS1.add(arrowS1); buttonPanelS1.add(stage1);
        buttonPanelS2.add(arrowS2); buttonPanelS2.add(stage2);
        
        //タイトルやボタンを配置
        /*
        p.add(Box.createRigidArea(new Dimension(areaSize,areaSize)));
        p.add(title);
        */
        menu.add(Box.createRigidArea(new Dimension(areaSize,areaSize)));
        menu.add(buttonPanel1);
        menu.add(buttonPanel2);

        stageS.add(Box.createRigidArea(new Dimension(areaSize, areaSize)));
        stageS.add(buttonPanelS1);
        stageS.add(buttonPanelS2);

        //色の設定
        Color menuColor = new Color(100,100,110);
        buttonPanel1.setBackground(menuColor);
        buttonPanel2.setBackground(menuColor);
        buttonPanelS1.setBackground(menuColor);
        buttonPanelS2.setBackground(menuColor);
        menu.setBackground(menuColor);
        stageS.setBackground(menuColor);
        p.setBackground(menuColor);
        this.setBackground(titleColor);
        menu.setBounds(xLocation, 0, Math.round(sizeX)-xLocation+1, Math.round(sizeY));
        stageS.setBounds(xLocation, 0, Math.round(sizeX)-xLocation+1, Math.round(sizeY));

        layout = new CardLayout();
        p.setLayout(layout);
        p.setBounds(xLocation, 0, Math.round(sizeX)-xLocation+1, Math.round(sizeY));
        p.add(menu, "menu");
        p.add(stageS, "ss");
        
        this.add(p);
        this.add(title);
        this.add(playerPreview);

        addKeyListener(this);
    }

    public void arrowMove(int loc, int dir) {
        if(stageSelect) {
            switch(loc) {
                case 0:
                    arrowS2.setVisible(false);
                    arrowS1.setVisible(true);
                    break;
                case 1:
                    arrowS1.setVisible(false);
                    arrowS2.setVisible(true);
                    break;
                default:
                    break;
            }
        } else {
            switch(loc) {
                case 0:
                    arrow2.setVisible(false);
                    arrow1.setVisible(true);
                    break;
                case 1:
                    arrow1.setVisible(false);
                    arrow2.setVisible(true);
                    break;
                default:
                    break;
            }
        }
    }

    //プレイヤーを描画
    public void drawPlayer(int id) {
        //this.repaint();
        playerPreview.getGraphics().drawImage(playerImage.get(id), 0, 0, titleColor,null);
    }

    //フォーカスを取得
    public void focusThis() {
        addKeyListener(this);
        this.requestFocus();
    }


    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_UP:
                functions.get(0).func();
                break;
            case KeyEvent.VK_DOWN:
                functions.get(1).func();
                break;
            case KeyEvent.VK_SPACE:
                functions.get(2).func();
                break;
            case KeyEvent.VK_ESCAPE:
                functions.get(3).func();
                break;
            default:
                break;
        }
    }
    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}

}