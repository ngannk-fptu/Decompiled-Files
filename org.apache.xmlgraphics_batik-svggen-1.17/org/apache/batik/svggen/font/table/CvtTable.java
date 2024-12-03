/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.batik.svggen.font.table.DirectoryEntry;
import org.apache.batik.svggen.font.table.Table;

public class CvtTable
implements Table {
    private short[] values;

    protected CvtTable(DirectoryEntry de, RandomAccessFile raf) throws IOException {
        raf.seek(de.getOffset());
        int len = de.getLength() / 2;
        this.values = new short[len];
        for (int i = 0; i < len; ++i) {
            this.values[i] = raf.readShort();
        }
    }

    @Override
    public int getType() {
        return 1668707360;
    }

    public short[] getValues() {
        return this.values;
    }
}

