/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http;

import java.lang.reflect.Type;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Arrays;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

public class RequestEntity<T>
extends HttpEntity<T> {
    @Nullable
    private final HttpMethod method;
    private final URI url;
    @Nullable
    private final Type type;

    public RequestEntity(HttpMethod method, URI url) {
        this(null, null, method, url, null);
    }

    public RequestEntity(@Nullable T body, HttpMethod method, URI url) {
        this(body, null, method, url, null);
    }

    public RequestEntity(@Nullable T body, HttpMethod method, URI url, Type type) {
        this(body, null, method, url, type);
    }

    public RequestEntity(MultiValueMap<String, String> headers, HttpMethod method, URI url) {
        this(null, headers, method, url, null);
    }

    public RequestEntity(@Nullable T body, @Nullable MultiValueMap<String, String> headers, @Nullable HttpMethod method, URI url) {
        this(body, headers, method, url, null);
    }

    public RequestEntity(@Nullable T body, @Nullable MultiValueMap<String, String> headers, @Nullable HttpMethod method, URI url, @Nullable Type type) {
        super(body, headers);
        this.method = method;
        this.url = url;
        this.type = type;
    }

    @Nullable
    public HttpMethod getMethod() {
        return this.method;
    }

    public URI getUrl() {
        return this.url;
    }

    @Nullable
    public Type getType() {
        Object body;
        if (this.type == null && (body = this.getBody()) != null) {
            return body.getClass();
        }
        return this.type;
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!super.equals(other)) {
            return false;
        }
        RequestEntity otherEntity = (RequestEntity)other;
        return ObjectUtils.nullSafeEquals((Object)this.getMethod(), (Object)otherEntity.getMethod()) && ObjectUtils.nullSafeEquals(this.getUrl(), otherEntity.getUrl());
    }

    @Override
    public int hashCode() {
        int hashCode = super.hashCode();
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode((Object)this.method);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.url);
        return hashCode;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("<");
        builder.append((Object)this.getMethod());
        builder.append(' ');
        builder.append(this.getUrl());
        builder.append(',');
        Object body = this.getBody();
        HttpHeaders headers = this.getHeaders();
        if (body != null) {
            builder.append(body);
            builder.append(',');
        }
        builder.append(headers);
        builder.append('>');
        return builder.toString();
    }

    public static BodyBuilder method(HttpMethod method, URI url) {
        return new DefaultBodyBuilder(method, url);
    }

    public static HeadersBuilder<?> get(URI url) {
        return RequestEntity.method(HttpMethod.GET, url);
    }

    public static HeadersBuilder<?> head(URI url) {
        return RequestEntity.method(HttpMethod.HEAD, url);
    }

    public static BodyBuilder post(URI url) {
        return RequestEntity.method(HttpMethod.POST, url);
    }

    public static BodyBuilder put(URI url) {
        return RequestEntity.method(HttpMethod.PUT, url);
    }

    public static BodyBuilder patch(URI url) {
        return RequestEntity.method(HttpMethod.PATCH, url);
    }

    public static HeadersBuilder<?> delete(URI url) {
        return RequestEntity.method(HttpMethod.DELETE, url);
    }

    public static HeadersBuilder<?> options(URI url) {
        return RequestEntity.method(HttpMethod.OPTIONS, url);
    }

    private static class DefaultBodyBuilder
    implements BodyBuilder {
        private final HttpMethod method;
        private final URI url;
        private final HttpHeaders headers = new HttpHeaders();

        public DefaultBodyBuilder(HttpMethod method, URI url) {
            this.method = method;
            this.url = url;
        }

        @Override
        public BodyBuilder header(String headerName, String ... headerValues) {
            for (String headerValue : headerValues) {
                this.headers.add(headerName, headerValue);
            }
            return this;
        }

        @Override
        public BodyBuilder accept(MediaType ... acceptableMediaTypes) {
            this.headers.setAccept(Arrays.asList(acceptableMediaTypes));
            return this;
        }

        @Override
        public BodyBuilder acceptCharset(Charset ... acceptableCharsets) {
            this.headers.setAcceptCharset(Arrays.asList(acceptableCharsets));
            return this;
        }

        @Override
        public BodyBuilder contentLength(long contentLength) {
            this.headers.setContentLength(contentLength);
            return this;
        }

        @Override
        public BodyBuilder contentType(MediaType contentType) {
            this.headers.setContentType(contentType);
            return this;
        }

        @Override
        public BodyBuilder ifModifiedSince(long ifModifiedSince) {
            this.headers.setIfModifiedSince(ifModifiedSince);
            return this;
        }

        @Override
        public BodyBuilder ifNoneMatch(String ... ifNoneMatches) {
            this.headers.setIfNoneMatch(Arrays.asList(ifNoneMatches));
            return this;
        }

        @Override
        public RequestEntity<Void> build() {
            return new RequestEntity<Void>(this.headers, this.method, this.url);
        }

        @Override
        public <T> RequestEntity<T> body(T body) {
            return new RequestEntity<T>(body, this.headers, this.method, this.url);
        }

        @Override
        public <T> RequestEntity<T> body(T body, Type type) {
            return new RequestEntity<T>(body, this.headers, this.method, this.url, type);
        }
    }

    public static interface BodyBuilder
    extends HeadersBuilder<BodyBuilder> {
        public BodyBuilder contentLength(long var1);

        public BodyBuilder contentType(MediaType var1);

        public <T> RequestEntity<T> body(T var1);

        public <T> RequestEntity<T> body(T var1, Type var2);
    }

    public static interface HeadersBuilder<B extends HeadersBuilder<B>> {
        public B header(String var1, String ... var2);

        public B accept(MediaType ... var1);

        public B acceptCharset(Charset ... var1);

        public B ifModifiedSince(long var1);

        public B ifNoneMatch(String ... var1);

        public RequestEntity<Void> build();
    }
}

