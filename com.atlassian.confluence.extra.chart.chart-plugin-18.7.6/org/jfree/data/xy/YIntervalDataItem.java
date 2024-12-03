/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.xy;

import org.jfree.data.ComparableObjectItem;
import org.jfree.data.xy.YInterval;

public class YIntervalDataItem
extends ComparableObjectItem {
    public YIntervalDataItem(double x, double y, double yLow, double yHigh) {
        super(new Double(x), new YInterval(y, yLow, yHigh));
    }

    public Double getX() {
        return (Double)this.getComparable();
    }

    public double getYValue() {
        YInterval interval = (YInterval)this.getObject();
        if (interval != null) {
            return interval.getY();
        }
        return Double.NaN;
    }

    public double getYLowValue() {
        YInterval interval = (YInterval)this.getObject();
        if (interval != null) {
            return interval.getYLow();
        }
        return Double.NaN;
    }

    public double getYHighValue() {
        YInterval interval = (YInterval)this.getObject();
        if (interval != null) {
            return interval.getYHigh();
        }
        return Double.NaN;
    }
}

