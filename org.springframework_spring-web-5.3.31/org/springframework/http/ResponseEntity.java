/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.MultiValueMap
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.http;

import java.net.URI;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

public class ResponseEntity<T>
extends HttpEntity<T> {
    private final Object status;

    public ResponseEntity(HttpStatus status) {
        this(null, null, status);
    }

    public ResponseEntity(@Nullable T body, HttpStatus status) {
        this(body, null, status);
    }

    public ResponseEntity(MultiValueMap<String, String> headers, HttpStatus status) {
        this(null, headers, status);
    }

    public ResponseEntity(@Nullable T body, @Nullable MultiValueMap<String, String> headers, HttpStatus status) {
        this(body, headers, (Object)status);
    }

    public ResponseEntity(@Nullable T body, @Nullable MultiValueMap<String, String> headers, int rawStatus) {
        this(body, headers, (Object)rawStatus);
    }

    private ResponseEntity(@Nullable T body, @Nullable MultiValueMap<String, String> headers, Object status) {
        super(body, headers);
        Assert.notNull((Object)status, (String)"HttpStatus must not be null");
        this.status = status;
    }

    public HttpStatus getStatusCode() {
        if (this.status instanceof HttpStatus) {
            return (HttpStatus)((Object)this.status);
        }
        return HttpStatus.valueOf((Integer)this.status);
    }

    public int getStatusCodeValue() {
        if (this.status instanceof HttpStatus) {
            return ((HttpStatus)((Object)this.status)).value();
        }
        return (Integer)this.status;
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!super.equals(other)) {
            return false;
        }
        ResponseEntity otherEntity = (ResponseEntity)other;
        return ObjectUtils.nullSafeEquals((Object)this.status, (Object)otherEntity.status);
    }

    @Override
    public int hashCode() {
        return 29 * super.hashCode() + ObjectUtils.nullSafeHashCode((Object)this.status);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("<");
        builder.append(this.status);
        if (this.status instanceof HttpStatus) {
            builder.append(' ');
            builder.append(((HttpStatus)((Object)this.status)).getReasonPhrase());
        }
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

    public static BodyBuilder status(HttpStatus status) {
        Assert.notNull((Object)((Object)status), (String)"HttpStatus must not be null");
        return new DefaultBuilder((Object)status);
    }

    public static BodyBuilder status(int status) {
        return new DefaultBuilder(status);
    }

    public static BodyBuilder ok() {
        return ResponseEntity.status(HttpStatus.OK);
    }

    public static <T> ResponseEntity<T> ok(@Nullable T body) {
        return ResponseEntity.ok().body(body);
    }

    public static <T> ResponseEntity<T> of(Optional<T> body) {
        Assert.notNull(body, (String)"Body must not be null");
        return body.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    public static BodyBuilder created(URI location) {
        return (BodyBuilder)ResponseEntity.status(HttpStatus.CREATED).location(location);
    }

    public static BodyBuilder accepted() {
        return ResponseEntity.status(HttpStatus.ACCEPTED);
    }

    public static HeadersBuilder<?> noContent() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT);
    }

    public static BodyBuilder badRequest() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST);
    }

    public static HeadersBuilder<?> notFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND);
    }

    public static BodyBuilder unprocessableEntity() {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    public static BodyBuilder internalServerError() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static class DefaultBuilder
    implements BodyBuilder {
        private final Object statusCode;
        private final HttpHeaders headers = new HttpHeaders();

        public DefaultBuilder(Object statusCode) {
            this.statusCode = statusCode;
        }

        @Override
        public BodyBuilder header(String headerName, String ... headerValues) {
            for (String headerValue : headerValues) {
                this.headers.add(headerName, headerValue);
            }
            return this;
        }

        @Override
        public BodyBuilder headers(@Nullable HttpHeaders headers) {
            if (headers != null) {
                this.headers.putAll((Map<? extends String, ? extends List<String>>)((Object)headers));
            }
            return this;
        }

        @Override
        public BodyBuilder headers(Consumer<HttpHeaders> headersConsumer) {
            headersConsumer.accept(this.headers);
            return this;
        }

        @Override
        public BodyBuilder allow(HttpMethod ... allowedMethods) {
            this.headers.setAllow(new LinkedHashSet<HttpMethod>(Arrays.asList(allowedMethods)));
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
        public BodyBuilder eTag(String etag) {
            if (!etag.startsWith("\"") && !etag.startsWith("W/\"")) {
                etag = "\"" + etag;
            }
            if (!etag.endsWith("\"")) {
                etag = etag + "\"";
            }
            this.headers.setETag(etag);
            return this;
        }

        @Override
        public BodyBuilder lastModified(ZonedDateTime date) {
            this.headers.setLastModified(date);
            return this;
        }

        @Override
        public BodyBuilder lastModified(Instant date) {
            this.headers.setLastModified(date);
            return this;
        }

        @Override
        public BodyBuilder lastModified(long date) {
            this.headers.setLastModified(date);
            return this;
        }

        @Override
        public BodyBuilder location(URI location) {
            this.headers.setLocation(location);
            return this;
        }

        @Override
        public BodyBuilder cacheControl(CacheControl cacheControl) {
            this.headers.setCacheControl(cacheControl);
            return this;
        }

        @Override
        public BodyBuilder varyBy(String ... requestHeaders) {
            this.headers.setVary(Arrays.asList(requestHeaders));
            return this;
        }

        @Override
        public <T> ResponseEntity<T> build() {
            return this.body(null);
        }

        @Override
        public <T> ResponseEntity<T> body(@Nullable T body) {
            return new ResponseEntity(body, this.headers, this.statusCode);
        }
    }

    public static interface BodyBuilder
    extends HeadersBuilder<BodyBuilder> {
        public BodyBuilder contentLength(long var1);

        public BodyBuilder contentType(MediaType var1);

        public <T> ResponseEntity<T> body(@Nullable T var1);
    }

    public static interface HeadersBuilder<B extends HeadersBuilder<B>> {
        public B header(String var1, String ... var2);

        public B headers(@Nullable HttpHeaders var1);

        public B headers(Consumer<HttpHeaders> var1);

        public B allow(HttpMethod ... var1);

        public B eTag(String var1);

        public B lastModified(ZonedDateTime var1);

        public B lastModified(Instant var1);

        public B lastModified(long var1);

        public B location(URI var1);

        public B cacheControl(CacheControl var1);

        public B varyBy(String ... var1);

        public <T> ResponseEntity<T> build();
    }
}

