/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hdgf.streams;

import java.io.IOException;
import org.apache.poi.hdgf.chunks.ChunkFactory;
import org.apache.poi.hdgf.exceptions.HDGFException;
import org.apache.poi.hdgf.pointers.Pointer;
import org.apache.poi.hdgf.pointers.PointerFactory;
import org.apache.poi.hdgf.streams.ChunkStream;
import org.apache.poi.hdgf.streams.CompressedStreamStore;
import org.apache.poi.hdgf.streams.PointerContainingStream;
import org.apache.poi.hdgf.streams.StreamStore;
import org.apache.poi.hdgf.streams.StringsStream;
import org.apache.poi.hdgf.streams.TrailerStream;
import org.apache.poi.hdgf.streams.UnknownStream;

public abstract class Stream {
    private Pointer pointer;
    private StreamStore store;

    public Pointer getPointer() {
        return this.pointer;
    }

    protected StreamStore getStore() {
        return this.store;
    }

    public StreamStore _getStore() {
        return this.store;
    }

    public int _getContentsLength() {
        return this.store.getContents().length;
    }

    protected Stream(Pointer pointer, StreamStore store) {
        this.pointer = pointer;
        this.store = store;
    }

    public static Stream createStream(Pointer pointer, byte[] documentData, ChunkFactory chunkFactory, PointerFactory pointerFactory) {
        StreamStore store;
        if (pointer.destinationCompressed()) {
            try {
                store = new CompressedStreamStore(documentData, pointer.getOffset(), pointer.getLength());
            }
            catch (IOException e) {
                throw new HDGFException(e);
            }
        } else {
            store = new StreamStore(documentData, pointer.getOffset(), pointer.getLength());
        }
        if (pointer.getType() == 20) {
            return new TrailerStream(pointer, store, chunkFactory, pointerFactory);
        }
        if (pointer.destinationHasPointers()) {
            return new PointerContainingStream(pointer, store, chunkFactory, pointerFactory);
        }
        if (pointer.destinationHasChunks()) {
            return new ChunkStream(pointer, store, chunkFactory);
        }
        if (pointer.destinationHasStrings()) {
            return new StringsStream(pointer, store, chunkFactory);
        }
        return new UnknownStream(pointer, store);
    }
}

