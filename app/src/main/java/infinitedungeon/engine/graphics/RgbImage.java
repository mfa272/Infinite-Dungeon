package infinitedungeon.engine.graphics;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.ImageIO;

import infinitedungeon.engine.Game;

public class RgbImage {

    private final int[] rgb;
    private final int width;
    private final int height;

    public RgbImage(String path) throws IOException {
        BufferedImage im = ImageIO.read(new File(path));
        width = im.getWidth();
        height = im.getHeight();
        rgb = im.getRGB(0, 0, width, height, null, 0, width);
        im.flush();
    }

    public RgbImage(int[] rgb, int width, int height) {
        this.width = width;
        this.height = height;
        this.rgb = rgb;
    }

    public RgbImage(int color, int width, int height) {
        this.width = width;
        this.height = height;
        this.rgb = new int[width * height];
        Arrays.fill(rgb, color);
    }

    public RgbImage(int width, int height) {
        this(Game.EMPTY_PIXEL, width, height);
    }

    public int[] getRgb() {
        return rgb;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public RgbImage clone() {
        return new RgbImage(Arrays.copyOf(rgb, rgb.length), width, height);
    }

    public void combine(RgbImage image, int offX, int offY, int startX, int startY, int endX, int endY) {
        int imageWidth = image.getWidth();
        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                setPixel(x + offX - startX, y + offY - startY, image.getRgb()[x + y * imageWidth]);
            }
        }
    }

    public void combine(RgbImage image, int offX, int offY) {
        combine(image, offX, offY, 0, 0, image.getWidth(), image.getHeight());
    }

    public void setPixel(int x, int y, int value) {
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight() || value == 0xffff00ff) {
            return;
        }
        rgb[x + y * getWidth()] = value;
    }
}
