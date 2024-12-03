/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.xy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jfree.data.DomainOrder;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.xy.AbstractXYZDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.util.PublicCloneable;

public class DefaultXYZDataset
extends AbstractXYZDataset
implements XYZDataset,
PublicCloneable {
    private List seriesKeys = new ArrayList();
    private List seriesList = new ArrayList();

    public int getSeriesCount() {
        return this.seriesList.size();
    }

    public Comparable getSeriesKey(int series) {
        if (series < 0 || series >= this.getSeriesCount()) {
            throw new IllegalArgumentException("Series index out of bounds");
        }
        return (Comparable)this.seriesKeys.get(series);
    }

    public int indexOf(Comparable seriesKey) {
        return this.seriesKeys.indexOf(seriesKey);
    }

    public DomainOrder getDomainOrder() {
        return DomainOrder.NONE;
    }

    public int getItemCount(int series) {
        if (series < 0 || series >= this.getSeriesCount()) {
            throw new IllegalArgumentException("Series index out of bounds");
        }
        double[][] seriesArray = (double[][])this.seriesList.get(series);
        return seriesArray[0].length;
    }

    public double getXValue(int series, int item) {
        double[][] seriesData = (double[][])this.seriesList.get(series);
        return seriesData[0][item];
    }

    public Number getX(int series, int item) {
        return new Double(this.getXValue(series, item));
    }

    public double getYValue(int series, int item) {
        double[][] seriesData = (double[][])this.seriesList.get(series);
        return seriesData[1][item];
    }

    public Number getY(int series, int item) {
        return new Double(this.getYValue(series, item));
    }

    public double getZValue(int series, int item) {
        double[][] seriesData = (double[][])this.seriesList.get(series);
        return seriesData[2][item];
    }

    public Number getZ(int series, int item) {
        return new Double(this.getZValue(series, item));
    }

    public void addSeries(Comparable seriesKey, double[][] data) {
        if (seriesKey == null) {
            throw new IllegalArgumentException("The 'seriesKey' cannot be null.");
        }
        if (data == null) {
            throw new IllegalArgumentException("The 'data' is null.");
        }
        if (data.length != 3) {
            throw new IllegalArgumentException("The 'data' array must have length == 3.");
        }
        if (data[0].length != data[1].length || data[0].length != data[2].length) {
            throw new IllegalArgumentException("The 'data' array must contain three arrays all having the same length.");
        }
        int seriesIndex = this.indexOf(seriesKey);
        if (seriesIndex == -1) {
            this.seriesKeys.add(seriesKey);
            this.seriesList.add(data);
        } else {
            this.seriesList.remove(seriesIndex);
            this.seriesList.add(seriesIndex, data);
        }
        this.notifyListeners(new DatasetChangeEvent(this, this));
    }

    public void removeSeries(Comparable seriesKey) {
        int seriesIndex = this.indexOf(seriesKey);
        if (seriesIndex >= 0) {
            this.seriesKeys.remove(seriesIndex);
            this.seriesList.remove(seriesIndex);
            this.notifyListeners(new DatasetChangeEvent(this, this));
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DefaultXYZDataset)) {
            return false;
        }
        DefaultXYZDataset that = (DefaultXYZDataset)obj;
        if (!((Object)this.seriesKeys).equals(that.seriesKeys)) {
            return false;
        }
        for (int i = 0; i < this.seriesList.size(); ++i) {
            double[][] d2;
            double[] d2x;
            double[][] d1 = (double[][])this.seriesList.get(i);
            double[] d1x = d1[0];
            if (!Arrays.equals(d1x, d2x = (d2 = (double[][])that.seriesList.get(i))[0])) {
                return false;
            }
            double[] d1y = d1[1];
            double[] d2y = d2[1];
            if (!Arrays.equals(d1y, d2y)) {
                return false;
            }
            double[] d1z = d1[2];
            double[] d2z = d2[2];
            if (Arrays.equals(d1z, d2z)) continue;
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = ((Object)this.seriesKeys).hashCode();
        result = 29 * result + ((Object)this.seriesList).hashCode();
        return result;
    }

    public Object clone() throws CloneNotSupportedException {
        DefaultXYZDataset clone = (DefaultXYZDataset)super.clone();
        clone.seriesKeys = new ArrayList(this.seriesKeys);
        clone.seriesList = new ArrayList(this.seriesList.size());
        for (int i = 0; i < this.seriesList.size(); ++i) {
            double[][] data = (double[][])this.seriesList.get(i);
            double[] x = data[0];
            double[] y = data[1];
            double[] z = data[2];
            double[] xx = new double[x.length];
            double[] yy = new double[y.length];
            double[] zz = new double[z.length];
            System.arraycopy(x, 0, xx, 0, x.length);
            System.arraycopy(y, 0, yy, 0, y.length);
            System.arraycopy(z, 0, zz, 0, z.length);
            clone.seriesList.add(i, new double[][]{xx, yy, zz});
        }
        return clone;
    }
}

