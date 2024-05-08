package infinitedungeon.game;

import java.awt.Rectangle;
import java.util.ArrayDeque;

import infinitedungeon.game.characters.Character;
import infinitedungeon.game.items.Item;

public class Square {

    private final ArrayDeque<Item> items;
    private final Type t;
    private Character c;
    private Square top;
    private Square left;
    private Square right;
    private Square bottom;
    private Rectangle room;

    public enum Type {
        GROUND,
        WALL,
        EMPTY,
        STAIRS
    }

    public Square(Type t) {
        this.t = t;
        items = new ArrayDeque<>();
    }

    public Type getType() {
        return t;
    }

    public Item getFirstItem() {
        return items.peek();
    }

    public Item pickFirstItem() {
        Item it = items.poll();
        it.setPicked(true);
        return it;
    }

    public void addItem(Item it) {
        items.add(it);
    }

    public Rectangle getRoom() {
        return room;
    }

    public void setRoom(Rectangle room) {
        this.room = room;
    }

    public Character getCharacter() {
        return c;
    }

    public void setCharacter(Character c) {
        this.c = c;
    }

    public Square getTop() {
        return top;
    }

    public void setTop(Square top) {
        this.top = top;
    }

    public Square getLeft() {
        return left;
    }

    public void setLeft(Square left) {
        this.left = left;
    }

    public Square getRight() {
        return right;
    }

    public void setRight(Square right) {
        this.right = right;
    }

    public Square getBottom() {
        return bottom;
    }

    public void setBottom(Square bottom) {
        this.bottom = bottom;
    }

    public boolean isWalkable() {
        return t == Type.GROUND && !containsCharacter() || t == Type.STAIRS;
    }

    public boolean contains(Character c) {
        return this.c == c;
    }

    public boolean containsCharacter() {
        return !contains(null);
    }
}
