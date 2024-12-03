/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.renderer.category;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.Icon;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.AbstractCategoryItemRenderer;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.data.category.CategoryDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.util.PaintUtilities;

public class MinMaxCategoryRenderer
extends AbstractCategoryItemRenderer {
    private static final long serialVersionUID = 2935615937671064911L;
    private boolean plotLines = false;
    private transient Paint groupPaint = Color.black;
    private transient Stroke groupStroke = new BasicStroke(1.0f);
    private transient Icon minIcon = this.getIcon((Shape)new Arc2D.Double(-4.0, -4.0, 8.0, 8.0, 0.0, 360.0, 0), null, Color.black);
    private transient Icon maxIcon = this.getIcon((Shape)new Arc2D.Double(-4.0, -4.0, 8.0, 8.0, 0.0, 360.0, 0), null, Color.black);
    private transient Icon objectIcon = this.getIcon((Shape)new Line2D.Double(-4.0, 0.0, 4.0, 0.0), false, true);
    private int lastCategory = -1;
    private double min;
    private double max;

    public boolean isDrawLines() {
        return this.plotLines;
    }

    public void setDrawLines(boolean draw) {
        if (this.plotLines != draw) {
            this.plotLines = draw;
            this.fireChangeEvent();
        }
    }

    public Paint getGroupPaint() {
        return this.groupPaint;
    }

    public void setGroupPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.groupPaint = paint;
        this.fireChangeEvent();
    }

    public Stroke getGroupStroke() {
        return this.groupStroke;
    }

    public void setGroupStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.groupStroke = stroke;
        this.fireChangeEvent();
    }

    public Icon getObjectIcon() {
        return this.objectIcon;
    }

    public void setObjectIcon(Icon icon) {
        if (icon == null) {
            throw new IllegalArgumentException("Null 'icon' argument.");
        }
        this.objectIcon = icon;
        this.fireChangeEvent();
    }

    public Icon getMaxIcon() {
        return this.maxIcon;
    }

    public void setMaxIcon(Icon icon) {
        if (icon == null) {
            throw new IllegalArgumentException("Null 'icon' argument.");
        }
        this.maxIcon = icon;
        this.fireChangeEvent();
    }

    public Icon getMinIcon() {
        return this.minIcon;
    }

    public void setMinIcon(Icon icon) {
        if (icon == null) {
            throw new IllegalArgumentException("Null 'icon' argument.");
        }
        this.minIcon = icon;
        this.fireChangeEvent();
    }

    public void drawItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column, int pass) {
        Number value = dataset.getValue(row, column);
        if (value != null) {
            EntityCollection entities;
            Number previousValue;
            double x1 = domainAxis.getCategoryMiddle(column, this.getColumnCount(), dataArea, plot.getDomainAxisEdge());
            double y1 = rangeAxis.valueToJava2D(value.doubleValue(), dataArea, plot.getRangeAxisEdge());
            g2.setPaint(this.getItemPaint(row, column));
            g2.setStroke(this.getItemStroke(row, column));
            Rectangle2D.Double shape = null;
            shape = new Rectangle2D.Double(x1 - 4.0, y1 - 4.0, 8.0, 8.0);
            PlotOrientation orient = plot.getOrientation();
            if (orient == PlotOrientation.VERTICAL) {
                this.objectIcon.paintIcon(null, g2, (int)x1, (int)y1);
            } else {
                this.objectIcon.paintIcon(null, g2, (int)y1, (int)x1);
            }
            if (this.lastCategory == column) {
                if (this.min > value.doubleValue()) {
                    this.min = value.doubleValue();
                }
                if (this.max < value.doubleValue()) {
                    this.max = value.doubleValue();
                }
                if (dataset.getRowCount() - 1 == row) {
                    g2.setPaint(this.groupPaint);
                    g2.setStroke(this.groupStroke);
                    double minY = rangeAxis.valueToJava2D(this.min, dataArea, plot.getRangeAxisEdge());
                    double maxY = rangeAxis.valueToJava2D(this.max, dataArea, plot.getRangeAxisEdge());
                    if (orient == PlotOrientation.VERTICAL) {
                        g2.draw(new Line2D.Double(x1, minY, x1, maxY));
                        this.minIcon.paintIcon(null, g2, (int)x1, (int)minY);
                        this.maxIcon.paintIcon(null, g2, (int)x1, (int)maxY);
                    } else {
                        g2.draw(new Line2D.Double(minY, x1, maxY, x1));
                        this.minIcon.paintIcon(null, g2, (int)minY, (int)x1);
                        this.maxIcon.paintIcon(null, g2, (int)maxY, (int)x1);
                    }
                }
            } else {
                this.lastCategory = column;
                this.min = value.doubleValue();
                this.max = value.doubleValue();
            }
            if (this.plotLines && column != 0 && (previousValue = dataset.getValue(row, column - 1)) != null) {
                double previous = previousValue.doubleValue();
                double x0 = domainAxis.getCategoryMiddle(column - 1, this.getColumnCount(), dataArea, plot.getDomainAxisEdge());
                double y0 = rangeAxis.valueToJava2D(previous, dataArea, plot.getRangeAxisEdge());
                g2.setPaint(this.getItemPaint(row, column));
                g2.setStroke(this.getItemStroke(row, column));
                Line2D.Double line = orient == PlotOrientation.VERTICAL ? new Line2D.Double(x0, y0, x1, y1) : new Line2D.Double(y0, x0, y1, x1);
                g2.draw(line);
            }
            if ((entities = state.getEntityCollection()) != null && shape != null) {
                this.addItemEntity(entities, dataset, row, column, shape);
            }
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MinMaxCategoryRenderer)) {
            return false;
        }
        MinMaxCategoryRenderer that = (MinMaxCategoryRenderer)obj;
        if (this.plotLines != that.plotLines) {
            return false;
        }
        if (!PaintUtilities.equal(this.groupPaint, that.groupPaint)) {
            return false;
        }
        if (!this.groupStroke.equals(that.groupStroke)) {
            return false;
        }
        return super.equals(obj);
    }

    private Icon getIcon(Shape shape, final Paint fillPaint, final Paint outlinePaint) {
        final int width = shape.getBounds().width;
        final int height = shape.getBounds().height;
        final GeneralPath path = new GeneralPath(shape);
        return new Icon(){

            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D)g;
                path.transform(AffineTransform.getTranslateInstance(x, y));
                if (fillPaint != null) {
                    g2.setPaint(fillPaint);
                    g2.fill(path);
                }
                if (outlinePaint != null) {
                    g2.setPaint(outlinePaint);
                    g2.draw(path);
                }
                path.transform(AffineTransform.getTranslateInstance(-x, -y));
            }

            public int getIconWidth() {
                return width;
            }

            public int getIconHeight() {
                return height;
            }
        };
    }

    private Icon getIcon(Shape shape, final boolean fill, final boolean outline) {
        final int width = shape.getBounds().width;
        final int height = shape.getBounds().height;
        final GeneralPath path = new GeneralPath(shape);
        return new Icon(){

            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D)g;
                path.transform(AffineTransform.getTranslateInstance(x, y));
                if (fill) {
                    g2.fill(path);
                }
                if (outline) {
                    g2.draw(path);
                }
                path.transform(AffineTransform.getTranslateInstance(-x, -y));
            }

            public int getIconWidth() {
                return width;
            }

            public int getIconHeight() {
                return height;
            }
        };
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeStroke(this.groupStroke, stream);
        SerialUtilities.writePaint(this.groupPaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.groupStroke = SerialUtilities.readStroke(stream);
        this.groupPaint = SerialUtilities.readPaint(stream);
        this.minIcon = this.getIcon((Shape)new Arc2D.Double(-4.0, -4.0, 8.0, 8.0, 0.0, 360.0, 0), null, Color.black);
        this.maxIcon = this.getIcon((Shape)new Arc2D.Double(-4.0, -4.0, 8.0, 8.0, 0.0, 360.0, 0), null, Color.black);
        this.objectIcon = this.getIcon((Shape)new Line2D.Double(-4.0, 0.0, 4.0, 0.0), false, true);
    }
}

