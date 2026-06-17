package src;

import javax.imageio.ImageIO;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

//Model-----------------------------------------------------------------------------------------------
public final class GameModel {
    private final static int blockNumbers[] = {/*横*/30, /*縦*/20}; //横と縦のブロックの数。
    private final static float zoom = 2.0f; //Frameサイズ拡大用の定数(テクスチャ拡大用)
    private final static int blockSize = 16; //ブロックテクスチャのサイズ
    private final static int playerSize[] = {16, 16}; //プレイヤーテクスチャのサイズ

    private int fps = 30; //1秒間で処理するフレーム数
    private int delayTime;

    protected int windowSize[]; //ウィンドウのサイズ

    private int maxNumbersOfBlockTextures = 128; //読み込むブロックテクスチャの最大数
    protected ArrayList<BufferedImage> blockTextures; //ブロックテクスチャを保持するArrayList
    protected int numbersOfBlockTextures = 128; //有効なブロックテクスチャの数

    protected Map<Integer, BufferedImage> playerTextures; //プレイヤーのテクスチャ、アニメーションも含む
    protected BufferedImage currentPlayerTexture; //現在のプレイヤーテクスチャ
    protected Image heart0; //黒ハートのテクスチャ
    protected Image heart1; //赤ハートのテクスチャ
    protected Counter timer;
    protected int prevPlayerTexKey; //一つ前に表示されていたプレイヤーテクスチャのkey
    protected int playerMovingState; //プレイヤーの移動状態
    protected int playerDirection; //プレイヤーの向いている方向(1:右, -1:左)
    protected int playerLocation[]; //playerの座標(x,y)、プレイヤーテクスチャの左上の座標
    protected int playerState; //プレイヤーの状態
    protected int playerHP; //プレイヤーの体力
    protected boolean isPlayerLanded; //プレイヤーが地面にいるか

    //1/27
    protected boolean isPlayerinvincible; //プレイヤーが無敵状態か
    protected Counter inv_timer; //無敵時間のカウント用

    protected boolean rightKey; //右キー(右矢印、Dキー)が押されているか
    protected boolean leftKey; //左キーが押されているか
    protected boolean upKey; //上キー(Space含む)が押されているか
    protected boolean downKey; //下キーが押されているか
    protected boolean shiftKey;

    protected boolean isMainMenuEnabled; //メインメニューが有効か
    protected boolean isGameOver; //ゲームオーバーか
    protected boolean isGameClear; //ゲームクリアしたか 2/1

    protected Thread thread;
    protected float velocityX; //プレイヤーのX軸方向の現在速度
    protected float velocityY; //プレイヤーのY軸方向の現在速度
    protected float maxVelocityX; //プレイヤーのX軸方向の最大速度
    protected float maxVelocityY; //プレイヤーのY軸方向の最大速度
    protected int maxX; //移動可能な最大のx座標
    protected float playerUnitSize[]; //プレイヤーの表示サイズ
    protected long sleepTime;

    protected ArrayList<StageData> stageData; //元のステージデータのArrayList
    protected ArrayList<StageData> modifiedStageData; //改変後のステージデータ
    protected StageData currentStageData = null; //現在表示するステージデータ
    protected ArrayList<Image> backgrounds; //背景用イメージリスト(1/19)
    protected ArrayList<Integer> animationCounter;  //Blockのアニメーション用idカウンタ 2/1
    protected ArrayList<Counter> animationTimer;  //Blockのアニメーション用のTimer 2/1
    protected ArrayList<Boolean> animationFlag;  // 2/1

    //2/1
    protected ArrayList<Enemy> enemies; //敵を管理する配列
    protected int enemyNum; //敵の数

