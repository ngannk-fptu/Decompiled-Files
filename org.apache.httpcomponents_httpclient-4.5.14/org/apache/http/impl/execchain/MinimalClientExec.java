/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.http.ConnectionReuseStrategy
 *  org.apache.http.HttpClientConnection
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpException
 *  org.apache.http.HttpHost
 *  org.apache.http.HttpRequest
 *  org.apache.http.HttpRequestInterceptor
 *  org.apache.http.HttpResponse
 *  org.apache.http.ProtocolException
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.protocol.HttpContext
 *  org.apache.http.protocol.HttpProcessor
 *  org.apache.http.protocol.HttpRequestExecutor
 *  org.apache.http.protocol.ImmutableHttpProcessor
 *  org.apache.http.protocol.RequestContent
 *  org.apache.http.protocol.RequestTargetHost
 *  org.apache.http.protocol.RequestUserAgent
 *  org.apache.http.util.Args
 *  org.apache.http.util.VersionInfo
 */
package org.apache.http.impl.execchain;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpExecutionAware;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.protocol.RequestClientConnControl;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.ConnectionRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.ConnectionShutdownException;
import org.apache.http.impl.execchain.ClientExecChain;
import org.apache.http.impl.execchain.ConnectionHolder;
import org.apache.http.impl.execchain.HttpResponseProxy;
import org.apache.http.impl.execchain.RequestAbortedException;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.Args;
import org.apache.http.util.VersionInfo;

@Contract(threading=ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class MinimalClientExec
implements ClientExecChain {
    private final Log log = LogFactory.getLog(this.getClass());
    private final HttpRequestExecutor requestExecutor;
    private final HttpClientConnectionManager connManager;
    private final ConnectionReuseStrategy reuseStrategy;
    private final ConnectionKeepAliveStrategy keepAliveStrategy;
    private final HttpProcessor httpProcessor;

    public MinimalClientExec(HttpRequestExecutor requestExecutor, HttpClientConnectionManager connManager, ConnectionReuseStrategy reuseStrategy, ConnectionKeepAliveStrategy keepAliveStrategy) {
        Args.notNull((Object)requestExecutor, (String)"HTTP request executor");
        Args.notNull((Object)connManager, (String)"Client connection manager");
        Args.notNull((Object)reuseStrategy, (String)"Connection reuse strategy");
        Args.notNull((Object)keepAliveStrategy, (String)"Connection keep alive strategy");
        this.httpProcessor = new ImmutableHttpProcessor(new HttpRequestInterceptor[]{new RequestContent(), new RequestTargetHost(), new RequestClientConnControl(), new RequestUserAgent(VersionInfo.getUserAgent((String)"Apache-HttpClient", (String)"org.apache.http.client", this.getClass()))});
        this.requestExecutor = requestExecutor;
        this.connManager = connManager;
        this.reuseStrategy = reuseStrategy;
        this.keepAliveStrategy = keepAliveStrategy;
    }

    static void rewriteRequestURI(HttpRequestWrapper request, HttpRoute route, boolean normalizeUri) throws ProtocolException {
        try {
            URI uri = request.getURI();
            if (uri != null) {
                uri = uri.isAbsolute() ? URIUtils.rewriteURI(uri, null, normalizeUri ? URIUtils.DROP_FRAGMENT_AND_NORMALIZE : URIUtils.DROP_FRAGMENT) : URIUtils.rewriteURI(uri);
                request.setURI(uri);
            }
        }
        catch (URISyntaxException ex) {
            throw new ProtocolException("Invalid URI: " + request.getRequestLine().getUri(), (Throwable)ex);
        }
    }

    @Override
    public CloseableHttpResponse execute(HttpRoute route, HttpRequestWrapper request, HttpClientContext context, HttpExecutionAware execAware) throws IOException, HttpException {
        HttpClientConnection managedConn;
        Args.notNull((Object)route, (String)"HTTP route");
        Args.notNull((Object)request, (String)"HTTP request");
        Args.notNull((Object)((Object)context), (String)"HTTP context");
        MinimalClientExec.rewriteRequestURI(request, route, context.getRequestConfig().isNormalizeUri());
        ConnectionRequest connRequest = this.connManager.requestConnection(route, null);
        if (execAware != null) {
            if (execAware.isAborted()) {
                connRequest.cancel();
                throw new RequestAbortedException("Request aborted");
            }
            execAware.setCancellable(connRequest);
        }
        RequestConfig config = context.getRequestConfig();
        try {
            int timeout = config.getConnectionRequestTimeout();
            managedConn = connRequest.get(timeout > 0 ? (long)timeout : 0L, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw new RequestAbortedException("Request aborted", interrupted);
        }
        catch (ExecutionException ex) {
            Throwable cause = ex.getCause();
            if (cause == null) {
                cause = ex;
            }
            throw new RequestAbortedException("Request execution failed", cause);
        }
        ConnectionHolder releaseTrigger = new ConnectionHolder(this.log, this.connManager, managedConn);
        try {
            URI uri;
            int timeout;
            if (execAware != null) {
                if (execAware.isAborted()) {
                    releaseTrigger.close();
                    throw new RequestAbortedException("Request aborted");
                }
                execAware.setCancellable(releaseTrigger);
            }
            if (!managedConn.isOpen()) {
                timeout = config.getConnectTimeout();
                this.connManager.connect(managedConn, route, timeout > 0 ? timeout : 0, (HttpContext)context);
                this.connManager.routeComplete(managedConn, route, (HttpContext)context);
            }
            if ((timeout = config.getSocketTimeout()) >= 0) {
                managedConn.setSocketTimeout(timeout);
            }
            HttpHost target = null;
            HttpRequest original = request.getOriginal();
            if (original instanceof HttpUriRequest && (uri = ((HttpUriRequest)original).getURI()).isAbsolute()) {
                target = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
            }
            if (target == null) {
                target = route.getTargetHost();
            }
            context.setAttribute("http.target_host", target);
            context.setAttribute("http.request", request);
            context.setAttribute("http.connection", managedConn);
            context.setAttribute("http.route", route);
            this.httpProcessor.process((HttpRequest)request, (HttpContext)context);
            HttpResponse response = this.requestExecutor.execute((HttpRequest)request, managedConn, (HttpContext)context);
            this.httpProcessor.process(response, (HttpContext)context);
            if (this.reuseStrategy.keepAlive(response, (HttpContext)context)) {
                long duration = this.keepAliveStrategy.getKeepAliveDuration(response, (HttpContext)context);
                releaseTrigger.setValidFor(duration, TimeUnit.MILLISECONDS);
                releaseTrigger.markReusable();
            } else {
                releaseTrigger.markNonReusable();
            }
            HttpEntity entity = response.getEntity();
            if (entity == null || !entity.isStreaming()) {
                releaseTrigger.releaseConnection();
                return new HttpResponseProxy(response, null);
            }
            return new HttpResponseProxy(response, releaseTrigger);
        }
        catch (ConnectionShutdownException ex) {
            InterruptedIOException ioex = new InterruptedIOException("Connection has been shut down");
            ioex.initCause(ex);
            throw ioex;
        }
        catch (HttpException ex) {
            releaseTrigger.abortConnection();
            throw ex;
        }
        catch (IOException ex) {
            releaseTrigger.abortConnection();
            throw ex;
        }
        catch (RuntimeException ex) {
            releaseTrigger.abortConnection();
            throw ex;
        }
        catch (Error error) {
            this.connManager.shutdown();
            throw error;
        }
    }
}

