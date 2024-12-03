/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.renderable;

import org.apache.batik.ext.awt.image.renderable.Filter;

public interface OffsetRable
extends Filter {
    public Filter getSource();

    public void setSource(Filter var1);

    public void setXoffset(double var1);

    public double getXoffset();

    public void setYoffset(double var1);

    public double getYoffset();
}

