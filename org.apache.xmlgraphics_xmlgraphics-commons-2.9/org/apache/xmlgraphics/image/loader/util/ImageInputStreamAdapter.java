/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader.util;

import java.io.IOException;
import java.io.InputStream;
import javax.imageio.stream.ImageInputStream;

public class ImageInputStreamAdapter
extends InputStream {
    private ImageInputStream iin;
    private long lastMarkPosition;

    public ImageInputStreamAdapter(ImageInputStream iin) {
        assert (iin != null) : "InputStream is null";
        this.iin = iin;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return this.iin.read(b, off, len);
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.iin.read(b);
    }

    @Override
    public int read() throws IOException {
        return this.iin.read();
    }

    @Override
    public long skip(long n) throws IOException {
        return this.iin.skipBytes(n);
    }

    @Override
    public void close() throws IOException {
        this.iin.close();
        this.iin = null;
    }

    @Override
    public synchronized void mark(int readlimit) {
        try {
            this.lastMarkPosition = this.iin.getStreamPosition();
        }
        catch (IOException ioe) {
            throw new RuntimeException("Unexpected IOException in ImageInputStream.getStreamPosition()", ioe);
        }
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public synchronized void reset() throws IOException {
        this.iin.seek(this.lastMarkPosition);
    }

    @Override
    public int available() throws IOException {
        return 0;
    }
}

