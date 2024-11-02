import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private final Player player;
    private final GameMap gameMap;
    private final TileSetting tileSetting;
    private final HitboxData hitboxData;

    private static final int TILE_SIZE = Value.TileSize;
    private static final int NUM_TILES_X = Value.NumTilex;
    private static final int NUM_TILES_Y = Value.NumTiley;

    public GamePanel() {
        // Paths to tilesheets and layer files
        String[] tilesheetPaths = Value.tilesheetPaths;
        String backgroundLayerPath = Value.backgroundLayerPath;

        // Initialize TileSetting and GameMap
        gameMap = new GameMap(tilesheetPaths, backgroundLayerPath, TILE_SIZE * NUM_TILES_X, TILE_SIZE * NUM_TILES_Y);
        tileSetting = new TileSetting(TILE_SIZE * NUM_TILES_X, TILE_SIZE * NUM_TILES_Y);
        hitboxData = new HitboxData();

        // Initialize player
        player = new Player(TILE_SIZE * 2, TILE_SIZE * 2, tileSetting, hitboxData);
        player.setGameMap(); // Ensure gameMap is set in Player

        // Set up a timer for the game loop
        int fps = 60;
        Timer timer = new Timer(1000 / fps, this);
        timer.start();

        if (gameMap.bgmapsize() != tileSetting.collisionmap(0)) {
            RepairSystem.synchronizeLayers(Value.backgroundLayerPath, Value.collisionLayerPath);
        }

        // Add key listener
        setFocusable(true);
        requestFocusInWindow(); // Request focus to ensure key events are captured
        addKeyListener(this);
    }

    @Override
    public Dimension getPreferredSize() {
        // Set the preferred size of the panel based on the tile size and number of tiles
        return new Dimension(TILE_SIZE * NUM_TILES_X, TILE_SIZE * NUM_TILES_Y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw background map
        tileSetting.drawBackMap(g, player.getX(), player.getY());

        // Draw game map
        gameMap.drawMap(g, player.getX(), player.getY());

        // Draw the player independently for smooth animation
        drawPlayer(g);

        // Draw fences on top
        tileSetting.drawFences(g, player.getX(), player.getY());

        // Access the hitbox data for drawing or debugging
        hitboxData.drawHitbox(g);
        hitboxData.drawTileHitbox(g);

    }

    private void drawPlayer(Graphics g) {  player.paint(g); }

    @Override
    public void actionPerformed(ActionEvent e) {
        player.update(); // Update player state and animation
        repaint(); // Trigger repaint to update visuals
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_ENTER) {
            // Get the player's current tile position
            int playerTileX = player.getX() / TILE_SIZE;
            int playerTileY = player.getY() / TILE_SIZE;

            int TileID = Value.waterID;
            // Set the current tile to water in the background layer
            gameMap.setTile(playerTileX, playerTileY, TileID);

            // Repaint to reflect changes
            repaint();
        } else {
            player.handleKeyPress(keyCode); // Handle other key presses for player movement
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        player.handleKeyRelease(e.getKeyCode()); // Handle key releases for player movement
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Optional: Implement if you need to handle keyTyped events
    }
}
