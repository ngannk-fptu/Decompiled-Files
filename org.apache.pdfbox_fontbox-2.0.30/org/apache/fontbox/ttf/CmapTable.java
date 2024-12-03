/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.ttf;

import java.io.IOException;
import org.apache.fontbox.ttf.CmapSubtable;
import org.apache.fontbox.ttf.TTFDataStream;
import org.apache.fontbox.ttf.TTFTable;
import org.apache.fontbox.ttf.TrueTypeFont;

public class CmapTable
extends TTFTable {
    public static final String TAG = "cmap";
    public static final int PLATFORM_UNICODE = 0;
    public static final int PLATFORM_MACINTOSH = 1;
    public static final int PLATFORM_WINDOWS = 3;
    public static final int ENCODING_MAC_ROMAN = 0;
    public static final int ENCODING_WIN_SYMBOL = 0;
    public static final int ENCODING_WIN_UNICODE_BMP = 1;
    public static final int ENCODING_WIN_SHIFT_JIS = 2;
    public static final int ENCODING_WIN_BIG5 = 3;
    public static final int ENCODING_WIN_PRC = 4;
    public static final int ENCODING_WIN_WANSUNG = 5;
    public static final int ENCODING_WIN_JOHAB = 6;
    public static final int ENCODING_WIN_UNICODE_FULL = 10;
    public static final int ENCODING_UNICODE_1_0 = 0;
    public static final int ENCODING_UNICODE_1_1 = 1;
    public static final int ENCODING_UNICODE_2_0_BMP = 3;
    public static final int ENCODING_UNICODE_2_0_FULL = 4;
    private CmapSubtable[] cmaps;

    CmapTable(TrueTypeFont font) {
        super(font);
    }

    @Override
    void read(TrueTypeFont ttf, TTFDataStream data) throws IOException {
        int version = data.readUnsignedShort();
        int numberOfTables = data.readUnsignedShort();
        this.cmaps = new CmapSubtable[numberOfTables];
        for (int i = 0; i < numberOfTables; ++i) {
            CmapSubtable cmap = new CmapSubtable();
            cmap.initData(data);
            this.cmaps[i] = cmap;
        }
        int numberOfGlyphs = ttf.getNumberOfGlyphs();
        for (int i = 0; i < numberOfTables; ++i) {
            this.cmaps[i].initSubtable(this, numberOfGlyphs, data);
        }
        this.initialized = true;
    }

    public CmapSubtable[] getCmaps() {
        return this.cmaps;
    }

    public void setCmaps(CmapSubtable[] cmapsValue) {
        this.cmaps = cmapsValue;
    }

    public CmapSubtable getSubtable(int platformId, int platformEncodingId) {
        for (CmapSubtable cmap : this.cmaps) {
            if (cmap.getPlatformId() != platformId || cmap.getPlatformEncodingId() != platformEncodingId) continue;
            return cmap;
        }
        return null;
    }
}

