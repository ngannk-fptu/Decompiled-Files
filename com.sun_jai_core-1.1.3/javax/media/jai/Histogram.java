/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.ListIterator;
import javax.media.jai.JaiI18N;
import javax.media.jai.PixelAccessor;
import javax.media.jai.ROI;
import javax.media.jai.UnpackedImageData;

public class Histogram
implements Serializable {
    private int[] numBins;
    private double[] lowValue;
    private double[] highValue;
    private int numBands;
    private double[] binWidth;
    private int[][] bins = null;
    private int[] totals = null;
    private double[] mean = null;

    private static final int[] fill(int[] array, int newLength) {
        int[] newArray = null;
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (newLength > 0) {
            newArray = new int[newLength];
            int oldLength = array.length;
            for (int i = 0; i < newLength; ++i) {
                newArray[i] = i < oldLength ? array[i] : array[0];
            }
        }
        return newArray;
    }

    private static final double[] fill(double[] array, int newLength) {
        double[] newArray = null;
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (newLength > 0) {
            newArray = new double[newLength];
            int oldLength = array.length;
            for (int i = 0; i < newLength; ++i) {
                newArray[i] = i < oldLength ? array[i] : array[0];
            }
        }
        return newArray;
    }

    public Histogram(int[] numBins, double[] lowValue, double[] highValue) {
        int i;
        if (numBins == null || lowValue == null || highValue == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.numBands = numBins.length;
        if (lowValue.length != this.numBands || highValue.length != this.numBands) {
            throw new IllegalArgumentException(JaiI18N.getString("Histogram0"));
        }
        if (this.numBands == 0) {
            throw new IllegalArgumentException(JaiI18N.getString("Histogram1"));
        }
        for (i = 0; i < this.numBands; ++i) {
            if (numBins[i] <= 0) {
                throw new IllegalArgumentException(JaiI18N.getString("Histogram2"));
            }
            if (!(lowValue[i] >= highValue[i])) continue;
            throw new IllegalArgumentException(JaiI18N.getString("Histogram3"));
        }
        this.numBins = (int[])numBins.clone();
        this.lowValue = (double[])lowValue.clone();
        this.highValue = (double[])highValue.clone();
        this.binWidth = new double[this.numBands];
        for (i = 0; i < this.numBands; ++i) {
            this.binWidth[i] = (highValue[i] - lowValue[i]) / (double)numBins[i];
        }
    }

    public Histogram(int[] numBins, double[] lowValue, double[] highValue, int numBands) {
        this(Histogram.fill(numBins, numBands), Histogram.fill(lowValue, numBands), Histogram.fill(highValue, numBands));
    }

    public Histogram(int numBins, double lowValue, double highValue, int numBands) {
        if (numBands <= 0) {
            throw new IllegalArgumentException(JaiI18N.getString("Histogram1"));
        }
        if (numBins <= 0) {
            throw new IllegalArgumentException(JaiI18N.getString("Histogram2"));
        }
        if (lowValue >= highValue) {
            throw new IllegalArgumentException(JaiI18N.getString("Histogram3"));
        }
        this.numBands = numBands;
        this.numBins = new int[numBands];
        this.lowValue = new double[numBands];
        this.highValue = new double[numBands];
        this.binWidth = new double[numBands];
        double bw = (highValue - lowValue) / (double)numBins;
        for (int i = 0; i < numBands; ++i) {
            this.numBins[i] = numBins;
            this.lowValue[i] = lowValue;
            this.highValue[i] = highValue;
            this.binWidth[i] = bw;
        }
    }

    public int[] getNumBins() {
        return (int[])this.numBins.clone();
    }

    public int getNumBins(int band) {
        return this.numBins[band];
    }

    public double[] getLowValue() {
        return (double[])this.lowValue.clone();
    }

    public double getLowValue(int band) {
        return this.lowValue[band];
    }

    public double[] getHighValue() {
        return (double[])this.highValue.clone();
    }

    public double getHighValue(int band) {
        return this.highValue[band];
    }

    public int getNumBands() {
        return this.numBands;
    }

    public synchronized int[][] getBins() {
        if (this.bins == null) {
            this.bins = new int[this.numBands][];
            for (int i = 0; i < this.numBands; ++i) {
                this.bins[i] = new int[this.numBins[i]];
            }
        }
        return this.bins;
    }

    public int[] getBins(int band) {
        this.getBins();
        return this.bins[band];
    }

    public int getBinSize(int band, int bin) {
        this.getBins();
        return this.bins[band][bin];
    }

    public double getBinLowValue(int band, int bin) {
        return this.lowValue[band] + (double)bin * this.binWidth[band];
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void clearHistogram() {
        if (this.bins == null) return;
        int[][] nArray = this.bins;
        synchronized (this.bins) {
            for (int i = 0; i < this.numBands; ++i) {
                int[] b = this.bins[i];
                int length = b.length;
                for (int j = 0; j < length; ++j) {
                    b[j] = 0;
                }
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int[] getTotals() {
        if (this.totals == null) {
            this.getBins();
            Histogram histogram = this;
            synchronized (histogram) {
                this.totals = new int[this.numBands];
                for (int i = 0; i < this.numBands; ++i) {
                    int[] b = this.bins[i];
                    int length = b.length;
                    int t = 0;
                    for (int j = 0; j < length; ++j) {
                        t += b[j];
                    }
                    this.totals[i] = t;
                }
            }
        }
        return this.totals;
    }

    public int getSubTotal(int band, int minBin, int maxBin) {
        if (minBin < 0 || maxBin >= this.numBins[band]) {
            throw new ArrayIndexOutOfBoundsException(JaiI18N.getString("Histogram5"));
        }
        if (minBin > maxBin) {
            throw new IllegalArgumentException(JaiI18N.getString("Histogram10"));
        }
        int[] b = this.getBins(band);
        int total = 0;
        for (int i = minBin; i <= maxBin; ++i) {
            total += b[i];
        }
        return total;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public double[] getMean() {
        if (this.mean == null) {
            this.getTotals();
            Histogram histogram = this;
            synchronized (histogram) {
                this.mean = new double[this.numBands];
                for (int i = 0; i < this.numBands; ++i) {
                    int[] counts = this.getBins(i);
                    int nBins = this.numBins[i];
                    double level = this.getLowValue(i);
                    double bw = this.binWidth[i];
                    double mu = 0.0;
                    double total = this.totals[i];
                    for (int b = 0; b < nBins; ++b) {
                        mu += (double)counts[b] / total * level;
                        level += bw;
                    }
                    this.mean[i] = mu;
                }
            }
        }
        return this.mean;
    }

    public void countPixels(Raster raster, ROI roi, int xStart, int yStart, int xPeriod, int yPeriod) {
        LinkedList rectList;
        if (raster == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        SampleModel sampleModel = raster.getSampleModel();
        if (sampleModel.getNumBands() != this.numBands) {
            throw new IllegalArgumentException(JaiI18N.getString("Histogram4"));
        }
        Rectangle bounds = raster.getBounds();
        if (roi == null) {
            rectList = new LinkedList();
            rectList.addLast(bounds);
        } else {
            rectList = roi.getAsRectangleList(bounds.x, bounds.y, bounds.width, bounds.height);
            if (rectList == null) {
                return;
            }
        }
        PixelAccessor accessor = new PixelAccessor(sampleModel, null);
        ListIterator iterator = rectList.listIterator(0);
        block8: while (iterator.hasNext()) {
            Rectangle r = (Rectangle)iterator.next();
            int tx = r.x;
            int ty = r.y;
            r.x = this.startPosition(tx, xStart, xPeriod);
            r.y = this.startPosition(ty, yStart, yPeriod);
            r.width = tx + r.width - r.x;
            r.height = ty + r.height - r.y;
            if (r.width <= 0 || r.height <= 0) continue;
            switch (accessor.sampleType) {
                case -1: 
                case 0: {
                    this.countPixelsByte(accessor, raster, r, xPeriod, yPeriod);
                    continue block8;
                }
                case 1: {
                    this.countPixelsUShort(accessor, raster, r, xPeriod, yPeriod);
                    continue block8;
                }
                case 2: {
                    this.countPixelsShort(accessor, raster, r, xPeriod, yPeriod);
                    continue block8;
                }
                case 3: {
                    this.countPixelsInt(accessor, raster, r, xPeriod, yPeriod);
                    continue block8;
                }
                case 4: {
                    this.countPixelsFloat(accessor, raster, r, xPeriod, yPeriod);
                    continue block8;
                }
                case 5: {
                    this.countPixelsDouble(accessor, raster, r, xPeriod, yPeriod);
                    continue block8;
                }
            }
            throw new RuntimeException(JaiI18N.getString("Histogram11"));
        }
    }

    private void countPixelsByte(PixelAccessor accessor, Raster raster, Rectangle rect, int xPeriod, int yPeriod) {
        UnpackedImageData uid = accessor.getPixels(raster, rect, 0, false);
        byte[][] byteData = uid.getByteData();
        int pixelStride = uid.pixelStride * xPeriod;
        int lineStride = uid.lineStride * yPeriod;
        int[] offsets = uid.bandOffsets;
        for (int b = 0; b < this.numBands; ++b) {
            byte[] data = byteData[b];
            int lineOffset = offsets[b];
            int[] bin = new int[this.numBins[b]];
            double low = this.lowValue[b];
            double high = this.highValue[b];
            double bwidth = this.binWidth[b];
            for (int h = 0; h < rect.height; h += yPeriod) {
                int pixelOffset = lineOffset;
                lineOffset += lineStride;
                for (int w = 0; w < rect.width; w += xPeriod) {
                    int i;
                    int d = data[pixelOffset] & 0xFF;
                    pixelOffset += pixelStride;
                    if (!((double)d >= low) || !((double)d < high)) continue;
                    int n = i = (int)(((double)d - low) / bwidth);
                    bin[n] = bin[n] + 1;
                }
            }
            this.mergeBins(b, bin);
        }
    }

    private void countPixelsUShort(PixelAccessor accessor, Raster raster, Rectangle rect, int xPeriod, int yPeriod) {
        UnpackedImageData uid = accessor.getPixels(raster, rect, 1, false);
        short[][] shortData = uid.getShortData();
        int pixelStride = uid.pixelStride * xPeriod;
        int lineStride = uid.lineStride * yPeriod;
        int[] offsets = uid.bandOffsets;
        for (int b = 0; b < this.numBands; ++b) {
            short[] data = shortData[b];
            int lineOffset = offsets[b];
            int[] bin = new int[this.numBins[b]];
            double low = this.lowValue[b];
            double high = this.highValue[b];
            double bwidth = this.binWidth[b];
            for (int h = 0; h < rect.height; h += yPeriod) {
                int pixelOffset = lineOffset;
                lineOffset += lineStride;
                for (int w = 0; w < rect.width; w += xPeriod) {
                    int i;
                    int d = data[pixelOffset] & 0xFFFF;
                    pixelOffset += pixelStride;
                    if (!((double)d >= low) || !((double)d < high)) continue;
                    int n = i = (int)(((double)d - low) / bwidth);
                    bin[n] = bin[n] + 1;
                }
            }
            this.mergeBins(b, bin);
        }
    }

    private void countPixelsShort(PixelAccessor accessor, Raster raster, Rectangle rect, int xPeriod, int yPeriod) {
        UnpackedImageData uid = accessor.getPixels(raster, rect, 2, false);
        short[][] shortData = uid.getShortData();
        int pixelStride = uid.pixelStride * xPeriod;
        int lineStride = uid.lineStride * yPeriod;
        int[] offsets = uid.bandOffsets;
        for (int b = 0; b < this.numBands; ++b) {
            short[] data = shortData[b];
            int lineOffset = offsets[b];
            int[] bin = new int[this.numBins[b]];
            double low = this.lowValue[b];
            double high = this.highValue[b];
            double bwidth = this.binWidth[b];
            for (int h = 0; h < rect.height; h += yPeriod) {
                int pixelOffset = lineOffset;
                lineOffset += lineStride;
                for (int w = 0; w < rect.width; w += xPeriod) {
                    int i;
                    short d = data[pixelOffset];
                    pixelOffset += pixelStride;
                    if (!((double)d >= low) || !((double)d < high)) continue;
                    int n = i = (int)(((double)d - low) / bwidth);
                    bin[n] = bin[n] + 1;
                }
            }
            this.mergeBins(b, bin);
        }
    }

    private void countPixelsInt(PixelAccessor accessor, Raster raster, Rectangle rect, int xPeriod, int yPeriod) {
        UnpackedImageData uid = accessor.getPixels(raster, rect, 3, false);
        int[][] intData = uid.getIntData();
        int pixelStride = uid.pixelStride * xPeriod;
        int lineStride = uid.lineStride * yPeriod;
        int[] offsets = uid.bandOffsets;
        for (int b = 0; b < this.numBands; ++b) {
            int[] data = intData[b];
            int lineOffset = offsets[b];
            int[] bin = new int[this.numBins[b]];
            double low = this.lowValue[b];
            double high = this.highValue[b];
            double bwidth = this.binWidth[b];
            for (int h = 0; h < rect.height; h += yPeriod) {
                int pixelOffset = lineOffset;
                lineOffset += lineStride;
                for (int w = 0; w < rect.width; w += xPeriod) {
                    int i;
                    int d = data[pixelOffset];
                    pixelOffset += pixelStride;
                    if (!((double)d >= low) || !((double)d < high)) continue;
                    int n = i = (int)(((double)d - low) / bwidth);
                    bin[n] = bin[n] + 1;
                }
            }
            this.mergeBins(b, bin);
        }
    }

    private void countPixelsFloat(PixelAccessor accessor, Raster raster, Rectangle rect, int xPeriod, int yPeriod) {
        UnpackedImageData uid = accessor.getPixels(raster, rect, 4, false);
        float[][] floatData = uid.getFloatData();
        int pixelStride = uid.pixelStride * xPeriod;
        int lineStride = uid.lineStride * yPeriod;
        int[] offsets = uid.bandOffsets;
        for (int b = 0; b < this.numBands; ++b) {
            float[] data = floatData[b];
            int lineOffset = offsets[b];
            int[] bin = new int[this.numBins[b]];
            double low = this.lowValue[b];
            double high = this.highValue[b];
            double bwidth = this.binWidth[b];
            for (int h = 0; h < rect.height; h += yPeriod) {
                int pixelOffset = lineOffset;
                lineOffset += lineStride;
                for (int w = 0; w < rect.width; w += xPeriod) {
                    int i;
                    float d = data[pixelOffset];
                    pixelOffset += pixelStride;
                    if (!((double)d >= low) || !((double)d < high)) continue;
                    int n = i = (int)(((double)d - low) / bwidth);
                    bin[n] = bin[n] + 1;
                }
            }
            this.mergeBins(b, bin);
        }
    }

    private void countPixelsDouble(PixelAccessor accessor, Raster raster, Rectangle rect, int xPeriod, int yPeriod) {
        UnpackedImageData uid = accessor.getPixels(raster, rect, 5, false);
        double[][] doubleData = uid.getDoubleData();
        int pixelStride = uid.pixelStride * xPeriod;
        int lineStride = uid.lineStride * yPeriod;
        int[] offsets = uid.bandOffsets;
        for (int b = 0; b < this.numBands; ++b) {
            double[] data = doubleData[b];
            int lineOffset = offsets[b];
            int[] bin = new int[this.numBins[b]];
            double low = this.lowValue[b];
            double high = this.highValue[b];
            double bwidth = this.binWidth[b];
            for (int h = 0; h < rect.height; h += yPeriod) {
                int pixelOffset = lineOffset;
                lineOffset += lineStride;
                for (int w = 0; w < rect.width; w += xPeriod) {
                    int i;
                    double d = data[pixelOffset];
                    pixelOffset += pixelStride;
                    if (!(d >= low) || !(d < high)) continue;
                    int n = i = (int)((d - low) / bwidth);
                    bin[n] = bin[n] + 1;
                }
            }
            this.mergeBins(b, bin);
        }
    }

    private int startPosition(int pos, int start, int Period) {
        int t = (pos - start) % Period;
        return t == 0 ? pos : pos + (Period - t);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void mergeBins(int band, int[] bin) {
        this.getBins();
        int[][] nArray = this.bins;
        synchronized (this.bins) {
            int[] b = this.bins[band];
            int length = b.length;
            for (int i = 0; i < length; ++i) {
                int n = i;
                b[n] = b[n] + bin[i];
            }
            // ** MonitorExit[var3_3] (shouldn't be in output)
            return;
        }
    }

    public double[] getMoment(int moment, boolean isAbsolute, boolean isCentral) {
        if (moment < 1) {
            throw new IllegalArgumentException(JaiI18N.getString("Histogram6"));
        }
        if ((moment == 1 || isCentral) && this.mean == null) {
            this.getMean();
        }
        if (moment == 1 && !isAbsolute && !isCentral) {
            return this.mean;
        }
        double[] moments = new double[this.numBands];
        if (moment == 1 && isCentral) {
            for (int band = 0; band < this.numBands; ++band) {
                moments[band] = 0.0;
            }
        } else {
            this.getTotals();
            for (int band = 0; band < this.numBands; ++band) {
                int[] counts = this.getBins(band);
                int nBins = this.numBins[band];
                double level = this.getLowValue(band);
                double bw = this.binWidth[band];
                double total = this.totals[band];
                double mmt = 0.0;
                if (isCentral) {
                    int b;
                    double mu = this.mean[band];
                    if (isAbsolute && moment % 2 == 0) {
                        for (b = 0; b < nBins; ++b) {
                            mmt += Math.pow(level - mu, moment) * (double)counts[b] / total;
                            level += bw;
                        }
                    } else {
                        for (b = 0; b < nBins; ++b) {
                            mmt += Math.abs(Math.pow(level - mu, moment)) * (double)counts[b] / total;
                            level += bw;
                        }
                    }
                } else if (isAbsolute && moment % 2 != 0) {
                    for (int b = 0; b < nBins; ++b) {
                        mmt += Math.abs(Math.pow(level, moment)) * (double)counts[b] / total;
                        level += bw;
                    }
                } else {
                    for (int b = 0; b < nBins; ++b) {
                        mmt += Math.pow(level, moment) * (double)counts[b] / total;
                        level += bw;
                    }
                }
                moments[band] = mmt;
            }
        }
        return moments;
    }

    public double[] getStandardDeviation() {
        this.getMean();
        double[] variance = this.getMoment(2, false, false);
        double[] stdev = new double[this.numBands];
        for (int i = 0; i < variance.length; ++i) {
            stdev[i] = Math.sqrt(variance[i] - this.mean[i] * this.mean[i]);
        }
        return stdev;
    }

    public double[] getEntropy() {
        this.getTotals();
        double log2 = Math.log(2.0);
        double[] entropy = new double[this.numBands];
        for (int band = 0; band < this.numBands; ++band) {
            int[] counts = this.getBins(band);
            int nBins = this.numBins[band];
            double total = this.totals[band];
            double H = 0.0;
            for (int b = 0; b < nBins; ++b) {
                double p = (double)counts[b] / total;
                if (p == 0.0) continue;
                H -= p * (Math.log(p) / log2);
            }
            entropy[band] = H;
        }
        return entropy;
    }

    public Histogram getSmoothed(boolean isWeighted, int k) {
        if (k < 0) {
            throw new IllegalArgumentException(JaiI18N.getString("Histogram7"));
        }
        if (k == 0) {
            return this;
        }
        Histogram smoothedHistogram = new Histogram(this.getNumBins(), this.getLowValue(), this.getHighValue());
        int[][] smoothedBins = smoothedHistogram.getBins();
        this.getTotals();
        double[] weights = null;
        if (isWeighted) {
            int i;
            int numWeights = 2 * k + 1;
            double denom = numWeights * numWeights;
            weights = new double[numWeights];
            for (i = 0; i <= k; ++i) {
                weights[i] = (double)(i + 1) / denom;
            }
            for (i = k + 1; i < numWeights; ++i) {
                weights[i] = weights[numWeights - 1 - i];
            }
        }
        for (int band = 0; band < this.numBands; ++band) {
            int max;
            int min;
            int b;
            int[] counts = this.getBins(band);
            int[] smoothedCounts = smoothedBins[band];
            int nBins = smoothedHistogram.getNumBins(band);
            int sum = 0;
            if (isWeighted) {
                for (b = 0; b < nBins; ++b) {
                    min = Math.max(b - k, 0);
                    max = Math.min(b + k, nBins);
                    int offset = k > b ? k - b : 0;
                    double acc = 0.0;
                    double weightTotal = 0.0;
                    for (int i = min; i < max; ++i) {
                        double w = weights[offset++];
                        acc += (double)counts[i] * w;
                        weightTotal += w;
                    }
                    smoothedCounts[b] = (int)(acc / weightTotal + 0.5);
                    sum += smoothedCounts[b];
                }
            } else {
                for (b = 0; b < nBins; ++b) {
                    min = Math.max(b - k, 0);
                    max = Math.min(b + k, nBins);
                    int acc = 0;
                    for (int i = min; i < max; ++i) {
                        acc += counts[i];
                    }
                    smoothedCounts[b] = (int)((double)acc / (double)(max - min + 1) + 0.5);
                    sum += smoothedCounts[b];
                }
            }
            double factor = (double)this.totals[band] / (double)sum;
            for (int b2 = 0; b2 < nBins; ++b2) {
                smoothedCounts[b2] = (int)((double)smoothedCounts[b2] * factor + 0.5);
            }
        }
        return smoothedHistogram;
    }

    public Histogram getGaussianSmoothed(double standardDeviation) {
        if (standardDeviation < 0.0) {
            throw new IllegalArgumentException(JaiI18N.getString("Histogram8"));
        }
        if (standardDeviation == 0.0) {
            return this;
        }
        Histogram smoothedHistogram = new Histogram(this.getNumBins(), this.getLowValue(), this.getHighValue());
        int[][] smoothedBins = smoothedHistogram.getBins();
        this.getTotals();
        int numWeights = (int)(5.16 * standardDeviation + 0.5);
        if (numWeights % 2 == 0) {
            ++numWeights;
        }
        double[] weights = new double[numWeights];
        int m = numWeights / 2;
        double var = standardDeviation * standardDeviation;
        double gain = 1.0 / Math.sqrt(Math.PI * 2 * var);
        double exp = -1.0 / (2.0 * var);
        for (int i = m; i < numWeights; ++i) {
            double del = i - m;
            double d = gain * Math.exp(exp * del * del);
            weights[numWeights - 1 - i] = d;
            weights[i] = d;
        }
        for (int band = 0; band < this.numBands; ++band) {
            int[] counts = this.getBins(band);
            int[] smoothedCounts = smoothedBins[band];
            int nBins = smoothedHistogram.getNumBins(band);
            int sum = 0;
            for (int b = 0; b < nBins; ++b) {
                int min = Math.max(b - m, 0);
                int max = Math.min(b + m, nBins);
                int offset = m > b ? m - b : 0;
                double acc = 0.0;
                double weightTotal = 0.0;
                for (int i = min; i < max; ++i) {
                    double w = weights[offset++];
                    acc += (double)counts[i] * w;
                    weightTotal += w;
                }
                smoothedCounts[b] = (int)(acc / weightTotal + 0.5);
                sum += smoothedCounts[b];
            }
            double factor = (double)this.totals[band] / (double)sum;
            for (int b = 0; b < nBins; ++b) {
                smoothedCounts[b] = (int)((double)smoothedCounts[b] * factor + 0.5);
            }
        }
        return smoothedHistogram;
    }

    public double[] getPTileThreshold(double p) {
        if (p <= 0.0 || p >= 1.0) {
            throw new IllegalArgumentException(JaiI18N.getString("Histogram9"));
        }
        double[] thresholds = new double[this.numBands];
        this.getTotals();
        for (int band = 0; band < this.numBands; ++band) {
            int nBins = this.numBins[band];
            int[] counts = this.getBins(band);
            int totalCount = this.totals[band];
            int numBinWidths = 0;
            int count = counts[0];
            int idx = 0;
            while ((double)count / (double)totalCount < p) {
                ++numBinWidths;
                count += counts[++idx];
            }
            thresholds[band] = this.getLowValue(band) + (double)numBinWidths * this.binWidth[band];
        }
        return thresholds;
    }

    public double[] getModeThreshold(double power) {
        double[] thresholds = new double[this.numBands];
        this.getTotals();
        for (int band = 0; band < this.numBands; ++band) {
            int nBins = this.numBins[band];
            int[] counts = this.getBins(band);
            int mode1 = 0;
            int mode1Count = counts[0];
            for (int b = 1; b < nBins; ++b) {
                if (counts[b] <= mode1Count) continue;
                mode1 = b;
                mode1Count = counts[b];
            }
            int mode2 = -1;
            double mode2count = 0.0;
            for (int b = 0; b < nBins; ++b) {
                double d = (double)counts[b] * Math.pow(Math.abs(b - mode1), power);
                if (!(d > mode2count)) continue;
                mode2 = b;
                mode2count = d;
            }
            int min = mode1;
            int minCount = counts[mode1];
            for (int b = mode1 + 1; b <= mode2; ++b) {
                if (counts[b] >= minCount) continue;
                min = b;
                minCount = counts[b];
            }
            thresholds[band] = (int)((double)(mode1 + mode2) / 2.0 + 0.5);
        }
        return thresholds;
    }

    public double[] getIterativeThreshold() {
        double[] thresholds = new double[this.numBands];
        this.getTotals();
        for (int band = 0; band < this.numBands; ++band) {
            int nBins = this.numBins[band];
            int[] counts = this.getBins(band);
            double bw = this.binWidth[band];
            double threshold = 0.5 * (this.getLowValue(band) + this.getHighValue(band));
            double mid1 = 0.5 * (this.getLowValue(band) + threshold);
            double mid2 = 0.5 * (threshold + this.getHighValue(band));
            if (this.totals[band] != 0) {
                double mean2;
                double mean1;
                int countDown = 1000;
                do {
                    thresholds[band] = threshold;
                    double total = this.totals[band];
                    double level = this.getLowValue(band);
                    mean1 = 0.0;
                    mean2 = 0.0;
                    int count1 = 0;
                    for (int b = 0; b < nBins; ++b) {
                        if (level <= threshold) {
                            int c = counts[b];
                            mean1 += (double)c * level;
                            count1 += c;
                        } else {
                            mean2 += (double)counts[b] * level;
                        }
                        level += bw;
                    }
                    mean1 = count1 != 0 ? (mean1 /= (double)count1) : mid1;
                    if (total != (double)count1) {
                        mean2 /= total - (double)count1;
                        continue;
                    }
                    mean2 = mid2;
                } while (Math.abs((threshold = 0.5 * (mean1 + mean2)) - thresholds[band]) > 1.0E-6 && --countDown > 0);
                continue;
            }
            thresholds[band] = threshold;
        }
        return thresholds;
    }

    public double[] getMaxVarianceThreshold() {
        double[] thresholds = new double[this.numBands];
        this.getTotals();
        this.getMean();
        double[] variance = this.getMoment(2, false, false);
        for (int band = 0; band < this.numBands; ++band) {
            double lv;
            int nBins = this.numBins[band];
            int[] counts = this.getBins(band);
            double total = this.totals[band];
            double mBand = this.mean[band];
            double bw = this.binWidth[band];
            double prob0 = 0.0;
            double mean0 = 0.0;
            double level = lv = this.getLowValue(band);
            double maxRatio = -1.7976931348623157E308;
            int maxIndex = 0;
            int runLength = 0;
            int t = 0;
            while (t < nBins) {
                double p = (double)counts[t] / total;
                if ((prob0 += p) != 0.0) {
                    double m0 = (mean0 += p * level) / prob0;
                    double prob1 = 1.0 - prob0;
                    if (prob1 != 0.0) {
                        double m1 = (mBand - mean0) / prob1;
                        double var0 = 0.0;
                        double g = lv;
                        int b = 0;
                        while (b <= t) {
                            double del = g - m0;
                            var0 += del * del * (double)counts[b];
                            ++b;
                            g += bw;
                        }
                        var0 /= total;
                        double var1 = 0.0;
                        int b2 = t + 1;
                        while (b2 < nBins) {
                            double del = g - m1;
                            var1 += del * del * (double)counts[b2];
                            ++b2;
                            g += bw;
                        }
                        if (var0 == 0.0 && (var1 /= total) == 0.0 && m1 != 0.0) {
                            maxIndex = (int)(((m0 + m1) / 2.0 - this.getLowValue(band)) / bw + 0.5);
                            runLength = 0;
                            break;
                        }
                        if (!(var0 / prob0 < 0.5) && !(var1 / prob1 < 0.5)) {
                            double mdel = m0 - m1;
                            double ratio = prob0 * prob1 * mdel * mdel / (var0 + var1);
                            if (ratio > maxRatio) {
                                maxRatio = ratio;
                                maxIndex = t;
                                runLength = 0;
                            } else if (ratio == maxRatio) {
                                ++runLength;
                            }
                        }
                    }
                }
                ++t;
                level += bw;
            }
            thresholds[band] = this.getLowValue(band) + ((double)maxIndex + (double)runLength / 2.0 + 0.5) * bw;
        }
        return thresholds;
    }

    public double[] getMaxEntropyThreshold() {
        double[] thresholds = new double[this.numBands];
        this.getTotals();
        double[] entropy = this.getEntropy();
        double log2 = Math.log(2.0);
        for (int band = 0; band < this.numBands; ++band) {
            int nBins = this.numBins[band];
            int[] counts = this.getBins(band);
            double total = this.totals[band];
            double H = entropy[band];
            double P1 = 0.0;
            double H1 = 0.0;
            double maxCriterion = -1.7976931348623157E308;
            int maxIndex = 0;
            int runLength = 0;
            for (int t = 0; t < nBins; ++t) {
                double p = (double)counts[t] / total;
                if (p == 0.0) continue;
                P1 += p;
                H1 -= p * Math.log(p) / log2;
                double max1 = 0.0;
                for (int b = 0; b <= t; ++b) {
                    if (!((double)counts[b] > max1)) continue;
                    max1 = counts[b];
                }
                if (max1 == 0.0) continue;
                double max2 = 0.0;
                for (int b = t + 1; b < nBins; ++b) {
                    if (!((double)counts[b] > max2)) continue;
                    max2 = counts[b];
                }
                if (max2 == 0.0) continue;
                double ratio = H1 / H;
                double criterion = ratio * Math.log(P1) / Math.log(max1 / total) + (1.0 - ratio) * Math.log(1.0 - P1) / Math.log(max2 / total);
                if (criterion > maxCriterion) {
                    maxCriterion = criterion;
                    maxIndex = t;
                    runLength = 0;
                    continue;
                }
                if (criterion != maxCriterion) continue;
                ++runLength;
            }
            thresholds[band] = this.getLowValue(band) + ((double)maxIndex + (double)runLength / 2.0 + 0.5) * this.binWidth[band];
        }
        return thresholds;
    }

    public double[] getMinErrorThreshold() {
        double[] thresholds = new double[this.numBands];
        this.getTotals();
        this.getMean();
        for (int band = 0; band < this.numBands; ++band) {
            int nBins = this.numBins[band];
            int[] counts = this.getBins(band);
            double total = this.totals[band];
            double lv = this.getLowValue(band);
            double bw = this.binWidth[band];
            int total1 = 0;
            int total2 = this.totals[band];
            double sum1 = 0.0;
            double sum2 = this.mean[band] * total;
            double level = lv;
            double minCriterion = Double.MAX_VALUE;
            int minIndex = 0;
            int runLength = 0;
            double J0 = Double.MAX_VALUE;
            double J1 = Double.MAX_VALUE;
            double J2 = Double.MAX_VALUE;
            int Jcount = 0;
            int t = 0;
            while (t < nBins) {
                int c = counts[t];
                total2 -= c;
                double incr = level * (double)c;
                sum2 -= incr;
                if ((total1 += c) != 0 && (sum1 += incr) != 0.0) {
                    if (total2 == 0 || sum2 == 0.0) break;
                    double m1 = sum1 / (double)total1;
                    double m2 = sum2 / (double)total2;
                    double s1 = 0.0;
                    double g = lv;
                    int b = 0;
                    while (b <= t) {
                        double v = g - m1;
                        s1 += (double)counts[b] * v * v;
                        ++b;
                        g += bw;
                    }
                    if (!((s1 /= (double)total1) < 0.5)) {
                        double s2 = 0.0;
                        int b2 = t + 1;
                        while (b2 < nBins) {
                            double v = g - m2;
                            s2 += (double)counts[b2] * v * v;
                            ++b2;
                            g += bw;
                        }
                        if (!((s2 /= (double)total2) < 0.5)) {
                            double P1 = (double)total1 / total;
                            double P2 = (double)total2 / total;
                            double J = 1.0 + P1 * Math.log(s1) + P2 * Math.log(s2) - 2.0 * (P1 * Math.log(P1) + P2 * Math.log(P2));
                            J0 = J1;
                            J1 = J2;
                            J2 = J;
                            if (++Jcount >= 3 && J1 <= J0 && J1 <= J2) {
                                if (J1 < minCriterion) {
                                    minCriterion = J1;
                                    minIndex = t - 1;
                                    runLength = 0;
                                } else if (J1 == minCriterion) {
                                    ++runLength;
                                }
                            }
                        }
                    }
                }
                ++t;
                level += bw;
            }
            thresholds[band] = minIndex == 0 ? this.mean[band] : this.getLowValue(band) + ((double)minIndex + (double)runLength / 2.0 + 0.5) * bw;
        }
        return thresholds;
    }

    public double[] getMinFuzzinessThreshold() {
        double[] thresholds = new double[this.numBands];
        this.getTotals();
        this.getMean();
        for (int band = 0; band < this.numBands; ++band) {
            double lv;
            int nBins = this.numBins[band];
            int[] counts = this.getBins(band);
            double total = this.totals[band];
            double bw = this.binWidth[band];
            int total1 = 0;
            int total2 = this.totals[band];
            double sum1 = 0.0;
            double sum2 = this.mean[band] * total;
            double level = lv = this.getLowValue(band);
            double C = this.getHighValue(band) - lv;
            double minCriterion = Double.MAX_VALUE;
            int minIndex = 0;
            int runLength = 0;
            int t = 0;
            while (t < nBins) {
                int c = counts[t];
                double incr = level * (double)c;
                sum1 += incr;
                sum2 -= incr;
                if ((total1 += c) != 0 && (total2 -= c) != 0) {
                    double m1 = sum1 / (double)total1;
                    double m2 = sum2 / (double)total2;
                    double g = lv;
                    double E = 0.0;
                    int b = 0;
                    while (b < nBins) {
                        double u = b <= t ? 1.0 / (1.0 + Math.abs(g - m1) / C) : 1.0 / (1.0 + Math.abs(g - m2) / C);
                        double v = 1.0 - u;
                        E += (-u * Math.log(u) - v * Math.log(v)) * ((double)counts[b] / total);
                        ++b;
                        g += bw;
                    }
                    if (E < minCriterion) {
                        minCriterion = E;
                        minIndex = t;
                        runLength = 0;
                    } else if (E == minCriterion) {
                        ++runLength;
                    }
                }
                ++t;
                level += bw;
            }
            thresholds[band] = lv + ((double)minIndex + (double)runLength / 2.0 + 0.5) * bw;
        }
        return thresholds;
    }
}

