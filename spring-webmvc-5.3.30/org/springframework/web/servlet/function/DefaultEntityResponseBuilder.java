/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 *  org.reactivestreams.Publisher
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  org.springframework.core.ParameterizedTypeReference
 *  org.springframework.core.ReactiveAdapter
 *  org.springframework.core.ReactiveAdapterRegistry
 *  org.springframework.core.io.InputStreamResource
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.support.ResourceRegion
 *  org.springframework.http.CacheControl
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.HttpMethod
 *  org.springframework.http.HttpOutputMessage
 *  org.springframework.http.HttpRange
 *  org.springframework.http.HttpStatus
 *  org.springframework.http.InvalidMediaTypeException
 *  org.springframework.http.MediaType
 *  org.springframework.http.converter.GenericHttpMessageConverter
 *  org.springframework.http.converter.HttpMessageConverter
 *  org.springframework.http.server.ServletServerHttpResponse
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 *  org.springframework.web.HttpMediaTypeNotAcceptableException
 *  org.springframework.web.context.request.async.DeferredResult
 */
package org.springframework.web.servlet.function;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.function.AbstractServerResponse;
import org.springframework.web.servlet.function.DefaultAsyncServerResponse;
import org.springframework.web.servlet.function.EntityResponse;
import org.springframework.web.servlet.function.ServerResponse;

