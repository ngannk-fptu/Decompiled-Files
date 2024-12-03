/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.store.Directory
 *  org.apache.lucene.store.FSDirectory
 *  org.apache.lucene.store.IOContext
 *  org.apache.lucene.store.IOContext$Context
 *  org.apache.lucene.store.IndexInput
 *  org.apache.lucene.store.IndexOutput
 */
package org.apache.lucene.store;

import java.io.EOFException;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.NativePosixUtil;

public class NativeUnixDirectory
extends FSDirectory {
    private static final long ALIGN = 512L;
    private static final long ALIGN_NOT_MASK = -512L;
    public static final int DEFAULT_MERGE_BUFFER_SIZE = 262144;
    public static final long DEFAULT_MIN_BYTES_DIRECT = 0xA00000L;
    private final int mergeBufferSize;
    private final long minBytesDirect;
    private final Directory delegate;

    public NativeUnixDirectory(File path, int mergeBufferSize, long minBytesDirect, Directory delegate) throws IOException {
        super(path, delegate.getLockFactory());
        if (((long)mergeBufferSize & 0x200L) != 0L) {
            throw new IllegalArgumentException("mergeBufferSize must be 0 mod 512 (got: " + mergeBufferSize + ")");
        }
        this.mergeBufferSize = mergeBufferSize;
        this.minBytesDirect = minBytesDirect;
        this.delegate = delegate;
    }

    public NativeUnixDirectory(File path, Directory delegate) throws IOException {
        this(path, 262144, 0xA00000L, delegate);
    }

    public IndexInput openInput(String name, IOContext context) throws IOException {
        this.ensureOpen();
        if (context.context != IOContext.Context.MERGE || context.mergeInfo.estimatedMergeBytes < this.minBytesDirect || this.fileLength(name) < this.minBytesDirect) {
            return this.delegate.openInput(name, context);
        }
        return new NativeUnixIndexInput(new File(this.getDirectory(), name), this.mergeBufferSize);
    }

    public IndexOutput createOutput(String name, IOContext context) throws IOException {
        this.ensureOpen();
        if (context.context != IOContext.Context.MERGE || context.mergeInfo.estimatedMergeBytes < this.minBytesDirect) {
            return this.delegate.createOutput(name, context);
        }
        this.ensureCanWrite(name);
        return new NativeUnixIndexOutput(new File(this.getDirectory(), name), this.mergeBufferSize);
    }

    private static final class NativeUnixIndexInput
    extends IndexInput {
        private final ByteBuffer buffer;
        private final FileInputStream fis;
        private final FileChannel channel;
        private final int bufferSize;
        private boolean isOpen;
        private boolean isClone;
        private long filePos;
        private int bufferPos;

        public NativeUnixIndexInput(File path, int bufferSize) throws IOException {
            super("NativeUnixIndexInput(path=\"" + path.getPath() + "\")");
            FileDescriptor fd = NativePosixUtil.open_direct(path.toString(), true);
            this.fis = new FileInputStream(fd);
            this.channel = this.fis.getChannel();
            this.bufferSize = bufferSize;
            this.buffer = ByteBuffer.allocateDirect(bufferSize);
            this.isOpen = true;
            this.isClone = false;
            this.filePos = -bufferSize;
            this.bufferPos = bufferSize;
        }

        public NativeUnixIndexInput(NativeUnixIndexInput other) throws IOException {
            super(other.toString());
            this.fis = null;
            this.channel = other.channel;
            this.bufferSize = other.bufferSize;
            this.buffer = ByteBuffer.allocateDirect(this.bufferSize);
            this.filePos = -this.bufferSize;
            this.bufferPos = this.bufferSize;
            this.isOpen = true;
            this.isClone = true;
            this.seek(other.getFilePointer());
        }

        public void close() throws IOException {
            if (this.isOpen && !this.isClone) {
                try {
                    this.channel.close();
                }
                finally {
                    if (!this.isClone) {
                        this.fis.close();
                    }
                }
            }
        }

        public long getFilePointer() {
            return this.filePos + (long)this.bufferPos;
        }

        public void seek(long pos) throws IOException {
            if (pos != this.getFilePointer()) {
                long alignedPos = pos & 0xFFFFFFFFFFFFFE00L;
                this.filePos = alignedPos - (long)this.bufferSize;
                int delta = (int)(pos - alignedPos);
                if (delta != 0) {
                    this.refill();
                    this.buffer.position(delta);
                    this.bufferPos = delta;
                } else {
                    this.bufferPos = this.bufferSize;
                }
            }
        }

        public long length() {
            try {
                return this.channel.size();
            }
            catch (IOException ioe) {
                throw new RuntimeException("IOException during length(): " + (Object)((Object)this), ioe);
            }
        }

        public byte readByte() throws IOException {
            if (this.bufferPos == this.bufferSize) {
                this.refill();
            }
            assert (this.bufferPos == this.buffer.position()) : "bufferPos=" + this.bufferPos + " vs buffer.position()=" + this.buffer.position();
            ++this.bufferPos;
            return this.buffer.get();
        }

        private void refill() throws IOException {
            int n;
            this.buffer.clear();
            this.filePos += (long)this.bufferSize;
            this.bufferPos = 0;
            assert ((this.filePos & 0xFFFFFFFFFFFFFE00L) == this.filePos) : "filePos=" + this.filePos + " anded=" + (this.filePos & 0xFFFFFFFFFFFFFE00L);
            try {
                n = this.channel.read(this.buffer, this.filePos);
            }
            catch (IOException ioe) {
                throw new IOException(ioe.getMessage() + ": " + (Object)((Object)this), ioe);
            }
            if (n < 0) {
                throw new EOFException("read past EOF: " + (Object)((Object)this));
            }
            this.buffer.rewind();
        }

        public void readBytes(byte[] dst, int offset, int len) throws IOException {
            int left;
            int toRead = len;
            while ((left = this.bufferSize - this.bufferPos) < toRead) {
                this.buffer.get(dst, offset, left);
                toRead -= left;
                offset += left;
                this.refill();
            }
            this.buffer.get(dst, offset, toRead);
            this.bufferPos += toRead;
        }

        public NativeUnixIndexInput clone() {
            try {
                return new NativeUnixIndexInput(this);
            }
            catch (IOException ioe) {
                throw new RuntimeException("IOException during clone: " + (Object)((Object)this), ioe);
            }
        }
    }

    private static final class NativeUnixIndexOutput
    extends IndexOutput {
        private final ByteBuffer buffer;
        private final FileOutputStream fos;
        private final FileChannel channel;
        private final int bufferSize;
        private int bufferPos;
        private long filePos;
        private long fileLength;
        private boolean isOpen;

        public NativeUnixIndexOutput(File path, int bufferSize) throws IOException {
            FileDescriptor fd = NativePosixUtil.open_direct(path.toString(), false);
            this.fos = new FileOutputStream(fd);
            this.channel = this.fos.getChannel();
            this.buffer = ByteBuffer.allocateDirect(bufferSize);
            this.bufferSize = bufferSize;
            this.isOpen = true;
        }

        public void writeByte(byte b) throws IOException {
            assert (this.bufferPos == this.buffer.position()) : "bufferPos=" + this.bufferPos + " vs buffer.position()=" + this.buffer.position();
            this.buffer.put(b);
            if (++this.bufferPos == this.bufferSize) {
                this.dump();
            }
        }

        public void writeBytes(byte[] src, int offset, int len) throws IOException {
            int left;
            int toWrite = len;
            while ((left = this.bufferSize - this.bufferPos) <= toWrite) {
                this.buffer.put(src, offset, left);
                toWrite -= left;
                offset += left;
                this.bufferPos = this.bufferSize;
                this.dump();
            }
            this.buffer.put(src, offset, toWrite);
            this.bufferPos += toWrite;
        }

        public void flush() {
        }

        private void dump() throws IOException {
            this.buffer.flip();
            long limit = this.filePos + (long)this.buffer.limit();
            if (limit > this.fileLength) {
                this.fileLength = limit;
            }
            this.buffer.limit((int)((long)this.buffer.limit() + 512L - 1L & 0xFFFFFFFFFFFFFE00L));
            assert (((long)this.buffer.limit() & 0xFFFFFFFFFFFFFE00L) == (long)this.buffer.limit()) : "limit=" + this.buffer.limit() + " vs " + ((long)this.buffer.limit() & 0xFFFFFFFFFFFFFE00L);
            assert ((this.filePos & 0xFFFFFFFFFFFFFE00L) == this.filePos);
            this.channel.write(this.buffer, this.filePos);
            this.filePos += (long)this.bufferPos;
            this.bufferPos = 0;
            this.buffer.clear();
        }

        public long getFilePointer() {
            return this.filePos + (long)this.bufferPos;
        }

        public void seek(long pos) throws IOException {
            if (pos != this.getFilePointer()) {
                long alignedPos;
                this.dump();
                this.filePos = alignedPos = pos & 0xFFFFFFFFFFFFFE00L;
                int n = (int)NativePosixUtil.pread(this.fos.getFD(), this.filePos, this.buffer);
                if (n < this.bufferSize) {
                    this.buffer.limit(n);
                }
                int delta = (int)(pos - alignedPos);
                this.buffer.position(delta);
                this.bufferPos = delta;
            }
        }

        public long length() {
            return this.fileLength + (long)this.bufferPos;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void close() throws IOException {
            if (this.isOpen) {
                this.isOpen = false;
                try {
                    this.dump();
                }
                finally {
                    try {
                        this.channel.truncate(this.fileLength);
                    }
                    finally {
                        try {
                            this.channel.close();
                        }
                        finally {
                            this.fos.close();
                        }
                    }
                }
            }
        }
    }
}

