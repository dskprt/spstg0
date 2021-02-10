package org.example.game;

import org.example.game.input.Keyboard;
import org.example.game.input.Mouse;
import org.example.game.util.GameState;
import org.example.game.util.Timer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferStrategy;
import java.awt.image.VolatileImage;
import java.io.IOException;
import java.util.Properties;

public class Game {

    public static Game INSTANCE;

    public final Properties gameInfo;

    public final JFrame frame;
    public final Canvas canvas;

    private Graphics g = null;
    private Graphics2D g2d = null;
    public Font font;

    public GameState state;
    public Timer timer;

    private int x = 50;
    private int y = 50;

    public Game() {
        INSTANCE = this;

        gameInfo = new Properties();

        try {
            gameInfo.load(this.getClass().getResourceAsStream("/game.properties"));
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        frame = new JFrame((String) gameInfo.get("window_title"));
        frame.setIgnoreRepaint(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                shutdown();
            }
        });

        canvas = new Canvas();
        canvas.setIgnoreRepaint(true);
        canvas.setSize(Integer.parseInt((String) gameInfo.get("window_width")), Integer.parseInt((String) gameInfo.get("window_height")));

        frame.add(canvas);
        frame.pack();

        canvas.requestFocus();
        canvas.addKeyListener(new Keyboard());

        Mouse m = new Mouse();

        canvas.addMouseListener(m);
        canvas.addMouseMotionListener(m);
    }

    public void run() {
        frame.setVisible(true);

        canvas.createBufferStrategy(2);
        BufferStrategy buffer = canvas.getBufferStrategy();

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();

        VolatileImage img = gc.createCompatibleVolatileImage(canvas.getWidth(), canvas.getHeight());

        timer = new Timer();
        state = GameState.RUNNING;

        try {
            font = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/assets/fonts/04B_03.ttf")).deriveFont(16f);
        } catch(FontFormatException | IOException e) {
            e.printStackTrace();
        }

        while(state != GameState.STOPPED) {
            update(timer.getDelta());

            try {
                if(img.validate(gc) == VolatileImage.IMAGE_INCOMPATIBLE) {
                    img = gc.createCompatibleVolatileImage(canvas.getWidth(), canvas.getHeight());
                }

                g2d = img.createGraphics();

                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

                g2d.setFont(font);

                // TODO change this to your liking
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

                render(g2d);

                g = buffer.getDrawGraphics();
                g.drawImage(img, 0, 0, canvas.getWidth(), canvas.getHeight(), null);

                if(!buffer.contentsLost()) buffer.show();

                Thread.yield();
            } finally {
                if(g != null) g.dispose();
                if(g2d != null) g2d.dispose();
            }
        }

        shutdown();
        System.exit(0);
    }

    private void update(double delta) {
        timer.tick();

        Keyboard.poll();
        Mouse.poll();

        // TODO update your game here (world, player etc.)
        if(Keyboard.isKeyDown(KeyEvent.VK_UP)) {
            y -= delta * 0.2;
        }

        if(Keyboard.isKeyDown(KeyEvent.VK_DOWN)) {
            y += delta * 0.2;
        }

        if(Keyboard.isKeyDown(KeyEvent.VK_LEFT)) {
            x -= delta * 0.2;
        }

        if(Keyboard.isKeyDown(KeyEvent.VK_RIGHT)) {
            x += delta * 0.2;
        }
    }

    private void render(Graphics2D g2d) {
        // TODO place all of your rendering logic here
        g2d.setColor(Color.GREEN);
        g2d.drawString(String.format("FPS: %s", timer.getFps()), 5, 5 + 16);
        g2d.drawString(String.format("Mouse[x=%s,y=%s] Button0=%s, Button1=%s, Button2=%s",
                Mouse.getPosition().x, Mouse.getPosition().y, Mouse.isButtonDown(Mouse.Button.LEFT),
                Mouse.isButtonDown(Mouse.Button.MIDDLE), Mouse.isButtonDown(Mouse.Button.RIGHT)), 5, 5 + (16 * 2) + 2);

        g2d.setColor(Color.WHITE);
        g2d.drawString("Hello world!", x, y);
    }

    private void shutdown() {
        System.out.println("Shutting down.");

        // TODO place all of your shutdown logic here
    }
}
