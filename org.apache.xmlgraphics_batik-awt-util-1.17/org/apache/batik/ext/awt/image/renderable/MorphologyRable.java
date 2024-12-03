/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.renderable;

import org.apache.batik.ext.awt.image.renderable.Filter;

public interface MorphologyRable
extends Filter {
    public Filter getSource();

    public void setSource(Filter var1);

    public void setRadiusX(double var1);

    public void setRadiusY(double var1);

    public void setDoDilation(boolean var1);

    public boolean getDoDilation();

    public double getRadiusX();

    public double getRadiusY();
}

