package infinitedungeon.game.characters;

import infinitedungeon.engine.graphics.RgbImage;
import infinitedungeon.engine.graphics.Sprite;

public class Attachment extends Sprite {
    private int offsetX;
    private int offsetY;

    public Attachment(RgbImage image, int offsetX, int offsetY) {
        super(image);
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    @Override
    public void setX(int x) {
        super.setX(x + offsetX);
    }

    @Override
    public void setY(int y) {
        super.setY(y + offsetY);
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }
}
