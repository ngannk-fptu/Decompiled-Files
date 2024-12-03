/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.xy;

import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.util.PublicCloneable;

public class XYBarDataset
extends AbstractIntervalXYDataset
implements IntervalXYDataset,
DatasetChangeListener,
PublicCloneable {
    private XYDataset underlying;
    private double barWidth;

    public XYBarDataset(XYDataset underlying, double barWidth) {
        this.underlying = underlying;
        this.underlying.addChangeListener(this);
        this.barWidth = barWidth;
    }

    public XYDataset getUnderlyingDataset() {
        return this.underlying;
    }

    public double getBarWidth() {
        return this.barWidth;
    }

    public void setBarWidth(double barWidth) {
        this.barWidth = barWidth;
        this.notifyListeners(new DatasetChangeEvent(this, this));
    }

    public int getSeriesCount() {
        return this.underlying.getSeriesCount();
    }

    public Comparable getSeriesKey(int series) {
        return this.underlying.getSeriesKey(series);
    }

    public int getItemCount(int series) {
        return this.underlying.getItemCount(series);
    }

    public Number getX(int series, int item) {
        return this.underlying.getX(series, item);
    }

    public double getXValue(int series, int item) {
        return this.underlying.getXValue(series, item);
    }

    public Number getY(int series, int item) {
        return this.underlying.getY(series, item);
    }

    public double getYValue(int series, int item) {
        return this.underlying.getYValue(series, item);
    }

    public Number getStartX(int series, int item) {
        Double result = null;
        Number xnum = this.underlying.getX(series, item);
        if (xnum != null) {
            result = new Double(xnum.doubleValue() - this.barWidth / 2.0);
        }
        return result;
    }

    public double getStartXValue(int series, int item) {
        return this.getXValue(series, item) - this.barWidth / 2.0;
    }

    public Number getEndX(int series, int item) {
        Double result = null;
        Number xnum = this.underlying.getX(series, item);
        if (xnum != null) {
            result = new Double(xnum.doubleValue() + this.barWidth / 2.0);
        }
        return result;
    }

    public double getEndXValue(int series, int item) {
        return this.getXValue(series, item) + this.barWidth / 2.0;
    }

    public Number getStartY(int series, int item) {
        return this.underlying.getY(series, item);
    }

    public double getStartYValue(int series, int item) {
        return this.getYValue(series, item);
    }

    public Number getEndY(int series, int item) {
        return this.underlying.getY(series, item);
    }

    public double getEndYValue(int series, int item) {
        return this.getYValue(series, item);
    }

    public void datasetChanged(DatasetChangeEvent event) {
        this.notifyListeners(event);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYBarDataset)) {
            return false;
        }
        XYBarDataset that = (XYBarDataset)obj;
        if (!this.underlying.equals(that.underlying)) {
            return false;
        }
        return this.barWidth == that.barWidth;
    }

    public Object clone() throws CloneNotSupportedException {
        XYBarDataset clone = (XYBarDataset)super.clone();
        if (this.underlying instanceof PublicCloneable) {
            PublicCloneable pc = (PublicCloneable)((Object)this.underlying);
            clone.underlying = (XYDataset)pc.clone();
        }
        return clone;
    }
}

