/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.http.CacheControl
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.HttpOutputMessage
 *  org.springframework.http.MediaType
 *  org.springframework.http.converter.HttpMessageConverter
 *  org.springframework.http.server.DelegatingServerHttpResponse
 *  org.springframework.http.server.ServerHttpResponse
 *  org.springframework.http.server.ServletServerHttpResponse
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.MultiValueMap
 *  org.springframework.web.context.request.async.DeferredResult
 */
package org.springframework.web.servlet.function;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.DelegatingServerHttpResponse;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.function.AbstractServerResponse;
import org.springframework.web.servlet.function.DefaultAsyncServerResponse;
import org.springframework.web.servlet.function.ServerResponse;

final class SseServerResponse
extends AbstractServerResponse {
    private final Consumer<ServerResponse.SseBuilder> sseConsumer;
    @Nullable
    private final Duration timeout;

    private SseServerResponse(Consumer<ServerResponse.SseBuilder> sseConsumer, @Nullable Duration timeout) {
        super(200, SseServerResponse.createHeaders(), SseServerResponse.emptyCookies());
        this.sseConsumer = sseConsumer;
        this.timeout = timeout;
    }

    private static HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_EVENT_STREAM);
        headers.setCacheControl(CacheControl.noCache());
        return headers;
    }

    private static MultiValueMap<String, Cookie> emptyCookies() {
        return CollectionUtils.toMultiValueMap(Collections.emptyMap());
    }

    @Override
    @Nullable
    protected ModelAndView writeToInternal(HttpServletRequest request, HttpServletResponse response, ServerResponse.Context context) throws ServletException, IOException {
        DeferredResult result = this.timeout != null ? new DeferredResult(Long.valueOf(this.timeout.toMillis())) : new DeferredResult();
        DefaultAsyncServerResponse.writeAsync(request, response, result);
        this.sseConsumer.accept(new DefaultSseBuilder(response, context, result, this.headers()));
        return null;
    }

    public static ServerResponse create(Consumer<ServerResponse.SseBuilder> sseConsumer, @Nullable Duration timeout) {
        Assert.notNull(sseConsumer, (String)"SseConsumer must not be null");
        return new SseServerResponse(sseConsumer, timeout);
    }

    private static final class DefaultSseBuilder
    implements ServerResponse.SseBuilder {
        private static final byte[] NL_NL = new byte[]{10, 10};
        private final ServerHttpResponse outputMessage;
        private final DeferredResult<?> deferredResult;
        private final List<HttpMessageConverter<?>> messageConverters;
        private final HttpHeaders httpHeaders;
        private final StringBuilder builder = new StringBuilder();
        private boolean sendFailed;

        public DefaultSseBuilder(HttpServletResponse response, ServerResponse.Context context, DeferredResult<?> deferredResult, HttpHeaders httpHeaders) {
            this.outputMessage = new ServletServerHttpResponse(response);
            this.deferredResult = deferredResult;
            this.messageConverters = context.messageConverters();
            this.httpHeaders = httpHeaders;
        }

        @Override
        public void send(Object object) throws IOException {
            this.data(object);
        }

        @Override
        public ServerResponse.SseBuilder id(String id) {
            Assert.hasLength((String)id, (String)"Id must not be empty");
            return this.field("id", id);
        }

        @Override
        public ServerResponse.SseBuilder event(String eventName) {
            Assert.hasLength((String)eventName, (String)"Name must not be empty");
            return this.field("event", eventName);
        }

        @Override
        public ServerResponse.SseBuilder retry(Duration duration) {
            Assert.notNull((Object)duration, (String)"Duration must not be null");
            String millis = Long.toString(duration.toMillis());
            return this.field("retry", millis);
        }

        @Override
        public ServerResponse.SseBuilder comment(String comment) {
            String[] lines;
            Assert.hasLength((String)comment, (String)"Comment must not be empty");
            for (String line : lines = comment.split("\n")) {
                this.field("", line);
            }
            return this;
        }

        private ServerResponse.SseBuilder field(String name, String value) {
            this.builder.append(name).append(':').append(value).append('\n');
            return this;
        }

        @Override
        public void data(Object object) throws IOException {
            Assert.notNull((Object)object, (String)"Object must not be null");
            if (object instanceof String) {
                this.writeString((String)object);
            } else {
                this.writeObject(object);
            }
        }

        private void writeString(String string) throws IOException {
            String[] lines;
            for (String line : lines = string.split("\n")) {
                this.field("data", line);
            }
            this.builder.append('\n');
            try {
                OutputStream body2 = this.outputMessage.getBody();
                body2.write(this.builderBytes());
                body2.flush();
            }
            catch (IOException ex) {
                this.sendFailed = true;
                throw ex;
            }
            finally {
                this.builder.setLength(0);
            }
        }

        private void writeObject(Object data) throws IOException {
            this.builder.append("data:");
            try {
                this.outputMessage.getBody().write(this.builderBytes());
                Class<?> dataClass = data.getClass();
                for (HttpMessageConverter<?> converter : this.messageConverters) {
                    if (!converter.canWrite(dataClass, MediaType.APPLICATION_JSON)) continue;
                    HttpMessageConverter<?> objectConverter = converter;
                    MutableHeadersServerHttpResponse response = new MutableHeadersServerHttpResponse(this.outputMessage, this.httpHeaders);
                    objectConverter.write(data, MediaType.APPLICATION_JSON, (HttpOutputMessage)response);
                    this.outputMessage.getBody().write(NL_NL);
                    this.outputMessage.flush();
                    return;
                }
            }
            catch (IOException ex) {
                this.sendFailed = true;
                throw ex;
            }
            finally {
                this.builder.setLength(0);
            }
        }

        private byte[] builderBytes() {
            return this.builder.toString().getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public void error(Throwable t) {
            if (this.sendFailed) {
                return;
            }
            this.deferredResult.setErrorResult((Object)t);
        }

        @Override
        public void complete() {
            if (this.sendFailed) {
                return;
            }
            try {
                this.outputMessage.flush();
                this.deferredResult.setResult(null);
            }
            catch (IOException ex) {
                this.deferredResult.setErrorResult((Object)ex);
            }
        }

        @Override
        public ServerResponse.SseBuilder onTimeout(Runnable onTimeout) {
            this.deferredResult.onTimeout(onTimeout);
            return this;
        }

        @Override
        public ServerResponse.SseBuilder onError(Consumer<Throwable> onError2) {
            this.deferredResult.onError(onError2);
            return this;
        }

        @Override
        public ServerResponse.SseBuilder onComplete(Runnable onCompletion) {
            this.deferredResult.onCompletion(onCompletion);
            return this;
        }

        private static final class MutableHeadersServerHttpResponse
        extends DelegatingServerHttpResponse {
            private final HttpHeaders mutableHeaders = new HttpHeaders();

            public MutableHeadersServerHttpResponse(ServerHttpResponse delegate, HttpHeaders headers) {
                super(delegate);
                this.mutableHeaders.putAll((Map)delegate.getHeaders());
                this.mutableHeaders.putAll((Map)headers);
            }

            public HttpHeaders getHeaders() {
                return this.mutableHeaders;
            }
        }
    }
}

