import java.awt.*;

public class HitboxData {
    public int hitboxLeft;
    public int hitboxRight;
    public int hitboxTop;
    public int hitboxBottom;

    public int tilePixelX ;
    public int tilePixelY ;
    public int tilehitsize ;

    public void update(int left, int right, int top, int bottom) {
        this.hitboxLeft = left;
        this.hitboxRight = right;
        this.hitboxTop = top;
        this.hitboxBottom = bottom;
    }
    public void updatetiles(int tpx , int tpy , int thbs ) {
        this.tilePixelX = tpx ;
        this.tilePixelY = tpy ;
        this.tilehitsize = thbs;
    }
}
