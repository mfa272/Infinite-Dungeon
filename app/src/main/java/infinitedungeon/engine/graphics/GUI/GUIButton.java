package infinitedungeon.engine.graphics.GUI;

import infinitedungeon.engine.MouseEventsHandler;
import infinitedungeon.engine.graphics.RgbImage;

public class GUIButton extends GUIMessage {

    private RgbImage baseImage;
    private RgbImage pressedImage;
    private RgbImage disabledImage;
    private Runnable t;

    public GUIButton(int x, int y, RgbImage baseImage) {
        super(x, y, baseImage);
        this.baseImage = baseImage;
        super.setMouseEventsHandler(new MouseEventsHandler() {
            @Override
            public void mouseButton1Clicked() {
                t.run();
            }

            @Override
            public void mouseButton1Pressed() {
                if (pressedImage != null) {
                    setDrawImage(pressedImage);
                }
            }

            @Override
            public void mouseButton1Released() {
                if (baseImage != null) {
                    setDrawImage(baseImage);
                }
            }
        });
    }

    public RgbImage getBaseImage() {
        return baseImage;
    }

    public void setBaseImage(RgbImage baseImage) {
        this.baseImage = baseImage;
    }

    public RgbImage getPressedImage() {
        return pressedImage;
    }

    public void setPressedImage(RgbImage pressedImage) {
        this.pressedImage = pressedImage;
    }

    public RgbImage getDisabledImage() {
        return disabledImage;
    }

    public void setDisabledImage(RgbImage disabledImage) {
        this.disabledImage = disabledImage;
    }

    public void setTask(Runnable t) {
        this.t = t;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled == true && baseImage != null) {
            setDrawImage(baseImage);
        } else if (enabled == false && disabledImage != null) {
            setDrawImage(disabledImage);
        }
    }
}
