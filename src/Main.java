import javax.swing.*;

// The entry point of the application
public class Main {

    public static void main () {
        // Ensure that GUI updates are performed on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                // initializing the repair system
                RepairSystem.synchronizeLayers(Value.backgroundLayerPath, Value.collisionLayerPath);
                // Create the main application frame
                JFrame frame = new JFrame("Abyss");

                // Create an instance of the game panel
                GamePanel gamePanel = new GamePanel();

                // Set up the frame
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setResizable(false); // Prevent resizing
                frame.setSize(gamePanel.getPreferredSize());
                frame.setLocationRelativeTo(null);
                frame.setContentPane(gamePanel);
                frame.pack(); // Adjusts frame size to fit content
                frame.setVisible(true);

            } catch (Exception e) {
                // Print the error message if something goes wrong
                System.err.println("An error occurred while running the game: " + e.getMessage());
                e.printStackTrace(); // Prints the full stack trace for debugging
            }
        });

    }
}
