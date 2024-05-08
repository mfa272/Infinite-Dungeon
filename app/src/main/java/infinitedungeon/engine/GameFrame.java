package infinitedungeon.engine;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

class GameFrame extends JFrame {

    private final int width;
    private final int height;
    private final double scale;
    private final boolean windowed;
    private final ImagePanel panel;
    private int horizontalMargin;
    private int verticalMargin;

    private class ImagePanel extends JPanel {
        private BufferedImage image;

        public void setImage(BufferedImage image) {
            this.image = image;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, (int) (width * scale), (int) (height * scale), null);
        }
    };

    public GameFrame(int canvasWidth, int canvasHeight, double canvasScale, boolean windowed) {
        this.width = canvasWidth;
        this.height = canvasHeight;
        this.scale = canvasScale;
        this.windowed = windowed;
        panel = new ImagePanel();
    }

    public void init() {
        getContentPane().setBackground(Color.BLACK);
        FlowLayout layout = new FlowLayout();
        setLayout(layout);
        setResizable(false);
        add(panel);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                panel.requestFocusInWindow();
            }
        });
        Dimension size = new Dimension((int) (width * scale), (int) (height * scale));
        panel.setPreferredSize(size);
        if (!windowed) {
            setUndecorated(true);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int screenWidth = (int) screenSize.getWidth();
            int screenHeight = (int) screenSize.getHeight();
            verticalMargin = (int) (screenHeight - height * scale) / 2;
            horizontalMargin = (int) (screenWidth - width * scale) / 2;
            pack();
            setSize(screenSize);
        } else {
            setUndecorated(false);
            verticalMargin = 0;
            horizontalMargin = 0;
            pack();
            Insets insets = getInsets();
            setSize(size.width + insets.left + insets.right, size.height + insets.top + insets.bottom);
        }
        layout.setVgap(verticalMargin);
        layout.setHgap(horizontalMargin);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    public void draw(BufferedImage image) {
        panel.setImage(image);
        panel.repaint();
    }

    @Override
    public void addMouseListener(MouseListener l) {
        panel.addMouseListener(l);
    }

    @Override
    public void addMouseMotionListener(MouseMotionListener l) {
        panel.addMouseMotionListener(l);
    }

    @Override
    public void addKeyListener(KeyListener l) {
        panel.addKeyListener(l);
    }
}
