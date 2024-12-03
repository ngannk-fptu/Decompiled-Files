/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolException;
import org.apache.http.auth.AuthProtocolState;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthenticationStrategy;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.NonRepeatableRequestException;
import org.apache.http.client.RedirectException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.UserTokenHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.Configurable;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.protocol.RequestClientConnControl;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.routing.BasicRouteDirector;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRouteDirector;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.auth.HttpAuthenticator;
import org.apache.http.impl.nio.client.AbstractClientExchangeHandler;
import org.apache.http.impl.nio.client.InternalClientExec;
import org.apache.http.impl.nio.client.InternalState;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.NHttpClientConnection;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.RequestTargetHost;

class MainClientExec
implements InternalClientExec {
    private final Log log = LogFactory.getLog(this.getClass());
    private final HttpProcessor httpProcessor;
    private final HttpProcessor proxyHttpProcessor;
    private final HttpRoutePlanner routePlanner;
    private final AuthenticationStrategy targetAuthStrategy;
    private final AuthenticationStrategy proxyAuthStrategy;
    private final UserTokenHandler userTokenHandler;
    private final RedirectStrategy redirectStrategy;
    private final HttpRouteDirector routeDirector;
    private final HttpAuthenticator authenticator;

    public MainClientExec(HttpProcessor httpProcessor, HttpRoutePlanner routePlanner, RedirectStrategy redirectStrategy, AuthenticationStrategy targetAuthStrategy, AuthenticationStrategy proxyAuthStrategy, UserTokenHandler userTokenHandler) {
        this.httpProcessor = httpProcessor;
        this.proxyHttpProcessor = new ImmutableHttpProcessor(new RequestTargetHost(), new RequestClientConnControl());
        this.routePlanner = routePlanner;
        this.redirectStrategy = redirectStrategy;
        this.targetAuthStrategy = targetAuthStrategy;
        this.proxyAuthStrategy = proxyAuthStrategy;
        this.userTokenHandler = userTokenHandler;
        this.routeDirector = new BasicRouteDirector();
        this.authenticator = new HttpAuthenticator(this.log);
    }

    @Override
    public void prepare(HttpHost target, HttpRequest original, InternalState state, AbstractClientExchangeHandler handler) throws HttpException, IOException {
        List<URI> redirectLocations;
        RequestConfig config;
        if (this.log.isDebugEnabled()) {
            this.log.debug("[exchange: " + state.getId() + "] start execution");
        }
        HttpClientContext localContext = state.getLocalContext();
        if (original instanceof Configurable && (config = ((Configurable)((Object)original)).getConfig()) != null) {
            localContext.setRequestConfig(config);
        }
        if ((redirectLocations = localContext.getRedirectLocations()) != null) {
            redirectLocations.clear();
        }
        HttpRequestWrapper request = HttpRequestWrapper.wrap(original);
        HttpRoute route = this.routePlanner.determineRoute(target, request, localContext);
        handler.setRoute(route);
        state.setMainRequest(request);
        handler.setCurrentRequest(request);
        this.prepareRequest(state, handler);
    }

    @Override
    public HttpRequest generateRequest(InternalState state, AbstractClientExchangeHandler handler) throws IOException, HttpException {
        AuthState proxyAuthState;
        HttpRoute route = handler.getRoute();
        handler.verifytRoute();
        if (!handler.isRouteEstablished()) {
            int step;
            block9: do {
                HttpRoute fact = handler.getActualRoute();
                step = this.routeDirector.nextStep(route, fact);
                switch (step) {
                    case 1: {
                        handler.onRouteToTarget();
                        break;
                    }
                    case 2: {
                        handler.onRouteToProxy();
                        break;
                    }
                    case 3: {
                        if (this.log.isDebugEnabled()) {
                            this.log.debug("[exchange: " + state.getId() + "] Tunnel required");
                        }
                        HttpRequest connect = this.createConnectRequest(route, state);
                        handler.setCurrentRequest(HttpRequestWrapper.wrap(connect));
                        break block9;
                    }
                    case 4: {
                        throw new HttpException("Proxy chains are not supported");
                    }
                    case 5: {
                        handler.onRouteUpgrade();
                        break;
                    }
                    case -1: {
                        throw new HttpException("Unable to establish route: planned = " + route + "; current = " + fact);
                    }
                    case 0: {
                        handler.onRouteComplete();
                        this.log.debug("[exchange: " + state.getId() + "] Connection route established");
                        break;
                    }
                    default: {
                        throw new IllegalStateException("Unknown step indicator " + step + " from RouteDirector.");
                    }
                }
            } while (step > 0);
        }
        HttpClientContext localContext = state.getLocalContext();
        HttpRequestWrapper currentRequest = handler.getCurrentRequest();
        if (currentRequest == null) {
            currentRequest = state.getMainRequest();
            handler.setCurrentRequest(currentRequest);
        }
        if (handler.isRouteEstablished()) {
            state.incrementExecCount();
            if (state.getExecCount() > 1) {
                HttpAsyncRequestProducer requestProducer = state.getRequestProducer();
                if (!requestProducer.isRepeatable() && state.isRequestContentProduced()) {
                    throw new NonRepeatableRequestException("Cannot retry request with a non-repeatable request entity.");
                }
                requestProducer.resetRequest();
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug("[exchange: " + state.getId() + "] Attempt " + state.getExecCount() + " to execute request");
            }
            if (!currentRequest.containsHeader("Authorization")) {
                AuthState targetAuthState = localContext.getTargetAuthState();
                if (this.log.isDebugEnabled()) {
                    this.log.debug("[exchange: " + state.getId() + "] Target auth state: " + (Object)((Object)targetAuthState.getState()));
                }
                this.authenticator.generateAuthResponse(currentRequest, targetAuthState, localContext);
            }
            if (!currentRequest.containsHeader("Proxy-Authorization") && !route.isTunnelled()) {
                proxyAuthState = localContext.getProxyAuthState();
                if (this.log.isDebugEnabled()) {
                    this.log.debug("[exchange: " + state.getId() + "] Proxy auth state: " + (Object)((Object)proxyAuthState.getState()));
                }
                this.authenticator.generateAuthResponse(currentRequest, proxyAuthState, localContext);
            }
        } else if (!currentRequest.containsHeader("Proxy-Authorization")) {
            proxyAuthState = localContext.getProxyAuthState();
            if (this.log.isDebugEnabled()) {
                this.log.debug("[exchange: " + state.getId() + "] Proxy auth state: " + (Object)((Object)proxyAuthState.getState()));
            }
            this.authenticator.generateAuthResponse(currentRequest, proxyAuthState, localContext);
        }
        NHttpClientConnection managedConn = handler.getConnection();
        localContext.setAttribute("http.connection", managedConn);
        RequestConfig config = localContext.getRequestConfig();
        if (config.getSocketTimeout() > 0) {
            managedConn.setSocketTimeout(config.getSocketTimeout());
        }
        return currentRequest;
    }

    @Override
    public void produceContent(InternalState state, ContentEncoder encoder, IOControl ioControl) throws IOException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("[exchange: " + state.getId() + "] produce content");
        }
        HttpAsyncRequestProducer requestProducer = state.getRequestProducer();
        state.setRequestContentProduced();
        requestProducer.produceContent(encoder, ioControl);
        if (encoder.isCompleted()) {
            requestProducer.resetRequest();
        }
    }

    @Override
    public void requestCompleted(InternalState state, AbstractClientExchangeHandler handler) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("[exchange: " + state.getId() + "] Request completed");
        }
        HttpClientContext localContext = state.getLocalContext();
        HttpAsyncRequestProducer requestProducer = state.getRequestProducer();
        requestProducer.requestCompleted(localContext);
    }

    @Override
    public void responseReceived(HttpResponse response, InternalState state, AbstractClientExchangeHandler handler) throws IOException, HttpException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("[exchange: " + state.getId() + "] Response received " + response.getStatusLine());
        }
        HttpClientContext context = state.getLocalContext();
        context.setAttribute("http.response", response);
        this.httpProcessor.process(response, (HttpContext)context);
        handler.setCurrentResponse(response);
        if (!handler.isRouteEstablished()) {
            int status = response.getStatusLine().getStatusCode();
            if (status < 200) {
                throw new HttpException("Unexpected response to CONNECT request: " + response.getStatusLine());
            }
            if (status == 200) {
                handler.onRouteTunnelToTarget();
                handler.setCurrentRequest(null);
            } else if (!this.handleConnectResponse(state, handler)) {
                state.setFinalResponse(response);
            }
        } else if (!this.handleResponse(state, handler)) {
            state.setFinalResponse(response);
        }
        if (state.getFinalResponse() != null) {
            HttpAsyncResponseConsumer<?> responseConsumer = state.getResponseConsumer();
            responseConsumer.responseReceived(response);
        }
    }

    @Override
    public void consumeContent(InternalState state, ContentDecoder decoder, IOControl ioControl) throws IOException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("[exchange: " + state.getId() + "] Consume content");
        }
        if (state.getFinalResponse() != null) {
            HttpAsyncResponseConsumer<?> responseConsumer = state.getResponseConsumer();
            responseConsumer.consumeContent(decoder, ioControl);
        } else {
            ByteBuffer tmpbuf = state.getTmpbuf();
            tmpbuf.clear();
            decoder.read(tmpbuf);
        }
    }

    @Override
    public void responseCompleted(InternalState state, AbstractClientExchangeHandler handler) throws IOException, HttpException {
        Object userToken;
        int status;
        HttpClientContext localContext = state.getLocalContext();
        HttpResponse currentResponse = handler.getCurrentResponse();
        if (!handler.isRouteEstablished() && (status = currentResponse.getStatusLine().getStatusCode()) == 200) {
            handler.setCurrentResponse(null);
            return;
        }
        boolean keepAlive = handler.manageConnectionPersistence();
        if (!keepAlive) {
            AuthState targetAuthState;
            handler.releaseConnection();
            AuthState proxyAuthState = localContext.getProxyAuthState();
            if (proxyAuthState.getState() == AuthProtocolState.SUCCESS && proxyAuthState.getAuthScheme() != null && proxyAuthState.getAuthScheme().isConnectionBased()) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("[exchange: " + state.getId() + "] Resetting proxy auth state");
                }
                proxyAuthState.reset();
            }
            if ((targetAuthState = localContext.getTargetAuthState()).getState() == AuthProtocolState.SUCCESS && targetAuthState.getAuthScheme() != null && targetAuthState.getAuthScheme().isConnectionBased()) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("[exchange: " + state.getId() + "] Resetting target auth state");
                }
                targetAuthState.reset();
            }
        }
        if ((userToken = localContext.getUserToken()) == null) {
            userToken = this.userTokenHandler.getUserToken(localContext);
            localContext.setAttribute("http.user-token", userToken);
        }
        if (state.getFinalResponse() != null) {
            HttpAsyncResponseConsumer<?> responseConsumer = state.getResponseConsumer();
            responseConsumer.responseCompleted(localContext);
            if (this.log.isDebugEnabled()) {
                this.log.debug("[exchange: " + state.getId() + "] Response processed");
            }
            handler.releaseConnection();
        } else if (state.getRedirect() != null) {
            HttpRequestWrapper newRequest;
            HttpRoute newRoute;
            HttpUriRequest redirect = state.getRedirect();
            URI uri = redirect.getURI();
            if (this.log.isDebugEnabled()) {
                this.log.debug("[exchange: " + state.getId() + "] Redirecting to '" + uri + "'");
            }
            state.setRedirect(null);
            HttpHost newTarget = URIUtils.extractHost(uri);
            if (newTarget == null) {
                throw new ProtocolException("Redirect URI does not specify a valid host name: " + uri);
            }
            HttpRoute route = handler.getRoute();
            if (!route.getTargetHost().equals(newTarget)) {
                AuthState targetAuthState = localContext.getTargetAuthState();
                if (this.log.isDebugEnabled()) {
                    this.log.debug("[exchange: " + state.getId() + "] Resetting target auth state");
                }
                targetAuthState.reset();
                AuthState proxyAuthState = localContext.getProxyAuthState();
                AuthScheme authScheme = proxyAuthState.getAuthScheme();
                if (authScheme != null && authScheme.isConnectionBased()) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug("[exchange: " + state.getId() + "] Resetting proxy auth state");
                    }
                    proxyAuthState.reset();
                }
            }
            if (!redirect.headerIterator().hasNext()) {
                HttpRequest original = state.getMainRequest().getOriginal();
                redirect.setHeaders(original.getAllHeaders());
            }
            if (!route.equals(newRoute = this.routePlanner.determineRoute(newTarget, newRequest = HttpRequestWrapper.wrap(redirect), localContext))) {
                handler.releaseConnection();
            }
            handler.setRoute(newRoute);
            handler.setCurrentRequest(newRequest);
            state.setMainRequest(newRequest);
            this.prepareRequest(state, handler);
        }
        handler.setCurrentResponse(null);
    }

    private void rewriteRequestURI(HttpRequestWrapper request, HttpRoute route) throws ProtocolException {
        try {
            URI uri = request.getURI();
            if (uri != null) {
                if (route.getProxyHost() != null && !route.isTunnelled()) {
                    if (!uri.isAbsolute()) {
                        HttpHost target = route.getTargetHost();
                        uri = URIUtils.rewriteURI(uri, target, true);
                    } else {
                        uri = URIUtils.rewriteURI(uri);
                    }
                } else {
                    uri = uri.isAbsolute() ? URIUtils.rewriteURI(uri, null, true) : URIUtils.rewriteURI(uri);
                }
                request.setURI(uri);
            }
        }
        catch (URISyntaxException ex) {
            throw new ProtocolException("Invalid URI: " + request.getRequestLine().getUri(), ex);
        }
    }

    private void prepareRequest(InternalState state, AbstractClientExchangeHandler handler) throws IOException, HttpException {
        String userinfo;
        URI uri;
        HttpRoute route;
        HttpRequestWrapper currentRequest;
        HttpClientContext localContext;
        block7: {
            localContext = state.getLocalContext();
            currentRequest = handler.getCurrentRequest();
            route = handler.getRoute();
            HttpRequest original = currentRequest.getOriginal();
            uri = null;
            if (original instanceof HttpUriRequest) {
                uri = ((HttpUriRequest)original).getURI();
            } else {
                String uriString = original.getRequestLine().getUri();
                try {
                    uri = URI.create(uriString);
                }
                catch (IllegalArgumentException ex) {
                    if (!this.log.isDebugEnabled()) break block7;
                    this.log.debug("[exchange: " + state.getId() + "] Unable to parse '" + uriString + "' as a valid URI; request URI and Host header may be inconsistent", ex);
                }
            }
        }
        currentRequest.setURI(uri);
        this.rewriteRequestURI(currentRequest, route);
        HttpHost target = null;
        if (uri != null && uri.isAbsolute() && uri.getHost() != null) {
            target = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        }
        if (target == null) {
            target = route.getTargetHost();
        }
        if (uri != null && (userinfo = uri.getUserInfo()) != null) {
            CredentialsProvider credsProvider = localContext.getCredentialsProvider();
            credsProvider.setCredentials(new AuthScope(target), new UsernamePasswordCredentials(userinfo));
        }
        localContext.setAttribute("http.request", currentRequest);
        localContext.setAttribute("http.target_host", target);
        localContext.setAttribute("http.route", route);
        this.httpProcessor.process(currentRequest, (HttpContext)localContext);
    }

    private HttpRequest createConnectRequest(HttpRoute route, InternalState state) throws IOException, HttpException {
        HttpHost target = route.getTargetHost();
        String host = target.getHostName();
        int port = target.getPort();
        StringBuilder buffer = new StringBuilder(host.length() + 6);
        buffer.append(host);
        buffer.append(':');
        buffer.append(Integer.toString(port));
        BasicHttpRequest request = new BasicHttpRequest("CONNECT", buffer.toString(), HttpVersion.HTTP_1_1);
        HttpClientContext localContext = state.getLocalContext();
        this.proxyHttpProcessor.process(request, (HttpContext)localContext);
        return request;
    }

    private boolean handleConnectResponse(InternalState state, AbstractClientExchangeHandler handler) {
        AuthState proxyAuthState;
        HttpResponse currentResponse;
        HttpRoute route;
        HttpHost proxy;
        CredentialsProvider credsProvider;
        HttpClientContext localContext = state.getLocalContext();
        RequestConfig config = localContext.getRequestConfig();
        if (config.isAuthenticationEnabled() && (credsProvider = localContext.getCredentialsProvider()) != null && this.authenticator.isAuthenticationRequested(proxy = (route = handler.getRoute()).getProxyHost(), currentResponse = handler.getCurrentResponse(), this.proxyAuthStrategy, proxyAuthState = localContext.getProxyAuthState(), localContext)) {
            return this.authenticator.handleAuthChallenge(proxy, currentResponse, this.proxyAuthStrategy, proxyAuthState, localContext);
        }
        return false;
    }

    private boolean handleResponse(InternalState state, AbstractClientExchangeHandler handler) throws HttpException {
        HttpResponse currentResponse;
        HttpRequestWrapper currentRequest;
        HttpClientContext localContext = state.getLocalContext();
        RequestConfig config = localContext.getRequestConfig();
        if (config.isAuthenticationEnabled() && this.needAuthentication(state, handler)) {
            HttpRequestWrapper currentRequest2 = handler.getCurrentRequest();
            HttpRequest original = currentRequest2.getOriginal();
            if (!original.containsHeader("Authorization")) {
                currentRequest2.removeHeaders("Authorization");
            }
            if (!original.containsHeader("Proxy-Authorization")) {
                currentRequest2.removeHeaders("Proxy-Authorization");
            }
            return true;
        }
        if (config.isRedirectsEnabled() && this.redirectStrategy.isRedirected(currentRequest = handler.getCurrentRequest(), currentResponse = handler.getCurrentResponse(), localContext)) {
            int maxRedirects;
            int n = maxRedirects = config.getMaxRedirects() >= 0 ? config.getMaxRedirects() : 100;
            if (state.getRedirectCount() >= maxRedirects) {
                throw new RedirectException("Maximum redirects (" + maxRedirects + ") exceeded");
            }
            state.incrementRedirectCount();
            HttpUriRequest redirect = this.redirectStrategy.getRedirect(currentRequest.getOriginal(), currentResponse, localContext);
            state.setRedirect(redirect);
            return true;
        }
        return false;
    }

    private boolean needAuthentication(InternalState state, AbstractClientExchangeHandler handler) {
        HttpClientContext localContext = state.getLocalContext();
        CredentialsProvider credsProvider = localContext.getCredentialsProvider();
        if (credsProvider != null) {
            HttpRoute route = handler.getRoute();
            HttpResponse currentResponse = handler.getCurrentResponse();
            HttpHost target = localContext.getTargetHost();
            if (target == null) {
                target = route.getTargetHost();
            }
            if (target.getPort() < 0) {
                target = new HttpHost(target.getHostName(), route.getTargetHost().getPort(), target.getSchemeName());
            }
            AuthState targetAuthState = localContext.getTargetAuthState();
            AuthState proxyAuthState = localContext.getProxyAuthState();
            boolean targetAuthRequested = this.authenticator.isAuthenticationRequested(target, currentResponse, this.targetAuthStrategy, targetAuthState, localContext);
            HttpHost proxy = route.getProxyHost();
            if (proxy == null) {
                proxy = route.getTargetHost();
            }
            boolean proxyAuthRequested = this.authenticator.isAuthenticationRequested(proxy, currentResponse, this.proxyAuthStrategy, proxyAuthState, localContext);
            if (targetAuthRequested) {
                return this.authenticator.handleAuthChallenge(target, currentResponse, this.targetAuthStrategy, targetAuthState, localContext);
            }
            if (proxyAuthRequested) {
                return this.authenticator.handleAuthChallenge(proxy, currentResponse, this.proxyAuthStrategy, proxyAuthState, localContext);
            }
        }
        return false;
    }
}

