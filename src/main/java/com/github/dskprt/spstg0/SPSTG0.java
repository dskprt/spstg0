package com.github.dskprt.spstg0;

import com.github.dskprt.spstg0.gui.Screen;
import com.github.dskprt.spstg0.gui.screens.MainMenuScreen;
import com.github.dskprt.spstg0.input.Keyboard;
import com.github.dskprt.spstg0.input.Mouse;
import com.github.dskprt.spstg0.util.GameState;
import com.github.dskprt.spstg0.util.Timer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.VolatileImage;
import java.io.IOException;
import java.util.Properties;

public class SPSTG0 {

    public static int width;
    public static int height;
    public static int scaleFactor;

    public static Properties gameInfo;

    public static JFrame frame;
    public static Canvas canvas;

    private static Graphics g = null;
    private static Graphics2D g2d = null;

    public static Font font;
    public static FontMetrics fontMetrics;

    public static GameState state;
    public static Timer timer;

    private static Screen screen;

    public static void init() {
        gameInfo = new Properties();

        try {
            gameInfo.load(SPSTG0.class.getResourceAsStream("/game.properties"));
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        width = Integer.parseInt((String) gameInfo.get("game_width"));
        height = Integer.parseInt((String) gameInfo.get("game_height"));

        scaleFactor = Integer.parseInt((String) gameInfo.get("window_width")) / width;

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

    public static void run() {
        frame.setVisible(true);

        canvas.createBufferStrategy(2);
        BufferStrategy buffer = canvas.getBufferStrategy();

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();

        VolatileImage img = gc.createCompatibleVolatileImage(width, height);

        timer = new Timer();
        state = GameState.RUNNING;

        setScreen(new MainMenuScreen());

        try {
            font = Font.createFont(Font.TRUETYPE_FONT, SPSTG0.class.getResourceAsStream("/assets/fonts/04B_03.ttf")).deriveFont(8f);
        } catch(FontFormatException | IOException e) {
            e.printStackTrace();
        }

        while(state != GameState.STOPPED) {
            update(timer.getDelta());

            try {
                if(img.validate(gc) == VolatileImage.IMAGE_INCOMPATIBLE) {
                    img = gc.createCompatibleVolatileImage(width, height);
                }

                g2d = img.createGraphics();
                g2d.setFont(font);

                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

                render(g2d);

                g = buffer.getDrawGraphics();

                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
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

    private static void update(double delta) {
        timer.tick();

        Keyboard.poll();
        Mouse.poll();

        if(screen != null) screen.update(delta);
    }

    private static void render(Graphics2D g2d) {
        if(fontMetrics == null) fontMetrics = g2d.getFontMetrics();

        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width, height);

        if(screen != null) screen.render(g2d);

        g2d.setColor(Color.GREEN);
        g2d.drawString(String.format("FPS: %s", timer.getFps()), 5, 5 + 8);
        g2d.drawString(String.format("Mouse[x=%s,y=%s] Button0=%s, Button1=%s, Button2=%s",
                Mouse.getPosition().x, Mouse.getPosition().y, Mouse.isButtonDown(Mouse.Button.LEFT),
                Mouse.isButtonDown(Mouse.Button.MIDDLE), Mouse.isButtonDown(Mouse.Button.RIGHT)), 5, 5 + (8 * 2) + 2);
    }

    private static void shutdown() {
        System.out.println("Shutting down.");

        // TODO place all of your shutdown logic here
    }

    public static Screen getScreen() {
        return screen;
    }

    public static void setScreen(Screen screen) {
        SPSTG0.screen = screen;
    }
}
