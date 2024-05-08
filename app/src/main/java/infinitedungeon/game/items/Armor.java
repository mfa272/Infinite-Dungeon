package infinitedungeon.game.items;

import infinitedungeon.game.InfiniteDungeon;
import infinitedungeon.game.characters.Player;

public class Armor extends Item {
    public Armor(int value, int remainingUses) {
        super(value, remainingUses, InfiniteDungeon.ARMOR_IMAGE);
    }

    @Override
    public void use(Player p) {
        p.equip(this);
    }

    @Override
    public String toString() {
        return "DEF: " + getValue() + " " + getRemainingUses() + "/" + getUses();
    }
}
