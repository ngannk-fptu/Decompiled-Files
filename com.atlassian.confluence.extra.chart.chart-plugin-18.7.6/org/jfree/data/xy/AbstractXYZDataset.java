/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.xy;

import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.XYZDataset;

public abstract class AbstractXYZDataset
extends AbstractXYDataset
implements XYZDataset {
    public double getZValue(int series, int item) {
        double result = Double.NaN;
        Number z = this.getZ(series, item);
        if (z != null) {
            result = z.doubleValue();
        }
        return result;
    }
}

