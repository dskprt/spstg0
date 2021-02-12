package com.github.dskprt.spstg0.gui.screens;

import com.github.dskprt.spstg0.SPSTG0;
import com.github.dskprt.spstg0.gui.Screen;

import java.awt.*;

public class MainMenuScreen extends Screen {

    private final String play = "Press [x] to play.";

    @Override
    public void init() {

    }

    @Override
    public void render(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);

        int x = ((SPSTG0.width - SPSTG0.fontMetrics.stringWidth(play)) / 2);
        g2d.drawString(play, x, SPSTG0.height - 50);
    }

    @Override
    public void update(double delta) {

    }
}
