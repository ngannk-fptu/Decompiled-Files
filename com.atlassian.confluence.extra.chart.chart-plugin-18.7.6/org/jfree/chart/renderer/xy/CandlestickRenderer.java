/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.renderer.xy;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.HighLowItemLabelGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.Range;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

public class CandlestickRenderer
extends AbstractXYItemRenderer
implements XYItemRenderer,
Cloneable,
PublicCloneable,
Serializable {
    private static final long serialVersionUID = 50390395841817121L;
    public static final int WIDTHMETHOD_AVERAGE = 0;
    public static final int WIDTHMETHOD_SMALLEST = 1;
    public static final int WIDTHMETHOD_INTERVALDATA = 2;
    private int autoWidthMethod = 0;
    private double autoWidthFactor = 0.6428571428571429;
    private double autoWidthGap = 0.0;
    private double candleWidth;
    private double maxCandleWidthInMilliseconds = 7.2E7;
    private double maxCandleWidth;
    private transient Paint upPaint;
    private transient Paint downPaint;
    private boolean drawVolume;
    private transient Paint volumePaint;
    private transient double maxVolume;
    private boolean useOutlinePaint;

    public CandlestickRenderer() {
        this(-1.0);
    }

    public CandlestickRenderer(double candleWidth) {
        this(candleWidth, true, new HighLowItemLabelGenerator());
    }

    public CandlestickRenderer(double candleWidth, boolean drawVolume, XYToolTipGenerator toolTipGenerator) {
        this.setBaseToolTipGenerator(toolTipGenerator);
        this.candleWidth = candleWidth;
        this.drawVolume = drawVolume;
        this.volumePaint = Color.gray;
        this.upPaint = Color.green;
        this.downPaint = Color.red;
        this.useOutlinePaint = false;
    }

    public double getCandleWidth() {
        return this.candleWidth;
    }

    public void setCandleWidth(double width) {
        if (width != this.candleWidth) {
            this.candleWidth = width;
            this.fireChangeEvent();
        }
    }

    public double getMaxCandleWidthInMilliseconds() {
        return this.maxCandleWidthInMilliseconds;
    }

    public void setMaxCandleWidthInMilliseconds(double millis) {
        this.maxCandleWidthInMilliseconds = millis;
        this.fireChangeEvent();
    }

    public int getAutoWidthMethod() {
        return this.autoWidthMethod;
    }

    public void setAutoWidthMethod(int autoWidthMethod) {
        if (this.autoWidthMethod != autoWidthMethod) {
            this.autoWidthMethod = autoWidthMethod;
            this.fireChangeEvent();
        }
    }

    public double getAutoWidthFactor() {
        return this.autoWidthFactor;
    }

    public void setAutoWidthFactor(double autoWidthFactor) {
        if (this.autoWidthFactor != autoWidthFactor) {
            this.autoWidthFactor = autoWidthFactor;
            this.fireChangeEvent();
        }
    }

    public double getAutoWidthGap() {
        return this.autoWidthGap;
    }

    public void setAutoWidthGap(double autoWidthGap) {
        if (this.autoWidthGap != autoWidthGap) {
            this.autoWidthGap = autoWidthGap;
            this.fireChangeEvent();
        }
    }

    public Paint getUpPaint() {
        return this.upPaint;
    }

    public void setUpPaint(Paint paint) {
        this.upPaint = paint;
        this.fireChangeEvent();
    }

    public Paint getDownPaint() {
        return this.downPaint;
    }

    public void setDownPaint(Paint paint) {
        this.downPaint = paint;
        this.fireChangeEvent();
    }

    public boolean getDrawVolume() {
        return this.drawVolume;
    }

    public void setDrawVolume(boolean flag) {
        if (this.drawVolume != flag) {
            this.drawVolume = flag;
            this.fireChangeEvent();
        }
    }

    public Paint getVolumePaint() {
        return this.volumePaint;
    }

    public void setVolumePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.volumePaint = paint;
        this.fireChangeEvent();
    }

    public boolean getUseOutlinePaint() {
        return this.useOutlinePaint;
    }

    public void setUseOutlinePaint(boolean use) {
        if (this.useOutlinePaint != use) {
            this.useOutlinePaint = use;
            this.fireChangeEvent();
        }
    }

    public Range findRangeBounds(XYDataset dataset) {
        return this.findRangeBounds(dataset, true);
    }

    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset dataset, PlotRenderingInfo info) {
        ValueAxis axis = plot.getDomainAxis();
        double x1 = axis.getLowerBound();
        double x2 = x1 + this.maxCandleWidthInMilliseconds;
        RectangleEdge edge = plot.getDomainAxisEdge();
        double xx1 = axis.valueToJava2D(x1, dataArea, edge);
        double xx2 = axis.valueToJava2D(x2, dataArea, edge);
        this.maxCandleWidth = Math.abs(xx2 - xx1);
        if (this.drawVolume) {
            OHLCDataset highLowDataset = (OHLCDataset)dataset;
            this.maxVolume = 0.0;
            for (int series = 0; series < highLowDataset.getSeriesCount(); ++series) {
                for (int item = 0; item < highLowDataset.getItemCount(series); ++item) {
                    double volume = highLowDataset.getVolumeValue(series, item);
                    if (!(volume > this.maxVolume)) continue;
                    this.maxVolume = volume;
                }
            }
        }
        return new XYItemRendererState(info);
    }

    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        double stickWidth;
        double volumeWidth;
        boolean horiz;
        PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            horiz = true;
        } else if (orientation == PlotOrientation.VERTICAL) {
            horiz = false;
        } else {
            return;
        }
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getOwner().getEntityCollection();
        }
        OHLCDataset highLowData = (OHLCDataset)dataset;
        double x = highLowData.getXValue(series, item);
        double yHigh = highLowData.getHighValue(series, item);
        double yLow = highLowData.getLowValue(series, item);
        double yOpen = highLowData.getOpenValue(series, item);
        double yClose = highLowData.getCloseValue(series, item);
        RectangleEdge domainEdge = plot.getDomainAxisEdge();
        double xx = domainAxis.valueToJava2D(x, dataArea, domainEdge);
        RectangleEdge edge = plot.getRangeAxisEdge();
        double yyHigh = rangeAxis.valueToJava2D(yHigh, dataArea, edge);
        double yyLow = rangeAxis.valueToJava2D(yLow, dataArea, edge);
        double yyOpen = rangeAxis.valueToJava2D(yOpen, dataArea, edge);
        double yyClose = rangeAxis.valueToJava2D(yClose, dataArea, edge);
        if (this.candleWidth > 0.0) {
            volumeWidth = this.candleWidth;
            stickWidth = this.candleWidth;
        } else {
            double xxWidth = 0.0;
            switch (this.autoWidthMethod) {
                case 0: {
                    int itemCount = highLowData.getItemCount(series);
                    if (horiz) {
                        xxWidth = dataArea.getHeight() / (double)itemCount;
                        break;
                    }
                    xxWidth = dataArea.getWidth() / (double)itemCount;
                    break;
                }
                case 1: {
                    int itemCount = highLowData.getItemCount(series);
                    double lastPos = -1.0;
                    xxWidth = dataArea.getWidth();
                    for (int i = 0; i < itemCount; ++i) {
                        double pos = domainAxis.valueToJava2D(highLowData.getXValue(series, i), dataArea, domainEdge);
                        if (lastPos != -1.0) {
                            xxWidth = Math.min(xxWidth, Math.abs(pos - lastPos));
                        }
                        lastPos = pos;
                    }
                    break;
                }
                case 2: {
                    IntervalXYDataset intervalXYData = (IntervalXYDataset)dataset;
                    double startPos = domainAxis.valueToJava2D(intervalXYData.getStartXValue(series, item), dataArea, plot.getDomainAxisEdge());
                    double endPos = domainAxis.valueToJava2D(intervalXYData.getEndXValue(series, item), dataArea, plot.getDomainAxisEdge());
                    xxWidth = Math.abs(endPos - startPos);
                }
            }
            xxWidth -= 2.0 * this.autoWidthGap;
            xxWidth *= this.autoWidthFactor;
            xxWidth = Math.min(xxWidth, this.maxCandleWidth);
            volumeWidth = Math.max(Math.min(1.0, this.maxCandleWidth), xxWidth);
            stickWidth = Math.max(Math.min(3.0, this.maxCandleWidth), xxWidth);
        }
        Paint p = this.getItemPaint(series, item);
        Paint outlinePaint = null;
        if (this.useOutlinePaint) {
            outlinePaint = this.getItemOutlinePaint(series, item);
        }
        Stroke s = this.getItemStroke(series, item);
        g2.setStroke(s);
        if (this.drawVolume) {
            double max;
            double min;
            int volume = (int)highLowData.getVolumeValue(series, item);
            double volumeHeight = (double)volume / this.maxVolume;
            if (horiz) {
                min = dataArea.getMinX();
                max = dataArea.getMaxX();
            } else {
                min = dataArea.getMinY();
                max = dataArea.getMaxY();
            }
            double zzVolume = volumeHeight * (max - min);
            g2.setPaint(this.getVolumePaint());
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(3, 0.3f));
            if (horiz) {
                g2.fill(new Rectangle2D.Double(min, xx - volumeWidth / 2.0, zzVolume, volumeWidth));
            } else {
                g2.fill(new Rectangle2D.Double(xx - volumeWidth / 2.0, max - zzVolume, volumeWidth, zzVolume));
            }
            g2.setComposite(originalComposite);
        }
        if (this.useOutlinePaint) {
            g2.setPaint(outlinePaint);
        } else {
            g2.setPaint(p);
        }
        double yyMaxOpenClose = Math.max(yyOpen, yyClose);
        double yyMinOpenClose = Math.min(yyOpen, yyClose);
        double maxOpenClose = Math.max(yOpen, yClose);
        double minOpenClose = Math.min(yOpen, yClose);
        if (yHigh > maxOpenClose) {
            if (horiz) {
                g2.draw(new Line2D.Double(yyHigh, xx, yyMaxOpenClose, xx));
            } else {
                g2.draw(new Line2D.Double(xx, yyHigh, xx, yyMaxOpenClose));
            }
        }
        if (yLow < minOpenClose) {
            if (horiz) {
                g2.draw(new Line2D.Double(yyLow, xx, yyMinOpenClose, xx));
            } else {
                g2.draw(new Line2D.Double(xx, yyLow, xx, yyMinOpenClose));
            }
        }
        Rectangle2D.Double body = null;
        Rectangle2D.Double hotspot = null;
        double length = Math.abs(yyHigh - yyLow);
        double base = Math.min(yyHigh, yyLow);
        if (horiz) {
            body = new Rectangle2D.Double(yyMinOpenClose, xx - stickWidth / 2.0, yyMaxOpenClose - yyMinOpenClose, stickWidth);
            hotspot = new Rectangle2D.Double(base, xx - stickWidth / 2.0, length, stickWidth);
        } else {
            body = new Rectangle2D.Double(xx - stickWidth / 2.0, yyMinOpenClose, stickWidth, yyMaxOpenClose - yyMinOpenClose);
            hotspot = new Rectangle2D.Double(xx - stickWidth / 2.0, base, stickWidth, length);
        }
        if (yClose > yOpen) {
            if (this.upPaint != null) {
                g2.setPaint(this.upPaint);
            } else {
                g2.setPaint(p);
            }
            g2.fill(body);
        } else {
            if (this.downPaint != null) {
                g2.setPaint(this.downPaint);
            } else {
                g2.setPaint(p);
            }
            g2.fill(body);
        }
        if (this.useOutlinePaint) {
            g2.setPaint(outlinePaint);
        } else {
            g2.setPaint(p);
        }
        g2.draw(body);
        if (entities != null) {
            this.addEntity(entities, hotspot, dataset, series, item, 0.0, 0.0);
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CandlestickRenderer)) {
            return false;
        }
        CandlestickRenderer that = (CandlestickRenderer)obj;
        if (this.candleWidth != that.candleWidth) {
            return false;
        }
        if (!PaintUtilities.equal(this.upPaint, that.upPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.downPaint, that.downPaint)) {
            return false;
        }
        if (this.drawVolume != that.drawVolume) {
            return false;
        }
        if (this.maxCandleWidthInMilliseconds != that.maxCandleWidthInMilliseconds) {
            return false;
        }
        if (this.autoWidthMethod != that.autoWidthMethod) {
            return false;
        }
        if (this.autoWidthFactor != that.autoWidthFactor) {
            return false;
        }
        if (this.autoWidthGap != that.autoWidthGap) {
            return false;
        }
        if (this.useOutlinePaint != that.useOutlinePaint) {
            return false;
        }
        if (!PaintUtilities.equal(this.volumePaint, that.volumePaint)) {
            return false;
        }
        return super.equals(obj);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.upPaint, stream);
        SerialUtilities.writePaint(this.downPaint, stream);
        SerialUtilities.writePaint(this.volumePaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.upPaint = SerialUtilities.readPaint(stream);
        this.downPaint = SerialUtilities.readPaint(stream);
        this.volumePaint = SerialUtilities.readPaint(stream);
    }

    public boolean drawVolume() {
        return this.drawVolume;
    }
}

