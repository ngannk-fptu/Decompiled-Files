/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.renderer;

import java.awt.Paint;

public interface PaintScale {
    public double getLowerBound();

    public double getUpperBound();

    public Paint getPaint(double var1);
}

