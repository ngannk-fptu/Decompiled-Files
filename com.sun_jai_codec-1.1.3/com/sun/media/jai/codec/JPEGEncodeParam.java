/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codec;

import com.sun.media.jai.codec.ImageEncodeParam;
import com.sun.media.jai.codec.JaiI18N;

public class JPEGEncodeParam
implements ImageEncodeParam {
    private static int JPEG_MAX_BANDS = 3;
    private int[] hSamp = new int[JPEG_MAX_BANDS];
    private int[] vSamp = new int[JPEG_MAX_BANDS];
    private int[][] qTab;
    private int[] qTabSlot = new int[JPEG_MAX_BANDS];
    private float qual;
    private int rstInterval;
    private boolean writeImageOnly;
    private boolean writeTablesOnly;
    private boolean writeJFIFHeader;
    private boolean qualitySet;
    private boolean[] qTabSet;

    public JPEGEncodeParam() {
        this.qTab = new int[JPEG_MAX_BANDS][];
        this.qTabSet = new boolean[JPEG_MAX_BANDS];
        this.hSamp[0] = 1;
        this.vSamp[0] = 1;
        this.qTabSlot[0] = 0;
        this.qTab[0] = null;
        this.qTabSet[0] = false;
        this.hSamp[1] = 2;
        this.vSamp[1] = 2;
        this.qTabSlot[1] = 1;
        this.qTab[1] = null;
        this.qTabSet[1] = false;
        this.hSamp[2] = 2;
        this.vSamp[2] = 2;
        this.qTabSlot[2] = 1;
        this.qTab[2] = null;
        this.qTabSet[2] = false;
        this.qual = 0.75f;
        this.rstInterval = 0;
        this.writeImageOnly = false;
        this.writeTablesOnly = false;
        this.writeJFIFHeader = true;
    }

    public void setHorizontalSubsampling(int component, int subsample) {
        this.hSamp[component] = subsample;
    }

    public int getHorizontalSubsampling(int component) {
        return this.hSamp[component];
    }

    public void setVerticalSubsampling(int component, int subsample) {
        this.vSamp[component] = subsample;
    }

    public int getVerticalSubsampling(int component) {
        return this.vSamp[component];
    }

    public void setLumaQTable(int[] qTable) {
        this.setQTable(0, 0, qTable);
        this.qTabSet[0] = true;
        this.qualitySet = false;
    }

    public void setChromaQTable(int[] qTable) {
        this.setQTable(1, 1, qTable);
        this.setQTable(2, 1, qTable);
        this.qTabSet[1] = true;
        this.qTabSet[2] = true;
        this.qualitySet = false;
    }

    public void setQTable(int component, int tableSlot, int[] qTable) {
        this.qTab[component] = (int[])qTable.clone();
        this.qTabSlot[component] = tableSlot;
        this.qTabSet[component] = true;
        this.qualitySet = false;
    }

    public boolean isQTableSet(int component) {
        return this.qTabSet[component];
    }

    public int[] getQTable(int component) {
        if (!this.qTabSet[component]) {
            throw new IllegalStateException(JaiI18N.getString("JPEGEncodeParam0"));
        }
        return this.qTab[component];
    }

    public int getQTableSlot(int component) {
        if (!this.qTabSet[component]) {
            throw new IllegalStateException(JaiI18N.getString("JPEGEncodeParam0"));
        }
        return this.qTabSlot[component];
    }

    public void setRestartInterval(int restartInterval) {
        this.rstInterval = restartInterval;
    }

    public int getRestartInterval() {
        return this.rstInterval;
    }

    public void setQuality(float quality) {
        this.qual = quality;
        for (int i = 0; i < JPEG_MAX_BANDS; ++i) {
            this.qTabSet[i] = false;
        }
        this.qualitySet = true;
    }

    public boolean isQualitySet() {
        return this.qualitySet;
    }

    public float getQuality() {
        return this.qual;
    }

    public void setWriteTablesOnly(boolean tablesOnly) {
        this.writeTablesOnly = tablesOnly;
    }

    public boolean getWriteTablesOnly() {
        return this.writeTablesOnly;
    }

    public void setWriteImageOnly(boolean imageOnly) {
        this.writeImageOnly = imageOnly;
    }

    public boolean getWriteImageOnly() {
        return this.writeImageOnly;
    }

    public void setWriteJFIFHeader(boolean writeJFIF) {
        this.writeJFIFHeader = writeJFIF;
    }

    public boolean getWriteJFIFHeader() {
        return this.writeJFIFHeader;
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
}

