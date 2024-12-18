import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class TileSetting {

    public static int TILE_SIZE = Value.TileSize;

    private int camX, camY;
    private final int screenWidth;
    private final int screenHeight;
    private final int mapWidth;
    private final int mapHeight;

    // Array of fence paths, 16 combinations for fence tiles
    static String[] FencePaths = Value.fencesheetPaths;

    private final BufferedImage[] fenceImages;
    private final boolean[][] collidableTiles;

    // Constructor to load fence tiles and the collision map
    public TileSetting(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        fenceImages = new BufferedImage[FencePaths.length];
        loadFenceImages();

        // Load and convert the collision map to a boolean array
        int[][] collisionMap = loadCollisionMap(Value.collisionLayerPath);
        collidableTiles = convertToBooleanArray(collisionMap);

        // Initialize dimensions based on collision map size
        this.mapWidth = TILE_SIZE * (collidableTiles.length == 0 ? 1 : collidableTiles[0].length);
        this.mapHeight = TILE_SIZE * (collidableTiles.length == 0 ? 1 : collidableTiles.length);
    }

    // Method to load fence images from paths
    private void loadFenceImages() {
        for (int i = 0; i < FencePaths.length; i++) {
            try {
                fenceImages[i] = ImageIO.read(Objects.requireNonNull(getClass().getResource(FencePaths[i])));
            } catch (IOException e) {
                System.err.println("Failed to load fence tile: " + FencePaths[i]);
                fenceImages[i] = createDefaultTile(); // Use default if loading fails
            }
        }
    }

    // Create a default tile in case of loading error
    private BufferedImage createDefaultTile() {
        BufferedImage defaultTile = new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = defaultTile.createGraphics();
        g2d.setColor(Color.GRAY);
        g2d.fillRect(0, 0, TILE_SIZE, TILE_SIZE);
        g2d.dispose();
        return defaultTile;
    }

    public void updateCamera(int playerX, int playerY) {
        // Smooth camera movement with easing effect
        camX += (playerX - screenWidth / 2 - camX); // Explicitly cast to int
        camY += (playerY - screenHeight / 2 - camY);
        camX = Math.max(0, Math.min(camX, mapWidth - screenWidth));
        camY = Math.max(0, Math.min(camY, mapHeight - screenHeight));
    }

    // Method to draw the black background (as a canvas)
    public void drawBackMap(Graphics g, int playerX, int playerY) {
        updateCamera(playerX, playerY);

        int extraTileSize = TILE_SIZE;
        int drawX = -camX - extraTileSize;
        int drawY = -camY - extraTileSize;
        int drawWidth = screenWidth + 2 * extraTileSize;
        int drawHeight = screenHeight + 2 * extraTileSize;

        g.setColor(Color.BLACK);
        g.fillRect(drawX, drawY, drawWidth, drawHeight);
    }

    // Method to draw fences on the map
    public void drawFences(Graphics g, int playerX, int playerY) {
        updateCamera(playerX, playerY);

        int startCol = Math.max(0, camX / TILE_SIZE);
        int startRow = Math.max(0, camY / TILE_SIZE);
        int endCol = Math.min((camX + screenWidth) / TILE_SIZE, collidableTiles[0].length - 1);
        int endRow = Math.min((camY + screenHeight) / TILE_SIZE, collidableTiles.length - 1);

        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                if (collidableTiles[row][col]) {
                    int tileX = col * TILE_SIZE - camX;
                    int tileY = row * TILE_SIZE - camY;

                    int fenceIndex = getFenceTileIndex(row, col);
                    g.drawImage(fenceImages[fenceIndex], tileX, tileY, TILE_SIZE, TILE_SIZE, null);
                }
            }
        }
    }

    private int getFenceTileIndex(int row, int col) {
        boolean top = row > 0 && collidableTiles[row - 1][col];
        boolean bottom = row < collidableTiles.length - 1 && collidableTiles[row + 1][col];
        boolean left = col > 0 && collidableTiles[row][col - 1];
        boolean right = col < collidableTiles[row].length - 1 && collidableTiles[row][col + 1];

        if (top && bottom && left && right) return 4;
        else if (top && bottom && left) return 5;
        else if (top && bottom && right) return 3;
        else if (left && bottom && right) return 1;
        else if (top && left && right) return 7;
        else if (top && left) return 8;
        else if (top && right) return 6;
        else if (left && right) return 13;
        else if (bottom && left) return 2;
        else if (bottom && top) return 10;
        else if (bottom && right) return 0;
        else if (top) return 11;
        else if (bottom) return 9;
        else if (left) return 14;
        else if (right) return 12;
        else return 15;
    }

    // Method to load collision map from a file (e.g., layer2.txt)
    private int[][] loadCollisionMap(String filePath) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream(filePath))))) {

            String line;
            int numCols = -1;
            int numRows = 0;
            while ((line = br.readLine()) != null) {
                if (numCols == -1) numCols = line.split(",").length;
                numRows++;
            }

            int[][] map = new int[numRows][numCols];

            try (BufferedReader br2 = new BufferedReader(new InputStreamReader(
                    Objects.requireNonNull(getClass().getResourceAsStream(filePath))))) {
                int row = 0;
                while ((line = br2.readLine()) != null) {
                    String[] values = line.split(",");
                    if (values.length != numCols) {
                        System.err.println("Error Not Equal row and column: " + filePath);
                    }
                    for (int col = 0; col < values.length; col++) {
                        map[row][col] = Integer.parseInt(values[col].trim());
                    }
                    row++;
                }
            }
            return map;
        } catch (IOException e) {
            System.err.println("Failed to load collision map from " + filePath);
            e.printStackTrace();
            return new int[0][0]; // Return an empty array on error
        }
    }

    private boolean[][] convertToBooleanArray(int[][] intArray) {
        boolean[][] booleanArray = new boolean[intArray.length][intArray[0].length];
        for (int row = 0; row < intArray.length; row++) {
            for (int col = 0; col < intArray[row].length; col++) {
                booleanArray[row][col] = intArray[row][col] != 0; // Non-zero values are collidable
            }
        }
        return booleanArray;
    }

    public boolean isCollidable(int[] position, byte pd, HitboxData hitboxData) {
        int x = position[2];
        int y = position[3];
        int playerSize = 48;  // Assuming player is 48x48
        int tileSize = TILE_SIZE; // Tile size in your game

        // Update player hitbox position based on the player's current coordinates
        hitboxData.update(x + playerSize / 4, x + (3 * playerSize / 4),
                y + playerSize / 4, y + (3 * playerSize / 4));

        // Screen boundary check
        if (x < 0 || x + playerSize > screenWidth || y < 0 || y + playerSize > screenHeight) {
            return false; // Collision with screen boundary
        }

        boolean collisionDetected = false;

        // Calculate tile ranges to check based on hitbox, adjusted for camera offset
        int tileXStart = Math.max(0, (hitboxData.hitboxLeft + camX) / tileSize);
        int tileXEnd = Math.min(collidableTiles[0].length - 1, (hitboxData.hitboxRight + camX) / tileSize);
        int tileYStart = Math.max(0, (hitboxData.hitboxTop + camY) / tileSize);
        int tileYEnd = Math.min(collidableTiles.length - 1, (hitboxData.hitboxBottom + camY) / tileSize);

        // Iterate over the nearby tiles
        for (int tileY = tileYStart; tileY <= tileYEnd; tileY++) {
            for (int tileX = tileXStart; tileX <= tileXEnd; tileX++) {
                if (collidableTiles[tileY][tileX]) {
                    // Get the tile's pixel coordinates in the game world, adjusted by camera
                    int tilePixelX = tileX * tileSize - camX;
                    int tilePixelY = tileY * tileSize - camY;

                    // Update the hitbox for the current tile
                    hitboxData.updatetiles(tilePixelX, tilePixelY, tileSize);

                    // Define tile hitbox boundaries
                    int tileHitboxRight = tilePixelX + tileSize;
                    int tileHitboxBottom = tilePixelY + tileSize;

                    // Check for collision based on movement direction
                    if (pd == 1 && hitboxData.hitboxTop < tileHitboxBottom && hitboxData.hitboxBottom > tilePixelY &&
                            hitboxData.hitboxRight > tilePixelX && hitboxData.hitboxLeft < tileHitboxRight) {
                        collisionDetected = true;
                        y = tileHitboxBottom; // Push player below the tile
                    } else if (pd == 2 && hitboxData.hitboxBottom > tilePixelY && hitboxData.hitboxTop < tileHitboxBottom &&
                            hitboxData.hitboxRight > tilePixelX && hitboxData.hitboxLeft < tileHitboxRight) {
                        collisionDetected = true;
                        y = tilePixelY - playerSize; // Push player above the tile
                    } else if (pd == 3 && hitboxData.hitboxLeft < tileHitboxRight && hitboxData.hitboxRight > tilePixelX &&
                            hitboxData.hitboxBottom > tilePixelY && hitboxData.hitboxTop < tileHitboxBottom) {
                        collisionDetected = true;
                        x = tileHitboxRight; // Push player to the right of the tile
                    } else if (pd == 4 && hitboxData.hitboxRight > tilePixelX && hitboxData.hitboxLeft < tileHitboxRight &&
                            hitboxData.hitboxBottom > tilePixelY && hitboxData.hitboxTop < tileHitboxBottom) {
                        collisionDetected = true;
                        x = tilePixelX - playerSize; // Push player to the left of the tile
                    }
                }
            }
        }

        // Update player position only if no collision
        return !collisionDetected;
    }

    public int collisionmap(int n) {
        if(n==0) return collidableTiles.length;
        else return collidableTiles[0].length;
    }

}
