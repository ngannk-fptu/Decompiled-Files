/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.hc.client5.http.impl.classic;

import java.io.IOException;
import java.util.Iterator;
import org.apache.hc.client5.http.AuthenticationStrategy;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.auth.AuthExchange;
import org.apache.hc.client5.http.auth.ChallengeType;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.auth.CredentialsStore;
import org.apache.hc.client5.http.classic.ExecChain;
import org.apache.hc.client5.http.classic.ExecChainHandler;
import org.apache.hc.client5.http.classic.ExecRuntime;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.AuthSupport;
import org.apache.hc.client5.http.impl.auth.HttpAuthenticator;
import org.apache.hc.client5.http.impl.classic.RequestEntityProxy;
import org.apache.hc.client5.http.impl.classic.ResponseEntityProxy;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.http.protocol.HttpProcessor;
import org.apache.hc.core5.net.URIAuthority;
import org.apache.hc.core5.util.Args;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Contract(threading=ThreadingBehavior.STATELESS)
@Internal
public final class ProtocolExec
implements ExecChainHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ProtocolExec.class);
    private final HttpProcessor httpProcessor;
    private final AuthenticationStrategy targetAuthStrategy;
    private final AuthenticationStrategy proxyAuthStrategy;
    private final HttpAuthenticator authenticator;

    public ProtocolExec(HttpProcessor httpProcessor, AuthenticationStrategy targetAuthStrategy, AuthenticationStrategy proxyAuthStrategy) {
        this.httpProcessor = Args.notNull(httpProcessor, "HTTP protocol processor");
        this.targetAuthStrategy = Args.notNull(targetAuthStrategy, "Target authentication strategy");
        this.proxyAuthStrategy = Args.notNull(proxyAuthStrategy, "Proxy authentication strategy");
        this.authenticator = new HttpAuthenticator();
    }

    @Override
    public ClassicHttpResponse execute(ClassicHttpRequest userRequest, ExecChain.Scope scope, ExecChain chain) throws IOException, HttpException {
        Args.notNull(userRequest, "HTTP request");
        Args.notNull(scope, "Scope");
        if (Method.CONNECT.isSame(userRequest.getMethod())) {
            throw new ProtocolException("Direct execution of CONNECT is not allowed");
        }
        String exchangeId = scope.exchangeId;
        HttpRoute route = scope.route;
        HttpClientContext context = scope.clientContext;
        ExecRuntime execRuntime = scope.execRuntime;
        HttpHost routeTarget = route.getTargetHost();
        HttpHost proxy = route.getProxyHost();
        try {
            ClassicHttpResponse response;
            ClassicHttpRequest request;
            if (proxy != null && !route.isTunnelled()) {
                ClassicRequestBuilder requestBuilder = ClassicRequestBuilder.copy(userRequest);
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
            URIAuthority authority = request.getAuthority();
            CredentialsProvider credsProvider = context.getCredentialsProvider();
            if (credsProvider instanceof CredentialsStore) {
                AuthSupport.extractFromAuthority(request.getScheme(), authority, (CredentialsStore)credsProvider);
            }
            HttpHost target = new HttpHost(request.getScheme(), request.getAuthority());
            AuthExchange targetAuthExchange = context.getAuthExchange(target);
            AuthExchange proxyAuthExchange = proxy != null ? context.getAuthExchange(proxy) : new AuthExchange();
            RequestEntityProxy.enhance(request);
            block3: while (true) {
                context.setAttribute("http.route", route);
                context.setAttribute("http.request", request);
                this.httpProcessor.process(request, (EntityDetails)request.getEntity(), (HttpContext)context);
                if (!request.containsHeader("Authorization")) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("{} target auth state: {}", (Object)exchangeId, (Object)targetAuthExchange.getState());
                    }
                    this.authenticator.addAuthResponse(target, ChallengeType.TARGET, request, targetAuthExchange, context);
                }
                if (!request.containsHeader("Proxy-Authorization") && !route.isTunnelled()) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("{} proxy auth state: {}", (Object)exchangeId, (Object)proxyAuthExchange.getState());
                    }
                    this.authenticator.addAuthResponse(proxy, ChallengeType.PROXY, request, proxyAuthExchange, context);
                }
                response = chain.proceed(request, scope);
                context.setAttribute("http.response", response);
                this.httpProcessor.process(response, (EntityDetails)response.getEntity(), (HttpContext)context);
                if (Method.TRACE.isSame(request.getMethod())) {
                    ResponseEntityProxy.enhance(response, execRuntime);
                    return response;
                }
                HttpEntity requestEntity = request.getEntity();
                if (requestEntity != null && !requestEntity.isRepeatable()) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("{} Cannot retry non-repeatable request", (Object)exchangeId);
                    }
                    ResponseEntityProxy.enhance(response, execRuntime);
                    return response;
                }
                if (!this.needAuthentication(targetAuthExchange, proxyAuthExchange, route, request, response, context)) break;
                HttpEntity responseEntity = response.getEntity();
                if (execRuntime.isConnectionReusable()) {
                    EntityUtils.consume(responseEntity);
                } else {
                    execRuntime.disconnectEndpoint();
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
                ClassicHttpRequest original = scope.originalRequest;
                request.setHeaders(new Header[0]);
                Iterator<Header> it = original.headerIterator();
                while (true) {
                    if (!it.hasNext()) continue block3;
                    request.addHeader(it.next());
                }
                break;
            }
            ResponseEntityProxy.enhance(response, execRuntime);
            return response;
        }
        catch (HttpException ex) {
            execRuntime.discardEndpoint();
            throw ex;
        }
        catch (IOException | RuntimeException ex) {
            execRuntime.discardEndpoint();
            for (AuthExchange authExchange : context.getAuthExchanges().values()) {
                if (!authExchange.isConnectionBased()) continue;
                authExchange.reset();
            }
            throw ex;
        }
    }

    private boolean needAuthentication(AuthExchange targetAuthExchange, AuthExchange proxyAuthExchange, HttpRoute route, ClassicHttpRequest request, HttpResponse response, HttpClientContext context) {
        RequestConfig config = context.getRequestConfig();
        if (config.isAuthenticationEnabled()) {
            HttpHost target = AuthSupport.resolveAuthTarget(request, route);
            boolean targetAuthRequested = this.authenticator.isChallenged(target, ChallengeType.TARGET, response, targetAuthExchange, context);
            HttpHost proxy = route.getProxyHost();
            if (proxy == null) {
                proxy = route.getTargetHost();
            }
            boolean proxyAuthRequested = this.authenticator.isChallenged(proxy, ChallengeType.PROXY, response, proxyAuthExchange, context);
            if (targetAuthRequested) {
                return this.authenticator.updateAuthState(target, ChallengeType.TARGET, response, this.targetAuthStrategy, targetAuthExchange, context);
            }
            if (proxyAuthRequested) {
                return this.authenticator.updateAuthState(proxy, ChallengeType.PROXY, response, this.proxyAuthStrategy, proxyAuthExchange, context);
            }
        }
        return false;
    }
}

