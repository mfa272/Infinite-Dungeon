package infinitedungeon.engine.graphics;

import java.io.IOException;
import java.util.Arrays;

public class Tileset {

    private final int tileWidth;
    private final int tileHeight;
    private final RgbImage[] tiles;

    public Tileset(String path, int tileWidth, int tileHeight) throws IOException {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        int n;
        RgbImage im = new RgbImage(path);
        double doubleN = ((double) im.getWidth() * (double) im.getHeight())
                / ((double) tileWidth * (double) tileHeight);
        if (doubleN != Math.floor(doubleN)) {
            throw new IllegalArgumentException("Wrong size");
        } else {
            n = (int) doubleN;
        }
        tiles = new RgbImage[n];
        int columns = im.getWidth() / tileWidth;
        for (int k = 0; k < n; k++) {
            RgbImage tile = new RgbImage(tileWidth, tileHeight);
            int x = k;
            int y = 0;
            while (x >= columns) {
                x -= columns;
                y++;
            }
            tile.combine(im, 0, 0, x * tileWidth, y * tileHeight, (x + 1) * tileWidth, (y + 1) * tileHeight);
            tiles[k] = tile;
        }
    }

    public Tileset(RgbImage[] tiles) {
        this.tileWidth = tiles[0].getWidth();
        this.tileHeight = tiles[0].getHeight();
        for (RgbImage t : tiles) {
            if (t.getWidth() != tileWidth || t.getHeight() != tileHeight) {
                throw new IllegalArgumentException("Tile sizes differ");
            }
        }
        this.tiles = Arrays.copyOf(tiles, tiles.length);
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public int getLenght() {
        return tiles.length;
    }

    public RgbImage getTile(int index) {
        return tiles[index];
    }

    public Tileset getSubTileset(int[] indexes) {
        RgbImage[] newTiles = new RgbImage[indexes.length];
        for (int i = 0; i < indexes.length; i++) {
            newTiles[i] = tiles[indexes[i]];
        }
        return new Tileset(newTiles);
    }
}
