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
import org.apache.http.util.Args;

public class BasicAsyncResponseProducer
implements HttpAsyncResponseProducer {
    private final HttpResponse response;
    private final HttpAsyncContentProducer producer;

    protected BasicAsyncResponseProducer(HttpResponse response, HttpAsyncContentProducer producer) {
        Args.notNull(response, "HTTP response");
        Args.notNull(producer, "HTTP content producer");
        this.response = response;
        this.producer = producer;
    }

    public BasicAsyncResponseProducer(HttpResponse response) {
        Args.notNull(response, "HTTP response");
        this.response = response;
        HttpEntity entity = response.getEntity();
        this.producer = entity != null ? (entity instanceof HttpAsyncContentProducer ? (HttpAsyncContentProducer)((Object)entity) : new EntityAsyncContentProducer(entity)) : null;
    }

    @Override
    public HttpResponse generateResponse() {
        return this.response;
    }

    @Override
    public void produceContent(ContentEncoder encoder, IOControl ioControl) throws IOException {
        if (this.producer != null) {
            this.producer.produceContent(encoder, ioControl);
            if (encoder.isCompleted()) {
                this.producer.close();
            }
        }
    }

    @Override
    public void responseCompleted(HttpContext context) {
    }

    @Override
    public void failed(Exception ex) {
    }

    @Override
    public void close() throws IOException {
        if (this.producer != null) {
            this.producer.close();
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(this.response);
        if (this.producer != null) {
            buf.append(" ").append(this.producer);
        }
        return buf.toString();
    }
}

