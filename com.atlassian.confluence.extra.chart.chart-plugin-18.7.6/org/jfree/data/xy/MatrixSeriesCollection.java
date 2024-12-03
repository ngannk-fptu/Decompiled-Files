/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.xy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jfree.data.xy.AbstractXYZDataset;
import org.jfree.data.xy.MatrixSeries;
import org.jfree.data.xy.XYZDataset;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public class MatrixSeriesCollection
extends AbstractXYZDataset
implements XYZDataset,
PublicCloneable,
Serializable {
    private static final long serialVersionUID = -3197705779242543945L;
    private List seriesList = new ArrayList();

    public MatrixSeriesCollection() {
        this(null);
    }

    public MatrixSeriesCollection(MatrixSeries series) {
        if (series != null) {
            this.seriesList.add(series);
            series.addChangeListener(this);
        }
    }

    public int getItemCount(int seriesIndex) {
        return this.getSeries(seriesIndex).getItemCount();
    }

    public MatrixSeries getSeries(int seriesIndex) {
        if (seriesIndex < 0 || seriesIndex > this.getSeriesCount()) {
            throw new IllegalArgumentException("Index outside valid range.");
        }
        MatrixSeries series = (MatrixSeries)this.seriesList.get(seriesIndex);
        return series;
    }

    public int getSeriesCount() {
        return this.seriesList.size();
    }

    public Comparable getSeriesKey(int seriesIndex) {
        return this.getSeries(seriesIndex).getKey();
    }

    public Number getX(int seriesIndex, int itemIndex) {
        MatrixSeries series = (MatrixSeries)this.seriesList.get(seriesIndex);
        int x = series.getItemColumn(itemIndex);
        return new Integer(x);
    }

    public Number getY(int seriesIndex, int itemIndex) {
        MatrixSeries series = (MatrixSeries)this.seriesList.get(seriesIndex);
        int y = series.getItemRow(itemIndex);
        return new Integer(y);
    }

    public Number getZ(int seriesIndex, int itemIndex) {
        MatrixSeries series = (MatrixSeries)this.seriesList.get(seriesIndex);
        Number z = series.getItem(itemIndex);
        return z;
    }

    public void addSeries(MatrixSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Cannot add null series.");
        }
        this.seriesList.add(series);
        series.addChangeListener(this);
        this.fireDatasetChanged();
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof MatrixSeriesCollection) {
            MatrixSeriesCollection c = (MatrixSeriesCollection)obj;
            return ObjectUtilities.equal(this.seriesList, c.seriesList);
        }
        return false;
    }

    public int hashCode() {
        return this.seriesList != null ? ((Object)this.seriesList).hashCode() : 0;
    }

    public Object clone() throws CloneNotSupportedException {
        MatrixSeriesCollection clone = (MatrixSeriesCollection)super.clone();
        clone.seriesList = (List)ObjectUtilities.deepClone(this.seriesList);
        return clone;
    }

    public void removeAllSeries() {
        for (int i = 0; i < this.seriesList.size(); ++i) {
            MatrixSeries series = (MatrixSeries)this.seriesList.get(i);
            series.removeChangeListener(this);
        }
        this.seriesList.clear();
        this.fireDatasetChanged();
    }

    public void removeSeries(MatrixSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Cannot remove null series.");
        }
        if (this.seriesList.contains(series)) {
            series.removeChangeListener(this);
            this.seriesList.remove(series);
            this.fireDatasetChanged();
        }
    }

    public void removeSeries(int seriesIndex) {
        if (seriesIndex < 0 || seriesIndex > this.getSeriesCount()) {
            throw new IllegalArgumentException("Index outside valid range.");
        }
        MatrixSeries series = (MatrixSeries)this.seriesList.get(seriesIndex);
        series.removeChangeListener(this);
        this.seriesList.remove(seriesIndex);
        this.fireDatasetChanged();
    }
}

