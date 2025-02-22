/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.axis;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.List;
import org.jfree.chart.Effect3D;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.ui.RectangleEdge;

public class NumberAxis3D
extends NumberAxis
implements Serializable {
    private static final long serialVersionUID = -1790205852569123512L;

    public NumberAxis3D() {
        this(null);
    }

    public NumberAxis3D(String label) {
        super(label);
    }

    public AxisState draw(Graphics2D g2, double cursor, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, PlotRenderingInfo plotState) {
        CategoryPlot cp;
        CategoryItemRenderer r;
        if (!this.isVisible()) {
            AxisState state = new AxisState(cursor);
            List ticks = this.refreshTicks(g2, state, dataArea, edge);
            state.setTicks(ticks);
            return state;
        }
        double xOffset = 0.0;
        double yOffset = 0.0;
        Plot plot = this.getPlot();
        if (plot instanceof CategoryPlot && (r = (cp = (CategoryPlot)plot).getRenderer()) instanceof Effect3D) {
            Effect3D e3D = (Effect3D)((Object)r);
            xOffset = e3D.getXOffset();
            yOffset = e3D.getYOffset();
        }
        double adjustedX = dataArea.getMinX();
        double adjustedY = dataArea.getMinY();
        double adjustedW = dataArea.getWidth() - xOffset;
        double adjustedH = dataArea.getHeight() - yOffset;
        if (edge == RectangleEdge.LEFT || edge == RectangleEdge.BOTTOM) {
            adjustedY += yOffset;
        } else if (edge == RectangleEdge.RIGHT || edge == RectangleEdge.TOP) {
            adjustedX += xOffset;
        }
        Rectangle2D.Double adjustedDataArea = new Rectangle2D.Double(adjustedX, adjustedY, adjustedW, adjustedH);
        AxisState info = this.drawTickMarksAndLabels(g2, cursor, plotArea, adjustedDataArea, edge);
        info = this.drawLabel(this.getLabel(), g2, plotArea, dataArea, edge, info);
        return info;
    }
}

