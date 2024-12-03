/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.renderable;

import java.awt.geom.Rectangle2D;
import org.apache.batik.ext.awt.image.renderable.FilterColorInterpolation;

public interface TurbulenceRable
extends FilterColorInterpolation {
    public void setTurbulenceRegion(Rectangle2D var1);

    public Rectangle2D getTurbulenceRegion();

    public int getSeed();

    public double getBaseFrequencyX();

    public double getBaseFrequencyY();

    public int getNumOctaves();

    public boolean isStitched();

    public boolean isFractalNoise();

    public void setSeed(int var1);

    public void setBaseFrequencyX(double var1);

    public void setBaseFrequencyY(double var1);

    public void setNumOctaves(int var1);

    public void setStitched(boolean var1);

    public void setFractalNoise(boolean var1);
}

