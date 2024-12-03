/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.font.ttf;

import com.sun.pdfview.font.ttf.HheaTable;
import com.sun.pdfview.font.ttf.MaxpTable;
import com.sun.pdfview.font.ttf.TrueTypeFont;
import com.sun.pdfview.font.ttf.TrueTypeTable;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class HmtxTable
extends TrueTypeTable {
    short[] advanceWidths;
    short[] leftSideBearings;

    protected HmtxTable(TrueTypeFont ttf) {
        super(1752003704);
        MaxpTable maxp = (MaxpTable)ttf.getTable("maxp");
        int numGlyphs = maxp.getNumGlyphs();
        HheaTable hhea = (HheaTable)ttf.getTable("hhea");
        int numOfLongHorMetrics = hhea.getNumOfLongHorMetrics();
        this.advanceWidths = new short[numOfLongHorMetrics];
        this.leftSideBearings = new short[numGlyphs];
    }

    public short getAdvance(int glyphID) {
        if (glyphID < this.advanceWidths.length) {
            return this.advanceWidths[glyphID];
        }
        return this.advanceWidths[this.advanceWidths.length - 1];
    }

    public short getLeftSideBearing(int glyphID) {
        return this.leftSideBearings[glyphID];
    }

    @Override
    public ByteBuffer getData() {
        int size = this.getLength();
        ByteBuffer buf = ByteBuffer.allocate(size);
        for (int i = 0; i < this.leftSideBearings.length; ++i) {
            if (i < this.advanceWidths.length) {
                buf.putShort(this.advanceWidths[i]);
            }
            buf.putShort(this.leftSideBearings[i]);
        }
        buf.flip();
        return buf;
    }

    @Override
    public void setData(ByteBuffer data) {
        int i;
        for (i = 0; i < this.leftSideBearings.length && data.hasRemaining(); ++i) {
            if (i < this.advanceWidths.length) {
                this.advanceWidths[i] = data.getShort();
            } else if (data.remaining() < 2) break;
            this.leftSideBearings[i] = data.getShort();
        }
        if (i < this.advanceWidths.length) {
            Arrays.fill(this.advanceWidths, i, this.advanceWidths.length - 1, (short)0);
        }
        if (i < this.leftSideBearings.length) {
            Arrays.fill(this.leftSideBearings, i, this.leftSideBearings.length - 1, (short)0);
        }
    }

    @Override
    public int getLength() {
        return this.advanceWidths.length * 2 + this.leftSideBearings.length * 2;
    }
}

