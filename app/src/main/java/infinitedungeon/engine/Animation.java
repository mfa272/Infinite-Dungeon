package infinitedungeon.engine;

import infinitedungeon.engine.graphics.Sprite;
import infinitedungeon.engine.graphics.Tileset;

public class Animation extends TimedTask {

    private final Tileset tileset;
    private final Sprite gm;
    private final boolean repeat;
    private final long millis;
    private int current;
    private boolean stop;

    public Animation(Tileset t, long delay, long millis, Sprite elem, boolean repeat) {
        super(delay);
        this.millis = millis;
        this.repeat = repeat;
        tileset = t;
        gm = elem;
        current = 0;
    }

    public void stop() {
        stop = true;
    }

    @Override
    public void run() {
        if (!stop) {
            setMilliseconds(millis);
            gm.setDrawImage(tileset.getTile(current++));
            if (current < tileset.getLenght()) {
                setToReschedule(true);
            } else {
                setToReschedule(false);
            }
            if (!toReschedule() && repeat) {
                current = 0;
                setToReschedule(true);
            }
        } else {
            setToReschedule(false);
        }
    }
}
