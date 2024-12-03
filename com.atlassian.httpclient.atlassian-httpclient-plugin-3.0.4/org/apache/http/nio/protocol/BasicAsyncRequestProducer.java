/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.protocol;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.entity.EntityAsyncContentProducer;
import org.apache.http.nio.entity.HttpAsyncContentProducer;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

public class BasicAsyncRequestProducer
implements HttpAsyncRequestProducer {
    private final HttpHost target;
    private final HttpRequest request;
    private final HttpAsyncContentProducer producer;

    protected BasicAsyncRequestProducer(HttpHost target, HttpEntityEnclosingRequest request, HttpAsyncContentProducer producer) {
        Args.notNull(target, "HTTP host");
        Args.notNull(request, "HTTP request");
        Args.notNull(producer, "HTTP content producer");
        this.target = target;
        this.request = request;
        this.producer = producer;
    }

    public BasicAsyncRequestProducer(HttpHost target, HttpRequest request) {
        HttpEntity entity;
        Args.notNull(target, "HTTP host");
        Args.notNull(request, "HTTP request");
        this.target = target;
        this.request = request;
        this.producer = request instanceof HttpEntityEnclosingRequest ? ((entity = ((HttpEntityEnclosingRequest)request).getEntity()) != null ? (entity instanceof HttpAsyncContentProducer ? (HttpAsyncContentProducer)((Object)entity) : new EntityAsyncContentProducer(entity)) : null) : null;
    }

    @Override
    public HttpRequest generateRequest() {
        return this.request;
    }

    @Override
    public HttpHost getTarget() {
        return this.target;
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
    public void requestCompleted(HttpContext context) {
    }

    @Override
    public void failed(Exception ex) {
    }

    @Override
    public boolean isRepeatable() {
        return this.producer == null || this.producer.isRepeatable();
    }

    @Override
    public void resetRequest() throws IOException {
        if (this.producer != null) {
            this.producer.close();
        }
    }

    @Override
    public void close() throws IOException {
        if (this.producer != null) {
            this.producer.close();
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.target);
        sb.append(' ');
        sb.append(this.request);
        if (this.producer != null) {
            sb.append(' ');
            sb.append(this.producer);
        }
        return sb.toString();
    }
}

