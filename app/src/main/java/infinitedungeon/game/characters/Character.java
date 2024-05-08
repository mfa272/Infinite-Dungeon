package infinitedungeon.game.characters;

import java.util.ArrayList;

import infinitedungeon.engine.TimedTask;
import infinitedungeon.engine.graphics.Sprite;
import infinitedungeon.engine.graphics.Tileset;
import infinitedungeon.game.InfiniteDungeon;
import infinitedungeon.game.Square;

public abstract class Character extends Sprite {

    private final static long WALK_TIME = 500;
    private final static long ATTACK_TIME = 250;
    private final static int[] WALK_RIGHT_TILES = { 6, 7, 8, 7 };
    private final static int[] WALK_UP_TILES = { 9, 10, 11, 10 };
    private final static int[] WALK_LEFT_TILES = { 3, 4, 5, 4 };
    private final static int[] WALK_DOWN_TILES = { 0, 1, 2, 1 };

    private Tileset t;
    private int mapX;
    private int mapY;
    private int health;
    private int maxHealth;
    private Square sq;
    private int attackDamage;
    private boolean walking;
    private boolean attacking;
    private Direction d;
    private HealthBar h;
    private final ArrayList<Attachment> attachedSprites;

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    public Character(Tileset t, int healthBarWidth, int healthBarHeight) {
        super(-1 * (InfiniteDungeon.GAME_WIDTH / InfiniteDungeon.DUNGEON_TILESET.getTileWidth()),
                -1 * (InfiniteDungeon.GAME_HEIGHT / InfiniteDungeon.DUNGEON_TILESET.getTileHeight()),
                t.getTile(1));
        this.t = t;
        h = new HealthBar(healthBarWidth, healthBarHeight);
        attachedSprites = new ArrayList<>();
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        for (Sprite s : attachedSprites) {
            s.setX(x);
        }
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        for (Sprite s : attachedSprites) {
            s.setY(y);
        }
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(int attackDamage) {
        this.attackDamage = attackDamage;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        if (maxHealth > 0) {
            this.health = health;
            if (this.health > maxHealth) {
                this.health = maxHealth;
            }
        }
        h.update(getHealth() * 100 / getMaxHealth());
    }

    public int getMapX() {
        return mapX;
    }

    public int getMapY() {
        return mapY;
    }

    public boolean isDead() {
        return getHealth() <= 0;
    }

    public boolean isAttacking() {
        return attacking;
    }

    public HealthBar getHealthBar() {
        return h;
    }

    public ArrayList<Attachment> getAttachedSprites() {
        return attachedSprites;
    }

    public void attachSprite(Attachment s) {
        s.setX(getX());
        s.setY(getY());
        attachedSprites.add(s);
    }

    public void moveDown() {
        walking = true;
        mapY++;
        setSquare(sq.getBottom());
    }

    public void moveLeft() {
        walking = true;
        mapX--;
        setSquare(sq.getLeft());
    }

    public void moveRight() {
        walking = true;
        setSquare(sq.getRight());
        mapX++;
    }

    public void moveUp() {
        walking = true;
        mapY--;
        setSquare(sq.getTop());
    }

    public Square getSquare() {
        return sq;
    }

    public Direction getDirection() {
        return d;
    }

    public void setDirection(Direction d) {
        this.d = d;
    }

    public void setSquare(Square sq) {
        if (this.sq != null) {
            this.sq.setCharacter(null);
        }
        this.sq = sq;
        if (sq != null) {
            sq.setCharacter(this);
        }
        ;
    }

    public boolean isWalking() {
        return walking;
    }

    public void spawn(int mapX, int mapY, Square sq) {
        setX(mapX * InfiniteDungeon.DUNGEON_TILESET.getTileWidth());
        setY(mapY * InfiniteDungeon.DUNGEON_TILESET.getTileHeight());
        this.mapX = mapX;
        this.mapY = mapY;
        setSquare(sq);
    }

    public void heal(int amount) {
        setHealth(Math.min(amount + getHealth(), getMaxHealth()));
    }

    public void attack(Character target) {
        attacking = true;
        target.takeAttack(getAttackDamage());
    }

    public void endAttack() {
        attacking = false;
    }

    public void endMovement() {
        walking = false;
    }

    public void takeAttack(int damage) {
        setHealth(getHealth() - damage);
    }

    public TimedTask getRightMovementTask() {
        return new TimedTask(0) {
            int count = 0;
            int move = InfiniteDungeon.DUNGEON_TILESET.getTileWidth();
            int prevX = getX();

            @Override
            public void run() {
                setDrawImage(t.getTile(WALK_RIGHT_TILES[count]));
                if (count++ < 3) {
                    setX(getX() + move / 3);
                    setMilliseconds(WALK_TIME / 4);
                    setToReschedule(true);
                } else {
                    if (getX() != prevX + move) {
                        setX(prevX + move);
                    }
                    setToReschedule(false);
                    endMovement();
                }
            }
        };
    }

    public TimedTask getLeftMovementTask() {
        return new TimedTask(0) {
            int count = 0;
            int move = -InfiniteDungeon.DUNGEON_TILESET.getTileWidth();
            int prevX = getX();

            @Override
            public void run() {
                setDrawImage(t.getTile(WALK_LEFT_TILES[count]));
                if (count++ < 3) {
                    setX(getX() + move / 3);
                    setMilliseconds(WALK_TIME / 4);
                    setToReschedule(true);
                } else {
                    if (getX() != prevX + move) {
                        setX(prevX + move);
                    }
                    setToReschedule(false);
                    endMovement();
                }
            }
        };
    }

    public TimedTask getUpMovementTask() {
        return new TimedTask(0) {
            int count = 0;
            int move = -InfiniteDungeon.DUNGEON_TILESET.getTileHeight();
            int prevY = getY();

            @Override
            public void run() {
                setDrawImage(t.getTile(WALK_UP_TILES[count]));
                if (count++ < 3) {
                    setY(getY() + move / 3);
                    setMilliseconds(WALK_TIME / 4);
                    setToReschedule(true);
                } else {
                    if (getY() != prevY + move) {
                        setY(prevY + move);
                    }
                    setToReschedule(false);
                    endMovement();
                }
            }
        };
    }

    public TimedTask getDownMovementTask() {
        return new TimedTask(0) {
            int count = 0;
            int move = InfiniteDungeon.DUNGEON_TILESET.getTileHeight();
            int prevY = getY();

            @Override
            public void run() {
                setDrawImage(t.getTile(WALK_DOWN_TILES[count]));
                if (count++ < 3) {
                    setY(getY() + move / 3);
                    setMilliseconds(WALK_TIME / 4);
                    setToReschedule(true);
                } else {
                    if (getY() != prevY + move) {
                        setY(prevY + move);
                    }
                    setToReschedule(false);
                    endMovement();
                }
            }
        };
    }

    public TimedTask getRightAttackTask() {
        return new TimedTask(0) {
            @Override
            public void run() {
                if (attacking) {
                    setDrawImage(t.getTile(WALK_RIGHT_TILES[1]));
                    setX(getX() + InfiniteDungeon.DUNGEON_TILESET.getTileHeight() / 2);
                    setToReschedule(true);
                    setMilliseconds(ATTACK_TIME);
                    endAttack();
                } else {
                    setX(getX() - InfiniteDungeon.DUNGEON_TILESET.getTileHeight() / 2);
                    setToReschedule(false);
                }
            }

        };
    }

    public TimedTask getLeftAttackTask() {
        return new TimedTask(0) {
            @Override
            public void run() {
                if (attacking) {
                    setDrawImage(t.getTile(WALK_LEFT_TILES[1]));
                    setX(getX() - InfiniteDungeon.DUNGEON_TILESET.getTileWidth() / 2);
                    setToReschedule(true);
                    setMilliseconds(ATTACK_TIME);
                    endAttack();
                } else {
                    setX(getX() + InfiniteDungeon.DUNGEON_TILESET.getTileWidth() / 2);
                    setToReschedule(false);
                }
            }

        };
    }

    public TimedTask getUpAttackTask() {
        return new TimedTask(0) {

            @Override
            public void run() {
                if (attacking) {
                    setDrawImage(t.getTile(WALK_UP_TILES[1]));
                    setY(getY() - InfiniteDungeon.DUNGEON_TILESET.getTileHeight() / 2);
                    setToReschedule(true);
                    setMilliseconds(ATTACK_TIME);
                    endAttack();
                } else {
                    setY(getY() + InfiniteDungeon.DUNGEON_TILESET.getTileHeight() / 2);
                    setToReschedule(false);
                }
            }
        };
    }

    public TimedTask getDownAttackTask() {
        return new TimedTask(0) {
            @Override
            public void run() {
                if (attacking) {
                    setDrawImage(t.getTile(WALK_DOWN_TILES[1]));
                    setY(getY() + InfiniteDungeon.DUNGEON_TILESET.getTileHeight() / 2);
                    setToReschedule(true);
                    setMilliseconds(ATTACK_TIME);
                    endAttack();
                } else {
                    setY(getY() - InfiniteDungeon.DUNGEON_TILESET.getTileHeight() / 2);
                    setToReschedule(false);
                }
            }

        };
    }

    public TimedTask getTask() {
        if (getDirection() == null) {
            return null;
        }
        switch (getDirection()) {
            case UP: {
                if (isAttacking()) {
                    return getUpAttackTask();
                } else if (isWalking()) {
                    return getUpMovementTask();
                }
                break;
            }
            case DOWN: {
                if (isAttacking()) {
                    return getDownAttackTask();
                } else if (isWalking()) {
                    return getDownMovementTask();
                }
                break;
            }
            case LEFT: {
                if (isAttacking()) {
                    return getLeftAttackTask();
                } else if (isWalking()) {
                    return getLeftMovementTask();
                }
                break;
            }
            case RIGHT: {
                if (isAttacking()) {
                    return getRightAttackTask();
                } else if (isWalking()) {
                    return getRightMovementTask();
                }
                break;
            }
        }
        return null;
    }

    abstract void performAction();
}
