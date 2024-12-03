/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.support;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.http.support.AbstractMessageBuilder;
import org.apache.hc.core5.net.URIAuthority;
import org.apache.hc.core5.util.TextUtils;

public abstract class AbstractRequestBuilder<T>
extends AbstractMessageBuilder<T> {
    private final String method;
    private String scheme;
    private URIAuthority authority;
    private String path;
    private Charset charset;
    private List<NameValuePair> parameters;
    private boolean absoluteRequestUri;

    protected AbstractRequestBuilder(String method) {
        this.method = method;
    }

    protected AbstractRequestBuilder(Method method) {
        this(method.name());
    }

    protected AbstractRequestBuilder(String method, URI uri) {
        this.method = method;
        this.setUri(uri);
    }

    protected AbstractRequestBuilder(Method method, URI uri) {
        this(method.name(), uri);
    }

    protected AbstractRequestBuilder(Method method, String uri) {
        this(method.name(), uri != null ? URI.create(uri) : null);
    }

    protected AbstractRequestBuilder(String method, String uri) {
        this(method, uri != null ? URI.create(uri) : null);
    }

    protected void digest(HttpRequest request) {
        if (request == null) {
            return;
        }
        this.setScheme(request.getScheme());
        this.setAuthority(request.getAuthority());
        this.setPath(request.getPath());
        this.parameters = null;
        super.digest(request);
    }

    public String getMethod() {
        return this.method;
    }

    @Override
    public AbstractRequestBuilder<T> setVersion(ProtocolVersion version) {
        super.setVersion(version);
        return this;
    }

    public String getScheme() {
        return this.scheme;
    }

    public AbstractRequestBuilder<T> setScheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    public URIAuthority getAuthority() {
        return this.authority;
    }

    public AbstractRequestBuilder<T> setAuthority(URIAuthority authority) {
        this.authority = authority;
        return this;
    }

    public AbstractRequestBuilder<T> setHttpHost(HttpHost httpHost) {
        if (httpHost == null) {
            return this;
        }
        this.authority = new URIAuthority(httpHost);
        this.scheme = httpHost.getSchemeName();
        return this;
    }

    public String getPath() {
        return this.path;
    }

    public AbstractRequestBuilder<T> setPath(String path) {
        this.path = path;
        return this;
    }

    public URI getUri() {
        StringBuilder buf = new StringBuilder();
        if (this.authority != null) {
            buf.append(this.scheme != null ? this.scheme : URIScheme.HTTP.id).append("://");
            buf.append(this.authority.getHostName());
            if (this.authority.getPort() >= 0) {
                buf.append(":").append(this.authority.getPort());
            }
        }
        if (this.path == null) {
            buf.append("/");
        } else {
            if (buf.length() > 0 && !this.path.startsWith("/")) {
                buf.append("/");
            }
            buf.append(this.path);
        }
        return URI.create(buf.toString());
    }

    public AbstractRequestBuilder<T> setUri(URI uri) {
        if (uri == null) {
            this.scheme = null;
            this.authority = null;
            this.path = null;
        } else {
            this.scheme = uri.getScheme();
            if (uri.getHost() != null) {
                this.authority = new URIAuthority(uri.getRawUserInfo(), uri.getHost(), uri.getPort());
            } else if (uri.getRawAuthority() != null) {
                try {
                    this.authority = URIAuthority.create(uri.getRawAuthority());
                }
                catch (URISyntaxException ignore) {
                    this.authority = null;
                }
            } else {
                this.authority = null;
            }
            StringBuilder buf = new StringBuilder();
            String rawPath = uri.getRawPath();
            if (!TextUtils.isBlank(rawPath)) {
                buf.append(rawPath);
            } else {
                buf.append("/");
            }
            String query = uri.getRawQuery();
            if (query != null) {
                buf.append('?').append(query);
            }
            this.path = buf.toString();
        }
        return this;
    }

    public AbstractRequestBuilder<T> setUri(String uri) {
        this.setUri(uri != null ? URI.create(uri) : null);
        return this;
    }

    @Override
    public AbstractRequestBuilder<T> setHeaders(Header ... headers) {
        super.setHeaders(headers);
        return this;
    }

    @Override
    public AbstractRequestBuilder<T> addHeader(Header header) {
        super.addHeader(header);
        return this;
    }

    @Override
    public AbstractRequestBuilder<T> addHeader(String name, String value) {
        super.addHeader(name, value);
        return this;
    }

    @Override
    public AbstractRequestBuilder<T> removeHeader(Header header) {
        super.removeHeader(header);
        return this;
    }

    @Override
    public AbstractRequestBuilder<T> removeHeaders(String name) {
        super.removeHeaders(name);
        return this;
    }

    @Override
    public AbstractRequestBuilder<T> setHeader(Header header) {
        super.setHeader(header);
        return this;
    }

    @Override
    public AbstractRequestBuilder<T> setHeader(String name, String value) {
        super.setHeader(name, value);
        return this;
    }

    public Charset getCharset() {
        return this.charset;
    }

    public AbstractRequestBuilder<T> setCharset(Charset charset) {
        this.charset = charset;
        return this;
    }

    public List<NameValuePair> getParameters() {
        return this.parameters != null ? new ArrayList<NameValuePair>(this.parameters) : null;
    }

    public AbstractRequestBuilder<T> addParameter(NameValuePair nvp) {
        if (nvp == null) {
            return this;
        }
        if (this.parameters == null) {
            this.parameters = new LinkedList<NameValuePair>();
        }
        this.parameters.add(nvp);
        return this;
    }

    public AbstractRequestBuilder<T> addParameter(String name, String value) {
        return this.addParameter(new BasicNameValuePair(name, value));
    }

    public AbstractRequestBuilder<T> addParameters(NameValuePair ... nvps) {
        for (NameValuePair nvp : nvps) {
            this.addParameter(nvp);
        }
        return this;
    }

    public boolean isAbsoluteRequestUri() {
        return this.absoluteRequestUri;
    }

    public AbstractRequestBuilder<T> setAbsoluteRequestUri(boolean absoluteRequestUri) {
        this.absoluteRequestUri = absoluteRequestUri;
        return this;
    }
}

