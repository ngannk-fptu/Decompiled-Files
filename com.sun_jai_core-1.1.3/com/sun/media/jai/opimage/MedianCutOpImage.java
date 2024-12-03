/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.ColorQuantizerOpImage;
import com.sun.media.jai.opimage.Cube;
import com.sun.media.jai.opimage.HistogramHash;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.PixelAccessor;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROI;
import javax.media.jai.ROIShape;
import javax.media.jai.UnpackedImageData;

public class MedianCutOpImage
extends ColorQuantizerOpImage {
    private int histogramSize;
    private int[] counts;
    private int[] colors;
    private Cube[] partition;
    private int bits = 8;
    private int mask;
    HistogramHash histogram;

    public MedianCutOpImage(RenderedImage source, Map config, ImageLayout layout, int maxColorNum, int upperBound, ROI roi, int xPeriod, int yPeriod) {
        super(source, config, layout, maxColorNum, roi, xPeriod, yPeriod);
        this.colorMap = null;
        this.histogramSize = upperBound;
    }

    protected synchronized void train() {
        int oldbits;
        PlanarImage source = this.getSourceImage(0);
        if (this.roi == null) {
            this.roi = new ROIShape(source.getBounds());
        }
        int minTileX = source.getMinTileX();
        int maxTileX = source.getMaxTileX();
        int minTileY = source.getMinTileY();
        int maxTileY = source.getMaxTileY();
        int xStart = source.getMinX();
        int yStart = source.getMinY();
        this.histogram = new HistogramHash(this.histogramSize);
        block0: do {
            this.histogram.init();
            oldbits = this.bits;
            this.mask = 255 << 8 - this.bits & 0xFF;
            this.mask = this.mask | this.mask << 8 | this.mask << 16;
            for (int y = minTileY; y <= maxTileY; ++y) {
                for (int x = minTileX; x <= maxTileX; ++x) {
                    Rectangle tileRect = source.getTileRect(x, y);
                    if (!this.roi.intersects(tileRect)) continue;
                    if (this.checkForSkippedTiles && tileRect.x >= xStart && tileRect.y >= yStart) {
                        int offsetX = (this.xPeriod - (tileRect.x - xStart) % this.xPeriod) % this.xPeriod;
                        int offsetY = (this.yPeriod - (tileRect.y - yStart) % this.yPeriod) % this.yPeriod;
                        if (offsetX >= tileRect.width || offsetY >= tileRect.height) continue;
                    }
                    this.computeHistogram(source.getData(tileRect));
                    if (this.histogram.isFull()) break;
                }
                if (this.histogram.isFull()) continue block0;
            }
        } while (oldbits != this.bits);
        this.counts = this.histogram.getCounts();
        this.colors = this.histogram.getColors();
        this.medianCut(this.maxColorNum);
        this.setProperty("LUT", this.colorMap);
        this.setProperty("JAI.LookupTable", this.colorMap);
    }

    private void computeHistogram(Raster source) {
        LinkedList rectList;
        if (!this.isInitialized) {
            this.srcPA = new PixelAccessor(this.getSourceImage(0));
            this.srcSampleType = this.srcPA.sampleType == -1 ? 0 : this.srcPA.sampleType;
            this.isInitialized = true;
        }
        Rectangle srcBounds = this.getSourceImage(0).getBounds().intersection(source.getBounds());
        if (this.roi == null) {
            rectList = new LinkedList();
            rectList.addLast(srcBounds);
        } else {
            rectList = this.roi.getAsRectangleList(srcBounds.x, srcBounds.y, srcBounds.width, srcBounds.height);
            if (rectList == null) {
                return;
            }
        }
        ListIterator iterator = rectList.listIterator(0);
        int xStart = source.getMinX();
        int yStart = source.getMinY();
        while (iterator.hasNext()) {
            Rectangle rect = srcBounds.intersection((Rectangle)iterator.next());
            int tx = rect.x;
            int ty = rect.y;
            rect.x = MedianCutOpImage.startPosition(tx, xStart, this.xPeriod);
            rect.y = MedianCutOpImage.startPosition(ty, yStart, this.yPeriod);
            rect.width = tx + rect.width - rect.x;
            rect.height = ty + rect.height - rect.y;
            if (rect.isEmpty()) continue;
            UnpackedImageData uid = this.srcPA.getPixels(source, rect, this.srcSampleType, false);
            switch (uid.type) {
                case 0: {
                    this.computeHistogramByte(uid);
                }
            }
        }
    }

    private void computeHistogramByte(UnpackedImageData uid) {
        Rectangle rect = uid.rect;
        byte[][] data = uid.getByteData();
        int lineStride = uid.lineStride;
        int pixelStride = uid.pixelStride;
        byte[] rBand = data[0];
        byte[] gBand = data[1];
        byte[] bBand = data[2];
        int lineInc = lineStride * this.yPeriod;
        int pixelInc = pixelStride * this.xPeriod;
        int lastLine = rect.height * lineStride;
        for (int lo = 0; lo < lastLine; lo += lineInc) {
            int lastPixel = lo + rect.width * pixelStride;
            for (int po = lo; po < lastPixel; po += pixelInc) {
                int p = (rBand[po + uid.bandOffsets[0]] & 0xFF) << 16 | (gBand[po + uid.bandOffsets[1]] & 0xFF) << 8 | bBand[po + uid.bandOffsets[2]] & 0xFF;
                if (this.histogram.insert(p & this.mask)) continue;
                --this.bits;
                return;
            }
        }
    }

    public void medianCut(int expectedColorNum) {
        this.partition = new Cube[expectedColorNum];
        int numCubes = 0;
        Cube cube = new Cube();
        int numColors = 0;
        for (int i = 0; i < this.histogramSize; ++i) {
            if (this.counts[i] == 0) continue;
            ++numColors;
            cube.count += this.counts[i];
        }
        cube.lower = 0;
        cube.upper = numColors - 1;
        cube.level = 0;
        this.shrinkCube(cube);
        this.partition[numCubes++] = cube;
        while (numCubes < expectedColorNum) {
            int median;
            int level = 255;
            int splitableCube = -1;
            for (int k = 0; k < numCubes; ++k) {
                if (this.partition[k].lower == this.partition[k].upper || this.partition[k].level >= level) continue;
                level = this.partition[k].level;
                splitableCube = k;
            }
            if (splitableCube == -1) break;
            cube = this.partition[splitableCube];
            level = cube.level;
            int lr = 77 * (cube.rmax - cube.rmin);
            int lg = 150 * (cube.gmax - cube.gmin);
            int lb = 29 * (cube.bmax - cube.bmin);
            int longDimMask = 0;
            if (lr >= lg && lr >= lb) {
                longDimMask = 0xFF0000;
            }
            if (lg >= lr && lg >= lb) {
                longDimMask = 65280;
            }
            if (lb >= lr && lb >= lg) {
                longDimMask = 255;
            }
            this.quickSort(this.colors, cube.lower, cube.upper, longDimMask);
            int count = 0;
            for (median = cube.lower; median <= cube.upper - 1 && count < cube.count / 2; count += this.counts[median], ++median) {
            }
            Cube cubeA = new Cube();
            cubeA.lower = cube.lower;
            cubeA.upper = median - 1;
            cubeA.count = count;
            cubeA.level = cube.level + 1;
            this.shrinkCube(cubeA);
            this.partition[splitableCube] = cubeA;
            Cube cubeB = new Cube();
            cubeB.lower = median;
            cubeB.upper = cube.upper;
            cubeB.count = cube.count - count;
            cubeB.level = cube.level + 1;
            this.shrinkCube(cubeB);
            this.partition[numCubes++] = cubeB;
        }
        this.createLUT(numCubes);
    }

    private void shrinkCube(Cube cube) {
        int rmin = 255;
        int rmax = 0;
        int gmin = 255;
        int gmax = 0;
        int bmin = 255;
        int bmax = 0;
        for (int i = cube.lower; i <= cube.upper; ++i) {
            int color = this.colors[i];
            int r = color >> 16;
            int g = color >> 8 & 0xFF;
            int b = color & 0xFF;
            if (r > rmax) {
                rmax = r;
            } else if (r < rmin) {
                rmin = r;
            }
            if (g > gmax) {
                gmax = g;
            } else if (g < gmin) {
                gmin = g;
            }
            if (b > bmax) {
                bmax = b;
                continue;
            }
            if (b >= bmin) continue;
            bmin = b;
        }
        cube.rmin = rmin;
        cube.rmax = rmax;
        cube.gmin = gmin;
        cube.gmax = gmax;
        cube.bmin = bmin;
        cube.bmax = bmax;
    }

    private void createLUT(int ncubes) {
        if (this.colorMap == null) {
            this.colorMap = new LookupTableJAI(new byte[3][ncubes]);
        }
        byte[] rLUT = this.colorMap.getByteData(0);
        byte[] gLUT = this.colorMap.getByteData(1);
        byte[] bLUT = this.colorMap.getByteData(2);
        float scale = 255.0f / (float)(this.mask & 0xFF);
        for (int k = 0; k < ncubes; ++k) {
            Cube cube = this.partition[k];
            float rsum = 0.0f;
            float gsum = 0.0f;
            float bsum = 0.0f;
            for (int i = cube.lower; i <= cube.upper; ++i) {
                int color = this.colors[i];
                int r = color >> 16;
                rsum += (float)r * (float)this.counts[i];
                int g = color >> 8 & 0xFF;
                gsum += (float)g * (float)this.counts[i];
                int b = color & 0xFF;
                bsum += (float)b * (float)this.counts[i];
            }
            rLUT[k] = (byte)(rsum / (float)cube.count * scale);
            gLUT[k] = (byte)(gsum / (float)cube.count * scale);
            bLUT[k] = (byte)(bsum / (float)cube.count * scale);
        }
    }

    void quickSort(int[] a, int lo0, int hi0, int longDimMask) {
        int lo = lo0;
        int hi = hi0;
        if (hi0 > lo0) {
            int mid = a[(lo0 + hi0) / 2] & longDimMask;
            while (lo <= hi) {
                while (lo < hi0 && (a[lo] & longDimMask) < mid) {
                    ++lo;
                }
                while (hi > lo0 && (a[hi] & longDimMask) > mid) {
                    --hi;
                }
                if (lo > hi) continue;
                int t = a[lo];
                a[lo] = a[hi];
                a[hi] = t;
                t = this.counts[lo];
                this.counts[lo] = this.counts[hi];
                this.counts[hi] = t;
                ++lo;
                --hi;
            }
            if (lo0 < hi) {
                this.quickSort(a, lo0, hi, longDimMask);
            }
            if (lo < hi0) {
                this.quickSort(a, lo, hi0, longDimMask);
            }
        }
    }
}

