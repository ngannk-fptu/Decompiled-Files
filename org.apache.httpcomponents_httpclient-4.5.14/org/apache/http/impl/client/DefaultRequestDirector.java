/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.http.ConnectionReuseStrategy
 *  org.apache.http.HttpClientConnection
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpEntityEnclosingRequest
 *  org.apache.http.HttpException
 *  org.apache.http.HttpHost
 *  org.apache.http.HttpRequest
 *  org.apache.http.HttpResponse
 *  org.apache.http.NoHttpResponseException
 *  org.apache.http.ProtocolException
 *  org.apache.http.ProtocolVersion
 *  org.apache.http.entity.BufferedHttpEntity
 *  org.apache.http.message.BasicHttpRequest
 *  org.apache.http.params.HttpConnectionParams
 *  org.apache.http.params.HttpParams
 *  org.apache.http.params.HttpProtocolParams
 *  org.apache.http.protocol.HttpContext
 *  org.apache.http.protocol.HttpProcessor
 *  org.apache.http.protocol.HttpRequestExecutor
 *  org.apache.http.util.Args
 *  org.apache.http.util.EntityUtils
 */
package org.apache.http.impl.client;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.ProtocolException;
import org.apache.http.ProtocolVersion;
import org.apache.http.auth.AuthProtocolState;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthenticationHandler;
import org.apache.http.client.AuthenticationStrategy;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.NonRepeatableRequestException;
import org.apache.http.client.RedirectException;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.RequestDirector;
import org.apache.http.client.UserTokenHandler;
import org.apache.http.client.methods.AbortableHttpRequest;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.BasicManagedEntity;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.routing.BasicRouteDirector;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.AuthenticationStrategyAdaptor;
import org.apache.http.impl.client.DefaultRedirectStrategyAdaptor;
import org.apache.http.impl.client.EntityEnclosingRequestWrapper;
import org.apache.http.impl.client.HttpAuthenticator;
import org.apache.http.impl.client.RequestWrapper;
import org.apache.http.impl.client.RoutedRequest;
import org.apache.http.impl.client.TunnelRefusedException;
import org.apache.http.impl.conn.ConnectionShutdownException;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;

