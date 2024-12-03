/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.blob;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.axiom.ext.io.StreamCopyException;
import org.apache.axiom.util.blob.BlobOutputStream;
import org.apache.axiom.util.blob.WritableBlob;

public class MemoryBlob
implements WritableBlob {
    static final int BUFFER_SIZE = 4096;
    List data;
    int index;
    byte[] currBuffer;
    OutputStreamImpl outputStream;
    boolean committed;

    private void init() {
        this.data = new ArrayList();
        this.addBuffer();
    }

    void addBuffer() {
        this.currBuffer = new byte[4096];
        this.data.add(this.currBuffer);
        this.index = 0;
    }

    public boolean isSupportingReadUncommitted() {
        return true;
    }

    public long getLength() {
        if (this.data == null) {
            return 0L;
        }
        return 4096 * (this.data.size() - 1) + this.index;
    }

    public BlobOutputStream getOutputStream() {
        if (this.data != null) {
            throw new IllegalStateException();
        }
        this.init();
        this.outputStream = new OutputStreamImpl();
        return this.outputStream;
    }

    public long readFrom(InputStream in, long length, boolean commit) throws StreamCopyException {
        if (this.data == null) {
            this.init();
        }
        if (length == -1L) {
            length = Long.MAX_VALUE;
        }
        long bytesReceived = 0L;
        boolean done = false;
        while (!done) {
            int bytesRead;
            int len = (int)Math.min((long)(4096 - this.index), length - bytesReceived);
            try {
                bytesRead = in.read(this.currBuffer, this.index, len);
            }
            catch (IOException ex) {
                throw new StreamCopyException(1, ex);
            }
            if (bytesRead >= 0) {
                bytesReceived += (long)bytesRead;
                this.index += bytesRead;
                if (this.index >= 4096) {
                    this.addBuffer();
                }
                if (bytesReceived < length) continue;
                done = true;
                continue;
            }
            done = true;
        }
        this.committed = commit;
        return bytesReceived;
    }

    public long readFrom(InputStream in, long length) throws StreamCopyException {
        return this.readFrom(in, length, this.data == null);
    }

    public InputStream getInputStream() throws IOException {
        return new InputStreamImpl();
    }

    public void writeTo(OutputStream os) throws StreamCopyException {
        int size = (int)this.getLength();
        if (this.data != null) {
            try {
                int numBuffers = this.data.size();
                for (int j = 0; j < numBuffers - 1; ++j) {
                    os.write((byte[])this.data.get(j), 0, 4096);
                }
                if (numBuffers > 0) {
                    int writeLimit = size - (numBuffers - 1) * 4096;
                    os.write((byte[])this.data.get(numBuffers - 1), 0, writeLimit);
                }
            }
            catch (IOException ex) {
                throw new StreamCopyException(2, ex);
            }
        }
    }

    public void release() {
    }

    class InputStreamImpl
    extends InputStream {
        private int i;
        private int currIndex;
        private int totalIndex;
        private int mark;
        private byte[] currBuffer;
        private byte[] read_byte = new byte[1];

        public int read() throws IOException {
            int read = this.read(this.read_byte);
            if (read < 0) {
                return -1;
            }
            return this.read_byte[0] & 0xFF;
        }

        public int available() throws IOException {
            return (int)MemoryBlob.this.getLength() - this.totalIndex;
        }

        public synchronized void mark(int readlimit) {
            this.mark = this.totalIndex;
        }

        public boolean markSupported() {
            return true;
        }

        public int read(byte[] b, int off, int len) throws IOException {
            int size = (int)MemoryBlob.this.getLength();
            int total = 0;
            if (this.totalIndex >= size) {
                return -1;
            }
            while (total < len && this.totalIndex < size) {
                if (this.currBuffer == null) {
                    this.currBuffer = (byte[])MemoryBlob.this.data.get(this.i);
                }
                int copy = Math.min(len - total, 4096 - this.currIndex);
                copy = Math.min(copy, size - this.totalIndex);
                System.arraycopy(this.currBuffer, this.currIndex, b, off, copy);
                total += copy;
                this.currIndex += copy;
                this.totalIndex += copy;
                off += copy;
                if (this.currIndex < 4096) continue;
                if (this.i + 1 < MemoryBlob.this.data.size()) {
                    ++this.i;
                    this.currIndex = 0;
                } else {
                    this.currIndex = 4096;
                }
                this.currBuffer = null;
            }
            return total;
        }

        public int read(byte[] b) throws IOException {
            return this.read(b, 0, b.length);
        }

        public synchronized void reset() throws IOException {
            this.i = this.mark / 4096;
            this.currIndex = this.mark - this.i * 4096;
            this.currBuffer = (byte[])MemoryBlob.this.data.get(this.i);
            this.totalIndex = this.mark;
        }
    }

    class OutputStreamImpl
    extends BlobOutputStream {
        byte[] writeByte = new byte[1];

        OutputStreamImpl() {
        }

        public WritableBlob getBlob() {
            return MemoryBlob.this;
        }

        public void write(byte[] b, int off, int len) throws IOException {
            int total = 0;
            while (total < len) {
                int copy = Math.min(len - total, 4096 - MemoryBlob.this.index);
                System.arraycopy(b, off, MemoryBlob.this.currBuffer, MemoryBlob.this.index, copy);
                total += copy;
                MemoryBlob.this.index += copy;
                off += copy;
                if (MemoryBlob.this.index < 4096) continue;
                MemoryBlob.this.addBuffer();
            }
        }

        public void write(byte[] b) throws IOException {
            this.write(b, 0, b.length);
        }

        public void write(int b) throws IOException {
            this.writeByte[0] = (byte)b;
            this.write(this.writeByte, 0, 1);
        }

        public void close() throws IOException {
            MemoryBlob.this.outputStream = null;
            MemoryBlob.this.committed = true;
        }
    }
}

