public class World {

    public static final int GRID_SIZE = 16;

    private static World instance;
    public TileMap groundMap = new TileMap("src/main/resources/assets/tiles/groundtiles.tmx", true);
    public TileMap infinite = new TileMap("src/main/resources/assets/tiles/infinite.tmx", true);
    public TileMap temporary = infinite;

    public static World getInstance() {
        return instance;
    }

    public static void setInstance(World instance) {
        World.instance = instance;
    }




}
