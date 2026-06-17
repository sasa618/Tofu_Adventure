package src;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

//StageData用のクラス------------------------------
//ステージデータの中身を扱う
public final class StageData {
    //private int stageData[][]; //ステージのブロックを整数の2次配列で管理[x][y] (1/19 stageDataLayers[1][x][y]に移動)
    private int stageDataLayers[][][]; //各レイヤー別に装飾(当たり判定のないブロック)を管理[レイヤー番号][x][y]
    private int background; //background用のイメージのid (1/19)
    private int id; //ステージのid
    private int playerFirstLocation[] = {0, 0};
    private int blockNumbers[];
    //private int collision[][]; //衝突判定用
    private float unitSize;
    private int unitSizeI;

    //2/1
    private final static int maxEnemyNum = 16; //敵の最大数
    private int enemyFirstLocations[][]; 
    private int enemyTypes[];
    private int enemyNum; //敵の数
    
    public StageData(String fileName, int id, int[] blockNumbers, float unitSize, int bg_id) {
        //2/1
        enemyFirstLocations = new int[maxEnemyNum][2];
        enemyNum = 0;
        enemyTypes = new int[maxEnemyNum];

        this.id = id;
        this.unitSize = unitSize;
        this.unitSizeI = (int)unitSize;
        this.background = bg_id; //(1/19)
        this.blockNumbers = blockNumbers;
        ///this.collision = new int[(int)(unitSize*blockNumbers[0])][(int)(unitSize*blockNumbers[1])];

        //ファイルがあるかどうかを調べる(1/19)---
        File fileL[] = new File[3];
        fileL[1] = new File("data/maps/"+fileName);
        if(!fileL[1].exists()) {
            fileL[1] = new File("data/maps/"+fileName+".txt");
            if(!fileL[1].exists())
                System.out.println("データファイル"+fileName+"が存在しません");
        }
        fileL[0] = new File("data/maps/"+fileName+"-L0");
        if(!fileL[0].exists()) {
            fileL[0] = new File("data/maps/"+fileName+"-L0.txt");
            if(!fileL[0].exists())
                System.out.println("データファイル"+fileName+"-L0が存在しません");
        }
        fileL[2] = new File("data/maps/"+fileName+"-L2");
        if(!fileL[2].exists()) {
            fileL[2] = new File("data/maps/"+fileName+"-L2.txt");
            if(!fileL[2].exists())
                System.out.println("データファイル"+fileName+"-L2が存在しません");
        }
        //---

        //stageData = new int[blockNumbers[0]][blockNumbers[1]];
        stageDataLayers = new int[3][blockNumbers[0]][blockNumbers[1]];

        //ブロックのデータを一つずつ読み込む
        FileReader fileReader;
        int filedata, i, j;
        char c;
        for(int k=0; k<=2; k++) {
            i=0; j=0;
            try {
                fileReader = new FileReader(fileL[k]);
                try {
                    while((filedata = fileReader.read()) != -1) {
                        if(filedata==10) { //'\n'は無視する
                            j++; i=0;
                            continue;
                        } else if(filedata==47 || filedata==13) { continue; } //'/'とCR(行頭復帰文字)は無視する
                        switch(filedata) {
                            case 'p': //p
                                playerFirstLocation[0] = i;
                                playerFirstLocation[1] = j;
                                stageDataLayers[k][i][j] = -1;
                                break;
                            case '0': //0
                                stageDataLayers[k][i][j] = 0;
                                break;
                            case 'D': //D
                                stageDataLayers[k][i][j] = 2;
                                break;
                            case '<': //<(1/19)
                                stageDataLayers[k][i][j] = 3;
                                break;
                            case '>': //>
                                stageDataLayers[k][i][j] = 4;
                                break;
                            case 'N': //N
                                stageDataLayers[k][i][j] = 5;
                                break;
                            case 'L': //L
                                stageDataLayers[k][i][j] = 6;
                                break;
                            case 'G': //G
                                stageDataLayers[k][i][j] = 7;
                                break;
                            case 'g':
                                stageDataLayers[k][i][j] = 8;
                                break;
                            case 'X': //X AnimationするBlock
                                stageDataLayers[k][i][j] = 9;
                                break;
                            case 'Y': //Y AnimationするBlock2
                                stageDataLayers[k][i][j] = 10;
                                break;
                            case 'Z': //Z AnimationするBlock3
                                stageDataLayers[k][i][j] = 11;
                                break;
                            case 'B': //B Butterfly 2/1
                                stageDataLayers[k][i][j] = 12;
                                break;
                            case 'b': //b butterfly2 2/1
                                stageDataLayers[k][i][j] = 13;
                                break;
                            case 'F': // F Fire 2/1
                                stageDataLayers[k][i][j] = 14;
                                break;
                            case 'f': // f fire 2/1
                                stageDataLayers[k][i][j] = 16;
                                break;
                            case 32: //空白
                                stageDataLayers[k][i][j] = -1;
                                break;
                                //2/1
                            case 'E': //
                                stageDataLayers[k][i][j] = -1;
                                enemyFirstLocations[enemyNum][0] = i;
                                enemyFirstLocations[enemyNum][1] = j;
                                enemyTypes[enemyNum] = 1;
                                enemyNum++;
                                break;
                            default:
                                c = (char)filedata;
                                System.out.println("コード"+c+"("+filedata+")は未定義です");
                                stageDataLayers[k][i][j] = 1;
                                break;
                        }
                        i++;
                    }
                } catch(IOException e){
                    System.out.println(e);
                }
            } catch(FileNotFoundException e) {
                System.out.println(e);
            }
        }
        System.out.println("Stage"+id+" was loaded!");
    }

