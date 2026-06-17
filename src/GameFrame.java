package src;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import src.stages.*;

//View----------------------------------------------------------------------------------------------
//各ステージの表示

//Frameの作成
public class GameFrame extends JFrame implements Runnable {
    protected GameModel model;
    protected MainMenuPanel mainMenu; //メインメニュー用
    protected TestStage testStage; //テスト用ステージ
    protected GameOverPanel gameOver; //ゲームオーバー画面
    protected GameClearPanel gameClear; //ゲームクリア画面 2/1
    protected StageType1 stage1;
    protected StageType2 stage2;
    private CardLayout frameLayout; //CardLayout

    protected String mainMenuName;
    protected String gameOverName;
    protected String gameClearName; // 2/1
    protected String stage1Name;
    protected String stage2Name;

    protected JLabel start;

    protected Thread thread;
    protected long sleepTime;

    protected int currentDisplay; //現在表示されるステージのid
    protected boolean isMainMenuEnabled;
    protected String stage;
    protected ArrayList<StageMasterPanel> stages;
    protected Counter timer;
    protected int imageId;

    public GameFrame(GameModel m, PlayerController c) {
        this.model = m;
        this.currentDisplay = -2;
        model.setIsMainMenuEnabled(true);

        timer = new Counter();
        imageId = 0;

        frameLayout = new CardLayout();
        this.setLayout(frameLayout);

        int width = model.getWindowSize(0);
        int height = model.getWindowSize(1);
        System.out.println("Window Size: ("+width+", "+height+")");
        this.setBounds(10,10,width+12, height+24);

        model.setCurrentStageData(0);

        stages = new ArrayList<StageMasterPanel>();
        stage1 = new StageType1(model, 1);
        stage1.playerLocationInit();
        stage2 = new StageType2(model, 2);
        //testStage.setBounds(0, 0, width, height);
        stages.add(stage1);
        stages.add(stage2);

        mainMenu = new MainMenuPanel(model, this);
        ///mainMenu.setBounds(0, 0, width, height);
        gameOver = new GameOverPanel(m, this);
        gameClear = new GameClearPanel(m, this); // 2/1

        model.loadEnemy(); //(2/1)

        mainMenuName = new String("menu");
        gameOverName = new String("gameOver");
        gameClearName = new String("gameClear"); // 2/1
        stage1Name = new String("stage1");
        stage2Name = new String("stage2");
        this.add(mainMenu, mainMenuName);
        this.add(gameOver, gameOverName);
        this.add(stage1, stage1Name);
        this.add(stage2, stage2Name);
        this.add(gameClear, gameClearName); // 2/1

        frameLayout.addLayoutComponent(mainMenu, mainMenuName);
        frameLayout.addLayoutComponent(gameOver, gameOverName);
        frameLayout.addLayoutComponent(stage1, stage1Name);
        frameLayout.addLayoutComponent(gameClear, gameClearName); // 2/1

        mainMenu.setFocusable(true);

        addKeyListener(c);

        sleepTime = 1000L / model.getFPS();

        thread = new Thread(this);
        thread.start();
    }

    public void gameStart(int num) {
        model.setIsMainMenuEnabled(false);
        switch(num) {
            case 1:
                model.setCurrentStageData(0);
                break;
            case 2:
                model.setCurrentStageData(4);
                break;
            default:
                model.setCurrentStageData(0);
                break;
        }
        model.setRightKey(false);
        model.setLeftKey(false);
        model.setDownKey(false);
        model.setUpKey(false);
        stage1.playerLocationInit();
        System.out.println("x = " + model.getPlayerLocationX() + ",y = " + model.getPlayerLocationY());
        this.requestFocus();
    }

    public void display() {
        if(model.getIsMainMenuEnabled()) {
            if(currentDisplay != -1) {
                frameLayout.show(this.getContentPane(), mainMenuName);
                currentDisplay = -1;
                //mainMenu.drawPlayer(0);
                mainMenu.focusThis();
                timer.start(500);
            }
            if(timer.check()) {
                if(imageId == 3) imageId = 0;
                else imageId++;
                //System.out.println("imageId: "+imageId);
                mainMenu.drawPlayer(imageId);
                timer.start(500);
            }
        } else if(model.getIsGameOver()) {
            if(currentDisplay != -2) {
                frameLayout.show(this.getContentPane(), gameOverName);
                stages.get(currentDisplay).playerLocationInit();
                currentDisplay = -2;
                gameOver.focusThis();
            }
        } else if(model.getIsGameClear()) { // 2/1
            if(currentDisplay != -2) {
                frameLayout.show(this.getContentPane(), gameClearName);
                stages.get(currentDisplay).playerLocationInit();
                currentDisplay = -2;
                gameClear.focusThis();
            }
        } else {
            if(currentDisplay == -1) {
                currentDisplay = model.getCurrentStageId();
                switch(currentDisplay) {
                    case 0:
                        stage = stage1Name;
                        break;
                    default:
                        stage = stage1Name;
                        break;
                }
                frameLayout.show(this.getContentPane(), stage);
            }
            stages.get(currentDisplay).repaint();
        }

    }

    public void run() {
        while(true) {
            model.move();

            model.damage(); //(1/27)
            model.moveEnemies(); //(2/1)
            //testStage.repaint();
            display();
            try {
                Thread.sleep(sleepTime); //fpsに合わせて処理を一時停止する
            } catch(InterruptedException e) {
                break;
            }
        }
    }
}
