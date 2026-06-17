package src.stages;

import java.awt.*;

import src.GameModel;

//テスト用のステージ
public class StageType2 extends StageMasterPanel {

    public StageType2(GameModel m, int id) {
        super(m, id);
    }

    @Override
    public void drawStageAll(Graphics g) {
        drawBackGround(g);
        drawStage(g);
        drawAllEnemies(g);
        drawPlayer(g);
        drawLayer2(g);
        drawHP(g);
    }
}