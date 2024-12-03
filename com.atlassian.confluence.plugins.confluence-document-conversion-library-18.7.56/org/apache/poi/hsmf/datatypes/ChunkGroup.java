/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hsmf.datatypes;

import org.apache.poi.hsmf.datatypes.Chunk;

public interface ChunkGroup {
    public Chunk[] getChunks();

    public void record(Chunk var1);

    public void chunksComplete();
}

