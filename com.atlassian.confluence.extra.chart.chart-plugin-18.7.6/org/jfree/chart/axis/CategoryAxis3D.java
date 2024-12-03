/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.axis;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import org.jfree.chart.Effect3D;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.CategoryAnchor;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.ui.RectangleEdge;

public class CategoryAxis3D
extends CategoryAxis
implements Cloneable,
Serializable {
    private static final long serialVersionUID = 4114732251353700972L;

    public CategoryAxis3D() {
        this(null);
    }

    public CategoryAxis3D(String label) {
        super(label);
    }

    public AxisState draw(Graphics2D g2, double cursor, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, PlotRenderingInfo plotState) {
        if (!this.isVisible()) {
            return new AxisState(cursor);
        }
        CategoryPlot plot = (CategoryPlot)this.getPlot();
        Rectangle2D.Double adjustedDataArea = new Rectangle2D.Double();
        if (plot.getRenderer() instanceof Effect3D) {
            Effect3D e3D = (Effect3D)((Object)plot.getRenderer());
            double adjustedX = dataArea.getMinX();
            double adjustedY = dataArea.getMinY();
            double adjustedW = dataArea.getWidth() - e3D.getXOffset();
            double adjustedH = dataArea.getHeight() - e3D.getYOffset();
            if (edge == RectangleEdge.LEFT || edge == RectangleEdge.BOTTOM) {
                adjustedY += e3D.getYOffset();
            } else if (edge == RectangleEdge.RIGHT || edge == RectangleEdge.TOP) {
                adjustedX += e3D.getXOffset();
            }
            ((Rectangle2D)adjustedDataArea).setRect(adjustedX, adjustedY, adjustedW, adjustedH);
        } else {
            ((Rectangle2D)adjustedDataArea).setRect(dataArea);
        }
        if (this.isAxisLineVisible()) {
            this.drawAxisLine(g2, cursor, adjustedDataArea, edge);
        }
        AxisState state = new AxisState(cursor);
        if (this.isTickMarksVisible()) {
            this.drawTickMarks(g2, cursor, adjustedDataArea, edge, state);
        }
        state = this.drawCategoryLabels(g2, plotArea, adjustedDataArea, edge, state, plotState);
        state = this.drawLabel(this.getLabel(), g2, plotArea, dataArea, edge, state);
        return state;
    }

    public double getCategoryJava2DCoordinate(CategoryAnchor anchor, int category, int categoryCount, Rectangle2D area, RectangleEdge edge) {
        double result = 0.0;
        Rectangle2D adjustedArea = area;
        CategoryPlot plot = (CategoryPlot)this.getPlot();
        CategoryItemRenderer renderer = plot.getRenderer();
        if (renderer instanceof Effect3D) {
            Effect3D e3D = (Effect3D)((Object)renderer);
            double adjustedX = area.getMinX();
            double adjustedY = area.getMinY();
            double adjustedW = area.getWidth() - e3D.getXOffset();
            double adjustedH = area.getHeight() - e3D.getYOffset();
            if (edge == RectangleEdge.LEFT || edge == RectangleEdge.BOTTOM) {
                adjustedY += e3D.getYOffset();
            } else if (edge == RectangleEdge.RIGHT || edge == RectangleEdge.TOP) {
                adjustedX += e3D.getXOffset();
            }
            adjustedArea = new Rectangle2D.Double(adjustedX, adjustedY, adjustedW, adjustedH);
        }
        if (anchor == CategoryAnchor.START) {
            result = this.getCategoryStart(category, categoryCount, adjustedArea, edge);
        } else if (anchor == CategoryAnchor.MIDDLE) {
            result = this.getCategoryMiddle(category, categoryCount, adjustedArea, edge);
        } else if (anchor == CategoryAnchor.END) {
            result = this.getCategoryEnd(category, categoryCount, adjustedArea, edge);
        }
        return result;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

