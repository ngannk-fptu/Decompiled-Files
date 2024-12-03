/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.statistics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.statistics.HistogramBin;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public class HistogramDataset
extends AbstractIntervalXYDataset
implements IntervalXYDataset,
Cloneable,
PublicCloneable,
Serializable {
    private static final long serialVersionUID = -6341668077370231153L;
    private List list = new ArrayList();
    private HistogramType type = HistogramType.FREQUENCY;

    public HistogramType getType() {
        return this.type;
    }

    public void setType(HistogramType type) {
        if (type == null) {
            throw new IllegalArgumentException("Null 'type' argument");
        }
        this.type = type;
        this.notifyListeners(new DatasetChangeEvent(this, this));
    }

    public void addSeries(Comparable key, double[] values, int bins) {
        double minimum = this.getMinimum(values);
        double maximum = this.getMaximum(values);
        this.addSeries(key, values, bins, minimum, maximum);
    }

    public void addSeries(Comparable key, double[] values, int bins, double minimum, double maximum) {
        int i;
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        if (values == null) {
            throw new IllegalArgumentException("Null 'values' argument.");
        }
        if (bins < 1) {
            throw new IllegalArgumentException("The 'bins' value must be at least 1.");
        }
        double binWidth = (maximum - minimum) / (double)bins;
        double lower = minimum;
        ArrayList<HistogramBin> binList = new ArrayList<HistogramBin>(bins);
        for (i = 0; i < bins; ++i) {
            HistogramBin bin;
            if (i == bins - 1) {
                bin = new HistogramBin(lower, maximum);
            } else {
                double upper = minimum + (double)(i + 1) * binWidth;
                bin = new HistogramBin(lower, upper);
                lower = upper;
            }
            binList.add(bin);
        }
        for (i = 0; i < values.length; ++i) {
            int binIndex = bins - 1;
            if (values[i] < maximum) {
                double fraction = (values[i] - minimum) / (maximum - minimum);
                if (fraction < 0.0) {
                    fraction = 0.0;
                }
                if ((binIndex = (int)(fraction * (double)bins)) >= bins) {
                    binIndex = bins - 1;
                }
            }
            HistogramBin bin = (HistogramBin)binList.get(binIndex);
            bin.incrementCount();
        }
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("key", key);
        map.put("bins", binList);
        map.put("values.length", new Integer(values.length));
        map.put("bin width", new Double(binWidth));
        this.list.add(map);
    }

    private double getMinimum(double[] values) {
        if (values == null || values.length < 1) {
            throw new IllegalArgumentException("Null or zero length 'values' argument.");
        }
        double min = Double.MAX_VALUE;
        for (int i = 0; i < values.length; ++i) {
            if (!(values[i] < min)) continue;
            min = values[i];
        }
        return min;
    }

    private double getMaximum(double[] values) {
        if (values == null || values.length < 1) {
            throw new IllegalArgumentException("Null or zero length 'values' argument.");
        }
        double max = -1.7976931348623157E308;
        for (int i = 0; i < values.length; ++i) {
            if (!(values[i] > max)) continue;
            max = values[i];
        }
        return max;
    }

    List getBins(int series) {
        Map map = (Map)this.list.get(series);
        return (List)map.get("bins");
    }

    private int getTotal(int series) {
        Map map = (Map)this.list.get(series);
        return (Integer)map.get("values.length");
    }

    private double getBinWidth(int series) {
        Map map = (Map)this.list.get(series);
        return (Double)map.get("bin width");
    }

    public int getSeriesCount() {
        return this.list.size();
    }

    public Comparable getSeriesKey(int series) {
        Map map = (Map)this.list.get(series);
        return (Comparable)map.get("key");
    }

    public int getItemCount(int series) {
        return this.getBins(series).size();
    }

    public Number getX(int series, int item) {
        List bins = this.getBins(series);
        HistogramBin bin = (HistogramBin)bins.get(item);
        double x = (bin.getStartBoundary() + bin.getEndBoundary()) / 2.0;
        return new Double(x);
    }

    public Number getY(int series, int item) {
        List bins = this.getBins(series);
        HistogramBin bin = (HistogramBin)bins.get(item);
        double total = this.getTotal(series);
        double binWidth = this.getBinWidth(series);
        if (this.type == HistogramType.FREQUENCY) {
            return new Double(bin.getCount());
        }
        if (this.type == HistogramType.RELATIVE_FREQUENCY) {
            return new Double((double)bin.getCount() / total);
        }
        if (this.type == HistogramType.SCALE_AREA_TO_1) {
            return new Double((double)bin.getCount() / (binWidth * total));
        }
        throw new IllegalStateException();
    }

    public Number getStartX(int series, int item) {
        List bins = this.getBins(series);
        HistogramBin bin = (HistogramBin)bins.get(item);
        return new Double(bin.getStartBoundary());
    }

    public Number getEndX(int series, int item) {
        List bins = this.getBins(series);
        HistogramBin bin = (HistogramBin)bins.get(item);
        return new Double(bin.getEndBoundary());
    }

    public Number getStartY(int series, int item) {
        return this.getY(series, item);
    }

    public Number getEndY(int series, int item) {
        return this.getY(series, item);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof HistogramDataset)) {
            return false;
        }
        HistogramDataset that = (HistogramDataset)obj;
        if (!ObjectUtilities.equal(this.type, that.type)) {
            return false;
        }
        return ObjectUtilities.equal(this.list, that.list);
    }

    public Object clone() throws CloneNotSupportedException {
        HistogramDataset clone = (HistogramDataset)super.clone();
        int seriesCount = this.getSeriesCount();
        clone.list = new ArrayList(seriesCount);
        for (int i = 0; i < seriesCount; ++i) {
            clone.list.add(new HashMap((Map)this.list.get(i)));
        }
        return clone;
    }
}

