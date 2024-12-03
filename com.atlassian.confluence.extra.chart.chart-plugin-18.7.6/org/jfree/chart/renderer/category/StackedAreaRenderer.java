/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.renderer.category;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.AreaRenderer;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.data.DataUtilities;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;

public class StackedAreaRenderer
extends AreaRenderer
implements Cloneable,
PublicCloneable,
Serializable {
    private static final long serialVersionUID = -3595635038460823663L;
    private boolean renderAsPercentages;

    public StackedAreaRenderer() {
        this(false);
    }

    public StackedAreaRenderer(boolean renderAsPercentages) {
        this.renderAsPercentages = renderAsPercentages;
    }

    public boolean getRenderAsPercentages() {
        return this.renderAsPercentages;
    }

    public void setRenderAsPercentages(boolean asPercentages) {
        this.renderAsPercentages = asPercentages;
        this.fireChangeEvent();
    }

    public int getPassCount() {
        return 2;
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

    public void drawItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column, int pass) {
        float transStackLeft;
        float transStack1;
        float transY1;
        if (!this.isSeriesVisible(row)) {
            return;
        }
        GeneralPath entityArea = null;
        EntityCollection entities = state.getEntityCollection();
        double y1 = 0.0;
        Number n = dataset.getValue(row, column);
        if (n != null) {
            y1 = n.doubleValue();
            if (this.renderAsPercentages) {
                double total = DataUtilities.calculateColumnTotal(dataset, column, state.getVisibleSeriesArray());
                y1 /= total;
            }
        }
        double[] stack1 = this.getStackValues(dataset, row, column, state.getVisibleSeriesArray());
        double xx1 = domainAxis.getCategoryMiddle(column, this.getColumnCount(), dataArea, plot.getDomainAxisEdge());
        double y0 = 0.0;
        n = dataset.getValue(row, Math.max(column - 1, 0));
        if (n != null) {
            y0 = n.doubleValue();
            if (this.renderAsPercentages) {
                double total = DataUtilities.calculateColumnTotal(dataset, Math.max(column - 1, 0), state.getVisibleSeriesArray());
                y0 /= total;
            }
        }
        double[] stack0 = this.getStackValues(dataset, row, Math.max(column - 1, 0), state.getVisibleSeriesArray());
        double xx0 = domainAxis.getCategoryStart(column, this.getColumnCount(), dataArea, plot.getDomainAxisEdge());
        int itemCount = dataset.getColumnCount();
        double y2 = 0.0;
        n = dataset.getValue(row, Math.min(column + 1, itemCount - 1));
        if (n != null) {
            y2 = n.doubleValue();
            if (this.renderAsPercentages) {
                double total = DataUtilities.calculateColumnTotal(dataset, Math.min(column + 1, itemCount - 1), state.getVisibleSeriesArray());
                y2 /= total;
            }
        }
        double[] stack2 = this.getStackValues(dataset, row, Math.min(column + 1, itemCount - 1), state.getVisibleSeriesArray());
        double xx2 = domainAxis.getCategoryEnd(column, this.getColumnCount(), dataArea, plot.getDomainAxisEdge());
        double xxLeft = xx0;
        double xxRight = xx2;
        double[] stackLeft = this.averageStackValues(stack0, stack1);
        double[] stackRight = this.averageStackValues(stack1, stack2);
        double[] adjStackLeft = this.adjustedStackValues(stack0, stack1);
        double[] adjStackRight = this.adjustedStackValues(stack1, stack2);
        RectangleEdge edge1 = plot.getRangeAxisEdge();
        GeneralPath left = new GeneralPath();
        GeneralPath right = new GeneralPath();
        if (y1 >= 0.0) {
            transY1 = (float)rangeAxis.valueToJava2D(y1 + stack1[1], dataArea, edge1);
            transStack1 = (float)rangeAxis.valueToJava2D(stack1[1], dataArea, edge1);
            transStackLeft = (float)rangeAxis.valueToJava2D(adjStackLeft[1], dataArea, edge1);
            if (y0 >= 0.0) {
                double yleft = (y0 + y1) / 2.0 + stackLeft[1];
                float transYLeft = (float)rangeAxis.valueToJava2D(yleft, dataArea, edge1);
                left.moveTo((float)xx1, transY1);
                left.lineTo((float)xx1, transStack1);
                left.lineTo((float)xxLeft, transStackLeft);
                left.lineTo((float)xxLeft, transYLeft);
                left.closePath();
            } else {
                left.moveTo((float)xx1, transStack1);
                left.lineTo((float)xx1, transY1);
                left.lineTo((float)xxLeft, transStackLeft);
                left.closePath();
            }
            float transStackRight = (float)rangeAxis.valueToJava2D(adjStackRight[1], dataArea, edge1);
            if (y2 >= 0.0) {
                double yright = (y1 + y2) / 2.0 + stackRight[1];
                float transYRight = (float)rangeAxis.valueToJava2D(yright, dataArea, edge1);
                right.moveTo((float)xx1, transStack1);
                right.lineTo((float)xx1, transY1);
                right.lineTo((float)xxRight, transYRight);
                right.lineTo((float)xxRight, transStackRight);
                right.closePath();
            } else {
                right.moveTo((float)xx1, transStack1);
                right.lineTo((float)xx1, transY1);
                right.lineTo((float)xxRight, transStackRight);
                right.closePath();
            }
        } else {
            transY1 = (float)rangeAxis.valueToJava2D(y1 + stack1[0], dataArea, edge1);
            transStack1 = (float)rangeAxis.valueToJava2D(stack1[0], dataArea, edge1);
            transStackLeft = (float)rangeAxis.valueToJava2D(adjStackLeft[0], dataArea, edge1);
            if (y0 >= 0.0) {
                left.moveTo((float)xx1, transStack1);
                left.lineTo((float)xx1, transY1);
                left.lineTo((float)xxLeft, transStackLeft);
                left.clone();
            } else {
                double yleft = (y0 + y1) / 2.0 + stackLeft[0];
                float transYLeft = (float)rangeAxis.valueToJava2D(yleft, dataArea, edge1);
                left.moveTo((float)xx1, transY1);
                left.lineTo((float)xx1, transStack1);
                left.lineTo((float)xxLeft, transStackLeft);
                left.lineTo((float)xxLeft, transYLeft);
                left.closePath();
            }
            float transStackRight = (float)rangeAxis.valueToJava2D(adjStackRight[0], dataArea, edge1);
            if (y2 >= 0.0) {
                right.moveTo((float)xx1, transStack1);
                right.lineTo((float)xx1, transY1);
                right.lineTo((float)xxRight, transStackRight);
                right.closePath();
            } else {
                double yright = (y1 + y2) / 2.0 + stackRight[0];
                float transYRight = (float)rangeAxis.valueToJava2D(yright, dataArea, edge1);
                right.moveTo((float)xx1, transStack1);
                right.lineTo((float)xx1, transY1);
                right.lineTo((float)xxRight, transYRight);
                right.lineTo((float)xxRight, transStackRight);
                right.closePath();
            }
        }
        if (pass == 0) {
            Paint itemPaint = this.getItemPaint(row, column);
            g2.setPaint(itemPaint);
            g2.fill(left);
            g2.fill(right);
            if (entities != null) {
                GeneralPath gp = new GeneralPath(left);
                gp.append(right, false);
                entityArea = gp;
                this.addItemEntity(entities, dataset, row, column, entityArea);
            }
        } else if (pass == 1) {
            this.drawItemLabel(g2, plot.getOrientation(), dataset, row, column, xx1, transY1, y1 < 0.0);
        }
    }

    protected double[] getStackValues(CategoryDataset dataset, int series, int index, int[] validRows) {
        double[] result = new double[2];
        double total = 0.0;
        if (this.renderAsPercentages) {
            total = DataUtilities.calculateColumnTotal(dataset, index, validRows);
        }
        for (int i = 0; i < series; ++i) {
            if (!this.isSeriesVisible(i)) continue;
            double v = 0.0;
            Number n = dataset.getValue(i, index);
            if (n != null) {
                v = n.doubleValue();
                if (this.renderAsPercentages) {
                    v /= total;
                }
            }
            if (Double.isNaN(v)) continue;
            if (v >= 0.0) {
                result[1] = result[1] + v;
                continue;
            }
            result[0] = result[0] + v;
        }
        return result;
    }

    private double[] averageStackValues(double[] stack1, double[] stack2) {
        double[] result = new double[]{(stack1[0] + stack2[0]) / 2.0, (stack1[1] + stack2[1]) / 2.0};
        return result;
    }

    private double[] adjustedStackValues(double[] stack1, double[] stack2) {
        double[] result = new double[]{stack1[0] == 0.0 || stack2[0] == 0.0 ? 0.0 : (stack1[0] + stack2[0]) / 2.0, stack1[1] == 0.0 || stack2[1] == 0.0 ? 0.0 : (stack1[1] + stack2[1]) / 2.0};
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StackedAreaRenderer)) {
            return false;
        }
        StackedAreaRenderer that = (StackedAreaRenderer)obj;
        if (this.renderAsPercentages != that.renderAsPercentages) {
            return false;
        }
        return super.equals(obj);
    }

    protected double getPreviousHeight(CategoryDataset dataset, int series, int category) {
        double result = 0.0;
        double total = 0.0;
        if (this.renderAsPercentages) {
            total = DataUtilities.calculateColumnTotal(dataset, category);
        }
        for (int i = 0; i < series; ++i) {
            Number n = dataset.getValue(i, category);
            if (n == null) continue;
            double v = n.doubleValue();
            if (this.renderAsPercentages) {
                v /= total;
            }
            result += v;
        }
        return result;
    }
}

