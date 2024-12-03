/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.server.reactive;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.SslInfo;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

public abstract class AbstractServerHttpRequest
implements ServerHttpRequest {
    private static final Pattern QUERY_PATTERN = Pattern.compile("([^&=]+)(=?)([^&]+)?");
    private static final Log logger = LogFactory.getLog(ServerHttpRequest.class);
    private final URI uri;
    private final RequestPath path;
    private final HttpHeaders headers;
    @Nullable
    private MultiValueMap<String, String> queryParams;
    @Nullable
    private MultiValueMap<String, HttpCookie> cookies;
    @Nullable
    private SslInfo sslInfo;

    public AbstractServerHttpRequest(URI uri, @Nullable String contextPath, HttpHeaders headers) {
        this.uri = uri;
        this.path = RequestPath.parse(uri, contextPath);
        this.headers = HttpHeaders.readOnlyHttpHeaders(headers);
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
        LinkedMultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        String query = this.getURI().getRawQuery();
        if (query != null) {
            Matcher matcher = QUERY_PATTERN.matcher(query);
            while (matcher.find()) {
                String name = this.decodeQueryParam(matcher.group(1));
                String eq = matcher.group(2);
                String value = matcher.group(3);
                value = value != null ? this.decodeQueryParam(value) : (StringUtils.hasLength(eq) ? "" : null);
                queryParams.add(name, value);
            }
        }
        return queryParams;
    }

    private String decodeQueryParam(String value) {
        try {
            return URLDecoder.decode(value, "UTF-8");
        }
        catch (UnsupportedEncodingException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("Could not decode query param [" + value + "] as 'UTF-8'. Falling back on default encoding; exception message: " + ex.getMessage());
            }
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
}