    //このコンストラクタでは、バッファを使用して画像やテキストデータを読み込みと、数値の初期化を行う
    public GameModel() {
        //animation用のArrayListの初期化 2/1
        animationCounter = new ArrayList<Integer>(); // 2/1
        animationTimer = new ArrayList<Counter>(); // 2/1
        animationFlag = new ArrayList<Boolean>(); // 2/1

        //ブロックテクスチャを読み込む
        blockTextures = new ArrayList<BufferedImage>();
        File file;
        StringBuilder url = new StringBuilder(23);
        try {
            for(int i=0; i<maxNumbersOfBlockTextures; i++) {
                url.append("textures/blocks/");
                url.append(i);
                url.append(".png");
                file = new File(url.toString());
                animationCounter.add(i); // 2/1
                animationFlag.add(false); // 2/1
                animationTimer.add(new Counter()); //2/1
                if(!file.exists()) {
                    numbersOfBlockTextures = i;
                    break;
                }
                url.delete(0, 23+(int)Math.max(1, Math.log10((double)i))); // 2/1
                blockTextures.add(ImageIO.read(file));
                try {
                    if(blockTextures.get(i).getTileHeight() != blockSize || blockTextures.get(i).getTileWidth() != blockSize)
                        throw new IllegalArgumentException();
                } catch(IllegalArgumentException e) {
                    System.out.println("ID"+i+"のブロックテクスチャのサイズが"+blockSize+"x"+blockSize+"ではありません");
                }
            }
        } catch(IOException e) {
            System.out.println(e);
        }
        System.out.println(numbersOfBlockTextures+" block textures are loaded!");
        
        //playerのテクスチャを読み込む
        playerTextures = new HashMap<>();
        try {
            file = new File("textures/player/player_wait.png");
            playerTextures.put(0, ImageIO.read(file));
        } catch(IOException e) {
            System.out.println("player_wait.pngが見つかりません");
        }
        try {
            file = new File("textures/player/player_walkL.png");
            playerTextures.put(-1, ImageIO.read(file));
            playerTextures.put(-3, ImageIO.read(file));
        } catch(IOException e) {
            System.out.println("player_walkL.pngが見つかりません");
        }
        //(1/18)---
        try {
            file = new File("textures/player/player_walkL1.png");
            playerTextures.put(-2, ImageIO.read(file));
        } catch(IOException e) {
            System.out.println("player_walkL1.pngが見つかりません");
        }
        try {
            file = new File("textures/player/player_walkL2.png");
            playerTextures.put(-4, ImageIO.read(file));
        } catch(IOException e) {
            System.out.println("player_walkL2.pngが見つかりません");
        }
        //---
        try {
            file = new File("textures/player/player_walkR.png");
            playerTextures.put(1, ImageIO.read(file));
            playerTextures.put(3, ImageIO.read(file));
        } catch(IOException e) {
            System.out.println("player_walkR.pngが見つかりません");
        }
        //(1/18)---
        try {
            file = new File("textures/player/player_walkR1.png");
            playerTextures.put(2, ImageIO.read(file));
        } catch(IOException e) {
            System.out.println("player_walkR1.pngが見つかりません");
        }
        try {
            file = new File("textures/player/player_walkR2.png");
            playerTextures.put(4, ImageIO.read(file));
        } catch(IOException e) {
            System.out.println("player_walkR2.pngが見つかりません");
        }
        //---
        try {
            file = new File("textures/player/player_jumpR.png");
            playerTextures.put(10, ImageIO.read(file));
        } catch(IOException e) {
            System.out.println("player_jumpR.pngが見つかりません");
        }
        try {
            file = new File("textures/player/player_jumpL.png");
            playerTextures.put(-10, ImageIO.read(file));
        } catch(IOException e) {
            System.out.println("player_jumpL.pngが見つかりません");
        }
        try {
            file = new File("textures/player/player_fallR.png");
            playerTextures.put(11, ImageIO.read(file));
        } catch(IOException e) {
            System.out.println("player_fallR.pngが見つかりません");
        }
        try {
            file = new File("textures/player/player_fallL.png");
            playerTextures.put(-11, ImageIO.read(file));
        } catch(IOException e) {
            System.out.println("player_fallL.pngが見つかりません");
        }
        
        //-----1/30
        int newSize = Math.round(zoom*blockSize);
        try {
            file = new File("textures/gui/heart0.png");
            heart0 = ImageIO.read(file).getScaledInstance(newSize, newSize, Image.SCALE_DEFAULT);
        } catch(IOException e) {
            System.out.println("heart0.pngが見つかりません");
        }
        try {
            file = new File("textures/gui/heart1.png");
            heart1 = ImageIO.read(file).getScaledInstance(newSize, newSize, Image.SCALE_DEFAULT);
        } catch(IOException e) {
            System.out.println("heart1.pngが見つかりません");
        }

        currentPlayerTexture = playerTextures.get(0);
        
        //Timerの初期化
        timer = new Counter();

        //画面サイズの計算
        windowSize = new int[2];
        float unitSize = zoom*blockSize;
        windowSize[0] = Math.round(blockNumbers[0]*unitSize);
        windowSize[1] = Math.round(blockNumbers[1]*unitSize);

        //背景の読み込み(1/19)
        backgrounds = new ArrayList<Image>();
        try {
            file = new File("textures/background/bluesky.png");
            backgrounds.add(ImageIO.read(file).getScaledInstance(windowSize[0], windowSize[1], Image.SCALE_FAST));
        } catch(IOException e) {
            System.out.println("bluesky.pngが見つかりません");
        }

        //ステージデータの読み込み
        stageData = new ArrayList<StageData>();
        stageData.add(new StageData("stage1", 0, blockNumbers, unitSize, 0)); //(1/19)
        stageData.add(new StageData("stage2", 1, blockNumbers, unitSize, 0));
        stageData.add(new StageData("stage3", 2, blockNumbers, unitSize, 0));
        stageData.add(new StageData("test20x30", 3, blockNumbers, unitSize, 0));

        isMainMenuEnabled = true; //(1/15)
        isGameOver = false; //(2/1)
        isGameClear = false; // 2/1

        //playerに関する初期化
        playerLocation = new int[2];
        playerState = 1;

        //fpsに関する処理
        delayTime = 1000 / fps;

        //速度に関する変数の初期化
        sleepTime = 1000L / fps;
        velocityX = 0.0f;
        velocityY = 0.0f;
        maxVelocityX = 8.0f;
        maxVelocityY = 18.0f;

        maxX = (int)(windowSize[0]-playerSize[0]*zoom);
        playerUnitSize = new float[2];
        playerUnitSize[0] = playerSize[0]*zoom;
        playerUnitSize[1] = playerSize[1]*zoom;

        //1/27
        playerHP = 3;
        isPlayerinvincible = false;
        inv_timer = new Counter();

        //2/1
        enemies = new ArrayList<Enemy>();
        enemyNum = 0;  
    }
    //(コンストラクタ終了)-----------------------------------------------

