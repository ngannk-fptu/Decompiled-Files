/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.batik.svggen.font.table.ClassDefFormat1;
import org.apache.batik.svggen.font.table.ClassDefFormat2;

public abstract class ClassDef {
    public abstract int getFormat();

    protected static ClassDef read(RandomAccessFile raf) throws IOException {
        ClassDef c = null;
        int format = raf.readUnsignedShort();
        if (format == 1) {
            c = new ClassDefFormat1(raf);
        } else if (format == 2) {
            c = new ClassDefFormat2(raf);
        }
        return c;
    }
}

