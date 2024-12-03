/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.ttf;

import java.io.IOException;
import org.apache.fontbox.ttf.HorizontalHeaderTable;
import org.apache.fontbox.ttf.TTFDataStream;
import org.apache.fontbox.ttf.TTFTable;
import org.apache.fontbox.ttf.TrueTypeFont;

public class HorizontalMetricsTable
extends TTFTable {
    public static final String TAG = "hmtx";
    private int[] advanceWidth;
    private short[] leftSideBearing;
    private short[] nonHorizontalLeftSideBearing;
    private int numHMetrics;

    HorizontalMetricsTable(TrueTypeFont font) {
        super(font);
    }

    @Override
    void read(TrueTypeFont ttf, TTFDataStream data) throws IOException {
        HorizontalHeaderTable hHeader = ttf.getHorizontalHeader();
        if (hHeader == null) {
            throw new IOException("Could not get hmtx table");
        }
        this.numHMetrics = hHeader.getNumberOfHMetrics();
        int numGlyphs = ttf.getNumberOfGlyphs();
        int bytesRead = 0;
        this.advanceWidth = new int[this.numHMetrics];
        this.leftSideBearing = new short[this.numHMetrics];
        for (int i = 0; i < this.numHMetrics; ++i) {
            this.advanceWidth[i] = data.readUnsignedShort();
            this.leftSideBearing[i] = data.readSignedShort();
            bytesRead += 4;
        }
        int numberNonHorizontal = numGlyphs - this.numHMetrics;
        if (numberNonHorizontal < 0) {
            numberNonHorizontal = numGlyphs;
        }
        this.nonHorizontalLeftSideBearing = new short[numberNonHorizontal];
        if ((long)bytesRead < this.getLength()) {
            for (int i = 0; i < numberNonHorizontal; ++i) {
                if ((long)bytesRead >= this.getLength()) continue;
                this.nonHorizontalLeftSideBearing[i] = data.readSignedShort();
                bytesRead += 2;
            }
        }
        this.initialized = true;
    }

    public int getAdvanceWidth(int gid) {
        if (this.advanceWidth.length == 0) {
            return 250;
        }
        if (gid < this.numHMetrics) {
            return this.advanceWidth[gid];
        }
        return this.advanceWidth[this.advanceWidth.length - 1];
    }

    public int getLeftSideBearing(int gid) {
        if (this.leftSideBearing.length == 0) {
            return 0;
        }
        if (gid < this.numHMetrics) {
            return this.leftSideBearing[gid];
        }
        return this.nonHorizontalLeftSideBearing[gid - this.numHMetrics];
    }
}

