/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.io;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.io.RandomAccessRead;

public class RandomAccessInputStream
extends InputStream {
    private static final Log LOG = LogFactory.getLog(RandomAccessInputStream.class);
    private final RandomAccessRead input;
    private long position;

    public RandomAccessInputStream(RandomAccessRead randomAccessRead) {
        this.input = randomAccessRead;
        this.position = 0L;
    }

    void restorePosition() throws IOException {
        this.input.seek(this.position);
    }

    @Override
    public int available() throws IOException {
        this.restorePosition();
        long available = this.input.length() - this.input.getPosition();
        if (available > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int)available;
    }

    @Override
    public int read() throws IOException {
        this.restorePosition();
        if (this.input.isEOF()) {
            return -1;
        }
        int b = this.input.read();
        if (b != -1) {
            ++this.position;
        } else {
            LOG.error((Object)("read() returns -1, assumed position: " + this.position + ", actual position: " + this.input.getPosition()));
        }
        return b;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        this.restorePosition();
        if (this.input.isEOF()) {
            return -1;
        }
        int n = this.input.read(b, off, len);
        if (n != -1) {
            this.position += (long)n;
        } else {
            LOG.error((Object)("read() returns -1, assumed position: " + this.position + ", actual position: " + this.input.getPosition()));
        }
        return n;
    }

    @Override
    public long skip(long n) throws IOException {
        this.restorePosition();
        this.input.seek(this.position + n);
        this.position += n;
        return n;
    }
}

