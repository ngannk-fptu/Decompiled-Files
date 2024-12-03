/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public abstract class LineOrientedOutputStream
extends OutputStream {
    private static final int INITIAL_SIZE = 132;
    private static final int CR = 13;
    private static final int LF = 10;
    private ByteArrayOutputStream buffer = new ByteArrayOutputStream(132);
    private boolean skip = false;

    @Override
    public final void write(int cc) throws IOException {
        byte c = (byte)cc;
        if (c == 10 || c == 13) {
            if (!this.skip) {
                this.processBuffer();
            }
        } else {
            this.buffer.write(cc);
        }
        this.skip = c == 13;
    }

    @Override
    public void flush() throws IOException {
    }

    protected void processBuffer() throws IOException {
        try {
            this.processLine(this.buffer.toByteArray());
        }
        finally {
            this.buffer.reset();
        }
    }

    protected abstract void processLine(String var1) throws IOException;

    protected void processLine(byte[] line) throws IOException {
        this.processLine(new String(line));
    }

    @Override
    public void close() throws IOException {
        if (this.buffer.size() > 0) {
            this.processBuffer();
        }
        super.close();
    }

    @Override
    public final void write(byte[] b, int off, int len) throws IOException {
        int offset;
        int blockStartOffset = offset = off;
        int remaining = len;
        while (remaining > 0) {
            while (remaining > 0 && b[offset] != 10 && b[offset] != 13) {
                ++offset;
                --remaining;
            }
            int blockLength = offset - blockStartOffset;
            if (blockLength > 0) {
                this.buffer.write(b, blockStartOffset, blockLength);
            }
            while (remaining > 0 && (b[offset] == 10 || b[offset] == 13)) {
                this.write(b[offset]);
                ++offset;
                --remaining;
            }
            blockStartOffset = offset;
        }
    }
}

