/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.nio.support;

import java.util.Arrays;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.message.BasicHttpResponse;
import org.apache.hc.core5.http.nio.AsyncEntityProducer;
import org.apache.hc.core5.http.nio.AsyncPushProducer;
import org.apache.hc.core5.http.nio.entity.BasicAsyncEntityProducer;
import org.apache.hc.core5.http.nio.support.BasicPushProducer;
import org.apache.hc.core5.http.support.AbstractResponseBuilder;
import org.apache.hc.core5.util.Args;

public class AsyncPushBuilder
extends AbstractResponseBuilder<AsyncPushProducer> {
    private AsyncEntityProducer entityProducer;

    AsyncPushBuilder(int status) {
        super(status);
    }

    public static AsyncPushBuilder create(int status) {
        Args.checkRange(status, 100, 599, "HTTP status code");
        return new AsyncPushBuilder(status);
    }

    @Override
    public AsyncPushBuilder setVersion(ProtocolVersion version) {
        super.setVersion(version);
        return this;
    }

    @Override
    public AsyncPushBuilder setHeaders(Header ... headers) {
        super.setHeaders(headers);
        return this;
    }

    @Override
    public AsyncPushBuilder addHeader(Header header) {
        super.addHeader(header);
        return this;
    }

    @Override
    public AsyncPushBuilder addHeader(String name, String value) {
        super.addHeader(name, value);
        return this;
    }

    @Override
    public AsyncPushBuilder removeHeader(Header header) {
        super.removeHeader(header);
        return this;
    }

    @Override
    public AsyncPushBuilder removeHeaders(String name) {
        super.removeHeaders(name);
        return this;
    }

    @Override
    public AsyncPushBuilder setHeader(Header header) {
        super.setHeader(header);
        return this;
    }

    @Override
    public AsyncPushBuilder setHeader(String name, String value) {
        super.setHeader(name, value);
        return this;
    }

    public AsyncEntityProducer getEntity() {
        return this.entityProducer;
    }

    public AsyncPushBuilder setEntity(AsyncEntityProducer entityProducer) {
        this.entityProducer = entityProducer;
        return this;
    }

    public AsyncPushBuilder setEntity(String content, ContentType contentType) {
        this.entityProducer = new BasicAsyncEntityProducer(content, contentType);
        return this;
    }

    public AsyncPushBuilder setEntity(String content) {
        this.entityProducer = new BasicAsyncEntityProducer(content);
        return this;
    }

    public AsyncPushBuilder setEntity(byte[] content, ContentType contentType) {
        this.entityProducer = new BasicAsyncEntityProducer(content, contentType);
        return this;
    }

    @Override
    public AsyncPushProducer build() {
        BasicHttpResponse response = new BasicHttpResponse(this.getStatus());
        response.setVersion(this.getVersion());
        response.setHeaders(this.getHeaders());
        return new BasicPushProducer(response, this.entityProducer);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AsyncPushProducer [status=");
        builder.append(this.getStatus());
        builder.append(", headerGroup=");
        builder.append(Arrays.toString(this.getHeaders()));
        builder.append(", entity=");
        builder.append(this.entityProducer != null ? this.entityProducer.getClass() : null);
        builder.append("]");
        return builder.toString();
    }
}

