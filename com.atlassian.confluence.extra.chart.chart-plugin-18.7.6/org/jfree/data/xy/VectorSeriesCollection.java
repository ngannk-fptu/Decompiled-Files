/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.xy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.Vector;
import org.jfree.data.xy.VectorDataItem;
import org.jfree.data.xy.VectorSeries;
import org.jfree.data.xy.VectorXYDataset;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public class VectorSeriesCollection
extends AbstractXYDataset
implements VectorXYDataset,
PublicCloneable,
Serializable {
    private List data = new ArrayList();

    public void addSeries(VectorSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Null 'series' argument.");
        }
        this.data.add(series);
        series.addChangeListener(this);
        this.fireDatasetChanged();
    }

    public boolean removeSeries(VectorSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Null 'series' argument.");
        }
        boolean removed = this.data.remove(series);
        if (removed) {
            series.removeChangeListener(this);
            this.fireDatasetChanged();
        }
        return removed;
    }

    public void removeAllSeries() {
        for (int i = 0; i < this.data.size(); ++i) {
            VectorSeries series = (VectorSeries)this.data.get(i);
            series.removeChangeListener(this);
        }
        this.data.clear();
        this.fireDatasetChanged();
    }

    public int getSeriesCount() {
        return this.data.size();
    }

    public VectorSeries getSeries(int series) {
        if (series < 0 || series >= this.getSeriesCount()) {
            throw new IllegalArgumentException("Series index out of bounds");
        }
        return (VectorSeries)this.data.get(series);
    }

    public Comparable getSeriesKey(int series) {
        return this.getSeries(series).getKey();
    }

    public int indexOf(VectorSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Null 'series' argument.");
        }
        return this.data.indexOf(series);
    }

    public int getItemCount(int series) {
        return this.getSeries(series).getItemCount();
    }

    public double getXValue(int series, int item) {
        VectorSeries s = (VectorSeries)this.data.get(series);
        VectorDataItem di = (VectorDataItem)s.getDataItem(item);
        return di.getXValue();
    }

    public Number getX(int series, int item) {
        return new Double(this.getXValue(series, item));
    }

    public double getYValue(int series, int item) {
        VectorSeries s = (VectorSeries)this.data.get(series);
        VectorDataItem di = (VectorDataItem)s.getDataItem(item);
        return di.getYValue();
    }

    public Number getY(int series, int item) {
        return new Double(this.getYValue(series, item));
    }

    public Vector getVector(int series, int item) {
        VectorSeries s = (VectorSeries)this.data.get(series);
        VectorDataItem di = (VectorDataItem)s.getDataItem(item);
        return di.getVector();
    }

    public double getVectorXValue(int series, int item) {
        VectorSeries s = (VectorSeries)this.data.get(series);
        VectorDataItem di = (VectorDataItem)s.getDataItem(item);
        return di.getVectorX();
    }

    public double getVectorYValue(int series, int item) {
        VectorSeries s = (VectorSeries)this.data.get(series);
        VectorDataItem di = (VectorDataItem)s.getDataItem(item);
        return di.getVectorY();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof VectorSeriesCollection)) {
            return false;
        }
        VectorSeriesCollection that = (VectorSeriesCollection)obj;
        return ObjectUtilities.equal(this.data, that.data);
    }

    public Object clone() throws CloneNotSupportedException {
        VectorSeriesCollection clone = (VectorSeriesCollection)super.clone();
        clone.data = (List)ObjectUtilities.deepClone(this.data);
        return clone;
    }
}

