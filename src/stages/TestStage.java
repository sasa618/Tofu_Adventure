package src.stages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import src.GameModel;

//テスト用のステージ
public class TestStage extends StageMasterPanel {

    public TestStage(GameModel m, int id) {
        super(m, id);
    }

    @Override
    public void drawStageAll(Graphics g) {
        drawBackGround(g);
        drawLayer0(g);
        drawStage(g);
        drawPlayer(g);
        drawLayer2(g);
        drawHP(g);
    }
}