/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import org.apache.http.nio.ContentEncoder;

public class ContentEncoderChannel
implements WritableByteChannel {
    private final ContentEncoder contentEncoder;

    public ContentEncoderChannel(ContentEncoder contentEncoder) {
        this.contentEncoder = contentEncoder;
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        return this.contentEncoder.write(src);
    }

    @Override
    public void close() {
    }

    @Override
    public boolean isOpen() {
        return true;
    }
}

