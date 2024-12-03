/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.xy;

import java.util.Arrays;
import java.util.Date;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.OHLCDataItem;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.util.PublicCloneable;

public class DefaultOHLCDataset
extends AbstractXYDataset
implements OHLCDataset,
PublicCloneable {
    private Comparable key;
    private OHLCDataItem[] data;

    public DefaultOHLCDataset(Comparable key, OHLCDataItem[] data) {
        this.key = key;
        this.data = data;
    }

    public Comparable getSeriesKey(int series) {
        return this.key;
    }

    public Number getX(int series, int item) {
        return new Long(this.data[item].getDate().getTime());
    }

    public Date getXDate(int series, int item) {
        return this.data[item].getDate();
    }

    public Number getY(int series, int item) {
        return this.getClose(series, item);
    }

    public Number getHigh(int series, int item) {
        return this.data[item].getHigh();
    }

    public double getHighValue(int series, int item) {
        double result = Double.NaN;
        Number high = this.getHigh(series, item);
        if (high != null) {
            result = high.doubleValue();
        }
        return result;
    }

    public Number getLow(int series, int item) {
        return this.data[item].getLow();
    }

    public double getLowValue(int series, int item) {
        double result = Double.NaN;
        Number low = this.getLow(series, item);
        if (low != null) {
            result = low.doubleValue();
        }
        return result;
    }

    public Number getOpen(int series, int item) {
        return this.data[item].getOpen();
    }

    public double getOpenValue(int series, int item) {
        double result = Double.NaN;
        Number open = this.getOpen(series, item);
        if (open != null) {
            result = open.doubleValue();
        }
        return result;
    }

    public Number getClose(int series, int item) {
        return this.data[item].getClose();
    }

    public double getCloseValue(int series, int item) {
        double result = Double.NaN;
        Number close = this.getClose(series, item);
        if (close != null) {
            result = close.doubleValue();
        }
        return result;
    }

    public Number getVolume(int series, int item) {
        return this.data[item].getVolume();
    }

    public double getVolumeValue(int series, int item) {
        double result = Double.NaN;
        Number volume = this.getVolume(series, item);
        if (volume != null) {
            result = volume.doubleValue();
        }
        return result;
    }

    public int getSeriesCount() {
        return 1;
    }

    public int getItemCount(int series) {
        return this.data.length;
    }

    public void sortDataByDate() {
        Arrays.sort(this.data);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DefaultOHLCDataset)) {
            return false;
        }
        DefaultOHLCDataset that = (DefaultOHLCDataset)obj;
        if (!this.key.equals(that.key)) {
            return false;
        }
        return Arrays.equals(this.data, that.data);
    }

    public Object clone() throws CloneNotSupportedException {
        DefaultOHLCDataset clone = (DefaultOHLCDataset)super.clone();
        clone.data = new OHLCDataItem[this.data.length];
        System.arraycopy(this.data, 0, clone.data, 0, this.data.length);
        return clone;
    }
}

