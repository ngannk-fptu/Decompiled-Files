/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.renderer.xy;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.labels.XYSeriesLabelGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.GradientXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarPainter;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.GradientPaintTransformer;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.StandardGradientPaintTransformer;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;

public class XYBarRenderer
extends AbstractXYItemRenderer
implements XYItemRenderer,
Cloneable,
PublicCloneable,
Serializable {
    private static final long serialVersionUID = 770559577251370036L;
    private static XYBarPainter defaultBarPainter = new GradientXYBarPainter();
    private static boolean defaultShadowsVisible = true;
    private double base;
    private boolean useYInterval;
    private double margin;
    private boolean drawBarOutline;
    private GradientPaintTransformer gradientPaintTransformer;
    private transient Shape legendBar;
    private ItemLabelPosition positiveItemLabelPositionFallback;
    private ItemLabelPosition negativeItemLabelPositionFallback;
    private XYBarPainter barPainter;
    private boolean shadowsVisible;
    private double shadowXOffset;
    private double shadowYOffset;
    private double barAlignmentFactor;

    public static XYBarPainter getDefaultBarPainter() {
        return defaultBarPainter;
    }

    public static void setDefaultBarPainter(XYBarPainter painter) {
        if (painter == null) {
            throw new IllegalArgumentException("Null 'painter' argument.");
        }
        defaultBarPainter = painter;
    }

    public static boolean getDefaultShadowsVisible() {
        return defaultShadowsVisible;
    }

    public static void setDefaultShadowsVisible(boolean visible) {
        defaultShadowsVisible = visible;
    }

    public XYBarRenderer() {
        this(0.0);
    }

    public XYBarRenderer(double margin) {
        this.margin = margin;
        this.base = 0.0;
        this.useYInterval = false;
        this.gradientPaintTransformer = new StandardGradientPaintTransformer();
        this.drawBarOutline = false;
        this.legendBar = new Rectangle2D.Double(-3.0, -5.0, 6.0, 10.0);
        this.barPainter = XYBarRenderer.getDefaultBarPainter();
        this.shadowsVisible = XYBarRenderer.getDefaultShadowsVisible();
        this.shadowXOffset = 4.0;
        this.shadowYOffset = 4.0;
        this.barAlignmentFactor = -1.0;
    }

    public double getBase() {
        return this.base;
    }

    public void setBase(double base) {
        this.base = base;
        this.fireChangeEvent();
    }

    public boolean getUseYInterval() {
        return this.useYInterval;
    }

    public void setUseYInterval(boolean use) {
        if (this.useYInterval != use) {
            this.useYInterval = use;
            this.fireChangeEvent();
        }
    }

    public double getMargin() {
        return this.margin;
    }

    public void setMargin(double margin) {
        this.margin = margin;
        this.fireChangeEvent();
    }

    public boolean isDrawBarOutline() {
        return this.drawBarOutline;
    }

    public void setDrawBarOutline(boolean draw) {
        this.drawBarOutline = draw;
        this.fireChangeEvent();
    }

    public GradientPaintTransformer getGradientPaintTransformer() {
        return this.gradientPaintTransformer;
    }

    public void setGradientPaintTransformer(GradientPaintTransformer transformer) {
        this.gradientPaintTransformer = transformer;
        this.fireChangeEvent();
    }

    public Shape getLegendBar() {
        return this.legendBar;
    }

    public void setLegendBar(Shape bar) {
        if (bar == null) {
            throw new IllegalArgumentException("Null 'bar' argument.");
        }
        this.legendBar = bar;
        this.fireChangeEvent();
    }

    public ItemLabelPosition getPositiveItemLabelPositionFallback() {
        return this.positiveItemLabelPositionFallback;
    }

    public void setPositiveItemLabelPositionFallback(ItemLabelPosition position) {
        this.positiveItemLabelPositionFallback = position;
        this.fireChangeEvent();
    }

    public ItemLabelPosition getNegativeItemLabelPositionFallback() {
        return this.negativeItemLabelPositionFallback;
    }

    public void setNegativeItemLabelPositionFallback(ItemLabelPosition position) {
        this.negativeItemLabelPositionFallback = position;
        this.fireChangeEvent();
    }

    public XYBarPainter getBarPainter() {
        return this.barPainter;
    }

    public void setBarPainter(XYBarPainter painter) {
        if (painter == null) {
            throw new IllegalArgumentException("Null 'painter' argument.");
        }
        this.barPainter = painter;
        this.fireChangeEvent();
    }

    public boolean getShadowsVisible() {
        return this.shadowsVisible;
    }

    public void setShadowVisible(boolean visible) {
        this.shadowsVisible = visible;
        this.fireChangeEvent();
    }

    public double getShadowXOffset() {
        return this.shadowXOffset;
    }

    public void setShadowXOffset(double offset) {
        this.shadowXOffset = offset;
        this.fireChangeEvent();
    }

    public double getShadowYOffset() {
        return this.shadowYOffset;
    }

    public void setShadowYOffset(double offset) {
        this.shadowYOffset = offset;
        this.fireChangeEvent();
    }

    public double getBarAlignmentFactor() {
        return this.barAlignmentFactor;
    }

    public void setBarAlignmentFactor(double factor) {
        this.barAlignmentFactor = factor;
        this.fireChangeEvent();
    }

    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset dataset, PlotRenderingInfo info) {
        XYBarRendererState state = new XYBarRendererState(info);
        ValueAxis rangeAxis = plot.getRangeAxisForDataset(plot.indexOf(dataset));
        state.setG2Base(rangeAxis.valueToJava2D(this.base, dataArea, plot.getRangeAxisEdge()));
        return state;
    }

    public LegendItem getLegendItem(int datasetIndex, int series) {
        XYDataset dataset;
        LegendItem result = null;
        XYPlot xyplot = this.getPlot();
        if (xyplot != null && (dataset = xyplot.getDataset(datasetIndex)) != null) {
            String label;
            XYSeriesLabelGenerator lg = this.getLegendItemLabelGenerator();
            String description = label = lg.generateLabel(dataset, series);
            String toolTipText = null;
            if (this.getLegendItemToolTipGenerator() != null) {
                toolTipText = this.getLegendItemToolTipGenerator().generateLabel(dataset, series);
            }
            String urlText = null;
            if (this.getLegendItemURLGenerator() != null) {
                urlText = this.getLegendItemURLGenerator().generateLabel(dataset, series);
            }
            Shape shape = this.legendBar;
            Paint paint = this.lookupSeriesPaint(series);
            Paint outlinePaint = this.lookupSeriesOutlinePaint(series);
            Stroke outlineStroke = this.lookupSeriesOutlineStroke(series);
            result = this.drawBarOutline ? new LegendItem(label, description, toolTipText, urlText, shape, paint, outlineStroke, outlinePaint) : new LegendItem(label, description, toolTipText, urlText, shape, paint);
            result.setLabelFont(this.lookupLegendTextFont(series));
            Paint labelPaint = this.lookupLegendTextPaint(series);
            if (labelPaint != null) {
                result.setLabelPaint(labelPaint);
            }
            result.setDataset(dataset);
            result.setDatasetIndex(datasetIndex);
            result.setSeriesKey(dataset.getSeriesKey(series));
            result.setSeriesIndex(series);
            if (this.getGradientPaintTransformer() != null) {
                result.setFillPaintTransformer(this.getGradientPaintTransformer());
            }
        }
        return result;
    }

    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        double value1;
        double value0;
        if (!this.getItemVisible(series, item)) {
            return;
        }
        IntervalXYDataset intervalDataset = (IntervalXYDataset)dataset;
        if (this.useYInterval) {
            value0 = intervalDataset.getStartYValue(series, item);
            value1 = intervalDataset.getEndYValue(series, item);
        } else {
            value0 = this.base;
            value1 = intervalDataset.getYValue(series, item);
        }
        if (Double.isNaN(value0) || Double.isNaN(value1)) {
            return;
        }
        if (value0 <= value1 ? !rangeAxis.getRange().intersects(value0, value1) : !rangeAxis.getRange().intersects(value1, value0)) {
            return;
        }
        double translatedValue0 = rangeAxis.valueToJava2D(value0, dataArea, plot.getRangeAxisEdge());
        double translatedValue1 = rangeAxis.valueToJava2D(value1, dataArea, plot.getRangeAxisEdge());
        double bottom = Math.min(translatedValue0, translatedValue1);
        double top = Math.max(translatedValue0, translatedValue1);
        double startX = intervalDataset.getStartXValue(series, item);
        if (Double.isNaN(startX)) {
            return;
        }
        double endX = intervalDataset.getEndXValue(series, item);
        if (Double.isNaN(endX)) {
            return;
        }
        if (startX <= endX ? !domainAxis.getRange().intersects(startX, endX) : !domainAxis.getRange().intersects(endX, startX)) {
            return;
        }
        if (this.barAlignmentFactor >= 0.0 && this.barAlignmentFactor <= 1.0) {
            double x = intervalDataset.getXValue(series, item);
            double interval = endX - startX;
            startX = x - interval * this.barAlignmentFactor;
            endX = startX + interval;
        }
        RectangleEdge location = plot.getDomainAxisEdge();
        double translatedStartX = domainAxis.valueToJava2D(startX, dataArea, location);
        double translatedEndX = domainAxis.valueToJava2D(endX, dataArea, location);
        double translatedWidth = Math.max(1.0, Math.abs(translatedEndX - translatedStartX));
        double left = Math.min(translatedStartX, translatedEndX);
        if (this.getMargin() > 0.0) {
            double cut = translatedWidth * this.getMargin();
            translatedWidth -= cut;
            left += cut / 2.0;
        }
        Rectangle2D.Double bar = null;
        PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            bottom = Math.max(bottom, dataArea.getMinX());
            top = Math.min(top, dataArea.getMaxX());
            bar = new Rectangle2D.Double(bottom, left, top - bottom, translatedWidth);
        } else if (orientation == PlotOrientation.VERTICAL) {
            bottom = Math.max(bottom, dataArea.getMinY());
            top = Math.min(top, dataArea.getMaxY());
            bar = new Rectangle2D.Double(left, bottom, translatedWidth, top - bottom);
        }
        boolean positive = value1 > 0.0;
        boolean inverted = rangeAxis.isInverted();
        RectangleEdge barBase = orientation == PlotOrientation.HORIZONTAL ? (positive && inverted || !positive && !inverted ? RectangleEdge.RIGHT : RectangleEdge.LEFT) : (positive && !inverted || !positive && inverted ? RectangleEdge.BOTTOM : RectangleEdge.TOP);
        if (this.getShadowsVisible()) {
            this.barPainter.paintBarShadow(g2, this, series, item, bar, barBase, !this.useYInterval);
        }
        this.barPainter.paintBar(g2, this, series, item, bar, barBase);
        if (this.isItemLabelVisible(series, item)) {
            XYItemLabelGenerator generator = this.getItemLabelGenerator(series, item);
            this.drawItemLabel(g2, dataset, series, item, plot, generator, bar, value1 < 0.0);
        }
        double x1 = (startX + endX) / 2.0;
        double y1 = dataset.getYValue(series, item);
        double transX1 = domainAxis.valueToJava2D(x1, dataArea, location);
        double transY1 = rangeAxis.valueToJava2D(y1, dataArea, plot.getRangeAxisEdge());
        int domainAxisIndex = plot.getDomainAxisIndex(domainAxis);
        int rangeAxisIndex = plot.getRangeAxisIndex(rangeAxis);
        this.updateCrosshairValues(crosshairState, x1, y1, domainAxisIndex, rangeAxisIndex, transX1, transY1, plot.getOrientation());
        EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
            this.addEntity(entities, bar, dataset, series, item, 0.0, 0.0);
        }
    }

    protected void drawItemLabel(Graphics2D g2, XYDataset dataset, int series, int item, XYPlot plot, XYItemLabelGenerator generator, Rectangle2D bar, boolean negative) {
        Shape bounds;
        if (generator == null) {
            return;
        }
        String label = generator.generateLabel(dataset, series, item);
        if (label == null) {
            return;
        }
        Font labelFont = this.getItemLabelFont(series, item);
        g2.setFont(labelFont);
        Paint paint = this.getItemLabelPaint(series, item);
        g2.setPaint(paint);
        ItemLabelPosition position = null;
        position = !negative ? this.getPositiveItemLabelPosition(series, item) : this.getNegativeItemLabelPosition(series, item);
        Point2D anchorPoint = this.calculateLabelAnchorPoint(position.getItemLabelAnchor(), bar, plot.getOrientation());
        if (this.isInternalAnchor(position.getItemLabelAnchor()) && (bounds = TextUtilities.calculateRotatedStringBounds(label, g2, (float)anchorPoint.getX(), (float)anchorPoint.getY(), position.getTextAnchor(), position.getAngle(), position.getRotationAnchor())) != null && !bar.contains(bounds.getBounds2D()) && (position = !negative ? this.getPositiveItemLabelPositionFallback() : this.getNegativeItemLabelPositionFallback()) != null) {
            anchorPoint = this.calculateLabelAnchorPoint(position.getItemLabelAnchor(), bar, plot.getOrientation());
        }
        if (position != null) {
            TextUtilities.drawRotatedString(label, g2, (float)anchorPoint.getX(), (float)anchorPoint.getY(), position.getTextAnchor(), position.getAngle(), position.getRotationAnchor());
        }
    }

    private Point2D calculateLabelAnchorPoint(ItemLabelAnchor anchor, Rectangle2D bar, PlotOrientation orientation) {
        Point2D.Double result = null;
        double offset = this.getItemLabelAnchorOffset();
        double x0 = bar.getX() - offset;
        double x1 = bar.getX();
        double x2 = bar.getX() + offset;
        double x3 = bar.getCenterX();
        double x4 = bar.getMaxX() - offset;
        double x5 = bar.getMaxX();
        double x6 = bar.getMaxX() + offset;
        double y0 = bar.getMaxY() + offset;
        double y1 = bar.getMaxY();
        double y2 = bar.getMaxY() - offset;
        double y3 = bar.getCenterY();
        double y4 = bar.getMinY() + offset;
        double y5 = bar.getMinY();
        double y6 = bar.getMinY() - offset;
        if (anchor == ItemLabelAnchor.CENTER) {
            result = new Point2D.Double(x3, y3);
        } else if (anchor == ItemLabelAnchor.INSIDE1) {
            result = new Point2D.Double(x4, y4);
        } else if (anchor == ItemLabelAnchor.INSIDE2) {
            result = new Point2D.Double(x4, y4);
        } else if (anchor == ItemLabelAnchor.INSIDE3) {
            result = new Point2D.Double(x4, y3);
        } else if (anchor == ItemLabelAnchor.INSIDE4) {
            result = new Point2D.Double(x4, y2);
        } else if (anchor == ItemLabelAnchor.INSIDE5) {
            result = new Point2D.Double(x4, y2);
        } else if (anchor == ItemLabelAnchor.INSIDE6) {
            result = new Point2D.Double(x3, y2);
        } else if (anchor == ItemLabelAnchor.INSIDE7) {
            result = new Point2D.Double(x2, y2);
        } else if (anchor == ItemLabelAnchor.INSIDE8) {
            result = new Point2D.Double(x2, y2);
        } else if (anchor == ItemLabelAnchor.INSIDE9) {
            result = new Point2D.Double(x2, y3);
        } else if (anchor == ItemLabelAnchor.INSIDE10) {
            result = new Point2D.Double(x2, y4);
        } else if (anchor == ItemLabelAnchor.INSIDE11) {
            result = new Point2D.Double(x2, y4);
        } else if (anchor == ItemLabelAnchor.INSIDE12) {
            result = new Point2D.Double(x3, y4);
        } else if (anchor == ItemLabelAnchor.OUTSIDE1) {
            result = new Point2D.Double(x5, y6);
        } else if (anchor == ItemLabelAnchor.OUTSIDE2) {
            result = new Point2D.Double(x6, y5);
        } else if (anchor == ItemLabelAnchor.OUTSIDE3) {
            result = new Point2D.Double(x6, y3);
        } else if (anchor == ItemLabelAnchor.OUTSIDE4) {
            result = new Point2D.Double(x6, y1);
        } else if (anchor == ItemLabelAnchor.OUTSIDE5) {
            result = new Point2D.Double(x5, y0);
        } else if (anchor == ItemLabelAnchor.OUTSIDE6) {
            result = new Point2D.Double(x3, y0);
        } else if (anchor == ItemLabelAnchor.OUTSIDE7) {
            result = new Point2D.Double(x1, y0);
        } else if (anchor == ItemLabelAnchor.OUTSIDE8) {
            result = new Point2D.Double(x0, y1);
        } else if (anchor == ItemLabelAnchor.OUTSIDE9) {
            result = new Point2D.Double(x0, y3);
        } else if (anchor == ItemLabelAnchor.OUTSIDE10) {
            result = new Point2D.Double(x0, y5);
        } else if (anchor == ItemLabelAnchor.OUTSIDE11) {
            result = new Point2D.Double(x1, y6);
        } else if (anchor == ItemLabelAnchor.OUTSIDE12) {
            result = new Point2D.Double(x3, y6);
        }
        return result;
    }

    private boolean isInternalAnchor(ItemLabelAnchor anchor) {
        return anchor == ItemLabelAnchor.CENTER || anchor == ItemLabelAnchor.INSIDE1 || anchor == ItemLabelAnchor.INSIDE2 || anchor == ItemLabelAnchor.INSIDE3 || anchor == ItemLabelAnchor.INSIDE4 || anchor == ItemLabelAnchor.INSIDE5 || anchor == ItemLabelAnchor.INSIDE6 || anchor == ItemLabelAnchor.INSIDE7 || anchor == ItemLabelAnchor.INSIDE8 || anchor == ItemLabelAnchor.INSIDE9 || anchor == ItemLabelAnchor.INSIDE10 || anchor == ItemLabelAnchor.INSIDE11 || anchor == ItemLabelAnchor.INSIDE12;
    }

    public Range findDomainBounds(XYDataset dataset) {
        if (dataset != null) {
            return DatasetUtilities.findDomainBounds(dataset, true);
        }
        return null;
    }

    public Range findRangeBounds(XYDataset dataset) {
        if (dataset != null) {
            return DatasetUtilities.findRangeBounds(dataset, this.useYInterval);
        }
        return null;
    }

    public Object clone() throws CloneNotSupportedException {
        XYBarRenderer result = (XYBarRenderer)super.clone();
        if (this.gradientPaintTransformer != null) {
            result.gradientPaintTransformer = (GradientPaintTransformer)ObjectUtilities.clone(this.gradientPaintTransformer);
        }
        result.legendBar = ShapeUtilities.clone(this.legendBar);
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYBarRenderer)) {
            return false;
        }
        XYBarRenderer that = (XYBarRenderer)obj;
        if (this.base != that.base) {
            return false;
        }
        if (this.drawBarOutline != that.drawBarOutline) {
            return false;
        }
        if (this.margin != that.margin) {
            return false;
        }
        if (this.useYInterval != that.useYInterval) {
            return false;
        }
        if (!ObjectUtilities.equal(this.gradientPaintTransformer, that.gradientPaintTransformer)) {
            return false;
        }
        if (!ShapeUtilities.equal(this.legendBar, that.legendBar)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.positiveItemLabelPositionFallback, that.positiveItemLabelPositionFallback)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.negativeItemLabelPositionFallback, that.negativeItemLabelPositionFallback)) {
            return false;
        }
        if (!this.barPainter.equals(that.barPainter)) {
            return false;
        }
        if (this.shadowsVisible != that.shadowsVisible) {
            return false;
        }
        if (this.shadowXOffset != that.shadowXOffset) {
            return false;
        }
        if (this.shadowYOffset != that.shadowYOffset) {
            return false;
        }
        if (this.barAlignmentFactor != that.barAlignmentFactor) {
            return false;
        }
        return super.equals(obj);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.legendBar = SerialUtilities.readShape(stream);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeShape(this.legendBar, stream);
    }

    protected class XYBarRendererState
    extends XYItemRendererState {
        private double g2Base;

        public XYBarRendererState(PlotRenderingInfo info) {
            super(info);
        }

        public double getG2Base() {
            return this.g2Base;
        }

        public void setG2Base(double value) {
            this.g2Base = value;
        }
    }
}

