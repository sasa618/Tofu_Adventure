package src;

//時間を測るクラス
//start(int)で渡した時間(ミリ秒)を測る
//check()で測り終わったかどうか調べる
//isCounting()で計測中か調べる
public class Counter {
    private long startTime;
    private int time;
    private boolean isTimerOn;
    private boolean isCounting;

    public Counter() {
        this.isTimerOn = false;
        this.isCounting = false;
    }

    public void reset() {
        isTimerOn = false;
        isCounting = false;
    }
    public void start(int millis) {
        this.time = millis;
        startTime = System.currentTimeMillis();
        isTimerOn = true;
        isCounting = true;
    }
    public void startNoDisturb(int millis) {
        if(!isCounting) start(millis);
    }
    public boolean isCounting() { return isCounting; }
    public boolean check() {
        if(!isTimerOn) return false;
        if(System.currentTimeMillis()-startTime >= time) {
            reset();
            return true;
        } else {
            return false;
        }
    }
}
