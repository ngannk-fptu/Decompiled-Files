/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYZToolTipGenerator;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.util.ObjectUtilities;

public class StandardXYZToolTipGenerator
extends StandardXYToolTipGenerator
implements XYZToolTipGenerator,
Serializable {
    private static final long serialVersionUID = -2961577421889473503L;
    public static final String DEFAULT_TOOL_TIP_FORMAT = "{0}: ({1}, {2}, {3})";
    private NumberFormat zFormat;
    private DateFormat zDateFormat;

    public StandardXYZToolTipGenerator() {
        this(DEFAULT_TOOL_TIP_FORMAT, NumberFormat.getNumberInstance(), NumberFormat.getNumberInstance(), NumberFormat.getNumberInstance());
    }

    public StandardXYZToolTipGenerator(String formatString, NumberFormat xFormat, NumberFormat yFormat, NumberFormat zFormat) {
        super(formatString, xFormat, yFormat);
        if (zFormat == null) {
            throw new IllegalArgumentException("Null 'zFormat' argument.");
        }
        this.zFormat = zFormat;
    }

    public StandardXYZToolTipGenerator(String formatString, DateFormat xFormat, DateFormat yFormat, DateFormat zFormat) {
        super(formatString, xFormat, yFormat);
        if (zFormat == null) {
            throw new IllegalArgumentException("Null 'zFormat' argument.");
        }
        this.zDateFormat = zFormat;
    }

    public NumberFormat getZFormat() {
        return this.zFormat;
    }

    public DateFormat getZDateFormat() {
        return this.zDateFormat;
    }

    public String generateToolTip(XYZDataset dataset, int series, int item) {
        return this.generateLabelString(dataset, series, item);
    }

    public String generateLabelString(XYDataset dataset, int series, int item) {
        String result = null;
        Object[] items = this.createItemArray((XYZDataset)dataset, series, item);
        result = MessageFormat.format(this.getFormatString(), items);
        return result;
    }

    protected Object[] createItemArray(XYZDataset dataset, int series, int item) {
        Object[] result = new Object[4];
        result[0] = dataset.getSeriesKey(series).toString();
        Number x = dataset.getX(series, item);
        DateFormat xf = this.getXDateFormat();
        result[1] = xf != null ? xf.format(x) : this.getXFormat().format(x);
        Number y = dataset.getY(series, item);
        DateFormat yf = this.getYDateFormat();
        result[2] = yf != null ? yf.format(y) : this.getYFormat().format(y);
        Number z = dataset.getZ(series, item);
        result[3] = this.zDateFormat != null ? this.zDateFormat.format(z) : this.zFormat.format(z);
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StandardXYZToolTipGenerator)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        StandardXYZToolTipGenerator that = (StandardXYZToolTipGenerator)obj;
        if (!ObjectUtilities.equal(this.zFormat, that.zFormat)) {
            return false;
        }
        return ObjectUtilities.equal(this.zDateFormat, that.zDateFormat);
    }
}

