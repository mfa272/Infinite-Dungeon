package infinitedungeon.engine;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.TimeUnit;

import infinitedungeon.engine.graphics.GameElement;
import infinitedungeon.engine.graphics.GUI.GUIPanel;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeMap;

public class GameScene {

    private final TaskQueue tq;
    private final ArrayDeque<SceneInputEvent> inputEvents;
    private final ArrayList<SimpleEntry<GameElement, Integer>> elements;
    private final TreeMap<Integer, Runnable> keyPressedEvents;
    private final TreeMap<Integer, Runnable> keyReleasedEvents;
    private boolean wasPaused;
    private boolean running;
    private boolean visible;
    private World world;
    private GameElement hoveredElement;
    private int cameraX;
    private int cameraY;

    public GameScene() {
        running = true;
        visible = true;
        tq = new TaskQueue();
        elements = new ArrayList<>();
        keyPressedEvents = new TreeMap<>();
        keyReleasedEvents = new TreeMap<>();
        inputEvents = new ArrayDeque<>();
    }

    private interface SceneInputEvent {

        public void process();
    }

    private class SceneMouseEvent implements SceneInputEvent {

        private final int x;
        private final int y;
        private final int id;
        private final int button;

        SceneMouseEvent(int x, int y, int id, int button) {
            this.x = x;
            this.y = y;
            this.id = id;
            this.button = button;
        }

        @Override
        public void process() {
            switch (id) {
                case MouseEvent.MOUSE_MOVED:
                    mouseMoved(x, y);
                    break;
                case MouseEvent.MOUSE_CLICKED:
                    if (hoveredElement != null) {
                        switch (button) {
                            case MouseEvent.BUTTON1:
                                hoveredElement.getMouseEventsHandler().mouseButton1Clicked();
                                break;
                            case MouseEvent.BUTTON2:
                                hoveredElement.getMouseEventsHandler().mouseButton2Clicked();
                                break;
                            case MouseEvent.BUTTON3:
                                hoveredElement.getMouseEventsHandler().mouseButton3Clicked();
                                break;
                        }
                    }
                    break;
                case MouseEvent.MOUSE_PRESSED:
                    if (hoveredElement != null) {
                        switch (button) {
                            case MouseEvent.BUTTON1:
                                hoveredElement.getMouseEventsHandler().mouseButton1Pressed();
                                break;
                            case MouseEvent.BUTTON2:
                                hoveredElement.getMouseEventsHandler().mouseButton2Pressed();
                                break;
                            case MouseEvent.BUTTON3:
                                hoveredElement.getMouseEventsHandler().mouseButton3Pressed();
                                break;
                        }
                    }
                    break;
                case MouseEvent.MOUSE_RELEASED:
                    if (hoveredElement != null) {
                        switch (button) {
                            case MouseEvent.BUTTON1:
                                hoveredElement.getMouseEventsHandler().mouseButton1Released();
                                break;
                            case MouseEvent.BUTTON2:
                                hoveredElement.getMouseEventsHandler().mouseButton2Released();
                                break;
                            case MouseEvent.BUTTON3:
                                hoveredElement.getMouseEventsHandler().mouseButton3Released();
                                break;
                        }
                    }
                    break;
            }
        }
    }

    private class SceneKeyboardEvent implements SceneInputEvent {

        private int id;
        private int keyCode;

        SceneKeyboardEvent(int id, int keyCode) {
            this.id = id;
            this.keyCode = keyCode;
        }

        @Override
        public void process() {
            switch (id) {
                case KeyEvent.KEY_PRESSED:
                    Runnable p = keyPressedEvents.get(keyCode);
                    if (p != null) {
                        p.run();
                    }
                    break;
                case KeyEvent.KEY_RELEASED:
                    Runnable r = keyReleasedEvents.get(keyCode);
                    if (r != null) {
                        r.run();
                    }
                    break;
            }
        }
    }

    public void enqueueInputEvent(MouseEvent e, double scale) {
        synchronized (inputEvents) {
            inputEvents.add(
                    new SceneMouseEvent((int) (e.getX() / scale), (int) (e.getY() / scale), e.getID(), e.getButton()));
        }
    }

    public void enqueueInputEvent(KeyEvent e) {
        synchronized (inputEvents) {
            inputEvents.add(new SceneKeyboardEvent(e.getID(), e.getExtendedKeyCode()));
        }
    }

    public void processInputEvents() {
        synchronized (inputEvents) {
            SceneInputEvent e = inputEvents.poll();
            while (e != null) {
                e.process();
                e = inputEvents.poll();
            }
        }
    }

