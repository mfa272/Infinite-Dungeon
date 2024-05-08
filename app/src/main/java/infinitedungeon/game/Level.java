package infinitedungeon.game;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import infinitedungeon.engine.GameScene;
import infinitedungeon.engine.TimedTask;
import infinitedungeon.engine.World;
import infinitedungeon.engine.graphics.Tileset;
import infinitedungeon.game.characters.Attachment;
import infinitedungeon.game.characters.Character;
import infinitedungeon.game.characters.Monster;
import infinitedungeon.game.characters.Player;
import infinitedungeon.game.items.Armor;
import infinitedungeon.game.items.Item;
import infinitedungeon.game.items.Potion;
import infinitedungeon.game.items.Sword;

public class Level extends GameScene {
    private final int MAP_WIDTH = 80;
    private final int MAP_HEIGHT = 80;

    private final int ROOM_MAX_SIZE = 16;
    private final int ROOM_MIN_SIZE = 6;
    private final int MAX_ROOMS = 20;

    private final double DIFFICULTY_FACTOR = 1.3;
    private final double difficulty;

    private final Tileset t;
    private final Square[][] squares;
    private final ArrayList<Monster> monsters;
    private final ArrayList<Item> droppedItems;
    private final Player player;
    private final ArrayList<Rectangle> rooms;

    private Rectangle playerRoom;
    private boolean turnFinished;

    private enum MonsterTemplate {
        GOBLIN(40, 10, InfiniteDungeon.GOBLIN_TILESET),
        SKELETON(35, 15, InfiniteDungeon.SKELETON_TILESET),
        DEMON(30, 20, InfiniteDungeon.DEMON_TILESET),
        LORD(100, 25, InfiniteDungeon.LORD_TILESET);

        private final int health;
        private final int attackDamage;
        private final Tileset t;

        private MonsterTemplate(int health, int attackDamage, Tileset t) {
            this.health = health;
            this.attackDamage = attackDamage;
            this.t = t;
        }
    }

    public Level(Tileset t, Player player, int floor) {
        super();
        turnFinished = true;
        difficulty = (floor) * DIFFICULTY_FACTOR;

        rooms = new ArrayList<>();
        squares = new Square[MAP_WIDTH][MAP_HEIGHT];
        for (int i = 0; i < MAP_WIDTH; i++) {
            for (int j = 0; j < MAP_HEIGHT; j++) {
                squares[i][j] = new Square(Square.Type.EMPTY);
            }
        }
        this.t = t;
        monsters = new ArrayList<>();
        droppedItems = new ArrayList<>();
        this.player = player;
        turnFinished = true;
    }

    public boolean isTurnFinished() {
        return turnFinished;
    }

    public boolean isCleared() {
        return (player.getSquare().getType() == Square.Type.STAIRS);
    }

