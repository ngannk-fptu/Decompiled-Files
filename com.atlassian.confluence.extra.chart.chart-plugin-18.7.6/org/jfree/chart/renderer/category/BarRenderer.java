/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.renderer.category;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.renderer.category.AbstractCategoryItemRenderer;
import org.jfree.chart.renderer.category.BarPainter;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.chart.renderer.category.GradientBarPainter;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.GradientPaintTransformer;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.StandardGradientPaintTransformer;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

public class BarRenderer
extends AbstractCategoryItemRenderer
implements Cloneable,
PublicCloneable,
Serializable {
    private static final long serialVersionUID = 6000649414965887481L;
    public static final double DEFAULT_ITEM_MARGIN = 0.2;
    public static final double BAR_OUTLINE_WIDTH_THRESHOLD = 3.0;
    private static BarPainter defaultBarPainter = new GradientBarPainter();
    private static boolean defaultShadowsVisible = true;
    private double itemMargin = 0.2;
    private boolean drawBarOutline = false;
    private double maximumBarWidth = 1.0;
    private double minimumBarLength = 0.0;
    private GradientPaintTransformer gradientPaintTransformer = new StandardGradientPaintTransformer();
    private ItemLabelPosition positiveItemLabelPositionFallback = null;
    private ItemLabelPosition negativeItemLabelPositionFallback = null;
    private double upperClip;
    private double lowerClip;
    private double base = 0.0;
    private boolean includeBaseInRange = true;
    private BarPainter barPainter;
    private boolean shadowsVisible;
    private transient Paint shadowPaint;
    private double shadowXOffset;
    private double shadowYOffset;

    public static BarPainter getDefaultBarPainter() {
        return defaultBarPainter;
    }

    public static void setDefaultBarPainter(BarPainter painter) {
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

    public BarRenderer() {
        this.setBaseLegendShape(new Rectangle2D.Double(-4.0, -4.0, 8.0, 8.0));
        this.barPainter = BarRenderer.getDefaultBarPainter();
        this.shadowsVisible = BarRenderer.getDefaultShadowsVisible();
        this.shadowPaint = Color.gray;
        this.shadowXOffset = 4.0;
        this.shadowYOffset = 4.0;
    }

    public double getBase() {
        return this.base;
    }

    public void setBase(double base) {
        this.base = base;
        this.fireChangeEvent();
    }

    public double getItemMargin() {
        return this.itemMargin;
    }

    public void setItemMargin(double percent) {
        this.itemMargin = percent;
        this.fireChangeEvent();
    }

    public boolean isDrawBarOutline() {
        return this.drawBarOutline;
    }

    public void setDrawBarOutline(boolean draw) {
        this.drawBarOutline = draw;
        this.fireChangeEvent();
    }

    public double getMaximumBarWidth() {
        return this.maximumBarWidth;
    }

    public void setMaximumBarWidth(double percent) {
        this.maximumBarWidth = percent;
        this.fireChangeEvent();
    }

    public double getMinimumBarLength() {
        return this.minimumBarLength;
    }

    public void setMinimumBarLength(double min) {
        if (min < 0.0) {
            throw new IllegalArgumentException("Requires 'min' >= 0.0");
        }
        this.minimumBarLength = min;
        this.fireChangeEvent();
    }

    public GradientPaintTransformer getGradientPaintTransformer() {
        return this.gradientPaintTransformer;
    }

    public void setGradientPaintTransformer(GradientPaintTransformer transformer) {
        this.gradientPaintTransformer = transformer;
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

    public boolean getIncludeBaseInRange() {
        return this.includeBaseInRange;
    }

    public void setIncludeBaseInRange(boolean include) {
        if (this.includeBaseInRange != include) {
            this.includeBaseInRange = include;
            this.fireChangeEvent();
        }
    }

    public BarPainter getBarPainter() {
        return this.barPainter;
    }

    public void setBarPainter(BarPainter painter) {
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

    public Paint getShadowPaint() {
        return this.shadowPaint;
    }

    public void setShadowPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.shadowPaint = paint;
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

    public double getLowerClip() {
        return this.lowerClip;
    }

    public double getUpperClip() {
        return this.upperClip;
    }

    public CategoryItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, CategoryPlot plot, int rendererIndex, PlotRenderingInfo info) {
        CategoryItemRendererState state = super.initialise(g2, dataArea, plot, rendererIndex, info);
        ValueAxis rangeAxis = plot.getRangeAxisForDataset(rendererIndex);
        this.lowerClip = rangeAxis.getRange().getLowerBound();
        this.upperClip = rangeAxis.getRange().getUpperBound();
        this.calculateBarWidth(plot, dataArea, rendererIndex, state);
        return state;
    }

    protected void calculateBarWidth(CategoryPlot plot, Rectangle2D dataArea, int rendererIndex, CategoryItemRendererState state) {
        CategoryAxis domainAxis = this.getDomainAxis(plot, rendererIndex);
        CategoryDataset dataset = plot.getDataset(rendererIndex);
        if (dataset != null) {
            int columns = dataset.getColumnCount();
            int rows = state.getVisibleSeriesCount() >= 0 ? state.getVisibleSeriesCount() : dataset.getRowCount();
            double space = 0.0;
            PlotOrientation orientation = plot.getOrientation();
            if (orientation == PlotOrientation.HORIZONTAL) {
                space = dataArea.getHeight();
            } else if (orientation == PlotOrientation.VERTICAL) {
                space = dataArea.getWidth();
            }
            double maxWidth = space * this.getMaximumBarWidth();
            double categoryMargin = 0.0;
            double currentItemMargin = 0.0;
            if (columns > 1) {
                categoryMargin = domainAxis.getCategoryMargin();
            }
            if (rows > 1) {
                currentItemMargin = this.getItemMargin();
            }
            double used = space * (1.0 - domainAxis.getLowerMargin() - domainAxis.getUpperMargin() - categoryMargin - currentItemMargin);
            if (rows * columns > 0) {
                state.setBarWidth(Math.min(used / (double)(rows * columns), maxWidth));
            } else {
                state.setBarWidth(Math.min(used, maxWidth));
            }
        }
    }

    protected double calculateBarW0(CategoryPlot plot, PlotOrientation orientation, Rectangle2D dataArea, CategoryAxis domainAxis, CategoryItemRendererState state, int row, int column) {
        double space = 0.0;
        space = orientation == PlotOrientation.HORIZONTAL ? dataArea.getHeight() : dataArea.getWidth();
        double barW0 = domainAxis.getCategoryStart(column, this.getColumnCount(), dataArea, plot.getDomainAxisEdge());
        int seriesCount = state.getVisibleSeriesCount() >= 0 ? state.getVisibleSeriesCount() : this.getRowCount();
        int categoryCount = this.getColumnCount();
        if (seriesCount > 1) {
            double seriesGap = space * this.getItemMargin() / (double)(categoryCount * (seriesCount - 1));
            double seriesW = this.calculateSeriesWidth(space, domainAxis, categoryCount, seriesCount);
            barW0 = barW0 + (double)row * (seriesW + seriesGap) + seriesW / 2.0 - state.getBarWidth() / 2.0;
        } else {
            barW0 = domainAxis.getCategoryMiddle(column, this.getColumnCount(), dataArea, plot.getDomainAxisEdge()) - state.getBarWidth() / 2.0;
        }
        return barW0;
    }

    protected double[] calculateBarL0L1(double value) {
        double lclip = this.getLowerClip();
        double uclip = this.getUpperClip();
        double barLow = Math.min(this.base, value);
        double barHigh = Math.max(this.base, value);
        if (barHigh < lclip) {
            return null;
        }
        if (barLow > uclip) {
            return null;
        }
        barLow = Math.max(barLow, lclip);
        barHigh = Math.min(barHigh, uclip);
        return new double[]{barLow, barHigh};
    }

    public Range findRangeBounds(CategoryDataset dataset) {
        if (dataset == null) {
            return null;
        }
        Range result = DatasetUtilities.findRangeBounds(dataset);
        if (result != null && this.includeBaseInRange) {
            result = Range.expandToInclude(result, this.base);
        }
        return result;
    }

    public LegendItem getLegendItem(int datasetIndex, int series) {
        String label;
        CategoryPlot cp = this.getPlot();
        if (cp == null) {
            return null;
        }
        if (!this.isSeriesVisible(series) || !this.isSeriesVisibleInLegend(series)) {
            return null;
        }
        CategoryDataset dataset = cp.getDataset(datasetIndex);
        String description = label = this.getLegendItemLabelGenerator().generateLabel(dataset, series);
        String toolTipText = null;
        if (this.getLegendItemToolTipGenerator() != null) {
            toolTipText = this.getLegendItemToolTipGenerator().generateLabel(dataset, series);
        }
        String urlText = null;
        if (this.getLegendItemURLGenerator() != null) {
            urlText = this.getLegendItemURLGenerator().generateLabel(dataset, series);
        }
        Shape shape = this.lookupLegendShape(series);
        Paint paint = this.lookupSeriesPaint(series);
        Paint outlinePaint = this.lookupSeriesOutlinePaint(series);
        Stroke outlineStroke = this.lookupSeriesOutlineStroke(series);
        LegendItem result = new LegendItem(label, description, toolTipText, urlText, true, shape, true, paint, this.isDrawBarOutline(), outlinePaint, outlineStroke, false, (Shape)new Line2D.Float(), (Stroke)new BasicStroke(1.0f), (Paint)Color.black);
        result.setLabelFont(this.lookupLegendTextFont(series));
        Paint labelPaint = this.lookupLegendTextPaint(series);
        if (labelPaint != null) {
            result.setLabelPaint(labelPaint);
        }
        result.setDataset(dataset);
        result.setDatasetIndex(datasetIndex);
        result.setSeriesKey(dataset.getRowKey(series));
        result.setSeriesIndex(series);
        if (this.gradientPaintTransformer != null) {
            result.setFillPaintTransformer(this.gradientPaintTransformer);
        }
        return result;
    }

    public void drawItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column, int pass) {
        RectangleEdge barBase;
        int visibleRow = state.getVisibleSeriesIndex(row);
        if (visibleRow < 0) {
            return;
        }
        Number dataValue = dataset.getValue(row, column);
        if (dataValue == null) {
            return;
        }
        double value = dataValue.doubleValue();
        PlotOrientation orientation = plot.getOrientation();
        double barW0 = this.calculateBarW0(plot, orientation, dataArea, domainAxis, state, visibleRow, column);
        double[] barL0L1 = this.calculateBarL0L1(value);
        if (barL0L1 == null) {
            return;
        }
        RectangleEdge edge = plot.getRangeAxisEdge();
        double transL0 = rangeAxis.valueToJava2D(barL0L1[0], dataArea, edge);
        double transL1 = rangeAxis.valueToJava2D(barL0L1[1], dataArea, edge);
        boolean positive = value >= this.base;
        boolean inverted = rangeAxis.isInverted();
        double barL0 = Math.min(transL0, transL1);
        double barLength = Math.abs(transL1 - transL0);
        double barLengthAdj = 0.0;
        if (barLength > 0.0 && barLength < this.getMinimumBarLength()) {
            barLengthAdj = this.getMinimumBarLength() - barLength;
        }
        double barL0Adj = 0.0;
        if (orientation == PlotOrientation.HORIZONTAL) {
            if (positive && inverted || !positive && !inverted) {
                barL0Adj = barLengthAdj;
                barBase = RectangleEdge.RIGHT;
            } else {
                barBase = RectangleEdge.LEFT;
            }
        } else if (positive && !inverted || !positive && inverted) {
            barL0Adj = barLengthAdj;
            barBase = RectangleEdge.BOTTOM;
        } else {
            barBase = RectangleEdge.TOP;
        }
        Rectangle2D.Double bar = null;
        bar = orientation == PlotOrientation.HORIZONTAL ? new Rectangle2D.Double(barL0 - barL0Adj, barW0, barLength + barLengthAdj, state.getBarWidth()) : new Rectangle2D.Double(barW0, barL0 - barL0Adj, state.getBarWidth(), barLength + barLengthAdj);
        if (this.getShadowsVisible()) {
            this.barPainter.paintBarShadow(g2, this, row, column, bar, barBase, true);
        }
        this.barPainter.paintBar(g2, this, row, column, bar, barBase);
        CategoryItemLabelGenerator generator = this.getItemLabelGenerator(row, column);
        if (generator != null && this.isItemLabelVisible(row, column)) {
            this.drawItemLabel(g2, dataset, row, column, plot, generator, bar, value < 0.0);
        }
        int datasetIndex = plot.indexOf(dataset);
        this.updateCrosshairValues(state.getCrosshairState(), dataset.getRowKey(row), dataset.getColumnKey(column), value, datasetIndex, barW0, barL0, orientation);
        EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
            this.addItemEntity(entities, dataset, row, column, bar);
        }
    }

    protected double calculateSeriesWidth(double space, CategoryAxis axis, int categories, int series) {
        double factor = 1.0 - this.getItemMargin() - axis.getLowerMargin() - axis.getUpperMargin();
        if (categories > 1) {
            factor -= axis.getCategoryMargin();
        }
        return space * factor / (double)(categories * series);
    }

    protected void drawItemLabel(Graphics2D g2, CategoryDataset data, int row, int column, CategoryPlot plot, CategoryItemLabelGenerator generator, Rectangle2D bar, boolean negative) {
        Shape bounds;
        String label = generator.generateLabel(data, row, column);
        if (label == null) {
            return;
        }
        Font labelFont = this.getItemLabelFont(row, column);
        g2.setFont(labelFont);
        Paint paint = this.getItemLabelPaint(row, column);
        g2.setPaint(paint);
        ItemLabelPosition position = null;
        position = !negative ? this.getPositiveItemLabelPosition(row, column) : this.getNegativeItemLabelPosition(row, column);
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

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BarRenderer)) {
            return false;
        }
        BarRenderer that = (BarRenderer)obj;
        if (this.base != that.base) {
            return false;
        }
        if (this.itemMargin != that.itemMargin) {
            return false;
        }
        if (this.drawBarOutline != that.drawBarOutline) {
            return false;
        }
        if (this.maximumBarWidth != that.maximumBarWidth) {
            return false;
        }
        if (this.minimumBarLength != that.minimumBarLength) {
            return false;
        }
        if (!ObjectUtilities.equal(this.gradientPaintTransformer, that.gradientPaintTransformer)) {
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
        if (!PaintUtilities.equal(this.shadowPaint, that.shadowPaint)) {
            return false;
        }
        if (this.shadowXOffset != that.shadowXOffset) {
            return false;
        }
        if (this.shadowYOffset != that.shadowYOffset) {
            return false;
        }
        return super.equals(obj);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.shadowPaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.shadowPaint = SerialUtilities.readPaint(stream);
    }
}

