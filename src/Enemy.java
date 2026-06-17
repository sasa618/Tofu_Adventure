package src;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Enemy {
    private final static float zoom = 1.5f; //Frameサイズ拡大用の定数(テクスチャ拡大用)
    private int fps = 30; //1秒間で処理するフレーム数

    protected boolean isFall; //地面の端で落下(true)or引き返し(false)
    protected int enemyLocation[]; //敵の座標(x,y)、左上端
    protected int enemySize[]; //敵のテクスチャのサイズ
    protected float enemyUnitSize[]; //敵の表示サイズ
    protected float enemySpeed; //敵の移動速度
    protected boolean isEnemyLanded; //着地しているか
    protected boolean enemyDirection; //向いている方向が左(true)or右(false)
    public int enemyDamage;

    protected File file;
    protected ArrayList<BufferedImage> enemyTexture; //敵のテクスチャ
    protected BufferedImage currentEnemyTexture;

    public Enemy(int type) {
        enemyLocation = new int[2];
        enemySize = new int[2];
        enemyUnitSize = new float[2];
        isEnemyLanded = false;
        enemyDirection = true;

        enemyTexture = new ArrayList<BufferedImage>();
        if(type == 1) {
            try {
                file = new File("textures/enemy/1_L.png");
                currentEnemyTexture = ImageIO.read(file);
                enemyTexture.add(currentEnemyTexture);
                
            } catch(IOException e) {
                System.out.println("textures/enemyに1_L.pngが見つかりません");
            }
            try {
                file = new File("textures/enemy/1_R.png");
                enemyTexture.add(ImageIO.read(file));
                System.out.println("enemy 1 was made!");
            } catch(IOException e) {
                System.out.println("textures/enemyに1_R.pngが見つかりません");
            }
            isFall = true;
            enemyDamage = 1;
            enemySpeed = 2;
            enemySize[0] = 16;
            enemySize[1] = 16;
            
        }
        enemyUnitSize[0] = enemySize[0]*zoom;
        enemyUnitSize[1] = enemySize[1]*zoom;
    }

    public boolean isBlockAir (StageData currentStageData, float x, float y) {
        int data = currentStageData.getStageDataL(x, y);
        if(data == -1 || data == 4)
            return true;
        return false;
    }

    public int getEnemySizeX() { return Math.round(enemySize[0]*zoom); }
    public int getEnemySizeY() { return Math.round(enemySize[1]*zoom); }
    public void setEnemyLocationX(int x) { enemyLocation[0] = x; } //敵の左側の座標を返す
    public void setEnemyLocationY(int y) { enemyLocation[1] = y; } //敵の上側の座標を返す
    public int getEnemyLocationX() { return enemyLocation[0]; } //敵の左側の座標を返す
    public int getEnemyLocationY() { return enemyLocation[1]; } //敵の上側の座標を返す
    public BufferedImage getEnemyImage() { return currentEnemyTexture; } //敵の画像を返す
    public int getEnemyDamage() { return enemyDamage; } //敵が与えるダメージ数を返す

    public void setCurrentEnemyTexture(int i) { currentEnemyTexture = enemyTexture.get(i); }

    public void moveEnemy (StageData curerntStageData) {
        float left = enemyLocation[0], right = enemyLocation[0] + enemyUnitSize[0];
        float top = enemyLocation[1], bottom = enemyLocation[1] + enemyUnitSize[1];
        //向きと着地の判定
        if(enemyDirection == true && !isBlockAir(curerntStageData, left, (top+bottom)/2))
            enemyDirection = false;
        if(enemyDirection == false && !isBlockAir(curerntStageData, right, (top+bottom)/2))
            enemyDirection = true;
        if(isBlockAir(curerntStageData, right, bottom) && isBlockAir(curerntStageData, left, bottom))
            isEnemyLanded = false;
        else
            isEnemyLanded = true;
        
        //崖際での挙動
        if(!isFall) {
            if(enemyDirection == true && isBlockAir(curerntStageData, left, bottom))
                enemyDirection = false;
            if(enemyDirection == false && isBlockAir(curerntStageData, right, bottom))
                enemyDirection = true;
        }

        if(enemyDirection == true) setCurrentEnemyTexture(0);
        else setCurrentEnemyTexture(1);

        float speed = enemySpeed;
        if(enemyDirection)
            speed *= -1;

        enemyLocation[0] += speed;
        if(!isEnemyLanded)
            enemyLocation[1] += 6;
    }
}