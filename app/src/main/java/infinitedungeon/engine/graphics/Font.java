package infinitedungeon.engine.graphics;

import java.util.ArrayList;

public class Font {

    private Tileset characters;
    private int color = 0xffff00ff;
    private String alphabet = " !\"#$%&'()*+,-./"
            + "0123456789:;<=>?"
            + "@ABCDEFGHIJKLMNO"
            + "PQRSTUVWXYZ[\\]^_"
            + "`abcdefghijklmno"
            + "pqrstuvwxyz{|}~ ";

    public Font(Tileset t) {
        characters = t;
    }

    public Font(Tileset t, int color) {
        this(t);
        this.color = color;
    }

    public int getCharWidth() {
        return characters.getTileWidth();
    }

    public int getCharHeight() {
        return characters.getTileHeight();
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void setAlphabet(String alphabet) {
        this.alphabet = alphabet;
    }

    public String getAlphabet() {
        return alphabet;
    }

    private void checkString(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (alphabet.indexOf(s.charAt(i)) == -1) {
                throw new IllegalArgumentException("Argument character isn't in the alphabet " + alphabet);
            }
        }
    }

    public RgbImage getImageString(String s) {
        checkString(s);
        RgbImage im = new RgbImage(characters.getTileWidth() * s.length(), characters.getTileHeight());
        for (int i = 0; i < s.length(); i++) {
            char character = s.charAt(i);
            im.combine(characters.getTile(alphabet.indexOf(character)), i * characters.getTileWidth(), 0);
        }
        if (color != 0xffff00ff) {
            int[] rgb = im.getRgb();
            for (int i = 0; i < rgb.length; i++) {
                if (rgb[i] != 0xffff00ff) {
                    rgb[i] = color;
                }
            }
        }
        return im;
    }

    public RgbImage getImageString(String s, int rowSize, String separator) {
        checkString(s);
        ArrayList<String> rows = new ArrayList<>();
        String[] words;
        if (separator != null) {
            words = s.split(separator);
        } else {
            words = new String[] { s };
        }
        String wordBuffer;
        String currentRow = "";
        for (int i = 0; i < words.length; i++) {
            wordBuffer = words[i];
            if (wordBuffer.length() >= rowSize) {
                if (!currentRow.equals("")) {
                    rows.add(currentRow);
                    currentRow = "";
                }
                while (wordBuffer.length() > rowSize) {
                    rows.add(wordBuffer.substring(0, rowSize));
                    wordBuffer = wordBuffer.substring(rowSize, wordBuffer.length());
                }
                if (!wordBuffer.equals("")) {
                    currentRow += wordBuffer + " ";
                    wordBuffer = "";
                }
            } else if (currentRow.length() + 1 + wordBuffer.length() > rowSize) {
                rows.add(currentRow.substring(0, currentRow.length()));
                currentRow = wordBuffer;
            } else {
                currentRow += " " + wordBuffer;
                if (currentRow.startsWith(" ")) {
                    currentRow = currentRow.substring(1);
                }
            }
            if (i == words.length - 1 && !currentRow.equals("")) {
                rows.add(currentRow);
            }
        }
        RgbImage im = new RgbImage(rowSize * characters.getTileWidth(), rows.size() * characters.getTileHeight());
        for (int i = 0; i < rows.size(); i++) {
            im.combine(getImageString(rows.get(i)), 0, i * characters.getTileHeight());
        }
        return im;
    }

    public void writeOnImage(String s, int rowSize, String separator, int offX, int offY, RgbImage target) {
        RgbImage im = getImageString(s, rowSize, separator);
        target.combine(im, offX, offY);
    }

    public void writeOnImage(String s, int rowSize, String separator, RgbImage target) {
        RgbImage im = getImageString(s, rowSize, separator);
        target.combine(im, (target.getWidth() - im.getWidth()) / 2, (target.getHeight() - im.getHeight()) / 2);
    }
}
