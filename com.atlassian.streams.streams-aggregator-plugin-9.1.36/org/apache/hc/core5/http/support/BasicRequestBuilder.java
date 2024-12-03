/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.support;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.message.BasicHttpRequest;
import org.apache.hc.core5.http.support.AbstractRequestBuilder;
import org.apache.hc.core5.net.URIAuthority;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.util.Args;

public class BasicRequestBuilder
extends AbstractRequestBuilder<BasicHttpRequest> {
    BasicRequestBuilder(String method) {
        super(method);
    }

    BasicRequestBuilder(Method method) {
        super(method);
    }

    BasicRequestBuilder(String method, URI uri) {
        super(method, uri);
    }

    BasicRequestBuilder(Method method, URI uri) {
        super(method, uri);
    }

    BasicRequestBuilder(Method method, String uri) {
        super(method, uri);
    }

    BasicRequestBuilder(String method, String uri) {
        super(method, uri);
    }

    public static BasicRequestBuilder create(String method) {
        Args.notBlank(method, "HTTP method");
        return new BasicRequestBuilder(method);
    }

    public static BasicRequestBuilder get() {
        return new BasicRequestBuilder(Method.GET);
    }

    public static BasicRequestBuilder get(URI uri) {
        return new BasicRequestBuilder(Method.GET, uri);
    }

    public static BasicRequestBuilder get(String uri) {
        return new BasicRequestBuilder(Method.GET, uri);
    }

    public static BasicRequestBuilder head() {
        return new BasicRequestBuilder(Method.HEAD);
    }

    public static BasicRequestBuilder head(URI uri) {
        return new BasicRequestBuilder(Method.HEAD, uri);
    }

    public static BasicRequestBuilder head(String uri) {
        return new BasicRequestBuilder(Method.HEAD, uri);
    }

    public static BasicRequestBuilder patch() {
        return new BasicRequestBuilder(Method.PATCH);
    }

    public static BasicRequestBuilder patch(URI uri) {
        return new BasicRequestBuilder(Method.PATCH, uri);
    }

    public static BasicRequestBuilder patch(String uri) {
        return new BasicRequestBuilder(Method.PATCH, uri);
    }

    public static BasicRequestBuilder post() {
        return new BasicRequestBuilder(Method.POST);
    }

    public static BasicRequestBuilder post(URI uri) {
        return new BasicRequestBuilder(Method.POST, uri);
    }

    public static BasicRequestBuilder post(String uri) {
        return new BasicRequestBuilder(Method.POST, uri);
    }

    public static BasicRequestBuilder put() {
        return new BasicRequestBuilder(Method.PUT);
    }

    public static BasicRequestBuilder put(URI uri) {
        return new BasicRequestBuilder(Method.PUT, uri);
    }

    public static BasicRequestBuilder put(String uri) {
        return new BasicRequestBuilder(Method.PUT, uri);
    }

    public static BasicRequestBuilder delete() {
        return new BasicRequestBuilder(Method.DELETE);
    }

    public static BasicRequestBuilder delete(URI uri) {
        return new BasicRequestBuilder(Method.DELETE, uri);
    }

    public static BasicRequestBuilder delete(String uri) {
        return new BasicRequestBuilder(Method.DELETE, uri);
    }

    public static BasicRequestBuilder trace() {
        return new BasicRequestBuilder(Method.TRACE);
    }

    public static BasicRequestBuilder trace(URI uri) {
        return new BasicRequestBuilder(Method.TRACE, uri);
    }

    public static BasicRequestBuilder trace(String uri) {
        return new BasicRequestBuilder(Method.TRACE, uri);
    }

    public static BasicRequestBuilder options() {
        return new BasicRequestBuilder(Method.OPTIONS);
    }

    public static BasicRequestBuilder options(URI uri) {
        return new BasicRequestBuilder(Method.OPTIONS, uri);
    }

    public static BasicRequestBuilder options(String uri) {
        return new BasicRequestBuilder(Method.OPTIONS, uri);
    }

    public static BasicRequestBuilder copy(HttpRequest request) {
        Args.notNull(request, "HTTP request");
        BasicRequestBuilder builder = new BasicRequestBuilder(request.getMethod());
        builder.digest(request);
        return builder;
    }

    @Override
    public BasicRequestBuilder setVersion(ProtocolVersion version) {
        super.setVersion(version);
        return this;
    }

    public BasicRequestBuilder setUri(URI uri) {
        super.setUri(uri);
        return this;
    }

    public BasicRequestBuilder setUri(String uri) {
        super.setUri(uri);
        return this;
    }

    public BasicRequestBuilder setScheme(String scheme) {
        super.setScheme(scheme);
        return this;
    }

    public BasicRequestBuilder setAuthority(URIAuthority authority) {
        super.setAuthority(authority);
        return this;
    }

    public BasicRequestBuilder setHttpHost(HttpHost httpHost) {
        super.setHttpHost(httpHost);
        return this;
    }

    public BasicRequestBuilder setPath(String path) {
        super.setPath(path);
        return this;
    }

    @Override
    public BasicRequestBuilder setHeaders(Header ... headers) {
        super.setHeaders(headers);
        return this;
    }

    @Override
    public BasicRequestBuilder addHeader(Header header) {
        super.addHeader(header);
        return this;
    }

    @Override
    public BasicRequestBuilder addHeader(String name, String value) {
        super.addHeader(name, value);
        return this;
    }

    @Override
    public BasicRequestBuilder removeHeader(Header header) {
        super.removeHeader(header);
        return this;
    }

    @Override
    public BasicRequestBuilder removeHeaders(String name) {
        super.removeHeaders(name);
        return this;
    }

    @Override
    public BasicRequestBuilder setHeader(Header header) {
        super.setHeader(header);
        return this;
    }

    @Override
    public BasicRequestBuilder setHeader(String name, String value) {
        super.setHeader(name, value);
        return this;
    }

    public BasicRequestBuilder setCharset(Charset charset) {
        super.setCharset(charset);
        return this;
    }

    public BasicRequestBuilder addParameter(NameValuePair nvp) {
        super.addParameter(nvp);
        return this;
    }

    public BasicRequestBuilder addParameter(String name, String value) {
        super.addParameter(name, value);
        return this;
    }

    public BasicRequestBuilder addParameters(NameValuePair ... nvps) {
        super.addParameters(nvps);
        return this;
    }

    public BasicRequestBuilder setAbsoluteRequestUri(boolean absoluteRequestUri) {
        super.setAbsoluteRequestUri(absoluteRequestUri);
        return this;
    }

    @Override
    public BasicHttpRequest build() {
        String path = this.getPath();
        List<NameValuePair> parameters = this.getParameters();
        if (parameters != null && !parameters.isEmpty()) {
            try {
                URI uri = new URIBuilder(path).setCharset(this.getCharset()).addParameters(parameters).build();
                path = uri.toASCIIString();
            }
            catch (URISyntaxException ex) {
                // empty catch block
            }
        }
        BasicHttpRequest result = new BasicHttpRequest(this.getMethod(), this.getScheme(), this.getAuthority(), path);
        result.setVersion(this.getVersion());
        result.setHeaders(this.getHeaders());
        result.setAbsoluteRequestUri(this.isAbsoluteRequestUri());
        return result;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("BasicRequestBuilder [method=");
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
        builder.append("]");
        return builder.toString();
    }
}