    public int getCameraX() {
        return cameraX;
    }

    public void setCameraX(int cameraX) {
        this.cameraX = cameraX;
    }

    public int getCameraY() {
        return cameraY;
    }

    public void setCameraY(int cameraY) {
        this.cameraY = cameraY;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isRunning() {
        return running;
    }

    public ArrayList<GameElement> getElements() {
        ArrayList<GameElement> elems = new ArrayList<>();
        for (SimpleEntry<GameElement, Integer> e : elements) {
            elems.add(e.getKey());
        }
        return elems;
    }

    public void addKeyPressedEvent(int keyCode, Runnable t) {
        keyPressedEvents.put(keyCode, t);
    }

    public void addKeyReleasedEvent(int keyCode, Runnable t) {
        keyReleasedEvents.put(keyCode, t);
    }

    public void removeKeyPressedEvent(int keyCode) {
        keyPressedEvents.remove(keyCode);
    }

    public void removeKeyReleasedEvent(int keyCode) {
        keyReleasedEvents.remove(keyCode);
    }

    public void clearKeyPressedEvents() {
        keyPressedEvents.clear();
    }

    public void clearKeyReleasedEvents() {
        keyReleasedEvents.clear();
    }

    public void addBackgroundElement(GameElement elem) {
        elements.add(new SimpleEntry<>(elem, 0));
    }

    public void addElement(GameElement elem) {
        elements.add(new SimpleEntry<>(elem, 1));
    }

    public void addForegroundElement(GameElement elem) {
        elements.add(new SimpleEntry<>(elem, 2));
    }

    public void addGUIPanel(GUIPanel p) {
        elements.add(new SimpleEntry<>(p, 3));
    }

    public boolean removeElement(GameElement element) {
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i).getKey() == element) {
                elements.remove(i);
                return true;
            }
        }
        return false;
    }

    public void addTimedTask(TimedTask t) {
        tq.add(t);
    }

    public void cancelTasks() {
        tq.cancelTasks();
    }

    void performTasks(long timeStamp, TimeUnit unit) {
        tq.performTasks(timeStamp, unit);
    }

    void sortElements() {
        Collections.sort(elements, new Comparator<SimpleEntry<GameElement, Integer>>() {
            @Override
            public int compare(SimpleEntry<GameElement, Integer> o1, SimpleEntry<GameElement, Integer> o2) {
                int priority1 = o1.getValue();
                int priority2 = o2.getValue();
                int prioCompare = Integer.compare(priority1, priority2);
                if (prioCompare != 0) {
                    return prioCompare;
                }
                if (priority1 == 3) {
                    return Integer.compare(elements.indexOf(o1), elements.indexOf(o2));
                }
                int y1 = o1.getKey().getDrawY(0);
                int y2 = o2.getKey().getDrawY(0);
                int height1 = o1.getKey().getDrawImage().getHeight();
                int height2 = o2.getKey().getDrawImage().getHeight();
                int positionCompare = Integer.compare(y1 + height1, y2 + height2);
                if (positionCompare != 0) {
                    return positionCompare;
                }
                return Integer.compare(elements.indexOf(o1), elements.indexOf(o2));
            }
        });
    }

    void setTaskTime(long time, TimeUnit unit) {
        tq.setTaskTime(time, unit);
    }

    void pause() {
        wasPaused = true;
        running = false;
    }

    void resume(long time, TimeUnit unit) {
        if (wasPaused) {
            tq.setTaskTime(time, unit);
            wasPaused = false;
            running = true;
        }
    }

    private void mouseMoved(int mouseX, int mouseY) {
        for (int i = elements.size() - 1; i >= 0; i--) {
            GameElement current = elements.get(i).getKey();
            if (current.isEnabled()) {
                boolean hoveringChange = current.updateMouseHoverStatus(mouseX, mouseY, cameraX, cameraY);
                if (current.isHovered()) {
                    if (hoveredElement != null && hoveredElement != current) {
                        hoveredElement.getMouseEventsHandler().mouseUnhovered();
                        hoveredElement.unhover();
                    }
                    if (hoveringChange) {
                        current.getMouseEventsHandler().mouseHovered();
                    }
                    hoveredElement = current;
                    return;
                }
            }
        }
        if (hoveredElement != null) {
            hoveredElement.getMouseEventsHandler().mouseUnhovered();
            hoveredElement.unhover();
            hoveredElement = null;
        }
    }
}
