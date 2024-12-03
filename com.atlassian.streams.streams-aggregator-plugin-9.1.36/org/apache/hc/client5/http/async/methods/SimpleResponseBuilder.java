/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.async.methods;

import java.util.Arrays;
import org.apache.hc.client5.http.async.methods.SimpleBody;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.support.AbstractResponseBuilder;
import org.apache.hc.core5.util.Args;

public class SimpleResponseBuilder
extends AbstractResponseBuilder<SimpleHttpResponse> {
    private SimpleBody body;

    SimpleResponseBuilder(int status) {
        super(status);
    }

    public static SimpleResponseBuilder create(int status) {
        Args.checkRange(status, 100, 599, "HTTP status code");
        return new SimpleResponseBuilder(status);
    }

    public static SimpleResponseBuilder copy(SimpleHttpResponse response) {
        Args.notNull(response, "HTTP response");
        SimpleResponseBuilder builder = new SimpleResponseBuilder(response.getCode());
        builder.digest(response);
        return builder;
    }

    protected void digest(SimpleHttpResponse response) {
        super.digest(response);
        this.setBody(response.getBody());
    }

    @Override
    public SimpleResponseBuilder setVersion(ProtocolVersion version) {
        super.setVersion(version);
        return this;
    }

    @Override
    public SimpleResponseBuilder setHeaders(Header ... headers) {
        super.setHeaders(headers);
        return this;
    }

    @Override
    public SimpleResponseBuilder addHeader(Header header) {
        super.addHeader(header);
        return this;
    }

    @Override
    public SimpleResponseBuilder addHeader(String name, String value) {
        super.addHeader(name, value);
        return this;
    }

    @Override
    public SimpleResponseBuilder removeHeader(Header header) {
        super.removeHeader(header);
        return this;
    }

    @Override
    public SimpleResponseBuilder removeHeaders(String name) {
        super.removeHeaders(name);
        return this;
    }

    @Override
    public SimpleResponseBuilder setHeader(Header header) {
        super.setHeader(header);
        return this;
    }

    @Override
    public SimpleResponseBuilder setHeader(String name, String value) {
        super.setHeader(name, value);
        return this;
    }

    public SimpleBody getBody() {
        return this.body;
    }

    public SimpleResponseBuilder setBody(SimpleBody body) {
        this.body = body;
        return this;
    }

    public SimpleResponseBuilder setBody(String content, ContentType contentType) {
        this.body = SimpleBody.create(content, contentType);
        return this;
    }

    public SimpleResponseBuilder setBody(byte[] content, ContentType contentType) {
        this.body = SimpleBody.create(content, contentType);
        return this;
    }

    @Override
    public SimpleHttpResponse build() {
        SimpleHttpResponse result = new SimpleHttpResponse(this.getStatus());
        result.setVersion(this.getVersion());
        result.setHeaders(this.getHeaders());
        result.setBody(this.body);
        return result;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SimpleResponseBuilder [status=");
        builder.append(this.getStatus());
        builder.append(", headerGroup=");
        builder.append(Arrays.toString(this.getHeaders()));
        builder.append(", body=");
        builder.append(this.body);
        builder.append("]");
        return builder.toString();
    }
}

