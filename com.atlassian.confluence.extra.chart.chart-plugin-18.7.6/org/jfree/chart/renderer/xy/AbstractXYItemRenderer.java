/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.renderer.xy;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardXYSeriesLabelGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.labels.XYSeriesLabelGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.text.TextUtilities;
import org.jfree.ui.GradientPaintTransformer;
import org.jfree.ui.Layer;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ObjectList;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public abstract class AbstractXYItemRenderer
extends AbstractRenderer
implements XYItemRenderer,
Cloneable,
Serializable {
    private static final long serialVersionUID = 8019124836026607990L;
    private XYPlot plot;
    private ObjectList itemLabelGeneratorList = new ObjectList();
    private XYItemLabelGenerator baseItemLabelGenerator;
    private ObjectList toolTipGeneratorList = new ObjectList();
    private XYToolTipGenerator baseToolTipGenerator;
    private XYURLGenerator urlGenerator = null;
    private List backgroundAnnotations = new ArrayList();
    private List foregroundAnnotations = new ArrayList();
    private XYSeriesLabelGenerator legendItemLabelGenerator = new StandardXYSeriesLabelGenerator("{0}");
    private XYSeriesLabelGenerator legendItemToolTipGenerator;
    private XYSeriesLabelGenerator legendItemURLGenerator;
    private XYItemLabelGenerator itemLabelGenerator = null;
    private XYToolTipGenerator toolTipGenerator = null;

    protected AbstractXYItemRenderer() {
    }

    public int getPassCount() {
        return 1;
    }

    public XYPlot getPlot() {
        return this.plot;
    }

    public void setPlot(XYPlot plot) {
        this.plot = plot;
    }

    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset data, PlotRenderingInfo info) {
        XYItemRendererState state = new XYItemRendererState(info);
        return state;
    }

    public XYItemLabelGenerator getItemLabelGenerator(int series, int item) {
        if (this.itemLabelGenerator != null) {
            return this.itemLabelGenerator;
        }
        XYItemLabelGenerator generator = (XYItemLabelGenerator)this.itemLabelGeneratorList.get(series);
        if (generator == null) {
            generator = this.baseItemLabelGenerator;
        }
        return generator;
    }

    public XYItemLabelGenerator getSeriesItemLabelGenerator(int series) {
        return (XYItemLabelGenerator)this.itemLabelGeneratorList.get(series);
    }

    public void setSeriesItemLabelGenerator(int series, XYItemLabelGenerator generator) {
        this.itemLabelGeneratorList.set(series, generator);
        this.fireChangeEvent();
    }

    public XYItemLabelGenerator getBaseItemLabelGenerator() {
        return this.baseItemLabelGenerator;
    }

    public void setBaseItemLabelGenerator(XYItemLabelGenerator generator) {
        this.baseItemLabelGenerator = generator;
        this.fireChangeEvent();
    }

    public XYToolTipGenerator getToolTipGenerator(int series, int item) {
        if (this.toolTipGenerator != null) {
            return this.toolTipGenerator;
        }
        XYToolTipGenerator generator = (XYToolTipGenerator)this.toolTipGeneratorList.get(series);
        if (generator == null) {
            generator = this.baseToolTipGenerator;
        }
        return generator;
    }

    public XYToolTipGenerator getSeriesToolTipGenerator(int series) {
        return (XYToolTipGenerator)this.toolTipGeneratorList.get(series);
    }

    public void setSeriesToolTipGenerator(int series, XYToolTipGenerator generator) {
        this.toolTipGeneratorList.set(series, generator);
        this.fireChangeEvent();
    }

    public XYToolTipGenerator getBaseToolTipGenerator() {
        return this.baseToolTipGenerator;
    }

    public void setBaseToolTipGenerator(XYToolTipGenerator generator) {
        this.baseToolTipGenerator = generator;
        this.fireChangeEvent();
    }

    public XYURLGenerator getURLGenerator() {
        return this.urlGenerator;
    }

    public void setURLGenerator(XYURLGenerator urlGenerator) {
        this.urlGenerator = urlGenerator;
        this.fireChangeEvent();
    }

    public void addAnnotation(XYAnnotation annotation) {
        this.addAnnotation(annotation, Layer.FOREGROUND);
    }

    public void addAnnotation(XYAnnotation annotation, Layer layer) {
        if (annotation == null) {
            throw new IllegalArgumentException("Null 'annotation' argument.");
        }
        if (layer.equals(Layer.FOREGROUND)) {
            this.foregroundAnnotations.add(annotation);
            this.fireChangeEvent();
        } else if (layer.equals(Layer.BACKGROUND)) {
            this.backgroundAnnotations.add(annotation);
            this.fireChangeEvent();
        } else {
            throw new RuntimeException("Unknown layer.");
        }
    }

    public boolean removeAnnotation(XYAnnotation annotation) {
        boolean removed = this.foregroundAnnotations.remove(annotation);
        this.fireChangeEvent();
        return removed &= this.backgroundAnnotations.remove(annotation);
    }

    public void removeAnnotations() {
        this.foregroundAnnotations.clear();
        this.backgroundAnnotations.clear();
        this.fireChangeEvent();
    }

    public Collection getAnnotations() {
        ArrayList result = new ArrayList(this.foregroundAnnotations);
        result.addAll(this.backgroundAnnotations);
        return result;
    }

    public XYSeriesLabelGenerator getLegendItemLabelGenerator() {
        return this.legendItemLabelGenerator;
    }

    public void setLegendItemLabelGenerator(XYSeriesLabelGenerator generator) {
        if (generator == null) {
            throw new IllegalArgumentException("Null 'generator' argument.");
        }
        this.legendItemLabelGenerator = generator;
        this.fireChangeEvent();
    }

    public XYSeriesLabelGenerator getLegendItemToolTipGenerator() {
        return this.legendItemToolTipGenerator;
    }

    public void setLegendItemToolTipGenerator(XYSeriesLabelGenerator generator) {
        this.legendItemToolTipGenerator = generator;
        this.fireChangeEvent();
    }

    public XYSeriesLabelGenerator getLegendItemURLGenerator() {
        return this.legendItemURLGenerator;
    }

    public void setLegendItemURLGenerator(XYSeriesLabelGenerator generator) {
        this.legendItemURLGenerator = generator;
        this.fireChangeEvent();
    }

    public Range findDomainBounds(XYDataset dataset) {
        return this.findDomainBounds(dataset, false);
    }

    protected Range findDomainBounds(XYDataset dataset, boolean includeInterval) {
        if (dataset == null) {
            return null;
        }
        if (this.getDataBoundsIncludesVisibleSeriesOnly()) {
            ArrayList<Comparable> visibleSeriesKeys = new ArrayList<Comparable>();
            int seriesCount = dataset.getSeriesCount();
            for (int s = 0; s < seriesCount; ++s) {
                if (!this.isSeriesVisible(s)) continue;
                visibleSeriesKeys.add(dataset.getSeriesKey(s));
            }
            return DatasetUtilities.findDomainBounds(dataset, visibleSeriesKeys, includeInterval);
        }
        return DatasetUtilities.findDomainBounds(dataset, includeInterval);
    }

    public Range findRangeBounds(XYDataset dataset) {
        return this.findRangeBounds(dataset, false);
    }

    protected Range findRangeBounds(XYDataset dataset, boolean includeInterval) {
        if (dataset == null) {
            return null;
        }
        if (this.getDataBoundsIncludesVisibleSeriesOnly()) {
            ArrayList<Comparable> visibleSeriesKeys = new ArrayList<Comparable>();
            int seriesCount = dataset.getSeriesCount();
            for (int s = 0; s < seriesCount; ++s) {
                if (!this.isSeriesVisible(s)) continue;
                visibleSeriesKeys.add(dataset.getSeriesKey(s));
            }
            Range xRange = null;
            XYPlot p = this.getPlot();
            if (p != null) {
                ValueAxis xAxis = null;
                int index = p.getIndexOf(this);
                if (index >= 0) {
                    xAxis = this.plot.getDomainAxisForDataset(index);
                }
                if (xAxis != null) {
                    xRange = xAxis.getRange();
                }
            }
            if (xRange == null) {
                xRange = new Range(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
            }
            return DatasetUtilities.findRangeBounds(dataset, visibleSeriesKeys, xRange, includeInterval);
        }
        return DatasetUtilities.findRangeBounds(dataset, includeInterval);
    }

    public LegendItemCollection getLegendItems() {
        if (this.plot == null) {
            return new LegendItemCollection();
        }
        LegendItemCollection result = new LegendItemCollection();
        int index = this.plot.getIndexOf(this);
        XYDataset dataset = this.plot.getDataset(index);
        if (dataset != null) {
            int seriesCount = dataset.getSeriesCount();
            for (int i = 0; i < seriesCount; ++i) {
                LegendItem item;
                if (!this.isSeriesVisibleInLegend(i) || (item = this.getLegendItem(index, i)) == null) continue;
                result.add(item);
            }
        }
        return result;
    }

    public LegendItem getLegendItem(int datasetIndex, int series) {
        XYDataset dataset;
        LegendItem result = null;
        XYPlot xyplot = this.getPlot();
        if (xyplot != null && (dataset = xyplot.getDataset(datasetIndex)) != null) {
            String label;
            String description = label = this.legendItemLabelGenerator.generateLabel(dataset, series);
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
            result = new LegendItem(label, description, toolTipText, urlText, shape, paint, outlineStroke, outlinePaint);
            Paint labelPaint = this.lookupLegendTextPaint(series);
            result.setLabelFont(this.lookupLegendTextFont(series));
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

    public void fillDomainGridBand(Graphics2D g2, XYPlot plot, ValueAxis axis, Rectangle2D dataArea, double start, double end) {
        double x1 = axis.valueToJava2D(start, dataArea, plot.getDomainAxisEdge());
        double x2 = axis.valueToJava2D(end, dataArea, plot.getDomainAxisEdge());
        Rectangle2D.Double band = plot.getOrientation() == PlotOrientation.VERTICAL ? new Rectangle2D.Double(Math.min(x1, x2), dataArea.getMinY(), Math.abs(x2 - x1), dataArea.getWidth()) : new Rectangle2D.Double(dataArea.getMinX(), Math.min(x1, x2), dataArea.getWidth(), Math.abs(x2 - x1));
        Paint paint = plot.getDomainTickBandPaint();
        if (paint != null) {
            g2.setPaint(paint);
            g2.fill(band);
        }
    }

    public void fillRangeGridBand(Graphics2D g2, XYPlot plot, ValueAxis axis, Rectangle2D dataArea, double start, double end) {
        double y1 = axis.valueToJava2D(start, dataArea, plot.getRangeAxisEdge());
        double y2 = axis.valueToJava2D(end, dataArea, plot.getRangeAxisEdge());
        Rectangle2D.Double band = plot.getOrientation() == PlotOrientation.VERTICAL ? new Rectangle2D.Double(dataArea.getMinX(), Math.min(y1, y2), dataArea.getWidth(), Math.abs(y2 - y1)) : new Rectangle2D.Double(Math.min(y1, y2), dataArea.getMinY(), Math.abs(y2 - y1), dataArea.getHeight());
        Paint paint = plot.getRangeTickBandPaint();
        if (paint != null) {
            g2.setPaint(paint);
            g2.fill(band);
        }
    }

    public void drawDomainGridLine(Graphics2D g2, XYPlot plot, ValueAxis axis, Rectangle2D dataArea, double value) {
        Range range = axis.getRange();
        if (!range.contains(value)) {
            return;
        }
        PlotOrientation orientation = plot.getOrientation();
        double v = axis.valueToJava2D(value, dataArea, plot.getDomainAxisEdge());
        Line2D.Double line = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
        } else if (orientation == PlotOrientation.VERTICAL) {
            line = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
        }
        Paint paint = plot.getDomainGridlinePaint();
        Stroke stroke = plot.getDomainGridlineStroke();
        g2.setPaint(paint != null ? paint : Plot.DEFAULT_OUTLINE_PAINT);
        g2.setStroke(stroke != null ? stroke : Plot.DEFAULT_OUTLINE_STROKE);
        g2.draw(line);
    }

    public void drawDomainLine(Graphics2D g2, XYPlot plot, ValueAxis axis, Rectangle2D dataArea, double value, Paint paint, Stroke stroke) {
        Range range = axis.getRange();
        if (!range.contains(value)) {
            return;
        }
        PlotOrientation orientation = plot.getOrientation();
        Line2D.Double line = null;
        double v = axis.valueToJava2D(value, dataArea, plot.getDomainAxisEdge());
        if (orientation == PlotOrientation.HORIZONTAL) {
            line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
        } else if (orientation == PlotOrientation.VERTICAL) {
            line = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
        }
        g2.setPaint(paint);
        g2.setStroke(stroke);
        g2.draw(line);
    }

    public void drawRangeLine(Graphics2D g2, XYPlot plot, ValueAxis axis, Rectangle2D dataArea, double value, Paint paint, Stroke stroke) {
        Range range = axis.getRange();
        if (!range.contains(value)) {
            return;
        }
        PlotOrientation orientation = plot.getOrientation();
        Line2D.Double line = null;
        double v = axis.valueToJava2D(value, dataArea, plot.getRangeAxisEdge());
        if (orientation == PlotOrientation.HORIZONTAL) {
            line = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
        } else if (orientation == PlotOrientation.VERTICAL) {
            line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
        }
        g2.setPaint(paint);
        g2.setStroke(stroke);
        g2.draw(line);
    }

    public void drawDomainMarker(Graphics2D g2, XYPlot plot, ValueAxis domainAxis, Marker marker, Rectangle2D dataArea) {
        if (marker instanceof ValueMarker) {
            ValueMarker vm = (ValueMarker)marker;
            double value = vm.getValue();
            Range range = domainAxis.getRange();
            if (!range.contains(value)) {
                return;
            }
            double v = domainAxis.valueToJava2D(value, dataArea, plot.getDomainAxisEdge());
            PlotOrientation orientation = plot.getOrientation();
            Line2D.Double line = null;
            if (orientation == PlotOrientation.HORIZONTAL) {
                line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
            } else if (orientation == PlotOrientation.VERTICAL) {
                line = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
            }
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(3, marker.getAlpha()));
            g2.setPaint(marker.getPaint());
            g2.setStroke(marker.getStroke());
            g2.draw(line);
            String label = marker.getLabel();
            RectangleAnchor anchor = marker.getLabelAnchor();
            if (label != null) {
                Font labelFont = marker.getLabelFont();
                g2.setFont(labelFont);
                g2.setPaint(marker.getLabelPaint());
                Point2D coordinates = this.calculateDomainMarkerTextAnchorPoint(g2, orientation, dataArea, line.getBounds2D(), marker.getLabelOffset(), LengthAdjustmentType.EXPAND, anchor);
                TextUtilities.drawAlignedString(label, g2, (float)coordinates.getX(), (float)coordinates.getY(), marker.getLabelTextAnchor());
            }
            g2.setComposite(originalComposite);
        } else if (marker instanceof IntervalMarker) {
            IntervalMarker im = (IntervalMarker)marker;
            double start = im.getStartValue();
            double end = im.getEndValue();
            Range range = domainAxis.getRange();
            if (!range.intersects(start, end)) {
                return;
            }
            double start2d = domainAxis.valueToJava2D(start, dataArea, plot.getDomainAxisEdge());
            double end2d = domainAxis.valueToJava2D(end, dataArea, plot.getDomainAxisEdge());
            double low = Math.min(start2d, end2d);
            double high = Math.max(start2d, end2d);
            PlotOrientation orientation = plot.getOrientation();
            Rectangle2D.Double rect = null;
            if (orientation == PlotOrientation.HORIZONTAL) {
                low = Math.max(low, dataArea.getMinY());
                high = Math.min(high, dataArea.getMaxY());
                rect = new Rectangle2D.Double(dataArea.getMinX(), low, dataArea.getWidth(), high - low);
            } else if (orientation == PlotOrientation.VERTICAL) {
                low = Math.max(low, dataArea.getMinX());
                high = Math.min(high, dataArea.getMaxX());
                rect = new Rectangle2D.Double(low, dataArea.getMinY(), high - low, dataArea.getHeight());
            }
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(3, marker.getAlpha()));
            Paint p = marker.getPaint();
            if (p instanceof GradientPaint) {
                GradientPaint gp = (GradientPaint)p;
                GradientPaintTransformer t = im.getGradientPaintTransformer();
                if (t != null) {
                    gp = t.transform(gp, rect);
                }
                g2.setPaint(gp);
            } else {
                g2.setPaint(p);
            }
            g2.fill(rect);
            if (im.getOutlinePaint() != null && im.getOutlineStroke() != null) {
                Line2D.Double line;
                if (orientation == PlotOrientation.VERTICAL) {
                    line = new Line2D.Double();
                    double y0 = dataArea.getMinY();
                    double y1 = dataArea.getMaxY();
                    g2.setPaint(im.getOutlinePaint());
                    g2.setStroke(im.getOutlineStroke());
                    if (range.contains(start)) {
                        ((Line2D)line).setLine(start2d, y0, start2d, y1);
                        g2.draw(line);
                    }
                    if (range.contains(end)) {
                        ((Line2D)line).setLine(end2d, y0, end2d, y1);
                        g2.draw(line);
                    }
                } else {
                    line = new Line2D.Double();
                    double x0 = dataArea.getMinX();
                    double x1 = dataArea.getMaxX();
                    g2.setPaint(im.getOutlinePaint());
                    g2.setStroke(im.getOutlineStroke());
                    if (range.contains(start)) {
                        ((Line2D)line).setLine(x0, start2d, x1, start2d);
                        g2.draw(line);
                    }
                    if (range.contains(end)) {
                        ((Line2D)line).setLine(x0, end2d, x1, end2d);
                        g2.draw(line);
                    }
                }
            }
            String label = marker.getLabel();
            RectangleAnchor anchor = marker.getLabelAnchor();
            if (label != null) {
                Font labelFont = marker.getLabelFont();
                g2.setFont(labelFont);
                g2.setPaint(marker.getLabelPaint());
                Point2D coordinates = this.calculateDomainMarkerTextAnchorPoint(g2, orientation, dataArea, rect, marker.getLabelOffset(), marker.getLabelOffsetType(), anchor);
                TextUtilities.drawAlignedString(label, g2, (float)coordinates.getX(), (float)coordinates.getY(), marker.getLabelTextAnchor());
            }
            g2.setComposite(originalComposite);
        }
    }

    protected Point2D calculateDomainMarkerTextAnchorPoint(Graphics2D g2, PlotOrientation orientation, Rectangle2D dataArea, Rectangle2D markerArea, RectangleInsets markerOffset, LengthAdjustmentType labelOffsetType, RectangleAnchor anchor) {
        Rectangle2D anchorRect = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            anchorRect = markerOffset.createAdjustedRectangle(markerArea, LengthAdjustmentType.CONTRACT, labelOffsetType);
        } else if (orientation == PlotOrientation.VERTICAL) {
            anchorRect = markerOffset.createAdjustedRectangle(markerArea, labelOffsetType, LengthAdjustmentType.CONTRACT);
        }
        return RectangleAnchor.coordinates(anchorRect, anchor);
    }

    public void drawRangeMarker(Graphics2D g2, XYPlot plot, ValueAxis rangeAxis, Marker marker, Rectangle2D dataArea) {
        if (marker instanceof ValueMarker) {
            ValueMarker vm = (ValueMarker)marker;
            double value = vm.getValue();
            Range range = rangeAxis.getRange();
            if (!range.contains(value)) {
                return;
            }
            double v = rangeAxis.valueToJava2D(value, dataArea, plot.getRangeAxisEdge());
            PlotOrientation orientation = plot.getOrientation();
            Line2D.Double line = null;
            if (orientation == PlotOrientation.HORIZONTAL) {
                line = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
            } else if (orientation == PlotOrientation.VERTICAL) {
                line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
            }
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(3, marker.getAlpha()));
            g2.setPaint(marker.getPaint());
            g2.setStroke(marker.getStroke());
            g2.draw(line);
            String label = marker.getLabel();
            RectangleAnchor anchor = marker.getLabelAnchor();
            if (label != null) {
                Font labelFont = marker.getLabelFont();
                g2.setFont(labelFont);
                g2.setPaint(marker.getLabelPaint());
                Point2D coordinates = this.calculateRangeMarkerTextAnchorPoint(g2, orientation, dataArea, line.getBounds2D(), marker.getLabelOffset(), LengthAdjustmentType.EXPAND, anchor);
                TextUtilities.drawAlignedString(label, g2, (float)coordinates.getX(), (float)coordinates.getY(), marker.getLabelTextAnchor());
            }
            g2.setComposite(originalComposite);
        } else if (marker instanceof IntervalMarker) {
            IntervalMarker im = (IntervalMarker)marker;
            double start = im.getStartValue();
            double end = im.getEndValue();
            Range range = rangeAxis.getRange();
            if (!range.intersects(start, end)) {
                return;
            }
            double start2d = rangeAxis.valueToJava2D(start, dataArea, plot.getRangeAxisEdge());
            double end2d = rangeAxis.valueToJava2D(end, dataArea, plot.getRangeAxisEdge());
            double low = Math.min(start2d, end2d);
            double high = Math.max(start2d, end2d);
            PlotOrientation orientation = plot.getOrientation();
            Rectangle2D.Double rect = null;
            if (orientation == PlotOrientation.HORIZONTAL) {
                low = Math.max(low, dataArea.getMinX());
                high = Math.min(high, dataArea.getMaxX());
                rect = new Rectangle2D.Double(low, dataArea.getMinY(), high - low, dataArea.getHeight());
            } else if (orientation == PlotOrientation.VERTICAL) {
                low = Math.max(low, dataArea.getMinY());
                high = Math.min(high, dataArea.getMaxY());
                rect = new Rectangle2D.Double(dataArea.getMinX(), low, dataArea.getWidth(), high - low);
            }
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(3, marker.getAlpha()));
            Paint p = marker.getPaint();
            if (p instanceof GradientPaint) {
                GradientPaint gp = (GradientPaint)p;
                GradientPaintTransformer t = im.getGradientPaintTransformer();
                if (t != null) {
                    gp = t.transform(gp, rect);
                }
                g2.setPaint(gp);
            } else {
                g2.setPaint(p);
            }
            g2.fill(rect);
            if (im.getOutlinePaint() != null && im.getOutlineStroke() != null) {
                Line2D.Double line;
                if (orientation == PlotOrientation.VERTICAL) {
                    line = new Line2D.Double();
                    double x0 = dataArea.getMinX();
                    double x1 = dataArea.getMaxX();
                    g2.setPaint(im.getOutlinePaint());
                    g2.setStroke(im.getOutlineStroke());
                    if (range.contains(start)) {
                        ((Line2D)line).setLine(x0, start2d, x1, start2d);
                        g2.draw(line);
                    }
                    if (range.contains(end)) {
                        ((Line2D)line).setLine(x0, end2d, x1, end2d);
                        g2.draw(line);
                    }
                } else {
                    line = new Line2D.Double();
                    double y0 = dataArea.getMinY();
                    double y1 = dataArea.getMaxY();
                    g2.setPaint(im.getOutlinePaint());
                    g2.setStroke(im.getOutlineStroke());
                    if (range.contains(start)) {
                        ((Line2D)line).setLine(start2d, y0, start2d, y1);
                        g2.draw(line);
                    }
                    if (range.contains(end)) {
                        ((Line2D)line).setLine(end2d, y0, end2d, y1);
                        g2.draw(line);
                    }
                }
            }
            String label = marker.getLabel();
            RectangleAnchor anchor = marker.getLabelAnchor();
            if (label != null) {
                Font labelFont = marker.getLabelFont();
                g2.setFont(labelFont);
                g2.setPaint(marker.getLabelPaint());
                Point2D coordinates = this.calculateRangeMarkerTextAnchorPoint(g2, orientation, dataArea, rect, marker.getLabelOffset(), marker.getLabelOffsetType(), anchor);
                TextUtilities.drawAlignedString(label, g2, (float)coordinates.getX(), (float)coordinates.getY(), marker.getLabelTextAnchor());
            }
            g2.setComposite(originalComposite);
        }
    }

    private Point2D calculateRangeMarkerTextAnchorPoint(Graphics2D g2, PlotOrientation orientation, Rectangle2D dataArea, Rectangle2D markerArea, RectangleInsets markerOffset, LengthAdjustmentType labelOffsetForRange, RectangleAnchor anchor) {
        Rectangle2D anchorRect = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            anchorRect = markerOffset.createAdjustedRectangle(markerArea, labelOffsetForRange, LengthAdjustmentType.CONTRACT);
        } else if (orientation == PlotOrientation.VERTICAL) {
            anchorRect = markerOffset.createAdjustedRectangle(markerArea, LengthAdjustmentType.CONTRACT, labelOffsetForRange);
        }
        return RectangleAnchor.coordinates(anchorRect, anchor);
    }

    protected Object clone() throws CloneNotSupportedException {
        PublicCloneable pc;
        AbstractXYItemRenderer clone = (AbstractXYItemRenderer)super.clone();
        if (this.itemLabelGenerator != null && this.itemLabelGenerator instanceof PublicCloneable) {
            pc = (PublicCloneable)((Object)this.itemLabelGenerator);
            clone.itemLabelGenerator = (XYItemLabelGenerator)pc.clone();
        }
        clone.itemLabelGeneratorList = (ObjectList)this.itemLabelGeneratorList.clone();
        if (this.baseItemLabelGenerator != null && this.baseItemLabelGenerator instanceof PublicCloneable) {
            pc = (PublicCloneable)((Object)this.baseItemLabelGenerator);
            clone.baseItemLabelGenerator = (XYItemLabelGenerator)pc.clone();
        }
        if (this.toolTipGenerator != null && this.toolTipGenerator instanceof PublicCloneable) {
            pc = (PublicCloneable)((Object)this.toolTipGenerator);
            clone.toolTipGenerator = (XYToolTipGenerator)pc.clone();
        }
        clone.toolTipGeneratorList = (ObjectList)this.toolTipGeneratorList.clone();
        if (this.baseToolTipGenerator != null && this.baseToolTipGenerator instanceof PublicCloneable) {
            pc = (PublicCloneable)((Object)this.baseToolTipGenerator);
            clone.baseToolTipGenerator = (XYToolTipGenerator)pc.clone();
        }
        if (clone.legendItemLabelGenerator instanceof PublicCloneable) {
            clone.legendItemLabelGenerator = (XYSeriesLabelGenerator)ObjectUtilities.clone(this.legendItemLabelGenerator);
        }
        if (clone.legendItemToolTipGenerator instanceof PublicCloneable) {
            clone.legendItemToolTipGenerator = (XYSeriesLabelGenerator)ObjectUtilities.clone(this.legendItemToolTipGenerator);
        }
        if (clone.legendItemURLGenerator instanceof PublicCloneable) {
            clone.legendItemURLGenerator = (XYSeriesLabelGenerator)ObjectUtilities.clone(this.legendItemURLGenerator);
        }
        clone.foregroundAnnotations = (List)ObjectUtilities.deepClone(this.foregroundAnnotations);
        clone.backgroundAnnotations = (List)ObjectUtilities.deepClone(this.backgroundAnnotations);
        if (clone.legendItemLabelGenerator instanceof PublicCloneable) {
            clone.legendItemLabelGenerator = (XYSeriesLabelGenerator)ObjectUtilities.clone(this.legendItemLabelGenerator);
        }
        if (clone.legendItemToolTipGenerator instanceof PublicCloneable) {
            clone.legendItemToolTipGenerator = (XYSeriesLabelGenerator)ObjectUtilities.clone(this.legendItemToolTipGenerator);
        }
        if (clone.legendItemURLGenerator instanceof PublicCloneable) {
            clone.legendItemURLGenerator = (XYSeriesLabelGenerator)ObjectUtilities.clone(this.legendItemURLGenerator);
        }
        return clone;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AbstractXYItemRenderer)) {
            return false;
        }
        AbstractXYItemRenderer that = (AbstractXYItemRenderer)obj;
        if (!ObjectUtilities.equal(this.itemLabelGenerator, that.itemLabelGenerator)) {
            return false;
        }
        if (!this.itemLabelGeneratorList.equals(that.itemLabelGeneratorList)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.baseItemLabelGenerator, that.baseItemLabelGenerator)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.toolTipGenerator, that.toolTipGenerator)) {
            return false;
        }
        if (!this.toolTipGeneratorList.equals(that.toolTipGeneratorList)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.baseToolTipGenerator, that.baseToolTipGenerator)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.urlGenerator, that.urlGenerator)) {
            return false;
        }
        if (!((Object)this.foregroundAnnotations).equals(that.foregroundAnnotations)) {
            return false;
        }
        if (!((Object)this.backgroundAnnotations).equals(that.backgroundAnnotations)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.legendItemLabelGenerator, that.legendItemLabelGenerator)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.legendItemToolTipGenerator, that.legendItemToolTipGenerator)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.legendItemURLGenerator, that.legendItemURLGenerator)) {
            return false;
        }
        return super.equals(obj);
    }

    public DrawingSupplier getDrawingSupplier() {
        DrawingSupplier result = null;
        XYPlot p = this.getPlot();
        if (p != null) {
            result = p.getDrawingSupplier();
        }
        return result;
    }

    protected void updateCrosshairValues(CrosshairState crosshairState, double x, double y, int domainAxisIndex, int rangeAxisIndex, double transX, double transY, PlotOrientation orientation) {
        if (orientation == null) {
            throw new IllegalArgumentException("Null 'orientation' argument.");
        }
        if (crosshairState != null) {
            if (this.plot.isDomainCrosshairLockedOnData()) {
                if (this.plot.isRangeCrosshairLockedOnData()) {
                    crosshairState.updateCrosshairPoint(x, y, domainAxisIndex, rangeAxisIndex, transX, transY, orientation);
                } else {
                    crosshairState.updateCrosshairX(x, domainAxisIndex);
                }
            } else if (this.plot.isRangeCrosshairLockedOnData()) {
                crosshairState.updateCrosshairY(y, rangeAxisIndex);
            }
        }
    }

    protected void drawItemLabel(Graphics2D g2, PlotOrientation orientation, XYDataset dataset, int series, int item, double x, double y, boolean negative) {
        XYItemLabelGenerator generator = this.getItemLabelGenerator(series, item);
        if (generator != null) {
            Font labelFont = this.getItemLabelFont(series, item);
            Paint paint = this.getItemLabelPaint(series, item);
            g2.setFont(labelFont);
            g2.setPaint(paint);
            String label = generator.generateLabel(dataset, series, item);
            ItemLabelPosition position = null;
            position = !negative ? this.getPositiveItemLabelPosition(series, item) : this.getNegativeItemLabelPosition(series, item);
            Point2D anchorPoint = this.calculateLabelAnchorPoint(position.getItemLabelAnchor(), x, y, orientation);
            TextUtilities.drawRotatedString(label, g2, (float)anchorPoint.getX(), (float)anchorPoint.getY(), position.getTextAnchor(), position.getAngle(), position.getRotationAnchor());
        }
    }

    public void drawAnnotations(Graphics2D g2, Rectangle2D dataArea, ValueAxis domainAxis, ValueAxis rangeAxis, Layer layer, PlotRenderingInfo info) {
        Iterator iterator = null;
        if (layer.equals(Layer.FOREGROUND)) {
            iterator = this.foregroundAnnotations.iterator();
        } else if (layer.equals(Layer.BACKGROUND)) {
            iterator = this.backgroundAnnotations.iterator();
        } else {
            throw new RuntimeException("Unknown layer.");
        }
        while (iterator.hasNext()) {
            XYAnnotation annotation = (XYAnnotation)iterator.next();
            annotation.draw(g2, this.plot, dataArea, domainAxis, rangeAxis, 0, info);
        }
    }

    protected void addEntity(EntityCollection entities, Shape area, XYDataset dataset, int series, int item, double entityX, double entityY) {
        if (!this.getItemCreateEntity(series, item)) {
            return;
        }
        Shape hotspot = area;
        if (hotspot == null) {
            double r = this.getDefaultEntityRadius();
            double w = r * 2.0;
            hotspot = this.getPlot().getOrientation() == PlotOrientation.VERTICAL ? new Ellipse2D.Double(entityX - r, entityY - r, w, w) : new Ellipse2D.Double(entityY - r, entityX - r, w, w);
        }
        String tip = null;
        XYToolTipGenerator generator = this.getToolTipGenerator(series, item);
        if (generator != null) {
            tip = generator.generateToolTip(dataset, series, item);
        }
        String url = null;
        if (this.getURLGenerator() != null) {
            url = this.getURLGenerator().generateURL(dataset, series, item);
        }
        XYItemEntity entity = new XYItemEntity(hotspot, dataset, series, item, tip, url);
        entities.add(entity);
    }

    public static boolean isPointInRect(Rectangle2D rect, double x, double y) {
        return x >= rect.getMinX() && x <= rect.getMaxX() && y >= rect.getMinY() && y <= rect.getMaxY();
    }

    public XYItemLabelGenerator getItemLabelGenerator() {
        return this.itemLabelGenerator;
    }

    public void setItemLabelGenerator(XYItemLabelGenerator generator) {
        this.itemLabelGenerator = generator;
        this.fireChangeEvent();
    }

    public XYToolTipGenerator getToolTipGenerator() {
        return this.toolTipGenerator;
    }

    public void setToolTipGenerator(XYToolTipGenerator generator) {
        this.toolTipGenerator = generator;
        this.fireChangeEvent();
    }

    protected void updateCrosshairValues(CrosshairState crosshairState, double x, double y, double transX, double transY, PlotOrientation orientation) {
        this.updateCrosshairValues(crosshairState, x, y, 0, 0, transX, transY, orientation);
    }
}

