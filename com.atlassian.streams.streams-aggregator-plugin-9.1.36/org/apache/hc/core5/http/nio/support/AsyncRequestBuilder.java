/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.nio.support;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.message.BasicHttpRequest;
import org.apache.hc.core5.http.nio.AsyncEntityProducer;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.http.nio.entity.BasicAsyncEntityProducer;
import org.apache.hc.core5.http.nio.entity.StringAsyncEntityProducer;
import org.apache.hc.core5.http.nio.support.BasicRequestProducer;
import org.apache.hc.core5.http.support.AbstractRequestBuilder;
import org.apache.hc.core5.net.URIAuthority;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.net.WWWFormCodec;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.TextUtils;

public class AsyncRequestBuilder
extends AbstractRequestBuilder<AsyncRequestProducer> {
    private AsyncEntityProducer entityProducer;

    AsyncRequestBuilder(String method) {
        super(method);
    }

    AsyncRequestBuilder(Method method) {
        super(method);
    }

    AsyncRequestBuilder(String method, URI uri) {
        super(method, uri);
    }

    AsyncRequestBuilder(Method method, URI uri) {
        this(method.name(), uri);
    }

    AsyncRequestBuilder(Method method, String uri) {
        this(method.name(), uri != null ? URI.create(uri) : null);
    }

    AsyncRequestBuilder(String method, String uri) {
        this(method, uri != null ? URI.create(uri) : null);
    }

    public static AsyncRequestBuilder create(String method) {
        Args.notBlank(method, "HTTP method");
        return new AsyncRequestBuilder(method);
    }

    public static AsyncRequestBuilder get() {
        return new AsyncRequestBuilder(Method.GET);
    }

    public static AsyncRequestBuilder get(URI uri) {
        return new AsyncRequestBuilder(Method.GET, uri);
    }

    public static AsyncRequestBuilder get(String uri) {
        return new AsyncRequestBuilder(Method.GET, uri);
    }

    public static AsyncRequestBuilder head() {
        return new AsyncRequestBuilder(Method.HEAD);
    }

    public static AsyncRequestBuilder head(URI uri) {
        return new AsyncRequestBuilder(Method.HEAD, uri);
    }

    public static AsyncRequestBuilder head(String uri) {
        return new AsyncRequestBuilder(Method.HEAD, uri);
    }

    public static AsyncRequestBuilder patch() {
        return new AsyncRequestBuilder(Method.PATCH);
    }

    public static AsyncRequestBuilder patch(URI uri) {
        return new AsyncRequestBuilder(Method.PATCH, uri);
    }

    public static AsyncRequestBuilder patch(String uri) {
        return new AsyncRequestBuilder(Method.PATCH, uri);
    }

    public static AsyncRequestBuilder post() {
        return new AsyncRequestBuilder(Method.POST);
    }

    public static AsyncRequestBuilder post(URI uri) {
        return new AsyncRequestBuilder(Method.POST, uri);
    }

    public static AsyncRequestBuilder post(String uri) {
        return new AsyncRequestBuilder(Method.POST, uri);
    }

    public static AsyncRequestBuilder put() {
        return new AsyncRequestBuilder(Method.PUT);
    }

    public static AsyncRequestBuilder put(URI uri) {
        return new AsyncRequestBuilder(Method.PUT, uri);
    }

    public static AsyncRequestBuilder put(String uri) {
        return new AsyncRequestBuilder(Method.PUT, uri);
    }

    public static AsyncRequestBuilder delete() {
        return new AsyncRequestBuilder(Method.DELETE);
    }

    public static AsyncRequestBuilder delete(URI uri) {
        return new AsyncRequestBuilder(Method.DELETE, uri);
    }

    public static AsyncRequestBuilder delete(String uri) {
        return new AsyncRequestBuilder(Method.DELETE, uri);
    }

    public static AsyncRequestBuilder trace() {
        return new AsyncRequestBuilder(Method.TRACE);
    }

    public static AsyncRequestBuilder trace(URI uri) {
        return new AsyncRequestBuilder(Method.TRACE, uri);
    }

    public static AsyncRequestBuilder trace(String uri) {
        return new AsyncRequestBuilder(Method.TRACE, uri);
    }

    public static AsyncRequestBuilder options() {
        return new AsyncRequestBuilder(Method.OPTIONS);
    }

    public static AsyncRequestBuilder options(URI uri) {
        return new AsyncRequestBuilder(Method.OPTIONS, uri);
    }

    public static AsyncRequestBuilder options(String uri) {
        return new AsyncRequestBuilder(Method.OPTIONS, uri);
    }

    @Override
    public AsyncRequestBuilder setVersion(ProtocolVersion version) {
        super.setVersion(version);
        return this;
    }

    public AsyncRequestBuilder setUri(URI uri) {
        super.setUri(uri);
        return this;
    }

    public AsyncRequestBuilder setUri(String uri) {
        super.setUri(uri);
        return this;
    }

    public AsyncRequestBuilder setScheme(String scheme) {
        super.setScheme(scheme);
        return this;
    }

    public AsyncRequestBuilder setAuthority(URIAuthority authority) {
        super.setAuthority(authority);
        return this;
    }

    public AsyncRequestBuilder setHttpHost(HttpHost httpHost) {
        super.setHttpHost(httpHost);
        return this;
    }

    public AsyncRequestBuilder setPath(String path) {
        super.setPath(path);
        return this;
    }

    @Override
    public AsyncRequestBuilder setHeaders(Header ... headers) {
        super.setHeaders(headers);
        return this;
    }

    @Override
    public AsyncRequestBuilder addHeader(Header header) {
        super.addHeader(header);
        return this;
    }

    @Override
    public AsyncRequestBuilder addHeader(String name, String value) {
        super.addHeader(name, value);
        return this;
    }

    @Override
    public AsyncRequestBuilder removeHeader(Header header) {
        super.removeHeader(header);
        return this;
    }

    @Override
    public AsyncRequestBuilder removeHeaders(String name) {
        super.removeHeaders(name);
        return this;
    }

    @Override
    public AsyncRequestBuilder setHeader(Header header) {
        super.setHeader(header);
        return this;
    }

    @Override
    public AsyncRequestBuilder setHeader(String name, String value) {
        super.setHeader(name, value);
        return this;
    }

    public AsyncRequestBuilder setCharset(Charset charset) {
        super.setCharset(charset);
        return this;
    }

    public AsyncRequestBuilder addParameter(NameValuePair nvp) {
        super.addParameter(nvp);
        return this;
    }

    public AsyncRequestBuilder addParameter(String name, String value) {
        super.addParameter(name, value);
        return this;
    }

    public AsyncRequestBuilder addParameters(NameValuePair ... nvps) {
        super.addParameters(nvps);
        return this;
    }

    public AsyncRequestBuilder setAbsoluteRequestUri(boolean absoluteRequestUri) {
        super.setAbsoluteRequestUri(absoluteRequestUri);
        return this;
    }

    public AsyncEntityProducer getEntity() {
        return this.entityProducer;
    }

    public AsyncRequestBuilder setEntity(AsyncEntityProducer entityProducer) {
        this.entityProducer = entityProducer;
        return this;
    }

    public AsyncRequestBuilder setEntity(String content, ContentType contentType) {
        this.entityProducer = new BasicAsyncEntityProducer(content, contentType);
        return this;
    }

    public AsyncRequestBuilder setEntity(String content) {
        this.entityProducer = new BasicAsyncEntityProducer(content);
        return this;
    }

    public AsyncRequestBuilder setEntity(byte[] content, ContentType contentType) {
        this.entityProducer = new BasicAsyncEntityProducer(content, contentType);
        return this;
    }

    @Override
    public AsyncRequestProducer build() {
        String path = this.getPath();
        if (TextUtils.isEmpty(path)) {
            path = "/";
        }
        AsyncEntityProducer entityProducerCopy = this.entityProducer;
        String method = this.getMethod();
        List<NameValuePair> parameters = this.getParameters();
        if (parameters != null && !parameters.isEmpty()) {
            Charset charset = this.getCharset();
            if (entityProducerCopy == null && (Method.POST.isSame(method) || Method.PUT.isSame(method))) {
                String content = WWWFormCodec.format(parameters, charset != null ? charset : ContentType.APPLICATION_FORM_URLENCODED.getCharset());
                entityProducerCopy = new StringAsyncEntityProducer(content, ContentType.APPLICATION_FORM_URLENCODED);
            } else {
                try {
                    URI uri = new URIBuilder(path).setCharset(charset).addParameters(parameters).build();
                    path = uri.toASCIIString();
                }
                catch (URISyntaxException ex) {
                    // empty catch block
                }
            }
        }
        if (entityProducerCopy != null && Method.TRACE.isSame(method)) {
            throw new IllegalStateException((Object)((Object)Method.TRACE) + " requests may not include an entity");
        }
        BasicHttpRequest request = new BasicHttpRequest(method, this.getScheme(), this.getAuthority(), path);
        request.setVersion(this.getVersion());
        request.setHeaders(this.getHeaders());
        request.setAbsoluteRequestUri(this.isAbsoluteRequestUri());
        return new BasicRequestProducer(request, entityProducerCopy);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AsyncRequestBuilder [method=");
        builder.append(this.getMethod());
        builder.append(", scheme=");
        builder.append(this.getScheme());
        builder.append(", authority=");
        builder.append(this.getAuthority());
        builder.append(", path=");
        builder.append(this.getPath());
        builder.append(", parameters=");
        builder.append(this.getParameters());
        builder.append(", headerGroup=");
        builder.append(Arrays.toString(this.getHeaders()));
        builder.append(", entity=");
        builder.append(this.entityProducer != null ? this.entityProducer.getClass() : null);
        builder.append("]");
        return builder.toString();
    }
}

