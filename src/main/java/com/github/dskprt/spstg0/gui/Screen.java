package com.github.dskprt.spstg0.gui;

import java.awt.*;

public abstract class Screen {

    public abstract void init();
    public abstract void render(Graphics2D g2d);
    public abstract void update(double delta);
}
