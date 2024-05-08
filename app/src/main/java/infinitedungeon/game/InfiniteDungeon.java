package infinitedungeon.game;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import infinitedungeon.engine.Animation;
import infinitedungeon.engine.AudioGroup;
import infinitedungeon.engine.Game;
import infinitedungeon.engine.GameScene;
import infinitedungeon.engine.TimedTask;
import infinitedungeon.engine.World;
import infinitedungeon.engine.graphics.Font;
import infinitedungeon.engine.graphics.RgbImage;
import infinitedungeon.engine.graphics.Sprite;
import infinitedungeon.engine.graphics.Tileset;
import infinitedungeon.engine.graphics.GUI.GUIButton;
import infinitedungeon.engine.graphics.GUI.GUIMessage;
import infinitedungeon.engine.graphics.GUI.GUIPanel;
import infinitedungeon.game.characters.Character;
import infinitedungeon.game.characters.Player;
import infinitedungeon.game.items.Armor;
import infinitedungeon.game.items.Potion;
import infinitedungeon.game.items.Sword;

public class InfiniteDungeon {

    public static final int GAME_WIDTH = 640;
    public static final int GAME_HEIGHT = 480;

    public static final Tileset FONT_SMALL_TILESET;
    public static final Tileset FONT_BIG_TILESET;

    public static final Tileset DUNGEON_TILESET;

    public static final Tileset PLAYER_TILESET;
    public static final Tileset GOBLIN_TILESET;
    public static final Tileset SKELETON_TILESET;
    public static final Tileset DEMON_TILESET;
    public static final Tileset LORD_TILESET;

    public static final RgbImage SWORD_IMAGE;
    public static final RgbImage ARMOR_IMAGE;
    public static final RgbImage POTION_IMAGE;

    public static final RgbImage BIG_BUTTON_IMAGE;
    public static final RgbImage BIG_BUTTON_PRESSED_IMAGE;
    public static final RgbImage BIG_BUTTON_DISABLED_IMAGE;
    public static final RgbImage SMALL_BUTTON_IMAGE;
    public static final RgbImage SMALL_BUTTON_PRESSED_IMAGE;
    public static final RgbImage SMALL_BUTTON_DISABLED_IMAGE;
    public static final RgbImage FLOOR_MESSAGE_PANEL_IMAGE;
    public static final RgbImage TITLE_PANEL_IMAGE;
    public static final RgbImage HELP_PANEL_IMAGE;
    public static final RgbImage INVENTORY_IMAGE;

    // Load graphic resources
    static {
        FONT_SMALL_TILESET = loadTileset(InfiniteDungeon.class.getResource("/font_small_t.png").getPath(), 16, 16);
        FONT_BIG_TILESET = loadTileset(InfiniteDungeon.class.getResource("/font_big_t.png").getPath(), 48, 48);
        DUNGEON_TILESET = loadTileset(InfiniteDungeon.class.getResource("/dungeonTileset.png").getPath(), 32, 32);
        PLAYER_TILESET = loadTileset(InfiniteDungeon.class.getResource("/character.png").getPath(), 32, 32);
        GOBLIN_TILESET = loadTileset(InfiniteDungeon.class.getResource("/goblin.png").getPath(), 32, 32);
        SKELETON_TILESET = loadTileset(InfiniteDungeon.class.getResource("/skeleton.png").getPath(), 32, 32);
        DEMON_TILESET = loadTileset(InfiniteDungeon.class.getResource("/demon.png").getPath(), 32, 32);
        LORD_TILESET = loadTileset(InfiniteDungeon.class.getResource("/lord.png").getPath(), 32, 32);
        SWORD_IMAGE = loadImage(InfiniteDungeon.class.getResource("/sword.png").getPath());
        ARMOR_IMAGE = loadImage(InfiniteDungeon.class.getResource("/armor.png").getPath());
        POTION_IMAGE = loadImage(InfiniteDungeon.class.getResource("/bread.png").getPath());
        BIG_BUTTON_IMAGE = loadImage(InfiniteDungeon.class.getResource("/button_big.png").getPath());
        BIG_BUTTON_PRESSED_IMAGE = loadImage(InfiniteDungeon.class.getResource("/button_big_pressed.png").getPath());
        BIG_BUTTON_DISABLED_IMAGE = loadImage(InfiniteDungeon.class.getResource("/button_big_disabled.png").getPath());
        SMALL_BUTTON_IMAGE = loadImage(InfiniteDungeon.class.getResource("/button_small.png").getPath());
        SMALL_BUTTON_PRESSED_IMAGE = loadImage(
                InfiniteDungeon.class.getResource("/button_small_pressed.png").getPath());
        SMALL_BUTTON_DISABLED_IMAGE = loadImage(
                InfiniteDungeon.class.getResource("/button_small_disabled.png").getPath());
        FLOOR_MESSAGE_PANEL_IMAGE = loadImage(InfiniteDungeon.class.getResource("/floor_message_panel.png").getPath());
        TITLE_PANEL_IMAGE = loadImage(InfiniteDungeon.class.getResource("/title_panel.png").getPath());
        HELP_PANEL_IMAGE = loadImage(InfiniteDungeon.class.getResource("/help_panel.png").getPath());
        INVENTORY_IMAGE = loadImage(InfiniteDungeon.class.getResource("/inventory_t.png").getPath());
    }

