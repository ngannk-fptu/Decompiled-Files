/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.http.HttpException
 *  org.apache.http.HttpHost
 *  org.apache.http.HttpRequest
 *  org.apache.http.HttpResponse
 *  org.apache.http.ProtocolException
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.params.HttpParams
 *  org.apache.http.protocol.HttpContext
 *  org.apache.http.protocol.HttpProcessor
 *  org.apache.http.util.Args
 */
package org.apache.http.impl.execchain;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpExecutionAware;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.execchain.ClientExecChain;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.util.Args;

@Contract(threading=ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class ProtocolExec
implements ClientExecChain {
    private final Log log = LogFactory.getLog(this.getClass());
    private final ClientExecChain requestExecutor;
    private final HttpProcessor httpProcessor;

    public ProtocolExec(ClientExecChain requestExecutor, HttpProcessor httpProcessor) {
        Args.notNull((Object)requestExecutor, (String)"HTTP client request executor");
        Args.notNull((Object)httpProcessor, (String)"HTTP protocol processor");
        this.requestExecutor = requestExecutor;
        this.httpProcessor = httpProcessor;
    }

    void rewriteRequestURI(HttpRequestWrapper request, HttpRoute route, boolean normalizeUri) throws ProtocolException {
        URI uri = request.getURI();
        if (uri != null) {
            try {
                request.setURI(URIUtils.rewriteURIForRoute(uri, route, normalizeUri));
            }
            catch (URISyntaxException ex) {
                throw new ProtocolException("Invalid URI: " + uri, (Throwable)ex);
            }
        }
    }

    @Override
    public CloseableHttpResponse execute(HttpRoute route, HttpRequestWrapper request, HttpClientContext context, HttpExecutionAware execAware) throws IOException, HttpException {
        String userinfo;
        URI uri;
        block18: {
            Args.notNull((Object)route, (String)"HTTP route");
            Args.notNull((Object)request, (String)"HTTP request");
            Args.notNull((Object)((Object)context), (String)"HTTP context");
            HttpRequest original = request.getOriginal();
            uri = null;
            if (original instanceof HttpUriRequest) {
                uri = ((HttpUriRequest)original).getURI();
            } else {
                String uriString = original.getRequestLine().getUri();
                try {
                    uri = URI.create(uriString);
                }
                catch (IllegalArgumentException ex) {
                    if (!this.log.isDebugEnabled()) break block18;
                    this.log.debug((Object)("Unable to parse '" + uriString + "' as a valid URI; " + "request URI and Host header may be inconsistent"), (Throwable)ex);
                }
            }
        }
        request.setURI(uri);
        this.rewriteRequestURI(request, route, context.getRequestConfig().isNormalizeUri());
        HttpParams params = request.getParams();
        HttpHost virtualHost = (HttpHost)params.getParameter("http.virtual-host");
        if (virtualHost != null && virtualHost.getPort() == -1) {
            int port = route.getTargetHost().getPort();
            if (port != -1) {
                virtualHost = new HttpHost(virtualHost.getHostName(), port, virtualHost.getSchemeName());
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Using virtual host" + virtualHost));
            }
        }
        HttpHost target = null;
        if (virtualHost != null) {
            target = virtualHost;
        } else if (uri != null && uri.isAbsolute() && uri.getHost() != null) {
            target = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        }
        if (target == null) {
            target = request.getTarget();
        }
        if (target == null) {
            target = route.getTargetHost();
        }
        if (uri != null && (userinfo = uri.getUserInfo()) != null) {
            CredentialsProvider credsProvider = context.getCredentialsProvider();
            if (credsProvider == null) {
                credsProvider = new BasicCredentialsProvider();
                context.setCredentialsProvider(credsProvider);
            }
            credsProvider.setCredentials(new AuthScope(target), new UsernamePasswordCredentials(userinfo));
        }
        context.setAttribute("http.target_host", target);
        context.setAttribute("http.route", route);
        context.setAttribute("http.request", request);
        this.httpProcessor.process((HttpRequest)request, (HttpContext)context);
        CloseableHttpResponse response = this.requestExecutor.execute(route, request, context, execAware);
        try {
            context.setAttribute("http.response", response);
            this.httpProcessor.process((HttpResponse)response, (HttpContext)context);
            return response;
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
            response.close();
            throw ex;
        }
    }
}