    /*
     * Steps for generating a level:
     * 1) Generate a room that does not intersect with others
     * 2) Generate a horizontal and a vertical corridor that connect it with the
     * previous one, taking as a central coordinate the center of one of the two
     * 3) Populate the tile array, placing walls at the top of the rooms
     * 4) Choose whether the level should start on the right or left
     * 5) Connect the squares so that characters can interact with them
     * 6) Spawn the player and monsters
     */
    public void generate() {
        Random rand = new Random();
        for (int i = 0; i < MAX_ROOMS; i++) {
            int width = ROOM_MIN_SIZE + rand.nextInt(ROOM_MAX_SIZE - ROOM_MIN_SIZE);
            int height = ROOM_MIN_SIZE + rand.nextInt(ROOM_MAX_SIZE - ROOM_MIN_SIZE);
            int x = rand.nextInt(MAP_WIDTH - width + 1);
            int y = rand.nextInt(MAP_HEIGHT - height + 1);
            Rectangle r = new Rectangle(x, y, width, height);

            boolean failed = false;
            for (Rectangle rect : rooms) {
                if (r.intersects(rect)) {
                    failed = true;
                    break;
                }
            }

            if (!failed) {
                carveRoom(r);
                if (rooms.size() > 0) {
                    boolean horizontal = rand.nextInt(2) == 0;
                    Rectangle previous = rooms.get(rooms.size() - 1);
                    if (horizontal) {
                        carveHorizontalTunnel((int) previous.getCenterX(), (int) r.getCenterX(),
                                (int) previous.getCenterY());
                        carveVerticalTunnel((int) previous.getCenterY(), (int) r.getCenterY(), (int) r.getCenterX());
                    } else {
                        carveVerticalTunnel((int) previous.getCenterY(), (int) r.getCenterY(),
                                (int) previous.getCenterX());
                        carveHorizontalTunnel((int) previous.getCenterX(), (int) r.getCenterX(), (int) r.getCenterY());
                    }
                }
                rooms.add(r);
            }
        }
        int[][] tiles = new int[MAP_WIDTH][MAP_HEIGHT];
        for (int i = 0; i < MAP_WIDTH; i++) {
            for (int j = 0; j < MAP_HEIGHT; j++) {
                if (squares[i][j].getType() == Square.Type.EMPTY) {
                    tiles[i][j] = 0;
                } else if (squares[i][j].getType() == Square.Type.GROUND) {
                    tiles[i][j] = 2;
                }
            }
        }

        int wallTile = 3;
        for (Rectangle r : rooms) {
            for (int i = r.x + 1; i < r.width + r.x; i++) {
                if (squares[i][r.y].getType() == Square.Type.EMPTY) {
                    if (wallTile == 6) {
                        wallTile = 3;
                    }
                    tiles[i][r.y] = wallTile++;
                }
            }
        }

        boolean leftToRight = rand.nextInt(2) == 0;
        Collections.sort(rooms, new Comparator<Rectangle>() {
            @Override
            public int compare(Rectangle o1, Rectangle o2) {
                if (leftToRight) {
                    return Integer.compare(o1.x, o2.x);
                }
                return Integer.compare(o2.x, o1.x);
            }
        });

        World w = new World(tiles);
        w.setTileset(t);

        Rectangle finalRoom = rooms.get(rooms.size() - 1);
        squares[(int) finalRoom.getCenterX()][(int) finalRoom.getCenterY()] = new Square(Square.Type.STAIRS);
        squares[(int) finalRoom.getCenterX()][(int) finalRoom.getCenterY()].setRoom(finalRoom);

        linkSquares();

        spawnPlayer();
        spawnMonsters();

        for (int i = playerRoom.x + 1; i < playerRoom.width + playerRoom.x; i++) {
            for (int j = playerRoom.y + 1; j < playerRoom.height + playerRoom.y; j++) {
                w.setTile(i, j, 1);
            }
        }

        setWorld(w);
    }

    public void removePickedItems() {
        for (Item it : droppedItems) {
            if (it.isPicked()) {
                removeElement(it);
            }
        }
    }

    public void nextTurn(Character.Direction d) {
        if (!player.isDead()) {
            turnFinished = false;
            player.setDirection(d);
            player.performAction();
            removeDeadMonsters();
            boolean newRoom = updatePlayerRoom();
            TimedTask playerTask = player.getTask();
            if (playerTask != null) {
                TimedTask lastTask = playerTask;
                if (!newRoom) {
                    for (Monster m : monsters) {
                        if (!m.isDead()) {
                            m.performAction();
                            TimedTask monsterTask = m.getTask();
                            if (monsterTask != null) {
                                lastTask.chain(monsterTask);
                                lastTask = monsterTask;
                            }
                        }
                    }
                }
                TimedTask turnFinishedTask = new TimedTask(0) {
                    @Override
                    public void run() {
                        setCameraX(player.getX() + player.getDrawImage().getWidth()
                                - InfiniteDungeon.GAME_WIDTH / 2);
                        setCameraY(player.getY() + player.getDrawImage().getHeight()
                                - InfiniteDungeon.GAME_HEIGHT / 2);
                        updatePlayerRoom();
                        turnFinished = true;
                    }
                };
                lastTask.chain(turnFinishedTask);
                addTimedTask(playerTask);
            } else {
                turnFinished = true;
            }
        }
    }

    private void carveRoom(Rectangle r) {
        for (int i = r.x + 1; i < r.width + r.x; i++) {
            for (int j = r.y + 1; j < r.height + r.y; j++) {
                squares[i][j] = new Square(Square.Type.GROUND);
                squares[i][j].setRoom(r);
            }
        }
    }

