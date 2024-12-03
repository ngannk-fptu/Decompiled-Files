/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hdgf.streams;

import org.apache.poi.hdgf.chunks.ChunkFactory;
import org.apache.poi.hdgf.pointers.Pointer;
import org.apache.poi.hdgf.pointers.PointerFactory;
import org.apache.poi.hdgf.streams.PointerContainingStream;
import org.apache.poi.hdgf.streams.StreamStore;

public class TrailerStream
extends PointerContainingStream {
    protected TrailerStream(Pointer pointer, StreamStore store, ChunkFactory chunkFactory, PointerFactory pointerFactory) {
        super(pointer, store, chunkFactory, pointerFactory);
    }
}

