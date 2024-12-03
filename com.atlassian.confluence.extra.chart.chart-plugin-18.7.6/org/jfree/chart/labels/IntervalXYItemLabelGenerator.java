/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import org.jfree.chart.labels.AbstractXYItemLabelGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.util.PublicCloneable;

public class IntervalXYItemLabelGenerator
extends AbstractXYItemLabelGenerator
implements XYItemLabelGenerator,
Cloneable,
PublicCloneable,
Serializable {
    public static final String DEFAULT_ITEM_LABEL_FORMAT = "{5} - {6}";

    public IntervalXYItemLabelGenerator() {
        this(DEFAULT_ITEM_LABEL_FORMAT, NumberFormat.getNumberInstance(), NumberFormat.getNumberInstance());
    }

    public IntervalXYItemLabelGenerator(String formatString, NumberFormat xFormat, NumberFormat yFormat) {
        super(formatString, xFormat, yFormat);
    }

    public IntervalXYItemLabelGenerator(String formatString, DateFormat xFormat, NumberFormat yFormat) {
        super(formatString, xFormat, yFormat);
    }

    public IntervalXYItemLabelGenerator(String formatString, NumberFormat xFormat, DateFormat yFormat) {
        super(formatString, xFormat, yFormat);
    }

    public IntervalXYItemLabelGenerator(String formatString, DateFormat xFormat, DateFormat yFormat) {
        super(formatString, xFormat, yFormat);
    }

    protected Object[] createItemArray(XYDataset dataset, int series, int item) {
        DateFormat xdf;
        double y;
        double x;
        IntervalXYDataset intervalDataset = null;
        if (dataset instanceof IntervalXYDataset) {
            intervalDataset = (IntervalXYDataset)dataset;
        }
        Object[] result = new Object[7];
        result[0] = dataset.getSeriesKey(series).toString();
        double xs = x = dataset.getXValue(series, item);
        double xe = x;
        double ys = y = dataset.getYValue(series, item);
        double ye = y;
        if (intervalDataset != null) {
            xs = intervalDataset.getStartXValue(series, item);
            xe = intervalDataset.getEndXValue(series, item);
            ys = intervalDataset.getStartYValue(series, item);
            ye = intervalDataset.getEndYValue(series, item);
        }
        if ((xdf = this.getXDateFormat()) != null) {
            result[1] = xdf.format(new Date((long)x));
            result[2] = xdf.format(new Date((long)xs));
            result[3] = xdf.format(new Date((long)xe));
        } else {
            NumberFormat xnf = this.getXFormat();
            result[1] = xnf.format(x);
            result[2] = xnf.format(xs);
            result[3] = xnf.format(xe);
        }
        NumberFormat ynf = this.getYFormat();
        DateFormat ydf = this.getYDateFormat();
        result[4] = Double.isNaN(y) && dataset.getY(series, item) == null ? this.getNullYString() : (ydf != null ? ydf.format(new Date((long)y)) : ynf.format(y));
        result[5] = Double.isNaN(ys) && intervalDataset.getStartY(series, item) == null ? this.getNullYString() : (ydf != null ? ydf.format(new Date((long)ys)) : ynf.format(ys));
        result[6] = Double.isNaN(ye) && intervalDataset.getEndY(series, item) == null ? this.getNullYString() : (ydf != null ? ydf.format(new Date((long)ye)) : ynf.format(ye));
        return result;
    }

    public String generateLabel(XYDataset dataset, int series, int item) {
        return this.generateLabelString(dataset, series, item);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof IntervalXYItemLabelGenerator)) {
            return false;
        }
        return super.equals(obj);
    }
}

