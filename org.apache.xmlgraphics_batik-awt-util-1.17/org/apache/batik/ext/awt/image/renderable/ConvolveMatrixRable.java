/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.renderable;

import java.awt.Point;
import java.awt.image.Kernel;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.FilterColorInterpolation;

public interface ConvolveMatrixRable
extends FilterColorInterpolation {
    public Filter getSource();

    public void setSource(Filter var1);

    public Kernel getKernel();

    public void setKernel(Kernel var1);

    public Point getTarget();

    public void setTarget(Point var1);

    public double getBias();

    public void setBias(double var1);

    public PadMode getEdgeMode();

    public void setEdgeMode(PadMode var1);

    public double[] getKernelUnitLength();

    public void setKernelUnitLength(double[] var1);

    public boolean getPreserveAlpha();

    public void setPreserveAlpha(boolean var1);
}

