/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.xy;

import org.jfree.data.ComparableObjectItem;
import org.jfree.data.xy.YWithXInterval;

public class XIntervalDataItem
extends ComparableObjectItem {
    public XIntervalDataItem(double x, double xLow, double xHigh, double y) {
        super(new Double(x), new YWithXInterval(y, xLow, xHigh));
    }

    public Number getX() {
        return (Number)((Object)this.getComparable());
    }

    public double getYValue() {
        YWithXInterval interval = (YWithXInterval)this.getObject();
        if (interval != null) {
            return interval.getY();
        }
        return Double.NaN;
    }

    public double getXLowValue() {
        YWithXInterval interval = (YWithXInterval)this.getObject();
        if (interval != null) {
            return interval.getXLow();
        }
        return Double.NaN;
    }

    public double getXHighValue() {
        YWithXInterval interval = (YWithXInterval)this.getObject();
        if (interval != null) {
            return interval.getXHigh();
        }
        return Double.NaN;
    }
}

