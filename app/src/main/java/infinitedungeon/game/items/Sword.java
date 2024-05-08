package infinitedungeon.game.items;

import infinitedungeon.game.InfiniteDungeon;
import infinitedungeon.game.characters.Player;

public class Sword extends Item {

    public Sword(int value, int remainingUses) {
        super(value, remainingUses, InfiniteDungeon.SWORD_IMAGE);
    }

    @Override
    public void use(Player p) {
        p.equip(this);
    }

    @Override
    public String toString() {
        return "ATK: " + getValue() + " " + getRemainingUses() + "/" + getUses();
    }
}
