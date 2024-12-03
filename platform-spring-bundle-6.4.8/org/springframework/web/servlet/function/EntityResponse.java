/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.Cookie
 */
package org.springframework.web.servlet.function;

import java.net.URI;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.function.Consumer;
import javax.servlet.http.Cookie;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.function.DefaultEntityResponseBuilder;
import org.springframework.web.servlet.function.ServerResponse;

public interface EntityResponse<T>
extends ServerResponse {
    public T entity();

    public static <T> Builder<T> fromObject(T t) {
        return DefaultEntityResponseBuilder.fromObject(t);
    }

    public static <T> Builder<T> fromObject(T t, ParameterizedTypeReference<T> entityType) {
        return DefaultEntityResponseBuilder.fromObject(t, entityType);
    }

    public static interface Builder<T> {
        public Builder<T> header(String var1, String ... var2);

        public Builder<T> headers(Consumer<HttpHeaders> var1);

        public Builder<T> status(HttpStatus var1);

        public Builder<T> status(int var1);

        public Builder<T> cookie(Cookie var1);

        public Builder<T> cookies(Consumer<MultiValueMap<String, Cookie>> var1);

        public Builder<T> allow(HttpMethod ... var1);

        public Builder<T> allow(Set<HttpMethod> var1);

        public Builder<T> eTag(String var1);

        public Builder<T> lastModified(ZonedDateTime var1);

        public Builder<T> lastModified(Instant var1);

        public Builder<T> location(URI var1);

        public Builder<T> cacheControl(CacheControl var1);

        public Builder<T> varyBy(String ... var1);

        public Builder<T> contentLength(long var1);

        public Builder<T> contentType(MediaType var1);

        public EntityResponse<T> build();
    }
}

