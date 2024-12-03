/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.batik.svggen.font.table.LookupSubtable;

public interface LookupSubtableFactory {
    public LookupSubtable read(int var1, RandomAccessFile var2, int var3) throws IOException;
}

