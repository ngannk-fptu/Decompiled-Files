/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.plot;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.CategoryToPieDataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.PieDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.ShapeUtilities;
import org.jfree.util.TableOrder;

public class MultiplePiePlot
extends Plot
implements Cloneable,
Serializable {
    private static final long serialVersionUID = -355377800470807389L;
    private JFreeChart pieChart;
    private CategoryDataset dataset;
    private TableOrder dataExtractOrder;
    private double limit = 0.0;
    private Comparable aggregatedItemsKey;
    private transient Paint aggregatedItemsPaint;
    private transient Map sectionPaints;
    private transient Shape legendItemShape;

    public MultiplePiePlot() {
        this(null);
    }

    public MultiplePiePlot(CategoryDataset dataset) {
        this.setDataset(dataset);
        PiePlot piePlot = new PiePlot(null);
        piePlot.setIgnoreNullValues(true);
        this.pieChart = new JFreeChart(piePlot);
        this.pieChart.removeLegend();
        this.dataExtractOrder = TableOrder.BY_COLUMN;
        this.pieChart.setBackgroundPaint(null);
        TextTitle seriesTitle = new TextTitle("Series Title", new Font("SansSerif", 1, 12));
        seriesTitle.setPosition(RectangleEdge.BOTTOM);
        this.pieChart.setTitle(seriesTitle);
        this.aggregatedItemsKey = "Other";
        this.aggregatedItemsPaint = Color.lightGray;
        this.sectionPaints = new HashMap();
        this.legendItemShape = new Ellipse2D.Double(-4.0, -4.0, 8.0, 8.0);
    }

    public CategoryDataset getDataset() {
        return this.dataset;
    }

    public void setDataset(CategoryDataset dataset) {
        if (this.dataset != null) {
            this.dataset.removeChangeListener(this);
        }
        this.dataset = dataset;
        if (dataset != null) {
            this.setDatasetGroup(dataset.getGroup());
            dataset.addChangeListener(this);
        }
        this.datasetChanged(new DatasetChangeEvent(this, dataset));
    }

    public JFreeChart getPieChart() {
        return this.pieChart;
    }

    public void setPieChart(JFreeChart pieChart) {
        if (pieChart == null) {
            throw new IllegalArgumentException("Null 'pieChart' argument.");
        }
        if (!(pieChart.getPlot() instanceof PiePlot)) {
            throw new IllegalArgumentException("The 'pieChart' argument must be a chart based on a PiePlot.");
        }
        this.pieChart = pieChart;
        this.fireChangeEvent();
    }

    public TableOrder getDataExtractOrder() {
        return this.dataExtractOrder;
    }

    public void setDataExtractOrder(TableOrder order) {
        if (order == null) {
            throw new IllegalArgumentException("Null 'order' argument");
        }
        this.dataExtractOrder = order;
        this.fireChangeEvent();
    }

    public double getLimit() {
        return this.limit;
    }

    public void setLimit(double limit) {
        this.limit = limit;
        this.fireChangeEvent();
    }

    public Comparable getAggregatedItemsKey() {
        return this.aggregatedItemsKey;
    }

    public void setAggregatedItemsKey(Comparable key) {
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        this.aggregatedItemsKey = key;
        this.fireChangeEvent();
    }

    public Paint getAggregatedItemsPaint() {
        return this.aggregatedItemsPaint;
    }

    public void setAggregatedItemsPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.aggregatedItemsPaint = paint;
        this.fireChangeEvent();
    }

    public String getPlotType() {
        return "Multiple Pie Plot";
    }

    public Shape getLegendItemShape() {
        return this.legendItemShape;
    }

    public void setLegendItemShape(Shape shape) {
        if (shape == null) {
            throw new IllegalArgumentException("Null 'shape' argument.");
        }
        this.legendItemShape = shape;
        this.fireChangeEvent();
    }

    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {
        int displayRows;
        RectangleInsets insets = this.getInsets();
        insets.trim(area);
        this.drawBackground(g2, area);
        this.drawOutline(g2, area);
        if (DatasetUtilities.isEmptyOrNull(this.dataset)) {
            this.drawNoDataMessage(g2, area);
            return;
        }
        int pieCount = 0;
        pieCount = this.dataExtractOrder == TableOrder.BY_ROW ? this.dataset.getRowCount() : this.dataset.getColumnCount();
        int displayCols = (int)Math.ceil(Math.sqrt(pieCount));
        if (displayCols > (displayRows = (int)Math.ceil((double)pieCount / (double)displayCols)) && area.getWidth() < area.getHeight()) {
            int temp = displayCols;
            displayCols = displayRows;
            displayRows = temp;
        }
        this.prefetchSectionPaints();
        int x = (int)area.getX();
        int y = (int)area.getY();
        int width = (int)area.getWidth() / displayCols;
        int height = (int)area.getHeight() / displayRows;
        int row = 0;
        int column = 0;
        int diff = displayRows * displayCols - pieCount;
        int xoffset = 0;
        Rectangle rect = new Rectangle();
        for (int pieIndex = 0; pieIndex < pieCount; ++pieIndex) {
            rect.setBounds(x + xoffset + width * column, y + height * row, width, height);
            String title = null;
            title = this.dataExtractOrder == TableOrder.BY_ROW ? this.dataset.getRowKey(pieIndex).toString() : this.dataset.getColumnKey(pieIndex).toString();
            this.pieChart.setTitle(title);
            PieDataset piedataset = null;
            CategoryToPieDataset dd = new CategoryToPieDataset(this.dataset, this.dataExtractOrder, pieIndex);
            piedataset = this.limit > 0.0 ? DatasetUtilities.createConsolidatedPieDataset(dd, this.aggregatedItemsKey, this.limit) : dd;
            PiePlot piePlot = (PiePlot)this.pieChart.getPlot();
            piePlot.setDataset(piedataset);
            piePlot.setPieIndex(pieIndex);
            for (int i = 0; i < piedataset.getItemCount(); ++i) {
                Comparable key = piedataset.getKey(i);
                Paint p = key.equals(this.aggregatedItemsKey) ? this.aggregatedItemsPaint : (Paint)this.sectionPaints.get(key);
                piePlot.setSectionPaint(key, p);
            }
            ChartRenderingInfo subinfo = null;
            if (info != null) {
                subinfo = new ChartRenderingInfo();
            }
            this.pieChart.draw(g2, rect, subinfo);
            if (info != null) {
                info.getOwner().getEntityCollection().addAll(subinfo.getEntityCollection());
                info.addSubplotInfo(subinfo.getPlotInfo());
            }
            if (++column != displayCols) continue;
            column = 0;
            if (++row != displayRows - 1 || diff == 0) continue;
            xoffset = diff * width / 2;
        }
    }

    private void prefetchSectionPaints() {
        PiePlot piePlot = (PiePlot)this.getPieChart().getPlot();
        if (this.dataExtractOrder == TableOrder.BY_ROW) {
            for (int c = 0; c < this.dataset.getColumnCount(); ++c) {
                Comparable key = this.dataset.getColumnKey(c);
                Paint p = piePlot.getSectionPaint(key);
                if (p == null && (p = (Paint)this.sectionPaints.get(key)) == null) {
                    p = this.getDrawingSupplier().getNextPaint();
                }
                this.sectionPaints.put(key, p);
            }
        } else {
            for (int r = 0; r < this.dataset.getRowCount(); ++r) {
                Comparable key = this.dataset.getRowKey(r);
                Paint p = piePlot.getSectionPaint(key);
                if (p == null && (p = (Paint)this.sectionPaints.get(key)) == null) {
                    p = this.getDrawingSupplier().getNextPaint();
                }
                this.sectionPaints.put(key, p);
            }
        }
    }

    public LegendItemCollection getLegendItems() {
        LegendItemCollection result = new LegendItemCollection();
        if (this.dataset == null) {
            return result;
        }
        List keys = null;
        this.prefetchSectionPaints();
        if (this.dataExtractOrder == TableOrder.BY_ROW) {
            keys = this.dataset.getColumnKeys();
        } else if (this.dataExtractOrder == TableOrder.BY_COLUMN) {
            keys = this.dataset.getRowKeys();
        }
        if (keys != null) {
            int section = 0;
            Iterator iterator = keys.iterator();
            while (iterator.hasNext()) {
                String label;
                Comparable key = (Comparable)iterator.next();
                String description = label = key.toString();
                Paint paint = (Paint)this.sectionPaints.get(key);
                LegendItem item = new LegendItem(label, description, null, null, this.getLegendItemShape(), paint, Plot.DEFAULT_OUTLINE_STROKE, paint);
                item.setDataset(this.getDataset());
                result.add(item);
                ++section;
            }
        }
        if (this.limit > 0.0) {
            result.add(new LegendItem(this.aggregatedItemsKey.toString(), this.aggregatedItemsKey.toString(), null, null, this.getLegendItemShape(), this.aggregatedItemsPaint, Plot.DEFAULT_OUTLINE_STROKE, this.aggregatedItemsPaint));
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MultiplePiePlot)) {
            return false;
        }
        MultiplePiePlot that = (MultiplePiePlot)obj;
        if (this.dataExtractOrder != that.dataExtractOrder) {
            return false;
        }
        if (this.limit != that.limit) {
            return false;
        }
        if (!this.aggregatedItemsKey.equals(that.aggregatedItemsKey)) {
            return false;
        }
        if (!PaintUtilities.equal(this.aggregatedItemsPaint, that.aggregatedItemsPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.pieChart, that.pieChart)) {
            return false;
        }
        if (!ShapeUtilities.equal(this.legendItemShape, that.legendItemShape)) {
            return false;
        }
        return super.equals(obj);
    }

    public Object clone() throws CloneNotSupportedException {
        MultiplePiePlot clone = (MultiplePiePlot)super.clone();
        clone.pieChart = (JFreeChart)this.pieChart.clone();
        clone.sectionPaints = new HashMap(this.sectionPaints);
        clone.legendItemShape = ShapeUtilities.clone(this.legendItemShape);
        return clone;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.aggregatedItemsPaint, stream);
        SerialUtilities.writeShape(this.legendItemShape, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.aggregatedItemsPaint = SerialUtilities.readPaint(stream);
        this.legendItemShape = SerialUtilities.readShape(stream);
        this.sectionPaints = new HashMap();
    }
}

