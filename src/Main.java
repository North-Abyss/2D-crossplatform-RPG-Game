import javax.swing.*;

public class Main {
    public static void main() {
        // Ensure that GUI updates are performed on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                // Initializing the repair system
                RepairSystem.synchronizeLayers(Value.backgroundLayerPath, Value.collisionLayerPath);

                // Display the main menu
                MainMenu menu = new MainMenu();
                menu.setVisible(true);

            } catch (Exception e) {
                System.err.println("An error occurred while running the game: " + e.getMessage());
                e.printStackTrace(); // Prints the full stack trace for debugging
            }
        });
    }
}
