/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.web.servlet.function;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.Conventions;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.function.AbstractServerResponse;
import org.springframework.web.servlet.function.RenderingResponse;
import org.springframework.web.servlet.function.ServerResponse;

final class DefaultRenderingResponseBuilder
implements RenderingResponse.Builder {
    private final String name;
    private int status = HttpStatus.OK.value();
    private final HttpHeaders headers = new HttpHeaders();
    private final MultiValueMap<String, Cookie> cookies = new LinkedMultiValueMap<String, Cookie>();
    private final Map<String, Object> model = new LinkedHashMap<String, Object>();

    public DefaultRenderingResponseBuilder(RenderingResponse other) {
        Assert.notNull((Object)other, "RenderingResponse must not be null");
        this.name = other.name();
        this.status = other instanceof DefaultRenderingResponse ? ((DefaultRenderingResponse)other).statusCode : other.statusCode().value();
        this.headers.putAll(other.headers());
        this.model.putAll(other.model());
    }

    public DefaultRenderingResponseBuilder(String name) {
        Assert.notNull((Object)name, "Name must not be null");
        this.name = name;
    }

    @Override
    public RenderingResponse.Builder status(HttpStatus status) {
        Assert.notNull((Object)status, "HttpStatus must not be null");
        this.status = status.value();
        return this;
    }

    @Override
    public RenderingResponse.Builder status(int status) {
        this.status = status;
        return this;
    }

    @Override
    public RenderingResponse.Builder cookie(Cookie cookie) {
        Assert.notNull((Object)cookie, "Cookie must not be null");
        this.cookies.add(cookie.getName(), cookie);
        return this;
    }

    @Override
    public RenderingResponse.Builder cookies(Consumer<MultiValueMap<String, Cookie>> cookiesConsumer) {
        cookiesConsumer.accept(this.cookies);
        return this;
    }

    @Override
    public RenderingResponse.Builder modelAttribute(Object attribute) {
        Assert.notNull(attribute, "Attribute must not be null");
        if (attribute instanceof Collection && ((Collection)attribute).isEmpty()) {
            return this;
        }
        return this.modelAttribute(Conventions.getVariableName(attribute), attribute);
    }

    @Override
    public RenderingResponse.Builder modelAttribute(String name, @Nullable Object value) {
        Assert.notNull((Object)name, "Name must not be null");
        this.model.put(name, value);
        return this;
    }

    @Override
    public RenderingResponse.Builder modelAttributes(Object ... attributes) {
        this.modelAttributes(Arrays.asList(attributes));
        return this;
    }

    @Override
    public RenderingResponse.Builder modelAttributes(Collection<?> attributes) {
        attributes.forEach(this::modelAttribute);
        return this;
    }

    @Override
    public RenderingResponse.Builder modelAttributes(Map<String, ?> attributes) {
        this.model.putAll(attributes);
        return this;
    }

    @Override
    public RenderingResponse.Builder header(String headerName, String ... headerValues) {
        for (String headerValue : headerValues) {
            this.headers.add(headerName, headerValue);
        }
        return this;
    }

    @Override
    public RenderingResponse.Builder headers(Consumer<HttpHeaders> headersConsumer) {
        headersConsumer.accept(this.headers);
        return this;
    }

    @Override
    public RenderingResponse build() {
        return new DefaultRenderingResponse(this.status, this.headers, this.cookies, this.name, this.model);
    }

    private static final class DefaultRenderingResponse
    extends AbstractServerResponse
    implements RenderingResponse {
        private final String name;
        private final Map<String, Object> model;

        public DefaultRenderingResponse(int statusCode, HttpHeaders headers, MultiValueMap<String, Cookie> cookies, String name, Map<String, Object> model) {
            super(statusCode, headers, cookies);
            this.name = name;
            this.model = Collections.unmodifiableMap(new LinkedHashMap<String, Object>(model));
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public Map<String, Object> model() {
            return this.model;
        }

        @Override
        protected ModelAndView writeToInternal(HttpServletRequest request, HttpServletResponse response, ServerResponse.Context context) {
            HttpStatus status = HttpStatus.resolve(this.statusCode);
            ModelAndView mav = status != null ? new ModelAndView(this.name, status) : new ModelAndView(this.name);
            mav.addAllObjects(this.model);
            return mav;
        }
    }
}

