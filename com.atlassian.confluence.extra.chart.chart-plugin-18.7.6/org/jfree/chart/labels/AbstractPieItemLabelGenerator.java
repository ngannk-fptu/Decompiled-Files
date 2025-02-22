/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.MessageFormat;
import java.text.NumberFormat;
import org.jfree.chart.HashUtilities;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.PieDataset;

public class AbstractPieItemLabelGenerator
implements Serializable {
    private static final long serialVersionUID = 7347703325267846275L;
    private String labelFormat;
    private NumberFormat numberFormat;
    private NumberFormat percentFormat;

    protected AbstractPieItemLabelGenerator(String labelFormat, NumberFormat numberFormat, NumberFormat percentFormat) {
        if (labelFormat == null) {
            throw new IllegalArgumentException("Null 'labelFormat' argument.");
        }
        if (numberFormat == null) {
            throw new IllegalArgumentException("Null 'numberFormat' argument.");
        }
        if (percentFormat == null) {
            throw new IllegalArgumentException("Null 'percentFormat' argument.");
        }
        this.labelFormat = labelFormat;
        this.numberFormat = numberFormat;
        this.percentFormat = percentFormat;
    }

    public String getLabelFormat() {
        return this.labelFormat;
    }

    public NumberFormat getNumberFormat() {
        return this.numberFormat;
    }

    public NumberFormat getPercentFormat() {
        return this.percentFormat;
    }

    protected Object[] createItemArray(PieDataset dataset, Comparable key) {
        double v;
        Object[] result = new Object[4];
        double total = DatasetUtilities.calculatePieDatasetTotal(dataset);
        result[0] = key.toString();
        Number value = dataset.getValue(key);
        result[1] = value != null ? this.numberFormat.format(value) : "null";
        double percent = 0.0;
        if (value != null && (v = value.doubleValue()) > 0.0) {
            percent = v / total;
        }
        result[2] = this.percentFormat.format(percent);
        result[3] = this.numberFormat.format(total);
        return result;
    }

    protected String generateSectionLabel(PieDataset dataset, Comparable key) {
        String result = null;
        if (dataset != null) {
            Object[] items = this.createItemArray(dataset, key);
            result = MessageFormat.format(this.labelFormat, items);
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AbstractPieItemLabelGenerator)) {
            return false;
        }
        AbstractPieItemLabelGenerator that = (AbstractPieItemLabelGenerator)obj;
        if (!this.labelFormat.equals(that.labelFormat)) {
            return false;
        }
        if (!this.numberFormat.equals(that.numberFormat)) {
            return false;
        }
        return this.percentFormat.equals(that.percentFormat);
    }

    public int hashCode() {
        int result = 127;
        result = HashUtilities.hashCode(result, this.labelFormat);
        result = HashUtilities.hashCode(result, this.numberFormat);
        result = HashUtilities.hashCode(result, this.percentFormat);
        return result;
    }

    public Object clone() throws CloneNotSupportedException {
        AbstractPieItemLabelGenerator clone = (AbstractPieItemLabelGenerator)super.clone();
        if (this.numberFormat != null) {
            clone.numberFormat = (NumberFormat)this.numberFormat.clone();
        }
        if (this.percentFormat != null) {
            clone.percentFormat = (NumberFormat)this.percentFormat.clone();
        }
        return clone;
    }
}

