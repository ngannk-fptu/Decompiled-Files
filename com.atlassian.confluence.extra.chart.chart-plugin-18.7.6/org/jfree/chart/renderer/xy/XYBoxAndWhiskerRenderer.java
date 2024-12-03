/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.renderer.xy;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.BoxAndWhiskerXYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.Outlier;
import org.jfree.chart.renderer.OutlierList;
import org.jfree.chart.renderer.OutlierListCollection;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.Range;
import org.jfree.data.statistics.BoxAndWhiskerXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

public class XYBoxAndWhiskerRenderer
extends AbstractXYItemRenderer
implements XYItemRenderer,
Cloneable,
PublicCloneable,
Serializable {
    private static final long serialVersionUID = -8020170108532232324L;
    private double boxWidth;
    private transient Paint boxPaint;
    private boolean fillBox;
    private transient Paint artifactPaint = Color.black;

    public XYBoxAndWhiskerRenderer() {
        this(-1.0);
    }

    public XYBoxAndWhiskerRenderer(double boxWidth) {
        this.boxWidth = boxWidth;
        this.boxPaint = Color.green;
        this.fillBox = true;
        this.setBaseToolTipGenerator(new BoxAndWhiskerXYToolTipGenerator());
    }

    public double getBoxWidth() {
        return this.boxWidth;
    }

    public void setBoxWidth(double width) {
        if (width != this.boxWidth) {
            this.boxWidth = width;
            this.fireChangeEvent();
        }
    }

    public Paint getBoxPaint() {
        return this.boxPaint;
    }

    public void setBoxPaint(Paint paint) {
        this.boxPaint = paint;
        this.fireChangeEvent();
    }

    public boolean getFillBox() {
        return this.fillBox;
    }

    public void setFillBox(boolean flag) {
        this.fillBox = flag;
        this.fireChangeEvent();
    }

    public Paint getArtifactPaint() {
        return this.artifactPaint;
    }

    public void setArtifactPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.artifactPaint = paint;
        this.fireChangeEvent();
    }

    public Range findRangeBounds(XYDataset dataset) {
        return this.findRangeBounds(dataset, true);
    }

    protected Paint lookupBoxPaint(int series, int item) {
        Paint p = this.getBoxPaint();
        if (p != null) {
            return p;
        }
        return this.getItemPaint(series, item);
    }

    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            this.drawHorizontalItem(g2, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairState, pass);
        } else if (orientation == PlotOrientation.VERTICAL) {
            this.drawVerticalItem(g2, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairState, pass);
        }
    }

    public void drawHorizontalItem(Graphics2D g2, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        double exactBoxWidth;
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getOwner().getEntityCollection();
        }
        BoxAndWhiskerXYDataset boxAndWhiskerData = (BoxAndWhiskerXYDataset)dataset;
        Number x = boxAndWhiskerData.getX(series, item);
        Number yMax = boxAndWhiskerData.getMaxRegularValue(series, item);
        Number yMin = boxAndWhiskerData.getMinRegularValue(series, item);
        Number yMedian = boxAndWhiskerData.getMedianValue(series, item);
        Number yAverage = boxAndWhiskerData.getMeanValue(series, item);
        Number yQ1Median = boxAndWhiskerData.getQ1Value(series, item);
        Number yQ3Median = boxAndWhiskerData.getQ3Value(series, item);
        double xx = domainAxis.valueToJava2D(x.doubleValue(), dataArea, plot.getDomainAxisEdge());
        RectangleEdge location = plot.getRangeAxisEdge();
        double yyMax = rangeAxis.valueToJava2D(yMax.doubleValue(), dataArea, location);
        double yyMin = rangeAxis.valueToJava2D(yMin.doubleValue(), dataArea, location);
        double yyMedian = rangeAxis.valueToJava2D(yMedian.doubleValue(), dataArea, location);
        double yyAverage = 0.0;
        if (yAverage != null) {
            yyAverage = rangeAxis.valueToJava2D(yAverage.doubleValue(), dataArea, location);
        }
        double yyQ1Median = rangeAxis.valueToJava2D(yQ1Median.doubleValue(), dataArea, location);
        double yyQ3Median = rangeAxis.valueToJava2D(yQ3Median.doubleValue(), dataArea, location);
        double width = exactBoxWidth = this.getBoxWidth();
        double dataAreaX = dataArea.getHeight();
        double maxBoxPercent = 0.1;
        double maxBoxWidth = dataAreaX * maxBoxPercent;
        if (exactBoxWidth <= 0.0) {
            int itemCount = boxAndWhiskerData.getItemCount(series);
            exactBoxWidth = dataAreaX / (double)itemCount * 4.5 / 7.0;
            width = exactBoxWidth < 3.0 ? 3.0 : (exactBoxWidth > maxBoxWidth ? maxBoxWidth : exactBoxWidth);
        }
        g2.setPaint(this.getItemPaint(series, item));
        Stroke s = this.getItemStroke(series, item);
        g2.setStroke(s);
        g2.draw(new Line2D.Double(yyMax, xx, yyQ3Median, xx));
        g2.draw(new Line2D.Double(yyMax, xx - width / 2.0, yyMax, xx + width / 2.0));
        g2.draw(new Line2D.Double(yyMin, xx, yyQ1Median, xx));
        g2.draw(new Line2D.Double(yyMin, xx - width / 2.0, yyMin, xx + width / 2.0));
        Rectangle2D.Double box = null;
        box = yyQ1Median < yyQ3Median ? new Rectangle2D.Double(yyQ1Median, xx - width / 2.0, yyQ3Median - yyQ1Median, width) : new Rectangle2D.Double(yyQ3Median, xx - width / 2.0, yyQ1Median - yyQ3Median, width);
        if (this.fillBox) {
            g2.setPaint(this.lookupBoxPaint(series, item));
            g2.fill(box);
        }
        g2.setStroke(this.getItemOutlineStroke(series, item));
        g2.setPaint(this.getItemOutlinePaint(series, item));
        g2.draw(box);
        g2.setPaint(this.getArtifactPaint());
        g2.draw(new Line2D.Double(yyMedian, xx - width / 2.0, yyMedian, xx + width / 2.0));
        if (yAverage != null) {
            double aRadius = width / 4.0;
            if (yyAverage > dataArea.getMinX() - aRadius && yyAverage < dataArea.getMaxX() + aRadius) {
                Ellipse2D.Double avgEllipse = new Ellipse2D.Double(yyAverage - aRadius, xx - aRadius, aRadius * 2.0, aRadius * 2.0);
                g2.fill(avgEllipse);
                g2.draw(avgEllipse);
            }
        }
        if (entities != null && box.intersects(dataArea)) {
            this.addEntity(entities, box, dataset, series, item, yyAverage, xx);
        }
    }

    public void drawVerticalItem(Graphics2D g2, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        double exactBoxWidth;
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getOwner().getEntityCollection();
        }
        BoxAndWhiskerXYDataset boxAndWhiskerData = (BoxAndWhiskerXYDataset)dataset;
        Number x = boxAndWhiskerData.getX(series, item);
        Number yMax = boxAndWhiskerData.getMaxRegularValue(series, item);
        Number yMin = boxAndWhiskerData.getMinRegularValue(series, item);
        Number yMedian = boxAndWhiskerData.getMedianValue(series, item);
        Number yAverage = boxAndWhiskerData.getMeanValue(series, item);
        Number yQ1Median = boxAndWhiskerData.getQ1Value(series, item);
        Number yQ3Median = boxAndWhiskerData.getQ3Value(series, item);
        List yOutliers = boxAndWhiskerData.getOutliers(series, item);
        double xx = domainAxis.valueToJava2D(x.doubleValue(), dataArea, plot.getDomainAxisEdge());
        RectangleEdge location = plot.getRangeAxisEdge();
        double yyMax = rangeAxis.valueToJava2D(yMax.doubleValue(), dataArea, location);
        double yyMin = rangeAxis.valueToJava2D(yMin.doubleValue(), dataArea, location);
        double yyMedian = rangeAxis.valueToJava2D(yMedian.doubleValue(), dataArea, location);
        double yyAverage = 0.0;
        if (yAverage != null) {
            yyAverage = rangeAxis.valueToJava2D(yAverage.doubleValue(), dataArea, location);
        }
        double yyQ1Median = rangeAxis.valueToJava2D(yQ1Median.doubleValue(), dataArea, location);
        double yyQ3Median = rangeAxis.valueToJava2D(yQ3Median.doubleValue(), dataArea, location);
        double width = exactBoxWidth = this.getBoxWidth();
        double dataAreaX = dataArea.getMaxX() - dataArea.getMinX();
        double maxBoxPercent = 0.1;
        double maxBoxWidth = dataAreaX * maxBoxPercent;
        if (exactBoxWidth <= 0.0) {
            int itemCount = boxAndWhiskerData.getItemCount(series);
            exactBoxWidth = dataAreaX / (double)itemCount * 4.5 / 7.0;
            width = exactBoxWidth < 3.0 ? 3.0 : (exactBoxWidth > maxBoxWidth ? maxBoxWidth : exactBoxWidth);
        }
        g2.setPaint(this.getItemPaint(series, item));
        Stroke s = this.getItemStroke(series, item);
        g2.setStroke(s);
        g2.draw(new Line2D.Double(xx, yyMax, xx, yyQ3Median));
        g2.draw(new Line2D.Double(xx - width / 2.0, yyMax, xx + width / 2.0, yyMax));
        g2.draw(new Line2D.Double(xx, yyMin, xx, yyQ1Median));
        g2.draw(new Line2D.Double(xx - width / 2.0, yyMin, xx + width / 2.0, yyMin));
        Rectangle2D.Double box = null;
        box = yyQ1Median > yyQ3Median ? new Rectangle2D.Double(xx - width / 2.0, yyQ3Median, width, yyQ1Median - yyQ3Median) : new Rectangle2D.Double(xx - width / 2.0, yyQ1Median, width, yyQ3Median - yyQ1Median);
        if (this.fillBox) {
            g2.setPaint(this.lookupBoxPaint(series, item));
            g2.fill(box);
        }
        g2.setStroke(this.getItemOutlineStroke(series, item));
        g2.setPaint(this.getItemOutlinePaint(series, item));
        g2.draw(box);
        g2.setPaint(this.getArtifactPaint());
        g2.draw(new Line2D.Double(xx - width / 2.0, yyMedian, xx + width / 2.0, yyMedian));
        double aRadius = 0.0;
        double oRadius = width / 3.0;
        if (yAverage != null) {
            aRadius = width / 4.0;
            if (yyAverage > dataArea.getMinY() - aRadius && yyAverage < dataArea.getMaxY() + aRadius) {
                Ellipse2D.Double avgEllipse = new Ellipse2D.Double(xx - aRadius, yyAverage - aRadius, aRadius * 2.0, aRadius * 2.0);
                g2.fill(avgEllipse);
                g2.draw(avgEllipse);
            }
        }
        ArrayList<Outlier> outliers = new ArrayList<Outlier>();
        OutlierListCollection outlierListCollection = new OutlierListCollection();
        for (int i = 0; i < yOutliers.size(); ++i) {
            double yyOutlier;
            double outlier = ((Number)yOutliers.get(i)).doubleValue();
            if (outlier > boxAndWhiskerData.getMaxOutlier(series, item).doubleValue()) {
                outlierListCollection.setHighFarOut(true);
            } else if (outlier < boxAndWhiskerData.getMinOutlier(series, item).doubleValue()) {
                outlierListCollection.setLowFarOut(true);
            } else if (outlier > boxAndWhiskerData.getMaxRegularValue(series, item).doubleValue()) {
                yyOutlier = rangeAxis.valueToJava2D(outlier, dataArea, location);
                outliers.add(new Outlier(xx, yyOutlier, oRadius));
            } else if (outlier < boxAndWhiskerData.getMinRegularValue(series, item).doubleValue()) {
                yyOutlier = rangeAxis.valueToJava2D(outlier, dataArea, location);
                outliers.add(new Outlier(xx, yyOutlier, oRadius));
            }
            Collections.sort(outliers);
        }
        Iterator iterator = outliers.iterator();
        while (iterator.hasNext()) {
            Outlier outlier = (Outlier)iterator.next();
            outlierListCollection.add(outlier);
        }
        double maxAxisValue = rangeAxis.valueToJava2D(rangeAxis.getUpperBound(), dataArea, location) + aRadius;
        double minAxisValue = rangeAxis.valueToJava2D(rangeAxis.getLowerBound(), dataArea, location) - aRadius;
        Iterator iterator2 = outlierListCollection.iterator();
        while (iterator2.hasNext()) {
            OutlierList list = (OutlierList)iterator2.next();
            Outlier outlier = list.getAveragedOutlier();
            Point2D point = outlier.getPoint();
            if (list.isMultiple()) {
                this.drawMultipleEllipse(point, width, oRadius, g2);
                continue;
            }
            this.drawEllipse(point, oRadius, g2);
        }
        if (outlierListCollection.isHighFarOut()) {
            this.drawHighFarOut(aRadius, g2, xx, maxAxisValue);
        }
        if (outlierListCollection.isLowFarOut()) {
            this.drawLowFarOut(aRadius, g2, xx, minAxisValue);
        }
        if (entities != null && box.intersects(dataArea)) {
            this.addEntity(entities, box, dataset, series, item, xx, yyAverage);
        }
    }

    protected void drawEllipse(Point2D point, double oRadius, Graphics2D g2) {
        Ellipse2D.Double dot = new Ellipse2D.Double(point.getX() + oRadius / 2.0, point.getY(), oRadius, oRadius);
        g2.draw(dot);
    }

    protected void drawMultipleEllipse(Point2D point, double boxWidth, double oRadius, Graphics2D g2) {
        Ellipse2D.Double dot1 = new Ellipse2D.Double(point.getX() - boxWidth / 2.0 + oRadius, point.getY(), oRadius, oRadius);
        Ellipse2D.Double dot2 = new Ellipse2D.Double(point.getX() + boxWidth / 2.0, point.getY(), oRadius, oRadius);
        g2.draw(dot1);
        g2.draw(dot2);
    }

    protected void drawHighFarOut(double aRadius, Graphics2D g2, double xx, double m) {
        double side = aRadius * 2.0;
        g2.draw(new Line2D.Double(xx - side, m + side, xx + side, m + side));
        g2.draw(new Line2D.Double(xx - side, m + side, xx, m));
        g2.draw(new Line2D.Double(xx + side, m + side, xx, m));
    }

    protected void drawLowFarOut(double aRadius, Graphics2D g2, double xx, double m) {
        double side = aRadius * 2.0;
        g2.draw(new Line2D.Double(xx - side, m - side, xx + side, m - side));
        g2.draw(new Line2D.Double(xx - side, m - side, xx, m));
        g2.draw(new Line2D.Double(xx + side, m - side, xx, m));
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYBoxAndWhiskerRenderer)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        XYBoxAndWhiskerRenderer that = (XYBoxAndWhiskerRenderer)obj;
        if (this.boxWidth != that.getBoxWidth()) {
            return false;
        }
        if (!PaintUtilities.equal(this.boxPaint, that.boxPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.artifactPaint, that.artifactPaint)) {
            return false;
        }
        return this.fillBox == that.fillBox;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.boxPaint, stream);
        SerialUtilities.writePaint(this.artifactPaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.boxPaint = SerialUtilities.readPaint(stream);
        this.artifactPaint = SerialUtilities.readPaint(stream);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

