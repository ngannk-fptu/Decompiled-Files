/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import java.io.EOFException;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.WeakIdentityMap;

abstract class ByteBufferIndexInput
extends IndexInput {
    protected final BufferCleaner cleaner;
    protected final long length;
    protected final long chunkSizeMask;
    protected final int chunkSizePower;
    protected ByteBuffer[] buffers;
    protected int curBufIndex = -1;
    protected ByteBuffer curBuf;
    private int offset;
    private String sliceDescription;
    private boolean isClone = false;
    private final WeakIdentityMap<ByteBufferIndexInput, Boolean> clones;

    public static ByteBufferIndexInput newInstance(String resourceDescription, ByteBuffer[] buffers, long length, int chunkSizePower, BufferCleaner cleaner, boolean trackClones) {
        WeakIdentityMap<ByteBufferIndexInput, Boolean> clones;
        WeakIdentityMap<ByteBufferIndexInput, Boolean> weakIdentityMap = clones = trackClones ? WeakIdentityMap.newConcurrentHashMap() : null;
        if (buffers.length == 1) {
            return new SingleBufferImpl(resourceDescription, buffers[0], length, chunkSizePower, cleaner, clones);
        }
        return new MultiBufferImpl(resourceDescription, buffers, 0, length, chunkSizePower, cleaner, clones);
    }

    ByteBufferIndexInput(String resourceDescription, ByteBuffer[] buffers, long length, int chunkSizePower, BufferCleaner cleaner, WeakIdentityMap<ByteBufferIndexInput, Boolean> clones) {
        super(resourceDescription);
        this.buffers = buffers;
        this.length = length;
        this.chunkSizePower = chunkSizePower;
        this.chunkSizeMask = (1L << chunkSizePower) - 1L;
        this.clones = clones;
        this.cleaner = cleaner;
        assert (chunkSizePower >= 0 && chunkSizePower <= 30);
        assert (length >>> chunkSizePower < Integer.MAX_VALUE);
    }

    @Override
    public final byte readByte() throws IOException {
        try {
            return this.curBuf.get();
        }
        catch (BufferUnderflowException e) {
            do {
                ++this.curBufIndex;
                if (this.curBufIndex >= this.buffers.length) {
                    throw new EOFException("read past EOF: " + this);
                }
                this.curBuf = this.buffers[this.curBufIndex];
                this.curBuf.position(0);
            } while (!this.curBuf.hasRemaining());
            return this.curBuf.get();
        }
        catch (NullPointerException npe) {
            throw new AlreadyClosedException("Already closed: " + this);
        }
    }

    @Override
    public final void readBytes(byte[] b, int offset, int len) throws IOException {
        try {
            this.curBuf.get(b, offset, len);
        }
        catch (BufferUnderflowException e) {
            int curAvail = this.curBuf.remaining();
            while (len > curAvail) {
                this.curBuf.get(b, offset, curAvail);
                len -= curAvail;
                offset += curAvail;
                ++this.curBufIndex;
                if (this.curBufIndex >= this.buffers.length) {
                    throw new EOFException("read past EOF: " + this);
                }
                this.curBuf = this.buffers[this.curBufIndex];
                this.curBuf.position(0);
                curAvail = this.curBuf.remaining();
            }
            this.curBuf.get(b, offset, len);
        }
        catch (NullPointerException npe) {
            throw new AlreadyClosedException("Already closed: " + this);
        }
    }

    @Override
    public final short readShort() throws IOException {
        try {
            return this.curBuf.getShort();
        }
        catch (BufferUnderflowException e) {
            return super.readShort();
        }
        catch (NullPointerException npe) {
            throw new AlreadyClosedException("Already closed: " + this);
        }
    }

    @Override
    public final int readInt() throws IOException {
        try {
            return this.curBuf.getInt();
        }
        catch (BufferUnderflowException e) {
            return super.readInt();
        }
        catch (NullPointerException npe) {
            throw new AlreadyClosedException("Already closed: " + this);
        }
    }

    @Override
    public final long readLong() throws IOException {
        try {
            return this.curBuf.getLong();
        }
        catch (BufferUnderflowException e) {
            return super.readLong();
        }
        catch (NullPointerException npe) {
            throw new AlreadyClosedException("Already closed: " + this);
        }
    }

    @Override
    public long getFilePointer() {
        try {
            return ((long)this.curBufIndex << this.chunkSizePower) + (long)this.curBuf.position() - (long)this.offset;
        }
        catch (NullPointerException npe) {
            throw new AlreadyClosedException("Already closed: " + this);
        }
    }

    @Override
    public void seek(long pos) throws IOException {
        int bi = (int)(pos >> this.chunkSizePower);
        try {
            if (bi == this.curBufIndex) {
                this.curBuf.position((int)(pos & this.chunkSizeMask));
            } else {
                ByteBuffer b = this.buffers[bi];
                b.position((int)(pos & this.chunkSizeMask));
                this.curBufIndex = bi;
                this.curBuf = b;
            }
        }
        catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            throw new EOFException("seek past EOF: " + this);
        }
        catch (NullPointerException npe) {
            throw new AlreadyClosedException("Already closed: " + this);
        }
    }

    @Override
    public final long length() {
        return this.length;
    }

    @Override
    public final ByteBufferIndexInput clone() {
        ByteBufferIndexInput clone = this.buildSlice((String)null, 0L, this.length);
        try {
            clone.seek(this.getFilePointer());
        }
        catch (IOException ioe) {
            throw new RuntimeException("Should never happen: " + this, ioe);
        }
        return clone;
    }

    public final ByteBufferIndexInput slice(String sliceDescription, long offset, long length) {
        if (offset < 0L || length < 0L || offset + length > this.length) {
            throw new IllegalArgumentException("slice() " + sliceDescription + " out of bounds: offset=" + offset + ",length=" + length + ",fileLength=" + this.length + ": " + this);
        }
        return this.buildSlice(sliceDescription, offset, length);
    }

    protected ByteBufferIndexInput buildSlice(String sliceDescription, long offset, long length) {
        if (this.buffers == null) {
            throw new AlreadyClosedException("Already closed: " + this);
        }
        ByteBuffer[] newBuffers = this.buildSlice(this.buffers, offset, length);
        int ofs = (int)(offset & this.chunkSizeMask);
        ByteBufferIndexInput clone = this.newCloneInstance(this.getFullSliceDescription(sliceDescription), newBuffers, ofs, length);
        clone.isClone = true;
        if (this.clones != null) {
            this.clones.put(clone, Boolean.TRUE);
        }
        return clone;
    }

    protected ByteBufferIndexInput newCloneInstance(String newResourceDescription, ByteBuffer[] newBuffers, int offset, long length) {
        if (newBuffers.length == 1) {
            newBuffers[0].position(offset);
            return new SingleBufferImpl(newResourceDescription, newBuffers[0].slice(), length, this.chunkSizePower, this.cleaner, this.clones);
        }
        return new MultiBufferImpl(newResourceDescription, newBuffers, offset, length, this.chunkSizePower, this.cleaner, this.clones);
    }

    private ByteBuffer[] buildSlice(ByteBuffer[] buffers, long offset, long length) {
        long sliceEnd = offset + length;
        int startIndex = (int)(offset >>> this.chunkSizePower);
        int endIndex = (int)(sliceEnd >>> this.chunkSizePower);
        ByteBuffer[] slices = new ByteBuffer[endIndex - startIndex + 1];
        for (int i = 0; i < slices.length; ++i) {
            slices[i] = buffers[startIndex + i].duplicate();
        }
        slices[slices.length - 1].limit((int)(sliceEnd & this.chunkSizeMask));
        return slices;
    }

    private void unsetBuffers() {
        this.buffers = null;
        this.curBuf = null;
        this.curBufIndex = 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void close() throws IOException {
        try {
            if (this.buffers == null) {
                return;
            }
            ByteBuffer[] bufs = this.buffers;
            this.unsetBuffers();
            if (this.clones != null) {
                this.clones.remove(this);
            }
            if (this.isClone) {
                return;
            }
            if (this.clones != null) {
                Iterator<ByteBufferIndexInput> it = this.clones.keyIterator();
                while (it.hasNext()) {
                    ByteBufferIndexInput clone = it.next();
                    assert (clone.isClone);
                    clone.unsetBuffers();
                }
                this.clones.clear();
            }
            for (ByteBuffer b : bufs) {
                this.freeBuffer(b);
            }
        }
        finally {
            this.unsetBuffers();
        }
    }

    private void freeBuffer(ByteBuffer b) throws IOException {
        if (this.cleaner != null) {
            this.cleaner.freeBuffer(this, b);
        }
    }

    @Override
    public final String toString() {
        if (this.sliceDescription != null) {
            return super.toString() + " [slice=" + this.sliceDescription + "]";
        }
        return super.toString();
    }

    static final class MultiBufferImpl
    extends ByteBufferIndexInput {
        private final int offset;

        MultiBufferImpl(String resourceDescription, ByteBuffer[] buffers, int offset, long length, int chunkSizePower, BufferCleaner cleaner, WeakIdentityMap<ByteBufferIndexInput, Boolean> clones) {
            super(resourceDescription, buffers, length, chunkSizePower, cleaner, clones);
            this.offset = offset;
            try {
                this.seek(0L);
            }
            catch (IOException ioe) {
                throw new AssertionError((Object)ioe);
            }
        }

        @Override
        public void seek(long pos) throws IOException {
            assert (pos >= 0L);
            super.seek(pos + (long)this.offset);
        }

        @Override
        public long getFilePointer() {
            return super.getFilePointer() - (long)this.offset;
        }

        @Override
        protected ByteBufferIndexInput buildSlice(String sliceDescription, long ofs, long length) {
            return super.buildSlice(sliceDescription, (long)this.offset + ofs, length);
        }
    }

    static final class SingleBufferImpl
    extends ByteBufferIndexInput {
        SingleBufferImpl(String resourceDescription, ByteBuffer buffer, long length, int chunkSizePower, BufferCleaner cleaner, WeakIdentityMap<ByteBufferIndexInput, Boolean> clones) {
            super(resourceDescription, new ByteBuffer[]{buffer}, length, chunkSizePower, cleaner, clones);
            this.curBufIndex = 0;
            this.curBuf = buffer;
            buffer.position(0);
        }

        @Override
        public void seek(long pos) throws IOException {
            try {
                this.curBuf.position((int)pos);
            }
            catch (IllegalArgumentException e) {
                if (pos < 0L) {
                    throw new IllegalArgumentException("Seeking to negative position: " + this, e);
                }
                throw new EOFException("seek past EOF: " + this);
            }
            catch (NullPointerException npe) {
                throw new AlreadyClosedException("Already closed: " + this);
            }
        }

        @Override
        public long getFilePointer() {
            try {
                return this.curBuf.position();
            }
            catch (NullPointerException npe) {
                throw new AlreadyClosedException("Already closed: " + this);
            }
        }
    }

    static interface BufferCleaner {
        public void freeBuffer(ByteBufferIndexInput var1, ByteBuffer var2) throws IOException;
    }
}

