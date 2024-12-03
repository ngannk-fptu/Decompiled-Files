/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hdgf.streams;

import org.apache.poi.hdgf.chunks.ChunkFactory;
import org.apache.poi.hdgf.pointers.Pointer;
import org.apache.poi.hdgf.streams.Stream;
import org.apache.poi.hdgf.streams.StreamStore;

public final class StringsStream
extends Stream {
    protected StringsStream(Pointer pointer, StreamStore store, ChunkFactory chunkFactory) {
        super(pointer, store);
    }
}

