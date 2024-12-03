/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.support;

import java.util.Arrays;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.message.BasicHttpResponse;
import org.apache.hc.core5.http.support.AbstractResponseBuilder;
import org.apache.hc.core5.util.Args;

public class BasicResponseBuilder
extends AbstractResponseBuilder<BasicHttpResponse> {
    protected BasicResponseBuilder(int status) {
        super(status);
    }

    public static BasicResponseBuilder create(int status) {
        Args.checkRange(status, 100, 599, "HTTP status code");
        return new BasicResponseBuilder(status);
    }

    public static BasicResponseBuilder copy(HttpResponse response) {
        Args.notNull(response, "HTTP response");
        BasicResponseBuilder builder = new BasicResponseBuilder(response.getCode());
        builder.digest(response);
        return builder;
    }

    @Override
    public BasicResponseBuilder setVersion(ProtocolVersion version) {
        super.setVersion(version);
        return this;
    }

    @Override
    public BasicResponseBuilder setHeaders(Header ... headers) {
        super.setHeaders(headers);
        return this;
    }

    @Override
    public BasicResponseBuilder addHeader(Header header) {
        super.addHeader(header);
        return this;
    }

    @Override
    public BasicResponseBuilder addHeader(String name, String value) {
        super.addHeader(name, value);
        return this;
    }

    @Override
    public BasicResponseBuilder removeHeader(Header header) {
        super.removeHeader(header);
        return this;
    }

    @Override
    public BasicResponseBuilder removeHeaders(String name) {
        super.removeHeaders(name);
        return this;
    }

    @Override
    public BasicResponseBuilder setHeader(Header header) {
        super.setHeader(header);
        return this;
    }

    @Override
    public BasicResponseBuilder setHeader(String name, String value) {
        super.setHeader(name, value);
        return this;
    }

    @Override
    public BasicHttpResponse build() {
        BasicHttpResponse result = new BasicHttpResponse(this.getStatus());
        result.setVersion(this.getVersion());
        result.setHeaders(this.getHeaders());
        return result;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("BasicResponseBuilder [status=");
        builder.append(this.getStatus());
        builder.append(", headerGroup=");
        builder.append(Arrays.toString(this.getHeaders()));
        builder.append("]");
        return builder.toString();
    }
}