    public int getID() { return id; }

    public int getBGid() { return background; } //(1/19)

    //(x,y)のマスのブロックデータを返す
    public int getStageData(int x, int y) {
        if(x>=0 && x<blockNumbers[0]) {
            if(y>=0 && y<blockNumbers[1])
                return stageDataLayers[1][x][y];
        }
        return -1; //範囲外なら-1(空白)を返す
    }
    //(x,y)のマスのブロックデータを返す
    public int getStageDataLayer(int x, int y, int layer) {
        if(x>=0 && x<blockNumbers[0]) {
            if(y>=0 && y<blockNumbers[1])
                return stageDataLayers[layer][x][y];
        }
        return -1; //範囲外なら-1(空白)を返す
    }
    public void setStageData(int x, int y, int data) { //(x,y)のマスのブロックデータを書き換える
        this.stageDataLayers[1][x][y] = data;
    }

    //(x,y)の座標のブロックデータを返す
    public int getStageDataL(float x, float y) {
        int nx, ny;
        nx = (int)(x/unitSize);
        ny = (int)(y/unitSize);
        //System.out.println("("+nx+", "+ny+"), us:"+unitSize+" ("+x+", "+y+")");
        try {
            return stageDataLayers[1][nx][ny];
        } catch(IndexOutOfBoundsException e) {
            return -1; //範囲外なら-1(空白)を返す
        }
    }
    //指定したレイヤーの、(x,y)の座標のブロックデータを返す
    public int getStageDataLayerL(float x, float y, int layer) {
        int nx, ny;
        nx = (int)(x/unitSize);
        ny = (int)(y/unitSize);
        //System.out.println("("+nx+", "+ny+"), us:"+unitSize+" ("+x+", "+y+")");
        try {
            return stageDataLayers[layer][nx][ny];
        } catch(IndexOutOfBoundsException e) {
            return -1; //範囲外なら-1(空白)を返す
        }
    }

    public int getFirstLoc(int i) { return playerFirstLocation[i]; }

    //2/1
    public int getEnemyNum() { return enemyNum; }
    public int getEnemyFirstPositionX(int i) { return enemyFirstLocations[i][0]; }
    public int getEnemyFirstPositionY(int i) { return enemyFirstLocations[i][1]; }
    public int getEnemyType(int i) { return enemyTypes[i]; }
}
