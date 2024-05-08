package infinitedungeon.engine.graphics;

import infinitedungeon.engine.MouseEventsHandler;

public abstract class GameElement {

    private boolean enabled;
    private boolean visible;
    private boolean hovered;
    private RgbImage image;
    private int x;
    private int y;
    private MouseEventsHandler mev;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public MouseEventsHandler getMouseEventsHandler() {
        return mev;
    }

    public void setMouseEventsHandler(MouseEventsHandler mev) {
        this.mev = mev;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean value) {
        visible = value;
    }

    public RgbImage getDrawImage() {
        return image;
    }

    public void setDrawImage(RgbImage image) {
        this.image = image;
    }

    public void setEnabled(boolean value) {
        enabled = value;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isHovered() {
        return hovered;
    }

    public boolean updateMouseHoverStatus(int mouseX, int mouseY, int cameraX, int cameraY) {
        int drawX = getDrawX(cameraX);
        int drawY = getDrawY(cameraY);
        if (mouseX >= drawX && mouseX <= image.getWidth() + drawX && mouseY >= drawY
                && mouseY <= image.getHeight() + drawY) {
            if (!hovered) {
                hovered = true;
                return true;
            } else {
                return false;
            }
        }
        if (hovered) {
            hovered = false;
            return true;
        } else {
            return false;
        }
    }

    public boolean unhover() {
        if (isHovered()) {
            hovered = false;
            return true;
        }
        return false;
    }

    public abstract int getDrawX(int cameraOffset);

    public abstract int getDrawY(int cameraOffset);
}
