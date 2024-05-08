package infinitedungeon.game.characters;

import infinitedungeon.engine.graphics.RgbImage;

public class HealthBar extends RgbImage {

    public HealthBar(int width, int height) {
        super(0x00FF00, width, height);
    }

    public void update(int percentHealth) {
        if (percentHealth > 100) {
            percentHealth = 100;
        } else if (percentHealth < 0) {
            percentHealth = 0;
        }
        int missingHealthWidth = (int) (((double) getWidth() / (double) 100) *
                ((double) 100 - (double) percentHealth));
        combine(new RgbImage(0x00FF00,
                getWidth() - missingHealthWidth, getHeight()), 0, 0);
        if (missingHealthWidth > 0) {
            RgbImage missingHealth = new RgbImage(0xFF0000,
                    (int) missingHealthWidth, getHeight());
            combine(missingHealth, getWidth() - missingHealth.getWidth(), 0);
        }
    }
}
