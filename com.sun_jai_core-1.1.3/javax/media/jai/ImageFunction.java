/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

public interface ImageFunction {
    public boolean isComplex();

    public int getNumElements();

    public void getElements(float var1, float var2, float var3, float var4, int var5, int var6, int var7, float[] var8, float[] var9);

    public void getElements(double var1, double var3, double var5, double var7, int var9, int var10, int var11, double[] var12, double[] var13);
}

