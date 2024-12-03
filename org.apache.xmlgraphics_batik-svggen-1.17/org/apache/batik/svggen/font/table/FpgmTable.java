/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.batik.svggen.font.table.DirectoryEntry;
import org.apache.batik.svggen.font.table.Program;
import org.apache.batik.svggen.font.table.Table;

public class FpgmTable
extends Program
implements Table {
    protected FpgmTable(DirectoryEntry de, RandomAccessFile raf) throws IOException {
        raf.seek(de.getOffset());
        this.readInstructions(raf, de.getLength());
    }

    @Override
    public int getType() {
        return 1718642541;
    }
}

