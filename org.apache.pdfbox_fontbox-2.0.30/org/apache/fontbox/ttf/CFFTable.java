/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.ttf;

import java.io.IOException;
import org.apache.fontbox.cff.CFFFont;
import org.apache.fontbox.cff.CFFParser;
import org.apache.fontbox.ttf.TTFDataStream;
import org.apache.fontbox.ttf.TTFTable;
import org.apache.fontbox.ttf.TrueTypeFont;

public class CFFTable
extends TTFTable {
    public static final String TAG = "CFF ";
    private CFFFont cffFont;

    CFFTable(TrueTypeFont font) {
        super(font);
    }

    @Override
    void read(TrueTypeFont ttf, TTFDataStream data) throws IOException {
        byte[] bytes = data.read((int)this.getLength());
        CFFParser parser = new CFFParser();
        this.cffFont = parser.parse(bytes, new CFFBytesource(this.font)).get(0);
        this.initialized = true;
    }

    public CFFFont getFont() {
        return this.cffFont;
    }

    private static class CFFBytesource
    implements CFFParser.ByteSource {
        private final TrueTypeFont ttf;

        CFFBytesource(TrueTypeFont ttf) {
            this.ttf = ttf;
        }

        @Override
        public byte[] getBytes() throws IOException {
            return this.ttf.getTableBytes(this.ttf.getTableMap().get(CFFTable.TAG));
        }
    }
}

