/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.reactivestreams.Publisher
 */
package org.springframework.web.servlet.function;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.reactivestreams.Publisher;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.async.AsyncWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.function.AsyncServerResponse;
import org.springframework.web.servlet.function.ErrorHandlingServerResponse;
import org.springframework.web.servlet.function.ServerResponse;

final class DefaultAsyncServerResponse
extends ErrorHandlingServerResponse
implements AsyncServerResponse {
    static final boolean reactiveStreamsPresent = ClassUtils.isPresent("org.reactivestreams.Publisher", DefaultAsyncServerResponse.class.getClassLoader());
    private final CompletableFuture<ServerResponse> futureResponse;
    @Nullable
    private final Duration timeout;

    private DefaultAsyncServerResponse(CompletableFuture<ServerResponse> futureResponse, @Nullable Duration timeout) {
        this.futureResponse = futureResponse;
        this.timeout = timeout;
    }

    @Override
    public ServerResponse block() {
        try {
            if (this.timeout != null) {
                return this.futureResponse.get(this.timeout.toMillis(), TimeUnit.MILLISECONDS);
            }
            return this.futureResponse.get();
        }
        catch (InterruptedException | ExecutionException | TimeoutException ex) {
            throw new IllegalStateException("Failed to get future response", ex);
        }
    }

    @Override
    public HttpStatus statusCode() {
        return this.delegate(ServerResponse::statusCode);
    }

    @Override
    public int rawStatusCode() {
        return this.delegate(ServerResponse::rawStatusCode);
    }

    @Override
    public HttpHeaders headers() {
        return this.delegate(ServerResponse::headers);
    }

    @Override
    public MultiValueMap<String, Cookie> cookies() {
        return this.delegate(ServerResponse::cookies);
    }

    private <R> R delegate(Function<ServerResponse, R> function) {
        ServerResponse response = this.futureResponse.getNow(null);
        if (response != null) {
            return function.apply(response);
        }
        throw new IllegalStateException("Future ServerResponse has not yet completed");
    }

    @Override
    @Nullable
    public ModelAndView writeTo(HttpServletRequest request, HttpServletResponse response, ServerResponse.Context context) throws ServletException, IOException {
        DefaultAsyncServerResponse.writeAsync(request, response, this.createDeferredResult(request));
        return null;
    }

    static void writeAsync(HttpServletRequest request, HttpServletResponse response, DeferredResult<?> deferredResult) throws ServletException, IOException {
        WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager((ServletRequest)request);
        AsyncWebRequest asyncWebRequest = WebAsyncUtils.createAsyncWebRequest(request, response);
        asyncManager.setAsyncWebRequest(asyncWebRequest);
        try {
            asyncManager.startDeferredResultProcessing(deferredResult, new Object[0]);
        }
        catch (IOException | ServletException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new ServletException("Async processing failed", (Throwable)ex);
        }
    }

    private DeferredResult<ServerResponse> createDeferredResult(HttpServletRequest request) {
        DeferredResult<ServerResponse> result = this.timeout != null ? new DeferredResult(this.timeout.toMillis()) : new DeferredResult<ServerResponse>();
        this.futureResponse.handle((value, ex) -> {
            if (ex != null) {
                ServerResponse errorResponse;
                if (ex instanceof CompletionException && ex.getCause() != null) {
                    ex = ex.getCause();
                }
                if ((errorResponse = this.errorResponse((Throwable)ex, request)) != null) {
                    result.setResult(errorResponse);
                } else {
                    result.setErrorResult(ex);
                }
            } else {
                result.setResult((ServerResponse)value);
            }
            return null;
        });
        return result;
    }

    public static AsyncServerResponse create(Object o, @Nullable Duration timeout) {
        ReactiveAdapterRegistry registry;
        ReactiveAdapter publisherAdapter;
        Assert.notNull(o, "Argument to async must not be null");
        if (o instanceof CompletableFuture) {
            CompletableFuture futureResponse = (CompletableFuture)o;
            return new DefaultAsyncServerResponse(futureResponse, timeout);
        }
        if (reactiveStreamsPresent && (publisherAdapter = (registry = ReactiveAdapterRegistry.getSharedInstance()).getAdapter(o.getClass())) != null) {
            Publisher publisher = publisherAdapter.toPublisher(o);
            ReactiveAdapter futureAdapter = registry.getAdapter(CompletableFuture.class);
            if (futureAdapter != null) {
                CompletableFuture futureResponse = (CompletableFuture)futureAdapter.fromPublisher(publisher);
                return new DefaultAsyncServerResponse(futureResponse, timeout);
            }
        }
        throw new IllegalArgumentException("Asynchronous type not supported: " + o.getClass());
    }
}

