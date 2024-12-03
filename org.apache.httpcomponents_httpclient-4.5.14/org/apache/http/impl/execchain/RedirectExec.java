/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpEntityEnclosingRequest
 *  org.apache.http.HttpException
 *  org.apache.http.HttpHost
 *  org.apache.http.HttpRequest
 *  org.apache.http.ProtocolException
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.protocol.HttpContext
 *  org.apache.http.util.Args
 *  org.apache.http.util.EntityUtils
 */
package org.apache.http.impl.execchain;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.ProtocolException;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.auth.AuthState;
import org.apache.http.client.RedirectException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpExecutionAware;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.execchain.ClientExecChain;
import org.apache.http.impl.execchain.RequestEntityProxy;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;

@Contract(threading=ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class RedirectExec
implements ClientExecChain {
    private final Log log = LogFactory.getLog(this.getClass());
    private final ClientExecChain requestExecutor;
    private final RedirectStrategy redirectStrategy;
    private final HttpRoutePlanner routePlanner;

    public RedirectExec(ClientExecChain requestExecutor, HttpRoutePlanner routePlanner, RedirectStrategy redirectStrategy) {
        Args.notNull((Object)requestExecutor, (String)"HTTP client request executor");
        Args.notNull((Object)routePlanner, (String)"HTTP route planner");
        Args.notNull((Object)redirectStrategy, (String)"HTTP redirect strategy");
        this.requestExecutor = requestExecutor;
        this.routePlanner = routePlanner;
        this.redirectStrategy = redirectStrategy;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public CloseableHttpResponse execute(HttpRoute route, HttpRequestWrapper request, HttpClientContext context, HttpExecutionAware execAware) throws IOException, HttpException {
        RequestConfig config;
        Args.notNull((Object)route, (String)"HTTP route");
        Args.notNull((Object)request, (String)"HTTP request");
        Args.notNull((Object)((Object)context), (String)"HTTP context");
        List<URI> redirectLocations = context.getRedirectLocations();
        if (redirectLocations != null) {
            redirectLocations.clear();
        }
        int maxRedirects = (config = context.getRequestConfig()).getMaxRedirects() > 0 ? config.getMaxRedirects() : 50;
        HttpRoute currentRoute = route;
        HttpRequestWrapper currentRequest = request;
        int redirectCount = 0;
        while (true) {
            CloseableHttpResponse response = this.requestExecutor.execute(currentRoute, currentRequest, context, execAware);
            try {
                URI uri;
                HttpHost newTarget;
                if (!config.isRedirectsEnabled()) return response;
                if (!this.redirectStrategy.isRedirected(currentRequest.getOriginal(), response, (HttpContext)context)) return response;
                if (!RequestEntityProxy.isRepeatable(currentRequest)) {
                    if (!this.log.isDebugEnabled()) return response;
                    this.log.debug((Object)"Cannot redirect non-repeatable request");
                    return response;
                }
                if (redirectCount >= maxRedirects) {
                    throw new RedirectException("Maximum redirects (" + maxRedirects + ") exceeded");
                }
                ++redirectCount;
                HttpUriRequest redirect = this.redirectStrategy.getRedirect(currentRequest.getOriginal(), response, (HttpContext)context);
                if (!redirect.headerIterator().hasNext()) {
                    HttpRequest original = request.getOriginal();
                    redirect.setHeaders(original.getAllHeaders());
                }
                if ((currentRequest = HttpRequestWrapper.wrap(redirect)) instanceof HttpEntityEnclosingRequest) {
                    RequestEntityProxy.enhance((HttpEntityEnclosingRequest)currentRequest);
                }
                if ((newTarget = URIUtils.extractHost(uri = currentRequest.getURI())) == null) {
                    throw new ProtocolException("Redirect URI does not specify a valid host name: " + uri);
                }
                if (!currentRoute.getTargetHost().equals((Object)newTarget)) {
                    AuthState proxyAuthState;
                    AuthState targetAuthState = context.getTargetAuthState();
                    if (targetAuthState != null) {
                        this.log.debug((Object)"Resetting target auth state");
                        targetAuthState.reset();
                    }
                    if ((proxyAuthState = context.getProxyAuthState()) != null && proxyAuthState.isConnectionBased()) {
                        this.log.debug((Object)"Resetting proxy auth state");
                        proxyAuthState.reset();
                    }
                }
                currentRoute = this.routePlanner.determineRoute(newTarget, currentRequest, (HttpContext)context);
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)("Redirecting to '" + uri + "' via " + currentRoute));
                }
                EntityUtils.consume((HttpEntity)response.getEntity());
                response.close();
            }
            catch (RuntimeException ex) {
                response.close();
                throw ex;
            }
            catch (IOException ex) {
                response.close();
                throw ex;
            }
            catch (HttpException ex) {
                try {
                    EntityUtils.consume((HttpEntity)response.getEntity());
                    throw ex;
                }
                catch (IOException ioex) {
                    this.log.debug((Object)"I/O error while releasing connection", (Throwable)ioex);
                    throw ex;
                }
                finally {
                    response.close();
                }
            }
        }
    }
}

