/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.axis;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.data.category.CategoryDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;

public class SubCategoryAxis
extends CategoryAxis
implements Cloneable,
Serializable {
    private static final long serialVersionUID = -1279463299793228344L;
    private List subCategories;
    private Font subLabelFont = new Font("SansSerif", 0, 10);
    private transient Paint subLabelPaint = Color.black;

    public SubCategoryAxis(String label) {
        super(label);
        this.subCategories = new ArrayList();
    }

    public void addSubCategory(Comparable subCategory) {
        if (subCategory == null) {
            throw new IllegalArgumentException("Null 'subcategory' axis.");
        }
        this.subCategories.add(subCategory);
        this.notifyListeners(new AxisChangeEvent(this));
    }

    public Font getSubLabelFont() {
        return this.subLabelFont;
    }

    public void setSubLabelFont(Font font) {
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");
        }
        this.subLabelFont = font;
        this.notifyListeners(new AxisChangeEvent(this));
    }

    public Paint getSubLabelPaint() {
        return this.subLabelPaint;
    }

    public void setSubLabelPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.subLabelPaint = paint;
        this.notifyListeners(new AxisChangeEvent(this));
    }

    public AxisSpace reserveSpace(Graphics2D g2, Plot plot, Rectangle2D plotArea, RectangleEdge edge, AxisSpace space) {
        if (space == null) {
            space = new AxisSpace();
        }
        if (!this.isVisible()) {
            return space;
        }
        space = super.reserveSpace(g2, plot, plotArea, edge, space);
        double maxdim = this.getMaxDim(g2, edge);
        if (RectangleEdge.isTopOrBottom(edge)) {
            space.add(maxdim, edge);
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            space.add(maxdim, edge);
        }
        return space;
    }

    private double getMaxDim(Graphics2D g2, RectangleEdge edge) {
        double result = 0.0;
        g2.setFont(this.subLabelFont);
        FontMetrics fm = g2.getFontMetrics();
        Iterator iterator = this.subCategories.iterator();
        while (iterator.hasNext()) {
            Comparable subcategory = (Comparable)iterator.next();
            String label = subcategory.toString();
            Rectangle2D bounds = TextUtilities.getTextBounds(label, g2, fm);
            double dim = 0.0;
            dim = RectangleEdge.isLeftOrRight(edge) ? bounds.getWidth() : bounds.getHeight();
            result = Math.max(result, dim);
        }
        return result;
    }

    public AxisState draw(Graphics2D g2, double cursor, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, PlotRenderingInfo plotState) {
        if (!this.isVisible()) {
            return new AxisState(cursor);
        }
        if (this.isAxisLineVisible()) {
            this.drawAxisLine(g2, cursor, dataArea, edge);
        }
        AxisState state = new AxisState(cursor);
        state = this.drawSubCategoryLabels(g2, plotArea, dataArea, edge, state, plotState);
        state = this.drawCategoryLabels(g2, plotArea, dataArea, edge, state, plotState);
        state = this.drawLabel(this.getLabel(), g2, plotArea, dataArea, edge, state);
        return state;
    }

    protected AxisState drawSubCategoryLabels(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, AxisState state, PlotRenderingInfo plotState) {
        if (state == null) {
            throw new IllegalArgumentException("Null 'state' argument.");
        }
        g2.setFont(this.subLabelFont);
        g2.setPaint(this.subLabelPaint);
        CategoryPlot plot = (CategoryPlot)this.getPlot();
        int categoryCount = 0;
        CategoryDataset dataset = plot.getDataset();
        if (dataset != null) {
            categoryCount = dataset.getColumnCount();
        }
        double maxdim = this.getMaxDim(g2, edge);
        for (int categoryIndex = 0; categoryIndex < categoryCount; ++categoryIndex) {
            double x0 = 0.0;
            double x1 = 0.0;
            double y0 = 0.0;
            double y1 = 0.0;
            if (edge == RectangleEdge.TOP) {
                x0 = this.getCategoryStart(categoryIndex, categoryCount, dataArea, edge);
                x1 = this.getCategoryEnd(categoryIndex, categoryCount, dataArea, edge);
                y1 = state.getCursor();
                y0 = y1 - maxdim;
            } else if (edge == RectangleEdge.BOTTOM) {
                x0 = this.getCategoryStart(categoryIndex, categoryCount, dataArea, edge);
                x1 = this.getCategoryEnd(categoryIndex, categoryCount, dataArea, edge);
                y0 = state.getCursor();
                y1 = y0 + maxdim;
            } else if (edge == RectangleEdge.LEFT) {
                y0 = this.getCategoryStart(categoryIndex, categoryCount, dataArea, edge);
                y1 = this.getCategoryEnd(categoryIndex, categoryCount, dataArea, edge);
                x1 = state.getCursor();
                x0 = x1 - maxdim;
            } else if (edge == RectangleEdge.RIGHT) {
                y0 = this.getCategoryStart(categoryIndex, categoryCount, dataArea, edge);
                y1 = this.getCategoryEnd(categoryIndex, categoryCount, dataArea, edge);
                x0 = state.getCursor();
                x1 = x0 + maxdim;
            }
            Rectangle2D.Double area = new Rectangle2D.Double(x0, y0, x1 - x0, y1 - y0);
            int subCategoryCount = this.subCategories.size();
            float width = (float)((x1 - x0) / (double)subCategoryCount);
            float height = (float)((y1 - y0) / (double)subCategoryCount);
            float xx = 0.0f;
            float yy = 0.0f;
            for (int i = 0; i < subCategoryCount; ++i) {
                if (RectangleEdge.isTopOrBottom(edge)) {
                    xx = (float)(x0 + ((double)i + 0.5) * (double)width);
                    yy = (float)area.getCenterY();
                } else {
                    xx = (float)area.getCenterX();
                    yy = (float)(y0 + ((double)i + 0.5) * (double)height);
                }
                String label = this.subCategories.get(i).toString();
                TextUtilities.drawRotatedString(label, g2, xx, yy, TextAnchor.CENTER, 0.0, TextAnchor.CENTER);
            }
        }
        if (edge.equals(RectangleEdge.TOP)) {
            double h = maxdim;
            state.cursorUp(h);
        } else if (edge.equals(RectangleEdge.BOTTOM)) {
            double h = maxdim;
            state.cursorDown(h);
        } else if (edge == RectangleEdge.LEFT) {
            double w = maxdim;
            state.cursorLeft(w);
        } else if (edge == RectangleEdge.RIGHT) {
            double w = maxdim;
            state.cursorRight(w);
        }
        return state;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof SubCategoryAxis && super.equals(obj)) {
            SubCategoryAxis axis = (SubCategoryAxis)obj;
            if (!((Object)this.subCategories).equals(axis.subCategories)) {
                return false;
            }
            if (!this.subLabelFont.equals(axis.subLabelFont)) {
                return false;
            }
            return this.subLabelPaint.equals(axis.subLabelPaint);
        }
        return false;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.subLabelPaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.subLabelPaint = SerialUtilities.readPaint(stream);
    }
}

