import java.io.*;
import java.net.*;

public class GameClient {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public GameClient(String serverAddress, int port) {

        try {
            socket = new Socket(serverAddress, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Thread to listen for server messages
            new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        System.out.println("Received: " + message);
                        // Parse and handle received position data
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendPosition(String position) {
        out.println(position);
    }

    public static void main(String[] args) {
        GameClient client = new GameClient("your_server_ip", 12345);
        // Example of sending position data
        client.sendPosition("Player1: x=10, y=20");
    }

}
