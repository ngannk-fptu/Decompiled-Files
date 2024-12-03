/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.entity.ProducingNHttpEntity;

@Deprecated
public class NHttpEntityWrapper
extends HttpEntityWrapper
implements ProducingNHttpEntity {
    private final ReadableByteChannel channel;
    private final ByteBuffer buffer;

    public NHttpEntityWrapper(HttpEntity httpEntity) throws IOException {
        super(httpEntity);
        this.channel = Channels.newChannel(httpEntity.getContent());
        this.buffer = ByteBuffer.allocate(4096);
    }

    @Override
    public InputStream getContent() throws IOException, UnsupportedOperationException {
        throw new UnsupportedOperationException("Does not support blocking methods");
    }

    @Override
    public boolean isStreaming() {
        return true;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException, UnsupportedOperationException {
        throw new UnsupportedOperationException("Does not support blocking methods");
    }

    @Override
    public void produceContent(ContentEncoder encoder, IOControl ioControl) throws IOException {
        int i = this.channel.read(this.buffer);
        this.buffer.flip();
        encoder.write(this.buffer);
        boolean buffering = this.buffer.hasRemaining();
        this.buffer.compact();
        if (i == -1 && !buffering) {
            encoder.complete();
            this.channel.close();
        }
    }

    @Override
    public void finish() {
        try {
            this.channel.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }
}

