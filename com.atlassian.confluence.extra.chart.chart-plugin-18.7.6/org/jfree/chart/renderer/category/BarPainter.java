/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.renderer.category;

import java.awt.Graphics2D;
import java.awt.geom.RectangularShape;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.ui.RectangleEdge;

public interface BarPainter {
    public void paintBar(Graphics2D var1, BarRenderer var2, int var3, int var4, RectangularShape var5, RectangleEdge var6);

    public void paintBarShadow(Graphics2D var1, BarRenderer var2, int var3, int var4, RectangularShape var5, RectangleEdge var6, boolean var7);
}

