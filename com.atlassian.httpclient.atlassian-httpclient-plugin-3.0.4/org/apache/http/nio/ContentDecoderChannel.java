/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import org.apache.http.nio.ContentDecoder;

public class ContentDecoderChannel
implements ReadableByteChannel {
    private final ContentDecoder decoder;

    public ContentDecoderChannel(ContentDecoder decoder) {
        this.decoder = decoder;
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        return this.decoder.read(dst);
    }

    @Override
    public void close() {
    }

    @Override
    public boolean isOpen() {
        return true;
    }
}

