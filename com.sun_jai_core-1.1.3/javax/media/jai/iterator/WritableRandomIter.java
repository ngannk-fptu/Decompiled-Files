/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.iterator;

import javax.media.jai.iterator.RandomIter;

public interface WritableRandomIter
extends RandomIter {
    public void setSample(int var1, int var2, int var3, int var4);

    public void setSample(int var1, int var2, int var3, float var4);

    public void setSample(int var1, int var2, int var3, double var4);

    public void setPixel(int var1, int var2, int[] var3);

    public void setPixel(int var1, int var2, float[] var3);

    public void setPixel(int var1, int var2, double[] var3);
}

