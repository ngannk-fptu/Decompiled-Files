/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.LinkedList;
import java.util.ListIterator;
import javax.media.jai.PixelAccessor;
import javax.media.jai.ROI;
import javax.media.jai.StatisticsOpImage;
import javax.media.jai.UnpackedImageData;

public class MeanOpImage
extends StatisticsOpImage {
    private boolean isInitialized = false;
    private double[] totalPixelValue;
    private int totalPixelCount;
    private PixelAccessor srcPA;
    private int srcSampleType;

    private final boolean tileIntersectsROI(int tileX, int tileY) {
        if (this.roi == null) {
            return true;
        }
        return this.roi.intersects(this.tileXToX(tileX), this.tileYToY(tileY), this.tileWidth, this.tileHeight);
    }

    public MeanOpImage(RenderedImage source, ROI roi, int xStart, int yStart, int xPeriod, int yPeriod) {
        super(source, roi, xStart, yStart, xPeriod, yPeriod);
    }

    protected String[] getStatisticsNames() {
        return new String[]{"mean"};
    }

    protected Object createStatistics(String name) {
        Object stats = name.equalsIgnoreCase("mean") ? (Object)new double[this.sampleModel.getNumBands()] : Image.UndefinedProperty;
        return stats;
    }

    private final int startPosition(int pos, int start, int period) {
        int t = (pos - start) % period;
        if (t == 0) {
            return pos;
        }
        return pos + (period - t);
    }

    protected void accumulateStatistics(String name, Raster source, Object stats) {
        LinkedList rectList;
        if (!this.isInitialized) {
            this.srcPA = new PixelAccessor(this.getSourceImage(0));
            this.srcSampleType = this.srcPA.sampleType == -1 ? 0 : this.srcPA.sampleType;
            this.totalPixelValue = new double[this.srcPA.numBands];
            this.totalPixelCount = 0;
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
        if (name.equalsIgnoreCase("mean")) {
            double[] mean = (double[])stats;
            if (this.totalPixelCount != 0) {
                for (int i = 0; i < this.srcPA.numBands; ++i) {
                    mean[i] = this.totalPixelValue[i] / (double)this.totalPixelCount;
                }
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
        for (int b = 0; b < this.srcPA.numBands; ++b) {
            byte[] d = data[b];
            int lastLine = uid.bandOffsets[b] + rect.height * lineStride;
            for (int lo = uid.bandOffsets[b]; lo < lastLine; lo += lineInc) {
                int lastPixel = lo + rect.width * pixelStride;
                for (int po = lo; po < lastPixel; po += pixelInc) {
                    int n = b;
                    this.totalPixelValue[n] = this.totalPixelValue[n] + (double)(d[po] & 0xFF);
                }
            }
        }
        this.totalPixelCount += (int)Math.ceil((double)rect.height / (double)this.yPeriod) * (int)Math.ceil((double)rect.width / (double)this.xPeriod);
    }

    private void accumulateStatisticsUShort(UnpackedImageData uid) {
        Rectangle rect = uid.rect;
        short[][] data = uid.getShortData();
        int lineStride = uid.lineStride;
        int pixelStride = uid.pixelStride;
        int lineInc = lineStride * this.yPeriod;
        int pixelInc = pixelStride * this.xPeriod;
        for (int b = 0; b < this.srcPA.numBands; ++b) {
            short[] d = data[b];
            int lastLine = uid.bandOffsets[b] + rect.height * lineStride;
            for (int lo = uid.bandOffsets[b]; lo < lastLine; lo += lineInc) {
                int lastPixel = lo + rect.width * pixelStride;
                for (int po = lo; po < lastPixel; po += pixelInc) {
                    int n = b;
                    this.totalPixelValue[n] = this.totalPixelValue[n] + (double)(d[po] & 0xFFFF);
                }
            }
        }
        this.totalPixelCount += (int)Math.ceil((double)rect.height / (double)this.yPeriod) * (int)Math.ceil((double)rect.width / (double)this.xPeriod);
    }

    private void accumulateStatisticsShort(UnpackedImageData uid) {
        Rectangle rect = uid.rect;
        short[][] data = uid.getShortData();
        int lineStride = uid.lineStride;
        int pixelStride = uid.pixelStride;
        int lineInc = lineStride * this.yPeriod;
        int pixelInc = pixelStride * this.xPeriod;
        for (int b = 0; b < this.srcPA.numBands; ++b) {
            short[] d = data[b];
            int lastLine = uid.bandOffsets[b] + rect.height * lineStride;
            for (int lo = uid.bandOffsets[b]; lo < lastLine; lo += lineInc) {
                int lastPixel = lo + rect.width * pixelStride;
                for (int po = lo; po < lastPixel; po += pixelInc) {
                    int n = b;
                    this.totalPixelValue[n] = this.totalPixelValue[n] + (double)d[po];
                }
            }
        }
        this.totalPixelCount += (int)Math.ceil((double)rect.height / (double)this.yPeriod) * (int)Math.ceil((double)rect.width / (double)this.xPeriod);
    }

    private void accumulateStatisticsInt(UnpackedImageData uid) {
        Rectangle rect = uid.rect;
        int[][] data = uid.getIntData();
        int lineStride = uid.lineStride;
        int pixelStride = uid.pixelStride;
        int lineInc = lineStride * this.yPeriod;
        int pixelInc = pixelStride * this.xPeriod;
        for (int b = 0; b < this.srcPA.numBands; ++b) {
            int[] d = data[b];
            int lastLine = uid.bandOffsets[b] + rect.height * lineStride;
            for (int lo = uid.bandOffsets[b]; lo < lastLine; lo += lineInc) {
                int lastPixel = lo + rect.width * pixelStride;
                for (int po = lo; po < lastPixel; po += pixelInc) {
                    int n = b;
                    this.totalPixelValue[n] = this.totalPixelValue[n] + (double)d[po];
                }
            }
        }
        this.totalPixelCount += (int)Math.ceil((double)rect.height / (double)this.yPeriod) * (int)Math.ceil((double)rect.width / (double)this.xPeriod);
    }

    private void accumulateStatisticsFloat(UnpackedImageData uid) {
        Rectangle rect = uid.rect;
        float[][] data = uid.getFloatData();
        int lineStride = uid.lineStride;
        int pixelStride = uid.pixelStride;
        int lineInc = lineStride * this.yPeriod;
        int pixelInc = pixelStride * this.xPeriod;
        for (int b = 0; b < this.srcPA.numBands; ++b) {
            float[] d = data[b];
            int lastLine = uid.bandOffsets[b] + rect.height * lineStride;
            for (int lo = uid.bandOffsets[b]; lo < lastLine; lo += lineInc) {
                int lastPixel = lo + rect.width * pixelStride;
                for (int po = lo; po < lastPixel; po += pixelInc) {
                    int n = b;
                    this.totalPixelValue[n] = this.totalPixelValue[n] + (double)d[po];
                }
            }
        }
        this.totalPixelCount += (int)Math.ceil((double)rect.height / (double)this.yPeriod) * (int)Math.ceil((double)rect.width / (double)this.xPeriod);
    }

    private void accumulateStatisticsDouble(UnpackedImageData uid) {
        Rectangle rect = uid.rect;
        double[][] data = uid.getDoubleData();
        int lineStride = uid.lineStride;
        int pixelStride = uid.pixelStride;
        int lineInc = lineStride * this.yPeriod;
        int pixelInc = pixelStride * this.xPeriod;
        for (int b = 0; b < this.srcPA.numBands; ++b) {
            double[] d = data[b];
            int lastLine = uid.bandOffsets[b] + rect.height * lineStride;
            for (int lo = uid.bandOffsets[b]; lo < lastLine; lo += lineInc) {
                int lastPixel = lo + rect.width * pixelStride;
                for (int po = lo; po < lastPixel; po += pixelInc) {
                    int n = b;
                    this.totalPixelValue[n] = this.totalPixelValue[n] + d[po];
                }
            }
        }
        this.totalPixelCount += (int)Math.ceil((double)rect.height / (double)this.yPeriod) * (int)Math.ceil((double)rect.width / (double)this.xPeriod);
    }
}