    private void carveHorizontalTunnel(int x1, int x2, int y) {
        int min = Math.min(x1, x2);
        int max = Math.max(x1, x2);
        for (int i = min; i <= max; i++) {
            if (squares[i][y].getType() == Square.Type.EMPTY) {
                squares[i][y] = new Square(Square.Type.GROUND);
            }
        }
    }

    private void carveVerticalTunnel(int y1, int y2, int x) {
        int min = Math.min(y1, y2);
        int max = Math.max(y1, y2);
        for (int i = min; i <= max; i++) {
            if (squares[x][i].getType() == Square.Type.EMPTY) {
                squares[x][i] = new Square(Square.Type.GROUND);
            }
        }
    }

    private void linkSquares() {
        for (int i = 0; i < MAP_WIDTH; i++) {
            for (int j = 0; j < MAP_HEIGHT; j++) {
                Square sq = squares[i][j];
                if (i > 0) {
                    sq.setLeft(squares[i - 1][j]);
                }
                if (i < MAP_WIDTH - 1) {
                    sq.setRight(squares[i + 1][j]);
                }
                if (j > 0) {
                    sq.setTop(squares[i][j - 1]);
                }
                if (j < MAP_HEIGHT - 1) {
                    sq.setBottom(squares[i][j + 1]);
                }
            }
        }
    }

    private void spawnPlayer() {
        Rectangle room = rooms.get(0);
        Square playerSquare = squares[(int) room.getCenterX()][(int) room.getCenterY()];
        player.spawn((int) room.getCenterX(), (int) room.getCenterY(), playerSquare);
        playerRoom = playerSquare.getRoom();
        addElement(player);
    }

    /*
     * In the central room, a boss and two other monsters are spawned.
     * The boss drops more powerful items, while the monsters that
     * accompany him only drop potions.
     * In the other rooms, a variable number of monsters is found.
     * Monsters should never block a corridor.
     */
    private void spawnMonsters() {
        Random rand = new Random();
        MonsterTemplate[] monstersList = MonsterTemplate.values();
        Rectangle bossRoom = rooms.get(rooms.size() / 2);
        for (int i = 1; i < rooms.size() - 1; i++) {
            Rectangle r = rooms.get(i);
            ArrayList<Integer> usedX = new ArrayList<>();
            ArrayList<Integer> usedY = new ArrayList<>();
            int numberOfMonsters;
            if (r == bossRoom) {
                int monsterX = (int) bossRoom.getCenterX();
                int monsterY = (int) bossRoom.getCenterY();
                usedX.add(monsterX);
                usedY.add(monsterY);
                Monster monster = new Monster(MonsterTemplate.LORD.t, player);
                int itemDrop = rand.nextInt(1);
                if (itemDrop == 0) {
                    monster.setDropItem(new Armor(2 + (int) (8 * (difficulty + 1)), 100));
                } else {
                    monster.setDropItem(new Sword(2 + (int) (8 * (difficulty + 1)), 100));
                }
                monster.setMaxHealth(MonsterTemplate.LORD.health + (int) (MonsterTemplate.LORD.health * difficulty));
                monster.setHealth(MonsterTemplate.LORD.health + (int) (MonsterTemplate.LORD.health * difficulty));
                monster.setAttackDamage(
                        MonsterTemplate.LORD.attackDamage + (int) (MonsterTemplate.LORD.attackDamage * difficulty));
                monster.spawn(monsterX, monsterY, squares[monsterX][monsterY]);
                monster.setVisible(false);
                monsters.add(monster);
                addElement(monster);
                for (Attachment s : monster.getAttachedSprites()) {
                    s.setVisible(false);
                    addForegroundElement(s);
                }
                numberOfMonsters = 2;
            } else {
                numberOfMonsters = rand.nextInt(5);
            }
            for (int j = 0; j < numberOfMonsters; j++) {
                MonsterTemplate monsterValue = monstersList[rand.nextInt(3)];
                int monsterX = r.x + 2 + rand.nextInt(r.x + r.width - 1 - (r.x + 2));
                int monsterY = r.y + 2 + rand.nextInt(r.y + r.height - 1 - (r.y + 2));
                while (usedX.contains(monsterX) && usedY.contains(monsterY)) {
                    monsterX = r.x + 2 + rand.nextInt(r.x + r.width - 1 - (r.x + 2));
                    monsterY = r.y + 2 + rand.nextInt(r.y + r.height - 1 - (r.y + 2));
                }
                if (!usedX.contains(monsterX)) {
                    usedX.add(monsterX);
                }
                if (!usedY.contains(monsterY)) {
                    usedY.add(monsterY);
                }
                Monster monster = new Monster(monsterValue.t, player);
                if (r == bossRoom) {
                    int itemDrop = rand.nextInt(2);
                    if (itemDrop < 1) {
                        monster.setDropItem(new Potion(player.getMaxHealth() / 3));
                    }
                } else {
                    int itemDrop = rand.nextInt(100);
                    if (itemDrop < 15) {
                        monster.setDropItem(new Potion(player.getMaxHealth() / 3));
                    } else if (itemDrop >= 15 && itemDrop < 20) {
                        monster.setDropItem(new Armor(2 + (int) (5 * difficulty), 30));
                    } else if (itemDrop > 94) {
                        monster.setDropItem(new Sword(2 + (int) (5 * difficulty), 30));
                    }
                }
                monster.setMaxHealth(monsterValue.health + (int) (monsterValue.health * difficulty));
                monster.setHealth(monsterValue.health + (int) (monsterValue.health * difficulty));
                monster.setAttackDamage(monsterValue.attackDamage + (int) (monsterValue.attackDamage * difficulty));
                monster.spawn(monsterX, monsterY, squares[monsterX][monsterY]);
                monster.setVisible(false);
                monsters.add(monster);
                addElement(monster);
                for (Attachment s : monster.getAttachedSprites()) {
                    s.setVisible(false);
                    addForegroundElement(s);
                }
            }
        }
    }

