package infinitedungeon.engine;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import infinitedungeon.engine.graphics.GameElement;
import infinitedungeon.engine.graphics.RgbImage;
import infinitedungeon.engine.graphics.Tileset;

public class Game {

    static public final int EMPTY_PIXEL = 0xffff00ff;
    static protected final TimeUnit TIME_SCALE = TimeUnit.NANOSECONDS;

    private final ArrayList<GameScene> scenes;
    private final ArrayList<GameScene> scenesToRemove;
    private final ArrayList<GameScene> scenesToAdd;

    private final ArrayList<AudioGroup> audioGroups;
    private boolean interrupted;
    private int width;
    private int height;
    private long frameSecondsCap;
    private double fps;
    private double scale;
    private boolean windowed;
    private ImageIcon frameIcon;
    private boolean stop;
    private long gameTime;

    private GameFrame gameFrame;
    private BufferedImage gameImage;
    private RgbImage gameImageRgb;

    public Game(int width, int height, double scale, boolean windowed, long fpsCap) {
        this.scale = scale;
        this.windowed = windowed;
        this.width = width;
        this.height = height;
        frameSecondsCap = fpsCap;
        scenes = new ArrayList<>();
        scenesToAdd = new ArrayList<>();
        scenesToRemove = new ArrayList<>();
        audioGroups = new ArrayList<>();
        // init javafx
        Platform.startup(() -> {
        });
    }

    public Game(int width, int height, long fpsCap) {
        this(width, height, 1.0, true, fpsCap);
    }

