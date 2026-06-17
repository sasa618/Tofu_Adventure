package src;

import java.awt.event.*;
import java.util.*;

//キー入力の受付
public class PlayerController implements MouseListener, KeyListener{
    protected GameModel model;
    protected GameFrame view;
    protected Map<Integer, KeyFunction> functions; //Mapを使って整数と処理を関連付けしている

    public PlayerController(GameModel m) {
        this.model = m;
        
        functions = new HashMap<>();
        //key=0は止まる
        functions.put(0, () -> { model.stopPlayer(); } );
        //key=1は右へ歩く
        functions.put(1, () -> {
            if(!model.getIsMainMenuEnabled()) {
                model.setRightKey(true);
            }
            //System.out.println("R: pressed");
        });
        //key=-1は右に進んでいる状態から止まる
        functions.put(-1, () -> {
            model.setRightKey(false);
            //System.out.println("R: released");
        });
        
        //key=2は左へ歩く
        functions.put(2, () -> {
            if(!model.getIsMainMenuEnabled()) {
                model.setLeftKey(true);
            }
            //System.out.println("L: pressed");
        });
        //key=-2は左に進んでいる状態から止まる
        functions.put(-2, () -> {
            model.setLeftKey(false);
            //System.out.println("L: released");
        });
        //key=10はジャンプ
        functions.put(10, () -> {
            if(model.getIsPlayerLanded() && !model.getIsMainMenuEnabled()) {
                model.setUpKey(true);
                //System.out.println("Jump: pressed");
            }
        });
        //key=50は決定
        functions.put(50, () -> {

        });
        //key=100はEsc
        functions.put(100, () -> {
            model.setIsMainMenuEnabled(true);
        });
    }
    
    public void mouseClicked(MouseEvent e) { }
    public void mousePressed(MouseEvent e) { }
    public void mouseReleased(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }

    public void keyPressed(KeyEvent e) { //キーを押したとき
        switch(e.getKeyCode()) {
            case KeyEvent.VK_UP: //ジャンプ
                functions.get(10).func();
                break;
            case KeyEvent.VK_W: //ジャンプ
                functions.get(10).func();
                break;
            case KeyEvent.VK_SPACE: //ジャンプ
                functions.get(10).func();
                break;
            case KeyEvent.VK_LEFT: //左移動
                functions.get(2).func();
                break;
            case KeyEvent.VK_RIGHT: //右移動
                functions.get(1).func();
                break;
            case KeyEvent.VK_DOWN: //
                break;
            case KeyEvent.VK_A: //左移動
                functions.get(2).func();
                break;
            case KeyEvent.VK_D: //右移動
                functions.get(1).func();
                break;
            case KeyEvent.VK_S: //
                break;
            case KeyEvent.VK_ESCAPE:
                functions.get(100).func();
                break;
            default:
                break;
        }
    }
    public void keyReleased(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_LEFT: //左移動から止まる
                functions.get(-2).func();
                //System.out.println("L: released");
                break;
            case KeyEvent.VK_RIGHT: //右移動から止まる
                functions.get(-1).func();
                //System.out.println("R: released");
                break;
            case KeyEvent.VK_DOWN: //
                break;
            case KeyEvent.VK_A: //左移動から止まる
                functions.get(-2).func();
                break;
            case KeyEvent.VK_D: //右移動から止まる
                functions.get(-1).func();
                break;
            case KeyEvent.VK_S: //
                break;
            default:
                break;
        }
    }
    
    public void keyTyped(KeyEvent e) { } //キーを押して離したとき
}