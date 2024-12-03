/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.impl.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.nio.AsyncPushConsumer;
import org.apache.hc.core5.http.nio.HandlerFactory;
import org.apache.hc.core5.http2.impl.nio.H2StreamHandler;

final class NoopH2StreamHandler
implements H2StreamHandler {
    static final NoopH2StreamHandler INSTANCE = new NoopH2StreamHandler();

    NoopH2StreamHandler() {
    }

    @Override
    public boolean isOutputReady() {
        return false;
    }

    @Override
    public void produceOutput() throws HttpException, IOException {
    }

    @Override
    public void consumePromise(List<Header> headers) throws HttpException, IOException {
    }

    @Override
    public void consumeHeader(List<Header> headers, boolean endStream) throws HttpException, IOException {
    }

    @Override
    public void updateInputCapacity() throws IOException {
    }

    @Override
    public void consumeData(ByteBuffer src, boolean endStream) throws HttpException, IOException {
    }

    @Override
    public HandlerFactory<AsyncPushConsumer> getPushHandlerFactory() {
        return null;
    }

    @Override
    public void failed(Exception cause) {
    }

    @Override
    public void handle(HttpException ex, boolean endStream) throws HttpException, IOException {
    }

    @Override
    public void releaseResources() {
    }
}

