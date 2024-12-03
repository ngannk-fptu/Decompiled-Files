/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.nio.support;

import java.util.Arrays;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.message.BasicHttpResponse;
import org.apache.hc.core5.http.nio.AsyncEntityProducer;
import org.apache.hc.core5.http.nio.AsyncResponseProducer;
import org.apache.hc.core5.http.nio.entity.BasicAsyncEntityProducer;
import org.apache.hc.core5.http.nio.support.BasicResponseProducer;
import org.apache.hc.core5.http.support.AbstractResponseBuilder;
import org.apache.hc.core5.util.Args;

public class AsyncResponseBuilder
extends AbstractResponseBuilder<AsyncResponseProducer> {
    private AsyncEntityProducer entityProducer;

    AsyncResponseBuilder(int status) {
        super(status);
    }

    public static AsyncResponseBuilder create(int status) {
        Args.checkRange(status, 100, 599, "HTTP status code");
        return new AsyncResponseBuilder(status);
    }

    @Override
    public AsyncResponseBuilder setVersion(ProtocolVersion version) {
        super.setVersion(version);
        return this;
    }

    @Override
    public AsyncResponseBuilder setHeaders(Header ... headers) {
        super.setHeaders(headers);
        return this;
    }

    @Override
    public AsyncResponseBuilder addHeader(Header header) {
        super.addHeader(header);
        return this;
    }

    @Override
    public AsyncResponseBuilder addHeader(String name, String value) {
        super.addHeader(name, value);
        return this;
    }

    @Override
    public AsyncResponseBuilder removeHeader(Header header) {
        super.removeHeader(header);
        return this;
    }

    @Override
    public AsyncResponseBuilder removeHeaders(String name) {
        super.removeHeaders(name);
        return this;
    }

    @Override
    public AsyncResponseBuilder setHeader(Header header) {
        super.setHeader(header);
        return this;
    }

    @Override
    public AsyncResponseBuilder setHeader(String name, String value) {
        super.setHeader(name, value);
        return this;
    }

    public AsyncEntityProducer getEntity() {
        return this.entityProducer;
    }

    public AsyncResponseBuilder setEntity(AsyncEntityProducer entityProducer) {
        this.entityProducer = entityProducer;
        return this;
    }

    public AsyncResponseBuilder setEntity(String content, ContentType contentType) {
        this.entityProducer = new BasicAsyncEntityProducer(content, contentType);
        return this;
    }

    public AsyncResponseBuilder setEntity(String content) {
        this.entityProducer = new BasicAsyncEntityProducer(content);
        return this;
    }

    public AsyncResponseBuilder setEntity(byte[] content, ContentType contentType) {
        this.entityProducer = new BasicAsyncEntityProducer(content, contentType);
        return this;
    }

    @Override
    public AsyncResponseProducer build() {
        BasicHttpResponse response = new BasicHttpResponse(this.getStatus());
        response.setVersion(this.getVersion());
        response.setHeaders(this.getHeaders());
        return new BasicResponseProducer((HttpResponse)response, this.entityProducer);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AsyncResponseBuilder [status=");
        builder.append(this.getStatus());
        builder.append(", headerGroup=");
        builder.append(Arrays.toString(this.getHeaders()));
        builder.append(", entity=");
        builder.append(this.entityProducer != null ? this.entityProducer.getClass() : null);
        builder.append("]");
        return builder.toString();
    }
}

