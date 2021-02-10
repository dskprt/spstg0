package com.github.dskprt.spstg0.input;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Mouse implements MouseListener, MouseMotionListener {

    private static final int BUTTON_COUNT = 3;

    private static final boolean[] state;
    private static final ButtonState[] buttons;

    private static Point position;

    static {
        position = new Point(0, 0);

        state = new boolean[BUTTON_COUNT];
        buttons = new ButtonState[BUTTON_COUNT];

        for(int i = 0; i < BUTTON_COUNT; ++i) {
            buttons[i] = ButtonState.RELEASED;
        }
    }

    public synchronized static void poll() {
        for(int i = 0; i < BUTTON_COUNT; i++) {
            if(state[i]) {
                if(buttons[i] == ButtonState.RELEASED) {
                    buttons[i] = ButtonState.CLICKED;
                } else {
                    buttons[i] = ButtonState.PRESSED;
                }
            } else {
                buttons[i] = ButtonState.RELEASED;
            }
        }
    }

    public static Point getPosition() {
        return position;
    }

    public static boolean isButtonDown(int button) {
        return buttons[button - 1] == ButtonState.CLICKED ||
                buttons[button - 1] == ButtonState.PRESSED;
    }

    public static boolean wasButtonClicked(int button) {
        return buttons[button - 1] == ButtonState.CLICKED;
    }

    @Override
    public synchronized void mousePressed(MouseEvent e) {
        state[e.getButton() - 1] = true;
    }

    @Override
    public synchronized void mouseReleased(MouseEvent e) {
        state[e.getButton() - 1] = false;
    }

    @Override
    public synchronized void mouseEntered(MouseEvent e) {
        mouseMoved(e);
    }

    @Override
    public synchronized void mouseExited(MouseEvent e) {
        mouseMoved(e);
    }

    @Override
    public synchronized void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    @Override
    public synchronized void mouseMoved(MouseEvent e) {
        position = e.getPoint();
    }

    @Override
    public void mouseClicked(MouseEvent e) { }

    public enum ButtonState {

        RELEASED,
        PRESSED,
        CLICKED
    }

    public static class Button {

        public static final int LEFT = 1;
        public static final int MIDDLE = 2;
        public static final int RIGHT = 3;
    }
}
