/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.plot.dial;

import org.jfree.chart.plot.dial.DialLayer;

public interface DialScale
extends DialLayer {
    public double valueToAngle(double var1);

    public double angleToValue(double var1);
}

