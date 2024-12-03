/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.impl.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.nio.AsyncPushConsumer;
import org.apache.hc.core5.http.nio.CapacityChannel;
import org.apache.hc.core5.http.protocol.HttpContext;

class NoopAsyncPushHandler
implements AsyncPushConsumer {
    NoopAsyncPushHandler() {
    }

    @Override
    public void consumePromise(HttpRequest promise, HttpResponse response, EntityDetails entityDetails, HttpContext context) throws HttpException, IOException {
    }

    @Override
    public void updateCapacity(CapacityChannel capacityChannel) throws IOException {
        capacityChannel.update(Integer.MAX_VALUE);
    }

    @Override
    public void consume(ByteBuffer src) throws IOException {
    }

    @Override
    public void streamEnd(List<? extends Header> trailers) throws HttpException, IOException {
    }

    @Override
    public void failed(Exception cause) {
    }

    @Override
    public void releaseResources() {
    }
}

