/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import javax.media.jai.PixelAccessor;
import javax.media.jai.ROI;
import javax.media.jai.StatisticsOpImage;
import javax.media.jai.UnpackedImageData;

public class ExtremaOpImage
extends StatisticsOpImage {
    protected double[][] extrema = null;
    protected ArrayList[] minLocations;
    protected ArrayList[] maxLocations;
    protected int[] minCounts;
    protected int[] maxCounts;
    protected boolean saveLocations;
    protected int maxRuns;
    protected int numMinLocations = 0;
    protected int numMaxLocations = 0;
    private boolean isInitialized = false;
    private PixelAccessor srcPA;
    private int srcSampleType;

    private final boolean tileIntersectsROI(int tileX, int tileY) {
        if (this.roi == null) {
            return true;
        }
        return this.roi.intersects(this.tileXToX(tileX), this.tileYToY(tileY), this.tileWidth, this.tileHeight);
    }

    public ExtremaOpImage(RenderedImage source, ROI roi, int xStart, int yStart, int xPeriod, int yPeriod, boolean saveLocations, int maxRuns) {
        super(source, roi, xStart, yStart, xPeriod, yPeriod);
        this.saveLocations = saveLocations;
        this.maxRuns = maxRuns;
    }

    public Object getProperty(String name) {
        int numBands = this.sampleModel.getNumBands();
        if (this.extrema == null) {
            return super.getProperty(name);
        }
        if (name.equalsIgnoreCase("extrema")) {
            double[][] stats = new double[2][numBands];
            for (int i = 0; i < numBands; ++i) {
                stats[0][i] = this.extrema[0][i];
                stats[1][i] = this.extrema[1][i];
            }
            return stats;
        }
        if (name.equalsIgnoreCase("minimum")) {
            double[] stats = new double[numBands];
            for (int i = 0; i < numBands; ++i) {
                stats[i] = this.extrema[0][i];
            }
            return stats;
        }
        if (name.equalsIgnoreCase("maximum")) {
            double[] stats = new double[numBands];
            for (int i = 0; i < numBands; ++i) {
                stats[i] = this.extrema[1][i];
            }
            return stats;
        }
        if (this.saveLocations && name.equalsIgnoreCase("minLocations")) {
            return this.minLocations;
        }
        if (this.saveLocations && name.equalsIgnoreCase("maxLocations")) {
            return this.maxLocations;
        }
        return Image.UndefinedProperty;
    }

    protected String[] getStatisticsNames() {
        return new String[]{"extrema", "maximum", "minimum", "maxLocations", "minLocations"};
    }

    protected Object createStatistics(String name) {
        int numBands = this.sampleModel.getNumBands();
        Object stats = null;
        stats = name.equalsIgnoreCase("extrema") ? new double[2][numBands] : (name.equalsIgnoreCase("minimum") || name.equalsIgnoreCase("maximum") ? (Object)new double[numBands] : (this.saveLocations && (name.equalsIgnoreCase("minLocations") || name.equalsIgnoreCase("maxLocations")) ? (Object)new ArrayList[numBands] : (Object)Image.UndefinedProperty));
        return stats;
    }

    private final int startPosition(int pos, int start, int period) {
        int t = (pos - start) % period;
        return t == 0 ? pos : pos + (period - t);
    }

    protected void accumulateStatistics(String name, Raster source, Object stats) {
        block19: {
            int i;
            block22: {
                block21: {
                    block20: {
                        block18: {
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
                            while (iterator.hasNext()) {
                                Rectangle rect = srcBounds.intersection((Rectangle)iterator.next());
                                int tx = rect.x;
                                int ty = rect.y;
                                rect.x = this.startPosition(tx, this.xStart, this.xPeriod);
                                rect.y = this.startPosition(ty, this.yStart, this.yPeriod);
                                rect.width = tx + rect.width - rect.x;
                                rect.height = ty + rect.height - rect.y;
                                if (rect.isEmpty()) continue;
                                this.initializeState(source);
                                UnpackedImageData uid = this.srcPA.getPixels(source, rect, this.srcSampleType, false);
                                switch (uid.type) {
                                    case 0: {
                                        this.accumulateStatisticsByte(uid);
                                        break;
                                    }
                                    case 1: {
                                        this.accumulateStatisticsUShort(uid);
                                        break;
                                    }
                                    case 2: {
                                        this.accumulateStatisticsShort(uid);
                                        break;
                                    }
                                    case 3: {
                                        this.accumulateStatisticsInt(uid);
                                        break;
                                    }
                                    case 4: {
                                        this.accumulateStatisticsFloat(uid);
                                        break;
                                    }
                                    case 5: {
                                        this.accumulateStatisticsDouble(uid);
                                    }
                                }
                            }
                            if (!name.equalsIgnoreCase("extrema")) break block18;
                            double[][] ext = (double[][])stats;
                            for (i = 0; i < this.srcPA.numBands; ++i) {
                                ext[0][i] = this.extrema[0][i];
                                ext[1][i] = this.extrema[1][i];
                            }
                            break block19;
                        }
                        if (!name.equalsIgnoreCase("minimum")) break block20;
                        double[] min = (double[])stats;
                        for (i = 0; i < this.srcPA.numBands; ++i) {
                            min[i] = this.extrema[0][i];
                        }
                        break block19;
                    }
                    if (!name.equalsIgnoreCase("maximum")) break block21;
                    double[] max = (double[])stats;
                    for (i = 0; i < this.srcPA.numBands; ++i) {
                        max[i] = this.extrema[1][i];
                    }
                    break block19;
                }
                if (!name.equalsIgnoreCase("minLocations")) break block22;
                ArrayList[] minLoc = (ArrayList[])stats;
                for (i = 0; i < this.srcPA.numBands; ++i) {
                    minLoc[i] = this.minLocations[i];
                }
                break block19;
            }
            if (!name.equalsIgnoreCase("maxLocations")) break block19;
            ArrayList[] maxLoc = (ArrayList[])stats;
            for (i = 0; i < this.srcPA.numBands; ++i) {
                maxLoc[i] = this.maxLocations[i];
            }
        }
    }

    private void accumulateStatisticsByte(UnpackedImageData uid) {
        Rectangle rect = uid.rect;
        byte[][] data = uid.getByteData();
        int lineStride = uid.lineStride;
        int pixelStride = uid.pixelStride;
        int lineInc = lineStride * this.yPeriod;
        int pixelInc = pixelStride * this.xPeriod;
        if (!this.saveLocations) {
            for (int b = 0; b < this.srcPA.numBands; ++b) {
                int min = (int)this.extrema[0][b];
                int max = (int)this.extrema[1][b];
                byte[] d = data[b];
                int lastLine = uid.bandOffsets[b] + rect.height * lineStride;
                for (int lo = uid.bandOffsets[b]; lo < lastLine; lo += lineInc) {
                    int lastPixel = lo + rect.width * pixelStride;
                    for (int po = lo; po < lastPixel; po += pixelInc) {
                        int p = d[po] & 0xFF;
                        if (p < min) {
                            min = p;
                            continue;
                        }
                        if (p <= max) continue;
                        max = p;
                    }
                }
                this.extrema[0][b] = min;
                this.extrema[1][b] = max;
            }
        } else {
            for (int b = 0; b < this.srcPA.numBands; ++b) {
                int min = (int)this.extrema[0][b];
                int max = (int)this.extrema[1][b];
                ArrayList minList = this.minLocations[b];
                ArrayList maxList = this.maxLocations[b];
                int minCount = this.minCounts[b];
                int maxCount = this.maxCounts[b];
                byte[] d = data[b];
                int lastLine = uid.bandOffsets[b] + rect.height * lineStride;
                int lo = uid.bandOffsets[b];
                int y = rect.y;
                while (lo < lastLine) {
                    int lastPixel = lo + rect.width * pixelStride;
                    int minStart = 0;
                    int maxStart = 0;
                    int minLength = 0;
                    int maxLength = 0;
                    int po = lo;
                    int x = rect.x;
                    while (po < lastPixel) {
                        int p = d[po] & 0xFF;
                        if (p < min) {
                            min = p;
                            minStart = x;
                            minLength = 1;
                            minList.clear();
                            minCount = 0;
                        } else if (p > max) {
                            max = p;
                            maxStart = x;
                            maxLength = 1;
                            maxList.clear();
                            maxCount = 0;
                        } else {
                            if (p == min) {
                                if (minLength == 0) {
                                    minStart = x;
                                }
                                ++minLength;
                            } else if (minLength > 0 && minCount < this.maxRuns) {
                                minList.add(new int[]{minStart, y, minLength});
                                ++minCount;
                                minLength = 0;
                            }
                            if (p == max) {
                                if (maxLength == 0) {
                                    maxStart = x;
                                }
                                ++maxLength;
                            } else if (maxLength > 0 && maxCount < this.maxRuns) {
                                maxList.add(new int[]{maxStart, y, maxLength});
                                ++maxCount;
                                maxLength = 0;
                            }
                        }
                        po += pixelInc;
                        x += this.xPeriod;
                    }
                    if (maxLength > 0 && maxCount < this.maxRuns) {
                        maxList.add(new int[]{maxStart, y, maxLength});
                        ++maxCount;
                    }
                    if (minLength > 0 && minCount < this.maxRuns) {
                        minList.add(new int[]{minStart, y, minLength});
                        ++minCount;
                    }
                    lo += lineInc;
                    y += this.yPeriod;
                }
                this.extrema[0][b] = min;
                this.extrema[1][b] = max;
                this.minCounts[b] = minCount;
                this.maxCounts[b] = maxCount;
            }
        }
    }

    private void accumulateStatisticsUShort(UnpackedImageData uid) {
        Rectangle rect = uid.rect;
        short[][] data = uid.getShortData();
        int lineStride = uid.lineStride;
        int pixelStride = uid.pixelStride;
        int lineInc = lineStride * this.yPeriod;
        int pixelInc = pixelStride * this.xPeriod;
        if (!this.saveLocations) {
            for (int b = 0; b < this.srcPA.numBands; ++b) {
                int min = (int)this.extrema[0][b];
                int max = (int)this.extrema[1][b];
                short[] d = data[b];
                int lastLine = uid.bandOffsets[b] + rect.height * lineStride;
                for (int lo = uid.bandOffsets[b]; lo < lastLine; lo += lineInc) {
                    int lastPixel = lo + rect.width * pixelStride;
                    for (int po = lo; po < lastPixel; po += pixelInc) {
                        int p = d[po] & 0xFFFF;
                        if (p < min) {
                            min = p;
                            continue;
                        }
                        if (p <= max) continue;
                        max = p;
                    }
                }
                this.extrema[0][b] = min;
                this.extrema[1][b] = max;
            }
        } else {
            for (int b = 0; b < this.srcPA.numBands; ++b) {
                int min = (int)this.extrema[0][b];
                int max = (int)this.extrema[1][b];
                ArrayList minList = this.minLocations[b];
                ArrayList maxList = this.maxLocations[b];
                int minCount = this.minCounts[b];
                int maxCount = this.maxCounts[b];
                short[] d = data[b];
                int lastLine = uid.bandOffsets[b] + rect.height * lineStride;
                int lo = uid.bandOffsets[b];
                int y = rect.y;
                while (lo < lastLine) {
                    int lastPixel = lo + rect.width * pixelStride;
                    int minStart = 0;
                    int maxStart = 0;
                    int minLength = 0;
                    int maxLength = 0;
                    int po = lo;
                    int x = rect.x;
                    while (po < lastPixel) {
                        int p = d[po] & 0xFFFF;
                        if (p < min) {
                            min = p;
                            minStart = x;
                            minLength = 1;
                            minList.clear();
                            minCount = 0;
                        } else if (p > max) {
                            max = p;
                            maxStart = x;
                            maxLength = 1;
                            maxList.clear();
                            maxCount = 0;
                        } else {
                            if (p == min) {
                                if (minLength == 0) {
                                    minStart = x;
                                }
                                ++minLength;
                            } else if (minLength > 0 && minCount < this.maxRuns) {
                                minList.add(new int[]{minStart, y, minLength});
                                ++minCount;
                                minLength = 0;
                            }
                            if (p == max) {
                                if (maxLength == 0) {
                                    maxStart = x;
                                }
                                ++maxLength;
                            } else if (maxLength > 0 && maxCount < this.maxRuns) {
                                maxList.add(new int[]{maxStart, y, maxLength});
                                ++maxCount;
                                maxLength = 0;
                            }
                        }
                        po += pixelInc;
                        x += this.xPeriod;
                    }
                    if (maxLength > 0 && maxCount < this.maxRuns) {
                        maxList.add(new int[]{maxStart, y, maxLength});
                        ++maxCount;
                    }
                    if (minLength > 0 && minCount < this.maxRuns) {
                        minList.add(new int[]{minStart, y, minLength});
                        ++minCount;
                    }
                    lo += lineInc;
                    y += this.yPeriod;
                }
                this.extrema[0][b] = min;
                this.extrema[1][b] = max;
                this.minCounts[b] = minCount;
                this.maxCounts[b] = maxCount;
            }
        }
    }

    private void accumulateStatisticsShort(UnpackedImageData uid) {
        Rectangle rect = uid.rect;
        short[][] data = uid.getShortData();
        int lineStride = uid.lineStride;
        int pixelStride = uid.pixelStride;
        int lineInc = lineStride * this.yPeriod;
        int pixelInc = pixelStride * this.xPeriod;
        if (!this.saveLocations) {
            for (int b = 0; b < this.srcPA.numBands; ++b) {
                int min = (int)this.extrema[0][b];
                int max = (int)this.extrema[1][b];
                short[] d = data[b];
                int lastLine = uid.bandOffsets[b] + rect.height * lineStride;
                for (int lo = uid.bandOffsets[b]; lo < lastLine; lo += lineInc) {
                    int lastPixel = lo + rect.width * pixelStride;
                    for (int po = lo; po < lastPixel; po += pixelInc) {
                        int p = d[po];
                        if (p < min) {
                            min = p;
                            continue;
                        }
                        if (p <= max) continue;
                        max = p;
                    }
                }
                this.extrema[0][b] = min;
                this.extrema[1][b] = max;
            }
        } else {
            for (int b = 0; b < this.srcPA.numBands; ++b) {
                int min = (int)this.extrema[0][b];
                int max = (int)this.extrema[1][b];
                ArrayList minList = this.minLocations[b];
                ArrayList maxList = this.maxLocations[b];
                int minCount = this.minCounts[b];
                int maxCount = this.maxCounts[b];
                short[] d = data[b];
                int lastLine = uid.bandOffsets[b] + rect.height * lineStride;
                int lo = uid.bandOffsets[b];
                int y = rect.y;
                while (lo < lastLine) {
                    int lastPixel = lo + rect.width * pixelStride;
                    int minStart = 0;
                    int maxStart = 0;
                    int minLength = 0;
                    int maxLength = 0;
                    int po = lo;
                    int x = rect.x;
                    while (po < lastPixel) {
                        int p = d[po];
                        if (p < min) {
                            min = p;
                            minStart = x;
                            minLength = 1;
                            minList.clear();
                            minCount = 0;
                        } else if (p > max) {
                            max = p;
                            maxStart = x;
                            maxLength = 1;
                            maxList.clear();
                            maxCount = 0;
                        } else {
                            if (p == min) {
                                if (minLength == 0) {
                                    minStart = x;
                                }
                                ++minLength;
                            } else if (minLength > 0 && minCount < this.maxRuns) {
                                minList.add(new int[]{minStart, y, minLength});
                                ++minCount;
                                minLength = 0;
                            }
                            if (p == max) {
                                if (maxLength == 0) {
                                    maxStart = x;
                                }
                                ++maxLength;
                            } else if (maxLength > 0 && maxCount < this.maxRuns) {
                                maxList.add(new int[]{maxStart, y, maxLength});
                                ++maxCount;
                                maxLength = 0;
                            }
                        }
                        po += pixelInc;
                        x += this.xPeriod;
                    }
                    if (maxLength > 0 && maxCount < this.maxRuns) {
                        maxList.add(new int[]{maxStart, y, maxLength});
                        ++maxCount;
                    }
                    if (minLength > 0 && minCount < this.maxRuns) {
                        minList.add(new int[]{minStart, y, minLength});
                        ++minCount;
                    }
                    lo += lineInc;
                    y += this.yPeriod;
                }
                this.extrema[0][b] = min;
                this.extrema[1][b] = max;
                this.minCounts[b] = minCount;
                this.maxCounts[b] = maxCount;
            }
        }
    }

    private void accumulateStatisticsInt(UnpackedImageData uid) {
        Rectangle rect = uid.rect;
        int[][] data = uid.getIntData();
        int lineStride = uid.lineStride;
        int pixelStride = uid.pixelStride;
        int lineInc = lineStride * this.yPeriod;
        int pixelInc = pixelStride * this.xPeriod;
        if (!this.saveLocations) {
            for (int b = 0; b < this.srcPA.numBands; ++b) {
                int min = (int)this.extrema[0][b];
                int max = (int)this.extrema[1][b];
                int[] d = data[b];
                int lastLine = uid.bandOffsets[b] + rect.height * lineStride;
                for (int lo = uid.bandOffsets[b]; lo < lastLine; lo += lineInc) {
                    int lastPixel = lo + rect.width * pixelStride;
                    for (int po = lo; po < lastPixel; po += pixelInc) {
                        int p = d[po];
                        if (p < min) {
                            min = p;
                            continue;
                        }
                        if (p <= max) continue;
                        max = p;
                    }
                }
                this.extrema[0][b] = min;
                this.extrema[1][b] = max;
            }
        } else {
            for (int b = 0; b < this.srcPA.numBands; ++b) {
                int min = (int)this.extrema[0][b];
                int max = (int)this.extrema[1][b];
                ArrayList minList = this.minLocations[b];
                ArrayList maxList = this.maxLocations[b];
                int minCount = this.minCounts[b];
                int maxCount = this.maxCounts[b];
                int[] d = data[b];
                int lastLine = uid.bandOffsets[b] + rect.height * lineStride;
                int lo = uid.bandOffsets[b];
                int y = rect.y;
                while (lo < lastLine) {
                    int lastPixel = lo + rect.width * pixelStride;
                    int minStart = 0;
                    int maxStart = 0;
                    int minLength = 0;
                    int maxLength = 0;
                    int po = lo;
                    int x = rect.x;
                    while (po < lastPixel) {
                        int p = d[po];
                        if (p < min) {
                            min = p;
                            minStart = x;
                            minLength = 1;
                            minList.clear();
                            minCount = 0;
                        } else if (p > max) {
                            max = p;
                            maxStart = x;
                            maxLength = 1;
                            maxList.clear();
                            maxCount = 0;
                        } else {
                            if (p == min) {
                                if (minLength == 0) {
                                    minStart = x;
                                }
                                ++minLength;
                            } else if (minLength > 0 && minCount < this.maxRuns) {
                                minList.add(new int[]{minStart, y, minLength});
                                ++minCount;
                                minLength = 0;
                            }
                            if (p == max) {
                                if (maxLength == 0) {
                                    maxStart = x;
                                }
                                ++maxLength;
                            } else if (maxLength > 0 && maxCount < this.maxRuns) {
                                maxList.add(new int[]{maxStart, y, maxLength});
                                ++maxCount;
                                maxLength = 0;
                            }
                        }
                        po += pixelInc;
                        x += this.xPeriod;
                    }
                    if (maxLength > 0 && maxCount < this.maxRuns) {
                        maxList.add(new int[]{maxStart, y, maxLength});
                        ++maxCount;
                    }
                    if (minLength > 0 && minCount < this.maxRuns) {
                        minList.add(new int[]{minStart, y, minLength});
                        ++minCount;
                    }
                    lo += lineInc;
                    y += this.yPeriod;
                }
                this.extrema[0][b] = min;
                this.extrema[1][b] = max;
                this.minCounts[b] = minCount;
                this.maxCounts[b] = maxCount;
            }
        }
    }

    private void accumulateStatisticsFloat(UnpackedImageData uid) {
        Rectangle rect = uid.rect;
        float[][] data = uid.getFloatData();
        int lineStride = uid.lineStride;
        int pixelStride = uid.pixelStride;
        int lineInc = lineStride * this.yPeriod;
        int pixelInc = pixelStride * this.xPeriod;
        if (!this.saveLocations) {
            for (int b = 0; b < this.srcPA.numBands; ++b) {
                float min = (float)this.extrema[0][b];
                float max = (float)this.extrema[1][b];
                float[] d = data[b];
                int lastLine = uid.bandOffsets[b] + rect.height * lineStride;
                for (int lo = uid.bandOffsets[b]; lo < lastLine; lo += lineInc) {
                    int lastPixel = lo + rect.width * pixelStride;
                    for (int po = lo; po < lastPixel; po += pixelInc) {
                        float p = d[po];
                        if (p < min) {
                            min = p;
                            continue;
                        }
                        if (!(p > max)) continue;
                        max = p;
                    }
                }
                this.extrema[0][b] = min;
                this.extrema[1][b] = max;
            }
        } else {
            for (int b = 0; b < this.srcPA.numBands; ++b) {
                float min = (float)this.extrema[0][b];
                float max = (float)this.extrema[1][b];
                ArrayList minList = this.minLocations[b];
                ArrayList maxList = this.maxLocations[b];
                int minCount = this.minCounts[b];
                int maxCount = this.maxCounts[b];
                float[] d = data[b];
                int lastLine = uid.bandOffsets[b] + rect.height * lineStride;
                int lo = uid.bandOffsets[b];
                int y = rect.y;
                while (lo < lastLine) {
                    int lastPixel = lo + rect.width * pixelStride;
                    int minStart = 0;
                    int maxStart = 0;
                    int minLength = 0;
                    int maxLength = 0;
                    int po = lo;
                    int x = rect.x;
                    while (po < lastPixel) {
                        float p = d[po];
                        if (p < min) {
                            min = p;
                            minStart = x;
                            minLength = 1;
                            minList.clear();
                            minCount = 0;
                        } else if (p > max) {
                            max = p;
                            maxStart = x;
                            maxLength = 1;
                            maxList.clear();
                            maxCount = 0;
                        } else {
                            if (p == min) {
                                if (minLength == 0) {
                                    minStart = x;
                                }
                                ++minLength;
                            } else if (minLength > 0 && minCount < this.maxRuns) {
                                minList.add(new int[]{minStart, y, minLength});
                                ++minCount;
                                minLength = 0;
                            }
                            if (p == max) {
                                if (maxLength == 0) {
                                    maxStart = x;
                                }
                                ++maxLength;
                            } else if (maxLength > 0 && maxCount < this.maxRuns) {
                                maxList.add(new int[]{maxStart, y, maxLength});
                                ++maxCount;
                                maxLength = 0;
                            }
                        }
                        po += pixelInc;
                        x += this.xPeriod;
                    }
                    if (maxLength > 0 && maxCount < this.maxRuns) {
                        maxList.add(new int[]{maxStart, y, maxLength});
                        ++maxCount;
                    }
                    if (minLength > 0 && minCount < this.maxRuns) {
                        minList.add(new int[]{minStart, y, minLength});
                        ++minCount;
                    }
                    lo += lineInc;
                    y += this.yPeriod;
                }
                this.extrema[0][b] = min;
                this.extrema[1][b] = max;
                this.minCounts[b] = minCount;
                this.maxCounts[b] = maxCount;
            }
        }
    }

    private void accumulateStatisticsDouble(UnpackedImageData uid) {
        Rectangle rect = uid.rect;
        double[][] data = uid.getDoubleData();
        int lineStride = uid.lineStride;
        int pixelStride = uid.pixelStride;
        int lineInc = lineStride * this.yPeriod;
        int pixelInc = pixelStride * this.xPeriod;
        if (!this.saveLocations) {
            for (int b = 0; b < this.srcPA.numBands; ++b) {
                double min = this.extrema[0][b];
                double max = this.extrema[1][b];
                double[] d = data[b];
                int lastLine = uid.bandOffsets[b] + rect.height * lineStride;
                for (int lo = uid.bandOffsets[b]; lo < lastLine; lo += lineInc) {
                    int lastPixel = lo + rect.width * pixelStride;
                    for (int po = lo; po < lastPixel; po += pixelInc) {
                        double p = d[po];
                        if (p < min) {
                            min = p;
                            continue;
                        }
                        if (!(p > max)) continue;
                        max = p;
                    }
                }
                this.extrema[0][b] = min;
                this.extrema[1][b] = max;
            }
        } else {
            for (int b = 0; b < this.srcPA.numBands; ++b) {
                double min = this.extrema[0][b];
                double max = this.extrema[1][b];
                ArrayList minList = this.minLocations[b];
                ArrayList maxList = this.maxLocations[b];
                int minCount = this.minCounts[b];
                int maxCount = this.maxCounts[b];
                double[] d = data[b];
                int lastLine = uid.bandOffsets[b] + rect.height * lineStride;
                int lo = uid.bandOffsets[b];
                int y = rect.y;
                while (lo < lastLine) {
                    int lastPixel = lo + rect.width * pixelStride;
                    int minStart = 0;
                    int maxStart = 0;
                    int minLength = 0;
                    int maxLength = 0;
                    int po = lo;
                    int x = rect.x;
                    while (po < lastPixel) {
                        double p = d[po];
                        if (p < min) {
                            min = p;
                            minStart = x;
                            minLength = 1;
                            minList.clear();
                            minCount = 0;
                        } else if (p > max) {
                            max = p;
                            maxStart = x;
                            maxLength = 1;
                            maxList.clear();
                            maxCount = 0;
                        } else {
                            if (p == min) {
                                if (minLength == 0) {
                                    minStart = x;
                                }
                                ++minLength;
                            } else if (minLength > 0 && minCount < this.maxRuns) {
                                minList.add(new int[]{minStart, y, minLength});
                                ++minCount;
                                minLength = 0;
                            }
                            if (p == max) {
                                if (maxLength == 0) {
                                    maxStart = x;
                                }
                                ++maxLength;
                            } else if (maxLength > 0 && maxCount < this.maxRuns) {
                                maxList.add(new int[]{maxStart, y, maxLength});
                                ++maxCount;
                                maxLength = 0;
                            }
                        }
                        po += pixelInc;
                        x += this.xPeriod;
                    }
                    if (maxLength > 0 && maxCount < this.maxRuns) {
                        maxList.add(new int[]{maxStart, y, maxLength});
                        ++maxCount;
                    }
                    if (minLength > 0 && minCount < this.maxRuns) {
                        minList.add(new int[]{minStart, y, minLength});
                        ++minCount;
                    }
                    lo += lineInc;
                    y += this.yPeriod;
                }
                this.extrema[0][b] = min;
                this.extrema[1][b] = max;
                this.minCounts[b] = minCount;
                this.maxCounts[b] = maxCount;
            }
        }
    }

    protected void initializeState(Raster source) {
        if (this.extrema == null) {
            int i;
            int numBands = this.sampleModel.getNumBands();
            this.extrema = new double[2][numBands];
            Rectangle rect = source.getBounds();
            if (this.roi != null) {
                LinkedList rectList = this.roi.getAsRectangleList(rect.x, rect.y, rect.width, rect.height);
                if (rectList == null) {
                    return;
                }
                ListIterator iterator = rectList.listIterator(0);
                if (iterator.hasNext()) {
                    rect = rect.intersection((Rectangle)iterator.next());
                }
            }
            rect.x = this.startPosition(rect.x, this.xStart, this.xPeriod);
            rect.y = this.startPosition(rect.y, this.yStart, this.yPeriod);
            source.getPixel(rect.x, rect.y, this.extrema[0]);
            for (i = 0; i < numBands; ++i) {
                this.extrema[1][i] = this.extrema[0][i];
            }
            if (this.saveLocations) {
                this.minLocations = new ArrayList[numBands];
                this.maxLocations = new ArrayList[numBands];
                this.minCounts = new int[numBands];
                this.maxCounts = new int[numBands];
                for (i = 0; i < numBands; ++i) {
                    this.minLocations[i] = new ArrayList();
                    this.maxLocations[i] = new ArrayList();
                    this.maxCounts[i] = 0;
                    this.minCounts[i] = 0;
                }
            }
        }
    }
}

