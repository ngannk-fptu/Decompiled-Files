/*
 * Decompiled with CFR 0.152.
 */
package javax.mail.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import javax.mail.internet.SharedInputStream;

public class SharedFileInputStream
extends BufferedInputStream
implements SharedInputStream {
    private static int defaultBufferSize = 2048;
    protected RandomAccessFile in;
    protected int bufsize;
    protected long bufpos;
    protected long start = 0L;
    protected long datalen;
    private boolean master = true;
    private SharedFile sf;

    private void ensureOpen() throws IOException {
        if (this.in == null) {
            throw new IOException("Stream closed");
        }
    }

    public SharedFileInputStream(File file) throws IOException {
        this(file, defaultBufferSize);
    }

    public SharedFileInputStream(String file) throws IOException {
        this(file, defaultBufferSize);
    }

    public SharedFileInputStream(File file, int size) throws IOException {
        super(null);
        if (size <= 0) {
            throw new IllegalArgumentException("Buffer size <= 0");
        }
        this.init(new SharedFile(file), size);
    }

    public SharedFileInputStream(String file, int size) throws IOException {
        super(null);
        if (size <= 0) {
            throw new IllegalArgumentException("Buffer size <= 0");
        }
        this.init(new SharedFile(file), size);
    }

    private void init(SharedFile sf, int size) throws IOException {
        this.sf = sf;
        this.in = sf.open();
        this.start = 0L;
        this.datalen = this.in.length();
        this.bufsize = size;
        this.buf = new byte[size];
    }

    private SharedFileInputStream(SharedFile sf, long start, long len, int bufsize) {
        super(null);
        this.master = false;
        this.sf = sf;
        this.in = sf.open();
        this.start = start;
        this.bufpos = start;
        this.datalen = len;
        this.bufsize = bufsize;
        this.buf = new byte[bufsize];
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void fill() throws IOException {
        if (this.markpos < 0) {
            this.pos = 0;
            this.bufpos += (long)this.count;
        } else if (this.pos >= this.buf.length) {
            if (this.markpos > 0) {
                int sz = this.pos - this.markpos;
                System.arraycopy(this.buf, this.markpos, this.buf, 0, sz);
                this.pos = sz;
                this.bufpos += (long)this.markpos;
                this.markpos = 0;
            } else if (this.buf.length >= this.marklimit) {
                this.markpos = -1;
                this.pos = 0;
                this.bufpos += (long)this.count;
            } else {
                int nsz = this.pos * 2;
                if (nsz > this.marklimit) {
                    nsz = this.marklimit;
                }
                byte[] nbuf = new byte[nsz];
                System.arraycopy(this.buf, 0, nbuf, 0, this.pos);
                this.buf = nbuf;
            }
        }
        this.count = this.pos;
        int len = this.buf.length - this.pos;
        if (this.bufpos - this.start + (long)this.pos + (long)len > this.datalen) {
            len = (int)(this.datalen - (this.bufpos - this.start + (long)this.pos));
        }
        RandomAccessFile randomAccessFile = this.in;
        synchronized (randomAccessFile) {
            this.in.seek(this.bufpos + (long)this.pos);
            int n = this.in.read(this.buf, this.pos, len);
            if (n > 0) {
                this.count = n + this.pos;
            }
        }
    }

    @Override
    public synchronized int read() throws IOException {
        this.ensureOpen();
        if (this.pos >= this.count) {
            this.fill();
            if (this.pos >= this.count) {
                return -1;
            }
        }
        return this.buf[this.pos++] & 0xFF;
    }

    private int read1(byte[] b, int off, int len) throws IOException {
        int avail = this.count - this.pos;
        if (avail <= 0) {
            this.fill();
            avail = this.count - this.pos;
            if (avail <= 0) {
                return -1;
            }
        }
        int cnt = avail < len ? avail : len;
        System.arraycopy(this.buf, this.pos, b, off, cnt);
        this.pos += cnt;
        return cnt;
    }

    @Override
    public synchronized int read(byte[] b, int off, int len) throws IOException {
        int n1;
        this.ensureOpen();
        if ((off | len | off + len | b.length - (off + len)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }
        int n = this.read1(b, off, len);
        if (n <= 0) {
            return n;
        }
        while (n < len && (n1 = this.read1(b, off + n, len - n)) > 0) {
            n += n1;
        }
        return n;
    }

    @Override
    public synchronized long skip(long n) throws IOException {
        this.ensureOpen();
        if (n <= 0L) {
            return 0L;
        }
        long avail = this.count - this.pos;
        if (avail <= 0L) {
            this.fill();
            avail = this.count - this.pos;
            if (avail <= 0L) {
                return 0L;
            }
        }
        long skipped = avail < n ? avail : n;
        this.pos = (int)((long)this.pos + skipped);
        return skipped;
    }

    @Override
    public synchronized int available() throws IOException {
        this.ensureOpen();
        return this.count - this.pos + this.in_available();
    }

    private int in_available() throws IOException {
        return (int)(this.start + this.datalen - (this.bufpos + (long)this.count));
    }

    @Override
    public synchronized void mark(int readlimit) {
        this.marklimit = readlimit;
        this.markpos = this.pos;
    }

    @Override
    public synchronized void reset() throws IOException {
        this.ensureOpen();
        if (this.markpos < 0) {
            throw new IOException("Resetting to invalid mark");
        }
        this.pos = this.markpos;
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public void close() throws IOException {
        if (this.in == null) {
            return;
        }
        try {
            if (this.master) {
                this.sf.forceClose();
            } else {
                this.sf.close();
            }
        }
        finally {
            this.sf = null;
            this.in = null;
            this.buf = null;
        }
    }

    @Override
    public long getPosition() {
        if (this.in == null) {
            throw new RuntimeException("Stream closed");
        }
        return this.bufpos + (long)this.pos - this.start;
    }

    @Override
    public synchronized InputStream newStream(long start, long end) {
        if (this.in == null) {
            throw new RuntimeException("Stream closed");
        }
        if (start < 0L) {
            throw new IllegalArgumentException("start < 0");
        }
        if (end == -1L) {
            end = this.datalen;
        }
        return new SharedFileInputStream(this.sf, this.start + start, end - start, this.bufsize);
    }

    protected void finalize() throws Throwable {
        super.finalize();
        this.close();
    }

    static class SharedFile {
        private int cnt;
        private RandomAccessFile in;

        SharedFile(String file) throws IOException {
            this.in = new RandomAccessFile(file, "r");
        }

        SharedFile(File file) throws IOException {
            this.in = new RandomAccessFile(file, "r");
        }

        public synchronized RandomAccessFile open() {
            ++this.cnt;
            return this.in;
        }

        public synchronized void close() throws IOException {
            if (this.cnt > 0 && --this.cnt <= 0) {
                this.in.close();
            }
        }

        public synchronized void forceClose() throws IOException {
            if (this.cnt > 0) {
                this.cnt = 0;
                this.in.close();
            } else {
                try {
                    this.in.close();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
            }
        }

        protected void finalize() throws Throwable {
            try {
                this.in.close();
            }
            finally {
                super.finalize();
            }
        }
    }
}

