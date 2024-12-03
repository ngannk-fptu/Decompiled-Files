/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.time.ohlc;

import org.jfree.data.ComparableObjectItem;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.ohlc.OHLC;

public class OHLCItem
extends ComparableObjectItem {
    public OHLCItem(RegularTimePeriod period, double open, double high, double low, double close) {
        super(period, new OHLC(open, high, low, close));
    }

    public RegularTimePeriod getPeriod() {
        return (RegularTimePeriod)this.getComparable();
    }

    public double getYValue() {
        return this.getCloseValue();
    }

    public double getOpenValue() {
        OHLC ohlc = (OHLC)this.getObject();
        if (ohlc != null) {
            return ohlc.getOpen();
        }
        return Double.NaN;
    }

    public double getHighValue() {
        OHLC ohlc = (OHLC)this.getObject();
        if (ohlc != null) {
            return ohlc.getHigh();
        }
        return Double.NaN;
    }

    public double getLowValue() {
        OHLC ohlc = (OHLC)this.getObject();
        if (ohlc != null) {
            return ohlc.getLow();
        }
        return Double.NaN;
    }

    public double getCloseValue() {
        OHLC ohlc = (OHLC)this.getObject();
        if (ohlc != null) {
            return ohlc.getClose();
        }
        return Double.NaN;
    }
}

