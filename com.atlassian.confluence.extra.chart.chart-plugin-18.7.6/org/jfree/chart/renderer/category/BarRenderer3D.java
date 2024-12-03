/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.renderer.category;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.Effect3D;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

public class BarRenderer3D
extends BarRenderer
implements Effect3D,
Cloneable,
PublicCloneable,
Serializable {
    private static final long serialVersionUID = 7686976503536003636L;
    public static final double DEFAULT_X_OFFSET = 12.0;
    public static final double DEFAULT_Y_OFFSET = 8.0;
    public static final Paint DEFAULT_WALL_PAINT = new Color(221, 221, 221);
    private double xOffset;
    private double yOffset;
    private transient Paint wallPaint;

    public BarRenderer3D() {
        this(12.0, 8.0);
    }

    public BarRenderer3D(double xOffset, double yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.wallPaint = DEFAULT_WALL_PAINT;
        ItemLabelPosition p1 = new ItemLabelPosition(ItemLabelAnchor.INSIDE12, TextAnchor.TOP_CENTER);
        this.setBasePositiveItemLabelPosition(p1);
        ItemLabelPosition p2 = new ItemLabelPosition(ItemLabelAnchor.INSIDE12, TextAnchor.TOP_CENTER);
        this.setBaseNegativeItemLabelPosition(p2);
    }

    public double getXOffset() {
        return this.xOffset;
    }

    public double getYOffset() {
        return this.yOffset;
    }

    public Paint getWallPaint() {
        return this.wallPaint;
    }

    public void setWallPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.wallPaint = paint;
        this.fireChangeEvent();
    }

    public CategoryItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, CategoryPlot plot, int rendererIndex, PlotRenderingInfo info) {
        Rectangle2D.Double adjusted = new Rectangle2D.Double(dataArea.getX(), dataArea.getY() + this.getYOffset(), dataArea.getWidth() - this.getXOffset(), dataArea.getHeight() - this.getYOffset());
        CategoryItemRendererState state = super.initialise(g2, adjusted, plot, rendererIndex, info);
        return state;
    }

    public void drawBackground(Graphics2D g2, CategoryPlot plot, Rectangle2D dataArea) {
        float x0 = (float)dataArea.getX();
        float x1 = x0 + (float)Math.abs(this.xOffset);
        float x3 = (float)dataArea.getMaxX();
        float x2 = x3 - (float)Math.abs(this.xOffset);
        float y0 = (float)dataArea.getMaxY();
        float y1 = y0 - (float)Math.abs(this.yOffset);
        float y3 = (float)dataArea.getMinY();
        float y2 = y3 + (float)Math.abs(this.yOffset);
        GeneralPath clip = new GeneralPath();
        clip.moveTo(x0, y0);
        clip.lineTo(x0, y2);
        clip.lineTo(x1, y3);
        clip.lineTo(x3, y3);
        clip.lineTo(x3, y1);
        clip.lineTo(x2, y0);
        clip.closePath();
        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(3, plot.getBackgroundAlpha()));
        Paint backgroundPaint = plot.getBackgroundPaint();
        if (backgroundPaint != null) {
            g2.setPaint(backgroundPaint);
            g2.fill(clip);
        }
        GeneralPath leftWall = new GeneralPath();
        leftWall.moveTo(x0, y0);
        leftWall.lineTo(x0, y2);
        leftWall.lineTo(x1, y3);
        leftWall.lineTo(x1, y1);
        leftWall.closePath();
        g2.setPaint(this.getWallPaint());
        g2.fill(leftWall);
        GeneralPath bottomWall = new GeneralPath();
        bottomWall.moveTo(x0, y0);
        bottomWall.lineTo(x1, y1);
        bottomWall.lineTo(x3, y1);
        bottomWall.lineTo(x2, y0);
        bottomWall.closePath();
        g2.setPaint(this.getWallPaint());
        g2.fill(bottomWall);
        g2.setPaint(Color.lightGray);
        Line2D.Double corner = new Line2D.Double(x0, y0, x1, y1);
        g2.draw(corner);
        ((Line2D)corner).setLine(x1, y1, x1, y3);
        g2.draw(corner);
        ((Line2D)corner).setLine(x1, y1, x3, y1);
        g2.draw(corner);
        Image backgroundImage = plot.getBackgroundImage();
        if (backgroundImage != null) {
            Rectangle2D.Double adjusted = new Rectangle2D.Double(dataArea.getX() + this.getXOffset(), dataArea.getY(), dataArea.getWidth() - this.getXOffset(), dataArea.getHeight() - this.getYOffset());
            plot.drawBackgroundImage(g2, adjusted);
        }
        g2.setComposite(originalComposite);
    }

    public void drawOutline(Graphics2D g2, CategoryPlot plot, Rectangle2D dataArea) {
        float x0 = (float)dataArea.getX();
        float x1 = x0 + (float)Math.abs(this.xOffset);
        float x3 = (float)dataArea.getMaxX();
        float x2 = x3 - (float)Math.abs(this.xOffset);
        float y0 = (float)dataArea.getMaxY();
        float y1 = y0 - (float)Math.abs(this.yOffset);
        float y3 = (float)dataArea.getMinY();
        float y2 = y3 + (float)Math.abs(this.yOffset);
        GeneralPath clip = new GeneralPath();
        clip.moveTo(x0, y0);
        clip.lineTo(x0, y2);
        clip.lineTo(x1, y3);
        clip.lineTo(x3, y3);
        clip.lineTo(x3, y1);
        clip.lineTo(x2, y0);
        clip.closePath();
        Stroke outlineStroke = plot.getOutlineStroke();
        Paint outlinePaint = plot.getOutlinePaint();
        if (outlineStroke != null && outlinePaint != null) {
            g2.setStroke(outlineStroke);
            g2.setPaint(outlinePaint);
            g2.draw(clip);
        }
    }

    public void drawDomainGridline(Graphics2D g2, CategoryPlot plot, Rectangle2D dataArea, double value) {
        Line2D.Double line1 = null;
        Line2D.Double line2 = null;
        PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            double y0 = value;
            double y1 = value - this.getYOffset();
            double x0 = dataArea.getMinX();
            double x1 = x0 + this.getXOffset();
            double x2 = dataArea.getMaxX();
            line1 = new Line2D.Double(x0, y0, x1, y1);
            line2 = new Line2D.Double(x1, y1, x2, y1);
        } else if (orientation == PlotOrientation.VERTICAL) {
            double x0 = value;
            double x1 = value + this.getXOffset();
            double y0 = dataArea.getMaxY();
            double y1 = y0 - this.getYOffset();
            double y2 = dataArea.getMinY();
            line1 = new Line2D.Double(x0, y0, x1, y1);
            line2 = new Line2D.Double(x1, y1, x1, y2);
        }
        Paint paint = plot.getDomainGridlinePaint();
        Stroke stroke = plot.getDomainGridlineStroke();
        g2.setPaint(paint != null ? paint : Plot.DEFAULT_OUTLINE_PAINT);
        g2.setStroke(stroke != null ? stroke : Plot.DEFAULT_OUTLINE_STROKE);
        g2.draw(line1);
        g2.draw(line2);
    }

    public void drawRangeGridline(Graphics2D g2, CategoryPlot plot, ValueAxis axis, Rectangle2D dataArea, double value) {
        Range range = axis.getRange();
        if (!range.contains(value)) {
            return;
        }
        Rectangle2D.Double adjusted = new Rectangle2D.Double(dataArea.getX(), dataArea.getY() + this.getYOffset(), dataArea.getWidth() - this.getXOffset(), dataArea.getHeight() - this.getYOffset());
        Line2D.Double line1 = null;
        Line2D.Double line2 = null;
        PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            double x0 = axis.valueToJava2D(value, adjusted, plot.getRangeAxisEdge());
            double x1 = x0 + this.getXOffset();
            double y0 = dataArea.getMaxY();
            double y1 = y0 - this.getYOffset();
            double y2 = dataArea.getMinY();
            line1 = new Line2D.Double(x0, y0, x1, y1);
            line2 = new Line2D.Double(x1, y1, x1, y2);
        } else if (orientation == PlotOrientation.VERTICAL) {
            double y0 = axis.valueToJava2D(value, adjusted, plot.getRangeAxisEdge());
            double y1 = y0 - this.getYOffset();
            double x0 = dataArea.getMinX();
            double x1 = x0 + this.getXOffset();
            double x2 = dataArea.getMaxX();
            line1 = new Line2D.Double(x0, y0, x1, y1);
            line2 = new Line2D.Double(x1, y1, x2, y1);
        }
        Paint paint = plot.getRangeGridlinePaint();
        Stroke stroke = plot.getRangeGridlineStroke();
        g2.setPaint(paint != null ? paint : Plot.DEFAULT_OUTLINE_PAINT);
        g2.setStroke(stroke != null ? stroke : Plot.DEFAULT_OUTLINE_STROKE);
        g2.draw(line1);
        g2.draw(line2);
    }

    public void drawRangeLine(Graphics2D g2, CategoryPlot plot, ValueAxis axis, Rectangle2D dataArea, double value, Paint paint, Stroke stroke) {
        Range range = axis.getRange();
        if (!range.contains(value)) {
            return;
        }
        Rectangle2D.Double adjusted = new Rectangle2D.Double(dataArea.getX(), dataArea.getY() + this.getYOffset(), dataArea.getWidth() - this.getXOffset(), dataArea.getHeight() - this.getYOffset());
        Line2D.Double line1 = null;
        Line2D.Double line2 = null;
        PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            double x0 = axis.valueToJava2D(value, adjusted, plot.getRangeAxisEdge());
            double x1 = x0 + this.getXOffset();
            double y0 = dataArea.getMaxY();
            double y1 = y0 - this.getYOffset();
            double y2 = dataArea.getMinY();
            line1 = new Line2D.Double(x0, y0, x1, y1);
            line2 = new Line2D.Double(x1, y1, x1, y2);
        } else if (orientation == PlotOrientation.VERTICAL) {
            double y0 = axis.valueToJava2D(value, adjusted, plot.getRangeAxisEdge());
            double y1 = y0 - this.getYOffset();
            double x0 = dataArea.getMinX();
            double x1 = x0 + this.getXOffset();
            double x2 = dataArea.getMaxX();
            line1 = new Line2D.Double(x0, y0, x1, y1);
            line2 = new Line2D.Double(x1, y1, x2, y1);
        }
        g2.setPaint(paint);
        g2.setStroke(stroke);
        g2.draw(line1);
        g2.draw(line2);
    }

    public void drawRangeMarker(Graphics2D g2, CategoryPlot plot, ValueAxis axis, Marker marker, Rectangle2D dataArea) {
        Rectangle2D.Double adjusted = new Rectangle2D.Double(dataArea.getX(), dataArea.getY() + this.getYOffset(), dataArea.getWidth() - this.getXOffset(), dataArea.getHeight() - this.getYOffset());
        if (marker instanceof ValueMarker) {
            ValueMarker vm = (ValueMarker)marker;
            double value = vm.getValue();
            Range range = axis.getRange();
            if (!range.contains(value)) {
                return;
            }
            GeneralPath path = null;
            PlotOrientation orientation = plot.getOrientation();
            if (orientation == PlotOrientation.HORIZONTAL) {
                float x = (float)axis.valueToJava2D(value, adjusted, plot.getRangeAxisEdge());
                float y = (float)adjusted.getMaxY();
                path = new GeneralPath();
                path.moveTo(x, y);
                path.lineTo((float)((double)x + this.getXOffset()), y - (float)this.getYOffset());
                path.lineTo((float)((double)x + this.getXOffset()), (float)(adjusted.getMinY() - this.getYOffset()));
                path.lineTo(x, (float)adjusted.getMinY());
                path.closePath();
            } else if (orientation == PlotOrientation.VERTICAL) {
                float y = (float)axis.valueToJava2D(value, adjusted, plot.getRangeAxisEdge());
                float x = (float)dataArea.getX();
                path = new GeneralPath();
                path.moveTo(x, y);
                path.lineTo(x + (float)this.xOffset, y - (float)this.yOffset);
                path.lineTo((float)(adjusted.getMaxX() + this.xOffset), y - (float)this.yOffset);
                path.lineTo((float)adjusted.getMaxX(), y);
                path.closePath();
            }
            g2.setPaint(marker.getPaint());
            g2.fill(path);
            g2.setPaint(marker.getOutlinePaint());
            g2.draw(path);
            String label = marker.getLabel();
            RectangleAnchor anchor = marker.getLabelAnchor();
            if (label != null) {
                Font labelFont = marker.getLabelFont();
                g2.setFont(labelFont);
                g2.setPaint(marker.getLabelPaint());
                Point2D coordinates = this.calculateRangeMarkerTextAnchorPoint(g2, orientation, dataArea, path.getBounds2D(), marker.getLabelOffset(), LengthAdjustmentType.EXPAND, anchor);
                TextUtilities.drawAlignedString(label, g2, (float)coordinates.getX(), (float)coordinates.getY(), marker.getLabelTextAnchor());
            }
        } else {
            super.drawRangeMarker(g2, plot, axis, marker, adjusted);
        }
    }

    public void drawItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column, int pass) {
        EntityCollection entities;
        CategoryItemLabelGenerator generator;
        Number dataValue = dataset.getValue(row, column);
        if (dataValue == null) {
            return;
        }
        double value = dataValue.doubleValue();
        Rectangle2D.Double adjusted = new Rectangle2D.Double(dataArea.getX(), dataArea.getY() + this.getYOffset(), dataArea.getWidth() - this.getXOffset(), dataArea.getHeight() - this.getYOffset());
        PlotOrientation orientation = plot.getOrientation();
        double barW0 = this.calculateBarW0(plot, orientation, adjusted, domainAxis, state, row, column);
        double[] barL0L1 = this.calculateBarL0L1(value);
        if (barL0L1 == null) {
            return;
        }
        RectangleEdge edge = plot.getRangeAxisEdge();
        double transL0 = rangeAxis.valueToJava2D(barL0L1[0], adjusted, edge);
        double transL1 = rangeAxis.valueToJava2D(barL0L1[1], adjusted, edge);
        double barL0 = Math.min(transL0, transL1);
        double barLength = Math.abs(transL1 - transL0);
        Rectangle2D.Double bar = null;
        bar = orientation == PlotOrientation.HORIZONTAL ? new Rectangle2D.Double(barL0, barW0, barLength, state.getBarWidth()) : new Rectangle2D.Double(barW0, barL0, state.getBarWidth(), barLength);
        Paint itemPaint = this.getItemPaint(row, column);
        g2.setPaint(itemPaint);
        g2.fill(bar);
        double x0 = bar.getMinX();
        double x1 = x0 + this.getXOffset();
        double x2 = bar.getMaxX();
        double x3 = x2 + this.getXOffset();
        double y0 = bar.getMinY() - this.getYOffset();
        double y1 = bar.getMinY();
        double y2 = bar.getMaxY() - this.getYOffset();
        double y3 = bar.getMaxY();
        GeneralPath bar3dRight = null;
        GeneralPath bar3dTop = null;
        if (barLength > 0.0) {
            bar3dRight = new GeneralPath();
            bar3dRight.moveTo((float)x2, (float)y3);
            bar3dRight.lineTo((float)x2, (float)y1);
            bar3dRight.lineTo((float)x3, (float)y0);
            bar3dRight.lineTo((float)x3, (float)y2);
            bar3dRight.closePath();
            if (itemPaint instanceof Color) {
                g2.setPaint(((Color)itemPaint).darker());
            }
            g2.fill(bar3dRight);
        }
        bar3dTop = new GeneralPath();
        bar3dTop.moveTo((float)x0, (float)y1);
        bar3dTop.lineTo((float)x1, (float)y0);
        bar3dTop.lineTo((float)x3, (float)y0);
        bar3dTop.lineTo((float)x2, (float)y1);
        bar3dTop.closePath();
        g2.fill(bar3dTop);
        if (this.isDrawBarOutline() && state.getBarWidth() > 3.0) {
            g2.setStroke(this.getItemOutlineStroke(row, column));
            g2.setPaint(this.getItemOutlinePaint(row, column));
            g2.draw(bar);
            if (bar3dRight != null) {
                g2.draw(bar3dRight);
            }
            if (bar3dTop != null) {
                g2.draw(bar3dTop);
            }
        }
        if ((generator = this.getItemLabelGenerator(row, column)) != null && this.isItemLabelVisible(row, column)) {
            this.drawItemLabel(g2, dataset, row, column, plot, generator, bar, value < 0.0);
        }
        if ((entities = state.getEntityCollection()) != null) {
            GeneralPath barOutline = new GeneralPath();
            barOutline.moveTo((float)x0, (float)y3);
            barOutline.lineTo((float)x0, (float)y1);
            barOutline.lineTo((float)x1, (float)y0);
            barOutline.lineTo((float)x3, (float)y0);
            barOutline.lineTo((float)x3, (float)y2);
            barOutline.lineTo((float)x2, (float)y3);
            barOutline.closePath();
            this.addItemEntity(entities, dataset, row, column, barOutline);
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BarRenderer3D)) {
            return false;
        }
        BarRenderer3D that = (BarRenderer3D)obj;
        if (this.xOffset != that.xOffset) {
            return false;
        }
        if (this.yOffset != that.yOffset) {
            return false;
        }
        if (!PaintUtilities.equal(this.wallPaint, that.wallPaint)) {
            return false;
        }
        return super.equals(obj);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.wallPaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.wallPaint = SerialUtilities.readPaint(stream);
    }
}

