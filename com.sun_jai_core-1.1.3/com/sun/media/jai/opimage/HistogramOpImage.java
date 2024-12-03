/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import java.awt.Image;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import javax.media.jai.Histogram;
import javax.media.jai.ROI;
import javax.media.jai.StatisticsOpImage;

final class HistogramOpImage
extends StatisticsOpImage {
    private int[] numBins;
    private double[] lowValue;
    private double[] highValue;
    private int numBands;

    private final boolean tileIntersectsROI(int tileX, int tileY) {
        if (this.roi == null) {
            return true;
        }
        return this.roi.intersects(this.tileXToX(tileX), this.tileYToY(tileY), this.tileWidth, this.tileHeight);
    }

    public HistogramOpImage(RenderedImage source, ROI roi, int xStart, int yStart, int xPeriod, int yPeriod, int[] numBins, double[] lowValue, double[] highValue) {
        super(source, roi, xStart, yStart, xPeriod, yPeriod);
        this.numBands = source.getSampleModel().getNumBands();
        this.numBins = new int[this.numBands];
        this.lowValue = new double[this.numBands];
        this.highValue = new double[this.numBands];
        for (int b = 0; b < this.numBands; ++b) {
            this.numBins[b] = numBins.length == 1 ? numBins[0] : numBins[b];
            this.lowValue[b] = lowValue.length == 1 ? lowValue[0] : lowValue[b];
            this.highValue[b] = highValue.length == 1 ? highValue[0] : highValue[b];
        }
    }

    protected String[] getStatisticsNames() {
        String[] names = new String[]{"histogram"};
        return names;
    }

    protected Object createStatistics(String name) {
        if (name.equalsIgnoreCase("histogram")) {
            return new Histogram(this.numBins, this.lowValue, this.highValue);
        }
        return Image.UndefinedProperty;
    }

    protected void accumulateStatistics(String name, Raster source, Object stats) {
        Histogram histogram = (Histogram)stats;
        histogram.countPixels(source, this.roi, this.xStart, this.yStart, this.xPeriod, this.yPeriod);
    }
}

