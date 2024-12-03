/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.renderable;

import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.FilterColorInterpolation;

public interface GaussianBlurRable
extends FilterColorInterpolation {
    public Filter getSource();

    public void setSource(Filter var1);

    public void setStdDeviationX(double var1);

    public void setStdDeviationY(double var1);

    public double getStdDeviationX();

    public double getStdDeviationY();
}

