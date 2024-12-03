/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen.font.table;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.batik.svggen.font.table.DirectoryEntry;
import org.apache.batik.svggen.font.table.Table;

public class LocaTable
implements Table {
    private byte[] buf = null;
    private int[] offsets = null;
    private short factor = 0;

    protected LocaTable(DirectoryEntry de, RandomAccessFile raf) throws IOException {
        raf.seek(de.getOffset());
        this.buf = new byte[de.getLength()];
        raf.read(this.buf);
    }

    public void init(int numGlyphs, boolean shortEntries) {
        if (this.buf == null) {
            return;
        }
        this.offsets = new int[numGlyphs + 1];
        ByteArrayInputStream bais = new ByteArrayInputStream(this.buf);
        if (shortEntries) {
            this.factor = (short)2;
            for (int i = 0; i <= numGlyphs; ++i) {
                this.offsets[i] = bais.read() << 8 | bais.read();
            }
        } else {
            this.factor = 1;
            for (int i = 0; i <= numGlyphs; ++i) {
                this.offsets[i] = bais.read() << 24 | bais.read() << 16 | bais.read() << 8 | bais.read();
            }
        }
        this.buf = null;
    }

    public int getOffset(int i) {
        if (this.offsets == null) {
            return 0;
        }
        return this.offsets[i] * this.factor;
    }

    @Override
    public int getType() {
        return 1819239265;
    }
}

