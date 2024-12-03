/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.batik.svggen.font.table.ClassDef;
import org.apache.batik.svggen.font.table.RangeRecord;

public class ClassDefFormat2
extends ClassDef {
    private int classRangeCount;
    private RangeRecord[] classRangeRecords;

    public ClassDefFormat2(RandomAccessFile raf) throws IOException {
        this.classRangeCount = raf.readUnsignedShort();
        this.classRangeRecords = new RangeRecord[this.classRangeCount];
        for (int i = 0; i < this.classRangeCount; ++i) {
            this.classRangeRecords[i] = new RangeRecord(raf);
        }
    }

    @Override
    public int getFormat() {
        return 2;
    }
}

