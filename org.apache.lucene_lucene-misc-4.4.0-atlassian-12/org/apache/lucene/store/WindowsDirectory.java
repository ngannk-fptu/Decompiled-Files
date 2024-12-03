/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.store.BufferedIndexInput
 *  org.apache.lucene.store.FSDirectory
 *  org.apache.lucene.store.IOContext
 *  org.apache.lucene.store.IndexInput
 *  org.apache.lucene.store.LockFactory
 */
package org.apache.lucene.store;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import org.apache.lucene.store.BufferedIndexInput;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.LockFactory;

public class WindowsDirectory
extends FSDirectory {
    private static final int DEFAULT_BUFFERSIZE = 4096;

    public WindowsDirectory(File path, LockFactory lockFactory) throws IOException {
        super(path, lockFactory);
    }

    public WindowsDirectory(File path) throws IOException {
        super(path, null);
    }

    public IndexInput openInput(String name, IOContext context) throws IOException {
        this.ensureOpen();
        return new WindowsIndexInput(new File(this.getDirectory(), name), Math.max(BufferedIndexInput.bufferSize((IOContext)context), 4096));
    }

    private static native long open(String var0) throws IOException;

    private static native int read(long var0, byte[] var2, int var3, int var4, long var5) throws IOException;

    private static native void close(long var0) throws IOException;

    private static native long length(long var0) throws IOException;

    static {
        System.loadLibrary("WindowsDirectory");
    }

    static class WindowsIndexInput
    extends BufferedIndexInput {
        private final long fd;
        private final long length;
        boolean isClone;
        boolean isOpen;

        public WindowsIndexInput(File file, int bufferSize) throws IOException {
            super("WindowsIndexInput(path=\"" + file.getPath() + "\")", bufferSize);
            this.fd = WindowsDirectory.open(file.getPath());
            this.length = WindowsDirectory.length(this.fd);
            this.isOpen = true;
        }

        protected void readInternal(byte[] b, int offset, int length) throws IOException {
            int bytesRead;
            try {
                bytesRead = WindowsDirectory.read(this.fd, b, offset, length, this.getFilePointer());
            }
            catch (IOException ioe) {
                throw new IOException(ioe.getMessage() + ": " + (Object)((Object)this), ioe);
            }
            if (bytesRead != length) {
                throw new EOFException("read past EOF: " + (Object)((Object)this));
            }
        }

        protected void seekInternal(long pos) throws IOException {
        }

        public synchronized void close() throws IOException {
            if (!this.isClone && this.isOpen) {
                WindowsDirectory.close(this.fd);
                this.isOpen = false;
            }
        }

        public long length() {
            return this.length;
        }

        public WindowsIndexInput clone() {
            WindowsIndexInput clone = (WindowsIndexInput)super.clone();
            clone.isClone = true;
            return clone;
        }
    }
}

