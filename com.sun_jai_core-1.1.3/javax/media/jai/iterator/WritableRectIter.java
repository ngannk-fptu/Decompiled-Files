/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.iterator;

import javax.media.jai.iterator.RectIter;

public interface WritableRectIter
extends RectIter {
    public void setSample(int var1);

    public void setSample(int var1, int var2);

    public void setSample(float var1);

    public void setSample(int var1, float var2);

    public void setSample(double var1);

    public void setSample(int var1, double var2);

    public void setPixel(int[] var1);

    public void setPixel(float[] var1);

    public void setPixel(double[] var1);
}

