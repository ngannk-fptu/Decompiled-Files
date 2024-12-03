/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.store;

import com.atlassian.lucene36.store.FSDirectory;
import com.atlassian.lucene36.store.IndexInput;
import com.atlassian.lucene36.store.LockFactory;
import com.atlassian.lucene36.store.SimpleFSDirectory;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIOFSDirectory
extends FSDirectory {
    public NIOFSDirectory(File path, LockFactory lockFactory) throws IOException {
        super(path, lockFactory);
    }

    public NIOFSDirectory(File path) throws IOException {
        super(path, null);
    }

    public IndexInput openInput(String name, int bufferSize) throws IOException {
        this.ensureOpen();
        return new NIOFSIndexInput(new File(this.getDirectory(), name), bufferSize, this.getReadChunkSize());
    }

    protected static class NIOFSIndexInput
    extends SimpleFSDirectory.SimpleFSIndexInput {
        private ByteBuffer byteBuf;
        private byte[] otherBuffer;
        private ByteBuffer otherByteBuf;
        final FileChannel channel;

        public NIOFSIndexInput(File path, int bufferSize, int chunkSize) throws IOException {
            super("NIOFSIndexInput(path=\"" + path + "\")", path, bufferSize, chunkSize);
            this.channel = this.file.getChannel();
        }

        protected void newBuffer(byte[] newBuffer) {
            super.newBuffer(newBuffer);
            this.byteBuf = ByteBuffer.wrap(newBuffer);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void close() throws IOException {
            if (!this.isClone && this.file.isOpen) {
                try {
                    this.channel.close();
                    Object var2_1 = null;
                }
                catch (Throwable throwable) {
                    Object var2_2 = null;
                    this.file.close();
                    throw throwable;
                }
                this.file.close();
                {
                }
            }
        }

        protected void readInternal(byte[] b, int offset, int len) throws IOException {
            int readLength;
            ByteBuffer bb;
            if (b == this.buffer && 0 == offset) {
                assert (this.byteBuf != null);
                this.byteBuf.clear();
                this.byteBuf.limit(len);
                bb = this.byteBuf;
            } else if (offset == 0) {
                if (this.otherBuffer != b) {
                    this.otherBuffer = b;
                    this.otherByteBuf = ByteBuffer.wrap(b);
                } else {
                    this.otherByteBuf.clear();
                }
                this.otherByteBuf.limit(len);
                bb = this.otherByteBuf;
            } else {
                bb = ByteBuffer.wrap(b, offset, len);
            }
            int readOffset = bb.position();
            assert (readLength == len);
            long pos = this.getFilePointer();
            try {
                int i;
                for (readLength = bb.limit() - readOffset; readLength > 0; readLength -= i) {
                    int limit = readLength > this.chunkSize ? readOffset + this.chunkSize : readOffset + readLength;
                    bb.limit(limit);
                    i = this.channel.read(bb, pos);
                    if (i == -1) {
                        throw new EOFException("read past EOF: " + this);
                    }
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
                IOException newIOE = new IOException(ioe.getMessage() + ": " + this);
                newIOE.initCause(ioe);
                throw newIOE;
            }
        }
    }
}

