/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.font.ttf;

import com.sun.pdfview.font.ttf.HeadTable;
import com.sun.pdfview.font.ttf.MaxpTable;
import com.sun.pdfview.font.ttf.TrueTypeFont;
import com.sun.pdfview.font.ttf.TrueTypeTable;
import java.nio.ByteBuffer;

public class LocaTable
extends TrueTypeTable {
    private boolean isLong;
    private int[] offsets;

    protected LocaTable(TrueTypeFont ttf) {
        super(1819239265);
        MaxpTable maxp = (MaxpTable)ttf.getTable("maxp");
        int numGlyphs = maxp.getNumGlyphs();
        HeadTable head = (HeadTable)ttf.getTable("head");
        short format = head.getIndexToLocFormat();
        this.isLong = format == 1;
        this.offsets = new int[numGlyphs + 1];
    }

    public int getOffset(int glyphID) {
        return this.offsets[glyphID];
    }

    public int getSize(int glyphID) {
        return this.offsets[glyphID + 1] - this.offsets[glyphID];
    }

    public boolean isLongFormat() {
        return this.isLong;
    }

    @Override
    public ByteBuffer getData() {
        int size = this.getLength();
        ByteBuffer buf = ByteBuffer.allocate(size);
        for (int i = 0; i < this.offsets.length; ++i) {
            if (this.isLongFormat()) {
                buf.putInt(this.offsets[i]);
                continue;
            }
            buf.putShort((short)(this.offsets[i] / 2));
        }
        buf.flip();
        return buf;
    }

    @Override
    public void setData(ByteBuffer data) {
        for (int i = 0; i < this.offsets.length; ++i) {
            this.offsets[i] = this.isLongFormat() ? data.getInt() : 2 * (0xFFFF & data.getShort());
        }
    }

    @Override
    public int getLength() {
        if (this.isLongFormat()) {
            return this.offsets.length * 4;
        }
        return this.offsets.length * 2;
    }
}

