/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.util.Collection;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.BufferedIndexInput;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.Lock;
import org.apache.lucene.store.LockFactory;
import org.apache.lucene.util.IOUtils;

public abstract class Directory
implements Closeable {
    protected volatile boolean isOpen = true;
    protected LockFactory lockFactory;

    public abstract String[] listAll() throws IOException;

    public abstract boolean fileExists(String var1) throws IOException;

    public abstract void deleteFile(String var1) throws IOException;

    public abstract long fileLength(String var1) throws IOException;

    public abstract IndexOutput createOutput(String var1, IOContext var2) throws IOException;

    public abstract void sync(Collection<String> var1) throws IOException;

    public abstract IndexInput openInput(String var1, IOContext var2) throws IOException;

    public Lock makeLock(String name) {
        return this.lockFactory.makeLock(name);
    }

    public void clearLock(String name) throws IOException {
        if (this.lockFactory != null) {
            this.lockFactory.clearLock(name);
        }
    }

    @Override
    public abstract void close() throws IOException;

    public void setLockFactory(LockFactory lockFactory) throws IOException {
        assert (lockFactory != null);
        this.lockFactory = lockFactory;
        lockFactory.setLockPrefix(this.getLockID());
    }

    public LockFactory getLockFactory() {
        return this.lockFactory;
    }

    public String getLockID() {
        return this.toString();
    }

    public String toString() {
        return super.toString() + " lockFactory=" + this.getLockFactory();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public void copy(Directory to, String src, String dest, IOContext context) throws IOException {
        IndexOutput os = null;
        IndexInput is = null;
        IOException priorException = null;
        os = to.createOutput(dest, context);
        is = this.openInput(src, context);
        os.copyBytes(is, is.length());
        boolean success = false;
        try {
            IOUtils.closeWhileHandlingException(priorException, os, is);
            success = true;
        }
        finally {
            if (!success) {
                try {
                    to.deleteFile(dest);
                }
                catch (Throwable throwable) {}
            }
        }
        catch (IOException ioe) {
            try {
                priorException = ioe;
                success = false;
            }
            catch (Throwable throwable) {
                boolean success2 = false;
                try {
                    IOUtils.closeWhileHandlingException(priorException, os, is);
                    success2 = true;
                }
                finally {
                    if (!success2) {
                        try {
                            to.deleteFile(dest);
                        }
                        catch (Throwable throwable2) {}
                    }
                }
                throw throwable;
            }
            try {
                IOUtils.closeWhileHandlingException(priorException, os, is);
                success = true;
            }
            finally {
                if (!success) {
                    try {
                        to.deleteFile(dest);
                    }
                    catch (Throwable throwable) {}
                }
            }
        }
    }

    public IndexInputSlicer createSlicer(final String name, final IOContext context) throws IOException {
        this.ensureOpen();
        return new IndexInputSlicer(){
            private final IndexInput base;
            {
                this.base = Directory.this.openInput(name, context);
            }

            @Override
            public IndexInput openSlice(String sliceDescription, long offset, long length) {
                return new SlicedIndexInput("SlicedIndexInput(" + sliceDescription + " in " + this.base + ")", this.base, offset, length);
            }

            @Override
            public void close() throws IOException {
                this.base.close();
            }

            @Override
            public IndexInput openFullSlice() {
                return this.base.clone();
            }
        };
    }

    protected final void ensureOpen() throws AlreadyClosedException {
        if (!this.isOpen) {
            throw new AlreadyClosedException("this Directory is closed");
        }
    }

    private static final class SlicedIndexInput
    extends BufferedIndexInput {
        IndexInput base;
        long fileOffset;
        long length;

        SlicedIndexInput(String sliceDescription, IndexInput base, long fileOffset, long length) {
            this(sliceDescription, base, fileOffset, length, 1024);
        }

        SlicedIndexInput(String sliceDescription, IndexInput base, long fileOffset, long length, int readBufferSize) {
            super("SlicedIndexInput(" + sliceDescription + " in " + base + " slice=" + fileOffset + ":" + (fileOffset + length) + ")", readBufferSize);
            this.base = base.clone();
            this.fileOffset = fileOffset;
            this.length = length;
        }

        @Override
        public SlicedIndexInput clone() {
            SlicedIndexInput clone = (SlicedIndexInput)super.clone();
            clone.base = this.base.clone();
            clone.fileOffset = this.fileOffset;
            clone.length = this.length;
            return clone;
        }

        @Override
        protected void readInternal(byte[] b, int offset, int len) throws IOException {
            long start = this.getFilePointer();
            if (start + (long)len > this.length) {
                throw new EOFException("read past EOF: " + this);
            }
            this.base.seek(this.fileOffset + start);
            this.base.readBytes(b, offset, len, false);
        }

        @Override
        protected void seekInternal(long pos) {
        }

        @Override
        public void close() throws IOException {
            this.base.close();
        }

        @Override
        public long length() {
            return this.length;
        }
    }

    public abstract class IndexInputSlicer
    implements Closeable {
        public abstract IndexInput openSlice(String var1, long var2, long var4) throws IOException;

        @Deprecated
        public abstract IndexInput openFullSlice() throws IOException;
    }
}

