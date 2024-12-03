/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.Cookie
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.HttpStatus
 *  org.springframework.lang.Nullable
 *  org.springframework.util.MultiValueMap
 */
package org.springframework.web.servlet.function;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import javax.servlet.http.Cookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.function.DefaultRenderingResponseBuilder;
import org.springframework.web.servlet.function.ServerResponse;

public interface RenderingResponse
extends ServerResponse {
    public String name();

    public Map<String, Object> model();

    public static Builder from(RenderingResponse other) {
        return new DefaultRenderingResponseBuilder(other);
    }

    public static Builder create(String name) {
        return new DefaultRenderingResponseBuilder(name);
    }

    public static interface Builder {
        public Builder modelAttribute(Object var1);

        public Builder modelAttribute(String var1, @Nullable Object var2);

        public Builder modelAttributes(Object ... var1);

        public Builder modelAttributes(Collection<?> var1);

        public Builder modelAttributes(Map<String, ?> var1);

        public Builder header(String var1, String ... var2);

        public Builder headers(Consumer<HttpHeaders> var1);

        public Builder status(HttpStatus var1);

        public Builder status(int var1);

        public Builder cookie(Cookie var1);

        public Builder cookies(Consumer<MultiValueMap<String, Cookie>> var1);

        public RenderingResponse build();
    }
}

