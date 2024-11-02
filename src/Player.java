import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Player {

    private final TileSetting tileSetting;
    private final HitboxData hitboxData;
    private int x; // Player's x-coordinate
    private int y; // Player's y-coordinate
    private BufferedImage currentImage; // Current image of the player
    private String state; // Current state of the player (idle, walking, running)
    private int animationFrame; // Current frame of the animation
    private int animationCounter = 0; // Counter to handle animation speed

    private Map<String, BufferedImage[]> animations; // Map to store animations for different states

    private static final int TILE_SIZE = Value.TileSize; // Size of each tile in pixels
    private static final int WALK_SPEED = Value.WALK_SPEED; // Slow walking speed (to match tile movement)
    private static final int RUN_SPEED = Value.RUN_SPEED; // Faster running speed
    // Reference to the game map for collision detection
    // Reference to the tile setting for collision detection

    private boolean isRunning = false; // Flag to check if running is enabled
    private boolean isMoving = false; // Flag to check if the player is moving
    private boolean spacePressed = false; // Flag to check if space bar is pressed
    private boolean upPressed = false, downPressed = false, leftPressed = false, rightPressed = false; // Direction flags

    public Player(int startX, int startY, TileSetting tilesetting, HitboxData hitboxData) {
        this.x = startX; // Initialize x-coordinate
        this.y = startY; // Initialize y-coordinate
        this.tileSetting = tilesetting;
        this.hitboxData = hitboxData;
        this.state = "idleDown"; // Default state when the player is not moving
        this.animationFrame = 0; // Start with the first frame of the animation
        loadImages();
        setState("idleDown"); // Set the initial state
    }

    public void setGameMap() {
    }

    private void loadImages() {
        animations = new HashMap<>();
        animations.put("idleUp", loadAnimationImages("/player/Idle/idleup", 4));
        animations.put("idleDown", loadAnimationImages("/player/Idle/idledown", 4));
        animations.put("idleLeft", loadAnimationImages("/player/Idle/idleleft", 4));
        animations.put("idleRight", loadAnimationImages("/player/Idle/idleright", 4));
        animations.put("walkingUp", loadAnimationImages("/player/Walk/walkup", 6));
        animations.put("walkingDown", loadAnimationImages("/player/Walk/walkdown", 6));
        animations.put("walkingLeft", loadAnimationImages("/player/Walk/walkleft", 6));
        animations.put("walkingRight", loadAnimationImages("/player/Walk/walkright", 6));
        animations.put("runningUp", loadAnimationImages("/player/Run/runup", 8));
        animations.put("runningDown", loadAnimationImages("/player/Run/rundown", 8));
        animations.put("runningLeft", loadAnimationImages("/player/Run/runleft", 8));
        animations.put("runningRight", loadAnimationImages("/player/Run/runright", 8));
    }

    private BufferedImage[] loadAnimationImages(String basePath, int count) {
        BufferedImage[] images = new BufferedImage[count];
        for (int i = 0; i < count; i++) {
            String path = basePath + (i + 1) + ".png";
            URL imageUrl = getClass().getResource(path);
            if (imageUrl == null) {
                System.err.println("Resource not found: " + path);
            } else {
                try {
                    BufferedImage original = ImageIO.read(imageUrl);
                    if (original != null) {
                        images[i] = scaleImage(original);
                    } else {
                        System.err.println("Failed to read image: " + path);
                    }
                } catch (IOException e) {
                    System.err.println("IOException while reading image: " + path);
                    e.printStackTrace();
                }
            }
        }
        return images;
    }

    private BufferedImage scaleImage(BufferedImage img) {
        Image tmp = img.getScaledInstance(TILE_SIZE, TILE_SIZE, Image.SCALE_SMOOTH);
        BufferedImage scaledImg = new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaledImg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return scaledImg;
    }

    public void setState(String newState) {
        if (!newState.equals(this.state)) {
            this.state = newState;
            this.animationFrame = 0;
            this.animationCounter = 0;
        }
        if (animations.containsKey(state)) {
            this.currentImage = animations.get(state)[animationFrame];
        }
    }

    public void update() {
        int animationSpeed = isMoving ? (isRunning ? Value.RUNNING_ANIMATION_SPEED : Value.WALKING_ANIMATION_SPEED) : Value.IDLE_ANIMATION_SPEED;
        animationCounter++;
        if (animationCounter >= animationSpeed) {
            animationCounter = 0;
            if (animations.containsKey(state)) {
                animationFrame = (animationFrame + 1) % animations.get(state).length;
                this.currentImage = animations.get(state)[animationFrame];
            }
        }
    }

    public void handleKeyPress(int keyCode) {
        int moveAmount = isRunning ? RUN_SPEED : WALK_SPEED;

        boolean newUpPressed = upPressed, newDownPressed = downPressed;
        boolean newLeftPressed = leftPressed, newRightPressed = rightPressed;

        if (keyCode == KeyEvent.VK_SPACE) spacePressed = true;
        if (keyCode == KeyEvent.VK_UP) newUpPressed = true;
        if (keyCode == KeyEvent.VK_DOWN) newDownPressed = true;
        if (keyCode == KeyEvent.VK_LEFT) newLeftPressed = true;
        if (keyCode == KeyEvent.VK_RIGHT) newRightPressed = true;

        if (newUpPressed == newDownPressed) newUpPressed = newDownPressed = false;
        if (newLeftPressed == newRightPressed) newLeftPressed = newRightPressed = false;

        if (newUpPressed)    {  y -= moveAmount; }
        if (newDownPressed)  {  y += moveAmount; }
        if (newLeftPressed)  {  x -= moveAmount; }
        if (newRightPressed) {  x += moveAmount; }

// Prepare the position array [x, y]
        int[] position = { x, y };

// Use the normal hitbox size for the player
        int playerHitboxSize = 48; // The size of the player's hitbox (48x48)

// Update player hitbox
        hitboxData.update(x, y, x, x + playerHitboxSize, y, y + playerHitboxSize);

// Check for collisions
        boolean canMove = tileSetting.isCollidable(position, hitboxData);
        if (canMove) {
            // If there is no collision, the new position has already been updated inside isCollidable
            x = position[0];
            y = position[1];
        }

        isMoving = newUpPressed || newDownPressed || newLeftPressed || newRightPressed;
        isRunning = spacePressed;
        setState(determineState(newUpPressed, newDownPressed, newLeftPressed, newRightPressed));
        upPressed = newUpPressed;
        downPressed = newDownPressed;
        leftPressed = newLeftPressed;
        rightPressed = newRightPressed;
    }

    String lastPressedDirection = "Down";
    public void handleKeyRelease(int keyCode) {
        if (keyCode == KeyEvent.VK_SPACE) {
            spacePressed = false;
            isRunning = false;
        } else if (keyCode == KeyEvent.VK_UP) {
            upPressed = false; lastPressedDirection = "Up";
        } else if (keyCode == KeyEvent.VK_DOWN) {
            downPressed = false; lastPressedDirection = "Down";
        } else if (keyCode == KeyEvent.VK_LEFT) {
            leftPressed = false; lastPressedDirection = "Left";
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            rightPressed = false; lastPressedDirection = "Right";
        }

        isMoving = upPressed || downPressed || leftPressed || rightPressed;

        if (!isMoving) setState("idle" + lastPressedDirection);
    }

    private String determineState(boolean upPressed, boolean downPressed, boolean leftPressed, boolean rightPressed) {
        if (upPressed) return isRunning ? "runningUp" : "walkingUp";
        if (downPressed) return isRunning ? "runningDown" : "walkingDown";
        if (leftPressed) return isRunning ? "runningLeft" : "walkingLeft";
        if (rightPressed) return isRunning ? "runningRight" : "walkingRight";
        return "idle"+lastPressedDirection;
    }

    public void paint(Graphics g) {
        if (currentImage != null) {
            g.drawImage(currentImage, x, y, null);
        } else {
            g.drawRect(x, y, TILE_SIZE, TILE_SIZE); // Draw a placeholder rectangle if the image is null
            System.err.println("Current image is null.");
        }
    }

    public int getX() { return x; }
    public int getY() { return y; }

}