final class DefaultEntityResponseBuilder<T>
implements EntityResponse.Builder<T> {
    private static final Type RESOURCE_REGION_LIST_TYPE = new ParameterizedTypeReference<List<ResourceRegion>>(){}.getType();
    private final T entity;
    private final Type entityType;
    private int status = HttpStatus.OK.value();
    private final HttpHeaders headers = new HttpHeaders();
    private final MultiValueMap<String, Cookie> cookies = new LinkedMultiValueMap();

    private DefaultEntityResponseBuilder(T entity, @Nullable Type entityType) {
        this.entity = entity;
        this.entityType = entityType != null ? entityType : entity.getClass();
    }

    @Override
    public EntityResponse.Builder<T> status(HttpStatus status) {
        Assert.notNull((Object)status, (String)"HttpStatus must not be null");
        this.status = status.value();
        return this;
    }

    @Override
    public EntityResponse.Builder<T> status(int status) {
        this.status = status;
        return this;
    }

    @Override
    public EntityResponse.Builder<T> cookie(Cookie cookie) {
        Assert.notNull((Object)cookie, (String)"Cookie must not be null");
        this.cookies.add((Object)cookie.getName(), (Object)cookie);
        return this;
    }

    @Override
    public EntityResponse.Builder<T> cookies(Consumer<MultiValueMap<String, Cookie>> cookiesConsumer) {
        cookiesConsumer.accept(this.cookies);
        return this;
    }

    @Override
    public EntityResponse.Builder<T> header(String headerName, String ... headerValues) {
        for (String headerValue : headerValues) {
            this.headers.add(headerName, headerValue);
        }
        return this;
    }

    @Override
    public EntityResponse.Builder<T> headers(Consumer<HttpHeaders> headersConsumer) {
        headersConsumer.accept(this.headers);
        return this;
    }

    @Override
    public EntityResponse.Builder<T> allow(HttpMethod ... allowedMethods) {
        this.headers.setAllow(new LinkedHashSet<HttpMethod>(Arrays.asList(allowedMethods)));
        return this;
    }

    @Override
    public EntityResponse.Builder<T> allow(Set<HttpMethod> allowedMethods) {
        this.headers.setAllow(allowedMethods);
        return this;
    }

    @Override
    public EntityResponse.Builder<T> contentLength(long contentLength) {
        this.headers.setContentLength(contentLength);
        return this;
    }

    @Override
    public EntityResponse.Builder<T> contentType(MediaType contentType) {
        this.headers.setContentType(contentType);
        return this;
    }

    @Override
    public EntityResponse.Builder<T> eTag(String etag) {
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
    public EntityResponse.Builder<T> lastModified(ZonedDateTime lastModified) {
        this.headers.setLastModified(lastModified);
        return this;
    }

    @Override
    public EntityResponse.Builder<T> lastModified(Instant lastModified) {
        this.headers.setLastModified(lastModified);
        return this;
    }

    @Override
    public EntityResponse.Builder<T> location(URI location) {
        this.headers.setLocation(location);
        return this;
    }

    @Override
    public EntityResponse.Builder<T> cacheControl(CacheControl cacheControl) {
        this.headers.setCacheControl(cacheControl);
        return this;
    }

    @Override
    public EntityResponse.Builder<T> varyBy(String ... requestHeaders) {
        this.headers.setVary(Arrays.asList(requestHeaders));
        return this;
    }

    @Override
    public EntityResponse<T> build() {
        ReactiveAdapter adapter;
        if (this.entity instanceof CompletionStage) {
            CompletionStage completionStage = (CompletionStage)this.entity;
            return new CompletionStageEntityResponse(this.status, this.headers, this.cookies, completionStage, this.entityType);
        }
        if (DefaultAsyncServerResponse.reactiveStreamsPresent && (adapter = ReactiveAdapterRegistry.getSharedInstance().getAdapter(this.entity.getClass())) != null) {
            Publisher publisher = adapter.toPublisher(this.entity);
            return new PublisherEntityResponse(this.status, this.headers, this.cookies, publisher, this.entityType);
        }
        return new DefaultEntityResponse<T>(this.status, this.headers, this.cookies, this.entity, this.entityType);
    }

    public static <T> EntityResponse.Builder<T> fromObject(T t) {
        return new DefaultEntityResponseBuilder<T>(t, null);
    }

    public static <T> EntityResponse.Builder<T> fromObject(T t, ParameterizedTypeReference<?> bodyType) {
        return new DefaultEntityResponseBuilder<T>(t, bodyType.getType());
    }

    private static class PublisherEntityResponse<T>
    extends DefaultEntityResponse<Publisher<T>> {
        public PublisherEntityResponse(int statusCode, HttpHeaders headers, MultiValueMap<String, Cookie> cookies, Publisher<T> entity, Type entityType) {
            super(statusCode, headers, cookies, entity, entityType);
        }

        @Override
        protected ModelAndView writeToInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse, ServerResponse.Context context) throws ServletException, IOException {
            DeferredResult deferredResult = new DeferredResult();
            DefaultAsyncServerResponse.writeAsync(servletRequest, servletResponse, deferredResult);
            ((Publisher)this.entity()).subscribe((Subscriber)new DeferredResultSubscriber(servletRequest, servletResponse, context, deferredResult));
            return null;
        }

        private static class NoContentLengthResponseWrapper
        extends HttpServletResponseWrapper {
            public NoContentLengthResponseWrapper(HttpServletResponse response) {
                super(response);
            }

            public void addIntHeader(String name, int value) {
                if (!"Content-Length".equals(name)) {
                    super.addIntHeader(name, value);
                }
            }

            public void addHeader(String name, String value) {
                if (!"Content-Length".equals(name)) {
                    super.addHeader(name, value);
                }
            }

            public void setContentLength(int len) {
            }

            public void setContentLengthLong(long len) {
            }
        }

        private class DeferredResultSubscriber
        implements Subscriber<T> {
            private final HttpServletRequest servletRequest;
            private final HttpServletResponse servletResponse;
            private final ServerResponse.Context context;
            private final DeferredResult<?> deferredResult;
            @Nullable
            private Subscription subscription;

            public DeferredResultSubscriber(HttpServletRequest servletRequest, HttpServletResponse servletResponse, ServerResponse.Context context, DeferredResult<?> deferredResult) {
                this.servletRequest = servletRequest;
                this.servletResponse = new NoContentLengthResponseWrapper(servletResponse);
                this.context = context;
                this.deferredResult = deferredResult;
            }

            public void onSubscribe(Subscription s) {
                if (this.subscription == null) {
                    this.subscription = s;
                    this.subscription.request(1L);
                } else {
                    s.cancel();
                }
            }

            public void onNext(T t) {
                Assert.state((this.subscription != null ? 1 : 0) != 0, (String)"No subscription");
                try {
                    PublisherEntityResponse.this.tryWriteEntityWithMessageConverters(t, this.servletRequest, this.servletResponse, this.context);
                    this.servletResponse.getOutputStream().flush();
                    this.subscription.request(1L);
                }
                catch (IOException | ServletException ex) {
                    this.subscription.cancel();
                    this.deferredResult.setErrorResult((Object)ex);
                }
            }

            public void onError(Throwable t) {
                try {
                    PublisherEntityResponse.this.handleError(t, this.servletRequest, this.servletResponse, this.context);
                }
                catch (IOException | ServletException handlingThrowable) {
                    this.deferredResult.setErrorResult((Object)handlingThrowable);
                }
            }

            public void onComplete() {
                try {
                    this.servletResponse.getOutputStream().flush();
                    this.deferredResult.setResult(null);
                }
                catch (IOException ex) {
                    this.deferredResult.setErrorResult((Object)ex);
                }
            }
        }
    }

    private static class CompletionStageEntityResponse<T>
    extends DefaultEntityResponse<CompletionStage<T>> {
        public CompletionStageEntityResponse(int statusCode, HttpHeaders headers, MultiValueMap<String, Cookie> cookies, CompletionStage<T> entity, Type entityType) {
            super(statusCode, headers, cookies, entity, entityType);
        }

        @Override
        protected ModelAndView writeToInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse, ServerResponse.Context context) throws ServletException, IOException {
            DeferredResult<ServerResponse> deferredResult = this.createDeferredResult(servletRequest, servletResponse, context);
            DefaultAsyncServerResponse.writeAsync(servletRequest, servletResponse, deferredResult);
            return null;
        }

        private DeferredResult<ServerResponse> createDeferredResult(HttpServletRequest request, HttpServletResponse response, ServerResponse.Context context) {
            DeferredResult result = new DeferredResult();
            ((CompletionStage)this.entity()).handle((value, ex) -> {
                if (ex != null) {
                    ServerResponse errorResponse;
                    if (ex instanceof CompletionException && ex.getCause() != null) {
                        ex = ex.getCause();
                    }
                    if ((errorResponse = this.errorResponse((Throwable)ex, request)) != null) {
                        result.setResult((Object)errorResponse);
                    } else {
                        result.setErrorResult(ex);
                    }
                } else {
                    try {
                        this.tryWriteEntityWithMessageConverters(value, request, response, context);
                        result.setResult(null);
                    }
                    catch (IOException | ServletException writeException) {
                        result.setErrorResult((Object)writeException);
                    }
                }
                return null;
            });
            return result;
        }
    }

    private static class DefaultEntityResponse<T>
    extends AbstractServerResponse
    implements EntityResponse<T> {
        private final T entity;
        private final Type entityType;

        public DefaultEntityResponse(int statusCode, HttpHeaders headers, MultiValueMap<String, Cookie> cookies, T entity, Type entityType) {
            super(statusCode, headers, cookies);
            this.entity = entity;
            this.entityType = entityType;
        }

        @Override
        public T entity() {
            return this.entity;
        }

        @Override
        protected ModelAndView writeToInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse, ServerResponse.Context context) throws ServletException, IOException {
            this.writeEntityWithMessageConverters(this.entity, servletRequest, servletResponse, context);
            return null;
        }

        protected void writeEntityWithMessageConverters(Object entity, HttpServletRequest request, HttpServletResponse response, ServerResponse.Context context) throws ServletException, IOException {
            ServletServerHttpResponse serverResponse = new ServletServerHttpResponse(response);
            MediaType contentType = DefaultEntityResponse.getContentType(response);
            Class<?> entityClass = entity.getClass();
            Type entityType = this.entityType;
            if (entityClass != InputStreamResource.class && Resource.class.isAssignableFrom(entityClass)) {
                serverResponse.getHeaders().set("Accept-Ranges", "bytes");
                String rangeHeader = request.getHeader("Range");
                if (rangeHeader != null) {
                    Resource resource = (Resource)entity;
                    try {
                        List httpRanges = HttpRange.parseRanges((String)rangeHeader);
                        serverResponse.getServletResponse().setStatus(HttpStatus.PARTIAL_CONTENT.value());
                        entity = HttpRange.toResourceRegions((List)httpRanges, (Resource)resource);
                        entityClass = entity.getClass();
                        entityType = RESOURCE_REGION_LIST_TYPE;
                    }
                    catch (IllegalArgumentException ex) {
                        serverResponse.getHeaders().set("Content-Range", "bytes */" + resource.contentLength());
                        serverResponse.getServletResponse().setStatus(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value());
                    }
                }
            }
            for (HttpMessageConverter<?> messageConverter : context.messageConverters()) {
                GenericHttpMessageConverter genericMessageConverter;
                if (messageConverter instanceof GenericHttpMessageConverter && (genericMessageConverter = (GenericHttpMessageConverter)messageConverter).canWrite(entityType, entityClass, contentType)) {
                    genericMessageConverter.write(entity, entityType, contentType, (HttpOutputMessage)serverResponse);
                    return;
                }
                if (!messageConverter.canWrite(entityClass, contentType)) continue;
                messageConverter.write(entity, contentType, (HttpOutputMessage)serverResponse);
                return;
            }
            List<MediaType> producibleMediaTypes = DefaultEntityResponse.producibleMediaTypes(context.messageConverters(), entityClass);
            throw new HttpMediaTypeNotAcceptableException(producibleMediaTypes);
        }

        @Nullable
        private static MediaType getContentType(HttpServletResponse response) {
            try {
                return MediaType.parseMediaType((String)response.getContentType()).removeQualityValue();
            }
            catch (InvalidMediaTypeException ex) {
                return null;
            }
        }

        protected void tryWriteEntityWithMessageConverters(Object entity, HttpServletRequest request, HttpServletResponse response, ServerResponse.Context context) throws ServletException, IOException {
            try {
                this.writeEntityWithMessageConverters(entity, request, response, context);
            }
            catch (IOException | ServletException ex) {
                this.handleError(ex, request, response, context);
            }
        }

        private static List<MediaType> producibleMediaTypes(List<HttpMessageConverter<?>> messageConverters, Class<?> entityClass) {
            return messageConverters.stream().filter(messageConverter -> messageConverter.canWrite(entityClass, null)).flatMap(messageConverter -> messageConverter.getSupportedMediaTypes(entityClass).stream()).collect(Collectors.toList());
        }
    }
}

