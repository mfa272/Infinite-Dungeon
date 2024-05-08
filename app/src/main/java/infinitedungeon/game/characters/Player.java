package infinitedungeon.game.characters;

import infinitedungeon.engine.graphics.Tileset;
import infinitedungeon.game.Square;
import infinitedungeon.game.items.Armor;
import infinitedungeon.game.items.Item;
import infinitedungeon.game.items.Sword;

public class Player extends Character {

    private final Item inventoryItems[];
    private Sword sword;
    private Armor armor;
    private int damageReduction;

    public Player(Tileset t, int healthBarWidth, int healthBarHeight) {
        super(t, healthBarWidth, healthBarHeight);
        inventoryItems = new Item[3];
    }

    public Item[] getInventoryItems() {
        return inventoryItems;
    }

    public Sword getSword() {
        return sword;
    }

    public Armor getArmor() {
        return armor;
    }

    public void equip(Sword s) {
        Sword bufferSword = s;
        for (int i = 0; i < 3; i++) {
            if (inventoryItems[i] == s) {
                inventoryItems[i] = getSword();
                sword = bufferSword;
            }
        }
    }

    public void equip(Armor a) {
        Armor bufferArmor = a;
        for (int i = 0; i < 3; i++) {
            if (inventoryItems[i] == a) {
                inventoryItems[i] = getArmor();
                armor = (bufferArmor);
            }
        }
    }

    public void destroyItem(Item it) {
        if (it == sword) {
            sword = null;
        } else if (it == armor) {
            armor = null;
        } else {
            for (int i = 0; i < 3; i++) {
                if (inventoryItems[i] == it) {
                    inventoryItems[i] = null;
                }
            }
        }
    }

    @Override
    public int getAttackDamage() {
        if (sword != null) {
            return super.getAttackDamage() + sword.getValue();
        }
        return super.getAttackDamage();
    }

    public int getDamageReduction() {
        if (armor != null) {
            return damageReduction + armor.getValue();
        }
        return damageReduction;
    }

    public boolean pickItem() {
        for (int i = 0; i < 3; i++) {
            Item it = getSquare().getFirstItem();
            if (inventoryItems[i] == null && it != null) {
                inventoryItems[i] = it;
                getSquare().pickFirstItem();
                return true;
            }
        }
        return false;
    }

    public boolean addItem(Item it) {
        for (int i = 0; i < 3; i++) {
            if (inventoryItems[i] == null) {
                inventoryItems[i] = it;
                return true;
            }
        }
        return false;
    }

    @Override
    public void takeAttack(int damage) {
        super.takeAttack((int) Math.max((double) (damage - getDamageReduction()), 0.0));
        if (armor != null) {
            armor.consume(1);
            if (armor.isExpired()) {
                armor = null;
            }
        }
    }

    @Override
    public void attack(Character target) {
        super.attack(target);
        if (sword != null) {
            sword.consume(1);
            if (sword.isExpired()) {
                sword = null;
            }
        }
    }

    @Override
    public void performAction() {
        switch (getDirection()) {
            case UP: {
                Square nextSquare = getSquare().getTop();
                if (nextSquare != null) {
                    if (nextSquare.isWalkable()) {
                        moveUp();
                    } else if (nextSquare.containsCharacter()) {
                        attack(getSquare().getTop().getCharacter());
                    }
                }
                break;
            }
            case DOWN: {
                Square nextSquare = getSquare().getBottom();
                if (nextSquare != null) {
                    if (nextSquare.isWalkable()) {
                        moveDown();
                    } else if (nextSquare.containsCharacter()) {
                        attack(getSquare().getBottom().getCharacter());
                    }
                }
                break;
            }
            case LEFT: {
                Square nextSquare = getSquare().getLeft();
                if (nextSquare != null) {
                    if (nextSquare.isWalkable()) {
                        moveLeft();
                    } else if (nextSquare.containsCharacter()) {
                        attack(getSquare().getLeft().getCharacter());
                    }
                }
                break;
            }
            case RIGHT: {
                Square nextSquare = getSquare().getRight();
                if (nextSquare != null) {
                    if (nextSquare.isWalkable()) {
                        moveRight();
                    } else if (nextSquare.containsCharacter()) {
                        attack(getSquare().getRight().getCharacter());
                    }
                }
                break;
            }
        }
    }
}
