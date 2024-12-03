/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.universalchardet;

import java.io.IOException;
import java.io.OutputStream;
import org.mozilla.universalchardet.UniversalDetector;

public class EncodingDetectorOutputStream
extends OutputStream {
    private OutputStream out;
    private final UniversalDetector detector = new UniversalDetector(null);

    public EncodingDetectorOutputStream(OutputStream out) {
        this.out = out;
    }

    @Override
    public void close() throws IOException {
        this.out.close();
        this.detector.dataEnd();
    }

    @Override
    public void flush() throws IOException {
        this.out.flush();
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.out.write(b, off, len);
        if (!this.detector.isDone()) {
            this.detector.handleData(b, off, len);
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }

    @Override
    public void write(int b) throws IOException {
        this.write(new byte[]{(byte)b});
    }

    public String getDetectedCharset() {
        return this.detector.getDetectedCharset();
    }
}

