package infinitedungeon.game.items;

import infinitedungeon.engine.graphics.RgbImage;
import infinitedungeon.engine.graphics.Sprite;
import infinitedungeon.game.Square;
import infinitedungeon.game.characters.Player;

public abstract class Item extends Sprite {

    private Square sq;
    private int remainingUses;
    private int uses;

    public int getUses() {
        return uses;
    }

    private int value;
    private boolean expired;
    private boolean picked;

    public boolean isPicked() {
        return picked;
    }

    public void setPicked(boolean picked) {
        this.picked = picked;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public int getRemainingUses() {
        return remainingUses;
    }

    public Item(int value, int uses, RgbImage image) {
        super(image);
        this.uses = uses;
        this.value = value;
        this.remainingUses = uses;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Square getSquare() {
        return sq;
    }

    public void setSquare(Square sq) {
        this.sq = sq;
    }

    public Item(RgbImage image) {
        super(image);
    }

    public void consume(int uses) {
        remainingUses -= uses;
        if (remainingUses <= 0) {
            setExpired(true);
        }
    }

    public abstract void use(Player p);
}
