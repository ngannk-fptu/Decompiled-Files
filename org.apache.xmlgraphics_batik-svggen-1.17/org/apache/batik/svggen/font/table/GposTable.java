/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.batik.svggen.font.table.DirectoryEntry;
import org.apache.batik.svggen.font.table.Table;

public class GposTable
implements Table {
    protected GposTable(DirectoryEntry de, RandomAccessFile raf) throws IOException {
        raf.seek(de.getOffset());
        raf.readInt();
        raf.readInt();
        raf.readInt();
        raf.readInt();
    }

    @Override
    public int getType() {
        return 1196445523;
    }

    public String toString() {
        return "GPOS";
    }
}

