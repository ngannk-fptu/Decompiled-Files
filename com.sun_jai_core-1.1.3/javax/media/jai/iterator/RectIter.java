/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.iterator;

public interface RectIter {
    public void startLines();

    public void nextLine();

    public boolean nextLineDone();

    public void jumpLines(int var1);

    public boolean finishedLines();

    public void startPixels();

    public void nextPixel();

    public boolean nextPixelDone();

    public void jumpPixels(int var1);

    public boolean finishedPixels();

    public void startBands();

    public void nextBand();

    public boolean nextBandDone();

    public boolean finishedBands();

    public int getSample();

    public int getSample(int var1);

    public float getSampleFloat();

    public float getSampleFloat(int var1);

    public double getSampleDouble();

    public double getSampleDouble(int var1);

    public int[] getPixel(int[] var1);

    public float[] getPixel(float[] var1);

    public double[] getPixel(double[] var1);
}

