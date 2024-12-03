/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.renderer.category;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.data.DataUtilities;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.util.BooleanUtilities;
import org.jfree.util.PublicCloneable;

public class StackedBarRenderer3D
extends BarRenderer3D
implements Cloneable,
PublicCloneable,
Serializable {
    private static final long serialVersionUID = -5832945916493247123L;
    private boolean renderAsPercentages;

    public StackedBarRenderer3D() {
        this(false);
    }

    public StackedBarRenderer3D(double xOffset, double yOffset) {
        super(xOffset, yOffset);
    }

    public StackedBarRenderer3D(boolean renderAsPercentages) {
        this.renderAsPercentages = renderAsPercentages;
    }

    public StackedBarRenderer3D(double xOffset, double yOffset, boolean renderAsPercentages) {
        super(xOffset, yOffset);
        this.renderAsPercentages = renderAsPercentages;
    }

    public boolean getRenderAsPercentages() {
        return this.renderAsPercentages;
    }

    public void setRenderAsPercentages(boolean asPercentages) {
        this.renderAsPercentages = asPercentages;
        this.fireChangeEvent();
    }

    public Range findRangeBounds(CategoryDataset dataset) {
        if (dataset == null) {
            return null;
        }
        if (this.renderAsPercentages) {
            return new Range(0.0, 1.0);
        }
        return DatasetUtilities.findStackedRangeBounds(dataset);
    }

    protected void calculateBarWidth(CategoryPlot plot, Rectangle2D dataArea, int rendererIndex, CategoryItemRendererState state) {
        CategoryAxis domainAxis = this.getDomainAxis(plot, rendererIndex);
        CategoryDataset data = plot.getDataset(rendererIndex);
        if (data != null) {
            PlotOrientation orientation = plot.getOrientation();
            double space = 0.0;
            if (orientation == PlotOrientation.HORIZONTAL) {
                space = dataArea.getHeight();
            } else if (orientation == PlotOrientation.VERTICAL) {
                space = dataArea.getWidth();
            }
            double maxWidth = space * this.getMaximumBarWidth();
            int columns = data.getColumnCount();
            double categoryMargin = 0.0;
            if (columns > 1) {
                categoryMargin = domainAxis.getCategoryMargin();
            }
            double used = space * (1.0 - domainAxis.getLowerMargin() - domainAxis.getUpperMargin() - categoryMargin);
            if (columns > 0) {
                state.setBarWidth(Math.min(used / (double)columns, maxWidth));
            } else {
                state.setBarWidth(Math.min(used, maxWidth));
            }
        }
    }

    protected static List createStackedValueList(CategoryDataset dataset, Comparable category, double base, boolean asPercentages) {
        int[] rows = new int[dataset.getRowCount()];
        for (int i = 0; i < rows.length; ++i) {
            rows[i] = i;
        }
        return StackedBarRenderer3D.createStackedValueList(dataset, category, rows, base, asPercentages);
    }

    protected static List createStackedValueList(CategoryDataset dataset, Comparable category, int[] includedRows, double base, boolean asPercentages) {
        ArrayList<Object[]> result = new ArrayList<Object[]>();
        double posBase = base;
        double negBase = base;
        double total = 0.0;
        if (asPercentages) {
            total = DataUtilities.calculateColumnTotal(dataset, dataset.getColumnIndex(category), includedRows);
        }
        int baseIndex = -1;
        int rowCount = includedRows.length;
        for (int i = 0; i < rowCount; ++i) {
            int r = includedRows[i];
            Number n = dataset.getValue(dataset.getRowKey(r), category);
            if (n == null) continue;
            double v = n.doubleValue();
            if (asPercentages) {
                v /= total;
            }
            if (v >= 0.0) {
                if (baseIndex < 0) {
                    result.add(new Object[]{null, new Double(base)});
                    baseIndex = 0;
                }
                result.add(new Object[]{new Integer(r), new Double(posBase += v)});
                continue;
            }
            if (!(v < 0.0)) continue;
            if (baseIndex < 0) {
                result.add(new Object[]{null, new Double(base)});
                baseIndex = 0;
            }
            result.add(0, new Object[]{new Integer(-r - 1), new Double(negBase += v)});
            ++baseIndex;
        }
        return result;
    }

    public void drawItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column, int pass) {
        if (row < dataset.getRowCount() - 1) {
            return;
        }
        Comparable category = dataset.getColumnKey(column);
        List values = StackedBarRenderer3D.createStackedValueList(dataset, dataset.getColumnKey(column), state.getVisibleSeriesArray(), this.getBase(), this.renderAsPercentages);
        Rectangle2D.Double adjusted = new Rectangle2D.Double(dataArea.getX(), dataArea.getY() + this.getYOffset(), dataArea.getWidth() - this.getXOffset(), dataArea.getHeight() - this.getYOffset());
        PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            this.drawStackHorizontal(values, category, g2, state, adjusted, plot, domainAxis, rangeAxis, dataset);
        } else {
            this.drawStackVertical(values, category, g2, state, adjusted, plot, domainAxis, rangeAxis, dataset);
        }
    }

    protected void drawStackHorizontal(List values, Comparable category, Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset) {
        int column = dataset.getColumnIndex(category);
        double barX0 = domainAxis.getCategoryMiddle(column, dataset.getColumnCount(), dataArea, plot.getDomainAxisEdge()) - state.getBarWidth() / 2.0;
        double barW = state.getBarWidth();
        ArrayList<Object[]> itemLabelList = new ArrayList<Object[]>();
        boolean inverted = rangeAxis.isInverted();
        int blockCount = values.size() - 1;
        for (int k = 0; k < blockCount; ++k) {
            Paint fillPaint;
            int index = inverted ? blockCount - k - 1 : k;
            Object[] prev = (Object[])values.get(index);
            Object[] curr = (Object[])values.get(index + 1);
            int series = 0;
            if (curr[0] == null) {
                series = -((Integer)prev[0]).intValue() - 1;
            } else {
                series = (Integer)curr[0];
                if (series < 0) {
                    series = -((Integer)prev[0]).intValue() - 1;
                }
            }
            double v0 = (Double)prev[1];
            double vv0 = rangeAxis.valueToJava2D(v0, dataArea, plot.getRangeAxisEdge());
            double v1 = (Double)curr[1];
            double vv1 = rangeAxis.valueToJava2D(v1, dataArea, plot.getRangeAxisEdge());
            Shape[] faces = this.createHorizontalBlock(barX0, barW, vv0, vv1, inverted);
            Paint fillPaintDark = fillPaint = this.getItemPaint(series, column);
            if (fillPaintDark instanceof Color) {
                fillPaintDark = ((Color)fillPaint).darker();
            }
            boolean drawOutlines = this.isDrawBarOutline();
            Paint outlinePaint = fillPaint;
            if (drawOutlines) {
                outlinePaint = this.getItemOutlinePaint(series, column);
                g2.setStroke(this.getItemOutlineStroke(series, column));
            }
            for (int f = 0; f < 6; ++f) {
                if (f == 5) {
                    g2.setPaint(fillPaint);
                } else {
                    g2.setPaint(fillPaintDark);
                }
                g2.fill(faces[f]);
                if (!drawOutlines) continue;
                g2.setPaint(outlinePaint);
                g2.draw(faces[f]);
            }
            itemLabelList.add(new Object[]{new Integer(series), faces[5].getBounds2D(), BooleanUtilities.valueOf(v0 < this.getBase())});
            EntityCollection entities = state.getEntityCollection();
            if (entities == null) continue;
            this.addItemEntity(entities, dataset, series, column, faces[5]);
        }
        for (int i = 0; i < itemLabelList.size(); ++i) {
            Object[] record = (Object[])itemLabelList.get(i);
            int series = (Integer)record[0];
            Rectangle2D bar = (Rectangle2D)record[1];
            boolean neg = (Boolean)record[2];
            CategoryItemLabelGenerator generator = this.getItemLabelGenerator(series, column);
            if (generator == null || !this.isItemLabelVisible(series, column)) continue;
            this.drawItemLabel(g2, dataset, series, column, plot, generator, bar, neg);
        }
    }

    private Shape[] createHorizontalBlock(double x0, double width, double y0, double y1, boolean inverted) {
        Shape[] result = new Shape[6];
        Point2D.Double p00 = new Point2D.Double(y0, x0);
        Point2D.Double p01 = new Point2D.Double(y0, x0 + width);
        Point2D.Double p02 = new Point2D.Double(((Point2D)p01).getX() + this.getXOffset(), ((Point2D)p01).getY() - this.getYOffset());
        Point2D.Double p03 = new Point2D.Double(((Point2D)p00).getX() + this.getXOffset(), ((Point2D)p00).getY() - this.getYOffset());
        Point2D.Double p0 = new Point2D.Double(y1, x0);
        Point2D.Double p1 = new Point2D.Double(y1, x0 + width);
        Point2D.Double p2 = new Point2D.Double(((Point2D)p1).getX() + this.getXOffset(), ((Point2D)p1).getY() - this.getYOffset());
        Point2D.Double p3 = new Point2D.Double(((Point2D)p0).getX() + this.getXOffset(), ((Point2D)p0).getY() - this.getYOffset());
        GeneralPath bottom = new GeneralPath();
        bottom.moveTo((float)((Point2D)p1).getX(), (float)((Point2D)p1).getY());
        bottom.lineTo((float)((Point2D)p01).getX(), (float)((Point2D)p01).getY());
        bottom.lineTo((float)((Point2D)p02).getX(), (float)((Point2D)p02).getY());
        bottom.lineTo((float)((Point2D)p2).getX(), (float)((Point2D)p2).getY());
        bottom.closePath();
        GeneralPath top = new GeneralPath();
        top.moveTo((float)((Point2D)p0).getX(), (float)((Point2D)p0).getY());
        top.lineTo((float)((Point2D)p00).getX(), (float)((Point2D)p00).getY());
        top.lineTo((float)((Point2D)p03).getX(), (float)((Point2D)p03).getY());
        top.lineTo((float)((Point2D)p3).getX(), (float)((Point2D)p3).getY());
        top.closePath();
        GeneralPath back = new GeneralPath();
        back.moveTo((float)((Point2D)p2).getX(), (float)((Point2D)p2).getY());
        back.lineTo((float)((Point2D)p02).getX(), (float)((Point2D)p02).getY());
        back.lineTo((float)((Point2D)p03).getX(), (float)((Point2D)p03).getY());
        back.lineTo((float)((Point2D)p3).getX(), (float)((Point2D)p3).getY());
        back.closePath();
        GeneralPath front = new GeneralPath();
        front.moveTo((float)((Point2D)p0).getX(), (float)((Point2D)p0).getY());
        front.lineTo((float)((Point2D)p1).getX(), (float)((Point2D)p1).getY());
        front.lineTo((float)((Point2D)p01).getX(), (float)((Point2D)p01).getY());
        front.lineTo((float)((Point2D)p00).getX(), (float)((Point2D)p00).getY());
        front.closePath();
        GeneralPath left = new GeneralPath();
        left.moveTo((float)((Point2D)p0).getX(), (float)((Point2D)p0).getY());
        left.lineTo((float)((Point2D)p1).getX(), (float)((Point2D)p1).getY());
        left.lineTo((float)((Point2D)p2).getX(), (float)((Point2D)p2).getY());
        left.lineTo((float)((Point2D)p3).getX(), (float)((Point2D)p3).getY());
        left.closePath();
        GeneralPath right = new GeneralPath();
        right.moveTo((float)((Point2D)p00).getX(), (float)((Point2D)p00).getY());
        right.lineTo((float)((Point2D)p01).getX(), (float)((Point2D)p01).getY());
        right.lineTo((float)((Point2D)p02).getX(), (float)((Point2D)p02).getY());
        right.lineTo((float)((Point2D)p03).getX(), (float)((Point2D)p03).getY());
        right.closePath();
        result[0] = bottom;
        result[1] = back;
        if (inverted) {
            result[2] = right;
            result[3] = left;
        } else {
            result[2] = left;
            result[3] = right;
        }
        result[4] = top;
        result[5] = front;
        return result;
    }

    protected void drawStackVertical(List values, Comparable category, Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset) {
        int column = dataset.getColumnIndex(category);
        double barX0 = domainAxis.getCategoryMiddle(column, dataset.getColumnCount(), dataArea, plot.getDomainAxisEdge()) - state.getBarWidth() / 2.0;
        double barW = state.getBarWidth();
        ArrayList<Object[]> itemLabelList = new ArrayList<Object[]>();
        boolean inverted = rangeAxis.isInverted();
        int blockCount = values.size() - 1;
        for (int k = 0; k < blockCount; ++k) {
            Paint fillPaint;
            int index = inverted ? blockCount - k - 1 : k;
            Object[] prev = (Object[])values.get(index);
            Object[] curr = (Object[])values.get(index + 1);
            int series = 0;
            if (curr[0] == null) {
                series = -((Integer)prev[0]).intValue() - 1;
            } else {
                series = (Integer)curr[0];
                if (series < 0) {
                    series = -((Integer)prev[0]).intValue() - 1;
                }
            }
            double v0 = (Double)prev[1];
            double vv0 = rangeAxis.valueToJava2D(v0, dataArea, plot.getRangeAxisEdge());
            double v1 = (Double)curr[1];
            double vv1 = rangeAxis.valueToJava2D(v1, dataArea, plot.getRangeAxisEdge());
            Shape[] faces = this.createVerticalBlock(barX0, barW, vv0, vv1, inverted);
            Paint fillPaintDark = fillPaint = this.getItemPaint(series, column);
            if (fillPaintDark instanceof Color) {
                fillPaintDark = ((Color)fillPaint).darker();
            }
            boolean drawOutlines = this.isDrawBarOutline();
            Paint outlinePaint = fillPaint;
            if (drawOutlines) {
                outlinePaint = this.getItemOutlinePaint(series, column);
                g2.setStroke(this.getItemOutlineStroke(series, column));
            }
            for (int f = 0; f < 6; ++f) {
                if (f == 5) {
                    g2.setPaint(fillPaint);
                } else {
                    g2.setPaint(fillPaintDark);
                }
                g2.fill(faces[f]);
                if (!drawOutlines) continue;
                g2.setPaint(outlinePaint);
                g2.draw(faces[f]);
            }
            itemLabelList.add(new Object[]{new Integer(series), faces[5].getBounds2D(), BooleanUtilities.valueOf(v0 < this.getBase())});
            EntityCollection entities = state.getEntityCollection();
            if (entities == null) continue;
            this.addItemEntity(entities, dataset, series, column, faces[5]);
        }
        for (int i = 0; i < itemLabelList.size(); ++i) {
            Object[] record = (Object[])itemLabelList.get(i);
            int series = (Integer)record[0];
            Rectangle2D bar = (Rectangle2D)record[1];
            boolean neg = (Boolean)record[2];
            CategoryItemLabelGenerator generator = this.getItemLabelGenerator(series, column);
            if (generator == null || !this.isItemLabelVisible(series, column)) continue;
            this.drawItemLabel(g2, dataset, series, column, plot, generator, bar, neg);
        }
    }

    private Shape[] createVerticalBlock(double x0, double width, double y0, double y1, boolean inverted) {
        Shape[] result = new Shape[6];
        Point2D.Double p00 = new Point2D.Double(x0, y0);
        Point2D.Double p01 = new Point2D.Double(x0 + width, y0);
        Point2D.Double p02 = new Point2D.Double(((Point2D)p01).getX() + this.getXOffset(), ((Point2D)p01).getY() - this.getYOffset());
        Point2D.Double p03 = new Point2D.Double(((Point2D)p00).getX() + this.getXOffset(), ((Point2D)p00).getY() - this.getYOffset());
        Point2D.Double p0 = new Point2D.Double(x0, y1);
        Point2D.Double p1 = new Point2D.Double(x0 + width, y1);
        Point2D.Double p2 = new Point2D.Double(((Point2D)p1).getX() + this.getXOffset(), ((Point2D)p1).getY() - this.getYOffset());
        Point2D.Double p3 = new Point2D.Double(((Point2D)p0).getX() + this.getXOffset(), ((Point2D)p0).getY() - this.getYOffset());
        GeneralPath right = new GeneralPath();
        right.moveTo((float)((Point2D)p1).getX(), (float)((Point2D)p1).getY());
        right.lineTo((float)((Point2D)p01).getX(), (float)((Point2D)p01).getY());
        right.lineTo((float)((Point2D)p02).getX(), (float)((Point2D)p02).getY());
        right.lineTo((float)((Point2D)p2).getX(), (float)((Point2D)p2).getY());
        right.closePath();
        GeneralPath left = new GeneralPath();
        left.moveTo((float)((Point2D)p0).getX(), (float)((Point2D)p0).getY());
        left.lineTo((float)((Point2D)p00).getX(), (float)((Point2D)p00).getY());
        left.lineTo((float)((Point2D)p03).getX(), (float)((Point2D)p03).getY());
        left.lineTo((float)((Point2D)p3).getX(), (float)((Point2D)p3).getY());
        left.closePath();
        GeneralPath back = new GeneralPath();
        back.moveTo((float)((Point2D)p2).getX(), (float)((Point2D)p2).getY());
        back.lineTo((float)((Point2D)p02).getX(), (float)((Point2D)p02).getY());
        back.lineTo((float)((Point2D)p03).getX(), (float)((Point2D)p03).getY());
        back.lineTo((float)((Point2D)p3).getX(), (float)((Point2D)p3).getY());
        back.closePath();
        GeneralPath front = new GeneralPath();
        front.moveTo((float)((Point2D)p0).getX(), (float)((Point2D)p0).getY());
        front.lineTo((float)((Point2D)p1).getX(), (float)((Point2D)p1).getY());
        front.lineTo((float)((Point2D)p01).getX(), (float)((Point2D)p01).getY());
        front.lineTo((float)((Point2D)p00).getX(), (float)((Point2D)p00).getY());
        front.closePath();
        GeneralPath top = new GeneralPath();
        top.moveTo((float)((Point2D)p0).getX(), (float)((Point2D)p0).getY());
        top.lineTo((float)((Point2D)p1).getX(), (float)((Point2D)p1).getY());
        top.lineTo((float)((Point2D)p2).getX(), (float)((Point2D)p2).getY());
        top.lineTo((float)((Point2D)p3).getX(), (float)((Point2D)p3).getY());
        top.closePath();
        GeneralPath bottom = new GeneralPath();
        bottom.moveTo((float)((Point2D)p00).getX(), (float)((Point2D)p00).getY());
        bottom.lineTo((float)((Point2D)p01).getX(), (float)((Point2D)p01).getY());
        bottom.lineTo((float)((Point2D)p02).getX(), (float)((Point2D)p02).getY());
        bottom.lineTo((float)((Point2D)p03).getX(), (float)((Point2D)p03).getY());
        bottom.closePath();
        result[0] = bottom;
        result[1] = back;
        result[2] = left;
        result[3] = right;
        result[4] = top;
        result[5] = front;
        if (inverted) {
            result[0] = top;
            result[4] = bottom;
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StackedBarRenderer3D)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        StackedBarRenderer3D that = (StackedBarRenderer3D)obj;
        return this.renderAsPercentages == that.getRenderAsPercentages();
    }
}