    //blockNumbers[i]を返す。引数iは0か1
    public int getBlockNumbers(int i) {
        if(i!=0 && i!=1) return -1;
        return blockNumbers[i]; 
    }
    public float getZoom() { return zoom; }
    public int getBlockSize() { return blockSize; }
    public int getPlayerSize(int i) {
        if(i!=0 && i!=1) return -1;
        return playerSize[i];
    }

    public int getWindowSize(int i) {
        if(i!=0 && i!=1) return -1;
        return windowSize[i];
    } 

    //fpsを更新
    public int getDelayTime() { return delayTime; }
    public void setFPS(int f) {
        if(f >= 10 && f <= 60) {
            this.fps = f;
            this.delayTime = 1000 / f;
        }
    }
    public int getFPS() { return fps; }

    //BlockAnimaiton用の関数 2/1
    public void AnimationUpdateId(int id, int init, int frames, int waitTime) {
        if(!animationFlag.get(id)) {
            animationTimer.get(id).start(waitTime);
            animationFlag.set(id, true);
        }
        if(animationTimer.get(id).check()){
            if(animationCounter.get(id) == init + frames-1) animationCounter.set(id, init);
            else if(animationCounter.get(id) >= init && animationCounter.get(id) < id + frames-1) animationCounter.set(id, animationCounter.get(id) + 1);
            animationTimer.get(id).start(waitTime);
        }
    }

    // 2/1
    public BufferedImage getBlockTexture(int id) {
        if(id>= 9 && id <= 11) AnimationUpdateId(id, 9, 3, 200); // 9.png - 11.png の3Frameのアニメーション 200ミリ秒で更新 2/1
        else if(id >= 12 && id <= 13) AnimationUpdateId(id, 12, 2, 300); // 2/1 butterfly
        else if(id >= 14 && id <= 17) AnimationUpdateId(id, 14, 4, 100); // 2/1 fire
        return blockTextures.get(animationCounter.get(id)); // 2/1
    }


