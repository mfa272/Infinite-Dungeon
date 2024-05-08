package infinitedungeon.engine.graphics;

import infinitedungeon.engine.MouseEventsHandler;

public class Sprite extends GameElement {

    public Sprite(RgbImage image) {
        super.setMouseEventsHandler(new MouseEventsHandler() {
        });
        super.setEnabled(true);
        super.setVisible(true);
        super.setDrawImage(image);
    }

    public Sprite(int x, int y, RgbImage image) {
        this(image);
        super.setX(x);
        super.setY(y);
    }

    @Override
    public int getDrawX(int cameraOffset) {
        return getX() - cameraOffset;
    }

    @Override
    public int getDrawY(int cameraOffset) {
        return getY() - cameraOffset;
    }
}
