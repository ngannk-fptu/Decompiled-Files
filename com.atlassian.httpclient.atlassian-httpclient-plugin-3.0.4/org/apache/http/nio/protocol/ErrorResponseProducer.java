/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.protocol;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.entity.EntityAsyncContentProducer;
import org.apache.http.nio.entity.HttpAsyncContentProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseProducer;
import org.apache.http.protocol.HttpContext;

public class ErrorResponseProducer
implements HttpAsyncResponseProducer {
    private final HttpResponse response;
    private final HttpEntity entity;
    private final HttpAsyncContentProducer contentProducer;
    private final boolean keepAlive;

    public ErrorResponseProducer(HttpResponse response, HttpEntity entity, boolean keepAlive) {
        this.response = response;
        this.entity = entity;
        this.contentProducer = entity instanceof HttpAsyncContentProducer ? (HttpAsyncContentProducer)((Object)entity) : new EntityAsyncContentProducer(entity);
        this.keepAlive = keepAlive;
    }

    @Override
    public HttpResponse generateResponse() {
        if (this.keepAlive) {
            this.response.addHeader("Connection", "Keep-Alive");
        } else {
            this.response.addHeader("Connection", "Close");
        }
        this.response.setEntity(this.entity);
        return this.response;
    }

    @Override
    public void produceContent(ContentEncoder encoder, IOControl ioControl) throws IOException {
        this.contentProducer.produceContent(encoder, ioControl);
    }

    @Override
    public void responseCompleted(HttpContext context) {
    }

    @Override
    public void failed(Exception ex) {
    }

    @Override
    public void close() throws IOException {
        this.contentProducer.close();
    }
}

