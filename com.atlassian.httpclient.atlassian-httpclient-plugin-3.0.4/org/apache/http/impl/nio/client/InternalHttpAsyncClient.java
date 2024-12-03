/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.client;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthState;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.concurrent.BasicFuture;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.config.Lookup;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClientBase;
import org.apache.http.impl.nio.client.DefaultClientExchangeHandlerImpl;
import org.apache.http.impl.nio.client.FutureWrapper;
import org.apache.http.impl.nio.client.InternalClientExec;
import org.apache.http.nio.NHttpClientEventHandler;
import org.apache.http.nio.conn.NHttpClientConnectionManager;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

class InternalHttpAsyncClient
extends CloseableHttpAsyncClientBase {
    private final Log log = LogFactory.getLog(this.getClass());
    private final NHttpClientConnectionManager connmgr;
    private final ConnectionReuseStrategy connReuseStrategy;
    private final ConnectionKeepAliveStrategy keepaliveStrategy;
    private final InternalClientExec exec;
    private final Lookup<CookieSpecProvider> cookieSpecRegistry;
    private final Lookup<AuthSchemeProvider> authSchemeRegistry;
    private final CookieStore cookieStore;
    private final CredentialsProvider credentialsProvider;
    private final RequestConfig defaultConfig;

    public InternalHttpAsyncClient(NHttpClientConnectionManager connmgr, ConnectionReuseStrategy connReuseStrategy, ConnectionKeepAliveStrategy keepaliveStrategy, ThreadFactory threadFactory, NHttpClientEventHandler handler, InternalClientExec exec, Lookup<CookieSpecProvider> cookieSpecRegistry, Lookup<AuthSchemeProvider> authSchemeRegistry, CookieStore cookieStore, CredentialsProvider credentialsProvider, RequestConfig defaultConfig) {
        super(connmgr, threadFactory, handler);
        this.connmgr = connmgr;
        this.connReuseStrategy = connReuseStrategy;
        this.keepaliveStrategy = keepaliveStrategy;
        this.exec = exec;
        this.cookieSpecRegistry = cookieSpecRegistry;
        this.authSchemeRegistry = authSchemeRegistry;
        this.cookieStore = cookieStore;
        this.credentialsProvider = credentialsProvider;
        this.defaultConfig = defaultConfig;
    }

    private void setupContext(HttpClientContext context) {
        if (context.getAttribute("http.auth.target-scope") == null) {
            context.setAttribute("http.auth.target-scope", new AuthState());
        }
        if (context.getAttribute("http.auth.proxy-scope") == null) {
            context.setAttribute("http.auth.proxy-scope", new AuthState());
        }
        if (context.getAttribute("http.authscheme-registry") == null) {
            context.setAttribute("http.authscheme-registry", this.authSchemeRegistry);
        }
        if (context.getAttribute("http.cookiespec-registry") == null) {
            context.setAttribute("http.cookiespec-registry", this.cookieSpecRegistry);
        }
        if (context.getAttribute("http.cookie-store") == null) {
            context.setAttribute("http.cookie-store", this.cookieStore);
        }
        if (context.getAttribute("http.auth.credentials-provider") == null) {
            context.setAttribute("http.auth.credentials-provider", this.credentialsProvider);
        }
        if (context.getAttribute("http.request-config") == null) {
            context.setAttribute("http.request-config", this.defaultConfig);
        }
    }

    @Override
    public <T> Future<T> execute(HttpAsyncRequestProducer requestProducer, HttpAsyncResponseConsumer<T> responseConsumer, HttpContext context, FutureCallback<T> callback) {
        BasicFuture<T> future = new BasicFuture<T>(callback);
        HttpClientContext localcontext = HttpClientContext.adapt(context != null ? context : new BasicHttpContext());
        this.setupContext(localcontext);
        DefaultClientExchangeHandlerImpl<T> handler = new DefaultClientExchangeHandlerImpl<T>(this.log, requestProducer, responseConsumer, localcontext, future, this.connmgr, this.connReuseStrategy, this.keepaliveStrategy, this.exec);
        this.execute(handler);
        return new FutureWrapper<T>(future, handler);
    }

    @Override
    public <T> Future<List<T>> execute(HttpHost target, List<? extends HttpAsyncRequestProducer> requestProducers, List<? extends HttpAsyncResponseConsumer<T>> responseConsumers, HttpContext context, FutureCallback<List<T>> callback) {
        throw new UnsupportedOperationException("Pipelining not supported");
    }
}

