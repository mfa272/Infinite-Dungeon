package infinitedungeon.engine.graphics.GUI;

import infinitedungeon.engine.graphics.Font;
import infinitedungeon.engine.graphics.RgbImage;

public class GUIMessage extends GUIPanel {

    private RgbImage backgroundImage;
    private String text;
    private Font f;
    private String separator;
    private boolean centered;
    private int textOffX;
    private int textOffY;
    private int rowSize;

    public GUIMessage(int x, int y, RgbImage image) {
        super(x, y, image);
        backgroundImage = image;
    }

    public String getText() {
        return text;
    }

    public RgbImage getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(RgbImage backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public Font getFont() {
        return f;
    }

    public void setFont(Font f) {
        this.f = f;
        if (backgroundImage != null) {
            setDrawImage(backgroundImage);
        }
    }

    public RgbImage getBackgoundImage() {
        return backgroundImage;
    }

    @Override
    public void setDrawImage(RgbImage image) {
        backgroundImage = image;
        if (text != null && f != null) {
            if (centered) {
                setText(text, rowSize, separator);
                return;
            } else {
                setText(text, rowSize, separator, textOffX, textOffY);
                return;
            }
        }
        super.setDrawImage(image);
    }

    public void setText(String text, int rowSize, String wordSeparator, int offX, int offY) {
        if (f == null) {
            throw new NullPointerException("Font can't be null");
        }
        separator = wordSeparator;
        this.text = text;
        this.rowSize = rowSize;
        centered = false;
        textOffX = offX;
        textOffY = offY;
        RgbImage im;
        if (backgroundImage != null) {
            im = backgroundImage.clone();
            f.writeOnImage(text, rowSize, separator, offX, offY, im);
            super.setDrawImage(im);
        } else {
            super.setDrawImage(f.getImageString(text, rowSize, separator));
        }
    }

    public void setText(String text, int rowSize, String wordSeparator) {
        if (f == null) {
            throw new NullPointerException("Font can't be null");
        }
        separator = wordSeparator;
        this.text = text;
        this.rowSize = rowSize;
        centered = true;
        RgbImage im;
        if (backgroundImage != null) {
            im = backgroundImage.clone();
            f.writeOnImage(text, rowSize, separator, im);
            super.setDrawImage(im);
        } else {
            super.setDrawImage(f.getImageString(text, rowSize, separator));
        }
    }

    public void deleteText() {
        text = null;
        setDrawImage(backgroundImage);
    }
}
