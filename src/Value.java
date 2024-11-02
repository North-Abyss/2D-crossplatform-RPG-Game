

public class Value {

    // Default tile size and screen dimensions
    public static int TileSize = 48; // Default tile size in pixels
    public static final int NumTilex = 16; // Number of tiles horizontally based on screen width
    public static final int NumTiley = 12; // Number of tiles vertically based on screen height

    public static int waterFrameDurationMs=800;

    public static final int WALK_SPEED = 2 ; // Slow walking speed (to match tile movement)
    public static final int RUN_SPEED = 8 ; // Faster running speed
    public static final int IDLE_ANIMATION_SPEED  = 20 ; // Animation speed for idle (higher = slower)
    public static final int WALKING_ANIMATION_SPEED = 10 ; // Animation speed for walking
    public static final int RUNNING_ANIMATION_SPEED = 8 ;// Animation speed for running (higher = faster)

    static String[] tilesheetPaths = {"Map/Land/land.png", "Map/Land/grass.png",};
    static String[] waterAnimationPaths = {"Map/Land/Water1.png","Map/Land/Water2.png","Map/Land/Water3.png","Map/Land/Water4.png"};
    static String[] fencesheetPaths = {"Map/Fences/fb11.png", "Map/Fences/fb12.png","Map/Fences/fb13.png",
            "Map/Fences/fb21.png","Map/Fences/fb22.png","Map/Fences/fb23.png",
            "Map/Fences/fb31.png","Map/Fences/fb32.png","Map/Fences/fb33.png",
            "Map/Fences/fc1.png","Map/Fences/fc2.png","Map/Fences/fc3.png",
            "Map/Fences/fr1.png","Map/Fences/fr2.png","Map/Fences/fr3.png",
            "Map/Fences/fs.png",};

    static String backgroundLayerPath = "Map/layer1.txt";
    static String collisionLayerPath = "Map/layer2.txt";

    // Resource strings for image paths
    static final String BACKGROUND_IMAGE_PATH = "/Menu/bgmainmenu.jpg";
    static final String BUTTON_IMAGE_PATH = "/Menu/Button.png";

    static int[] anitile ={12};
    public static int waterID =12;

}





