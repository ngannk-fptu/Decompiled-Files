/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.renderer.xy;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.BooleanList;
import org.jfree.util.BooleanUtilities;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;

public class XYLineAndShapeRenderer
extends AbstractXYItemRenderer
implements XYItemRenderer,
Cloneable,
PublicCloneable,
Serializable {
    private static final long serialVersionUID = -7435246895986425885L;
    private Boolean linesVisible = null;
    private BooleanList seriesLinesVisible = new BooleanList();
    private boolean baseLinesVisible;
    private transient Shape legendLine;
    private Boolean shapesVisible;
    private BooleanList seriesShapesVisible;
    private boolean baseShapesVisible;
    private Boolean shapesFilled;
    private BooleanList seriesShapesFilled;
    private boolean baseShapesFilled;
    private boolean drawOutlines;
    private boolean useFillPaint;
    private boolean useOutlinePaint;
    private boolean drawSeriesLineAsPath;

    public XYLineAndShapeRenderer() {
        this(true, true);
    }

    public XYLineAndShapeRenderer(boolean lines, boolean shapes) {
        this.baseLinesVisible = lines;
        this.legendLine = new Line2D.Double(-7.0, 0.0, 7.0, 0.0);
        this.shapesVisible = null;
        this.seriesShapesVisible = new BooleanList();
        this.baseShapesVisible = shapes;
        this.shapesFilled = null;
        this.useFillPaint = false;
        this.seriesShapesFilled = new BooleanList();
        this.baseShapesFilled = true;
        this.drawOutlines = true;
        this.useOutlinePaint = false;
        this.drawSeriesLineAsPath = false;
    }

    public boolean getDrawSeriesLineAsPath() {
        return this.drawSeriesLineAsPath;
    }

    public void setDrawSeriesLineAsPath(boolean flag) {
        if (this.drawSeriesLineAsPath != flag) {
            this.drawSeriesLineAsPath = flag;
            this.fireChangeEvent();
        }
    }

    public int getPassCount() {
        return 2;
    }

    public boolean getItemLineVisible(int series, int item) {
        Boolean flag = this.linesVisible;
        if (flag == null) {
            flag = this.getSeriesLinesVisible(series);
        }
        if (flag != null) {
            return flag;
        }
        return this.baseLinesVisible;
    }

    public Boolean getLinesVisible() {
        return this.linesVisible;
    }

    public void setLinesVisible(Boolean visible) {
        this.linesVisible = visible;
        this.fireChangeEvent();
    }

    public void setLinesVisible(boolean visible) {
        this.setLinesVisible(BooleanUtilities.valueOf(visible));
    }

    public Boolean getSeriesLinesVisible(int series) {
        return this.seriesLinesVisible.getBoolean(series);
    }

    public void setSeriesLinesVisible(int series, Boolean flag) {
        this.seriesLinesVisible.setBoolean(series, flag);
        this.fireChangeEvent();
    }

    public void setSeriesLinesVisible(int series, boolean visible) {
        this.setSeriesLinesVisible(series, BooleanUtilities.valueOf(visible));
    }

    public boolean getBaseLinesVisible() {
        return this.baseLinesVisible;
    }

    public void setBaseLinesVisible(boolean flag) {
        this.baseLinesVisible = flag;
        this.fireChangeEvent();
    }

    public Shape getLegendLine() {
        return this.legendLine;
    }

    public void setLegendLine(Shape line) {
        if (line == null) {
            throw new IllegalArgumentException("Null 'line' argument.");
        }
        this.legendLine = line;
        this.fireChangeEvent();
    }

    public boolean getItemShapeVisible(int series, int item) {
        Boolean flag = this.shapesVisible;
        if (flag == null) {
            flag = this.getSeriesShapesVisible(series);
        }
        if (flag != null) {
            return flag;
        }
        return this.baseShapesVisible;
    }

    public Boolean getShapesVisible() {
        return this.shapesVisible;
    }

    public void setShapesVisible(Boolean visible) {
        this.shapesVisible = visible;
        this.fireChangeEvent();
    }

    public void setShapesVisible(boolean visible) {
        this.setShapesVisible(BooleanUtilities.valueOf(visible));
    }

    public Boolean getSeriesShapesVisible(int series) {
        return this.seriesShapesVisible.getBoolean(series);
    }

    public void setSeriesShapesVisible(int series, boolean visible) {
        this.setSeriesShapesVisible(series, BooleanUtilities.valueOf(visible));
    }

    public void setSeriesShapesVisible(int series, Boolean flag) {
        this.seriesShapesVisible.setBoolean(series, flag);
        this.fireChangeEvent();
    }

    public boolean getBaseShapesVisible() {
        return this.baseShapesVisible;
    }

    public void setBaseShapesVisible(boolean flag) {
        this.baseShapesVisible = flag;
        this.fireChangeEvent();
    }

    public boolean getItemShapeFilled(int series, int item) {
        Boolean flag = this.shapesFilled;
        if (flag == null) {
            flag = this.getSeriesShapesFilled(series);
        }
        if (flag != null) {
            return flag;
        }
        return this.baseShapesFilled;
    }

    public void setShapesFilled(boolean filled) {
        this.setShapesFilled(BooleanUtilities.valueOf(filled));
    }

    public void setShapesFilled(Boolean filled) {
        this.shapesFilled = filled;
        this.fireChangeEvent();
    }

    public Boolean getSeriesShapesFilled(int series) {
        return this.seriesShapesFilled.getBoolean(series);
    }

    public void setSeriesShapesFilled(int series, boolean flag) {
        this.setSeriesShapesFilled(series, BooleanUtilities.valueOf(flag));
    }

    public void setSeriesShapesFilled(int series, Boolean flag) {
        this.seriesShapesFilled.setBoolean(series, flag);
        this.fireChangeEvent();
    }

    public boolean getBaseShapesFilled() {
        return this.baseShapesFilled;
    }

    public void setBaseShapesFilled(boolean flag) {
        this.baseShapesFilled = flag;
        this.fireChangeEvent();
    }

    public boolean getDrawOutlines() {
        return this.drawOutlines;
    }

    public void setDrawOutlines(boolean flag) {
        this.drawOutlines = flag;
        this.fireChangeEvent();
    }

    public boolean getUseFillPaint() {
        return this.useFillPaint;
    }

    public void setUseFillPaint(boolean flag) {
        this.useFillPaint = flag;
        this.fireChangeEvent();
    }

    public boolean getUseOutlinePaint() {
        return this.useOutlinePaint;
    }

    public void setUseOutlinePaint(boolean flag) {
        this.useOutlinePaint = flag;
        this.fireChangeEvent();
    }

    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset data, PlotRenderingInfo info) {
        State state = new State(info);
        state.seriesPath = new GeneralPath();
        return state;
    }

    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        if (!this.getItemVisible(series, item)) {
            return;
        }
        if (this.isLinePass(pass)) {
            if (this.getItemLineVisible(series, item)) {
                if (this.drawSeriesLineAsPath) {
                    this.drawPrimaryLineAsPath(state, g2, plot, dataset, pass, series, item, domainAxis, rangeAxis, dataArea);
                } else {
                    this.drawPrimaryLine(state, g2, plot, dataset, pass, series, item, domainAxis, rangeAxis, dataArea);
                }
            }
        } else if (this.isItemPass(pass)) {
            EntityCollection entities = null;
            if (info != null) {
                entities = info.getOwner().getEntityCollection();
            }
            this.drawSecondaryPass(g2, plot, dataset, pass, series, item, domainAxis, dataArea, rangeAxis, crosshairState, entities);
        }
    }

    protected boolean isLinePass(int pass) {
        return pass == 0;
    }

    protected boolean isItemPass(int pass) {
        return pass == 1;
    }

    protected void drawPrimaryLine(XYItemRendererState state, Graphics2D g2, XYPlot plot, XYDataset dataset, int pass, int series, int item, ValueAxis domainAxis, ValueAxis rangeAxis, Rectangle2D dataArea) {
        if (item == 0) {
            return;
        }
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        if (Double.isNaN(y1) || Double.isNaN(x1)) {
            return;
        }
        double x0 = dataset.getXValue(series, item - 1);
        double y0 = dataset.getYValue(series, item - 1);
        if (Double.isNaN(y0) || Double.isNaN(x0)) {
            return;
        }
        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        double transX0 = domainAxis.valueToJava2D(x0, dataArea, xAxisLocation);
        double transY0 = rangeAxis.valueToJava2D(y0, dataArea, yAxisLocation);
        double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
        double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);
        if (Double.isNaN(transX0) || Double.isNaN(transY0) || Double.isNaN(transX1) || Double.isNaN(transY1)) {
            return;
        }
        PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            state.workingLine.setLine(transY0, transX0, transY1, transX1);
        } else if (orientation == PlotOrientation.VERTICAL) {
            state.workingLine.setLine(transX0, transY0, transX1, transY1);
        }
        if (state.workingLine.intersects(dataArea)) {
            this.drawFirstPassShape(g2, pass, series, item, state.workingLine);
        }
    }

    protected void drawFirstPassShape(Graphics2D g2, int pass, int series, int item, Shape shape) {
        g2.setStroke(this.getItemStroke(series, item));
        g2.setPaint(this.getItemPaint(series, item));
        g2.draw(shape);
    }

    protected void drawPrimaryLineAsPath(XYItemRendererState state, Graphics2D g2, XYPlot plot, XYDataset dataset, int pass, int series, int item, ValueAxis domainAxis, ValueAxis rangeAxis, Rectangle2D dataArea) {
        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
        double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);
        State s = (State)state;
        if (!Double.isNaN(transX1) && !Double.isNaN(transY1)) {
            float x = (float)transX1;
            float y = (float)transY1;
            PlotOrientation orientation = plot.getOrientation();
            if (orientation == PlotOrientation.HORIZONTAL) {
                x = (float)transY1;
                y = (float)transX1;
            }
            if (s.isLastPointGood()) {
                s.seriesPath.lineTo(x, y);
            } else {
                s.seriesPath.moveTo(x, y);
            }
            s.setLastPointGood(true);
        } else {
            s.setLastPointGood(false);
        }
        if (item == s.getLastItemIndex()) {
            this.drawFirstPassShape(g2, pass, series, item, s.seriesPath);
        }
    }

    protected void drawSecondaryPass(Graphics2D g2, XYPlot plot, XYDataset dataset, int pass, int series, int item, ValueAxis domainAxis, Rectangle2D dataArea, ValueAxis rangeAxis, CrosshairState crosshairState, EntityCollection entities) {
        Shape entityArea = null;
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        if (Double.isNaN(y1) || Double.isNaN(x1)) {
            return;
        }
        PlotOrientation orientation = plot.getOrientation();
        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
        double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);
        if (this.getItemShapeVisible(series, item)) {
            Shape shape = this.getItemShape(series, item);
            if (orientation == PlotOrientation.HORIZONTAL) {
                shape = ShapeUtilities.createTranslatedShape(shape, transY1, transX1);
            } else if (orientation == PlotOrientation.VERTICAL) {
                shape = ShapeUtilities.createTranslatedShape(shape, transX1, transY1);
            }
            entityArea = shape;
            if (shape.intersects(dataArea)) {
                if (this.getItemShapeFilled(series, item)) {
                    if (this.useFillPaint) {
                        g2.setPaint(this.getItemFillPaint(series, item));
                    } else {
                        g2.setPaint(this.getItemPaint(series, item));
                    }
                    g2.fill(shape);
                }
                if (this.drawOutlines) {
                    if (this.getUseOutlinePaint()) {
                        g2.setPaint(this.getItemOutlinePaint(series, item));
                    } else {
                        g2.setPaint(this.getItemPaint(series, item));
                    }
                    g2.setStroke(this.getItemOutlineStroke(series, item));
                    g2.draw(shape);
                }
            }
        }
        double xx = transX1;
        double yy = transY1;
        if (orientation == PlotOrientation.HORIZONTAL) {
            xx = transY1;
            yy = transX1;
        }
        if (this.isItemLabelVisible(series, item)) {
            this.drawItemLabel(g2, orientation, dataset, series, item, xx, yy, y1 < 0.0);
        }
        int domainAxisIndex = plot.getDomainAxisIndex(domainAxis);
        int rangeAxisIndex = plot.getRangeAxisIndex(rangeAxis);
        this.updateCrosshairValues(crosshairState, x1, y1, domainAxisIndex, rangeAxisIndex, transX1, transY1, orientation);
        if (entities != null && XYLineAndShapeRenderer.isPointInRect(dataArea, xx, yy)) {
            this.addEntity(entities, entityArea, dataset, series, item, xx, yy);
        }
    }

    public LegendItem getLegendItem(int datasetIndex, int series) {
        XYPlot plot = this.getPlot();
        if (plot == null) {
            return null;
        }
        LegendItem result = null;
        XYDataset dataset = plot.getDataset(datasetIndex);
        if (dataset != null && this.getItemVisible(series, 0)) {
            String label;
            String description = label = this.getLegendItemLabelGenerator().generateLabel(dataset, series);
            String toolTipText = null;
            if (this.getLegendItemToolTipGenerator() != null) {
                toolTipText = this.getLegendItemToolTipGenerator().generateLabel(dataset, series);
            }
            String urlText = null;
            if (this.getLegendItemURLGenerator() != null) {
                urlText = this.getLegendItemURLGenerator().generateLabel(dataset, series);
            }
            boolean shapeIsVisible = this.getItemShapeVisible(series, 0);
            Shape shape = this.lookupLegendShape(series);
            boolean shapeIsFilled = this.getItemShapeFilled(series, 0);
            Paint fillPaint = this.useFillPaint ? this.lookupSeriesFillPaint(series) : this.lookupSeriesPaint(series);
            boolean shapeOutlineVisible = this.drawOutlines;
            Paint outlinePaint = this.useOutlinePaint ? this.lookupSeriesOutlinePaint(series) : this.lookupSeriesPaint(series);
            Stroke outlineStroke = this.lookupSeriesOutlineStroke(series);
            boolean lineVisible = this.getItemLineVisible(series, 0);
            Stroke lineStroke = this.lookupSeriesStroke(series);
            Paint linePaint = this.lookupSeriesPaint(series);
            result = new LegendItem(label, description, toolTipText, urlText, shapeIsVisible, shape, shapeIsFilled, fillPaint, shapeOutlineVisible, outlinePaint, outlineStroke, lineVisible, this.legendLine, lineStroke, linePaint);
            result.setLabelFont(this.lookupLegendTextFont(series));
            Paint labelPaint = this.lookupLegendTextPaint(series);
            if (labelPaint != null) {
                result.setLabelPaint(labelPaint);
            }
            result.setSeriesKey(dataset.getSeriesKey(series));
            result.setSeriesIndex(series);
            result.setDataset(dataset);
            result.setDatasetIndex(datasetIndex);
        }
        return result;
    }

    public Object clone() throws CloneNotSupportedException {
        XYLineAndShapeRenderer clone = (XYLineAndShapeRenderer)super.clone();
        clone.seriesLinesVisible = (BooleanList)this.seriesLinesVisible.clone();
        if (this.legendLine != null) {
            clone.legendLine = ShapeUtilities.clone(this.legendLine);
        }
        clone.seriesShapesVisible = (BooleanList)this.seriesShapesVisible.clone();
        clone.seriesShapesFilled = (BooleanList)this.seriesShapesFilled.clone();
        return clone;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYLineAndShapeRenderer)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        XYLineAndShapeRenderer that = (XYLineAndShapeRenderer)obj;
        if (!ObjectUtilities.equal(this.linesVisible, that.linesVisible)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.seriesLinesVisible, that.seriesLinesVisible)) {
            return false;
        }
        if (this.baseLinesVisible != that.baseLinesVisible) {
            return false;
        }
        if (!ShapeUtilities.equal(this.legendLine, that.legendLine)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.shapesVisible, that.shapesVisible)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.seriesShapesVisible, that.seriesShapesVisible)) {
            return false;
        }
        if (this.baseShapesVisible != that.baseShapesVisible) {
            return false;
        }
        if (!ObjectUtilities.equal(this.shapesFilled, that.shapesFilled)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.seriesShapesFilled, that.seriesShapesFilled)) {
            return false;
        }
        if (this.baseShapesFilled != that.baseShapesFilled) {
            return false;
        }
        if (this.drawOutlines != that.drawOutlines) {
            return false;
        }
        if (this.useOutlinePaint != that.useOutlinePaint) {
            return false;
        }
        if (this.useFillPaint != that.useFillPaint) {
            return false;
        }
        return this.drawSeriesLineAsPath == that.drawSeriesLineAsPath;
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.legendLine = SerialUtilities.readShape(stream);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeShape(this.legendLine, stream);
    }

    public static class State
    extends XYItemRendererState {
        public GeneralPath seriesPath;
        private boolean lastPointGood;

        public State(PlotRenderingInfo info) {
            super(info);
        }

        public boolean isLastPointGood() {
            return this.lastPointGood;
        }

        public void setLastPointGood(boolean good) {
            this.lastPointGood = good;
        }

        public void startSeriesPass(XYDataset dataset, int series, int firstItem, int lastItem, int pass, int passCount) {
            this.seriesPath.reset();
            this.lastPointGood = false;
            super.startSeriesPass(dataset, series, firstItem, lastItem, pass, passCount);
        }
    }
}

