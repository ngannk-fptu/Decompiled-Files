/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.plot.dial;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.EventListener;
import org.jfree.chart.plot.dial.DialLayerChangeListener;
import org.jfree.chart.plot.dial.DialPlot;

public interface DialLayer {
    public boolean isVisible();

    public void addChangeListener(DialLayerChangeListener var1);

    public void removeChangeListener(DialLayerChangeListener var1);

    public boolean hasListener(EventListener var1);

    public boolean isClippedToWindow();

    public void draw(Graphics2D var1, DialPlot var2, Rectangle2D var3, Rectangle2D var4);
}

