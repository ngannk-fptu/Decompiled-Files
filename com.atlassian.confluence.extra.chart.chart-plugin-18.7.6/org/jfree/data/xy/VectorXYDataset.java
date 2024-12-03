/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.xy;

import org.jfree.data.xy.Vector;
import org.jfree.data.xy.XYDataset;

public interface VectorXYDataset
extends XYDataset {
    public double getVectorXValue(int var1, int var2);

    public double getVectorYValue(int var1, int var2);

    public Vector getVector(int var1, int var2);
}