    //
    public boolean checkPlayerLocation(int x, int y, int layer, int block_id) {
        if(currentStageData.getStageDataLayerL(x+(playerUnitSize[0]/2), y+(playerUnitSize[1]/2), layer)==block_id) {
            return true;
        } else { return false; }
    }

    //player座標の設定と参照
    public int getPlayerLocationX() { return playerLocation[0]; } //プレイヤーの左端のX座標を返す
    public int getPlayerLocationY() { return playerLocation[1]; } //プレイヤーの上のY座標を返す
    public int getPlayerLocationRightX() { return playerLocation[0]+(int)playerUnitSize[0]; } //プレイヤーの右端のX座標を返す
    public int getPlayerLocationBottomY() { return playerLocation[1]+(int)playerUnitSize[1]; } //プレイヤーの下のY座標を返す
    public void setPlayerLocation(int x, int y) {
    	int data0, data1;
    	data0 = currentStageData.getStageDataL(x+(playerUnitSize[0]), y+(playerUnitSize[1]));
    	data1 = currentStageData.getStageDataL(x, y+(playerUnitSize[1]));
    	
        if(x>=0 && x<=maxX) { //X軸がウィンドウに収まるときのみ更新
            if(data1 == -1 || data1 == 4) {
                if(data0 == -1 || data0 == 4) {
                    /*
                     * デバッグ用
                    System.out.println("nx1:"+nx1+" nx2:"+nx2+" ny:"+ny);
                    System.out.println("p: ("+playerLocation[0]+", "+playerLocation[1]+")");
                    System.out.println("false");
                    */
                    this.playerLocation[0] = x;
                    this.playerLocation[1] = y;
                    isPlayerLanded = false;
                } else { //地面にいるとき
                    this.playerLocation[0] = x;
                    if(y < 0) this.playerLocation[1] = y; //落下をしないようにする
                    isPlayerLanded = true;
                }
            } else { //地面にいるとき
                this.playerLocation[0] = x;
                if(y < 0) this.playerLocation[1] = y; //落下をしないようにする
                isPlayerLanded = true;
            }
        }
        if(isPlayerLanded) {
            while(true) {
            	data0 = currentStageData.getStageDataL(playerLocation[0], playerLocation[1]+playerUnitSize[1]);
            	data1 = currentStageData.getStageDataL(playerLocation[0]+playerUnitSize[0], playerLocation[1]+playerUnitSize[1]);
                if(data0 != -1 && data1 != 4) break;
                if(data1 != -1 && data1 != 4) break;
                this.playerLocation[1] += 1;
            }
        }
    }
    //プレイヤーの状態に対する操作
    public int getPlayerState() { return playerState; }
    public void changePlayerState(int state) { this.playerState = state; } 
    //指定したプレイヤーの状態のImageを返す
    public BufferedImage getPlayerTexture(int i) {
        return playerTextures.get(i);
    }
    public BufferedImage getCurrentPlayerTexture() { return currentPlayerTexture; }
    public void setCurrentPlayerTexture(int i) { //key(i)に対応するImageを返す
        if(i > 0 && i <= 4){
            if(!(playerState > 0 && playerState <= 4)){
            	playerState = 1;
            	timer.start(200);
            }
            if(timer.check()){
            	if(playerState == 4) playerState = 1;
            	else if(playerState > 0 && playerState < 4) playerState++;
            	timer.start(200);
            } else {
            	//playerState = i;
            }
        } else if(i < 0 && i >= -4){
            if(!(playerState < 0 && playerState >= -4)){
            	playerState = -1;
            	timer.start(200);
            }
            if(timer.check()){
            	if(playerState == -4) playerState = -1;
            	else if(playerState < 0 && playerState > -4) playerState--;
            	timer.start(200);
            } else {
            	//playerState = i;
            }
        } else if(i==0) {
            playerState = playerDirection;
        } else {
            playerState = i;
        }
        
        //if(i!=0) {
            this.currentPlayerTexture = playerTextures.get(playerState);
        //}
        //System.out.println("i="+i+", playerState="+playerState);
    }
    public Image getHeartTexture(int i) {
    	switch(i){
    	    case 1:
    	    	if(playerHP <= 0) return heart0;
    	    	else return heart1;
    	    case 2:
    	    	if(playerHP <= 1) return heart0;
    	    	else return heart1;
    	    case 3:
    	    	if(playerHP <= 2) return heart0;
    	    	else return heart1;
    	    default:
    	    	return heart0;
    	}
    }

