/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.entity;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import org.apache.http.HttpEntity;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.entity.HttpAsyncContentProducer;
import org.apache.http.util.Args;

public class EntityAsyncContentProducer
implements HttpAsyncContentProducer {
    private final HttpEntity entity;
    private final ByteBuffer buffer;
    private ReadableByteChannel channel;

    public EntityAsyncContentProducer(HttpEntity entity) {
        Args.notNull(entity, "HTTP entity");
        this.entity = entity;
        this.buffer = ByteBuffer.allocate(4096);
    }

    @Override
    public void produceContent(ContentEncoder encoder, IOControl ioControl) throws IOException {
        if (this.channel == null) {
            this.channel = Channels.newChannel(this.entity.getContent());
        }
        int i = this.channel.read(this.buffer);
        this.buffer.flip();
        encoder.write(this.buffer);
        boolean buffering = this.buffer.hasRemaining();
        this.buffer.compact();
        if (i == -1 && !buffering) {
            encoder.complete();
            this.close();
        }
    }

    @Override
    public boolean isRepeatable() {
        return this.entity.isRepeatable();
    }

    @Override
    public void close() throws IOException {
        ReadableByteChannel local = this.channel;
        this.channel = null;
        if (local != null) {
            local.close();
        }
        if (this.entity.isStreaming()) {
            InputStream inStream = this.entity.getContent();
            inStream.close();
        }
    }

    public String toString() {
        return this.entity.toString();
    }
}

