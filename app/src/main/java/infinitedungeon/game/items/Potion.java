package infinitedungeon.game.items;

import infinitedungeon.game.InfiniteDungeon;
import infinitedungeon.game.characters.Player;

public class Potion extends Item {

    public Potion(int value) {
        super(value, value, InfiniteDungeon.POTION_IMAGE);
    }

    @Override
    public void consume(int uses) {
        super.consume(uses);
        setValue(getValue() - uses);
    }

    @Override
    public void use(Player p) {
        int potionPower = getValue();
        consume(Math.min(getValue(), p.getMaxHealth() - p.getHealth()));
        p.heal(potionPower);
    }

    @Override
    public String toString() {
        return "+" + getValue() + " HP";
    }
}
