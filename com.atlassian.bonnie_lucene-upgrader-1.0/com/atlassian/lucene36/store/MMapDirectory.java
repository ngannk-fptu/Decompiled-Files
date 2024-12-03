/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.store;

import com.atlassian.lucene36.store.AlreadyClosedException;
import com.atlassian.lucene36.store.FSDirectory;
import com.atlassian.lucene36.store.IndexInput;
import com.atlassian.lucene36.store.LockFactory;
import com.atlassian.lucene36.util.Constants;
import com.atlassian.lucene36.util.WeakIdentityMap;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Iterator;

public class MMapDirectory
extends FSDirectory {
    private boolean useUnmapHack = UNMAP_SUPPORTED;
    public static final int DEFAULT_MAX_BUFF;
    private int chunkSizePower;
    public static final boolean UNMAP_SUPPORTED;

    public MMapDirectory(File path, LockFactory lockFactory) throws IOException {
        super(path, lockFactory);
        this.setMaxChunkSize(DEFAULT_MAX_BUFF);
    }

    public MMapDirectory(File path) throws IOException {
        super(path, null);
        this.setMaxChunkSize(DEFAULT_MAX_BUFF);
    }

    public void setUseUnmap(boolean useUnmapHack) {
        if (useUnmapHack && !UNMAP_SUPPORTED) {
            throw new IllegalArgumentException("Unmap hack not supported on this platform!");
        }
        this.useUnmapHack = useUnmapHack;
    }

    public boolean getUseUnmap() {
        return this.useUnmapHack;
    }

    final void cleanMapping(final ByteBuffer buffer) throws IOException {
        if (this.useUnmapHack) {
            try {
                AccessController.doPrivileged(new PrivilegedExceptionAction<Object>(){

                    @Override
                    public Object run() throws Exception {
                        Method getCleanerMethod = buffer.getClass().getMethod("cleaner", new Class[0]);
                        getCleanerMethod.setAccessible(true);
                        Object cleaner = getCleanerMethod.invoke((Object)buffer, new Object[0]);
                        if (cleaner != null) {
                            cleaner.getClass().getMethod("clean", new Class[0]).invoke(cleaner, new Object[0]);
                        }
                        return null;
                    }
                });
            }
            catch (PrivilegedActionException e) {
                IOException ioe = new IOException("unable to unmap the mapped buffer");
                ioe.initCause(e.getCause());
                throw ioe;
            }
        }
    }

    public final void setMaxChunkSize(int maxChunkSize) {
        if (maxChunkSize <= 0) {
            throw new IllegalArgumentException("Maximum chunk size for mmap must be >0");
        }
        this.chunkSizePower = 31 - Integer.numberOfLeadingZeros(maxChunkSize);
        assert (this.chunkSizePower >= 0 && this.chunkSizePower <= 30);
    }

    public final int getMaxChunkSize() {
        return 1 << this.chunkSizePower;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public IndexInput openInput(String name, int bufferSize) throws IOException {
        MMapIndexInput mMapIndexInput;
        this.ensureOpen();
        File f = new File(this.getDirectory(), name);
        RandomAccessFile raf = new RandomAccessFile(f, "r");
        try {
            mMapIndexInput = new MMapIndexInput("MMapIndexInput(path=\"" + f + "\")", raf, this.chunkSizePower);
            Object var7_6 = null;
        }
        catch (Throwable throwable) {
            Object var7_7 = null;
            raf.close();
            throw throwable;
        }
        raf.close();
        return mMapIndexInput;
    }

    static {
        boolean v;
        DEFAULT_MAX_BUFF = Constants.JRE_IS_64BIT ? 0x40000000 : 0x10000000;
        try {
            Class.forName("sun.misc.Cleaner");
            Class.forName("java.nio.DirectByteBuffer").getMethod("cleaner", new Class[0]);
            v = true;
        }
        catch (Exception e) {
            v = false;
        }
        UNMAP_SUPPORTED = v;
    }

    private final class MMapIndexInput
    extends IndexInput {
        private ByteBuffer[] buffers;
        private final long length;
        private final long chunkSizeMask;
        private final long chunkSize;
        private final int chunkSizePower;
        private int curBufIndex;
        private ByteBuffer curBuf;
        private boolean isClone;
        private final WeakIdentityMap<MMapIndexInput, Boolean> clones;

        MMapIndexInput(String resourceDescription, RandomAccessFile raf, int chunkSizePower) throws IOException {
            super(resourceDescription);
            this.isClone = false;
            this.clones = WeakIdentityMap.newConcurrentHashMap();
            this.length = raf.length();
            this.chunkSizePower = chunkSizePower;
            this.chunkSize = 1L << chunkSizePower;
            this.chunkSizeMask = this.chunkSize - 1L;
            if (chunkSizePower < 0 || chunkSizePower > 30) {
                throw new IllegalArgumentException("Invalid chunkSizePower used for ByteBuffer size: " + chunkSizePower);
            }
            if (this.length >>> chunkSizePower >= Integer.MAX_VALUE) {
                throw new IllegalArgumentException("RandomAccessFile too big for chunk size: " + raf.toString());
            }
            int nrBuffers = (int)(this.length >>> chunkSizePower) + 1;
            this.buffers = new ByteBuffer[nrBuffers];
            long bufferStart = 0L;
            FileChannel rafc = raf.getChannel();
            for (int bufNr = 0; bufNr < nrBuffers; ++bufNr) {
                int bufSize = (int)(this.length > bufferStart + this.chunkSize ? this.chunkSize : this.length - bufferStart);
                this.buffers[bufNr] = rafc.map(FileChannel.MapMode.READ_ONLY, bufferStart, bufSize);
                bufferStart += (long)bufSize;
            }
            this.seek(0L);
        }

        public byte readByte() throws IOException {
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
                throw new AlreadyClosedException("MMapIndexInput already closed: " + this);
            }
        }

        public void readBytes(byte[] b, int offset, int len) throws IOException {
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
                throw new AlreadyClosedException("MMapIndexInput already closed: " + this);
            }
        }

        public short readShort() throws IOException {
            try {
                return this.curBuf.getShort();
            }
            catch (BufferUnderflowException e) {
                return super.readShort();
            }
            catch (NullPointerException npe) {
                throw new AlreadyClosedException("MMapIndexInput already closed: " + this);
            }
        }

        public int readInt() throws IOException {
            try {
                return this.curBuf.getInt();
            }
            catch (BufferUnderflowException e) {
                return super.readInt();
            }
            catch (NullPointerException npe) {
                throw new AlreadyClosedException("MMapIndexInput already closed: " + this);
            }
        }

        public long readLong() throws IOException {
            try {
                return this.curBuf.getLong();
            }
            catch (BufferUnderflowException e) {
                return super.readLong();
            }
            catch (NullPointerException npe) {
                throw new AlreadyClosedException("MMapIndexInput already closed: " + this);
            }
        }

        public long getFilePointer() {
            try {
                return ((long)this.curBufIndex << this.chunkSizePower) + (long)this.curBuf.position();
            }
            catch (NullPointerException npe) {
                throw new AlreadyClosedException("MMapIndexInput already closed: " + this);
            }
        }

        public void seek(long pos) throws IOException {
            int bi = (int)(pos >> this.chunkSizePower);
            try {
                ByteBuffer b = this.buffers[bi];
                b.position((int)(pos & this.chunkSizeMask));
                this.curBufIndex = bi;
                this.curBuf = b;
            }
            catch (ArrayIndexOutOfBoundsException aioobe) {
                if (pos < 0L) {
                    throw new IllegalArgumentException("Seeking to negative position: " + this);
                }
                throw new EOFException("seek past EOF: " + this);
            }
            catch (IllegalArgumentException iae) {
                if (pos < 0L) {
                    throw new IllegalArgumentException("Seeking to negative position: " + this);
                }
                throw new EOFException("seek past EOF: " + this);
            }
            catch (NullPointerException npe) {
                throw new AlreadyClosedException("MMapIndexInput already closed: " + this);
            }
        }

        public long length() {
            return this.length;
        }

        public Object clone() {
            if (this.buffers == null) {
                throw new AlreadyClosedException("MMapIndexInput already closed: " + this);
            }
            MMapIndexInput clone = (MMapIndexInput)super.clone();
            clone.isClone = true;
            assert (clone.clones == this.clones);
            clone.buffers = new ByteBuffer[this.buffers.length];
            for (int bufNr = 0; bufNr < this.buffers.length; ++bufNr) {
                clone.buffers[bufNr] = this.buffers[bufNr].duplicate();
            }
            try {
                clone.seek(this.getFilePointer());
            }
            catch (IOException ioe) {
                throw new RuntimeException("Should never happen: " + this, ioe);
            }
            this.clones.put(clone, Boolean.TRUE);
            return clone;
        }

        private void unsetBuffers() {
            this.buffers = null;
            this.curBuf = null;
            this.curBufIndex = 0;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void close() throws IOException {
            try {
                block9: {
                    block8: {
                        if (this.isClone) break block8;
                        if (this.buffers != null) break block9;
                    }
                    Object var7_1 = null;
                    this.unsetBuffers();
                    return;
                }
                ByteBuffer[] bufs = this.buffers;
                this.unsetBuffers();
                Iterator<MMapIndexInput> it = this.clones.keyIterator();
                while (it.hasNext()) {
                    MMapIndexInput clone = it.next();
                    assert (clone.isClone);
                    clone.unsetBuffers();
                }
                this.clones.clear();
                for (ByteBuffer b : bufs) {
                    MMapDirectory.this.cleanMapping(b);
                }
            }
            catch (Throwable throwable) {
                Object var7_3 = null;
                this.unsetBuffers();
                throw throwable;
            }
            Object var7_2 = null;
            this.unsetBuffers();
        }
    }
}

