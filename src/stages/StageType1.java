package src.stages;

import java.awt.*;

import src.GameModel;

//テスト用のステージ
public class StageType1 extends StageMasterPanel {

    public StageType1(GameModel m, int id) {
        super(m, id); //親クラスのコンストラクタ呼び出し
    }

    @Override
    public void drawStageAll(Graphics g) {
        drawBackGround(g);
        drawLayer0(g);
        drawStage(g);
        drawAllEnemies(g);
        drawPlayer(g);
        drawLayer2(g);
        drawHP(g);
    }
}