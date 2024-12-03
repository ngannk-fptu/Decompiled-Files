/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.universalchardet;

import java.io.IOException;
import java.io.InputStream;
import org.mozilla.universalchardet.UniversalDetector;

public class EncodingDetectorInputStream
extends InputStream {
    private InputStream in;
    private final UniversalDetector detector = new UniversalDetector(null);

    public EncodingDetectorInputStream(InputStream in) {
        this.in = in;
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
    public void mark(int arg0) {
        this.in.mark(arg0);
    }

    @Override
    public boolean markSupported() {
        return this.in.markSupported();
    }

    @Override
    public int read() throws IOException {
        byte[] data = new byte[1];
        int nrOfBytesRead = this.read(data, 0, 1);
        if (nrOfBytesRead >= 0) {
            return data[0];
        }
        return -1;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int nrOfBytesRead = this.in.read(b, off, len);
        if (!this.detector.isDone() && nrOfBytesRead > 0) {
            this.detector.handleData(b, off, nrOfBytesRead);
        }
        if (nrOfBytesRead == -1) {
            this.detector.dataEnd();
        }
        return nrOfBytesRead;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public void reset() throws IOException {
        this.in.reset();
    }

    @Override
    public long skip(long n) throws IOException {
        if (this.detector.isDone()) {
            return this.in.skip(n);
        }
        int lastRead = 0;
        long count = -1L;
        for (long i = 0L; i < n && lastRead >= 0; ++i) {
            lastRead = this.in.read();
            ++count;
        }
        return count;
    }

    public String getDetectedCharset() {
        return this.detector.getDetectedCharset();
    }
}

