/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.ColorQuantizerOpImage;
import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROI;
import javax.media.jai.iterator.RandomIter;
import javax.media.jai.iterator.RandomIterFactory;

public class NeuQuantOpImage
extends ColorQuantizerOpImage {
    protected static final int prime1 = 499;
    protected static final int prime2 = 491;
    protected static final int prime3 = 487;
    protected static final int prime4 = 503;
    protected static final int minpicturebytes = 1509;
    private int ncycles;
    private final int maxnetpos;
    private final int netbiasshift = 4;
    private final int intbiasshift = 16;
    private final int intbias = 65536;
    private final int gammashift = 10;
    private final int gamma = 1024;
    private final int betashift = 10;
    private final int beta = 64;
    private final int betagamma = 65536;
    private final int initrad;
    private final int radiusbiasshift = 6;
    private final int radiusbias = 64;
    private final int initradius;
    private final int radiusdec = 30;
    private final int alphabiasshift = 10;
    private final int initalpha = 1024;
    private int alphadec;
    private final int radbiasshift = 8;
    private final int radbias = 256;
    private final int alpharadbshift = 18;
    private final int alpharadbias = 262144;
    private int[][] network;
    private int[] netindex;
    private int[] bias;
    private int[] freq;
    private int[] radpower;

    public NeuQuantOpImage(RenderedImage source, Map config, ImageLayout layout, int maxColorNum, int upperBound, ROI roi, int xPeriod, int yPeriod) {
        super(source, config, layout, maxColorNum, roi, xPeriod, yPeriod);
        this.maxnetpos = this.maxColorNum - 1;
        this.netbiasshift = 4;
        this.intbiasshift = 16;
        this.intbias = 65536;
        this.gammashift = 10;
        this.gamma = 1024;
        this.betashift = 10;
        this.beta = 64;
        this.betagamma = 65536;
        this.initrad = this.maxColorNum >> 3;
        this.radiusbiasshift = 6;
        this.radiusbias = 64;
        this.initradius = this.initrad * 64;
        this.radiusdec = 30;
        this.alphabiasshift = 10;
        this.initalpha = 1024;
        this.radbiasshift = 8;
        this.radbias = 256;
        this.alpharadbshift = 18;
        this.alpharadbias = 262144;
        this.netindex = new int[256];
        this.bias = new int[this.maxColorNum];
        this.freq = new int[this.maxColorNum];
        this.radpower = new int[this.initrad];
        this.colorMap = null;
        this.ncycles = upperBound;
    }

    protected synchronized void train() {
        this.network = new int[this.maxColorNum][];
        for (int i = 0; i < this.maxColorNum; ++i) {
            this.network[i] = new int[4];
            int[] p = this.network[i];
            p[1] = p[2] = (i << 12) / this.maxColorNum;
            p[0] = p[2];
            this.freq[i] = 65536 / this.maxColorNum;
            this.bias[i] = 0;
        }
        PlanarImage source = this.getSourceImage(0);
        Rectangle rect = source.getBounds();
        if (this.roi != null) {
            rect = this.roi.getBounds();
        }
        RandomIter iterator = RandomIterFactory.create(source, rect);
        int samplefac = this.xPeriod * this.yPeriod;
        int startX = rect.x / this.xPeriod;
        int startY = rect.y / this.yPeriod;
        int offsetX = rect.x % this.xPeriod;
        int offsetY = rect.y % this.yPeriod;
        int pixelsPerLine = (rect.width - 1) / this.xPeriod + 1;
        int numSamples = pixelsPerLine * ((rect.height - 1) / this.yPeriod + 1);
        if (numSamples < 1509) {
            samplefac = 1;
        }
        this.alphadec = 30 + (samplefac - 1) / 3;
        int pix = 0;
        int delta = numSamples / this.ncycles;
        int alpha = 1024;
        int radius = this.initradius;
        int rad = radius >> 6;
        if (rad <= 1) {
            rad = 0;
        }
        for (int i = 0; i < rad; ++i) {
            this.radpower[i] = alpha * ((rad * rad - i * i) * 256 / (rad * rad));
        }
        int step = numSamples < 1509 ? 3 : (numSamples % 499 != 0 ? 1497 : (numSamples % 491 != 0 ? 1473 : (numSamples % 487 != 0 ? 1461 : 1509)));
        int[] pixel = new int[3];
        int i = 0;
        while (i < numSamples) {
            int y = (pix / pixelsPerLine + startY) * this.yPeriod + offsetY;
            int x = (pix % pixelsPerLine + startX) * this.xPeriod + offsetX;
            try {
                iterator.getPixel(x, y, pixel);
            }
            catch (Exception e) {
                continue;
            }
            int b = pixel[2] << 4;
            int g = pixel[1] << 4;
            int r = pixel[0] << 4;
            int j = this.contest(b, g, r);
            this.altersingle(alpha, j, b, g, r);
            if (rad != 0) {
                this.alterneigh(rad, j, b, g, r);
            }
            if ((pix += step) >= numSamples) {
                pix -= numSamples;
            }
            if (++i % delta != 0) continue;
            alpha -= alpha / this.alphadec;
            if ((rad = (radius -= radius / 30) >> 6) <= 1) {
                rad = 0;
            }
            for (j = 0; j < rad; ++j) {
                this.radpower[j] = alpha * ((rad * rad - j * j) * 256 / (rad * rad));
            }
        }
        this.unbiasnet();
        this.inxbuild();
        this.createLUT();
        this.setProperty("LUT", this.colorMap);
        this.setProperty("JAI.LookupTable", this.colorMap);
    }

    private void createLUT() {
        int i;
        this.colorMap = new LookupTableJAI(new byte[3][this.maxColorNum]);
        byte[][] map = this.colorMap.getByteData();
        int[] index = new int[this.maxColorNum];
        for (i = 0; i < this.maxColorNum; ++i) {
            index[this.network[i][3]] = i;
        }
        for (i = 0; i < this.maxColorNum; ++i) {
            int j = index[i];
            map[2][i] = (byte)this.network[j][0];
            map[1][i] = (byte)this.network[j][1];
            map[0][i] = (byte)this.network[j][2];
        }
    }

    private void inxbuild() {
        int previouscol = 0;
        int startpos = 0;
        for (int i = 0; i < this.maxColorNum; ++i) {
            int[] q;
            int j;
            int[] p = this.network[i];
            int smallpos = i;
            int smallval = p[1];
            for (j = i + 1; j < this.maxColorNum; ++j) {
                q = this.network[j];
                if (q[1] >= smallval) continue;
                smallpos = j;
                smallval = q[1];
            }
            q = this.network[smallpos];
            if (i != smallpos) {
                j = q[0];
                q[0] = p[0];
                p[0] = j;
                j = q[1];
                q[1] = p[1];
                p[1] = j;
                j = q[2];
                q[2] = p[2];
                p[2] = j;
                j = q[3];
                q[3] = p[3];
                p[3] = j;
            }
            if (smallval == previouscol) continue;
            this.netindex[previouscol] = startpos + i >> 1;
            for (j = previouscol + 1; j < smallval; ++j) {
                this.netindex[j] = i;
            }
            previouscol = smallval;
            startpos = i;
        }
        this.netindex[previouscol] = startpos + this.maxnetpos >> 1;
        for (int j = previouscol + 1; j < 256; ++j) {
            this.netindex[j] = this.maxnetpos;
        }
    }

    protected byte findNearestEntry(int r, int g, int b) {
        int bestd = 1000;
        int best = -1;
        int i = this.netindex[g];
        int j = i - 1;
        while (i < this.maxColorNum || j >= 0) {
            int a;
            int dist;
            int[] p;
            if (i < this.maxColorNum) {
                p = this.network[i];
                dist = p[1] - g;
                if (dist >= bestd) {
                    i = this.maxColorNum;
                } else {
                    ++i;
                    if (dist < 0) {
                        dist = -dist;
                    }
                    if ((a = p[0] - b) < 0) {
                        a = -a;
                    }
                    if ((dist += a) < bestd) {
                        a = p[2] - r;
                        if (a < 0) {
                            a = -a;
                        }
                        if ((dist += a) < bestd) {
                            bestd = dist;
                            best = p[3];
                        }
                    }
                }
            }
            if (j < 0) continue;
            p = this.network[j];
            dist = g - p[1];
            if (dist >= bestd) {
                j = -1;
                continue;
            }
            --j;
            if (dist < 0) {
                dist = -dist;
            }
            if ((a = p[0] - b) < 0) {
                a = -a;
            }
            if ((dist += a) >= bestd) continue;
            a = p[2] - r;
            if (a < 0) {
                a = -a;
            }
            if ((dist += a) >= bestd) continue;
            bestd = dist;
            best = p[3];
        }
        return (byte)best;
    }

    private void unbiasnet() {
        for (int i = 0; i < this.maxColorNum; ++i) {
            int[] nArray = this.network[i];
            nArray[0] = nArray[0] >> 4;
            int[] nArray2 = this.network[i];
            nArray2[1] = nArray2[1] >> 4;
            int[] nArray3 = this.network[i];
            nArray3[2] = nArray3[2] >> 4;
            this.network[i][3] = i;
        }
    }

    private void alterneigh(int rad, int i, int b, int g, int r) {
        int hi;
        int lo = i - rad;
        if (lo < -1) {
            lo = -1;
        }
        if ((hi = i + rad) > this.maxColorNum) {
            hi = this.maxColorNum;
        }
        int j = i + 1;
        int k = i - 1;
        int m = 1;
        while (j < hi || k > lo) {
            int[] p;
            int a = this.radpower[m++];
            if (j < hi) {
                p = this.network[j++];
                p[0] = p[0] - a * (p[0] - b) / 262144;
                p[1] = p[1] - a * (p[1] - g) / 262144;
                p[2] = p[2] - a * (p[2] - r) / 262144;
            }
            if (k <= lo) continue;
            p = this.network[k--];
            p[0] = p[0] - a * (p[0] - b) / 262144;
            p[1] = p[1] - a * (p[1] - g) / 262144;
            p[2] = p[2] - a * (p[2] - r) / 262144;
        }
    }

    private void altersingle(int alpha, int i, int b, int g, int r) {
        int[] n = this.network[i];
        n[0] = n[0] - alpha * (n[0] - b) / 1024;
        n[1] = n[1] - alpha * (n[1] - g) / 1024;
        n[2] = n[2] - alpha * (n[2] - r) / 1024;
    }

    private int contest(int b, int g, int r) {
        int bestpos;
        int bestd;
        int bestbiasd = bestd = Integer.MAX_VALUE;
        int bestbiaspos = bestpos = -1;
        int i = 0;
        while (i < this.maxColorNum) {
            int biasdist;
            int a;
            int[] n = this.network[i];
            int dist = n[0] - b;
            if (dist < 0) {
                dist = -dist;
            }
            if ((a = n[1] - g) < 0) {
                a = -a;
            }
            dist += a;
            a = n[2] - r;
            if (a < 0) {
                a = -a;
            }
            if ((dist += a) < bestd) {
                bestd = dist;
                bestpos = i;
            }
            if ((biasdist = dist - (this.bias[i] >> 12)) < bestbiasd) {
                bestbiasd = biasdist;
                bestbiaspos = i;
            }
            int betafreq = this.freq[i] >> 10;
            int n2 = i;
            this.freq[n2] = this.freq[n2] - betafreq;
            int n3 = i++;
            this.bias[n3] = this.bias[n3] + (betafreq << 10);
        }
        int n = bestpos;
        this.freq[n] = this.freq[n] + 64;
        int n4 = bestpos;
        this.bias[n4] = this.bias[n4] - 65536;
        return bestbiaspos;
    }
}

