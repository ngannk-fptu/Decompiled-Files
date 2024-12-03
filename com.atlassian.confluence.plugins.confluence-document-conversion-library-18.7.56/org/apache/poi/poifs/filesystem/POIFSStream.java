/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.poifs.filesystem;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.poi.poifs.filesystem.BlockStore;

public class POIFSStream
implements Iterable<ByteBuffer> {
    private final BlockStore blockStore;
    private int startBlock;
    private OutputStream outStream;

    public POIFSStream(BlockStore blockStore, int startBlock) {
        this.blockStore = blockStore;
        this.startBlock = startBlock;
    }

    public POIFSStream(BlockStore blockStore) {
        this.blockStore = blockStore;
        this.startBlock = -2;
    }

    public int getStartBlock() {
        return this.startBlock;
    }

    @Override
    public Iterator<ByteBuffer> iterator() {
        return this.getBlockIterator();
    }

    Iterator<ByteBuffer> getBlockIterator() {
        if (this.startBlock == -2) {
            throw new IllegalStateException("Can't read from a new stream before it has been written to");
        }
        return new StreamBlockByteBufferIterator(this.startBlock);
    }

    Iterator<Integer> getBlockOffsetIterator() {
        if (this.startBlock == -2) {
            throw new IllegalStateException("Can't read from a new stream before it has been written to");
        }
        return new StreamBlockOffsetIterator(this.startBlock);
    }

    void updateContents(byte[] contents) throws IOException {
        OutputStream os = this.getOutputStream();
        os.write(contents);
        os.close();
    }

    public OutputStream getOutputStream() throws IOException {
        if (this.outStream == null) {
            this.outStream = new StreamBlockByteBuffer();
        }
        return this.outStream;
    }

    public void free() throws IOException {
        BlockStore.ChainLoopDetector loopDetector = this.blockStore.getChainLoopDetector();
        this.free(loopDetector);
    }

    private void free(BlockStore.ChainLoopDetector loopDetector) {
        int nextBlock = this.startBlock;
        while (nextBlock != -2) {
            int thisBlock = nextBlock;
            loopDetector.claim(thisBlock);
            nextBlock = this.blockStore.getNextBlock(thisBlock);
            this.blockStore.setNextBlock(thisBlock, -1);
        }
        this.startBlock = -2;
    }

    protected class StreamBlockByteBuffer
    extends OutputStream {
        byte[] oneByte = new byte[1];
        ByteBuffer buffer;
        BlockStore.ChainLoopDetector loopDetector;
        int prevBlock;
        int nextBlock;

        StreamBlockByteBuffer() throws IOException {
            this.loopDetector = POIFSStream.this.blockStore.getChainLoopDetector();
            this.prevBlock = -2;
            this.nextBlock = POIFSStream.this.startBlock;
        }

        void createBlockIfNeeded() throws IOException {
            if (this.buffer != null && this.buffer.hasRemaining()) {
                return;
            }
            int thisBlock = this.nextBlock;
            if (thisBlock == -2) {
                thisBlock = POIFSStream.this.blockStore.getFreeBlock();
                this.loopDetector.claim(thisBlock);
                this.nextBlock = -2;
                if (this.prevBlock != -2) {
                    POIFSStream.this.blockStore.setNextBlock(this.prevBlock, thisBlock);
                }
                POIFSStream.this.blockStore.setNextBlock(thisBlock, -2);
                if (POIFSStream.this.startBlock == -2) {
                    POIFSStream.this.startBlock = thisBlock;
                }
            } else {
                this.loopDetector.claim(thisBlock);
                this.nextBlock = POIFSStream.this.blockStore.getNextBlock(thisBlock);
            }
            if (this.buffer != null) {
                POIFSStream.this.blockStore.releaseBuffer(this.buffer);
            }
            this.buffer = POIFSStream.this.blockStore.createBlockIfNeeded(thisBlock);
            this.prevBlock = thisBlock;
        }

        @Override
        public void write(int b) throws IOException {
            this.oneByte[0] = (byte)(b & 0xFF);
            this.write(this.oneByte);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            int writeBytes;
            if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (len == 0) {
                return;
            }
            do {
                this.createBlockIfNeeded();
                writeBytes = Math.min(this.buffer.remaining(), len);
                this.buffer.put(b, off, writeBytes);
                off += writeBytes;
            } while ((len -= writeBytes) > 0);
        }

        @Override
        public void close() throws IOException {
            POIFSStream toFree = new POIFSStream(POIFSStream.this.blockStore, this.nextBlock);
            toFree.free(this.loopDetector);
            if (this.prevBlock != -2) {
                POIFSStream.this.blockStore.setNextBlock(this.prevBlock, -2);
            }
        }
    }

    private class StreamBlockByteBufferIterator
    implements Iterator<ByteBuffer> {
        private final BlockStore.ChainLoopDetector loopDetector;
        private int nextBlock;

        StreamBlockByteBufferIterator(int firstBlock) {
            this.nextBlock = firstBlock;
            try {
                this.loopDetector = POIFSStream.this.blockStore.getChainLoopDetector();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean hasNext() {
            return this.nextBlock != -2;
        }

        @Override
        public ByteBuffer next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException("Can't read past the end of the stream");
            }
            try {
                this.loopDetector.claim(this.nextBlock);
                ByteBuffer data = POIFSStream.this.blockStore.getBlockAt(this.nextBlock);
                this.nextBlock = POIFSStream.this.blockStore.getNextBlock(this.nextBlock);
                return data;
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private class StreamBlockOffsetIterator
    implements Iterator<Integer> {
        private final BlockStore.ChainLoopDetector loopDetector;
        private int nextBlock;

        StreamBlockOffsetIterator(int firstBlock) {
            this.nextBlock = firstBlock;
            try {
                this.loopDetector = POIFSStream.this.blockStore.getChainLoopDetector();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean hasNext() {
            return this.nextBlock != -2;
        }

        @Override
        public Integer next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException("Can't read past the end of the stream");
            }
            this.loopDetector.claim(this.nextBlock);
            int currentBlock = this.nextBlock;
            this.nextBlock = POIFSStream.this.blockStore.getNextBlock(this.nextBlock);
            return currentBlock;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

