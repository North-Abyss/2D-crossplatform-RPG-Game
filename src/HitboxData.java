import java.awt.Color;
import java.awt.Graphics;

public class HitboxData {

    public int hitboxLeft;
    public int hitboxRight;
    public int hitboxTop;
    public int hitboxBottom;

    public int tilePixelX;
    public int tilePixelY;
    public int tilehitsize;

    // Colors for debugging
    private static final Color PLAYER_HITBOX_COLOR = new Color(0, 0, 255, 100); // Semi-transparent blue
    private static final Color TILE_HITBOX_COLOR = new Color(255, 0, 0, 100);   // Semi-transparent red

    // Update the player hitbox
    public void update(int left, int right, int top, int bottom) {
        this.hitboxLeft = left;
        this.hitboxRight = right;
        this.hitboxTop = top;
        this.hitboxBottom = bottom;
    }

    // Update the tile hitbox
    public void updatetiles(int tpx, int tpy, int thbs) {
        this.tilePixelX = tpx;
        this.tilePixelY = tpy;
        this.tilehitsize = thbs;
    }

    // Draw player hitbox for debugging
    public void drawPlayerHitbox(Graphics g, int camX, int camY) {
        g.setColor(PLAYER_HITBOX_COLOR);
        g.fillRect(
                hitboxLeft - camX,
                hitboxTop - camY,
                hitboxRight - hitboxLeft,
                hitboxBottom - hitboxTop
        );
    }

    // Draw tile hitbox for debugging
    public void drawTileHitbox(Graphics g, int camX, int camY) {
        g.setColor(TILE_HITBOX_COLOR);
        g.fillRect(
                tilePixelX - camX,
                tilePixelY - camY,
                tilehitsize,
                tilehitsize
        );
    }
}