    public boolean getInterrupted() {
        return interrupted;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double getScale() {
        return scale;
    }

    public boolean isWindowed() {
        return windowed;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public void setWindowed(boolean windowed) {
        this.windowed = windowed;
    }

    public void setWindowIcon(ImageIcon icon) {
        frameIcon = icon;
    }

    public double getFps() {
        return fps;
    }

    public void addScene(GameScene s) {
        scenesToAdd.add(s);
    }

    public void removeScene(GameScene s) {
        if (scenes.contains(s)) {
            scenesToRemove.add(s);
        }
    }

    public void addAudioGroup(AudioGroup s) {
        audioGroups.add(s);
        s.setInit(true);
    }

    public boolean removeAudioGroup(AudioGroup s) {
        s.stopAudios();
        return audioGroups.remove(s);
    }

    public void stop() {
        stop = true;
    }

    public void loop() {
        updateFrame();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable gameLoop = new Runnable() {

            private long frames = 0;
            private double seconds = 0;

            @Override
            public void run() {
                if (!stop) {
                    long newTime = System.nanoTime();

                    synchronized (scenes) {
                        for (GameScene s : scenesToRemove) {
                            s.cancelTasks();
                            scenes.remove(s);
                        }

                        scenesToRemove.clear();
                        for (GameScene s : scenesToAdd) {
                            s.setTaskTime(newTime, TimeUnit.NANOSECONDS);
                            scenes.add(s);
                        }
                        scenesToAdd.clear();
                    }
                    for (GameScene s : scenes) {
                        if (s.isRunning()) {
                            s.sortElements();
                            if (s == scenes.get(scenes.size() - 1)) {
                                s.processInputEvents();
                            }
                            s.performTasks(newTime, TimeUnit.NANOSECONDS);
                        }
                    }
                    for (GameScene s : scenes) {
                        int cameraX = s.getCameraX();
                        int cameraY = s.getCameraY();
                        if (s.isVisible()) {
                            World w = s.getWorld();
                            if (w != null) {
                                Tileset t = w.getTileset();
                                int worldWidth = w.getWidth();
                                int worldHeight = w.getHeight();
                                int worldTileWidth = w.getTileWidth();
                                int worldTileHeight = w.getTileHeight();
                                int firstRow = Math.max(cameraY / worldTileHeight, 0);
                                int firstColumn = Math.max(cameraX / worldTileWidth, 0);
                                int lastRow = (int) Math.min(worldHeight,
                                        firstRow + Math.ceil((double) height / worldTileHeight) + 1);
                                int lastColumn = (int) Math.min(worldWidth,
                                        firstColumn + Math.ceil((double) width / worldTileWidth) + 1);
                                for (int i = firstColumn; i < lastColumn; i++) {
                                    for (int j = firstRow; j < lastRow; j++) {
                                        int x = i * worldTileWidth;
                                        int y = j * worldTileHeight;
                                        gameImageRgb.combine(t.getTile(w.getTile(i, j)), x - cameraX, y - cameraY);
                                    }
                                }
                                int worldEndX = worldTileWidth * worldWidth;
                                int worldEndY = worldTileHeight * worldHeight;
                                for (int i = worldEndX - cameraX; i < width; i++) {
                                    for (int j = 0; j < height; j++) {
                                        gameImageRgb.setPixel(i, j, 0);
                                    }
                                }
                                for (int i = 0; i < worldEndX - cameraX; i++) {
                                    for (int j = worldEndY - cameraY; j < height; j++) {
                                        gameImageRgb.setPixel(i, j, 0);
                                    }
                                }
                                for (int i = 0; i < -cameraX; i++) {
                                    for (int j = 0; j < height; j++) {
                                        gameImageRgb.setPixel(i, j, 0);
                                    }
                                }
                                for (int i = -cameraX; i < width; i++) {
                                    for (int j = 0; j < -cameraY; j++) {
                                        gameImageRgb.setPixel(i, j, 0);
                                    }
                                }
                            }
                            ArrayList<GameElement> elements = s.getElements();

                            for (GameElement elem : elements) {
                                if (elem.isVisible()) {
                                    int x = elem.getDrawX(cameraX);
                                    int y = elem.getDrawY(cameraY);
                                    gameImageRgb.combine(elem.getDrawImage(), x, y);
                                }
                            }
                        }
                    }
                    gameFrame.draw(gameImage);
                    frames++;
                    seconds += (newTime - gameTime) / 1e9;
                    if (seconds >= 1.0) {
                        fps = frames / seconds;
                        seconds = 0;
                        frames = 0;
                    }
                    gameTime = newTime;
                } else {
                    scheduler.shutdown();
                    gameFrame.dispose();
                    Platform.exit();
                }
            }
        };
        scheduler.scheduleAtFixedRate(gameLoop, 0, (long)((1.0 / frameSecondsCap) * 1000000000), TimeUnit.NANOSECONDS);
    }

    public void pauseScene(GameScene s) {
        s.pause();
    }

    public void resumeScene(GameScene s) {
        s.resume(gameTime, TimeUnit.NANOSECONDS);
    }

    public void updateFrame() {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    if (gameFrame != null) {
                        gameFrame.dispose();
                    }
                    gameFrame = new GameFrame(width, height, scale, windowed);
                    gameFrame.init();
                    if (frameIcon != null) {
                        gameFrame.setIconImage(frameIcon.getImage());
                    }

                    gameFrame.addMouseMotionListener(new MouseMotionAdapter() {
                        @Override
                        public void mouseMoved(MouseEvent e) {
                            GameScene lastScene = null;
                            synchronized (scenes) {
                                if (scenes.size() > 0) {
                                    lastScene = scenes.get(scenes.size() - 1);
                                }
                            }
                            if (lastScene != null && lastScene.isRunning()) {
                                lastScene.enqueueInputEvent(e, scale);
                            }
                        }
                    });

                    gameFrame.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            GameScene lastScene = null;
                            synchronized (scenes) {
                                if (scenes.size() > 0) {
                                    lastScene = scenes.get(scenes.size() - 1);
                                }
                            }
                            if (lastScene != null && lastScene.isRunning()) {
                                lastScene.enqueueInputEvent(e, scale);
                            }
                        }

                        @Override
                        public void mousePressed(MouseEvent e) {
                            GameScene lastScene = null;
                            synchronized (scenes) {
                                if (scenes.size() > 0) {
                                    lastScene = scenes.get(scenes.size() - 1);
                                }
                            }
                            if (lastScene != null && lastScene.isRunning()) {
                                lastScene.enqueueInputEvent(e, scale);
                            }
                        }

                        @Override
                        public void mouseReleased(MouseEvent e) {
                            GameScene lastScene = null;
                            synchronized (scenes) {
                                if (scenes.size() > 0) {
                                    lastScene = scenes.get(scenes.size() - 1);
                                }
                            }
                            if (lastScene != null && lastScene.isRunning()) {
                                lastScene.enqueueInputEvent(e, scale);
                            }
                        }
                    });
                    gameFrame.addKeyListener(new KeyAdapter() {
                        @Override
                        public void keyPressed(KeyEvent e) {
                            GameScene lastScene;
                            synchronized (scenes) {
                                lastScene = scenes.get(scenes.size() - 1);
                            }
                            if (lastScene.isRunning()) {
                                lastScene.enqueueInputEvent(e);
                            }
                        }

                        @Override
                        public void keyReleased(KeyEvent e) {
                            GameScene lastScene;
                            synchronized (scenes) {
                                lastScene = scenes.get(scenes.size() - 1);
                            }
                            if (lastScene.isRunning()) {
                                lastScene.enqueueInputEvent(e);
                            }
                        }
                    });
                    gameImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                    int[] gamePixels = ((DataBufferInt) gameImage.getRaster().getDataBuffer()).getData();
                    gameImageRgb = new RgbImage(gamePixels, width, height);
                }
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            interrupted = true;
        }
    }
}