@Deprecated
public class DefaultRequestDirector
implements RequestDirector {
    private final Log log;
    protected final ClientConnectionManager connManager;
    protected final HttpRoutePlanner routePlanner;
    protected final ConnectionReuseStrategy reuseStrategy;
    protected final ConnectionKeepAliveStrategy keepAliveStrategy;
    protected final HttpRequestExecutor requestExec;
    protected final HttpProcessor httpProcessor;
    protected final HttpRequestRetryHandler retryHandler;
    protected final RedirectHandler redirectHandler;
    protected final RedirectStrategy redirectStrategy;
    protected final AuthenticationHandler targetAuthHandler;
    protected final AuthenticationStrategy targetAuthStrategy;
    protected final AuthenticationHandler proxyAuthHandler;
    protected final AuthenticationStrategy proxyAuthStrategy;
    protected final UserTokenHandler userTokenHandler;
    protected final HttpParams params;
    protected ManagedClientConnection managedConn;
    protected final AuthState targetAuthState;
    protected final AuthState proxyAuthState;
    private final HttpAuthenticator authenticator;
    private int execCount;
    private int redirectCount;
    private final int maxRedirects;
    private HttpHost virtualHost;

    public DefaultRequestDirector(HttpRequestExecutor requestExec, ClientConnectionManager conman, ConnectionReuseStrategy reustrat, ConnectionKeepAliveStrategy kastrat, HttpRoutePlanner rouplan, HttpProcessor httpProcessor, HttpRequestRetryHandler retryHandler, RedirectHandler redirectHandler, AuthenticationHandler targetAuthHandler, AuthenticationHandler proxyAuthHandler, UserTokenHandler userTokenHandler, HttpParams params) {
        this(LogFactory.getLog(DefaultRequestDirector.class), requestExec, conman, reustrat, kastrat, rouplan, httpProcessor, retryHandler, (RedirectStrategy)new DefaultRedirectStrategyAdaptor(redirectHandler), new AuthenticationStrategyAdaptor(targetAuthHandler), new AuthenticationStrategyAdaptor(proxyAuthHandler), userTokenHandler, params);
    }

    public DefaultRequestDirector(Log log, HttpRequestExecutor requestExec, ClientConnectionManager conman, ConnectionReuseStrategy reustrat, ConnectionKeepAliveStrategy kastrat, HttpRoutePlanner rouplan, HttpProcessor httpProcessor, HttpRequestRetryHandler retryHandler, RedirectStrategy redirectStrategy, AuthenticationHandler targetAuthHandler, AuthenticationHandler proxyAuthHandler, UserTokenHandler userTokenHandler, HttpParams params) {
        this(LogFactory.getLog(DefaultRequestDirector.class), requestExec, conman, reustrat, kastrat, rouplan, httpProcessor, retryHandler, redirectStrategy, new AuthenticationStrategyAdaptor(targetAuthHandler), new AuthenticationStrategyAdaptor(proxyAuthHandler), userTokenHandler, params);
    }

    public DefaultRequestDirector(Log log, HttpRequestExecutor requestExec, ClientConnectionManager conman, ConnectionReuseStrategy reustrat, ConnectionKeepAliveStrategy kastrat, HttpRoutePlanner rouplan, HttpProcessor httpProcessor, HttpRequestRetryHandler retryHandler, RedirectStrategy redirectStrategy, AuthenticationStrategy targetAuthStrategy, AuthenticationStrategy proxyAuthStrategy, UserTokenHandler userTokenHandler, HttpParams params) {
        Args.notNull((Object)log, (String)"Log");
        Args.notNull((Object)requestExec, (String)"Request executor");
        Args.notNull((Object)conman, (String)"Client connection manager");
        Args.notNull((Object)reustrat, (String)"Connection reuse strategy");
        Args.notNull((Object)kastrat, (String)"Connection keep alive strategy");
        Args.notNull((Object)rouplan, (String)"Route planner");
        Args.notNull((Object)httpProcessor, (String)"HTTP protocol processor");
        Args.notNull((Object)retryHandler, (String)"HTTP request retry handler");
        Args.notNull((Object)redirectStrategy, (String)"Redirect strategy");
        Args.notNull((Object)targetAuthStrategy, (String)"Target authentication strategy");
        Args.notNull((Object)proxyAuthStrategy, (String)"Proxy authentication strategy");
        Args.notNull((Object)userTokenHandler, (String)"User token handler");
        Args.notNull((Object)params, (String)"HTTP parameters");
        this.log = log;
        this.authenticator = new HttpAuthenticator(log);
        this.requestExec = requestExec;
        this.connManager = conman;
        this.reuseStrategy = reustrat;
        this.keepAliveStrategy = kastrat;
        this.routePlanner = rouplan;
        this.httpProcessor = httpProcessor;
        this.retryHandler = retryHandler;
        this.redirectStrategy = redirectStrategy;
        this.targetAuthStrategy = targetAuthStrategy;
        this.proxyAuthStrategy = proxyAuthStrategy;
        this.userTokenHandler = userTokenHandler;
        this.params = params;
        this.redirectHandler = redirectStrategy instanceof DefaultRedirectStrategyAdaptor ? ((DefaultRedirectStrategyAdaptor)redirectStrategy).getHandler() : null;
        this.targetAuthHandler = targetAuthStrategy instanceof AuthenticationStrategyAdaptor ? ((AuthenticationStrategyAdaptor)targetAuthStrategy).getHandler() : null;
        this.proxyAuthHandler = proxyAuthStrategy instanceof AuthenticationStrategyAdaptor ? ((AuthenticationStrategyAdaptor)proxyAuthStrategy).getHandler() : null;
        this.managedConn = null;
        this.execCount = 0;
        this.redirectCount = 0;
        this.targetAuthState = new AuthState();
        this.proxyAuthState = new AuthState();
        this.maxRedirects = this.params.getIntParameter("http.protocol.max-redirects", 100);
    }

    private RequestWrapper wrapRequest(HttpRequest request) throws ProtocolException {
        if (request instanceof HttpEntityEnclosingRequest) {
            return new EntityEnclosingRequestWrapper((HttpEntityEnclosingRequest)request);
        }
        return new RequestWrapper(request);
    }

    protected void rewriteRequestURI(RequestWrapper request, HttpRoute route) throws ProtocolException {
        try {
            URI uri = request.getURI();
            if (route.getProxyHost() != null && !route.isTunnelled()) {
                if (!uri.isAbsolute()) {
                    HttpHost target = route.getTargetHost();
                    uri = URIUtils.rewriteURI(uri, target, URIUtils.DROP_FRAGMENT_AND_NORMALIZE);
                } else {
                    uri = URIUtils.rewriteURI(uri);
                }
            } else {
                uri = uri.isAbsolute() ? URIUtils.rewriteURI(uri, null, URIUtils.DROP_FRAGMENT_AND_NORMALIZE) : URIUtils.rewriteURI(uri);
            }
            request.setURI(uri);
        }
        catch (URISyntaxException ex) {
            throw new ProtocolException("Invalid URI: " + request.getRequestLine().getUri(), (Throwable)ex);
        }
    }

    @Override
    public HttpResponse execute(HttpHost targetHost, HttpRequest request, HttpContext context) throws HttpException, IOException {
        HttpHost host;
        int port;
        context.setAttribute("http.auth.target-scope", (Object)this.targetAuthState);
        context.setAttribute("http.auth.proxy-scope", (Object)this.proxyAuthState);
        HttpHost target = targetHost;
        HttpRequest orig = request;
        RequestWrapper origWrapper = this.wrapRequest(orig);
        origWrapper.setParams(this.params);
        HttpRoute origRoute = this.determineRoute(target, origWrapper, context);
        this.virtualHost = (HttpHost)origWrapper.getParams().getParameter("http.virtual-host");
        if (this.virtualHost != null && this.virtualHost.getPort() == -1 && (port = (host = target != null ? target : origRoute.getTargetHost()).getPort()) != -1) {
            this.virtualHost = new HttpHost(this.virtualHost.getHostName(), port, this.virtualHost.getSchemeName());
        }
        RoutedRequest roureq = new RoutedRequest(origWrapper, origRoute);
        boolean reuse = false;
        boolean done = false;
        try {
            HttpResponse response = null;
            while (!done) {
                RoutedRequest followup;
                RequestWrapper wrapper = roureq.getRequest();
                HttpRoute route = roureq.getRoute();
                response = null;
                Object userToken = context.getAttribute("http.user-token");
                if (this.managedConn == null) {
                    ClientConnectionRequest connRequest = this.connManager.requestConnection(route, userToken);
                    if (orig instanceof AbortableHttpRequest) {
                        ((AbortableHttpRequest)orig).setConnectionRequest(connRequest);
                    }
                    long timeout = HttpClientParams.getConnectionManagerTimeout(this.params);
                    try {
                        this.managedConn = connRequest.getConnection(timeout, TimeUnit.MILLISECONDS);
                    }
                    catch (InterruptedException interrupted) {
                        Thread.currentThread().interrupt();
                        throw new InterruptedIOException();
                    }
                    if (HttpConnectionParams.isStaleCheckingEnabled((HttpParams)this.params) && this.managedConn.isOpen()) {
                        this.log.debug((Object)"Stale connection check");
                        if (this.managedConn.isStale()) {
                            this.log.debug((Object)"Stale connection detected");
                            this.managedConn.close();
                        }
                    }
                }
                if (orig instanceof AbortableHttpRequest) {
                    ((AbortableHttpRequest)orig).setReleaseTrigger(this.managedConn);
                }
                try {
                    this.tryConnect(roureq, context);
                }
                catch (TunnelRefusedException ex) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug((Object)ex.getMessage());
                    }
                    response = ex.getResponse();
                    break;
                }
                String userinfo = wrapper.getURI().getUserInfo();
                if (userinfo != null) {
                    this.targetAuthState.update(new BasicScheme(), new UsernamePasswordCredentials(userinfo));
                }
                if (this.virtualHost != null) {
                    target = this.virtualHost;
                } else {
                    URI requestURI = wrapper.getURI();
                    if (requestURI.isAbsolute()) {
                        target = URIUtils.extractHost(requestURI);
                    }
                }
                if (target == null) {
                    target = route.getTargetHost();
                }
                wrapper.resetHeaders();
                this.rewriteRequestURI(wrapper, route);
                context.setAttribute("http.target_host", (Object)target);
                context.setAttribute("http.route", (Object)route);
                context.setAttribute("http.connection", (Object)this.managedConn);
                this.requestExec.preProcess((HttpRequest)wrapper, this.httpProcessor, context);
                response = this.tryExecute(roureq, context);
                if (response == null) continue;
                response.setParams(this.params);
                this.requestExec.postProcess(response, this.httpProcessor, context);
                reuse = this.reuseStrategy.keepAlive(response, context);
                if (reuse) {
                    long duration = this.keepAliveStrategy.getKeepAliveDuration(response, context);
                    if (this.log.isDebugEnabled()) {
                        String s = duration > 0L ? "for " + duration + " " + (Object)((Object)TimeUnit.MILLISECONDS) : "indefinitely";
                        this.log.debug((Object)("Connection can be kept alive " + s));
                    }
                    this.managedConn.setIdleDuration(duration, TimeUnit.MILLISECONDS);
                }
                if ((followup = this.handleResponse(roureq, response, context)) == null) {
                    done = true;
                } else {
                    if (reuse) {
                        HttpEntity entity = response.getEntity();
                        EntityUtils.consume((HttpEntity)entity);
                        this.managedConn.markReusable();
                    } else {
                        this.managedConn.close();
                        if (this.proxyAuthState.getState().compareTo(AuthProtocolState.CHALLENGED) > 0 && this.proxyAuthState.getAuthScheme() != null && this.proxyAuthState.getAuthScheme().isConnectionBased()) {
                            this.log.debug((Object)"Resetting proxy auth state");
                            this.proxyAuthState.reset();
                        }
                        if (this.targetAuthState.getState().compareTo(AuthProtocolState.CHALLENGED) > 0 && this.targetAuthState.getAuthScheme() != null && this.targetAuthState.getAuthScheme().isConnectionBased()) {
                            this.log.debug((Object)"Resetting target auth state");
                            this.targetAuthState.reset();
                        }
                    }
                    if (!followup.getRoute().equals(roureq.getRoute())) {
                        this.releaseConnection();
                    }
                    roureq = followup;
                }
                if (this.managedConn == null) continue;
                if (userToken == null) {
                    userToken = this.userTokenHandler.getUserToken(context);
                    context.setAttribute("http.user-token", userToken);
                }
                if (userToken == null) continue;
                this.managedConn.setState(userToken);
            }
            if (response == null || response.getEntity() == null || !response.getEntity().isStreaming()) {
                if (reuse) {
                    this.managedConn.markReusable();
                }
                this.releaseConnection();
            } else {
                Object entity = response.getEntity();
                entity = new BasicManagedEntity((HttpEntity)entity, this.managedConn, reuse);
                response.setEntity(entity);
            }
            return response;
        }
        catch (ConnectionShutdownException ex) {
            InterruptedIOException ioex = new InterruptedIOException("Connection has been shut down");
            ioex.initCause(ex);
            throw ioex;
        }
        catch (HttpException ex) {
            this.abortConnection();
            throw ex;
        }
        catch (IOException ex) {
            this.abortConnection();
            throw ex;
        }
        catch (RuntimeException ex) {
            this.abortConnection();
            throw ex;
        }
    }

    private void tryConnect(RoutedRequest req, HttpContext context) throws HttpException, IOException {
        HttpRoute route = req.getRoute();
        RequestWrapper wrapper = req.getRequest();
        int connectCount = 0;
        while (true) {
            context.setAttribute("http.request", (Object)wrapper);
            ++connectCount;
            try {
                if (!this.managedConn.isOpen()) {
                    this.managedConn.open(route, context, this.params);
                } else {
                    this.managedConn.setSocketTimeout(HttpConnectionParams.getSoTimeout((HttpParams)this.params));
                }
                this.establishRoute(route, context);
            }
            catch (IOException ex) {
                try {
                    this.managedConn.close();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                if (this.retryHandler.retryRequest(ex, connectCount, context)) {
                    if (!this.log.isInfoEnabled()) continue;
                    this.log.info((Object)("I/O exception (" + ex.getClass().getName() + ") caught when connecting to " + route + ": " + ex.getMessage()));
                    if (this.log.isDebugEnabled()) {
                        this.log.debug((Object)ex.getMessage(), (Throwable)ex);
                    }
                    this.log.info((Object)("Retrying connect to " + route));
                    continue;
                }
                throw ex;
            }
            break;
        }
    }

    private HttpResponse tryExecute(RoutedRequest req, HttpContext context) throws HttpException, IOException {
        RequestWrapper wrapper = req.getRequest();
        HttpRoute route = req.getRoute();
        HttpResponse response = null;
        IOException retryReason = null;
        while (true) {
            ++this.execCount;
            wrapper.incrementExecCount();
            if (!wrapper.isRepeatable()) {
                this.log.debug((Object)"Cannot retry non-repeatable request");
                if (retryReason != null) {
                    throw new NonRepeatableRequestException("Cannot retry request with a non-repeatable request entity.  The cause lists the reason the original request failed.", retryReason);
                }
                throw new NonRepeatableRequestException("Cannot retry request with a non-repeatable request entity.");
            }
            try {
                if (!this.managedConn.isOpen()) {
                    if (!route.isTunnelled()) {
                        this.log.debug((Object)"Reopening the direct connection.");
                        this.managedConn.open(route, context, this.params);
                    } else {
                        this.log.debug((Object)"Proxied connection. Need to start over.");
                        break;
                    }
                }
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)("Attempt " + this.execCount + " to execute request"));
                }
                response = this.requestExec.execute((HttpRequest)wrapper, (HttpClientConnection)this.managedConn, context);
            }
            catch (IOException ex) {
                this.log.debug((Object)"Closing the connection.");
                try {
                    this.managedConn.close();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                if (this.retryHandler.retryRequest(ex, wrapper.getExecCount(), context)) {
                    if (this.log.isInfoEnabled()) {
                        this.log.info((Object)("I/O exception (" + ex.getClass().getName() + ") caught when processing request to " + route + ": " + ex.getMessage()));
                    }
                    if (this.log.isDebugEnabled()) {
                        this.log.debug((Object)ex.getMessage(), (Throwable)ex);
                    }
                    if (this.log.isInfoEnabled()) {
                        this.log.info((Object)("Retrying request to " + route));
                    }
                    retryReason = ex;
                    continue;
                }
                if (ex instanceof NoHttpResponseException) {
                    NoHttpResponseException updatedex = new NoHttpResponseException(route.getTargetHost().toHostString() + " failed to respond");
                    updatedex.setStackTrace(ex.getStackTrace());
                    throw updatedex;
                }
                throw ex;
            }
            break;
        }
        return response;
    }

    protected void releaseConnection() {
        try {
            this.managedConn.releaseConnection();
        }
        catch (IOException ignored) {
            this.log.debug((Object)"IOException releasing connection", (Throwable)ignored);
        }
        this.managedConn = null;
    }

    protected HttpRoute determineRoute(HttpHost targetHost, HttpRequest request, HttpContext context) throws HttpException {
        return this.routePlanner.determineRoute(targetHost != null ? targetHost : (HttpHost)request.getParams().getParameter("http.default-host"), request, context);
    }

    protected void establishRoute(HttpRoute route, HttpContext context) throws HttpException, IOException {
        int step;
        BasicRouteDirector rowdy = new BasicRouteDirector();
        do {
            HttpRoute fact = this.managedConn.getRoute();
            step = rowdy.nextStep(route, fact);
            switch (step) {
                case 1: 
                case 2: {
                    this.managedConn.open(route, context, this.params);
                    break;
                }
                case 3: {
                    boolean secure = this.createTunnelToTarget(route, context);
                    this.log.debug((Object)"Tunnel to target created.");
                    this.managedConn.tunnelTarget(secure, this.params);
                    break;
                }
                case 4: {
                    int hop = fact.getHopCount() - 1;
                    boolean secure = this.createTunnelToProxy(route, hop, context);
                    this.log.debug((Object)"Tunnel to proxy created.");
                    this.managedConn.tunnelProxy(route.getHopTarget(hop), secure, this.params);
                    break;
                }
                case 5: {
                    this.managedConn.layerProtocol(context, this.params);
                    break;
                }
                case -1: {
                    throw new HttpException("Unable to establish route: planned = " + route + "; current = " + fact);
                }
                case 0: {
                    break;
                }
                default: {
                    throw new IllegalStateException("Unknown step indicator " + step + " from RouteDirector.");
                }
            }
        } while (step > 0);
    }

    protected boolean createTunnelToTarget(HttpRoute route, HttpContext context) throws HttpException, IOException {
        HttpHost proxy = route.getProxyHost();
        HttpHost target = route.getTargetHost();
        HttpResponse response = null;
        while (true) {
            if (!this.managedConn.isOpen()) {
                this.managedConn.open(route, context, this.params);
            }
            HttpRequest connect = this.createConnectRequest(route, context);
            connect.setParams(this.params);
            context.setAttribute("http.target_host", (Object)target);
            context.setAttribute("http.route", (Object)route);
            context.setAttribute("http.proxy_host", (Object)proxy);
            context.setAttribute("http.connection", (Object)this.managedConn);
            context.setAttribute("http.request", (Object)connect);
            this.requestExec.preProcess(connect, this.httpProcessor, context);
            response = this.requestExec.execute(connect, (HttpClientConnection)this.managedConn, context);
            response.setParams(this.params);
            this.requestExec.postProcess(response, this.httpProcessor, context);
            int status = response.getStatusLine().getStatusCode();
            if (status < 200) {
                throw new HttpException("Unexpected response to CONNECT request: " + response.getStatusLine());
            }
            if (!HttpClientParams.isAuthenticating(this.params)) continue;
            if (!this.authenticator.isAuthenticationRequested(proxy, response, this.proxyAuthStrategy, this.proxyAuthState, context) || !this.authenticator.authenticate(proxy, response, this.proxyAuthStrategy, this.proxyAuthState, context)) break;
            if (this.reuseStrategy.keepAlive(response, context)) {
                this.log.debug((Object)"Connection kept alive");
                HttpEntity entity = response.getEntity();
                EntityUtils.consume((HttpEntity)entity);
                continue;
            }
            this.managedConn.close();
        }
        int status = response.getStatusLine().getStatusCode();
        if (status > 299) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                response.setEntity((HttpEntity)new BufferedHttpEntity(entity));
            }
            this.managedConn.close();
            throw new TunnelRefusedException("CONNECT refused by proxy: " + response.getStatusLine(), response);
        }
        this.managedConn.markReusable();
        return false;
    }

    protected boolean createTunnelToProxy(HttpRoute route, int hop, HttpContext context) throws HttpException, IOException {
        throw new HttpException("Proxy chains are not supported.");
    }

    protected HttpRequest createConnectRequest(HttpRoute route, HttpContext context) {
        HttpHost target = route.getTargetHost();
        String host = target.getHostName();
        int port = target.getPort();
        if (port < 0) {
            Scheme scheme = this.connManager.getSchemeRegistry().getScheme(target.getSchemeName());
            port = scheme.getDefaultPort();
        }
        StringBuilder buffer = new StringBuilder(host.length() + 6);
        buffer.append(host);
        buffer.append(':');
        buffer.append(Integer.toString(port));
        String authority = buffer.toString();
        ProtocolVersion ver = HttpProtocolParams.getVersion((HttpParams)this.params);
        BasicHttpRequest req = new BasicHttpRequest("CONNECT", authority, ver);
        return req;
    }

    protected RoutedRequest handleResponse(RoutedRequest roureq, HttpResponse response, HttpContext context) throws HttpException, IOException {
        HttpRoute route = roureq.getRoute();
        RequestWrapper request = roureq.getRequest();
        HttpParams params = request.getParams();
        if (HttpClientParams.isAuthenticating(params)) {
            HttpHost target = (HttpHost)context.getAttribute("http.target_host");
            if (target == null) {
                target = route.getTargetHost();
            }
            if (target.getPort() < 0) {
                Scheme scheme = this.connManager.getSchemeRegistry().getScheme(target);
                target = new HttpHost(target.getHostName(), scheme.getDefaultPort(), target.getSchemeName());
            }
            boolean targetAuthRequested = this.authenticator.isAuthenticationRequested(target, response, this.targetAuthStrategy, this.targetAuthState, context);
            HttpHost proxy = route.getProxyHost();
            if (proxy == null) {
                proxy = route.getTargetHost();
            }
            boolean proxyAuthRequested = this.authenticator.isAuthenticationRequested(proxy, response, this.proxyAuthStrategy, this.proxyAuthState, context);
            if (targetAuthRequested && this.authenticator.authenticate(target, response, this.targetAuthStrategy, this.targetAuthState, context)) {
                return roureq;
            }
            if (proxyAuthRequested && this.authenticator.authenticate(proxy, response, this.proxyAuthStrategy, this.proxyAuthState, context)) {
                return roureq;
            }
        }
        if (HttpClientParams.isRedirecting(params) && this.redirectStrategy.isRedirected(request, response, context)) {
            if (this.redirectCount >= this.maxRedirects) {
                throw new RedirectException("Maximum redirects (" + this.maxRedirects + ") exceeded");
            }
            ++this.redirectCount;
            this.virtualHost = null;
            HttpUriRequest redirect = this.redirectStrategy.getRedirect(request, response, context);
            HttpRequest orig = request.getOriginal();
            redirect.setHeaders(orig.getAllHeaders());
            URI uri = redirect.getURI();
            HttpHost newTarget = URIUtils.extractHost(uri);
            if (newTarget == null) {
                throw new ProtocolException("Redirect URI does not specify a valid host name: " + uri);
            }
            if (!route.getTargetHost().equals((Object)newTarget)) {
                this.log.debug((Object)"Resetting target auth state");
                this.targetAuthState.reset();
                AuthScheme authScheme = this.proxyAuthState.getAuthScheme();
                if (authScheme != null && authScheme.isConnectionBased()) {
                    this.log.debug((Object)"Resetting proxy auth state");
                    this.proxyAuthState.reset();
                }
            }
            RequestWrapper wrapper = this.wrapRequest(redirect);
            wrapper.setParams(params);
            HttpRoute newRoute = this.determineRoute(newTarget, wrapper, context);
            RoutedRequest newRequest = new RoutedRequest(wrapper, newRoute);
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Redirecting to '" + uri + "' via " + newRoute));
            }
            return newRequest;
        }
        return null;
    }

    private void abortConnection() {
        ManagedClientConnection mcc = this.managedConn;
        if (mcc != null) {
            block5: {
                this.managedConn = null;
                try {
                    mcc.abortConnection();
                }
                catch (IOException ex) {
                    if (!this.log.isDebugEnabled()) break block5;
                    this.log.debug((Object)ex.getMessage(), (Throwable)ex);
                }
            }
            try {
                mcc.releaseConnection();
            }
            catch (IOException ignored) {
                this.log.debug((Object)"Error releasing connection", (Throwable)ignored);
            }
        }
    }
}

