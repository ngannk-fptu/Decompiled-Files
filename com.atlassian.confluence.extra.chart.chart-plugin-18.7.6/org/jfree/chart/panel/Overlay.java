/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.panel;

import java.awt.Graphics2D;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.event.OverlayChangeListener;

public interface Overlay {
    public void paintOverlay(Graphics2D var1, ChartPanel var2);

    public void addChangeListener(OverlayChangeListener var1);

    public void removeChangeListener(OverlayChangeListener var1);
}

