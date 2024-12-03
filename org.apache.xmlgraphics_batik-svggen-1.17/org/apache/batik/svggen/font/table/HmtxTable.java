/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen.font.table;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.batik.svggen.font.table.DirectoryEntry;
import org.apache.batik.svggen.font.table.Table;

public class HmtxTable
implements Table {
    private byte[] buf = null;
    private int[] hMetrics = null;
    private short[] leftSideBearing = null;

    protected HmtxTable(DirectoryEntry de, RandomAccessFile raf) throws IOException {
        raf.seek(de.getOffset());
        this.buf = new byte[de.getLength()];
        raf.read(this.buf);
    }

    public void init(int numberOfHMetrics, int lsbCount) {
        int i;
        if (this.buf == null) {
            return;
        }
        this.hMetrics = new int[numberOfHMetrics];
        ByteArrayInputStream bais = new ByteArrayInputStream(this.buf);
        for (i = 0; i < numberOfHMetrics; ++i) {
            this.hMetrics[i] = bais.read() << 24 | bais.read() << 16 | bais.read() << 8 | bais.read();
        }
        if (lsbCount > 0) {
            this.leftSideBearing = new short[lsbCount];
            for (i = 0; i < lsbCount; ++i) {
                this.leftSideBearing[i] = (short)(bais.read() << 8 | bais.read());
            }
        }
        this.buf = null;
    }

    public int getAdvanceWidth(int i) {
        if (this.hMetrics == null) {
            return 0;
        }
        if (i < this.hMetrics.length) {
            return this.hMetrics[i] >> 16;
        }
        return this.hMetrics[this.hMetrics.length - 1] >> 16;
    }

    public short getLeftSideBearing(int i) {
        if (this.hMetrics == null) {
            return 0;
        }
        if (i < this.hMetrics.length) {
            return (short)(this.hMetrics[i] & 0xFFFF);
        }
        return this.leftSideBearing[i - this.hMetrics.length];
    }

    @Override
    public int getType() {
        return 1752003704;
    }
}

