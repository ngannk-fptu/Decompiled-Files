/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.hc.client5.http.impl.classic;

import java.io.IOException;
import java.io.InterruptedIOException;
import org.apache.hc.client5.http.HttpRequestRetryStrategy;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.classic.ExecChain;
import org.apache.hc.client5.http.classic.ExecChainHandler;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.RequestFailedException;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.NoHttpResponseException;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Contract(threading=ThreadingBehavior.STATELESS)
@Internal
public class HttpRequestRetryExec
implements ExecChainHandler {
    private static final Logger LOG = LoggerFactory.getLogger(HttpRequestRetryExec.class);
    private final HttpRequestRetryStrategy retryStrategy;

    public HttpRequestRetryExec(HttpRequestRetryStrategy retryStrategy) {
        Args.notNull(retryStrategy, "retryStrategy");
        this.retryStrategy = retryStrategy;
    }

    @Override
    public ClassicHttpResponse execute(ClassicHttpRequest request, ExecChain.Scope scope, ExecChain chain) throws IOException, HttpException {
        Args.notNull(request, "request");
        Args.notNull(scope, "scope");
        String exchangeId = scope.exchangeId;
        HttpRoute route = scope.route;
        HttpClientContext context = scope.clientContext;
        ClassicHttpRequest currentRequest = request;
        int execCount = 1;
        while (true) {
            block25: {
                ClassicHttpResponse response;
                try {
                    response = chain.proceed(currentRequest, scope);
                }
                catch (IOException ex) {
                    if (scope.execRuntime.isExecutionAborted()) {
                        throw new RequestFailedException("Request aborted");
                    }
                    HttpEntity requestEntity = request.getEntity();
                    if (requestEntity != null && !requestEntity.isRepeatable()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("{} cannot retry non-repeatable request", (Object)exchangeId);
                        }
                        throw ex;
                    }
                    if (this.retryStrategy.retryRequest(request, ex, execCount, context)) {
                        TimeValue nextInterval;
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("{} {}", new Object[]{exchangeId, ex.getMessage(), ex});
                        }
                        if (LOG.isInfoEnabled()) {
                            LOG.info("Recoverable I/O exception ({}) caught when processing request to {}", (Object)ex.getClass().getName(), (Object)route);
                        }
                        if (TimeValue.isPositive(nextInterval = this.retryStrategy.getRetryInterval(request, ex, execCount, context))) {
                            try {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("{} wait for {}", (Object)exchangeId, (Object)nextInterval);
                                }
                                nextInterval.sleep();
                            }
                            catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                throw new InterruptedIOException();
                            }
                        }
                        currentRequest = ClassicRequestBuilder.copy(scope.originalRequest).build();
                        break block25;
                    }
                    if (ex instanceof NoHttpResponseException) {
                        NoHttpResponseException updatedex = new NoHttpResponseException(route.getTargetHost().toHostString() + " failed to respond");
                        updatedex.setStackTrace(ex.getStackTrace());
                        throw updatedex;
                    }
                    throw ex;
                }
                try {
                    HttpEntity entity = request.getEntity();
                    if (entity != null && !entity.isRepeatable()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("{} cannot retry non-repeatable request", (Object)exchangeId);
                        }
                        return response;
                    }
                    if (this.retryStrategy.retryRequest(response, execCount, context)) {
                        RequestConfig requestConfig;
                        Timeout responseTimeout;
                        TimeValue nextInterval = this.retryStrategy.getRetryInterval(response, execCount, context);
                        if (TimeValue.isPositive(nextInterval) && (responseTimeout = (requestConfig = context.getRequestConfig()).getResponseTimeout()) != null && nextInterval.compareTo(responseTimeout) > 0) {
                            return response;
                        }
                        response.close();
                        if (TimeValue.isPositive(nextInterval)) {
                            try {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("{} wait for {}", (Object)exchangeId, (Object)nextInterval);
                                }
                                nextInterval.sleep();
                            }
                            catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                throw new InterruptedIOException();
                            }
                        }
                    } else {
                        return response;
                    }
                    currentRequest = ClassicRequestBuilder.copy(scope.originalRequest).build();
                }
                catch (RuntimeException ex) {
                    response.close();
                    throw ex;
                }
            }
            ++execCount;
        }
    }
}

