/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.plot;

import java.awt.geom.Point2D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;

public interface Pannable {
    public PlotOrientation getOrientation();

    public boolean isDomainPannable();

    public boolean isRangePannable();

    public void panDomainAxes(double var1, PlotRenderingInfo var3, Point2D var4);

    public void panRangeAxes(double var1, PlotRenderingInfo var3, Point2D var4);
}