    private boolean updatePlayerRoom() {
        if (playerRoom != player.getSquare().getRoom()) {
            if (playerRoom != null) {
                for (int i = playerRoom.x + 1; i < playerRoom.width + playerRoom.x; i++) {
                    for (int j = playerRoom.y + 1; j < playerRoom.height + playerRoom.y; j++) {
                        getWorld().setTile(i, j, 2);
                    }
                }
                for (Monster m : monsters) {
                    m.setVisible(false);

                    for (Attachment s : m.getAttachedSprites()) {
                        s.setVisible(false);
                    }
                }
                for (Item i : droppedItems) {
                    i.setVisible(false);
                }
            }
            playerRoom = player.getSquare().getRoom();
            if (playerRoom != null) {
                for (int i = playerRoom.x + 1; i < playerRoom.width + playerRoom.x; i++) {
                    for (int j = playerRoom.y + 1; j < playerRoom.height + playerRoom.y; j++) {
                        getWorld().setTile(i, j, 1);
                        if (squares[i][j].getType() == Square.Type.STAIRS) {
                            getWorld().setTile(i, j, 6);
                        }
                    }
                }
                for (Monster m : monsters) {
                    if (!m.isDead() && playerRoom == m.getSquare().getRoom()) {
                        m.setVisible(true);
                        for (Attachment s : m.getAttachedSprites()) {
                            s.setVisible(true);
                        }
                    }
                }
                for (Item i : droppedItems) {
                    if (playerRoom == i.getSquare().getRoom()) {
                        i.setVisible(true);
                    }
                }
            }
            return true;
        }
        return false;
    }

    private void removeDeadMonsters() {
        for (Monster m : monsters) {
            if ((m).isDead()) {
                removeElement(m);
                for (Attachment s : m.getAttachedSprites()) {
                    removeElement(s);
                }
                Item i = m.getDropItem();
                if (i != null) {
                    i.setX(m.getX());
                    i.setY(m.getY());
                    i.setSquare(m.getSquare());
                    droppedItems.add(i);
                    addBackgroundElement(i);
                    if (m.getDropItem() != null) {
                        m.getSquare().addItem(m.getDropItem());
                        m.setDropItem(null);
                    }
                }
                m.setSquare(null);
            }
        }
    }
}
