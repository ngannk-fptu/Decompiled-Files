/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.xy;

import java.io.Serializable;
import org.jfree.chart.HashUtilities;
import org.jfree.data.DomainInfo;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.util.PublicCloneable;

public class IntervalXYDelegate
implements DatasetChangeListener,
DomainInfo,
Serializable,
Cloneable,
PublicCloneable {
    private static final long serialVersionUID = -685166711639592857L;
    private XYDataset dataset;
    private boolean autoWidth;
    private double intervalPositionFactor;
    private double fixedIntervalWidth;
    private double autoIntervalWidth;

    public IntervalXYDelegate(XYDataset dataset) {
        this(dataset, true);
    }

    public IntervalXYDelegate(XYDataset dataset, boolean autoWidth) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        this.dataset = dataset;
        this.autoWidth = autoWidth;
        this.intervalPositionFactor = 0.5;
        this.autoIntervalWidth = Double.POSITIVE_INFINITY;
        this.fixedIntervalWidth = 1.0;
    }

    public boolean isAutoWidth() {
        return this.autoWidth;
    }

    public void setAutoWidth(boolean b) {
        this.autoWidth = b;
        if (b) {
            this.autoIntervalWidth = this.recalculateInterval();
        }
    }

    public double getIntervalPositionFactor() {
        return this.intervalPositionFactor;
    }

    public void setIntervalPositionFactor(double d) {
        if (d < 0.0 || 1.0 < d) {
            throw new IllegalArgumentException("Argument 'd' outside valid range.");
        }
        this.intervalPositionFactor = d;
    }

    public double getFixedIntervalWidth() {
        return this.fixedIntervalWidth;
    }

    public void setFixedIntervalWidth(double w) {
        if (w < 0.0) {
            throw new IllegalArgumentException("Negative 'w' argument.");
        }
        this.fixedIntervalWidth = w;
        this.autoWidth = false;
    }

    public double getIntervalWidth() {
        if (this.isAutoWidth() && !Double.isInfinite(this.autoIntervalWidth)) {
            return this.autoIntervalWidth;
        }
        return this.fixedIntervalWidth;
    }

    public Number getStartX(int series, int item) {
        Double startX = null;
        Number x = this.dataset.getX(series, item);
        if (x != null) {
            startX = new Double(x.doubleValue() - this.getIntervalPositionFactor() * this.getIntervalWidth());
        }
        return startX;
    }

    public double getStartXValue(int series, int item) {
        return this.dataset.getXValue(series, item) - this.getIntervalPositionFactor() * this.getIntervalWidth();
    }

    public Number getEndX(int series, int item) {
        Double endX = null;
        Number x = this.dataset.getX(series, item);
        if (x != null) {
            endX = new Double(x.doubleValue() + (1.0 - this.getIntervalPositionFactor()) * this.getIntervalWidth());
        }
        return endX;
    }

    public double getEndXValue(int series, int item) {
        return this.dataset.getXValue(series, item) + (1.0 - this.getIntervalPositionFactor()) * this.getIntervalWidth();
    }

    public double getDomainLowerBound(boolean includeInterval) {
        double result = Double.NaN;
        Range r = this.getDomainBounds(includeInterval);
        if (r != null) {
            result = r.getLowerBound();
        }
        return result;
    }

    public double getDomainUpperBound(boolean includeInterval) {
        double result = Double.NaN;
        Range r = this.getDomainBounds(includeInterval);
        if (r != null) {
            result = r.getUpperBound();
        }
        return result;
    }

    public Range getDomainBounds(boolean includeInterval) {
        Range range = DatasetUtilities.findDomainBounds(this.dataset, false);
        if (includeInterval && range != null) {
            double lowerAdj = this.getIntervalWidth() * this.getIntervalPositionFactor();
            double upperAdj = this.getIntervalWidth() - lowerAdj;
            range = new Range(range.getLowerBound() - lowerAdj, range.getUpperBound() + upperAdj);
        }
        return range;
    }

    public void datasetChanged(DatasetChangeEvent e) {
        if (this.autoWidth) {
            this.autoIntervalWidth = this.recalculateInterval();
        }
    }

    private double recalculateInterval() {
        double result = Double.POSITIVE_INFINITY;
        int seriesCount = this.dataset.getSeriesCount();
        for (int series = 0; series < seriesCount; ++series) {
            result = Math.min(result, this.calculateIntervalForSeries(series));
        }
        return result;
    }

    private double calculateIntervalForSeries(int series) {
        double result = Double.POSITIVE_INFINITY;
        int itemCount = this.dataset.getItemCount(series);
        if (itemCount > 1) {
            double prev = this.dataset.getXValue(series, 0);
            for (int item = 1; item < itemCount; ++item) {
                double x = this.dataset.getXValue(series, item);
                result = Math.min(result, x - prev);
                prev = x;
            }
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof IntervalXYDelegate)) {
            return false;
        }
        IntervalXYDelegate that = (IntervalXYDelegate)obj;
        if (this.autoWidth != that.autoWidth) {
            return false;
        }
        if (this.intervalPositionFactor != that.intervalPositionFactor) {
            return false;
        }
        return this.fixedIntervalWidth == that.fixedIntervalWidth;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public int hashCode() {
        int hash = 5;
        hash = HashUtilities.hashCode(hash, this.autoWidth);
        hash = HashUtilities.hashCode(hash, this.intervalPositionFactor);
        hash = HashUtilities.hashCode(hash, this.fixedIntervalWidth);
        return hash;
    }
}

