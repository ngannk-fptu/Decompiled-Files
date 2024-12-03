/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.Internal
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.concurrent.CancellableDependency
 *  org.apache.hc.core5.http.ClassicHttpRequest
 *  org.apache.hc.core5.http.ClassicHttpResponse
 *  org.apache.hc.core5.http.HttpException
 *  org.apache.hc.core5.http.HttpHost
 *  org.apache.hc.core5.http.HttpRequest
 *  org.apache.hc.core5.http.config.Lookup
 *  org.apache.hc.core5.http.impl.io.HttpRequestExecutor
 *  org.apache.hc.core5.http.io.support.ClassicRequestBuilder
 *  org.apache.hc.core5.http.protocol.BasicHttpContext
 *  org.apache.hc.core5.http.protocol.HttpContext
 *  org.apache.hc.core5.io.CloseMode
 *  org.apache.hc.core5.io.ModalCloseable
 *  org.apache.hc.core5.util.Args
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.hc.client5.http.impl.classic;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.auth.AuthSchemeFactory;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.classic.ExecChain;
import org.apache.hc.client5.http.config.Configurable;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.CookieSpecFactory;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.ExecSupport;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.ExecChainElement;
import org.apache.hc.client5.http.impl.classic.InternalExecRuntime;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.routing.HttpRoutePlanner;
import org.apache.hc.client5.http.routing.RoutingSupport;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.concurrent.CancellableDependency;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.config.Lookup;
import org.apache.hc.core5.http.impl.io.HttpRequestExecutor;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.http.protocol.BasicHttpContext;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.io.ModalCloseable;
import org.apache.hc.core5.util.Args;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Contract(threading=ThreadingBehavior.SAFE_CONDITIONAL)
@Internal
class InternalHttpClient
extends CloseableHttpClient
implements Configurable {
    private static final Logger LOG = LoggerFactory.getLogger(InternalHttpClient.class);
    private final HttpClientConnectionManager connManager;
    private final HttpRequestExecutor requestExecutor;
    private final ExecChainElement execChain;
    private final HttpRoutePlanner routePlanner;
    private final Lookup<CookieSpecFactory> cookieSpecRegistry;
    private final Lookup<AuthSchemeFactory> authSchemeRegistry;
    private final CookieStore cookieStore;
    private final CredentialsProvider credentialsProvider;
    private final RequestConfig defaultConfig;
    private final ConcurrentLinkedQueue<Closeable> closeables;

    public InternalHttpClient(HttpClientConnectionManager connManager, HttpRequestExecutor requestExecutor, ExecChainElement execChain, HttpRoutePlanner routePlanner, Lookup<CookieSpecFactory> cookieSpecRegistry, Lookup<AuthSchemeFactory> authSchemeRegistry, CookieStore cookieStore, CredentialsProvider credentialsProvider, RequestConfig defaultConfig, List<Closeable> closeables) {
        this.connManager = (HttpClientConnectionManager)Args.notNull((Object)connManager, (String)"Connection manager");
        this.requestExecutor = (HttpRequestExecutor)Args.notNull((Object)requestExecutor, (String)"Request executor");
        this.execChain = (ExecChainElement)Args.notNull((Object)execChain, (String)"Execution chain");
        this.routePlanner = (HttpRoutePlanner)Args.notNull((Object)routePlanner, (String)"Route planner");
        this.cookieSpecRegistry = cookieSpecRegistry;
        this.authSchemeRegistry = authSchemeRegistry;
        this.cookieStore = cookieStore;
        this.credentialsProvider = credentialsProvider;
        this.defaultConfig = defaultConfig;
        this.closeables = closeables != null ? new ConcurrentLinkedQueue<Closeable>(closeables) : null;
    }

    private HttpRoute determineRoute(HttpHost target, HttpContext context) throws HttpException {
        return this.routePlanner.determineRoute(target, context);
    }

    private void setupContext(HttpClientContext context) {
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
    protected CloseableHttpResponse doExecute(HttpHost target, ClassicHttpRequest request, HttpContext context) throws IOException {
        Args.notNull((Object)request, (String)"HTTP request");
        try {
            HttpClientContext localcontext = HttpClientContext.adapt((HttpContext)(context != null ? context : new BasicHttpContext()));
            RequestConfig config = null;
            if (request instanceof Configurable) {
                config = ((Configurable)request).getConfig();
            }
            if (config != null) {
                localcontext.setRequestConfig(config);
            }
            this.setupContext(localcontext);
            HttpRoute route = this.determineRoute(target != null ? target : RoutingSupport.determineHost((HttpRequest)request), (HttpContext)localcontext);
            String exchangeId = ExecSupport.getNextExchangeId();
            localcontext.setExchangeId(exchangeId);
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} preparing request execution", (Object)exchangeId);
            }
            InternalExecRuntime execRuntime = new InternalExecRuntime(LOG, this.connManager, this.requestExecutor, request instanceof CancellableDependency ? (CancellableDependency)request : null);
            ExecChain.Scope scope = new ExecChain.Scope(exchangeId, route, request, execRuntime, localcontext);
            ClassicHttpResponse response = this.execChain.execute(ClassicRequestBuilder.copy((ClassicHttpRequest)request).build(), scope);
            return CloseableHttpResponse.adapt(response);
        }
        catch (HttpException httpException) {
            throw new ClientProtocolException(httpException.getMessage(), httpException);
        }
    }

    @Override
    public RequestConfig getConfig() {
        return this.defaultConfig;
    }

    public void close() {
        this.close(CloseMode.GRACEFUL);
    }

    public void close(CloseMode closeMode) {
        if (this.closeables != null) {
            Closeable closeable;
            while ((closeable = this.closeables.poll()) != null) {
                try {
                    if (closeable instanceof ModalCloseable) {
                        ((ModalCloseable)closeable).close(closeMode);
                        continue;
                    }
                    closeable.close();
                }
                catch (IOException ex) {
                    LOG.error(ex.getMessage(), (Throwable)ex);
                }
            }
        }
    }
}

