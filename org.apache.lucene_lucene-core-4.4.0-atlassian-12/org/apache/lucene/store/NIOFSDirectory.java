/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.apache.lucene.store.BufferedIndexInput;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.LockFactory;

public class NIOFSDirectory
extends FSDirectory {
    public NIOFSDirectory(File path, LockFactory lockFactory) throws IOException {
        super(path, lockFactory);
    }

    public NIOFSDirectory(File path) throws IOException {
        super(path, null);
    }

    @Override
    public IndexInput openInput(String name, IOContext context) throws IOException {
        this.ensureOpen();
        return new NIOFSIndexInput(new File(this.getDirectory(), name), context, this.getReadChunkSize());
    }

    @Override
    public Directory.IndexInputSlicer createSlicer(String name, final IOContext context) throws IOException {
        this.ensureOpen();
        final File path = new File(this.getDirectory(), name);
        final RandomAccessFile descriptor = new RandomAccessFile(path, "r");
        return new Directory.IndexInputSlicer(){

            @Override
            public void close() throws IOException {
                descriptor.close();
            }

            @Override
            public IndexInput openSlice(String sliceDescription, long offset, long length) {
                return new NIOFSIndexInput(sliceDescription, path, descriptor, descriptor.getChannel(), offset, length, BufferedIndexInput.bufferSize(context), NIOFSDirectory.this.getReadChunkSize());
            }

            @Override
            public IndexInput openFullSlice() {
                try {
                    return this.openSlice("full-slice", 0L, descriptor.length());
                }
                catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };
    }

    protected static class NIOFSIndexInput
    extends FSDirectory.FSIndexInput {
        private ByteBuffer byteBuf;
        final FileChannel channel;

        public NIOFSIndexInput(File path, IOContext context, int chunkSize) throws IOException {
            super("NIOFSIndexInput(path=\"" + path + "\")", path, context, chunkSize);
            this.channel = this.file.getChannel();
        }

        public NIOFSIndexInput(String sliceDescription, File path, RandomAccessFile file, FileChannel fc, long off, long length, int bufferSize, int chunkSize) {
            super("NIOFSIndexInput(" + sliceDescription + " in path=\"" + path + "\" slice=" + off + ":" + (off + length) + ")", file, off, length, bufferSize, chunkSize);
            this.channel = fc;
            this.isClone = true;
        }

        @Override
        protected void newBuffer(byte[] newBuffer) {
            super.newBuffer(newBuffer);
            this.byteBuf = ByteBuffer.wrap(newBuffer);
        }

        @Override
        protected void readInternal(byte[] b, int offset, int len) throws IOException {
            int readLength;
            ByteBuffer bb;
            if (b == this.buffer && 0 == offset) {
                assert (this.byteBuf != null);
                this.byteBuf.clear();
                this.byteBuf.limit(len);
                bb = this.byteBuf;
            } else {
                bb = ByteBuffer.wrap(b, offset, len);
            }
            int readOffset = bb.position();
            assert (readLength == len);
            long pos = this.getFilePointer() + this.off;
            if (pos + (long)len > this.end) {
                throw new EOFException("read past EOF: " + this);
            }
            try {
                int i;
                for (readLength = bb.limit() - readOffset; readLength > 0; readLength -= i) {
                    int limit = readLength > this.chunkSize ? readOffset + this.chunkSize : readOffset + readLength;
                    bb.limit(limit);
                    i = this.channel.read(bb, pos);
                    pos += (long)i;
                    readOffset += i;
                }
            }
            catch (OutOfMemoryError e) {
                OutOfMemoryError outOfMemoryError = new OutOfMemoryError("OutOfMemoryError likely caused by the Sun VM Bug described in https://issues.apache.org/jira/browse/LUCENE-1566; try calling FSDirectory.setReadChunkSize with a value smaller than the current chunk size (" + this.chunkSize + ")");
                outOfMemoryError.initCause(e);
                throw outOfMemoryError;
            }
            catch (IOException ioe) {
                throw new IOException(ioe.getMessage() + ": " + this, ioe);
            }
        }

        @Override
        protected void seekInternal(long pos) throws IOException {
        }
    }
}

