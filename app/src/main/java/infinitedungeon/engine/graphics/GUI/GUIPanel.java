package infinitedungeon.engine.graphics.GUI;

import infinitedungeon.engine.MouseEventsHandler;
import infinitedungeon.engine.graphics.GameElement;
import infinitedungeon.engine.graphics.RgbImage;

public class GUIPanel extends GameElement {

    public GUIPanel(RgbImage image) {
        super.setMouseEventsHandler(new MouseEventsHandler() {
        });
        super.setEnabled(true);
        super.setVisible(true);
        super.setDrawImage(image);
    }

    public GUIPanel(int x, int y, RgbImage image) {
        this(image);
        super.setX(x);
        super.setY(y);
    }

    @Override
    public int getDrawX(int cameraOffset) {
        return getX();
    }

    @Override
    public int getDrawY(int cameraOffset) {
        return getY();
    }
}
