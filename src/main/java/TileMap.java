import org.mapeditor.core.Map;
import org.mapeditor.core.Tile;
import org.mapeditor.core.TileLayer;
import org.mapeditor.io.TMXMapReader;

import java.awt.geom.Rectangle2D;

import static org.lwjgl.opengl.GL11.*;

public class TileMap {

    private Map map;
    private Texture tilesetTexture;
    private final boolean solid;

    public TileMap(String path, boolean isSolid) {
        this.solid = isSolid;
        try {
            TMXMapReader mapReader = new TMXMapReader();
            map = mapReader.readMap(path);

            this.tilesetTexture = new Texture("src/main/resources/assets/images/Textures-16.png");

        } catch (Exception e) {
            throw new IllegalStateException("Failed to load TMX map: " + path, e);

        }
    }

    public void removeTile(int x, int y) {
        for (int l = 0; l < map.getLayerCount(); l++) {
            if (map.getLayer(l) instanceof TileLayer layer) {
                layer.setTileAt(x, y, null);
            }
        }
    }

    public void render(Camera camera) {

        glEnable(GL_TEXTURE_2D);
        tilesetTexture.bind();

        int tileW = map.getTileWidth();
        int tileH = map.getTileHeight();

        for (int l = 0; l < map.getLayerCount(); l++) {
            if (map.getLayer(l) instanceof TileLayer layer) {

                int startX = 0;
                int startY = 0;
                int endX = map.getWidth();
                int endY = map.getHeight();

                if (camera != null) {
                    startX = Math.max(0, (int) Math.floor(camera.getX() / tileW) - 1);
                    startY = Math.max(0, (int) Math.floor(camera.getY() / tileH) - 1);
                    endX = Math.min(map.getWidth(), (int) Math.ceil((camera.getX() + camera.getViewportWidth()) / tileW) + 1);
                    endY = Math.min(map.getHeight(), (int) Math.ceil((camera.getY() + camera.getViewportHeight()) / tileH) + 1);
                }

                for (int y = startY; y < endY; y++) {
                    for (int x = startX; x < endX; x++) {
                        Tile tile = layer.getTileAt(x, y);
                        if (tile != null) {
                            drawTile(tile, x * tileW, y * tileH);
                        }
                    }
                }
            }
        }

        tilesetTexture.unbind();
    }
    private void drawTile(Tile tile, float worldX, float worldY) {
        int tileId = tile.getId(); // This is the tile ID, which you can use to determine which part of the tileset image to draw

        // Assuming you are using the first tileset in the list
        int imageWidth = tilesetTexture.getWidth();
        int imageHeight = tilesetTexture.getHeight();
        int tileWidth = map.getTileWidth();
        int tileHeight = map.getTileHeight(); // Make sure to get the height!

        int columns = imageWidth / tileWidth;
        int tileColumn = tileId % columns;
        int tileRow = tileId / columns;

        float u1 = (float) (tileColumn * tileWidth) / imageWidth;
        float v1 = (float) (tileRow * tileHeight) / imageHeight;
        float u2 = (float) ((tileColumn + 1) * tileWidth) / imageWidth;
        float v2 = (float) ((tileRow + 1) * tileHeight) / imageHeight;

        glBegin(GL_QUADS);
        glTexCoord2f(u1, v1); glVertex2f(worldX, worldY);
        glTexCoord2f(u2, v1); glVertex2f(worldX + tileWidth, worldY);
        glTexCoord2f(u2, v2); glVertex2f(worldX + tileWidth, worldY + tileHeight);
        glTexCoord2f(u1, v2); glVertex2f(worldX, worldY + tileHeight);
        glEnd();
    }

    public boolean isSolidTile(Rectangle2D.Float hitbox) {

        if (!solid) {
            return false;
        }

        int tileW = map.getTileWidth();
        int tileH = map.getTileHeight();

        // Calculate which tiles the hitbox overlaps
        int startX = (int) Math.floor(hitbox.x / tileW);
        int startY = (int) Math.floor(hitbox.y / tileH);
        int endX = (int) Math.floor((hitbox.x + hitbox.width) / tileW);
        int endY = (int) Math.floor((hitbox.y + hitbox.height) / tileH);

        // Ensure we stay within map bounds
        startX = Math.max(0, startX);
        startY = Math.max(0, startY);
        endX = Math.min(map.getWidth() - 1, endX);
        endY = Math.min(map.getHeight() - 1, endY);

        // Check each overlapping tile
        for (int l = 0; l < map.getLayerCount(); l++) {

            if (map.getLayer(l) instanceof TileLayer layer) {

                for (int y = startY; y <= endY; y++) {

                    for (int x = startX; x <= endX; x++) {
                        Tile tile = layer.getTileAt(x, y);

                        if (tile != null && !tile.getProperties().isEmpty() && tile.getProperties().getProperty("solid").equals("true")) {

                            Rectangle2D.Float tileRect = new Rectangle2D.Float(x * tileW, y * tileH, tileW, tileH);

                            if (hitbox.intersects(tileRect)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    // Returns the actual rectangle of the block we hit, or null if the path is clear
    public Rectangle2D.Float getCollidingTile(Rectangle2D.Float hitbox) {

        if (!solid) {
            return null;
        }

        int tileW = map.getTileWidth();
        int tileH = map.getTileHeight();

        int startX = (int) Math.floor(hitbox.x / tileW);
        int startY = (int) Math.floor(hitbox.y / tileH);
        int endX = (int) Math.floor((hitbox.x + hitbox.width) / tileW);
        int endY = (int) Math.floor((hitbox.y + hitbox.height) / tileH);

        startX = Math.max(0, startX);
        startY = Math.max(0, startY);
        endX = Math.min(map.getWidth() - 1, endX);
        endY = Math.min(map.getHeight() - 1, endY);

        for (int l = 0; l < map.getLayerCount(); l++) {
            if (map.getLayer(l) instanceof TileLayer layer) {
                for (int y = startY; y <= endY; y++) {
                    for (int x = startX; x <= endX; x++) {
                        Tile tile = layer.getTileAt(x, y);

                        if (tile != null && !tile.getProperties().isEmpty() && tile.getProperties().getProperty("solid").equals("true")) {
                            Rectangle2D.Float tileRect = new Rectangle2D.Float(x * tileW, y * tileH, tileW, tileH);

                            if (hitbox.intersects(tileRect)) {
                                return tileRect; // Found a wall! Return its exact coordinates.
                            }
                        }
                    }
                }
            }
        }
        return null; // No collision detected
    }
}



