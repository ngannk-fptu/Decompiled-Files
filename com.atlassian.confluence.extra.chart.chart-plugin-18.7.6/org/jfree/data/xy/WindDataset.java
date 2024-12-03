/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.xy;

import org.jfree.data.xy.XYDataset;

public interface WindDataset
extends XYDataset {
    public Number getWindDirection(int var1, int var2);

    public Number getWindForce(int var1, int var2);
}

