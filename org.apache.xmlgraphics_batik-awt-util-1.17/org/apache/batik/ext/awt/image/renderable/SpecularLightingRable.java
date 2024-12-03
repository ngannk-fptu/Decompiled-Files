/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.renderable;

import java.awt.geom.Rectangle2D;
import org.apache.batik.ext.awt.image.Light;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.FilterColorInterpolation;

public interface SpecularLightingRable
extends FilterColorInterpolation {
    public Filter getSource();

    public void setSource(Filter var1);

    public Light getLight();

    public void setLight(Light var1);

    public double getSurfaceScale();

    public void setSurfaceScale(double var1);

    public double getKs();

    public void setKs(double var1);

    public double getSpecularExponent();

    public void setSpecularExponent(double var1);

    public Rectangle2D getLitRegion();

    public void setLitRegion(Rectangle2D var1);

    public double[] getKernelUnitLength();

    public void setKernelUnitLength(double[] var1);
}