    private static Game game;
    private static Level level;
    private static GameScene pauseScene;
    private static AudioGroup music;
    private static AudioGroup sfx;
    private static Player player;
    private static Inventory inv;
    private static int floor;
    private static double fullScreenScale;
    private static GUIPanel healthBarPanel;

    private static final double MUSIC_VOLUME = 1.0;
    private static final double SFX_VOLUME = 1.0;

    private static final String MUSIC_FILE = InfiniteDungeon.class.getResource("/music.mp3").getPath();
    private static final String WALK_FX_FILE = InfiniteDungeon.class.getResource("/walk.mp3").getPath();
    private static final String ATTACK_FX_FILE = InfiniteDungeon.class.getResource("/attack.mp3").getPath();

    public static void main(String[] args) {
        sfx = new AudioGroup();
        music = new AudioGroup();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double widthScale = (double) screenSize.width / GAME_WIDTH;
        double heightScale = (double) screenSize.height / GAME_HEIGHT;
        fullScreenScale = Math.min(widthScale, heightScale);
        game = new Game(GAME_WIDTH, GAME_HEIGHT, 1, true, 60);
        game.setWindowIcon(new ImageIcon(".src/main/resources/icon.png"));

        GameScene titleScene = new GameScene();

        World w = new World(20, 15);
        w.setTileset(DUNGEON_TILESET);
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 15; j++) {
                w.setTile(i, j, 2);
            }
        }
        titleScene.setWorld(w);

        // Set title and pause scenes
        Sprite titlePlayer = new Sprite(new RgbImage(1, 1));
        Font titlePanelFont = new Font(FONT_BIG_TILESET);
        titlePanelFont.setColor(0xfffdd0);
        Font buttonFont = new Font(FONT_SMALL_TILESET);
        buttonFont.setColor(0x964B00);
        Font helpPanelFont = new Font(FONT_SMALL_TILESET);
        helpPanelFont.setColor(0xfffdd0);

        GUIMessage titlePanel = new GUIMessage(
                (GAME_WIDTH - TITLE_PANEL_IMAGE.getWidth()) / 2,
                100, TITLE_PANEL_IMAGE);
        titlePanel.setFont(titlePanelFont);
        titlePanel.setText("Infinite Dungeon", 8, " ");

        GUIButton exitButton = new GUIButton(0,
                GAME_HEIGHT - SMALL_BUTTON_IMAGE.getHeight(), SMALL_BUTTON_IMAGE);
        exitButton.setPressedImage(SMALL_BUTTON_PRESSED_IMAGE);
        exitButton.setDisabledImage(SMALL_BUTTON_DISABLED_IMAGE);
        exitButton.setFont(buttonFont);
        exitButton.setText("X", 1, null);
        exitButton.setTask(new Runnable() {
            @Override
            public void run() {
                game.stop();
            }
        });

        int helpPanelX = (GAME_WIDTH - HELP_PANEL_IMAGE.getWidth()) / 2;
        int helpPanelY = (GAME_HEIGHT - HELP_PANEL_IMAGE.getHeight()) / 2;
        GUIPanel helpPanel = new GUIPanel(helpPanelX, helpPanelY, HELP_PANEL_IMAGE);
        String[] helpStrings = new String[] { "WASD: ",
                "- move/attack",
                "P: ",
                "- pick up items",
                "ENTER:",
                "- go down the stairs",
                "CLICK LEFT:",
                "- equip/use item",
                "DOUBLE CLICK RIGHT:",
                "- destroy item",
                "ESC:",
                "- pause" };
        for (int i = 0; i < helpStrings.length; i++) {
            int offX = helpPanelFont.getCharWidth();
            int offY = helpPanelFont.getCharHeight();
            helpPanelFont.writeOnImage(helpStrings[i], helpStrings[i].length(),
                    null, offX, offY * (i + 1), HELP_PANEL_IMAGE);
        }

        GUIButton muteButton = new GUIButton(GAME_WIDTH - BIG_BUTTON_IMAGE.getWidth(),
                GAME_HEIGHT - BIG_BUTTON_IMAGE.getHeight(), BIG_BUTTON_IMAGE);
        muteButton.setPressedImage(BIG_BUTTON_PRESSED_IMAGE);
        muteButton.setDisabledImage(BIG_BUTTON_DISABLED_IMAGE);
        muteButton.setFont(buttonFont);
        muteButton.setText("SOUND ON", 8, null);
        muteButton.setTask(new Runnable() {
            @Override
            public void run() {
                if (music.getVolume() > 0) {
                    music.setVolume(0);
                    sfx.setVolume(0);
                    muteButton.setText("SOUND ON", 8, null);
                } else {
                    music.setVolume(1);
                    sfx.setVolume(1);
                    muteButton.setText("SOUND OFF", 9, null);
                }
            }
        });

        GUIButton windowButton = new GUIButton(muteButton.getX(),
                muteButton.getY() - BIG_BUTTON_IMAGE.getHeight(), BIG_BUTTON_IMAGE);
        windowButton.setPressedImage(BIG_BUTTON_PRESSED_IMAGE);
        windowButton.setDisabledImage(BIG_BUTTON_DISABLED_IMAGE);
        windowButton.setFont(buttonFont);
        windowButton.setText("FULLSCREEN", 10, null);
        windowButton.setTask(new Runnable() {
            @Override
            public void run() {
                if (game.isWindowed()) {
                    game.setWindowed(false);
                    game.setScale(fullScreenScale);
                    windowButton.setText("WINDOW", 6, null);
                    game.updateFrame();
                } else {
                    game.setWindowed(true);
                    game.setScale(1);
                    windowButton.setText("FULLSCREEN", 10, null);
                    game.updateFrame();
                }
            }
        });

        pauseScene = new GameScene();
        pauseScene.addKeyReleasedEvent(KeyEvent.VK_ESCAPE, new Runnable() {
            @Override
            public void run() {
                game.resumeScene(level);
                game.removeScene(pauseScene);
            }
        });

        int pauseSceneHeight = BIG_BUTTON_IMAGE.getHeight() * 4;
        int pauseSceneButtonsX = (GAME_WIDTH - BIG_BUTTON_IMAGE.getWidth()) / 2;
        int resumeButtonY = (GAME_HEIGHT - pauseSceneHeight) / 2;
        GUIButton resumeButton = new GUIButton(pauseSceneButtonsX,
                resumeButtonY, BIG_BUTTON_IMAGE);
        resumeButton.setPressedImage(BIG_BUTTON_PRESSED_IMAGE);
        resumeButton.setDisabledImage(BIG_BUTTON_DISABLED_IMAGE);
        resumeButton.setFont(buttonFont);
        resumeButton.setText("RESUME", 6, null);
        resumeButton.setTask(new Runnable() {
            @Override
            public void run() {
                game.resumeScene(level);
                game.removeScene(pauseScene);
            }
        });

        int musicButtonY = resumeButtonY + BIG_BUTTON_IMAGE.getHeight();
        GUIButton musicButton = new GUIButton(pauseSceneButtonsX,
                musicButtonY, BIG_BUTTON_IMAGE);
        musicButton.setPressedImage(BIG_BUTTON_PRESSED_IMAGE);
        musicButton.setDisabledImage(BIG_BUTTON_DISABLED_IMAGE);
        musicButton.setFont(buttonFont);
        musicButton.setTask(new Runnable() {
            @Override
            public void run() {
                if (music.getVolume() > 0) {
                    music.setVolume(0);
                    musicButton.setText("MUSIC ON", 8, null);
                } else {
                    music.setVolume(MUSIC_VOLUME);
                    musicButton.setText("MUSIC OFF", 9, null);
                }
            }
        });

        int sfxButtonY = musicButtonY + BIG_BUTTON_IMAGE.getHeight();
        GUIButton sfxButton = new GUIButton(pauseSceneButtonsX,
                sfxButtonY, BIG_BUTTON_IMAGE);
        sfxButton.setPressedImage(BIG_BUTTON_PRESSED_IMAGE);
        sfxButton.setDisabledImage(BIG_BUTTON_DISABLED_IMAGE);
        sfxButton.setFont(buttonFont);
        sfxButton.setTask(new Runnable() {
            @Override
            public void run() {
                if (sfx.getVolume() > 0) {
                    sfx.setVolume(0);
                    sfxButton.setText("SFX ON", 6, null);
                } else {
                    sfx.setVolume(SFX_VOLUME);
                    sfxButton.setText("SFX OFF", 7, null);
                }
            }
        });

        int pauseWindowButtonY = sfxButtonY + BIG_BUTTON_IMAGE.getHeight();
        GUIButton pauseWindowButton = new GUIButton(pauseSceneButtonsX,
                pauseWindowButtonY, BIG_BUTTON_IMAGE);
        pauseWindowButton.setPressedImage(BIG_BUTTON_PRESSED_IMAGE);
        pauseWindowButton.setDisabledImage(BIG_BUTTON_DISABLED_IMAGE);
        pauseWindowButton.setFont(buttonFont);
        pauseWindowButton.setText("FULLSCREEN", 10, null);
        pauseWindowButton.setTask(new Runnable() {
            @Override
            public void run() {
                if (game.isWindowed()) {
                    game.setWindowed(false);
                    game.setScale(fullScreenScale);
                    pauseWindowButton.setText("WINDOW", 6, null);
                    game.updateFrame();
                } else {
                    game.setWindowed(true);
                    game.setScale(1);
                    pauseWindowButton.setText("FULLSCREEN", 10, null);
                    game.updateFrame();
                }
            }
        });

        int pauseExitButtonY = pauseWindowButtonY + BIG_BUTTON_IMAGE.getHeight();
        GUIButton pauseExitButton = new GUIButton(pauseSceneButtonsX,
                pauseExitButtonY, BIG_BUTTON_IMAGE);
        pauseExitButton.setPressedImage(BIG_BUTTON_PRESSED_IMAGE);
        pauseExitButton.setDisabledImage(BIG_BUTTON_DISABLED_IMAGE);
        pauseExitButton.setFont(buttonFont);
        pauseExitButton.setText("EXIT", 4, null);
        pauseExitButton.setTask(new Runnable() {
            @Override
            public void run() {
                game.stop();
            }
        });

        int startButtonX = (GAME_WIDTH - BIG_BUTTON_IMAGE.getWidth()) / 2;
        int startButtonY = (titlePanel.getY() + TITLE_PANEL_IMAGE.getHeight() + 50);
        GUIButton startButton = new GUIButton(startButtonX, startButtonY, BIG_BUTTON_IMAGE);
        titlePlayer.setX(startButtonX + BIG_BUTTON_IMAGE.getWidth() + 16);
        titlePlayer.setY(startButtonY + 16);
        startButton.setPressedImage(BIG_BUTTON_PRESSED_IMAGE);
        startButton.setDisabledImage(SMALL_BUTTON_DISABLED_IMAGE);
        startButton.setFont(buttonFont);
        startButton.setText("START", 5, null);
        startButton.setTask(new Runnable() {
            @Override
            public void run() {
                if (music.getVolume() > 0) {
                    musicButton.setText("MUSIC OFF", 9, null);
                } else {
                    musicButton.setText("MUSIC ON", 8, null);
                }
                if (sfx.getVolume() > 0) {
                    sfxButton.setText("SFX OFF", 7, null);
                } else {
                    sfxButton.setText("SFX ON", 6, null);
                }
                restart();
                nextLevel();
            }
        });

        GUIButton helpButton = new GUIButton(0 + SMALL_BUTTON_PRESSED_IMAGE.getWidth(),
                GAME_HEIGHT - SMALL_BUTTON_IMAGE.getHeight(), SMALL_BUTTON_IMAGE);

        int helpPanelButtonX = ((GAME_WIDTH - SMALL_BUTTON_IMAGE.getWidth()) / 2);
        int helpPanelButtonY = helpPanelY + HELP_PANEL_IMAGE.getHeight() - SMALL_BUTTON_IMAGE.getHeight() - 16;
        GUIButton helpPanelButton = new GUIButton(helpPanelButtonX,
                helpPanelButtonY, SMALL_BUTTON_IMAGE);
        helpPanelButton.setPressedImage(SMALL_BUTTON_PRESSED_IMAGE);
        helpPanelButton.setDisabledImage(SMALL_BUTTON_DISABLED_IMAGE);
        helpPanelButton.setFont(buttonFont);
        helpPanelButton.setText("OK", 2, null);
        helpPanelButton.setTask(new Runnable() {
            @Override
            public void run() {
                titleScene.removeElement(helpPanel);
                titleScene.removeElement(helpPanelButton);
                startButton.setEnabled(true);
                exitButton.setEnabled(true);
                helpButton.setEnabled(true);
                muteButton.setEnabled(true);
                windowButton.setEnabled(true);
            }
        });

        helpButton.setPressedImage(SMALL_BUTTON_PRESSED_IMAGE);
        helpButton.setDisabledImage(SMALL_BUTTON_DISABLED_IMAGE);
        helpButton.setFont(buttonFont);
        helpButton.setText("?", 1, null);
        helpButton.setTask(new Runnable() {
            @Override
            public void run() {
                titleScene.addGUIPanel(helpPanel);
                titleScene.addGUIPanel(helpPanelButton);
                startButton.setEnabled(false);
                exitButton.setEnabled(false);
                helpButton.setEnabled(false);
                muteButton.setEnabled(false);
                windowButton.setEnabled(false);
            }
        });

        titleScene.addGUIPanel(titlePanel);
        titleScene.addGUIPanel(startButton);
        titleScene.addGUIPanel(exitButton);
        titleScene.addGUIPanel(helpButton);
        titleScene.addGUIPanel(windowButton);
        titleScene.addGUIPanel(muteButton);
        titleScene.addElement(titlePlayer);
        titleScene.addTimedTask(new Animation(PLAYER_TILESET.getSubTileset(
                new int[] { 3, 4, 5 }), 0, 250, titlePlayer, true));

        pauseScene.addGUIPanel(resumeButton);
        pauseScene.addGUIPanel(musicButton);
        pauseScene.addGUIPanel(sfxButton);
        pauseScene.addGUIPanel(pauseWindowButton);
        pauseScene.addGUIPanel(pauseExitButton);

        game.addScene(titleScene);
        game.addAudioGroup(sfx);
        game.addAudioGroup(music);
        music.playAudio(MUSIC_FILE, true);

        game.loop();
    }

    private static void nextLevel() {
        game.removeScene(level);

        level = new Level(DUNGEON_TILESET, player, floor++);

        Font floorMessagePanelFont = new Font(FONT_BIG_TILESET);
        floorMessagePanelFont.setColor(0xfffdd0);
        int floorMessagePanelY = GAME_HEIGHT / 4;
        GUIMessage floorMessagePanel = new GUIMessage(0, floorMessagePanelY,
                FLOOR_MESSAGE_PANEL_IMAGE);
        String floorMessage = "FLOOR #" + floor;
        floorMessagePanel.setFont(floorMessagePanelFont);
        floorMessagePanel.setText(floorMessage, floorMessage.length(), null);

        level.generate();
        level.addKeyPressedEvent(KeyEvent.VK_A, new Runnable() {
            @Override
            public void run() {
                if (level.isTurnFinished()) {
                    level.nextTurn(Character.Direction.LEFT);
                    inv.updateSlots();
                    if (player.isWalking()) {
                        sfx.playAudio(WALK_FX_FILE, false);
                    } else if (player.isAttacking()) {
                        sfx.playAudio(ATTACK_FX_FILE, false);
                    }
                    if (player.isDead()) {
                        lose();
                    }
                }
            }
        });

        level.addKeyPressedEvent(KeyEvent.VK_S, new Runnable() {
            @Override
            public void run() {
                if (level.isTurnFinished()) {
                    level.nextTurn(Character.Direction.DOWN);
                    inv.updateSlots();
                    if (player.isWalking()) {
                        sfx.playAudio(WALK_FX_FILE, false);
                    } else if (player.isAttacking()) {
                        sfx.playAudio(ATTACK_FX_FILE, false);
                    }
                }
                if (player.isDead()) {
                    lose();
                }
            }

        });

        level.addKeyPressedEvent(KeyEvent.VK_W, new Runnable() {
            @Override
            public void run() {
                if (level.isTurnFinished()) {
                    level.nextTurn(Character.Direction.UP);
                    inv.updateSlots();
                    if (player.isWalking()) {
                        sfx.playAudio(WALK_FX_FILE, false);
                    } else if (player.isAttacking()) {
                        sfx.playAudio(ATTACK_FX_FILE, false);
                    }
                }
                if (player.isDead()) {
                    lose();
                }
            }

        });

        level.addKeyPressedEvent(KeyEvent.VK_D, new Runnable() {
            @Override
            public void run() {
                if (level.isTurnFinished()) {
                    level.nextTurn(Character.Direction.RIGHT);
                    inv.updateSlots();
                    if (player.isWalking()) {
                        sfx.playAudio(WALK_FX_FILE, false);
                    } else if (player.isAttacking()) {
                        sfx.playAudio(ATTACK_FX_FILE, false);
                    }
                }
                if (player.isDead()) {
                    lose();
                }
            }
        });

        level.addKeyPressedEvent(KeyEvent.VK_P, new Runnable() {
            @Override
            public void run() {
                if (level.isTurnFinished()) {
                    player.pickItem();
                    inv.updateSlots();
                    level.removePickedItems();
                }
            }
        });

        level.addKeyPressedEvent(KeyEvent.VK_ENTER, new Runnable() {
            @Override
            public void run() {
                if (level.isTurnFinished() && level.isCleared()) {
                    nextLevel();
                }
            }
        });

        level.addKeyReleasedEvent(KeyEvent.VK_ESCAPE, new Runnable() {
            @Override
            public void run() {
                game.pauseScene(level);
                game.addScene(pauseScene);
            }
        });

        GUIMessage fpsPanel = new GUIMessage(0, 0,
                new RgbImage(FONT_SMALL_TILESET.getTileWidth() * 7, FONT_SMALL_TILESET.getTileHeight()));

        Font fpsFont = new Font(FONT_SMALL_TILESET);
        fpsFont.setColor(0xFFFFFF);
        fpsPanel.setFont(fpsFont);

        level.addGUIPanel(fpsPanel);
        level.addGUIPanel(floorMessagePanel);
        level.addTimedTask(new TimedTask(1000) {
            @Override
            public void run() {
                fpsPanel.setText("FPS: " + Integer.toString((int) Math.round(game.getFps())), 7, null, 0, 0);
                setToReschedule(true);
            }
        });

        level.addGUIPanel(healthBarPanel);
        level.addGUIPanel(inv.getInventoryPanel());
        level.addGUIPanel(inv.getSwordSlot());
        level.addGUIPanel(inv.getArmorSlot());
        level.addGUIPanel(inv.getTooltipPanel());
        for (GUIPanel p : inv.getInventorySlots()) {
            level.addGUIPanel(p);
        }
        level.addTimedTask(new TimedTask(1500) {
            @Override
            public void run() {
                level.removeElement(floorMessagePanel);
            }
        });
        game.addScene(level);

        level.setCameraX(player.getX() + player.getDrawImage().getWidth() - GAME_WIDTH / 2);
        level.setCameraY(player.getY() + player.getDrawImage().getHeight() - GAME_HEIGHT / 2);
    }

    private static void lose() {
        game.removeScene(level);
        GameScene lossScene = new GameScene();
        Font messageFont = new Font(FONT_BIG_TILESET);
        messageFont.setColor(0xfffdd0);
        GUIMessage lossMessage = new GUIMessage(0, 0, new RgbImage(0, GAME_WIDTH, GAME_HEIGHT));
        lossMessage.setFont(messageFont);
        lossMessage.setText("You lost", 8, " ");
        lossScene.addGUIPanel(lossMessage);
        GUIButton backButton = new GUIButton((GAME_WIDTH - BIG_BUTTON_IMAGE.getWidth()) / 2,
                (GAME_HEIGHT - BIG_BUTTON_IMAGE.getHeight()) / 2 + 80, BIG_BUTTON_IMAGE);
        backButton.setPressedImage(BIG_BUTTON_PRESSED_IMAGE);
        backButton.setDisabledImage(BIG_BUTTON_DISABLED_IMAGE);

        Font buttonFont = new Font(FONT_SMALL_TILESET);
        buttonFont.setColor(0x964B00);
        backButton.setFont(buttonFont);
        backButton.setText("BACK", 4, null);
        backButton.setTask(new Runnable() {
            @Override
            public void run() {
                game.removeScene(lossScene);
            }
        });
        lossScene.addGUIPanel(backButton);
        game.addScene(lossScene);
    }

    private static void restart() {
        floor = 0;
        player = new Player(PLAYER_TILESET, 100, 30);
        player.setAttackDamage(10);
        player.setMaxHealth(500);
        player.setHealth(500);
        healthBarPanel = new GUIPanel(0, GAME_HEIGHT - player.getHealthBar().getHeight(),
                player.getHealthBar());
        inv = new Inventory(player,
                healthBarPanel.getX() + healthBarPanel.getDrawImage().getWidth(),
                healthBarPanel.getY() - SWORD_IMAGE.getHeight(),
                SWORD_IMAGE.getWidth(),
                SWORD_IMAGE.getHeight(),
                GAME_WIDTH - healthBarPanel.getDrawImage().getWidth());

        player.addItem(new Sword(5, 50));
        player.addItem(new Armor(5, 50));
        player.addItem(new Potion(500));
        inv.updateSlots();
    }

    private static Tileset loadTileset(String path, int width, int height) {
        try {
            return new Tileset(path, width, height);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Unable to load resources",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
            return null;
        }
    }

    private static RgbImage loadImage(String path) {
        try {
            return new RgbImage(path);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Unable to load resources",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
            return null;
        }
    }
}
