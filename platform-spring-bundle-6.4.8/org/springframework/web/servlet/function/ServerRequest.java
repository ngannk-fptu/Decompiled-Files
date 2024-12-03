/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 *  javax.servlet.http.Part
 */
package org.springframework.web.servlet.function;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Consumer;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRange;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.RequestPath;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.function.DefaultServerRequest;
import org.springframework.web.servlet.function.DefaultServerRequestBuilder;
import org.springframework.web.servlet.function.ServerResponse;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.UriBuilder;

public interface ServerRequest {
    @Nullable
    default public HttpMethod method() {
        return HttpMethod.resolve(this.methodName());
    }

    public String methodName();

    public URI uri();

    public UriBuilder uriBuilder();

    default public String path() {
        return this.requestPath().pathWithinApplication().value();
    }

    @Deprecated
    default public PathContainer pathContainer() {
        return this.requestPath();
    }

    default public RequestPath requestPath() {
        return ServletRequestPathUtils.getParsedRequestPath((ServletRequest)this.servletRequest());
    }

    public Headers headers();

    public MultiValueMap<String, Cookie> cookies();

    public Optional<InetSocketAddress> remoteAddress();

    public List<HttpMessageConverter<?>> messageConverters();

    public <T> T body(Class<T> var1) throws ServletException, IOException;

    public <T> T body(ParameterizedTypeReference<T> var1) throws ServletException, IOException;

    default public Optional<Object> attribute(String name) {
        Map<String, Object> attributes = this.attributes();
        if (attributes.containsKey(name)) {
            return Optional.of(attributes.get(name));
        }
        return Optional.empty();
    }

    public Map<String, Object> attributes();

    default public Optional<String> param(String name) {
        List paramValues = (List)this.params().get(name);
        if (CollectionUtils.isEmpty(paramValues)) {
            return Optional.empty();
        }
        String value = (String)paramValues.get(0);
        if (value == null) {
            value = "";
        }
        return Optional.of(value);
    }

    public MultiValueMap<String, String> params();

    public MultiValueMap<String, Part> multipartData() throws IOException, ServletException;

    default public String pathVariable(String name) {
        Map<String, String> pathVariables = this.pathVariables();
        if (pathVariables.containsKey(name)) {
            return this.pathVariables().get(name);
        }
        throw new IllegalArgumentException("No path variable with name \"" + name + "\" available");
    }

    public Map<String, String> pathVariables();

    public HttpSession session();

    public Optional<Principal> principal();

    public HttpServletRequest servletRequest();

    default public Optional<ServerResponse> checkNotModified(Instant lastModified) {
        Assert.notNull((Object)lastModified, "LastModified must not be null");
        return DefaultServerRequest.checkNotModified(this.servletRequest(), lastModified, null);
    }

    default public Optional<ServerResponse> checkNotModified(String etag) {
        Assert.notNull((Object)etag, "Etag must not be null");
        return DefaultServerRequest.checkNotModified(this.servletRequest(), null, etag);
    }

    default public Optional<ServerResponse> checkNotModified(Instant lastModified, String etag) {
        Assert.notNull((Object)lastModified, "LastModified must not be null");
        Assert.notNull((Object)etag, "Etag must not be null");
        return DefaultServerRequest.checkNotModified(this.servletRequest(), lastModified, etag);
    }

    public static ServerRequest create(HttpServletRequest servletRequest, List<HttpMessageConverter<?>> messageReaders) {
        return new DefaultServerRequest(servletRequest, messageReaders);
    }

    public static Builder from(ServerRequest other) {
        return new DefaultServerRequestBuilder(other);
    }

    public static interface Builder {
        public Builder method(HttpMethod var1);

        public Builder uri(URI var1);

        public Builder header(String var1, String ... var2);

        public Builder headers(Consumer<HttpHeaders> var1);

        public Builder cookie(String var1, String ... var2);

        public Builder cookies(Consumer<MultiValueMap<String, Cookie>> var1);

        public Builder body(byte[] var1);

        public Builder body(String var1);

        public Builder attribute(String var1, Object var2);

        public Builder attributes(Consumer<Map<String, Object>> var1);

        public Builder param(String var1, String ... var2);

        public Builder params(Consumer<MultiValueMap<String, String>> var1);

        public Builder remoteAddress(InetSocketAddress var1);

        public ServerRequest build();
    }

    public static interface Headers {
        public List<MediaType> accept();

        public List<Charset> acceptCharset();

        public List<Locale.LanguageRange> acceptLanguage();

        public OptionalLong contentLength();

        public Optional<MediaType> contentType();

        @Nullable
        public InetSocketAddress host();

        public List<HttpRange> range();

        public List<String> header(String var1);

        @Nullable
        default public String firstHeader(String headerName) {
            List<String> list = this.header(headerName);
            return list.isEmpty() ? null : list.get(0);
        }

        public HttpHeaders asHttpHeaders();
    }
}

