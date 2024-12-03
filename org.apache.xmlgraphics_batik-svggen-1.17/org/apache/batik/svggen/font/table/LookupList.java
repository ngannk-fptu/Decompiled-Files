/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.batik.svggen.font.table.Feature;
import org.apache.batik.svggen.font.table.Lookup;
import org.apache.batik.svggen.font.table.LookupSubtableFactory;

public class LookupList {
    private int lookupCount;
    private int[] lookupOffsets;
    private Lookup[] lookups;

    public LookupList(RandomAccessFile raf, int offset, LookupSubtableFactory factory) throws IOException {
        int i;
        raf.seek(offset);
        this.lookupCount = raf.readUnsignedShort();
        this.lookupOffsets = new int[this.lookupCount];
        this.lookups = new Lookup[this.lookupCount];
        for (i = 0; i < this.lookupCount; ++i) {
            this.lookupOffsets[i] = raf.readUnsignedShort();
        }
        for (i = 0; i < this.lookupCount; ++i) {
            this.lookups[i] = new Lookup(factory, raf, offset + this.lookupOffsets[i]);
        }
    }

    public Lookup getLookup(Feature feature, int index) {
        if (feature.getLookupCount() > index) {
            int i = feature.getLookupListIndex(index);
            return this.lookups[i];
        }
        return null;
    }
}

