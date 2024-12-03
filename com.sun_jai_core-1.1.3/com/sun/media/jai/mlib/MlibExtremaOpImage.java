/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.medialib.mlib.Image
 *  com.sun.medialib.mlib.mediaLibImage
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.media.jai.opimage.ExtremaOpImage;
import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import javax.media.jai.ROI;

final class MlibExtremaOpImage
extends ExtremaOpImage {
    private int[] minCount;
    private int[] maxCount;
    private int[][] minLocs;
    private int[][] maxLocs;

    public MlibExtremaOpImage(RenderedImage source, ROI roi, int xStart, int yStart, int xPeriod, int yPeriod, boolean saveLocations, int maxRuns) {
        super(source, roi, xStart, yStart, xPeriod, yPeriod, saveLocations, maxRuns);
    }

    protected void accumulateStatistics(String name, Raster source, Object stats) {
        block35: {
            int numBands;
            block38: {
                block37: {
                    block36: {
                        block34: {
                            block33: {
                                mediaLibImage[] srcML;
                                MediaLibAccessor srcAccessor;
                                int offsetY;
                                int offsetX;
                                block32: {
                                    numBands = this.sampleModel.getNumBands();
                                    this.initializeState(source);
                                    Rectangle tileRect = source.getBounds();
                                    offsetX = (this.xPeriod - (tileRect.x - this.xStart) % this.xPeriod) % this.xPeriod;
                                    offsetY = (this.yPeriod - (tileRect.y - this.yStart) % this.yPeriod) % this.yPeriod;
                                    if (offsetX >= tileRect.width || offsetY >= tileRect.height) {
                                        return;
                                    }
                                    int formatTag = MediaLibAccessor.findCompatibleTag(null, source);
                                    srcAccessor = new MediaLibAccessor(source, tileRect, formatTag);
                                    srcML = srcAccessor.getMediaLibImages();
                                    if (this.saveLocations) break block32;
                                    switch (srcAccessor.getDataType()) {
                                        case 0: 
                                        case 1: 
                                        case 2: 
                                        case 3: {
                                            int i;
                                            int[] imin = new int[numBands];
                                            int[] imax = new int[numBands];
                                            for (i = 0; i < srcML.length; ++i) {
                                                Image.Extrema2((int[])imin, (int[])imax, (mediaLibImage)srcML[i], (int)offsetX, (int)offsetY, (int)this.xPeriod, (int)this.yPeriod);
                                            }
                                            imin = srcAccessor.getIntParameters(0, imin);
                                            imax = srcAccessor.getIntParameters(0, imax);
                                            for (i = 0; i < numBands; ++i) {
                                                this.extrema[0][i] = Math.min((double)imin[i], this.extrema[0][i]);
                                                this.extrema[1][i] = Math.max((double)imax[i], this.extrema[1][i]);
                                            }
                                            break block33;
                                        }
                                        case 4: 
                                        case 5: {
                                            int i;
                                            double[] dmin = new double[numBands];
                                            double[] dmax = new double[numBands];
                                            for (i = 0; i < srcML.length; ++i) {
                                                Image.Extrema2_Fp((double[])dmin, (double[])dmax, (mediaLibImage)srcML[i], (int)offsetX, (int)offsetY, (int)this.xPeriod, (int)this.yPeriod);
                                            }
                                            dmin = srcAccessor.getDoubleParameters(0, dmin);
                                            dmax = srcAccessor.getDoubleParameters(0, dmax);
                                            for (i = 0; i < numBands; ++i) {
                                                this.extrema[0][i] = Math.min(dmin[i], this.extrema[0][i]);
                                                this.extrema[1][i] = Math.max(dmax[i], this.extrema[1][i]);
                                            }
                                            break;
                                        }
                                    }
                                    break block33;
                                }
                                Rectangle loc = source.getBounds();
                                int xOffset = loc.x;
                                int yOffset = loc.y;
                                switch (srcAccessor.getDataType()) {
                                    case 0: 
                                    case 1: 
                                    case 2: 
                                    case 3: {
                                        int i;
                                        int[] imin = new int[numBands];
                                        int[] imax = new int[numBands];
                                        for (i = 0; i < numBands; ++i) {
                                            imin[i] = (int)this.extrema[0][i];
                                            imax[i] = (int)this.extrema[1][i];
                                        }
                                        for (i = 0; i < srcML.length; ++i) {
                                            Image.ExtremaLocations((int[])imin, (int[])imax, (mediaLibImage)srcML[i], (int)offsetX, (int)offsetY, (int)this.xPeriod, (int)this.yPeriod, (boolean)this.saveLocations, (int)this.maxRuns, (int[])this.minCount, (int[])this.maxCount, (int[][])this.minLocs, (int[][])this.maxLocs);
                                        }
                                        imin = srcAccessor.getIntParameters(0, imin);
                                        imax = srcAccessor.getIntParameters(0, imax);
                                        this.minCount = srcAccessor.getIntParameters(0, this.minCount);
                                        this.maxCount = srcAccessor.getIntParameters(0, this.maxCount);
                                        this.minLocs = srcAccessor.getIntArrayParameters(0, this.minLocs);
                                        this.maxLocs = srcAccessor.getIntArrayParameters(0, this.maxLocs);
                                        for (i = 0; i < numBands; ++i) {
                                            int j;
                                            ArrayList minList = this.minLocations[i];
                                            ArrayList maxList = this.maxLocations[i];
                                            if ((double)imin[i] < this.extrema[0][i]) {
                                                minList.clear();
                                                this.extrema[0][i] = imin[i];
                                            }
                                            int[] minBuf = this.minLocs[i];
                                            int[] maxBuf = this.maxLocs[i];
                                            int k = 0;
                                            for (j = 0; j < this.minCount[i]; ++j) {
                                                minList.add(new int[]{minBuf[k++] + xOffset, minBuf[k++] + yOffset, minBuf[k++]});
                                            }
                                            if ((double)imax[i] > this.extrema[1][i]) {
                                                maxList.clear();
                                                this.extrema[1][i] = imax[i];
                                            }
                                            k = 0;
                                            for (j = 0; j < this.maxCount[i]; ++j) {
                                                maxList.add(new int[]{maxBuf[k++] + xOffset, maxBuf[k++] + yOffset, maxBuf[k++]});
                                            }
                                        }
                                        break;
                                    }
                                    case 4: 
                                    case 5: {
                                        int i;
                                        double[] dmin = new double[numBands];
                                        double[] dmax = new double[numBands];
                                        for (i = 0; i < numBands; ++i) {
                                            dmin[i] = this.extrema[0][i];
                                            dmax[i] = this.extrema[1][i];
                                        }
                                        for (i = 0; i < srcML.length; ++i) {
                                            Image.ExtremaLocations_Fp((double[])dmin, (double[])dmax, (mediaLibImage)srcML[i], (int)offsetX, (int)offsetY, (int)this.xPeriod, (int)this.yPeriod, (boolean)this.saveLocations, (int)this.maxRuns, (int[])this.minCount, (int[])this.maxCount, (int[][])this.minLocs, (int[][])this.maxLocs);
                                        }
                                        dmin = srcAccessor.getDoubleParameters(0, dmin);
                                        dmax = srcAccessor.getDoubleParameters(0, dmax);
                                        this.minCount = srcAccessor.getIntParameters(0, this.minCount);
                                        this.maxCount = srcAccessor.getIntParameters(0, this.maxCount);
                                        this.minLocs = srcAccessor.getIntArrayParameters(0, this.minLocs);
                                        this.maxLocs = srcAccessor.getIntArrayParameters(0, this.maxLocs);
                                        for (i = 0; i < numBands; ++i) {
                                            int j;
                                            ArrayList minList = this.minLocations[i];
                                            ArrayList maxList = this.maxLocations[i];
                                            if (dmin[i] < this.extrema[0][i]) {
                                                minList.clear();
                                                this.extrema[0][i] = dmin[i];
                                            }
                                            int[] minBuf = this.minLocs[i];
                                            int[] maxBuf = this.maxLocs[i];
                                            int k = 0;
                                            for (j = 0; j < this.minCount[i]; ++j) {
                                                minList.add(new int[]{minBuf[k++] + xOffset, minBuf[k++] + yOffset, minBuf[k++]});
                                            }
                                            if (dmax[i] > this.extrema[1][i]) {
                                                maxList.clear();
                                                this.extrema[1][i] = dmax[i];
                                            }
                                            k = 0;
                                            for (j = 0; j < this.maxCount[i]; ++j) {
                                                maxList.add(new int[]{maxBuf[k++] + xOffset, maxBuf[k++] + yOffset, maxBuf[k++]});
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                            if (!name.equalsIgnoreCase("extrema")) break block34;
                            double[][] ext = (double[][])stats;
                            for (int i = 0; i < numBands; ++i) {
                                ext[0][i] = this.extrema[0][i];
                                ext[1][i] = this.extrema[1][i];
                            }
                            break block35;
                        }
                        if (!name.equalsIgnoreCase("minimum")) break block36;
                        double[] min = (double[])stats;
                        for (int i = 0; i < numBands; ++i) {
                            min[i] = this.extrema[0][i];
                        }
                        break block35;
                    }
                    if (!name.equalsIgnoreCase("maximum")) break block37;
                    double[] max = (double[])stats;
                    for (int i = 0; i < numBands; ++i) {
                        max[i] = this.extrema[1][i];
                    }
                    break block35;
                }
                if (!name.equalsIgnoreCase("minLocations")) break block38;
                ArrayList[] minLoc = (ArrayList[])stats;
                for (int i = 0; i < numBands; ++i) {
                    minLoc[i] = this.minLocations[i];
                }
                break block35;
            }
            if (!name.equalsIgnoreCase("maxLocations")) break block35;
            ArrayList[] maxLoc = (ArrayList[])stats;
            for (int i = 0; i < numBands; ++i) {
                maxLoc[i] = this.maxLocations[i];
            }
        }
    }

    protected void initializeState(Raster source) {
        if (this.extrema == null) {
            int numBands = this.sampleModel.getNumBands();
            this.minCount = new int[numBands];
            this.maxCount = new int[numBands];
            this.minLocs = new int[numBands][];
            this.maxLocs = new int[numBands][];
            int size = (this.getTileWidth() + 1) / 2 * this.getTileHeight();
            for (int i = 0; i < numBands; ++i) {
                this.minLocs[i] = new int[size];
                this.maxLocs[i] = new int[size];
            }
            super.initializeState(source);
        }
    }
}

