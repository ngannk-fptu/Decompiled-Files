/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.hc.client5.http.impl.async;

import java.io.IOException;
import org.apache.hc.client5.http.HttpRequestRetryStrategy;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.async.AsyncExecCallback;
import org.apache.hc.client5.http.async.AsyncExecChain;
import org.apache.hc.client5.http.async.AsyncExecChainHandler;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.nio.AsyncDataConsumer;
import org.apache.hc.core5.http.nio.AsyncEntityProducer;
import org.apache.hc.core5.http.nio.entity.NoopEntityConsumer;
import org.apache.hc.core5.http.support.BasicRequestBuilder;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Contract(threading=ThreadingBehavior.STATELESS)
@Internal
public final class AsyncHttpRequestRetryExec
implements AsyncExecChainHandler {
    private static final Logger LOG = LoggerFactory.getLogger(AsyncHttpRequestRetryExec.class);
    private final HttpRequestRetryStrategy retryStrategy;

    public AsyncHttpRequestRetryExec(HttpRequestRetryStrategy retryStrategy) {
        Args.notNull(retryStrategy, "retryStrategy");
        this.retryStrategy = retryStrategy;
    }

    private void internalExecute(final State state, final HttpRequest request, final AsyncEntityProducer entityProducer, final AsyncExecChain.Scope scope, AsyncExecChain chain, final AsyncExecCallback asyncExecCallback) throws HttpException, IOException {
        final String exchangeId = scope.exchangeId;
        chain.proceed(BasicRequestBuilder.copy(request).build(), entityProducer, scope, new AsyncExecCallback(){

            @Override
            public AsyncDataConsumer handleResponse(HttpResponse response, EntityDetails entityDetails) throws HttpException, IOException {
                HttpClientContext clientContext = scope.clientContext;
                if (entityProducer != null && !entityProducer.isRepeatable()) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("{} cannot retry non-repeatable request", (Object)exchangeId);
                    }
                    return asyncExecCallback.handleResponse(response, entityDetails);
                }
                state.retrying = AsyncHttpRequestRetryExec.this.retryStrategy.retryRequest(response, scope.execCount.get(), clientContext);
                if (state.retrying) {
                    state.delay = AsyncHttpRequestRetryExec.this.retryStrategy.getRetryInterval(response, scope.execCount.get(), clientContext);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("{} retrying request in {}", (Object)exchangeId, (Object)state.delay);
                    }
                    return new NoopEntityConsumer();
                }
                return asyncExecCallback.handleResponse(response, entityDetails);
            }

            @Override
            public void handleInformationResponse(HttpResponse response) throws HttpException, IOException {
                asyncExecCallback.handleInformationResponse(response);
            }

            @Override
            public void completed() {
                if (state.retrying) {
                    scope.execCount.incrementAndGet();
                    if (entityProducer != null) {
                        entityProducer.releaseResources();
                    }
                    scope.scheduler.scheduleExecution(request, entityProducer, scope, asyncExecCallback, state.delay);
                } else {
                    asyncExecCallback.completed();
                }
            }

            @Override
            public void failed(Exception cause) {
                if (cause instanceof IOException) {
                    HttpRoute route = scope.route;
                    HttpClientContext clientContext = scope.clientContext;
                    if (entityProducer != null && !entityProducer.isRepeatable()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("{} cannot retry non-repeatable request", (Object)exchangeId);
                        }
                    } else if (AsyncHttpRequestRetryExec.this.retryStrategy.retryRequest(request, (IOException)cause, scope.execCount.get(), clientContext)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("{} {}", new Object[]{exchangeId, cause.getMessage(), cause});
                        }
                        if (LOG.isInfoEnabled()) {
                            LOG.info("Recoverable I/O exception ({}) caught when processing request to {}", (Object)cause.getClass().getName(), (Object)route);
                        }
                        scope.execRuntime.discardEndpoint();
                        if (entityProducer != null) {
                            entityProducer.releaseResources();
                        }
                        state.retrying = true;
                        scope.execCount.incrementAndGet();
                        scope.scheduler.scheduleExecution(request, entityProducer, scope, asyncExecCallback, state.delay);
                        return;
                    }
                }
                asyncExecCallback.failed(cause);
            }
        });
    }

    @Override
    public void execute(HttpRequest request, AsyncEntityProducer entityProducer, AsyncExecChain.Scope scope, AsyncExecChain chain, AsyncExecCallback asyncExecCallback) throws HttpException, IOException {
        State state = new State();
        state.retrying = false;
        this.internalExecute(state, request, entityProducer, scope, chain, asyncExecCallback);
    }

    private static class State {
        volatile boolean retrying;
        volatile TimeValue delay;

        private State() {
        }
    }
}

