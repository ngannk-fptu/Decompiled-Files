/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  io.atlassian.fugue.Option
 */
package com.atlassian.httpclient.apache.httpcomponents;

import com.atlassian.httpclient.apache.httpcomponents.CommonBuilder;
import com.atlassian.httpclient.apache.httpcomponents.DefaultMessage;
import com.atlassian.httpclient.apache.httpcomponents.Headers;
import com.atlassian.httpclient.api.EntityBuilder;
import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.httpclient.api.Request;
import com.atlassian.httpclient.api.ResponsePromise;
import com.google.common.base.Preconditions;
import io.atlassian.fugue.Option;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DefaultRequest
extends DefaultMessage
implements Request {
    private final URI uri;
    private final boolean cacheDisabled;
    private final Map<String, String> attributes;
    private final Request.Method method;
    private final Option<Long> contentLength;

    private DefaultRequest(URI uri, boolean cacheDisabled, Map<String, String> attributes, Headers headers, Request.Method method, InputStream entityStream, Option<Long> contentLength) {
        super(headers, entityStream, (Option<Long>)Option.none());
        this.uri = uri;
        this.cacheDisabled = cacheDisabled;
        this.attributes = attributes;
        this.method = method;
        this.contentLength = contentLength;
    }

    public static DefaultRequestBuilder builder(HttpClient httpClient) {
        return new DefaultRequestBuilder(httpClient);
    }

    @Override
    public Request.Method getMethod() {
        return this.method;
    }

    @Override
    public URI getUri() {
        return this.uri;
    }

    @Override
    public String getAccept() {
        return super.getAccept();
    }

    @Override
    public String getAttribute(String name) {
        return this.attributes.get(name);
    }

    @Override
    public Map<String, String> getAttributes() {
        return Collections.unmodifiableMap(this.attributes);
    }

    @Override
    public Option<Long> getContentLength() {
        return this.contentLength;
    }

    @Override
    public boolean isCacheDisabled() {
        return this.cacheDisabled;
    }

    @Override
    public Request validate() {
        super.validate();
        Preconditions.checkNotNull((Object)this.uri);
        Preconditions.checkNotNull((Object)((Object)this.method));
        switch (this.method) {
            case GET: 
            case DELETE: 
            case HEAD: {
                if (!this.hasEntity()) break;
                throw new IllegalStateException("Request method " + (Object)((Object)this.method) + " does not support an entity");
            }
        }
        return this;
    }

    public static class DefaultRequestBuilder
    implements Request.Builder {
        private final HttpClient httpClient;
        private final Map<String, String> attributes;
        private final CommonBuilder<DefaultRequest> commonBuilder;
        private URI uri;
        private boolean cacheDisabled;
        private Request.Method method;
        private Option<Long> contentLength;

        public DefaultRequestBuilder(HttpClient httpClient) {
            this.httpClient = httpClient;
            this.attributes = new HashMap<String, String>();
            this.commonBuilder = new CommonBuilder();
            this.setAccept("*/*");
            this.contentLength = Option.none();
        }

        @Override
        public DefaultRequestBuilder setUri(URI uri) {
            this.uri = uri;
            return this;
        }

        @Override
        public DefaultRequestBuilder setAccept(String accept) {
            this.setHeader("Accept", accept);
            return this;
        }

        @Override
        public DefaultRequestBuilder setCacheDisabled() {
            this.cacheDisabled = true;
            return this;
        }

        @Override
        public DefaultRequestBuilder setAttribute(String name, String value) {
            this.attributes.put(name, value);
            return this;
        }

        @Override
        public DefaultRequestBuilder setAttributes(Map<String, String> properties) {
            this.attributes.putAll(properties);
            return this;
        }

        @Override
        public DefaultRequestBuilder setEntity(EntityBuilder entityBuilder) {
            EntityBuilder.Entity entity = entityBuilder.build();
            Map<String, String> headers = entity.getHeaders();
            for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                this.setHeader(headerEntry.getKey(), headerEntry.getValue());
            }
            this.setEntityStream(entity.getInputStream());
            return this;
        }

        @Override
        public DefaultRequestBuilder setHeader(String name, String value) {
            this.commonBuilder.setHeader(name, value);
            return this;
        }

        @Override
        public DefaultRequestBuilder setHeaders(Map<String, String> headers) {
            this.commonBuilder.setHeaders((Map)headers);
            return this;
        }

        @Override
        public DefaultRequestBuilder setEntity(String entity) {
            this.commonBuilder.setEntity(entity);
            this.setContentLength(entity.length());
            return this;
        }

        @Override
        public DefaultRequestBuilder setEntityStream(InputStream entityStream) {
            this.commonBuilder.setEntityStream(entityStream);
            return this;
        }

        @Override
        public DefaultRequestBuilder setContentCharset(String contentCharset) {
            this.commonBuilder.setContentCharset(contentCharset);
            return this;
        }

        @Override
        public DefaultRequestBuilder setContentType(String contentType) {
            this.commonBuilder.setContentType(contentType);
            return this;
        }

        @Override
        public DefaultRequestBuilder setEntityStream(InputStream entityStream, String charset) {
            this.setEntityStream(entityStream);
            this.commonBuilder.setContentCharset(charset);
            return this;
        }

        @Override
        public DefaultRequestBuilder setContentLength(long contentLength) {
            Preconditions.checkArgument((contentLength >= 0L ? 1 : 0) != 0, (Object)"Content length must be greater than or equal to 0");
            this.contentLength = Option.some((Object)contentLength);
            return this;
        }

        @Override
        public DefaultRequest build() {
            return new DefaultRequest(this.uri, this.cacheDisabled, this.attributes, this.commonBuilder.getHeaders(), this.method, this.commonBuilder.getEntityStream(), this.contentLength);
        }

        @Override
        public ResponsePromise get() {
            return this.execute(Request.Method.GET);
        }

        @Override
        public ResponsePromise post() {
            return this.execute(Request.Method.POST);
        }

        @Override
        public ResponsePromise put() {
            return this.execute(Request.Method.PUT);
        }

        @Override
        public ResponsePromise delete() {
            return this.execute(Request.Method.DELETE);
        }

        @Override
        public ResponsePromise options() {
            return this.execute(Request.Method.OPTIONS);
        }

        @Override
        public ResponsePromise head() {
            return this.execute(Request.Method.HEAD);
        }

        @Override
        public ResponsePromise trace() {
            return this.execute(Request.Method.TRACE);
        }

        @Override
        public ResponsePromise execute(Request.Method method) {
            Preconditions.checkNotNull((Object)((Object)method), (Object)"HTTP method must not be null");
            this.setMethod(method);
            return this.httpClient.execute(this.build().validate());
        }

        public void setMethod(Request.Method method) {
            this.method = method;
        }
    }
}

