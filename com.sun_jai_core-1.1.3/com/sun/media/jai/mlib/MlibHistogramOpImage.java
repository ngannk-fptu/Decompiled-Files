/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.medialib.mlib.Image
 *  com.sun.medialib.mlib.mediaLibImage
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.medialib.mlib.mediaLibImage;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ComponentSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.Iterator;
import java.util.TreeMap;
import javax.media.jai.Histogram;
import javax.media.jai.StatisticsOpImage;

final class MlibHistogramOpImage
extends StatisticsOpImage {
    private int[] numBins;
    private double[] lowValueFP;
    private double[] highValueFP;
    private int[] lowValue;
    private int[] highValue;
    private int numBands = this.sampleModel.getNumBands();
    private int[] bandIndexMap;
    private boolean reorderBands = false;

    public MlibHistogramOpImage(RenderedImage source, int xPeriod, int yPeriod, int[] numBins, double[] lowValueFP, double[] highValueFP) {
        super(source, null, source.getMinX(), source.getMinY(), xPeriod, yPeriod);
        int i;
        this.numBins = new int[this.numBands];
        this.lowValueFP = new double[this.numBands];
        this.highValueFP = new double[this.numBands];
        for (int b = 0; b < this.numBands; ++b) {
            this.numBins[b] = numBins.length == 1 ? numBins[0] : numBins[b];
            this.lowValueFP[b] = lowValueFP.length == 1 ? lowValueFP[0] : lowValueFP[b];
            this.highValueFP[b] = highValueFP.length == 1 ? highValueFP[0] : highValueFP[b];
        }
        this.lowValue = new int[this.lowValueFP.length];
        for (i = 0; i < this.lowValueFP.length; ++i) {
            this.lowValue[i] = (int)Math.ceil(this.lowValueFP[i]);
        }
        this.highValue = new int[this.highValueFP.length];
        for (i = 0; i < this.highValueFP.length; ++i) {
            this.highValue[i] = (int)Math.ceil(this.highValueFP[i]);
        }
        if (this.numBands > 1) {
            int i2;
            ComponentSampleModel csm = (ComponentSampleModel)this.sampleModel;
            TreeMap<Integer, Integer> indexMap = new TreeMap<Integer, Integer>();
            int[] indices = csm.getBankIndices();
            boolean checkBanks = false;
            for (i2 = 1; i2 < this.numBands; ++i2) {
                if (indices[i2] == indices[i2 - 1]) continue;
                checkBanks = true;
                break;
            }
            if (checkBanks) {
                for (i2 = 0; i2 < this.numBands; ++i2) {
                    indexMap.put(new Integer(indices[i2]), new Integer(i2));
                }
                this.bandIndexMap = new int[this.numBands];
                Iterator bankIter = indexMap.keySet().iterator();
                int k = 0;
                while (bankIter.hasNext()) {
                    int idx = (Integer)indexMap.get(bankIter.next());
                    if (idx != k) {
                        this.reorderBands = true;
                    }
                    this.bandIndexMap[k++] = idx;
                }
            }
            if (!this.reorderBands) {
                indexMap.clear();
                if (this.bandIndexMap == null) {
                    this.bandIndexMap = new int[this.numBands];
                }
                int[] offsets = csm.getBandOffsets();
                for (int i3 = 0; i3 < this.numBands; ++i3) {
                    indexMap.put(new Integer(offsets[i3]), new Integer(i3));
                }
                Iterator offsetIter = indexMap.keySet().iterator();
                int k = 0;
                while (offsetIter.hasNext()) {
                    int idx = (Integer)indexMap.get(offsetIter.next());
                    if (idx != k) {
                        this.reorderBands = true;
                    }
                    this.bandIndexMap[k++] = idx;
                }
            }
        }
    }

    protected String[] getStatisticsNames() {
        String[] names = new String[]{"histogram"};
        return names;
    }

    protected Object createStatistics(String name) {
        if (name.equalsIgnoreCase("histogram")) {
            return new Histogram(this.numBins, this.lowValueFP, this.highValueFP);
        }
        return Image.UndefinedProperty;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void accumulateStatistics(String name, Raster source, Object stats) {
        Object histo;
        Histogram histogram = (Histogram)stats;
        int numBands = histogram.getNumBands();
        int[][] histJAI = histogram.getBins();
        Rectangle tileRect = source.getBounds();
        if (!this.reorderBands && tileRect.equals(this.getBounds())) {
            histo = histJAI;
        } else {
            histo = new int[numBands][];
            for (int i = 0; i < numBands; ++i) {
                histo[i] = new int[histogram.getNumBins(i)];
            }
        }
        int formatTag = MediaLibAccessor.findCompatibleTag(null, source);
        MediaLibAccessor accessor = new MediaLibAccessor(source, tileRect, formatTag);
        mediaLibImage[] img = accessor.getMediaLibImages();
        int offsetX = (this.xPeriod - (tileRect.x - this.xStart) % this.xPeriod) % this.xPeriod;
        int offsetY = (this.yPeriod - (tileRect.y - this.yStart) % this.yPeriod) % this.yPeriod;
        if (histo == histJAI) {
            Histogram histogram2 = histogram;
            synchronized (histogram2) {
                com.sun.medialib.mlib.Image.Histogram2((int[][])histo, (mediaLibImage)img[0], (int[])this.lowValue, (int[])this.highValue, (int)offsetX, (int)offsetY, (int)this.xPeriod, (int)this.yPeriod);
            }
        }
        com.sun.medialib.mlib.Image.Histogram2((int[][])histo, (mediaLibImage)img[0], (int[])this.lowValue, (int[])this.highValue, (int)offsetX, (int)offsetY, (int)this.xPeriod, (int)this.yPeriod);
        Histogram histogram3 = histogram;
        synchronized (histogram3) {
            for (int i = 0; i < numBands; ++i) {
                int numBins = histo[i].length;
                int[] binsBandJAI = this.reorderBands ? histJAI[this.bandIndexMap[i]] : histJAI[i];
                int[] binsBand = histo[i];
                for (int j = 0; j < numBins; ++j) {
                    int n = j;
                    binsBandJAI[n] = binsBandJAI[n] + binsBand[j];
                }
            }
        }
    }
}

