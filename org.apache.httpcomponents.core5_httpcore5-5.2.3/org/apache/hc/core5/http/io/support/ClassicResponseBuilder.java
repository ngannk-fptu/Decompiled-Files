/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.io.support;

import java.util.Arrays;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.apache.hc.core5.http.support.AbstractResponseBuilder;
import org.apache.hc.core5.util.Args;

public class ClassicResponseBuilder
extends AbstractResponseBuilder<ClassicHttpResponse> {
    private HttpEntity entity;

    ClassicResponseBuilder(int status) {
        super(status);
    }

    public static ClassicResponseBuilder create(int status) {
        Args.checkRange(status, 100, 599, "HTTP status code");
        return new ClassicResponseBuilder(status);
    }

    public static ClassicResponseBuilder copy(ClassicHttpResponse response) {
        Args.notNull(response, "HTTP response");
        ClassicResponseBuilder builder = new ClassicResponseBuilder(response.getCode());
        builder.digest(response);
        return builder;
    }

    protected void digest(ClassicHttpResponse response) {
        super.digest(response);
        this.setEntity(response.getEntity());
    }

    @Override
    public ClassicResponseBuilder setVersion(ProtocolVersion version) {
        super.setVersion(version);
        return this;
    }

    @Override
    public ClassicResponseBuilder setHeaders(Header ... headers) {
        super.setHeaders(headers);
        return this;
    }

    @Override
    public ClassicResponseBuilder addHeader(Header header) {
        super.addHeader(header);
        return this;
    }

    @Override
    public ClassicResponseBuilder addHeader(String name, String value) {
        super.addHeader(name, value);
        return this;
    }

    @Override
    public ClassicResponseBuilder removeHeader(Header header) {
        super.removeHeader(header);
        return this;
    }

    @Override
    public ClassicResponseBuilder removeHeaders(String name) {
        super.removeHeaders(name);
        return this;
    }

    @Override
    public ClassicResponseBuilder setHeader(Header header) {
        super.setHeader(header);
        return this;
    }

    @Override
    public ClassicResponseBuilder setHeader(String name, String value) {
        super.setHeader(name, value);
        return this;
    }

    public HttpEntity getEntity() {
        return this.entity;
    }

    public ClassicResponseBuilder setEntity(HttpEntity entity) {
        this.entity = entity;
        return this;
    }

    public ClassicResponseBuilder setEntity(String content, ContentType contentType) {
        this.entity = new StringEntity(content, contentType);
        return this;
    }

    public ClassicResponseBuilder setEntity(String content) {
        this.entity = new StringEntity(content);
        return this;
    }

    public ClassicResponseBuilder setEntity(byte[] content, ContentType contentType) {
        this.entity = new ByteArrayEntity(content, contentType);
        return this;
    }

    @Override
    public ClassicHttpResponse build() {
        BasicClassicHttpResponse result = new BasicClassicHttpResponse(this.getStatus());
        result.setVersion(this.getVersion());
        result.setHeaders(this.getHeaders());
        result.setEntity(this.entity);
        return result;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ClassicResponseBuilder [status=");
        builder.append(this.getStatus());
        builder.append(", headerGroup=");
        builder.append(Arrays.toString(this.getHeaders()));
        builder.append(", entity=");
        builder.append(this.entity != null ? this.entity.getClass() : null);
        builder.append("]");
        return builder.toString();
    }
}

