/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.iterator;

public interface RandomIter {
    public int getSample(int var1, int var2, int var3);

    public float getSampleFloat(int var1, int var2, int var3);

    public double getSampleDouble(int var1, int var2, int var3);

    public int[] getPixel(int var1, int var2, int[] var3);

    public float[] getPixel(int var1, int var2, float[] var3);

    public double[] getPixel(int var1, int var2, double[] var3);

    public void done();
}