    public int getPlayerUnitSize(int i) { return (int)playerUnitSize[i]; }
    
    public void changeStage() { // Stageを変える、switchで次に移す仕様(1/26更新)
     	int current_id = currentStageData.getID();
     	System.out.println(current_id);
    	switch(current_id){
    	    case 0:
    	    	setCurrentStageData(1);
                resetEnemies();
    	    	break;
    	    case 1:
    	        setCurrentStageData(2);
                resetEnemies();
    	    	break;
    	    case 2:
    	    	setCurrentStageData(0); // 2/1
                resetEnemies();
                isGameClear = true; // 2/1
                setPlayerHP(3); // 2/1
    	    	break;
    	    case 3:
                setCurrentStageData(0); // 2/1
                isGameClear = true; // 2/1
                setPlayerHP(3); // 2/1
    	    	break;
    	    default:
    	    	setCurrentStageData(0);
    	    	break;
    	}
    }   

    //xとyは相対距離(+(x,y))、移動できる場合はtrue,できない場合はfalseを返す(1/19更新)
    public boolean movePlayer(int x, int y) {
    	int data11, data12, data21, data22;
        float nx1, nx2, ny1, ny2;
        nx1 = playerLocation[0]+x;
        nx2 = playerLocation[0]+(playerUnitSize[0])+x;
        ny1 = playerLocation[1]+y;
        ny2 = playerLocation[1]+(playerUnitSize[1])+y;
        
        data11 = currentStageData.getStageDataL(nx1, ny1);
        data21 = currentStageData.getStageDataL(nx2, ny1);
        data12 = currentStageData.getStageDataL(nx1, ny2);
        data22 = currentStageData.getStageDataL(nx2, ny2);
        
     	//System.out.println("11:"+data11+" 12:"+data12+" 21:"+data21+" 22:"+data22);
        //System.out.println("("+nx1+", "+ny+")");
        if(data11 == -1 || data11 == 4) { //ブロックがなければ移動する
            if(data21 == -1 || data21 == 4) {
                if(isPlayerLanded) {
                    setPlayerLocation(playerLocation[0]+x, playerLocation[1]+y);
                } else {
                    if((data12 != -1 && data12 != 4) || (data22 != -1 && data22 != 4)) {
                        setPlayerLocation(playerLocation[0], playerLocation[1]+y);
                    } else {
                        setPlayerLocation(playerLocation[0]+x, playerLocation[1]+y);
                    }
                }
                if(checkPlayerLocation((int)nx1, (int)ny1, 1, 4)) {
            	    changeStage();
        	}
                return true;
            }
        }
        
        return false;
    }
    public boolean getIsPlayerLanded() { return isPlayerLanded; }

    public int getPlayerMovingState() { return playerMovingState; }
    public void changePlayerMovingState(int state) { this.playerMovingState = state; }

    public void stopPlayer() { changePlayerMovingState(0); }

    //プレイヤーの初期位置を返す
    //引数：
    //      i: 軸(x=0,y=1)
    public int getPlayerFirstLoc(int i) {
        return currentStageData.getFirstLoc(i);
    }

    //背景画像を返す
    public Image getCurrentBackgroundImage() { return backgrounds.get(currentStageData.getBGid()); }
    //現在のステージデータを参照する
    public int getCurrentStageData(int x, int y) { return currentStageData.getStageData(x, y); }
    //現在のステージデータの指定のレイヤーを参照する(1/19)
    public int getCurrentStageDataLayer(int x, int y, int layer) {
        return currentStageData.getStageDataLayer(x, y, layer);
    }
    //現在のステージのidを返す (1/15)
    public int getCurrentStageId() { return currentStageData.getID(); }
    //現在のステージデータを変更する
    //ステージの切り替えはこれを呼び出して行う
    public void setCurrentStageData(int id) {
        int newx, newy;
        this.currentStageData = stageData.get(id);
        newx = (int)(currentStageData.getFirstLoc(0) * blockSize * zoom);
        newy = (int)(currentStageData.getFirstLoc(1) * blockSize * zoom);
        setPlayerLocation(newx, newy);
        velocityX = 0; velocityY = 0;
    }

