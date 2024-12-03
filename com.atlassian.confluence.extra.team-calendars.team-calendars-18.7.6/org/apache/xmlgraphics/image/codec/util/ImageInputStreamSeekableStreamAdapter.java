/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.codec.util;

import java.io.IOException;
import javax.imageio.stream.ImageInputStream;
import org.apache.xmlgraphics.image.codec.util.SeekableStream;

public class ImageInputStreamSeekableStreamAdapter
extends SeekableStream {
    private ImageInputStream stream;

    public ImageInputStreamSeekableStreamAdapter(ImageInputStream stream) throws IOException {
        this.stream = stream;
    }

    @Override
    public boolean canSeekBackwards() {
        return true;
    }

    @Override
    public long getFilePointer() throws IOException {
        return this.stream.getStreamPosition();
    }

    @Override
    public void seek(long pos) throws IOException {
        this.stream.seek(pos);
    }

    @Override
    public int read() throws IOException {
        return this.stream.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return this.stream.read(b, off, len);
    }

    @Override
    public void close() throws IOException {
        super.close();
        this.stream.close();
    }
}

