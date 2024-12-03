/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.ContentType
 *  org.apache.hc.core5.http.Header
 *  org.apache.hc.core5.http.HttpHost
 *  org.apache.hc.core5.http.HttpRequest
 *  org.apache.hc.core5.http.Method
 *  org.apache.hc.core5.http.NameValuePair
 *  org.apache.hc.core5.http.ProtocolVersion
 *  org.apache.hc.core5.http.support.AbstractRequestBuilder
 *  org.apache.hc.core5.net.URIAuthority
 *  org.apache.hc.core5.net.URIBuilder
 *  org.apache.hc.core5.net.WWWFormCodec
 *  org.apache.hc.core5.util.Args
 */
package org.apache.hc.client5.http.async.methods;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import org.apache.hc.client5.http.async.methods.SimpleBody;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.support.AbstractRequestBuilder;
import org.apache.hc.core5.net.URIAuthority;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.net.WWWFormCodec;
import org.apache.hc.core5.util.Args;

public class SimpleRequestBuilder
extends AbstractRequestBuilder<SimpleHttpRequest> {
    private SimpleBody body;
    private RequestConfig requestConfig;

    SimpleRequestBuilder(String method) {
        super(method);
    }

    SimpleRequestBuilder(Method method) {
        super(method);
    }

    SimpleRequestBuilder(String method, URI uri) {
        super(method, uri);
    }

    SimpleRequestBuilder(Method method, URI uri) {
        super(method, uri);
    }

    SimpleRequestBuilder(Method method, String uri) {
        super(method, uri);
    }

    SimpleRequestBuilder(String method, String uri) {
        super(method, uri);
    }

    public static SimpleRequestBuilder create(String method) {
        Args.notBlank((CharSequence)method, (String)"HTTP method");
        return new SimpleRequestBuilder(method);
    }

    public static SimpleRequestBuilder create(Method method) {
        Args.notNull((Object)method, (String)"HTTP method");
        return new SimpleRequestBuilder(method);
    }

    public static SimpleRequestBuilder get() {
        return new SimpleRequestBuilder(Method.GET);
    }

    public static SimpleRequestBuilder get(URI uri) {
        return new SimpleRequestBuilder(Method.GET, uri);
    }

    public static SimpleRequestBuilder get(String uri) {
        return new SimpleRequestBuilder(Method.GET, uri);
    }

    public static SimpleRequestBuilder head() {
        return new SimpleRequestBuilder(Method.HEAD);
    }

    public static SimpleRequestBuilder head(URI uri) {
        return new SimpleRequestBuilder(Method.HEAD, uri);
    }

    public static SimpleRequestBuilder head(String uri) {
        return new SimpleRequestBuilder(Method.HEAD, uri);
    }

    public static SimpleRequestBuilder patch() {
        return new SimpleRequestBuilder(Method.PATCH);
    }

    public static SimpleRequestBuilder patch(URI uri) {
        return new SimpleRequestBuilder(Method.PATCH, uri);
    }

    public static SimpleRequestBuilder patch(String uri) {
        return new SimpleRequestBuilder(Method.PATCH, uri);
    }

    public static SimpleRequestBuilder post() {
        return new SimpleRequestBuilder(Method.POST);
    }

    public static SimpleRequestBuilder post(URI uri) {
        return new SimpleRequestBuilder(Method.POST, uri);
    }

    public static SimpleRequestBuilder post(String uri) {
        return new SimpleRequestBuilder(Method.POST, uri);
    }

    public static SimpleRequestBuilder put() {
        return new SimpleRequestBuilder(Method.PUT);
    }

    public static SimpleRequestBuilder put(URI uri) {
        return new SimpleRequestBuilder(Method.PUT, uri);
    }

    public static SimpleRequestBuilder put(String uri) {
        return new SimpleRequestBuilder(Method.PUT, uri);
    }

    public static SimpleRequestBuilder delete() {
        return new SimpleRequestBuilder(Method.DELETE);
    }

    public static SimpleRequestBuilder delete(URI uri) {
        return new SimpleRequestBuilder(Method.DELETE, uri);
    }

    public static SimpleRequestBuilder delete(String uri) {
        return new SimpleRequestBuilder(Method.DELETE, uri);
    }

    public static SimpleRequestBuilder trace() {
        return new SimpleRequestBuilder(Method.TRACE);
    }

    public static SimpleRequestBuilder trace(URI uri) {
        return new SimpleRequestBuilder(Method.TRACE, uri);
    }

    public static SimpleRequestBuilder trace(String uri) {
        return new SimpleRequestBuilder(Method.TRACE, uri);
    }

    public static SimpleRequestBuilder options() {
        return new SimpleRequestBuilder(Method.OPTIONS);
    }

    public static SimpleRequestBuilder options(URI uri) {
        return new SimpleRequestBuilder(Method.OPTIONS, uri);
    }

    public static SimpleRequestBuilder options(String uri) {
        return new SimpleRequestBuilder(Method.OPTIONS, uri);
    }

    public static SimpleRequestBuilder copy(SimpleHttpRequest request) {
        Args.notNull((Object)request, (String)"HTTP request");
        SimpleRequestBuilder builder = new SimpleRequestBuilder(request.getMethod());
        builder.digest(request);
        return builder;
    }

    public static SimpleRequestBuilder copy(HttpRequest request) {
        Args.notNull((Object)request, (String)"HTTP request");
        SimpleRequestBuilder builder = new SimpleRequestBuilder(request.getMethod());
        builder.digest(request);
        return builder;
    }

    protected void digest(SimpleHttpRequest request) {
        super.digest((HttpRequest)request);
        this.setBody(request.getBody());
    }

    protected void digest(HttpRequest request) {
        super.digest(request);
    }

    public SimpleRequestBuilder setVersion(ProtocolVersion version) {
        super.setVersion(version);
        return this;
    }

    public SimpleRequestBuilder setUri(URI uri) {
        super.setUri(uri);
        return this;
    }

    public SimpleRequestBuilder setUri(String uri) {
        super.setUri(uri);
        return this;
    }

    public SimpleRequestBuilder setScheme(String scheme) {
        super.setScheme(scheme);
        return this;
    }

    public SimpleRequestBuilder setAuthority(URIAuthority authority) {
        super.setAuthority(authority);
        return this;
    }

    public SimpleRequestBuilder setHttpHost(HttpHost httpHost) {
        super.setHttpHost(httpHost);
        return this;
    }

    public SimpleRequestBuilder setPath(String path) {
        super.setPath(path);
        return this;
    }

    public SimpleRequestBuilder setHeaders(Header ... headers) {
        super.setHeaders(headers);
        return this;
    }

    public SimpleRequestBuilder addHeader(Header header) {
        super.addHeader(header);
        return this;
    }

    public SimpleRequestBuilder addHeader(String name, String value) {
        super.addHeader(name, value);
        return this;
    }

    public SimpleRequestBuilder removeHeader(Header header) {
        super.removeHeader(header);
        return this;
    }

    public SimpleRequestBuilder removeHeaders(String name) {
        super.removeHeaders(name);
        return this;
    }

    public SimpleRequestBuilder setHeader(Header header) {
        super.setHeader(header);
        return this;
    }

    public SimpleRequestBuilder setHeader(String name, String value) {
        super.setHeader(name, value);
        return this;
    }

    public SimpleRequestBuilder setCharset(Charset charset) {
        super.setCharset(charset);
        return this;
    }

    public SimpleRequestBuilder addParameter(NameValuePair nvp) {
        super.addParameter(nvp);
        return this;
    }

    public SimpleRequestBuilder addParameter(String name, String value) {
        super.addParameter(name, value);
        return this;
    }

    public SimpleRequestBuilder addParameters(NameValuePair ... nvps) {
        super.addParameters(nvps);
        return this;
    }

    public SimpleRequestBuilder setAbsoluteRequestUri(boolean absoluteRequestUri) {
        super.setAbsoluteRequestUri(absoluteRequestUri);
        return this;
    }

    public SimpleBody getBody() {
        return this.body;
    }

    public SimpleRequestBuilder setBody(SimpleBody body) {
        this.body = body;
        return this;
    }

    public SimpleRequestBuilder setBody(String content, ContentType contentType) {
        this.body = SimpleBody.create(content, contentType);
        return this;
    }

    public SimpleRequestBuilder setBody(byte[] content, ContentType contentType) {
        this.body = SimpleBody.create(content, contentType);
        return this;
    }

    public RequestConfig getRequestConfig() {
        return this.requestConfig;
    }

    public SimpleRequestBuilder setRequestConfig(RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
        return this;
    }

    public SimpleHttpRequest build() {
        String path = this.getPath();
        SimpleBody bodyCopy = this.body;
        String method = this.getMethod();
        List parameters = this.getParameters();
        if (parameters != null && !parameters.isEmpty()) {
            Charset charsetCopy = this.getCharset();
            if (bodyCopy == null && (Method.POST.isSame(method) || Method.PUT.isSame(method))) {
                String content = WWWFormCodec.format((Iterable)parameters, (Charset)(charsetCopy != null ? charsetCopy : ContentType.APPLICATION_FORM_URLENCODED.getCharset()));
                bodyCopy = SimpleBody.create(content, ContentType.APPLICATION_FORM_URLENCODED);
            } else {
                try {
                    URI uri = new URIBuilder(path).setCharset(charsetCopy).addParameters(parameters).build();
                    path = uri.toASCIIString();
                }
                catch (URISyntaxException uRISyntaxException) {
                    // empty catch block
                }
            }
        }
        if (bodyCopy != null && Method.TRACE.isSame(method)) {
            throw new IllegalStateException(Method.TRACE + " requests may not include an entity");
        }
        SimpleHttpRequest result = new SimpleHttpRequest(method, this.getScheme(), this.getAuthority(), path);
        result.setVersion(this.getVersion());
        result.setHeaders(this.getHeaders());
        result.setBody(bodyCopy);
        result.setAbsoluteRequestUri(this.isAbsoluteRequestUri());
        result.setConfig(this.requestConfig);
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
        builder.append(", body=");
        builder.append(this.body);
        builder.append("]");
        return builder.toString();
    }
}

