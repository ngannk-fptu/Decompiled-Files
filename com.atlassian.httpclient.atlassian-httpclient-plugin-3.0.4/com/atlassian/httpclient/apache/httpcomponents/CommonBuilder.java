/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.httpclient.apache.httpcomponents;

import com.atlassian.httpclient.apache.httpcomponents.EntityByteArrayInputStream;
import com.atlassian.httpclient.apache.httpcomponents.Headers;
import com.atlassian.httpclient.api.Common;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

public class CommonBuilder<T>
implements Common<CommonBuilder<T>> {
    private final Headers.Builder headersBuilder = new Headers.Builder();
    private InputStream entityStream;

    @Override
    public CommonBuilder<T> setHeader(String name, String value) {
        this.headersBuilder.setHeader(name, value);
        return this;
    }

    @Override
    public CommonBuilder<T> setHeaders(Map<String, String> headers) {
        this.headersBuilder.setHeaders(headers);
        return this;
    }

    @Override
    public CommonBuilder<T> setEntity(String entity) {
        if (entity != null) {
            String charset = "UTF-8";
            byte[] bytes = entity.getBytes(Charset.forName("UTF-8"));
            this.setEntityStream(new EntityByteArrayInputStream(bytes), "UTF-8");
        } else {
            this.setEntityStream(null, null);
        }
        return this;
    }

    @Override
    public CommonBuilder<T> setEntityStream(InputStream entityStream) {
        this.entityStream = entityStream;
        return this;
    }

    @Override
    public CommonBuilder<T> setContentCharset(String contentCharset) {
        this.headersBuilder.setContentCharset(contentCharset);
        return this;
    }

    @Override
    public CommonBuilder<T> setContentType(String contentType) {
        this.headersBuilder.setContentType(contentType);
        return this;
    }

    @Override
    public CommonBuilder<T> setEntityStream(InputStream entityStream, String charset) {
        this.setEntityStream(entityStream);
        this.headersBuilder.setContentCharset(charset);
        return this;
    }

    public InputStream getEntityStream() {
        return this.entityStream;
    }

    public Headers getHeaders() {
        return this.headersBuilder.build();
    }
}

