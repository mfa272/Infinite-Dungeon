package infinitedungeon.game.characters;

import infinitedungeon.engine.graphics.Tileset;
import infinitedungeon.game.items.Item;

public class Monster extends Character {

    private final static int HEALTH_BAR_HEIGHT = 5;
    private Item dropItem;
    private Player enemy;

    public Monster(Tileset t, Player enemy) {
        super(t, t.getTileWidth(), HEALTH_BAR_HEIGHT);
        this.enemy = enemy;
        super.attachSprite(new Attachment(super.getHealthBar(), 0,
                -(HEALTH_BAR_HEIGHT + 1)));
    }

    public Item getDropItem() {
        return dropItem;
    }

    public void setDropItem(Item dropItem) {
        this.dropItem = dropItem;
    }

    @Override
    public void performAction() {
        if (enemy.getSquare().getRoom() == getSquare().getRoom()) {
            if (getSquare().getRight().contains(enemy)) {
                setDirection(Character.Direction.RIGHT);
                attack(enemy);
            } else if (getSquare().getLeft().contains(enemy)) {
                setDirection(Character.Direction.LEFT);
                attack(enemy);
            } else if (getSquare().getBottom().contains(enemy)) {
                setDirection(Character.Direction.DOWN);
                attack(enemy);
            } else if (getSquare().getTop().contains(enemy)) {
                setDirection(Character.Direction.UP);
                attack(enemy);
            } else if (enemy.getMapX() < getMapX() && getSquare().getLeft().isWalkable()) {
                setDirection(Character.Direction.LEFT);
                moveLeft();
            } else if (enemy.getMapX() > getMapX() && getSquare().getRight().isWalkable()) {
                setDirection(Character.Direction.RIGHT);
                moveRight();
            } else if (enemy.getMapY() < getMapY() && getSquare().getTop().isWalkable()) {
                setDirection(Character.Direction.UP);
                moveUp();
            } else if (enemy.getMapY() > getMapY() && getSquare().getBottom().isWalkable()) {
                setDirection(Character.Direction.DOWN);
                moveDown();
            }
        }
    }
}
