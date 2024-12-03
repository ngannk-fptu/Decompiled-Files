/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.jfree.chart.labels.ContourToolTipGenerator;
import org.jfree.data.contour.ContourDataset;

public class StandardContourToolTipGenerator
implements ContourToolTipGenerator,
Serializable {
    private static final long serialVersionUID = -1881659351247502711L;
    private DecimalFormat valueForm = new DecimalFormat("##.###");

    public String generateToolTip(ContourDataset data, int item) {
        double x = data.getXValue(0, item);
        double y = data.getYValue(0, item);
        double z = data.getZValue(0, item);
        String xString = null;
        if (data.isDateAxis(0)) {
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
            StringBuffer strbuf = new StringBuffer();
            strbuf = formatter.format(new Date((long)x), strbuf, new FieldPosition(0));
            xString = strbuf.toString();
        } else {
            xString = this.valueForm.format(x);
        }
        if (!Double.isNaN(z)) {
            return "X: " + xString + ", Y: " + this.valueForm.format(y) + ", Z: " + this.valueForm.format(z);
        }
        return "X: " + xString + ", Y: " + this.valueForm.format(y) + ", Z: no data";
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StandardContourToolTipGenerator)) {
            return false;
        }
        StandardContourToolTipGenerator that = (StandardContourToolTipGenerator)obj;
        if (this.valueForm != null) {
            return this.valueForm.equals(that.valueForm);
        }
        return false;
    }
}

