/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.io.support;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.io.entity.HttpEntities;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpRequest;
import org.apache.hc.core5.http.support.AbstractRequestBuilder;
import org.apache.hc.core5.net.URIAuthority;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.TextUtils;

public class ClassicRequestBuilder
extends AbstractRequestBuilder<ClassicHttpRequest> {
    private HttpEntity entity;

    ClassicRequestBuilder(String method) {
        super(method);
    }

    ClassicRequestBuilder(Method method) {
        super(method);
    }

    ClassicRequestBuilder(String method, URI uri) {
        super(method, uri);
    }

    ClassicRequestBuilder(Method method, URI uri) {
        super(method, uri);
    }

    ClassicRequestBuilder(Method method, String uri) {
        super(method, uri);
    }

    ClassicRequestBuilder(String method, String uri) {
        super(method, uri);
    }

    public static ClassicRequestBuilder create(String method) {
        Args.notBlank(method, "HTTP method");
        return new ClassicRequestBuilder(method);
    }

    public static ClassicRequestBuilder get() {
        return new ClassicRequestBuilder(Method.GET);
    }

    public static ClassicRequestBuilder get(URI uri) {
        return new ClassicRequestBuilder(Method.GET, uri);
    }

    public static ClassicRequestBuilder get(String uri) {
        return new ClassicRequestBuilder(Method.GET, uri);
    }

    public static ClassicRequestBuilder head() {
        return new ClassicRequestBuilder(Method.HEAD);
    }

    public static ClassicRequestBuilder head(URI uri) {
        return new ClassicRequestBuilder(Method.HEAD, uri);
    }

    public static ClassicRequestBuilder head(String uri) {
        return new ClassicRequestBuilder(Method.HEAD, uri);
    }

    public static ClassicRequestBuilder patch() {
        return new ClassicRequestBuilder(Method.PATCH);
    }

    public static ClassicRequestBuilder patch(URI uri) {
        return new ClassicRequestBuilder(Method.PATCH, uri);
    }

    public static ClassicRequestBuilder patch(String uri) {
        return new ClassicRequestBuilder(Method.PATCH, uri);
    }

    public static ClassicRequestBuilder post() {
        return new ClassicRequestBuilder(Method.POST);
    }

    public static ClassicRequestBuilder post(URI uri) {
        return new ClassicRequestBuilder(Method.POST, uri);
    }

    public static ClassicRequestBuilder post(String uri) {
        return new ClassicRequestBuilder(Method.POST, uri);
    }

    public static ClassicRequestBuilder put() {
        return new ClassicRequestBuilder(Method.PUT);
    }

    public static ClassicRequestBuilder put(URI uri) {
        return new ClassicRequestBuilder(Method.PUT, uri);
    }

    public static ClassicRequestBuilder put(String uri) {
        return new ClassicRequestBuilder(Method.PUT, uri);
    }

    public static ClassicRequestBuilder delete() {
        return new ClassicRequestBuilder(Method.DELETE);
    }

    public static ClassicRequestBuilder delete(URI uri) {
        return new ClassicRequestBuilder(Method.DELETE, uri);
    }

    public static ClassicRequestBuilder delete(String uri) {
        return new ClassicRequestBuilder(Method.DELETE, uri);
    }

    public static ClassicRequestBuilder trace() {
        return new ClassicRequestBuilder(Method.TRACE);
    }

    public static ClassicRequestBuilder trace(URI uri) {
        return new ClassicRequestBuilder(Method.TRACE, uri);
    }

    public static ClassicRequestBuilder trace(String uri) {
        return new ClassicRequestBuilder(Method.TRACE, uri);
    }

    public static ClassicRequestBuilder options() {
        return new ClassicRequestBuilder(Method.OPTIONS);
    }

    public static ClassicRequestBuilder options(URI uri) {
        return new ClassicRequestBuilder(Method.OPTIONS, uri);
    }

    public static ClassicRequestBuilder options(String uri) {
        return new ClassicRequestBuilder(Method.OPTIONS, uri);
    }

    public static ClassicRequestBuilder copy(ClassicHttpRequest request) {
        Args.notNull(request, "HTTP request");
        ClassicRequestBuilder builder = new ClassicRequestBuilder(request.getMethod());
        builder.digest(request);
        return builder;
    }

    protected void digest(ClassicHttpRequest request) {
        super.digest(request);
        this.setEntity(request.getEntity());
    }

    @Override
    public ClassicRequestBuilder setVersion(ProtocolVersion version) {
        super.setVersion(version);
        return this;
    }

    public ClassicRequestBuilder setUri(URI uri) {
        super.setUri(uri);
        return this;
    }

    public ClassicRequestBuilder setUri(String uri) {
        super.setUri(uri);
        return this;
    }

    public ClassicRequestBuilder setScheme(String scheme) {
        super.setScheme(scheme);
        return this;
    }

    public ClassicRequestBuilder setAuthority(URIAuthority authority) {
        super.setAuthority(authority);
        return this;
    }

    public ClassicRequestBuilder setHttpHost(HttpHost httpHost) {
        super.setHttpHost(httpHost);
        return this;
    }

    public ClassicRequestBuilder setPath(String path) {
        super.setPath(path);
        return this;
    }

    @Override
    public ClassicRequestBuilder setHeaders(Header ... headers) {
        super.setHeaders(headers);
        return this;
    }

    @Override
    public ClassicRequestBuilder addHeader(Header header) {
        super.addHeader(header);
        return this;
    }

    @Override
    public ClassicRequestBuilder addHeader(String name, String value) {
        super.addHeader(name, value);
        return this;
    }

    @Override
    public ClassicRequestBuilder removeHeader(Header header) {
        super.removeHeader(header);
        return this;
    }

    @Override
    public ClassicRequestBuilder removeHeaders(String name) {
        super.removeHeaders(name);
        return this;
    }

    @Override
    public ClassicRequestBuilder setHeader(Header header) {
        super.setHeader(header);
        return this;
    }

    @Override
    public ClassicRequestBuilder setHeader(String name, String value) {
        super.setHeader(name, value);
        return this;
    }

    public ClassicRequestBuilder setCharset(Charset charset) {
        super.setCharset(charset);
        return this;
    }

    public ClassicRequestBuilder addParameter(NameValuePair nvp) {
        super.addParameter(nvp);
        return this;
    }

    public ClassicRequestBuilder addParameter(String name, String value) {
        super.addParameter(name, value);
        return this;
    }

    public ClassicRequestBuilder addParameters(NameValuePair ... nvps) {
        super.addParameters(nvps);
        return this;
    }

    public ClassicRequestBuilder setAbsoluteRequestUri(boolean absoluteRequestUri) {
        super.setAbsoluteRequestUri(absoluteRequestUri);
        return this;
    }

    public HttpEntity getEntity() {
        return this.entity;
    }

    public ClassicRequestBuilder setEntity(HttpEntity entity) {
        this.entity = entity;
        return this;
    }

    public ClassicRequestBuilder setEntity(String content, ContentType contentType) {
        this.entity = new StringEntity(content, contentType);
        return this;
    }

    public ClassicRequestBuilder setEntity(String content) {
        this.entity = new StringEntity(content);
        return this;
    }

    public ClassicRequestBuilder setEntity(byte[] content, ContentType contentType) {
        this.entity = new ByteArrayEntity(content, contentType);
        return this;
    }

    @Override
    public ClassicHttpRequest build() {
        String path = this.getPath();
        if (TextUtils.isEmpty(path)) {
            path = "/";
        }
        HttpEntity entityCopy = this.entity;
        String method = this.getMethod();
        List<NameValuePair> parameters = this.getParameters();
        if (parameters != null && !parameters.isEmpty()) {
            if (entityCopy == null && (Method.POST.isSame(method) || Method.PUT.isSame(method))) {
                entityCopy = HttpEntities.createUrlEncoded(parameters, this.getCharset());
            } else {
                try {
                    URI uri = new URIBuilder(path).setCharset(this.getCharset()).addParameters(parameters).build();
                    path = uri.toASCIIString();
                }
                catch (URISyntaxException uri) {
                    // empty catch block
                }
            }
        }
        if (entityCopy != null && Method.TRACE.isSame(method)) {
            throw new IllegalStateException((Object)((Object)Method.TRACE) + " requests may not include an entity");
        }
        BasicClassicHttpRequest result = new BasicClassicHttpRequest(method, this.getScheme(), this.getAuthority(), path);
        result.setVersion(this.getVersion());
        result.setHeaders(this.getHeaders());
        result.setEntity(entityCopy);
        result.setAbsoluteRequestUri(this.isAbsoluteRequestUri());
        return result;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ClassicRequestBuilder [method=");
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
        builder.append(this.entity != null ? this.entity.getClass() : null);
        builder.append("]");
        return builder.toString();
    }
}

