/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.plot;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.chart.urls.CategoryURLGenerator;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintList;
import org.jfree.util.PaintUtilities;
import org.jfree.util.Rotation;
import org.jfree.util.ShapeUtilities;
import org.jfree.util.StrokeList;
import org.jfree.util.TableOrder;

public class SpiderWebPlot
extends Plot
implements Cloneable,
Serializable {
    private static final long serialVersionUID = -5376340422031599463L;
    public static final double DEFAULT_HEAD = 0.01;
    public static final double DEFAULT_AXIS_LABEL_GAP = 0.1;
    public static final double DEFAULT_INTERIOR_GAP = 0.25;
    public static final double MAX_INTERIOR_GAP = 0.4;
    public static final double DEFAULT_START_ANGLE = 90.0;
    public static final Font DEFAULT_LABEL_FONT = new Font("SansSerif", 0, 10);
    public static final Paint DEFAULT_LABEL_PAINT = Color.black;
    public static final Paint DEFAULT_LABEL_BACKGROUND_PAINT = new Color(255, 255, 192);
    public static final Paint DEFAULT_LABEL_OUTLINE_PAINT = Color.black;
    public static final Stroke DEFAULT_LABEL_OUTLINE_STROKE = new BasicStroke(0.5f);
    public static final Paint DEFAULT_LABEL_SHADOW_PAINT = Color.lightGray;
    public static final double DEFAULT_MAX_VALUE = -1.0;
    protected double headPercent;
    private double interiorGap;
    private double axisLabelGap;
    private transient Paint axisLinePaint;
    private transient Stroke axisLineStroke;
    private CategoryDataset dataset;
    private double maxValue;
    private TableOrder dataExtractOrder;
    private double startAngle;
    private Rotation direction;
    private transient Shape legendItemShape;
    private transient Paint seriesPaint;
    private PaintList seriesPaintList;
    private transient Paint baseSeriesPaint;
    private transient Paint seriesOutlinePaint;
    private PaintList seriesOutlinePaintList;
    private transient Paint baseSeriesOutlinePaint;
    private transient Stroke seriesOutlineStroke;
    private StrokeList seriesOutlineStrokeList;
    private transient Stroke baseSeriesOutlineStroke;
    private Font labelFont;
    private transient Paint labelPaint;
    private CategoryItemLabelGenerator labelGenerator;
    private boolean webFilled = true;
    private CategoryToolTipGenerator toolTipGenerator;
    private CategoryURLGenerator urlGenerator;

    public SpiderWebPlot() {
        this(null);
    }

    public SpiderWebPlot(CategoryDataset dataset) {
        this(dataset, TableOrder.BY_ROW);
    }

    public SpiderWebPlot(CategoryDataset dataset, TableOrder extract) {
        if (extract == null) {
            throw new IllegalArgumentException("Null 'extract' argument.");
        }
        this.dataset = dataset;
        if (dataset != null) {
            dataset.addChangeListener(this);
        }
        this.dataExtractOrder = extract;
        this.headPercent = 0.01;
        this.axisLabelGap = 0.1;
        this.axisLinePaint = Color.black;
        this.axisLineStroke = new BasicStroke(1.0f);
        this.interiorGap = 0.25;
        this.startAngle = 90.0;
        this.direction = Rotation.CLOCKWISE;
        this.maxValue = -1.0;
        this.seriesPaint = null;
        this.seriesPaintList = new PaintList();
        this.baseSeriesPaint = null;
        this.seriesOutlinePaint = null;
        this.seriesOutlinePaintList = new PaintList();
        this.baseSeriesOutlinePaint = DEFAULT_OUTLINE_PAINT;
        this.seriesOutlineStroke = null;
        this.seriesOutlineStrokeList = new StrokeList();
        this.baseSeriesOutlineStroke = DEFAULT_OUTLINE_STROKE;
        this.labelFont = DEFAULT_LABEL_FONT;
        this.labelPaint = DEFAULT_LABEL_PAINT;
        this.labelGenerator = new StandardCategoryItemLabelGenerator();
        this.legendItemShape = DEFAULT_LEGEND_ITEM_CIRCLE;
    }

    public String getPlotType() {
        return "Spider Web Plot";
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

    public boolean isWebFilled() {
        return this.webFilled;
    }

    public void setWebFilled(boolean flag) {
        this.webFilled = flag;
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

    public double getHeadPercent() {
        return this.headPercent;
    }

    public void setHeadPercent(double percent) {
        this.headPercent = percent;
        this.fireChangeEvent();
    }

    public double getStartAngle() {
        return this.startAngle;
    }

    public void setStartAngle(double angle) {
        this.startAngle = angle;
        this.fireChangeEvent();
    }

    public double getMaxValue() {
        return this.maxValue;
    }

    public void setMaxValue(double value) {
        this.maxValue = value;
        this.fireChangeEvent();
    }

    public Rotation getDirection() {
        return this.direction;
    }

    public void setDirection(Rotation direction) {
        if (direction == null) {
            throw new IllegalArgumentException("Null 'direction' argument.");
        }
        this.direction = direction;
        this.fireChangeEvent();
    }

    public double getInteriorGap() {
        return this.interiorGap;
    }

    public void setInteriorGap(double percent) {
        if (percent < 0.0 || percent > 0.4) {
            throw new IllegalArgumentException("Percentage outside valid range.");
        }
        if (this.interiorGap != percent) {
            this.interiorGap = percent;
            this.fireChangeEvent();
        }
    }

    public double getAxisLabelGap() {
        return this.axisLabelGap;
    }

    public void setAxisLabelGap(double gap) {
        this.axisLabelGap = gap;
        this.fireChangeEvent();
    }

    public Paint getAxisLinePaint() {
        return this.axisLinePaint;
    }

    public void setAxisLinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.axisLinePaint = paint;
        this.fireChangeEvent();
    }

    public Stroke getAxisLineStroke() {
        return this.axisLineStroke;
    }

    public void setAxisLineStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.axisLineStroke = stroke;
        this.fireChangeEvent();
    }

    public Paint getSeriesPaint() {
        return this.seriesPaint;
    }

    public void setSeriesPaint(Paint paint) {
        this.seriesPaint = paint;
        this.fireChangeEvent();
    }

    public Paint getSeriesPaint(int series) {
        if (this.seriesPaint != null) {
            return this.seriesPaint;
        }
        Paint result = this.seriesPaintList.getPaint(series);
        if (result == null) {
            DrawingSupplier supplier = this.getDrawingSupplier();
            if (supplier != null) {
                Paint p = supplier.getNextPaint();
                this.seriesPaintList.setPaint(series, p);
                result = p;
            } else {
                result = this.baseSeriesPaint;
            }
        }
        return result;
    }

    public void setSeriesPaint(int series, Paint paint) {
        this.seriesPaintList.setPaint(series, paint);
        this.fireChangeEvent();
    }

    public Paint getBaseSeriesPaint() {
        return this.baseSeriesPaint;
    }

    public void setBaseSeriesPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.baseSeriesPaint = paint;
        this.fireChangeEvent();
    }

    public Paint getSeriesOutlinePaint() {
        return this.seriesOutlinePaint;
    }

    public void setSeriesOutlinePaint(Paint paint) {
        this.seriesOutlinePaint = paint;
        this.fireChangeEvent();
    }

    public Paint getSeriesOutlinePaint(int series) {
        if (this.seriesOutlinePaint != null) {
            return this.seriesOutlinePaint;
        }
        Paint result = this.seriesOutlinePaintList.getPaint(series);
        if (result == null) {
            result = this.baseSeriesOutlinePaint;
        }
        return result;
    }

    public void setSeriesOutlinePaint(int series, Paint paint) {
        this.seriesOutlinePaintList.setPaint(series, paint);
        this.fireChangeEvent();
    }

    public Paint getBaseSeriesOutlinePaint() {
        return this.baseSeriesOutlinePaint;
    }

    public void setBaseSeriesOutlinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.baseSeriesOutlinePaint = paint;
        this.fireChangeEvent();
    }

    public Stroke getSeriesOutlineStroke() {
        return this.seriesOutlineStroke;
    }

    public void setSeriesOutlineStroke(Stroke stroke) {
        this.seriesOutlineStroke = stroke;
        this.fireChangeEvent();
    }

    public Stroke getSeriesOutlineStroke(int series) {
        if (this.seriesOutlineStroke != null) {
            return this.seriesOutlineStroke;
        }
        Stroke result = this.seriesOutlineStrokeList.getStroke(series);
        if (result == null) {
            result = this.baseSeriesOutlineStroke;
        }
        return result;
    }

    public void setSeriesOutlineStroke(int series, Stroke stroke) {
        this.seriesOutlineStrokeList.setStroke(series, stroke);
        this.fireChangeEvent();
    }

    public Stroke getBaseSeriesOutlineStroke() {
        return this.baseSeriesOutlineStroke;
    }

    public void setBaseSeriesOutlineStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.baseSeriesOutlineStroke = stroke;
        this.fireChangeEvent();
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

    public Font getLabelFont() {
        return this.labelFont;
    }

    public void setLabelFont(Font font) {
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");
        }
        this.labelFont = font;
        this.fireChangeEvent();
    }

    public Paint getLabelPaint() {
        return this.labelPaint;
    }

    public void setLabelPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.labelPaint = paint;
        this.fireChangeEvent();
    }

    public CategoryItemLabelGenerator getLabelGenerator() {
        return this.labelGenerator;
    }

    public void setLabelGenerator(CategoryItemLabelGenerator generator) {
        if (generator == null) {
            throw new IllegalArgumentException("Null 'generator' argument.");
        }
        this.labelGenerator = generator;
    }

    public CategoryToolTipGenerator getToolTipGenerator() {
        return this.toolTipGenerator;
    }

    public void setToolTipGenerator(CategoryToolTipGenerator generator) {
        this.toolTipGenerator = generator;
        this.fireChangeEvent();
    }

    public CategoryURLGenerator getURLGenerator() {
        return this.urlGenerator;
    }

    public void setURLGenerator(CategoryURLGenerator generator) {
        this.urlGenerator = generator;
        this.fireChangeEvent();
    }

    public LegendItemCollection getLegendItems() {
        LegendItemCollection result = new LegendItemCollection();
        if (this.getDataset() == null) {
            return result;
        }
        List keys = null;
        if (this.dataExtractOrder == TableOrder.BY_ROW) {
            keys = this.dataset.getRowKeys();
        } else if (this.dataExtractOrder == TableOrder.BY_COLUMN) {
            keys = this.dataset.getColumnKeys();
        }
        if (keys != null) {
            int series = 0;
            Iterator iterator = keys.iterator();
            Shape shape = this.getLegendItemShape();
            while (iterator.hasNext()) {
                String label;
                String description = label = iterator.next().toString();
                Paint paint = this.getSeriesPaint(series);
                Paint outlinePaint = this.getSeriesOutlinePaint(series);
                Stroke stroke = this.getSeriesOutlineStroke(series);
                LegendItem item = new LegendItem(label, description, null, null, shape, paint, stroke, outlinePaint);
                item.setDataset(this.getDataset());
                result.add(item);
                ++series;
            }
        }
        return result;
    }

    protected Point2D getWebPoint(Rectangle2D bounds, double angle, double length) {
        double angrad = Math.toRadians(angle);
        double x = Math.cos(angrad) * length * bounds.getWidth() / 2.0;
        double y = -Math.sin(angrad) * length * bounds.getHeight() / 2.0;
        return new Point2D.Double(bounds.getX() + x + bounds.getWidth() / 2.0, bounds.getY() + y + bounds.getHeight() / 2.0);
    }

    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {
        RectangleInsets insets = this.getInsets();
        insets.trim(area);
        if (info != null) {
            info.setPlotArea(area);
            info.setDataArea(area);
        }
        this.drawBackground(g2, area);
        this.drawOutline(g2, area);
        Shape savedClip = g2.getClip();
        g2.clip(area);
        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(3, this.getForegroundAlpha()));
        if (!DatasetUtilities.isEmptyOrNull(this.dataset)) {
            int seriesCount = 0;
            int catCount = 0;
            if (this.dataExtractOrder == TableOrder.BY_ROW) {
                seriesCount = this.dataset.getRowCount();
                catCount = this.dataset.getColumnCount();
            } else {
                seriesCount = this.dataset.getColumnCount();
                catCount = this.dataset.getRowCount();
            }
            if (this.maxValue == -1.0) {
                this.calculateMaxValue(seriesCount, catCount);
            }
            double gapHorizontal = area.getWidth() * this.getInteriorGap();
            double gapVertical = area.getHeight() * this.getInteriorGap();
            double X = area.getX() + gapHorizontal / 2.0;
            double Y = area.getY() + gapVertical / 2.0;
            double W = area.getWidth() - gapHorizontal;
            double H = area.getHeight() - gapVertical;
            double headW = area.getWidth() * this.headPercent;
            double headH = area.getHeight() * this.headPercent;
            double min = Math.min(W, H) / 2.0;
            X = (X + X + W) / 2.0 - min;
            Y = (Y + Y + H) / 2.0 - min;
            W = 2.0 * min;
            H = 2.0 * min;
            Point2D.Double centre = new Point2D.Double(X + W / 2.0, Y + H / 2.0);
            Rectangle2D.Double radarArea = new Rectangle2D.Double(X, Y, W, H);
            for (int cat = 0; cat < catCount; ++cat) {
                double angle = this.getStartAngle() + this.getDirection().getFactor() * (double)cat * 360.0 / (double)catCount;
                Point2D endPoint = this.getWebPoint(radarArea, angle, 1.0);
                Line2D.Double line = new Line2D.Double(centre, endPoint);
                g2.setPaint(this.axisLinePaint);
                g2.setStroke(this.axisLineStroke);
                g2.draw(line);
                this.drawLabel(g2, radarArea, 0.0, cat, angle, 360.0 / (double)catCount);
            }
            for (int series = 0; series < seriesCount; ++series) {
                this.drawRadarPoly(g2, radarArea, centre, info, series, catCount, headH, headW);
            }
        } else {
            this.drawNoDataMessage(g2, area);
        }
        g2.setClip(savedClip);
        g2.setComposite(originalComposite);
        this.drawOutline(g2, area);
    }

    private void calculateMaxValue(int seriesCount, int catCount) {
        double v = 0.0;
        Number nV = null;
        for (int seriesIndex = 0; seriesIndex < seriesCount; ++seriesIndex) {
            for (int catIndex = 0; catIndex < catCount; ++catIndex) {
                nV = this.getPlotValue(seriesIndex, catIndex);
                if (nV == null || !((v = nV.doubleValue()) > this.maxValue)) continue;
                this.maxValue = v;
            }
        }
    }

    protected void drawRadarPoly(Graphics2D g2, Rectangle2D plotArea, Point2D centre, PlotRenderingInfo info, int series, int catCount, double headH, double headW) {
        Polygon polygon = new Polygon();
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getOwner().getEntityCollection();
        }
        for (int cat = 0; cat < catCount; ++cat) {
            double value;
            Number dataValue = this.getPlotValue(series, cat);
            if (dataValue == null || !((value = dataValue.doubleValue()) >= 0.0)) continue;
            double angle = this.getStartAngle() + this.getDirection().getFactor() * (double)cat * 360.0 / (double)catCount;
            Point2D point = this.getWebPoint(plotArea, angle, value / this.maxValue);
            polygon.addPoint((int)point.getX(), (int)point.getY());
            Paint paint = this.getSeriesPaint(series);
            Paint outlinePaint = this.getSeriesOutlinePaint(series);
            Stroke outlineStroke = this.getSeriesOutlineStroke(series);
            Ellipse2D.Double head = new Ellipse2D.Double(point.getX() - headW / 2.0, point.getY() - headH / 2.0, headW, headH);
            g2.setPaint(paint);
            g2.fill(head);
            g2.setStroke(outlineStroke);
            g2.setPaint(outlinePaint);
            g2.draw(head);
            if (entities == null) continue;
            int row = 0;
            int col = 0;
            if (this.dataExtractOrder == TableOrder.BY_ROW) {
                row = series;
                col = cat;
            } else {
                row = cat;
                col = series;
            }
            String tip = null;
            if (this.toolTipGenerator != null) {
                tip = this.toolTipGenerator.generateToolTip(this.dataset, row, col);
            }
            String url = null;
            if (this.urlGenerator != null) {
                url = this.urlGenerator.generateURL(this.dataset, row, col);
            }
            Rectangle area = new Rectangle((int)(point.getX() - headW), (int)(point.getY() - headH), (int)(headW * 2.0), (int)(headH * 2.0));
            CategoryItemEntity entity = new CategoryItemEntity(area, tip, url, this.dataset, this.dataset.getRowKey(row), this.dataset.getColumnKey(col));
            entities.add(entity);
        }
        Paint paint = this.getSeriesPaint(series);
        g2.setPaint(paint);
        g2.setStroke(this.getSeriesOutlineStroke(series));
        g2.draw(polygon);
        if (this.webFilled) {
            g2.setComposite(AlphaComposite.getInstance(3, 0.1f));
            g2.fill(polygon);
            g2.setComposite(AlphaComposite.getInstance(3, this.getForegroundAlpha()));
        }
    }

    protected Number getPlotValue(int series, int cat) {
        Number value = null;
        if (this.dataExtractOrder == TableOrder.BY_ROW) {
            value = this.dataset.getValue(series, cat);
        } else if (this.dataExtractOrder == TableOrder.BY_COLUMN) {
            value = this.dataset.getValue(cat, series);
        }
        return value;
    }

    protected void drawLabel(Graphics2D g2, Rectangle2D plotArea, double value, int cat, double startAngle, double extent) {
        FontRenderContext frc = g2.getFontRenderContext();
        String label = null;
        label = this.dataExtractOrder == TableOrder.BY_ROW ? this.labelGenerator.generateColumnLabel(this.dataset, cat) : this.labelGenerator.generateRowLabel(this.dataset, cat);
        Rectangle2D labelBounds = this.getLabelFont().getStringBounds(label, frc);
        LineMetrics lm = this.getLabelFont().getLineMetrics(label, frc);
        double ascent = lm.getAscent();
        Point2D labelLocation = this.calculateLabelLocation(labelBounds, ascent, plotArea, startAngle);
        Composite saveComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(3, 1.0f));
        g2.setPaint(this.getLabelPaint());
        g2.setFont(this.getLabelFont());
        g2.drawString(label, (float)labelLocation.getX(), (float)labelLocation.getY());
        g2.setComposite(saveComposite);
    }

    protected Point2D calculateLabelLocation(Rectangle2D labelBounds, double ascent, Rectangle2D plotArea, double startAngle) {
        Arc2D.Double arc1 = new Arc2D.Double(plotArea, startAngle, 0.0, 0);
        Point2D point1 = arc1.getEndPoint();
        double deltaX = -(point1.getX() - plotArea.getCenterX()) * this.axisLabelGap;
        double deltaY = -(point1.getY() - plotArea.getCenterY()) * this.axisLabelGap;
        double labelX = point1.getX() - deltaX;
        double labelY = point1.getY() - deltaY;
        if (labelX < plotArea.getCenterX()) {
            labelX -= labelBounds.getWidth();
        }
        if (labelX == plotArea.getCenterX()) {
            labelX -= labelBounds.getWidth() / 2.0;
        }
        if (labelY > plotArea.getCenterY()) {
            labelY += ascent;
        }
        return new Point2D.Double(labelX, labelY);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SpiderWebPlot)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        SpiderWebPlot that = (SpiderWebPlot)obj;
        if (!this.dataExtractOrder.equals(that.dataExtractOrder)) {
            return false;
        }
        if (this.headPercent != that.headPercent) {
            return false;
        }
        if (this.interiorGap != that.interiorGap) {
            return false;
        }
        if (this.startAngle != that.startAngle) {
            return false;
        }
        if (!this.direction.equals(that.direction)) {
            return false;
        }
        if (this.maxValue != that.maxValue) {
            return false;
        }
        if (this.webFilled != that.webFilled) {
            return false;
        }
        if (this.axisLabelGap != that.axisLabelGap) {
            return false;
        }
        if (!PaintUtilities.equal(this.axisLinePaint, that.axisLinePaint)) {
            return false;
        }
        if (!this.axisLineStroke.equals(that.axisLineStroke)) {
            return false;
        }
        if (!ShapeUtilities.equal(this.legendItemShape, that.legendItemShape)) {
            return false;
        }
        if (!PaintUtilities.equal(this.seriesPaint, that.seriesPaint)) {
            return false;
        }
        if (!this.seriesPaintList.equals(that.seriesPaintList)) {
            return false;
        }
        if (!PaintUtilities.equal(this.baseSeriesPaint, that.baseSeriesPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.seriesOutlinePaint, that.seriesOutlinePaint)) {
            return false;
        }
        if (!this.seriesOutlinePaintList.equals(that.seriesOutlinePaintList)) {
            return false;
        }
        if (!PaintUtilities.equal(this.baseSeriesOutlinePaint, that.baseSeriesOutlinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.seriesOutlineStroke, that.seriesOutlineStroke)) {
            return false;
        }
        if (!this.seriesOutlineStrokeList.equals(that.seriesOutlineStrokeList)) {
            return false;
        }
        if (!this.baseSeriesOutlineStroke.equals(that.baseSeriesOutlineStroke)) {
            return false;
        }
        if (!this.labelFont.equals(that.labelFont)) {
            return false;
        }
        if (!PaintUtilities.equal(this.labelPaint, that.labelPaint)) {
            return false;
        }
        if (!this.labelGenerator.equals(that.labelGenerator)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.toolTipGenerator, that.toolTipGenerator)) {
            return false;
        }
        return ObjectUtilities.equal(this.urlGenerator, that.urlGenerator);
    }

    public Object clone() throws CloneNotSupportedException {
        SpiderWebPlot clone = (SpiderWebPlot)super.clone();
        clone.legendItemShape = ShapeUtilities.clone(this.legendItemShape);
        clone.seriesPaintList = (PaintList)this.seriesPaintList.clone();
        clone.seriesOutlinePaintList = (PaintList)this.seriesOutlinePaintList.clone();
        clone.seriesOutlineStrokeList = (StrokeList)this.seriesOutlineStrokeList.clone();
        return clone;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeShape(this.legendItemShape, stream);
        SerialUtilities.writePaint(this.seriesPaint, stream);
        SerialUtilities.writePaint(this.baseSeriesPaint, stream);
        SerialUtilities.writePaint(this.seriesOutlinePaint, stream);
        SerialUtilities.writePaint(this.baseSeriesOutlinePaint, stream);
        SerialUtilities.writeStroke(this.seriesOutlineStroke, stream);
        SerialUtilities.writeStroke(this.baseSeriesOutlineStroke, stream);
        SerialUtilities.writePaint(this.labelPaint, stream);
        SerialUtilities.writePaint(this.axisLinePaint, stream);
        SerialUtilities.writeStroke(this.axisLineStroke, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.legendItemShape = SerialUtilities.readShape(stream);
        this.seriesPaint = SerialUtilities.readPaint(stream);
        this.baseSeriesPaint = SerialUtilities.readPaint(stream);
        this.seriesOutlinePaint = SerialUtilities.readPaint(stream);
        this.baseSeriesOutlinePaint = SerialUtilities.readPaint(stream);
        this.seriesOutlineStroke = SerialUtilities.readStroke(stream);
        this.baseSeriesOutlineStroke = SerialUtilities.readStroke(stream);
        this.labelPaint = SerialUtilities.readPaint(stream);
        this.axisLinePaint = SerialUtilities.readPaint(stream);
        this.axisLineStroke = SerialUtilities.readStroke(stream);
        if (this.dataset != null) {
            this.dataset.addChangeListener(this);
        }
    }
}

