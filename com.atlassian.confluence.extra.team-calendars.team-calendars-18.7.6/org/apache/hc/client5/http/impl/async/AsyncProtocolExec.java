/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.hc.client5.http.impl.async;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hc.client5.http.AuthenticationStrategy;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.SchemePortResolver;
import org.apache.hc.client5.http.async.AsyncExecCallback;
import org.apache.hc.client5.http.async.AsyncExecChain;
import org.apache.hc.client5.http.async.AsyncExecChainHandler;
import org.apache.hc.client5.http.async.AsyncExecRuntime;
import org.apache.hc.client5.http.auth.AuthExchange;
import org.apache.hc.client5.http.auth.ChallengeType;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultSchemePortResolver;
import org.apache.hc.client5.http.impl.RequestSupport;
import org.apache.hc.client5.http.impl.auth.AuthCacheKeeper;
import org.apache.hc.client5.http.impl.auth.HttpAuthenticator;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.http.nio.AsyncDataConsumer;
import org.apache.hc.core5.http.nio.AsyncEntityProducer;
import org.apache.hc.core5.http.support.BasicRequestBuilder;
import org.apache.hc.core5.net.URIAuthority;
import org.apache.hc.core5.util.Args;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Contract(threading=ThreadingBehavior.STATELESS)
@Internal
public final class AsyncProtocolExec
implements AsyncExecChainHandler {
    private static final Logger LOG = LoggerFactory.getLogger(AsyncProtocolExec.class);
    private final AuthenticationStrategy targetAuthStrategy;
    private final AuthenticationStrategy proxyAuthStrategy;
    private final HttpAuthenticator authenticator;
    private final SchemePortResolver schemePortResolver;
    private final AuthCacheKeeper authCacheKeeper;

    AsyncProtocolExec(AuthenticationStrategy targetAuthStrategy, AuthenticationStrategy proxyAuthStrategy, SchemePortResolver schemePortResolver, boolean authCachingDisabled) {
        this.targetAuthStrategy = Args.notNull(targetAuthStrategy, "Target authentication strategy");
        this.proxyAuthStrategy = Args.notNull(proxyAuthStrategy, "Proxy authentication strategy");
        this.authenticator = new HttpAuthenticator();
        this.schemePortResolver = schemePortResolver != null ? schemePortResolver : DefaultSchemePortResolver.INSTANCE;
        this.authCacheKeeper = authCachingDisabled ? null : new AuthCacheKeeper(this.schemePortResolver);
    }

    @Override
    public void execute(HttpRequest userRequest, AsyncEntityProducer entityProducer, AsyncExecChain.Scope scope, AsyncExecChain chain, AsyncExecCallback asyncExecCallback) throws HttpException, IOException {
        AuthExchange proxyAuthExchange;
        URIAuthority authority;
        HttpRequest request;
        if (Method.CONNECT.isSame(userRequest.getMethod())) {
            throw new ProtocolException("Direct execution of CONNECT is not allowed");
        }
        HttpRoute route = scope.route;
        HttpHost routeTarget = route.getTargetHost();
        HttpHost proxy = route.getProxyHost();
        HttpClientContext clientContext = scope.clientContext;
        if (proxy != null && !route.isTunnelled()) {
            BasicRequestBuilder requestBuilder = BasicRequestBuilder.copy(userRequest);
            if (requestBuilder.getAuthority() == null) {
                requestBuilder.setAuthority(new URIAuthority(routeTarget));
            }
            requestBuilder.setAbsoluteRequestUri(true);
            request = requestBuilder.build();
        } else {
            request = userRequest;
        }
        if (request.getScheme() == null) {
            request.setScheme(routeTarget.getSchemeName());
        }
        if (request.getAuthority() == null) {
            request.setAuthority(new URIAuthority(routeTarget));
        }
        if ((authority = request.getAuthority()).getUserInfo() != null) {
            throw new ProtocolException("Request URI authority contains deprecated userinfo component");
        }
        HttpHost target = new HttpHost(request.getScheme(), authority.getHostName(), this.schemePortResolver.resolve(request.getScheme(), authority));
        String pathPrefix = RequestSupport.extractPathPrefix(request);
        AuthExchange targetAuthExchange = clientContext.getAuthExchange(target);
        AuthExchange authExchange = proxyAuthExchange = proxy != null ? clientContext.getAuthExchange(proxy) : new AuthExchange();
        if (!targetAuthExchange.isConnectionBased() && targetAuthExchange.getPathPrefix() != null && !pathPrefix.startsWith(targetAuthExchange.getPathPrefix())) {
            targetAuthExchange.reset();
        }
        if (targetAuthExchange.getPathPrefix() == null) {
            targetAuthExchange.setPathPrefix(pathPrefix);
        }
        if (this.authCacheKeeper != null) {
            this.authCacheKeeper.loadPreemptively(target, pathPrefix, targetAuthExchange, clientContext);
            if (proxy != null) {
                this.authCacheKeeper.loadPreemptively(proxy, null, proxyAuthExchange, clientContext);
            }
        }
        AtomicBoolean challenged = new AtomicBoolean(false);
        this.internalExecute(target, pathPrefix, targetAuthExchange, proxyAuthExchange, challenged, request, entityProducer, scope, chain, asyncExecCallback);
    }

    private void internalExecute(final HttpHost target, final String pathPrefix, final AuthExchange targetAuthExchange, final AuthExchange proxyAuthExchange, final AtomicBoolean challenged, final HttpRequest request, final AsyncEntityProducer entityProducer, final AsyncExecChain.Scope scope, final AsyncExecChain chain, final AsyncExecCallback asyncExecCallback) throws HttpException, IOException {
        final String exchangeId = scope.exchangeId;
        HttpRoute route = scope.route;
        final HttpClientContext clientContext = scope.clientContext;
        final AsyncExecRuntime execRuntime = scope.execRuntime;
        final HttpHost proxy = route.getProxyHost();
        if (!request.containsHeader("Authorization")) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} target auth state: {}", (Object)exchangeId, (Object)targetAuthExchange.getState());
            }
            this.authenticator.addAuthResponse(target, ChallengeType.TARGET, request, targetAuthExchange, clientContext);
        }
        if (!request.containsHeader("Proxy-Authorization") && !route.isTunnelled()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} proxy auth state: {}", (Object)exchangeId, (Object)proxyAuthExchange.getState());
            }
            this.authenticator.addAuthResponse(proxy, ChallengeType.PROXY, request, proxyAuthExchange, clientContext);
        }
        chain.proceed(request, entityProducer, scope, new AsyncExecCallback(){

            @Override
            public AsyncDataConsumer handleResponse(HttpResponse response, EntityDetails entityDetails) throws HttpException, IOException {
                if (Method.TRACE.isSame(request.getMethod())) {
                    return asyncExecCallback.handleResponse(response, entityDetails);
                }
                if (AsyncProtocolExec.this.needAuthentication(targetAuthExchange, proxyAuthExchange, proxy != null ? proxy : target, target, pathPrefix, response, clientContext)) {
                    challenged.set(true);
                    return null;
                }
                challenged.set(false);
                return asyncExecCallback.handleResponse(response, entityDetails);
            }

            @Override
            public void handleInformationResponse(HttpResponse response) throws HttpException, IOException {
                asyncExecCallback.handleInformationResponse(response);
            }

            @Override
            public void completed() {
                if (!execRuntime.isEndpointConnected()) {
                    if (proxyAuthExchange.getState() == AuthExchange.State.SUCCESS && proxyAuthExchange.isConnectionBased()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("{} resetting proxy auth state", (Object)exchangeId);
                        }
                        proxyAuthExchange.reset();
                    }
                    if (targetAuthExchange.getState() == AuthExchange.State.SUCCESS && targetAuthExchange.isConnectionBased()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("{} resetting target auth state", (Object)exchangeId);
                        }
                        targetAuthExchange.reset();
                    }
                }
                if (challenged.get()) {
                    if (entityProducer != null && !entityProducer.isRepeatable()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("{} cannot retry non-repeatable request", (Object)exchangeId);
                        }
                        asyncExecCallback.completed();
                    } else {
                        HttpRequest original = scope.originalRequest;
                        request.setHeaders(new Header[0]);
                        Iterator<Header> it = original.headerIterator();
                        while (it.hasNext()) {
                            request.addHeader(it.next());
                        }
                        try {
                            if (entityProducer != null) {
                                entityProducer.releaseResources();
                            }
                            AsyncProtocolExec.this.internalExecute(target, pathPrefix, targetAuthExchange, proxyAuthExchange, challenged, request, entityProducer, scope, chain, asyncExecCallback);
                        }
                        catch (IOException | HttpException ex) {
                            asyncExecCallback.failed(ex);
                        }
                    }
                } else {
                    asyncExecCallback.completed();
                }
            }

            @Override
            public void failed(Exception cause) {
                if (cause instanceof IOException || cause instanceof RuntimeException) {
                    for (AuthExchange authExchange : clientContext.getAuthExchanges().values()) {
                        if (!authExchange.isConnectionBased()) continue;
                        authExchange.reset();
                    }
                }
                asyncExecCallback.failed(cause);
            }
        });
    }

    private boolean needAuthentication(AuthExchange targetAuthExchange, AuthExchange proxyAuthExchange, HttpHost proxy, HttpHost target, String pathPrefix, HttpResponse response, HttpClientContext context) {
        RequestConfig config = context.getRequestConfig();
        if (config.isAuthenticationEnabled()) {
            boolean targetAuthRequested = this.authenticator.isChallenged(target, ChallengeType.TARGET, response, targetAuthExchange, context);
            if (this.authCacheKeeper != null) {
                if (targetAuthRequested) {
                    this.authCacheKeeper.updateOnChallenge(target, pathPrefix, targetAuthExchange, context);
                } else {
                    this.authCacheKeeper.updateOnNoChallenge(target, pathPrefix, targetAuthExchange, context);
                }
            }
            boolean proxyAuthRequested = this.authenticator.isChallenged(proxy, ChallengeType.PROXY, response, proxyAuthExchange, context);
            if (this.authCacheKeeper != null) {
                if (proxyAuthRequested) {
                    this.authCacheKeeper.updateOnChallenge(proxy, null, proxyAuthExchange, context);
                } else {
                    this.authCacheKeeper.updateOnNoChallenge(proxy, null, proxyAuthExchange, context);
                }
            }
            if (targetAuthRequested) {
                boolean updated = this.authenticator.updateAuthState(target, ChallengeType.TARGET, response, this.targetAuthStrategy, targetAuthExchange, context);
                if (this.authCacheKeeper != null) {
                    this.authCacheKeeper.updateOnResponse(target, pathPrefix, targetAuthExchange, context);
                }
                return updated;
            }
            if (proxyAuthRequested) {
                boolean updated = this.authenticator.updateAuthState(proxy, ChallengeType.PROXY, response, this.proxyAuthStrategy, proxyAuthExchange, context);
                if (this.authCacheKeeper != null) {
                    this.authCacheKeeper.updateOnResponse(proxy, null, proxyAuthExchange, context);
                }
                return updated;
            }
        }
        return false;
    }
}

