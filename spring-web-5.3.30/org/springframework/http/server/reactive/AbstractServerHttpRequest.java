/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.http.server.reactive;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.SslInfo;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public abstract class AbstractServerHttpRequest
implements ServerHttpRequest {
    private static final Pattern QUERY_PATTERN = Pattern.compile("([^&=]+)(=?)([^&]+)?");
    private final URI uri;
    private final RequestPath path;
    private final HttpHeaders headers;
    @Nullable
    private MultiValueMap<String, String> queryParams;
    @Nullable
    private MultiValueMap<String, HttpCookie> cookies;
    @Nullable
    private SslInfo sslInfo;
    @Nullable
    private String id;
    @Nullable
    private String logPrefix;

    public AbstractServerHttpRequest(URI uri, @Nullable String contextPath, MultiValueMap<String, String> headers) {
        this.uri = uri;
        this.path = RequestPath.parse(uri, contextPath);
        this.headers = HttpHeaders.readOnlyHttpHeaders(headers);
    }

    public AbstractServerHttpRequest(URI uri, @Nullable String contextPath, HttpHeaders headers) {
        this.uri = uri;
        this.path = RequestPath.parse(uri, contextPath);
        this.headers = HttpHeaders.readOnlyHttpHeaders(headers);
    }

    @Override
    public String getId() {
        if (this.id == null) {
            this.id = this.initId();
            if (this.id == null) {
                this.id = ObjectUtils.getIdentityHexString((Object)this);
            }
        }
        return this.id;
    }

    @Nullable
    protected String initId() {
        return null;
    }

    @Override
    public URI getURI() {
        return this.uri;
    }

    @Override
    public RequestPath getPath() {
        return this.path;
    }

    @Override
    public HttpHeaders getHeaders() {
        return this.headers;
    }

    @Override
    public MultiValueMap<String, String> getQueryParams() {
        if (this.queryParams == null) {
            this.queryParams = CollectionUtils.unmodifiableMultiValueMap(this.initQueryParams());
        }
        return this.queryParams;
    }

    protected MultiValueMap<String, String> initQueryParams() {
        LinkedMultiValueMap queryParams = new LinkedMultiValueMap();
        String query = this.getURI().getRawQuery();
        if (query != null) {
            Matcher matcher = QUERY_PATTERN.matcher(query);
            while (matcher.find()) {
                String name = this.decodeQueryParam(matcher.group(1));
                String eq = matcher.group(2);
                String value = matcher.group(3);
                value = value != null ? this.decodeQueryParam(value) : (StringUtils.hasLength((String)eq) ? "" : null);
                queryParams.add((Object)name, (Object)value);
            }
        }
        return queryParams;
    }

    private String decodeQueryParam(String value) {
        try {
            return URLDecoder.decode(value, "UTF-8");
        }
        catch (UnsupportedEncodingException ex) {
            return URLDecoder.decode(value);
        }
    }

    @Override
    public MultiValueMap<String, HttpCookie> getCookies() {
        if (this.cookies == null) {
            this.cookies = CollectionUtils.unmodifiableMultiValueMap(this.initCookies());
        }
        return this.cookies;
    }

    protected abstract MultiValueMap<String, HttpCookie> initCookies();

    @Override
    @Nullable
    public SslInfo getSslInfo() {
        if (this.sslInfo == null) {
            this.sslInfo = this.initSslInfo();
        }
        return this.sslInfo;
    }

    @Nullable
    protected abstract SslInfo initSslInfo();

    public abstract <T> T getNativeRequest();

    String getLogPrefix() {
        if (this.logPrefix == null) {
            this.logPrefix = "[" + this.initLogPrefix() + "] ";
        }
        return this.logPrefix;
    }

    protected String initLogPrefix() {
        return this.getId();
    }
}

