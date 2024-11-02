import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class MainMenu extends JFrame {
    private static final int BUTTON_WIDTH = 200; // Button width
    private static final int BUTTON_HEIGHT = 70; // Button height

    public MainMenu() {
        setTitle("Game Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false); // Prevent resizing

        // Set the preferred size based on the tile values
        // Only use half the vertical tiles
        setSize(Value.NumTilex * Value.TileSize, Value.NumTiley * Value.TileSize);
        setLocationRelativeTo(null);

        // Create a main panel with a null layout for absolute positioning
        BackgroundPanel panel = new BackgroundPanel(Value.BACKGROUND_IMAGE_PATH);
        panel.setLayout(null); // Allow absolute positioning

        // Create custom buttons with images and animations
        JButton singlePlayerButton = createImageButton("Single Player");
        JButton multiplayerButton = createImageButton("Multiplayer");
        JButton settingsButton = createImageButton("Settings");
        JButton exitButton = createImageButton("Exit");

        // Calculate button positions for the bottom half of the screen
        int panelHeight = getHeight();
        int buttonY1 = (int) (panelHeight * 0.4); // Position the first row of buttons (40% down)
        int buttonY2 = (int) (panelHeight * 0.55); // Position the second row of buttons (55% down)

        // Set button bounds for a 2x2 layout in the bottom half
        int buttonX = (getWidth() - BUTTON_WIDTH) / 2; // Center X position
        singlePlayerButton.setBounds(buttonX - BUTTON_WIDTH - 10, buttonY1, BUTTON_WIDTH, BUTTON_HEIGHT); // Left
        multiplayerButton.setBounds(buttonX + 10, buttonY1, BUTTON_WIDTH, BUTTON_HEIGHT); // Right
        settingsButton.setBounds(buttonX - BUTTON_WIDTH - 10, buttonY2, BUTTON_WIDTH, BUTTON_HEIGHT); // Left
        exitButton.setBounds(buttonX + 10, buttonY2, BUTTON_WIDTH, BUTTON_HEIGHT); // Right

        // Add action listeners for each button
        singlePlayerButton.addActionListener(_ -> startSinglePlayer());
        multiplayerButton.addActionListener(_ -> startMultiplayer());
        settingsButton.addActionListener(_ -> showSettings());
        exitButton.addActionListener(_ -> System.exit(0));

        // Add buttons to the panel
        panel.add(singlePlayerButton);
        panel.add(multiplayerButton);
        panel.add(settingsButton);
        panel.add(exitButton);

        add(panel); // Add the background panel to the frame
    }

    private JButton createImageButton(String buttonText) {
        JButton button = new JButton(buttonText);
        BufferedImage img = loadImage(Value.BUTTON_IMAGE_PATH);
        if (img != null) {
            button.setIcon(new ImageIcon(img.getScaledInstance(BUTTON_WIDTH, BUTTON_HEIGHT, Image.SCALE_SMOOTH)));
        }

        button.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT)); // Set preferred size
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setFont(new Font("Arial", Font.PLAIN, 16)); // Set font for button text
        button.setForeground(Color.BLACK); // Change text color to black

        // Click animation: adds a semi-transparent overlay on click
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Create a black overlay covering the button image
                BufferedImage overlayImage = new BufferedImage(BUTTON_WIDTH, BUTTON_HEIGHT, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = overlayImage.createGraphics();
                g2d.setColor(new Color(0, 0, 0, 128)); // 50% transparent black overlay
                g2d.fillRect(0, 0, BUTTON_WIDTH, BUTTON_HEIGHT);
                g2d.dispose();
                button.setIcon(new ImageIcon(overlayImage)); // Set the overlay as the button icon
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (img != null) {
                    button.setIcon(new ImageIcon(img.getScaledInstance(BUTTON_WIDTH, BUTTON_HEIGHT, Image.SCALE_SMOOTH))); // Restore the original icon
                }
                button.repaint(); // Remove overlay when mouse is released
            }
        });

        return button;
    }

    private BufferedImage loadImage(String path) {
        URL imageUrl = getClass().getResource(path);
        try {
            if (imageUrl == null) {
                System.err.println("Image not found: " + path);
                return createDefaultImage(); // Create a default image if the original is not found
            }
            return ImageIO.read(imageUrl);
        } catch (IOException e) {
            System.err.println("IOException while reading image: " + path);
            e.printStackTrace();
            return createDefaultImage(); // Return a default image in case of an error
        }
    }

    private BufferedImage createDefaultImage() {
        // Create a simple default image (e.g., a plain white image)
        BufferedImage img = new BufferedImage(100, 50, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        g.setColor(Color.BLACK);
        g.drawString("Image Not Found", 10, 20);
        g.dispose();
        return img;
    }

    private void startSinglePlayer() {
        System.out.println("Starting Single Player Game...");
        dispose(); // Close the main menu
        try {
            JFrame frame = new JFrame("Abyss");
            GamePanel gamePanel = new GamePanel();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false); // Prevent resizing
            frame.setSize(gamePanel.getPreferredSize());
            frame.setLocationRelativeTo(null);
            frame.setContentPane(gamePanel);
            frame.pack();
            frame.setVisible(true);
        } catch (Exception e) {
            System.err.println("An error occurred while running the game: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void startMultiplayer() {
        System.out.println("Connecting to Multiplayer...");
        dispose();
        new GameClient("your_server_ip", 12345);  // Connect to the server
    }

    private void showSettings() {
        System.out.println("Opening Settings...");
        // This can be implemented as a new settings window or dialog
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenu menu = new MainMenu();
            menu.setVisible(true);
        });
    }

    // Background panel to load and display the background image
    private class BackgroundPanel extends JPanel {
        private final BufferedImage backgroundImage;

        public BackgroundPanel(String imagePath) {
            backgroundImage = loadImage(imagePath);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }

            // Draw game name "Abyss" with an outline
            drawOutlinedText(g, "Abyss", getWidth() / 3, getHeight() / 8, new Font("Arial", Font.BOLD, 72), Color.WHITE, Color.BLACK);
        }

        private void drawOutlinedText(Graphics g, String text, int x, int y, Font font, Color outlineColor, Color textColor) {
            g.setFont(font);
            // Draw the outline
            g.setColor(outlineColor);
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx != 0 || dy != 0) {
                        g.drawString(text, x + dx, y + dy);
                    }
                }
            }
            // Draw the text
            g.setColor(textColor);
            g.drawString(text, x, y);
        }
    }
}
