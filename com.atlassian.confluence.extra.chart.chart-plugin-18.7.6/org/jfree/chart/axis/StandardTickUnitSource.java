/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.axis;

import java.io.Serializable;
import java.text.DecimalFormat;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnit;
import org.jfree.chart.axis.TickUnitSource;

public class StandardTickUnitSource
implements TickUnitSource,
Serializable {
    private static final double LOG_10_VALUE = Math.log(10.0);

    public TickUnit getLargerTickUnit(TickUnit unit) {
        double x = unit.getSize();
        double log = Math.log(x) / LOG_10_VALUE;
        double higher = Math.ceil(log);
        return new NumberTickUnit(Math.pow(10.0, higher), new DecimalFormat("0.0E0"));
    }

    public TickUnit getCeilingTickUnit(TickUnit unit) {
        return this.getLargerTickUnit(unit);
    }

    public TickUnit getCeilingTickUnit(double size) {
        double log = Math.log(size) / LOG_10_VALUE;
        double higher = Math.ceil(log);
        return new NumberTickUnit(Math.pow(10.0, higher), new DecimalFormat("0.0E0"));
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        return obj instanceof StandardTickUnitSource;
    }

    public int hashCode() {
        return 0;
    }
}