    //メインメニュー画面かどうか (1/15)
    public boolean getIsMainMenuEnabled() { return isMainMenuEnabled; }
    public void setIsMainMenuEnabled(boolean b) { this.isMainMenuEnabled = b; }

    //GameOver(2/1)
    public boolean getIsGameOver() { return isGameOver; }
    public void setIsGameOver(boolean b) { this.isGameOver = b; }

    //GameClear(2/1)
    public boolean getIsGameClear() { return isGameClear; }
    public void setIsGameClear(boolean b) { this.isGameClear = b; }
    
    //マップを改造したとき用
    public void saveCurrentStageData() {  }
    //
    public void initializeStageData(int id) { modifiedStageData.set(id, stageData.get(id)); }
    public void initializeAllStageData() {
        int i=0;
        for(StageData sd: stageData) {
            modifiedStageData.set(i++, sd);
        }
    }

    //各キーが押されているかどうかの変数を変更する
    public void setRightKey(boolean b) { this.rightKey = b; }
    public void setLeftKey(boolean b) { this.leftKey = b; }
    public void setUpKey(boolean b) { this.upKey = b; }
    public void setDownKey(boolean b) { this.downKey = b; }
    public void setShiftKey(boolean b) { this.shiftKey = b; }

    //プレイヤーの動きに関する計算(1/19更新)
    public void move() {
        if(rightKey) { //右キーが優先される
            if(isPlayerLanded) {
                setCurrentPlayerTexture(1);
            } else {
                if(velocityY > 0) setCurrentPlayerTexture(11);
                else              setCurrentPlayerTexture(10);
            }
            playerDirection = 1;
            if(velocityX < maxVelocityX) {
                velocityX += 3;
                if(velocityX > maxVelocityX) velocityX = maxVelocityX;
            }
            if(!movePlayer((int)velocityX, 0)) { velocityX = 0; }
        } else if(leftKey) {
            if(isPlayerLanded) {
                setCurrentPlayerTexture(-1);
            } else {
                if(velocityY > 0) setCurrentPlayerTexture(-11);
                else              setCurrentPlayerTexture(-10);
            }
            playerDirection = -1;
            if(velocityX > -1*maxVelocityX) {
                velocityX -= 3;
                if(velocityX < -1*maxVelocityX) velocityX = -1*maxVelocityX;
            }
            if(!movePlayer((int)velocityX, 0)) { velocityX = 0; }
        } else { //左右入力されていないときは止まる
            if(velocityX != 0) {
                //(1/19)
                if(isPlayerLanded) {
                    velocityX += -2 * (velocityX/Math.abs(velocityX));
                    if(Math.abs(velocityX) <= 1) velocityX = 0;
                } else velocityX += -0.2f * (velocityX/Math.abs(velocityX));
                //
                movePlayer((int)velocityX, 0);
            } else if(isPlayerLanded) {
                //setCurrentPlayerTexture(playerDirection);
                setCurrentPlayerTexture(0);
            }
            
        }
        //System.out.println("Vx: "+velocityX);
        if(upKey) { //ジャンプの処理
            setCurrentPlayerTexture(playerDirection*10);
            velocityY = -16.0f;
            if(!movePlayer(0, (int)velocityY)) { velocityY = 0; }
            upKey = false;
        }
        if(!isPlayerLanded) { //プレイヤーが地面にいない場合は落ちる
            if(velocityY < maxVelocityY) {
                velocityY += 1.0f;
                if(velocityY > maxVelocityY) velocityY = maxVelocityY;
            }
            if(velocityY > 0) {
                setCurrentPlayerTexture(playerDirection*11);
            }
            movePlayer(0, (int)velocityY);
        }
        if(isPlayerLanded) {
            velocityY = 0.0f;
            //setCurrentPlayerTexture(playerDirection); // 1/31 コメントアウトでキャラの点滅止まる
            //ここで渡したplayerDirectionによってplayerStateが-1→-2に変更されることがあった原因不明
        }
    }

