package infinitedungeon.engine;

import infinitedungeon.engine.graphics.Tileset;

public class World {

    private Tileset tileset;
    private final int[][] tiles;

    public static World loadFromString(String data) {
        String[] rowsData = data.split(":");
        int columns = Integer.parseInt(rowsData[0].split(" ")[0]);
        int rows = Integer.parseInt((rowsData[0].split(" "))[1]);
        int[][] tiles = new int[columns][rows];
        for (int i = 0; i < rows; i++) {
            String[] currentRow = rowsData[i + 1].split(" ");
            for (int j = 0; j < columns; j++) {
                tiles[j][i] = Integer.parseInt(currentRow[j]);
            }
        }
        return new World(tiles);
    }

    public World(int columns, int rows) {
        tiles = new int[columns][rows];
    }

    public World(int[][] tiles) {
        this.tiles = tiles;
    }

    public int getWidth() {
        return tiles.length;
    }

    public int getHeight() {
        return tiles[0].length;
    }

    public void setTile(int column, int row, int value) {
        tiles[column][row] = value;
    }

    public int getTileWidth() {
        return tileset.getTileWidth();
    }

    public int getTileHeight() {
        return tileset.getTileHeight();
    }

    public int getTile(int column, int row) {
        return tiles[column][row];
    }

    public void setTileset(Tileset tileset) {
        this.tileset = tileset;
    }

    public Tileset getTileset() {
        return tileset;
    }
}
