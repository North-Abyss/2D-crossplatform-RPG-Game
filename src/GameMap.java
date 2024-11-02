import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GameMap {

    private static final int TILE_SIZE = Value.TileSize;
    private static final int WATER_ANIMATION_FRAMES = Value.waterAnimationPaths.length;
    private final int FRAME_DURATION_MS;

    private final BufferedImage[][] animatedTiles;
    private final BufferedImage[] staticTilesheets;
    private final int[][] backgroundLayer;


    private int camX, camY;
    private final int screenWidth;
    private final int screenHeight;
    private final int mapWidth;
    private final int mapHeight;

    private int currentWaterFrame = 0;
    private long lastFrameTime = 0;

    public GameMap(String[] tilesheetPaths, String backgroundLayerPath, int screenWidth, int screenHeight) {
        this.backgroundLayer = loadLayer(backgroundLayerPath);
        this.staticTilesheets = loadTileSheets(tilesheetPaths);
        this.animatedTiles = loadAnimatedTiles(Value.waterAnimationPaths);

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.mapWidth = backgroundLayer[0].length * TILE_SIZE;
        this.mapHeight = backgroundLayer.length * TILE_SIZE;

        this.FRAME_DURATION_MS = Value.waterFrameDurationMs; // Configurable frame duration
        lastFrameTime = System.currentTimeMillis();
    }

    private BufferedImage[] loadTileSheets(String[] paths) {
        BufferedImage[] sheets = new BufferedImage[paths.length];
        for (int i = 0; i < paths.length; i++) {
            sheets[i] = loadImage(paths[i]);
        }
        return sheets;
    }

    private BufferedImage[][] loadAnimatedTiles(String[] paths) {
        BufferedImage[][] tiles = new BufferedImage[WATER_ANIMATION_FRAMES][paths.length];
        for (int frame = 0; frame < WATER_ANIMATION_FRAMES; frame++) {
            for (int tileType = 0; tileType < paths.length; tileType++) {
                tiles[frame][tileType] = loadImage(paths[frame]);
            }
        }
        return tiles;
    }

    private BufferedImage loadImage(String path) {
        URL imageUrl = getClass().getResource(path);
        try {
            if (imageUrl == null) {
                System.err.println("Tile sheet not found: " + path);
                return createDefaultTileSheet();
            }
            return ImageIO.read(imageUrl);
        } catch (IOException e) {
            System.err.println("IOException while reading image: " + path);
            e.printStackTrace();
            return createDefaultTileSheet();
        }
    }

    private BufferedImage createDefaultTileSheet() {
        return new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
    }

    private int[][] loadLayer(String layerPath) {
        List<int[]> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream(layerPath))))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int[] row = Arrays.stream(line.trim().split(","))
                        .map(String::trim)
                        .mapToInt(Integer::parseInt)
                        .toArray();
                rows.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new int[0][0];
        }
        return rows.toArray(new int[0][0]);
    }

    public void updateCamera(int playerX, int playerY) {
        // Smooth camera movement with easing effect
        camX += (playerX - screenWidth / 2 - camX); // Explicitly cast to int
        camY += (playerY - screenHeight / 2 - camY);
        camX = Math.max(0, Math.min(camX, mapWidth - screenWidth));
        camY = Math.max(0, Math.min(camY, mapHeight - screenHeight));
    }

    public void drawMap(Graphics g, int playerX, int playerY) {
        updateCamera(playerX, playerY);

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime >= FRAME_DURATION_MS) {
            currentWaterFrame = (currentWaterFrame + 1) % WATER_ANIMATION_FRAMES;
            lastFrameTime = currentTime;
        }

        int startTileX = Math.max(0, camX / TILE_SIZE);
        int startTileY = Math.max(0, camY / TILE_SIZE);
        int endTileX = Math.min((camX + screenWidth) / TILE_SIZE, backgroundLayer[0].length - 1);
        int endTileY = Math.min((camY + screenHeight) / TILE_SIZE, backgroundLayer.length - 1);

        for (int row = startTileY; row <= endTileY; row++) {
            for (int col = startTileX; col <= endTileX; col++) {
                int tileType = backgroundLayer[row][col];
                int tileX = col * TILE_SIZE - camX;
                int tileY = row * TILE_SIZE - camY;

                // Handle animated water tiles
                if (Arrays.stream(Value.anitile).anyMatch(num -> num == tileType) && animatedTiles[currentWaterFrame][0] != null) {
                    drawAnimatedTile(g, tileX, tileY);
                } else if (tileType >= 0 && tileType < staticTilesheets.length) {
                    g.drawImage(staticTilesheets[tileType], tileX, tileY, TILE_SIZE, TILE_SIZE, null);
                }
            }
        }
    }

    private void drawAnimatedTile(Graphics g, int tileX, int tileY) {
        g.drawImage(animatedTiles[currentWaterFrame][0], tileX, tileY, TILE_SIZE, TILE_SIZE, null);
    }


    public void setTile(int x, int y, int TileID) {
        if (x >= 0 && x < backgroundLayer[0].length && y >= 0 && y < backgroundLayer.length) {
            backgroundLayer[y][x] = TileID;
        }
    }

    public int bgmapsize(){
        return backgroundLayer.length;
    }

}
