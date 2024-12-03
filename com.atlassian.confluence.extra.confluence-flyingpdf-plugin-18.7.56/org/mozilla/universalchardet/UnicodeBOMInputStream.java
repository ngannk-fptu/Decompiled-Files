/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.universalchardet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

public class UnicodeBOMInputStream
extends InputStream {
    private final PushbackInputStream in;
    private final BOM bom;
    private boolean skipped = false;

    public UnicodeBOMInputStream(InputStream inputStream) throws IOException {
        this(inputStream, true);
    }

    public UnicodeBOMInputStream(InputStream inputStream, boolean skipIfFound) throws IOException {
        if (inputStream == null) {
            throw new NullPointerException("invalid input stream: null is not allowed");
        }
        this.in = new PushbackInputStream(inputStream, 4);
        byte[] bom = new byte[4];
        int read = this.in.read(bom);
        switch (read) {
            case 4: {
                if (bom[0] == -1 && bom[1] == -2 && bom[2] == 0 && bom[3] == 0) {
                    this.bom = BOM.UTF_32_LE;
                    break;
                }
                if (bom[0] == 0 && bom[1] == 0 && bom[2] == -2 && bom[3] == -1) {
                    this.bom = BOM.UTF_32_BE;
                    break;
                }
            }
            case 3: {
                if (bom[0] == -17 && bom[1] == -69 && bom[2] == -65) {
                    this.bom = BOM.UTF_8;
                    break;
                }
            }
            case 2: {
                if (bom[0] == -1 && bom[1] == -2) {
                    this.bom = BOM.UTF_16_LE;
                    break;
                }
                if (bom[0] == -2 && bom[1] == -1) {
                    this.bom = BOM.UTF_16_BE;
                    break;
                }
            }
            default: {
                this.bom = BOM.NONE;
            }
        }
        if (read > 0) {
            this.in.unread(bom, 0, read);
        }
        if (skipIfFound) {
            this.skipBOM();
        }
    }

    public final BOM getBOM() {
        return this.bom;
    }

    public final synchronized UnicodeBOMInputStream skipBOM() throws IOException {
        if (!this.skipped) {
            long bytesSkipped;
            long bytesToSkip = this.bom.bytes.length;
            for (long i = bytesSkipped = this.in.skip(bytesToSkip); i < bytesToSkip; ++i) {
                this.in.read();
            }
            this.skipped = true;
        }
        return this;
    }

    @Override
    public int read() throws IOException {
        this.skipped = true;
        return this.in.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        this.skipped = true;
        return this.in.read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        this.skipped = true;
        return this.in.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        this.skipped = true;
        return this.in.skip(n);
    }

    @Override
    public int available() throws IOException {
        return this.in.available();
    }

    @Override
    public void close() throws IOException {
        this.in.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        this.in.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        this.in.reset();
    }

    @Override
    public boolean markSupported() {
        return this.in.markSupported();
    }

    public static final class BOM {
        final byte[] bytes;
        private final String description;
        public static final BOM NONE = new BOM(new byte[0], "NONE");
        public static final BOM UTF_8 = new BOM(new byte[]{-17, -69, -65}, "UTF-8");
        public static final BOM UTF_16_LE = new BOM(new byte[]{-1, -2}, "UTF-16 little-endian");
        public static final BOM UTF_16_BE = new BOM(new byte[]{-2, -1}, "UTF-16 big-endian");
        public static final BOM UTF_32_LE = new BOM(new byte[]{-1, -2, 0, 0}, "UTF-32 little-endian");
        public static final BOM UTF_32_BE = new BOM(new byte[]{0, 0, -2, -1}, "UTF-32 big-endian");

        public final String toString() {
            return this.description;
        }

        public final byte[] getBytes() {
            int length = this.bytes.length;
            byte[] result = new byte[length];
            System.arraycopy(this.bytes, 0, result, 0, length);
            return result;
        }

        private BOM(byte[] bom, String description) {
            assert (bom != null) : "invalid BOM: null is not allowed";
            assert (description != null) : "invalid description: null is not allowed";
            assert (description.length() != 0) : "invalid description: empty string is not allowed";
            this.bytes = bom;
            this.description = description;
        }
    }
}

