/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen.font.table;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.batik.svggen.font.table.DirectoryEntry;
import org.apache.batik.svggen.font.table.GlyfCompositeDescript;
import org.apache.batik.svggen.font.table.GlyfDescript;
import org.apache.batik.svggen.font.table.GlyfSimpleDescript;
import org.apache.batik.svggen.font.table.LocaTable;
import org.apache.batik.svggen.font.table.Table;

public class GlyfTable
implements Table {
    private byte[] buf = null;
    private GlyfDescript[] descript;

    protected GlyfTable(DirectoryEntry de, RandomAccessFile raf) throws IOException {
        raf.seek(de.getOffset());
        this.buf = new byte[de.getLength()];
        raf.read(this.buf);
    }

    public void init(int numGlyphs, LocaTable loca) {
        int i;
        if (this.buf == null) {
            return;
        }
        this.descript = new GlyfDescript[numGlyphs];
        ByteArrayInputStream bais = new ByteArrayInputStream(this.buf);
        for (i = 0; i < numGlyphs; ++i) {
            int len = loca.getOffset(i + 1) - loca.getOffset(i);
            if (len <= 0) continue;
            bais.reset();
            bais.skip(loca.getOffset(i));
            short numberOfContours = (short)(bais.read() << 8 | bais.read());
            this.descript[i] = numberOfContours >= 0 ? new GlyfSimpleDescript(this, numberOfContours, bais) : new GlyfCompositeDescript(this, bais);
        }
        this.buf = null;
        for (i = 0; i < numGlyphs; ++i) {
            if (this.descript[i] == null) continue;
            this.descript[i].resolve();
        }
    }

    public GlyfDescript getDescription(int i) {
        return this.descript[i];
    }

    @Override
    public int getType() {
        return 1735162214;
    }
}