    //1/27
    public void setPlayerHP(int x) {
        playerHP = x;
    }
    public int getPlayerHP() {
        return playerHP;
    }
    //画面座標のx,yを入力し、受けるダメージを返す
    public int isBlockDamaging(float x, float y) {
        int d1 = currentStageData.getStageDataL(x, y), d2 = currentStageData.getStageDataLayerL(x, y, 2);
        //ダメージを受けるブロックを追加
        if(d1 == 5) {//N 
            //System.out.println("Needle");
            return 1;
        } else if(d2 == 14 || d2==15 || d2==16 || d2==17){//F
            //System.out.println("damage F");
            return 2;
        } else if(d2 == 6) {//L
            //System.out.println("Lava");
            return 3;
        } else
            return 0;
    }
    
    //プレイヤーの位置x,yを入力し、ダメージを受けるブロックに触れていればダメージ数を返し、受けていなければ0を返す。
    public int isPlayerTouchDamage(int x, int y) {
        //下側
        if(isBlockDamaging(x, y+(playerUnitSize[1]))!=0) {
            if(isBlockDamaging(x+(playerUnitSize[0]),y+(playerUnitSize[1]))!=0) 
                return isBlockDamaging(x, y+(playerUnitSize[1]));
            else if(currentStageData.getStageDataL(x+(playerUnitSize[0]),y+(playerUnitSize[1]))==-1)
                return isBlockDamaging(x, y+(playerUnitSize[1]));
        } else if(isBlockDamaging(x+(playerUnitSize[0]),y+(playerUnitSize[1]))!=0) {
            if(isBlockDamaging(x, y+(playerUnitSize[1]))!=0)
                return isBlockDamaging(x+(playerUnitSize[0]),y+(playerUnitSize[1]));
            else if(currentStageData.getStageDataL(x, y+(playerUnitSize[1]))==-1)
                return isBlockDamaging(x+(playerUnitSize[0]),y+(playerUnitSize[1]));
        }
        //右側
        else if(isBlockDamaging(x+(playerUnitSize[0])+2,y+(playerUnitSize[1])/4)!=0)
            return isBlockDamaging(x+(playerUnitSize[0])+2,y+(playerUnitSize[1])/4);
        else if(isBlockDamaging(x+(playerUnitSize[0])+2,y+(playerUnitSize[1])*3/4)!=0)
            return isBlockDamaging(x+(playerUnitSize[0])+2,y+(playerUnitSize[1])*3/4);
        //左側
        else if(isBlockDamaging(x-2,y+(playerUnitSize[1])/4)!=0)
            return isBlockDamaging(x,y+(playerUnitSize[1])/4);
        else if(isBlockDamaging(x-2,y+(playerUnitSize[1])*3/4)!=0)
            return isBlockDamaging(x,y+(playerUnitSize[1])*3/4);
        //上側
        else if(isBlockDamaging(x, y-1)!=0)
            return isBlockDamaging(x, y-1);
        else if(isBlockDamaging(x+(playerUnitSize[0]), y-1)!=0)
            return isBlockDamaging(x+(playerUnitSize[0]), y-1);
        return 0;
    }

    //2/1
    public int getEnemyNum() { return enemyNum; }
    public Enemy getEnemy(int i) { return enemies.get(i); }
    
    public boolean getIsPlayerInvincible() { return isPlayerinvincible; }
    public void setIsPlayerInvincible(boolean b) { isPlayerinvincible = b; }

    public void loadEnemy() {
        enemyNum = currentStageData.getEnemyNum();
        System.out.println("enemynum:"+enemyNum);
        float unitSize = zoom*blockSize;
        if(enemyNum == 0)
            return;
        for(int i=0; i<enemyNum; ++i) {
            int enemyType = currentStageData.getEnemyType(i), fx = currentStageData.getEnemyFirstPositionX(i), fy = currentStageData.getEnemyFirstPositionY(i);
            enemies.add(i, new Enemy(enemyType));
            enemies.get(i).setEnemyLocationX(Math.round(fx*unitSize));
            enemies.get(i).setEnemyLocationY(Math.round(fy*unitSize));
        }
    }

