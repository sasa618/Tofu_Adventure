package src.stages;

import javax.swing.*;
import java.awt.*;

import src.GameModel;
import src.Counter;
import src.Enemy;

//Stageの基本的な機能を持った抽象クラス。各ステージはこれを継承する。
public abstract class StageMasterPanel extends JPanel {
    protected GameModel model;
    protected int stageId;
    protected JPanel stage;
    protected int playerFirstLocation[] = {0, 0};
    protected Counter counter;
    protected Counter invTime; //無敵時間を測る
    protected int blockSize;
    protected int playerBlink; //playerの点滅状態を管理

    public StageMasterPanel(GameModel m, int stageId) {
        this.model = m;
        this.stageId = stageId;
        this.stage = new JPanel();
        this.counter = new Counter();
        this.invTime = new Counter();
        this.blockSize = Math.round(model.getZoom()*model.getBlockSize());
        this.playerBlink = 0;

        stage.setLayout(new GridLayout(model.getBlockNumbers(0), model.getBlockNumbers(1)));
        this.add(stage, BorderLayout.CENTER);

        //プレイヤーの初期表示位置の計算
        playerFirstLocation[0] = Math.round(model.getBlockSize() * model.getZoom() * (model.getPlayerFirstLoc(0)));
        playerFirstLocation[1] = Math.round(model.getBlockSize() * model.getZoom() * (model.getPlayerFirstLoc(1)+1) - model.getPlayerSize(1)*model.getZoom());
    }
    
    public void playerLocationInit() {
        model.setPlayerLocation(playerFirstLocation[0], playerFirstLocation[1]);
    }

    public void drawBackGround(Graphics g) { 
        g.drawImage(model.getCurrentBackgroundImage(), 0, 0, null);
    }
    public void drawLayer0(Graphics g) {
        int data;
        for(int j=0; j<model.getBlockNumbers(1); j++) {
            for(int i=0; i<model.getBlockNumbers(0); i++) {
                data = model.getCurrentStageDataLayer(i, j, 0);
                if(data == -1) continue;
                g.drawImage(model.getBlockTexture(data), i*blockSize, j*blockSize, blockSize, blockSize, null);
                //System.out.println("Created a block at ("+i*model.getBlockSize()+", "+j*model.getBlockSize()+")");
            }
        }
    }
    public void drawStage(Graphics g) {
        int data;
        for(int j=0; j<model.getBlockNumbers(1); j++) {
            for(int i=0; i<model.getBlockNumbers(0); i++) {
                data = model.getCurrentStageData(i, j);
                if(data == -1) continue;
                g.drawImage(model.getBlockTexture(data), i*blockSize, j*blockSize, blockSize, blockSize, null);
                //System.out.println("Created a block at ("+i*model.getBlockSize()+", "+j*model.getBlockSize()+")");
            }
        }
    }
    public void drawAllEnemies(Graphics g) {
        int enemyNum = model.getEnemyNum();
        for(int i=0;i<enemyNum;++i) {
            drawEnemy(g, i);
        }
    }
    public void drawEnemy(Graphics g, int i) {
        //System.out.println("enemy "+i+" was drawed!");
        Enemy enemy = model.getEnemy(i);
        int sizeX = enemy.getEnemySizeX();
        int sizeY = enemy.getEnemySizeY();
        g.drawImage(enemy.getEnemyImage(), enemy.getEnemyLocationX(), enemy.getEnemyLocationY(), sizeX, sizeY, null);
    }
    public void drawPlayer(Graphics g) {
        int sizeX = model.getPlayerUnitSize(0);
        int sizeY = model.getPlayerUnitSize(1);


        if(model.getIsPlayerInvincible()) invTime.start(800);
    
        if(invTime.check() || invTime.isCounting()) {
            if(playerBlink == 0) {
                counter.start(200);
                playerBlink = 1;
            }
            if(counter.check()) {
                if(playerBlink == 1) {
                    playerBlink = 2;
                } else {
                    playerBlink = 1;
                }
                counter.start(200);
            }
            if(playerBlink == 1)
                g.drawImage(model.getCurrentPlayerTexture(), model.getPlayerLocationX(), model.getPlayerLocationY(), sizeX, sizeY, null);
        } else {
            playerBlink = 0;
            g.drawImage(model.getCurrentPlayerTexture(), model.getPlayerLocationX(), model.getPlayerLocationY(), sizeX, sizeY, null);
        }
    }
    public void drawLayer2(Graphics g) {
        int data;
        for(int j=0; j<model.getBlockNumbers(1); j++) {
            for(int i=0; i<model.getBlockNumbers(0); i++) {
                data = model.getCurrentStageDataLayer(i, j, 2);
                if(data == -1) continue;
                g.drawImage(model.getBlockTexture(data), i*blockSize, j*blockSize, blockSize, blockSize, null);
                //System.out.println("Created a block at ("+i*model.getBlockSize()+", "+j*model.getBlockSize()+")");
            }
        }
    }
    public void drawHP(Graphics g) {
        int hpLocX = model.getWindowSize(0) - blockSize*2;
        for(int i=3; i>0; i--) {
            g.drawImage(model.getHeartTexture(i), hpLocX, 5, null);
            hpLocX -= blockSize + 3;
        }
        
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawStageAll(g);
    }

    //継承したクラスがそれぞれ定義する
    public abstract void drawStageAll(Graphics g);
}
