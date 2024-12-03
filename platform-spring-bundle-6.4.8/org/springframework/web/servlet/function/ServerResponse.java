/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.web.servlet.function;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.function.DefaultAsyncServerResponse;
import org.springframework.web.servlet.function.DefaultServerResponseBuilder;
import org.springframework.web.servlet.function.SseServerResponse;

public interface ServerResponse {
    public HttpStatus statusCode();

    public int rawStatusCode();

    public HttpHeaders headers();

    public MultiValueMap<String, Cookie> cookies();

    @Nullable
    public ModelAndView writeTo(HttpServletRequest var1, HttpServletResponse var2, Context var3) throws ServletException, IOException;

    public static BodyBuilder from(ServerResponse other) {
        return new DefaultServerResponseBuilder(other);
    }

    public static BodyBuilder status(HttpStatus status) {
        return new DefaultServerResponseBuilder(status);
    }

    public static BodyBuilder status(int status) {
        return new DefaultServerResponseBuilder(status);
    }

    public static BodyBuilder ok() {
        return ServerResponse.status(HttpStatus.OK);
    }

    public static BodyBuilder created(URI location) {
        BodyBuilder builder = ServerResponse.status(HttpStatus.CREATED);
        return (BodyBuilder)builder.location(location);
    }

    public static BodyBuilder accepted() {
        return ServerResponse.status(HttpStatus.ACCEPTED);
    }

    public static HeadersBuilder<?> noContent() {
        return ServerResponse.status(HttpStatus.NO_CONTENT);
    }

    public static BodyBuilder seeOther(URI location) {
        BodyBuilder builder = ServerResponse.status(HttpStatus.SEE_OTHER);
        return (BodyBuilder)builder.location(location);
    }

    public static BodyBuilder temporaryRedirect(URI location) {
        BodyBuilder builder = ServerResponse.status(HttpStatus.TEMPORARY_REDIRECT);
        return (BodyBuilder)builder.location(location);
    }

    public static BodyBuilder permanentRedirect(URI location) {
        BodyBuilder builder = ServerResponse.status(HttpStatus.PERMANENT_REDIRECT);
        return (BodyBuilder)builder.location(location);
    }

    public static BodyBuilder badRequest() {
        return ServerResponse.status(HttpStatus.BAD_REQUEST);
    }

    public static HeadersBuilder<?> notFound() {
        return ServerResponse.status(HttpStatus.NOT_FOUND);
    }

    public static BodyBuilder unprocessableEntity() {
        return ServerResponse.status(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    public static ServerResponse async(Object asyncResponse) {
        return DefaultAsyncServerResponse.create(asyncResponse, null);
    }

    public static ServerResponse async(Object asyncResponse, Duration timeout) {
        return DefaultAsyncServerResponse.create(asyncResponse, timeout);
    }

    public static ServerResponse sse(Consumer<SseBuilder> consumer) {
        return SseServerResponse.create(consumer, null);
    }

    public static ServerResponse sse(Consumer<SseBuilder> consumer, Duration timeout) {
        return SseServerResponse.create(consumer, timeout);
    }

    public static interface Context {
        public List<HttpMessageConverter<?>> messageConverters();
    }

    public static interface SseBuilder {
        public void send(Object var1) throws IOException;

        public SseBuilder id(String var1);

        public SseBuilder event(String var1);

        public SseBuilder retry(Duration var1);

        public SseBuilder comment(String var1);

        public void data(Object var1) throws IOException;

        public void error(Throwable var1);

        public void complete();

        public SseBuilder onTimeout(Runnable var1);

        public SseBuilder onError(Consumer<Throwable> var1);

        public SseBuilder onComplete(Runnable var1);
    }

    public static interface BodyBuilder
    extends HeadersBuilder<BodyBuilder> {
        public BodyBuilder contentLength(long var1);

        public BodyBuilder contentType(MediaType var1);

        public ServerResponse body(Object var1);

        public <T> ServerResponse body(T var1, ParameterizedTypeReference<T> var2);

        public ServerResponse render(String var1, Object ... var2);

        public ServerResponse render(String var1, Map<String, ?> var2);
    }

    public static interface HeadersBuilder<B extends HeadersBuilder<B>> {
        public B header(String var1, String ... var2);

        public B headers(Consumer<HttpHeaders> var1);

        public B cookie(Cookie var1);

        public B cookies(Consumer<MultiValueMap<String, Cookie>> var1);

        public B allow(HttpMethod ... var1);

        public B allow(Set<HttpMethod> var1);

        public B eTag(String var1);

        public B lastModified(ZonedDateTime var1);

        public B lastModified(Instant var1);

        public B location(URI var1);

        public B cacheControl(CacheControl var1);

        public B varyBy(String ... var1);

        public ServerResponse build();

        public ServerResponse build(BiFunction<HttpServletRequest, HttpServletResponse, ModelAndView> var1);
    }
}

