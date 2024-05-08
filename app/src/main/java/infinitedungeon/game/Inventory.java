package infinitedungeon.game;

import static infinitedungeon.game.InfiniteDungeon.FONT_SMALL_TILESET;

import infinitedungeon.engine.MouseEventsHandler;
import infinitedungeon.engine.graphics.Font;
import infinitedungeon.engine.graphics.RgbImage;
import infinitedungeon.engine.graphics.GUI.GUIMessage;
import infinitedungeon.engine.graphics.GUI.GUIPanel;
import infinitedungeon.game.characters.Player;
import infinitedungeon.game.items.Armor;
import infinitedungeon.game.items.Item;
import infinitedungeon.game.items.Sword;

public class Inventory {

    private final Player p;
    private final GUIPanel inventoryPanel;
    private final GUIPanel swordSlot;
    private final GUIPanel armorSlot;
    private final GUIPanel[] inventorySlots;
    private final GUIMessage tooltipPanel;
    private boolean rightButtonClicked;

    public GUIMessage getTooltipPanel() {
        return tooltipPanel;
    }

    public Inventory(Player p, int x, int y, int slotWidth, int slotHeight, int tooltipWidth) {
        this.inventorySlots = new GUIPanel[3];
        this.p = p;
        inventoryPanel = new GUIPanel(x + slotWidth * 2, y + slotHeight, InfiniteDungeon.INVENTORY_IMAGE);
        swordSlot = new GUIPanel(x, y + slotWidth, new RgbImage(0, 0));
        armorSlot = new GUIPanel(x + slotWidth, y + 32, new RgbImage(0, 0));
        inventorySlots[0] = new GUIPanel(x + slotWidth * 2,
                y + slotHeight, new RgbImage(0, 0));
        inventorySlots[1] = new GUIPanel(x + slotWidth * 3,
                y + slotHeight, new RgbImage(0, 0));
        inventorySlots[2] = new GUIPanel(x + slotWidth * 4,
                y + slotHeight, new RgbImage(0, 0));
        tooltipPanel = new GUIMessage(x, y, new RgbImage(tooltipWidth, slotHeight));
        tooltipPanel.setFont(new Font(FONT_SMALL_TILESET, 0xFFFFFF));
        swordSlot.setMouseEventsHandler(new MouseEventsHandler() {
            @Override
            public void mouseHovered() {
                Sword s = p.getSword();
                if (s != null) {
                    tooltipPanel.setText(s.toString(), s.toString().length(),
                            null, 0, 0);
                }
            }

            @Override
            public void mouseUnhovered() {
                tooltipPanel.deleteText();
            }
        });

        armorSlot.setMouseEventsHandler(new MouseEventsHandler() {
            @Override
            public void mouseHovered() {
                Armor a = p.getArmor();
                if (a != null) {
                    tooltipPanel.setText(a.toString(), a.toString().length(),
                            null, slotWidth, 0);
                }
            }

            @Override
            public void mouseUnhovered() {
                tooltipPanel.deleteText();
            }
        });

        for (int i = 0; i < 3; i++) {
            int slot = i;
            inventorySlots[slot].setMouseEventsHandler(new MouseEventsHandler() {

                @Override
                public void mouseButton1Clicked() {
                    Item it = p.getInventoryItems()[slot];
                    if (it != null) {
                        it.use(p);
                        if (it.isExpired()) {
                            p.destroyItem(it);
                        }
                        if (p.getInventoryItems()[slot] == it) {
                            tooltipPanel.setText(it.toString(),
                                    it.toString().length(), null,
                                    slotWidth * 2 + slotWidth * slot, 0);
                        } else {
                            tooltipPanel.deleteText();
                        }
                        updateSlots();
                    }
                }

                @Override
                public void mouseButton3Clicked() {
                    if (rightButtonClicked) {
                        Item it = p.getInventoryItems()[slot];
                        if (it != null) {
                            p.destroyItem(it);
                            tooltipPanel.deleteText();
                            updateSlots();
                        }
                    } else {
                        rightButtonClicked = true;
                        tooltipPanel.setText("CONFERMI?", 9, null,
                                slotWidth * 2 + slotWidth * slot, 0);
                    }
                }

                @Override
                public void mouseHovered() {
                    Item it = p.getInventoryItems()[slot];
                    if (it != null) {
                        tooltipPanel.setText(it.toString(), it.toString().length(),
                                null, slotWidth * 2 + slotWidth * slot, 0);
                    }
                }

                @Override
                public void mouseUnhovered() {
                    rightButtonClicked = false;
                    tooltipPanel.deleteText();
                }
            });
        }
    }

    public GUIPanel getInventoryPanel() {
        return inventoryPanel;
    }

    public GUIPanel getSwordSlot() {
        return swordSlot;
    }

    public GUIPanel getArmorSlot() {
        return armorSlot;
    }

    public GUIPanel[] getInventorySlots() {
        return inventorySlots;
    }

    public void updateSlots() {
        if (p.getSword() != null) {
            swordSlot.setDrawImage(p.getSword().getDrawImage());
        } else {
            swordSlot.setDrawImage(new RgbImage(0, 0));
        }
        if (p.getArmor() != null) {
            armorSlot.setDrawImage(p.getArmor().getDrawImage());
        } else {
            armorSlot.setDrawImage(new RgbImage(0, 0));
        }
        Item[] playerItems = p.getInventoryItems();
        for (int i = 0; i < 3; i++) {
            if (playerItems[i] != null) {
                inventorySlots[i].setDrawImage(playerItems[i].getDrawImage());
            } else {
                inventorySlots[i].setDrawImage(new RgbImage(0, 0));
            }
        }
    }
}
