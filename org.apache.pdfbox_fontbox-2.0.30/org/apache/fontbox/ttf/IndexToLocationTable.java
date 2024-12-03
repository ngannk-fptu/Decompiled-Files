/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.ttf;

import java.io.IOException;
import org.apache.fontbox.ttf.HeaderTable;
import org.apache.fontbox.ttf.TTFDataStream;
import org.apache.fontbox.ttf.TTFTable;
import org.apache.fontbox.ttf.TrueTypeFont;

public class IndexToLocationTable
extends TTFTable {
    private static final short SHORT_OFFSETS = 0;
    private static final short LONG_OFFSETS = 1;
    public static final String TAG = "loca";
    private long[] offsets;

    IndexToLocationTable(TrueTypeFont font) {
        super(font);
    }

    @Override
    void read(TrueTypeFont ttf, TTFDataStream data) throws IOException {
        HeaderTable head = ttf.getHeader();
        if (head == null) {
            throw new IOException("Could not get head table");
        }
        int numGlyphs = ttf.getNumberOfGlyphs();
        this.offsets = new long[numGlyphs + 1];
        for (int i = 0; i < numGlyphs + 1; ++i) {
            if (head.getIndexToLocFormat() == 0) {
                this.offsets[i] = data.readUnsignedShort() * 2;
                continue;
            }
            if (head.getIndexToLocFormat() == 1) {
                this.offsets[i] = data.readUnsignedInt();
                continue;
            }
            throw new IOException("Error:TTF.loca unknown offset format.");
        }
        this.initialized = true;
    }

    public long[] getOffsets() {
        return this.offsets;
    }

    public void setOffsets(long[] offsetsValue) {
        this.offsets = offsetsValue;
    }
}

