/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.core.ParameterizedTypeReference
 *  org.springframework.http.CacheControl
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.HttpMethod
 *  org.springframework.http.HttpStatus
 *  org.springframework.http.MediaType
 *  org.springframework.util.Assert
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 */
package org.springframework.web.servlet.function;

import java.net.URI;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.function.AbstractServerResponse;
import org.springframework.web.servlet.function.DefaultEntityResponseBuilder;
import org.springframework.web.servlet.function.DefaultRenderingResponseBuilder;
import org.springframework.web.servlet.function.ServerResponse;

class DefaultServerResponseBuilder
implements ServerResponse.BodyBuilder {
    private final int statusCode;
    private final HttpHeaders headers = new HttpHeaders();
    private final MultiValueMap<String, Cookie> cookies = new LinkedMultiValueMap();

    public DefaultServerResponseBuilder(ServerResponse other) {
        Assert.notNull((Object)other, (String)"ServerResponse must not be null");
        this.statusCode = other instanceof AbstractServerResponse ? ((AbstractServerResponse)other).statusCode : other.statusCode().value();
        this.headers.addAll((MultiValueMap)other.headers());
        this.cookies.addAll(other.cookies());
    }

    public DefaultServerResponseBuilder(HttpStatus status) {
        Assert.notNull((Object)status, (String)"HttpStatus must not be null");
        this.statusCode = status.value();
    }

    public DefaultServerResponseBuilder(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public ServerResponse.BodyBuilder header(String headerName, String ... headerValues) {
        for (String headerValue : headerValues) {
            this.headers.add(headerName, headerValue);
        }
        return this;
    }

    @Override
    public ServerResponse.BodyBuilder headers(Consumer<HttpHeaders> headersConsumer) {
        headersConsumer.accept(this.headers);
        return this;
    }

    @Override
    public ServerResponse.BodyBuilder cookie(Cookie cookie) {
        Assert.notNull((Object)cookie, (String)"Cookie must not be null");
        this.cookies.add((Object)cookie.getName(), (Object)cookie);
        return this;
    }

    @Override
    public ServerResponse.BodyBuilder cookies(Consumer<MultiValueMap<String, Cookie>> cookiesConsumer) {
        cookiesConsumer.accept(this.cookies);
        return this;
    }

    @Override
    public ServerResponse.BodyBuilder allow(HttpMethod ... allowedMethods) {
        this.headers.setAllow(new LinkedHashSet<HttpMethod>(Arrays.asList(allowedMethods)));
        return this;
    }

    @Override
    public ServerResponse.BodyBuilder allow(Set<HttpMethod> allowedMethods) {
        this.headers.setAllow(allowedMethods);
        return this;
    }

    @Override
    public ServerResponse.BodyBuilder contentLength(long contentLength) {
        this.headers.setContentLength(contentLength);
        return this;
    }

    @Override
    public ServerResponse.BodyBuilder contentType(MediaType contentType) {
        this.headers.setContentType(contentType);
        return this;
    }

    @Override
    public ServerResponse.BodyBuilder eTag(String etag) {
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
    public ServerResponse.BodyBuilder lastModified(ZonedDateTime lastModified) {
        this.headers.setLastModified(lastModified);
        return this;
    }

    @Override
    public ServerResponse.BodyBuilder lastModified(Instant lastModified) {
        this.headers.setLastModified(lastModified);
        return this;
    }

    @Override
    public ServerResponse.BodyBuilder location(URI location) {
        this.headers.setLocation(location);
        return this;
    }

    @Override
    public ServerResponse.BodyBuilder cacheControl(CacheControl cacheControl) {
        this.headers.setCacheControl(cacheControl);
        return this;
    }

    @Override
    public ServerResponse.BodyBuilder varyBy(String ... requestHeaders) {
        this.headers.setVary(Arrays.asList(requestHeaders));
        return this;
    }

    @Override
    public ServerResponse build() {
        return this.build((request, response) -> null);
    }

    @Override
    public ServerResponse build(BiFunction<HttpServletRequest, HttpServletResponse, ModelAndView> writeFunction) {
        return new WriterFunctionResponse(this.statusCode, this.headers, this.cookies, writeFunction);
    }

    @Override
    public ServerResponse body(Object body2) {
        return DefaultEntityResponseBuilder.fromObject(body2).status(this.statusCode).headers((HttpHeaders headers) -> headers.putAll((Map)this.headers)).cookies((MultiValueMap<String, Cookie> cookies) -> cookies.addAll(this.cookies)).build();
    }

    @Override
    public <T> ServerResponse body(T body2, ParameterizedTypeReference<T> bodyType) {
        return DefaultEntityResponseBuilder.fromObject(body2, bodyType).status(this.statusCode).headers((HttpHeaders headers) -> headers.putAll((Map)this.headers)).cookies((MultiValueMap<String, Cookie> cookies) -> cookies.addAll(this.cookies)).build();
    }

    @Override
    public ServerResponse render(String name, Object ... modelAttributes) {
        return new DefaultRenderingResponseBuilder(name).status(this.statusCode).headers((HttpHeaders headers) -> headers.putAll((Map)this.headers)).cookies((MultiValueMap<String, Cookie> cookies) -> cookies.addAll(this.cookies)).modelAttributes(modelAttributes).build();
    }

    @Override
    public ServerResponse render(String name, Map<String, ?> model) {
        return new DefaultRenderingResponseBuilder(name).status(this.statusCode).headers((HttpHeaders headers) -> headers.putAll((Map)this.headers)).cookies((MultiValueMap<String, Cookie> cookies) -> cookies.addAll(this.cookies)).modelAttributes(model).build();
    }

    private static class WriterFunctionResponse
    extends AbstractServerResponse {
        private final BiFunction<HttpServletRequest, HttpServletResponse, ModelAndView> writeFunction;

        public WriterFunctionResponse(int statusCode, HttpHeaders headers, MultiValueMap<String, Cookie> cookies, BiFunction<HttpServletRequest, HttpServletResponse, ModelAndView> writeFunction) {
            super(statusCode, headers, cookies);
            Assert.notNull(writeFunction, (String)"WriteFunction must not be null");
            this.writeFunction = writeFunction;
        }

        @Override
        protected ModelAndView writeToInternal(HttpServletRequest request, HttpServletResponse response, ServerResponse.Context context) {
            return this.writeFunction.apply(request, response);
        }
    }
}

