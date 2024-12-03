/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.xy;

import org.jfree.data.ComparableObjectItem;
import org.jfree.data.ComparableObjectSeries;
import org.jfree.data.xy.XIntervalDataItem;

public class XIntervalSeries
extends ComparableObjectSeries {
    public XIntervalSeries(Comparable key) {
        this(key, true, true);
    }

    public XIntervalSeries(Comparable key, boolean autoSort, boolean allowDuplicateXValues) {
        super(key, autoSort, allowDuplicateXValues);
    }

    public void add(double x, double xLow, double xHigh, double y) {
        super.add(new XIntervalDataItem(x, xLow, xHigh, y), true);
    }

    public Number getX(int index) {
        XIntervalDataItem item = (XIntervalDataItem)this.getDataItem(index);
        return item.getX();
    }

    public double getXLowValue(int index) {
        XIntervalDataItem item = (XIntervalDataItem)this.getDataItem(index);
        return item.getXLowValue();
    }

    public double getXHighValue(int index) {
        XIntervalDataItem item = (XIntervalDataItem)this.getDataItem(index);
        return item.getXHighValue();
    }

    public double getYValue(int index) {
        XIntervalDataItem item = (XIntervalDataItem)this.getDataItem(index);
        return item.getYValue();
    }

    public ComparableObjectItem getDataItem(int index) {
        return super.getDataItem(index);
    }
}

