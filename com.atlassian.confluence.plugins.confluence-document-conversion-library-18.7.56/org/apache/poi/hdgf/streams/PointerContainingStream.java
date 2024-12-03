/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hdgf.streams;

import org.apache.poi.hdgf.chunks.ChunkFactory;
import org.apache.poi.hdgf.pointers.Pointer;
import org.apache.poi.hdgf.pointers.PointerFactory;
import org.apache.poi.hdgf.streams.ChunkStream;
import org.apache.poi.hdgf.streams.Stream;
import org.apache.poi.hdgf.streams.StreamStore;

public class PointerContainingStream
extends Stream {
    private Pointer[] childPointers;
    private Stream[] childStreams;
    private ChunkFactory chunkFactory;
    private PointerFactory pointerFactory;

    protected PointerContainingStream(Pointer pointer, StreamStore store, ChunkFactory chunkFactory, PointerFactory pointerFactory) {
        super(pointer, store);
        this.chunkFactory = chunkFactory;
        this.pointerFactory = pointerFactory;
        this.childPointers = pointerFactory.createContainerPointers(pointer, store.getContents());
    }

    protected Pointer[] getChildPointers() {
        return this.childPointers;
    }

    public Stream[] getPointedToStreams() {
        return this.childStreams;
    }

    public void findChildren(byte[] documentData) {
        this.childStreams = new Stream[this.childPointers.length];
        for (int i = 0; i < this.childPointers.length; ++i) {
            Stream child;
            Pointer ptr = this.childPointers[i];
            this.childStreams[i] = Stream.createStream(ptr, documentData, this.chunkFactory, this.pointerFactory);
            if (this.childStreams[i] instanceof ChunkStream) {
                child = (ChunkStream)this.childStreams[i];
                ((ChunkStream)child).findChunks();
            }
            if (!(this.childStreams[i] instanceof PointerContainingStream)) continue;
            child = (PointerContainingStream)this.childStreams[i];
            ((PointerContainingStream)child).findChildren(documentData);
        }
    }
}