    public void printEnemyposition() {
        for(int i=0; i<enemyNum; ++i) {
            Enemy enemy = enemies.get(i);
            int x = enemy.getEnemyLocationX(), y = enemy.getEnemyLocationY();
            System.out.print("enemy."+i);
            System.out.println("("+x+","+y+")");
        }
    }
    //画面座標のx,yを入力し、受けるダメージを返す
    public int isEnemyexist(float x, float y) {
        for(int i=0;i<enemyNum;++i) {
            Enemy enemy = enemies.get(i);
            int ex = enemy.getEnemyLocationX(), ey = enemy.getEnemyLocationY(), sizex = enemy.getEnemySizeX(), sizey = enemy.getEnemySizeY();
            if(ex<x && x<ex+sizex) {
                if(ey<y && y<ey+sizey) {
                    return enemy.getEnemyDamage();
                }
            }
        }
        return 0;
    }

    public int isTouchEnemy(int x, int y) {
        //下側
        if(isEnemyexist(x, y+(playerUnitSize[1]))!=0) {
            if(isEnemyexist(x+(playerUnitSize[0]),y+(playerUnitSize[1]))!=0) 
                return isEnemyexist(x, y+(playerUnitSize[1]));
            else if(currentStageData.getStageDataL(x+(playerUnitSize[0]),y+(playerUnitSize[1]))==-1)
                return isEnemyexist(x, y+(playerUnitSize[1]));
        } else if(isEnemyexist(x+(playerUnitSize[0]),y+(playerUnitSize[1]))!=0) {
            if(isEnemyexist(x, y+(playerUnitSize[1]))!=0)
                return isEnemyexist(x+(playerUnitSize[0]),y+(playerUnitSize[1]));
            else if(currentStageData.getStageDataL(x, y+(playerUnitSize[1]))==-1)
                return isEnemyexist(x+(playerUnitSize[0]),y+(playerUnitSize[1]));
        }
        //右側
        else if(isEnemyexist(x+(playerUnitSize[0])+2,y+(playerUnitSize[1])/4)!=0)
            return isEnemyexist(x+(playerUnitSize[0])+2,y+(playerUnitSize[1])/4);
        else if(isEnemyexist(x+(playerUnitSize[0])+2,y+(playerUnitSize[1])*3/4)!=0)
            return isEnemyexist(x+(playerUnitSize[0])+2,y+(playerUnitSize[1])*3/4);
        //左側
        else if(isEnemyexist(x-2,y+(playerUnitSize[1])/4)!=0)
            return isEnemyexist(x,y+(playerUnitSize[1])/4);
        else if(isEnemyexist(x-2,y+(playerUnitSize[1])*3/4)!=0)
            return isEnemyexist(x,y+(playerUnitSize[1])*3/4);
        //上側
        else if(isEnemyexist(x, y-1)!=0)
            return isEnemyexist(x, y-1);
        else if(isEnemyexist(x+(playerUnitSize[0]), y-1)!=0)
            return isEnemyexist(x+(playerUnitSize[0]), y-1);
        return 0;
    }

    public void moveEnemies() {
        for(int i=0; i<enemyNum; ++i) {
            enemies.get(i).moveEnemy(currentStageData);
        }
    }

    public void damage() {
        int d  = isPlayerTouchDamage(playerLocation[0], playerLocation[1]), d1 = isTouchEnemy(playerLocation[0], playerLocation[1]);
        if(d<d1)
            d = d1;
        if(d!=0) {
            if(!isPlayerinvincible) {
                setPlayerHP(getPlayerHP()-d);
                System.out.println(getPlayerHP());
                inv_timer.start(3000); 
                isPlayerinvincible = true;
            } else if(inv_timer.check()) {
                isPlayerinvincible = false;
            }
        }
        if(inv_timer.check()) { isPlayerinvincible = false; }
        if(getPlayerHP() <= 0) { //(2/1)
            setIsGameOver(true);
            setPlayerHP(3);
        } 
    }
     void resetEnemies() {
        for(int i=0; i<enemyNum; ++i) {
            enemies.get(i).setEnemyLocationX(currentStageData.getEnemyFirstPositionX(i));
            enemies.get(i).setEnemyLocationY(currentStageData.getEnemyFirstPositionY(i));
        }
        loadEnemy();
     }
}

