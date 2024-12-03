/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hdgf.streams;

import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hdgf.chunks.Chunk;
import org.apache.poi.hdgf.chunks.ChunkFactory;
import org.apache.poi.hdgf.chunks.ChunkHeader;
import org.apache.poi.hdgf.pointers.Pointer;
import org.apache.poi.hdgf.streams.Stream;
import org.apache.poi.hdgf.streams.StreamStore;

public final class ChunkStream
extends Stream {
    private static final Logger LOG = LogManager.getLogger(ChunkStream.class);
    private final ChunkFactory chunkFactory;
    private Chunk[] chunks;

    ChunkStream(Pointer pointer, StreamStore store, ChunkFactory chunkFactory) {
        super(pointer, store);
        this.chunkFactory = chunkFactory;
        store.copyBlockHeaderToContents();
    }

    public Chunk[] getChunks() {
        return this.chunks;
    }

    public void findChunks() {
        ArrayList<Chunk> chunksA = new ArrayList<Chunk>();
        if (this.getPointer().getOffset() == 25779) {
            int i = 0;
            ++i;
        }
        int pos = 0;
        byte[] contents = this.getStore().getContents();
        try {
            while (pos < contents.length) {
                int headerSize = ChunkHeader.getHeaderSize(this.chunkFactory.getVersion());
                if (pos + headerSize <= contents.length) {
                    Chunk chunk = this.chunkFactory.createChunk(contents, pos);
                    chunksA.add(chunk);
                    pos += chunk.getOnDiskSize();
                    continue;
                }
                LOG.atWarn().log("Needed {} bytes to create the next chunk header, but only found {} bytes, ignoring rest of data", (Object)Unbox.box(headerSize), (Object)Unbox.box(contents.length - pos));
                pos = contents.length;
            }
        }
        catch (Exception e) {
            LOG.atError().withThrowable(e).log("Failed to create chunk at {}, ignoring rest of data.", (Object)Unbox.box(pos));
        }
        this.chunks = chunksA.toArray(new Chunk[0]);
    }
}

