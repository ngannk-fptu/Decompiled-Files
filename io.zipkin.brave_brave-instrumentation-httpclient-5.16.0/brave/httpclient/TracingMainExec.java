/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.Span
 *  brave.Tracer
 *  brave.http.HttpClientHandler
 *  brave.http.HttpClientRequest
 *  brave.http.HttpClientResponse
 *  brave.http.HttpTracing
 *  brave.internal.Nullable
 *  brave.propagation.CurrentTraceContext
 *  org.apache.http.HttpException
 *  org.apache.http.HttpHost
 *  org.apache.http.HttpRequest
 *  org.apache.http.client.methods.CloseableHttpResponse
 *  org.apache.http.client.methods.HttpExecutionAware
 *  org.apache.http.client.methods.HttpRequestWrapper
 *  org.apache.http.client.protocol.HttpClientContext
 *  org.apache.http.conn.routing.HttpRoute
 *  org.apache.http.impl.execchain.ClientExecChain
 *  org.apache.http.protocol.HttpContext
 */
package brave.httpclient;

import brave.Span;
import brave.Tracer;
import brave.http.HttpClientHandler;
import brave.http.HttpClientRequest;
import brave.http.HttpClientResponse;
import brave.http.HttpTracing;
import brave.httpclient.TracingProtocolExec;
import brave.internal.Nullable;
import brave.propagation.CurrentTraceContext;
import java.io.IOException;
import java.net.InetAddress;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpExecutionAware;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.execchain.ClientExecChain;
import org.apache.http.protocol.HttpContext;

class TracingMainExec
implements ClientExecChain {
    final Tracer tracer;
    final CurrentTraceContext currentTraceContext;
    final HttpClientHandler<HttpClientRequest, HttpClientResponse> handler;
    @Nullable
    final String serverName;
    final ClientExecChain mainExec;

    TracingMainExec(HttpTracing httpTracing, ClientExecChain mainExec) {
        this.tracer = httpTracing.tracing().tracer();
        this.currentTraceContext = httpTracing.tracing().currentTraceContext();
        this.serverName = "".equals(httpTracing.serverName()) ? null : httpTracing.serverName();
        this.handler = HttpClientHandler.create((HttpTracing)httpTracing);
        this.mainExec = mainExec;
    }

    public CloseableHttpResponse execute(HttpRoute route, HttpRequestWrapper request, HttpClientContext context, HttpExecutionAware execAware) throws IOException, HttpException {
        Span span = (Span)context.removeAttribute(Span.class.getName());
        if (span != null) {
            this.handler.handleSend((HttpClientRequest)new TracingProtocolExec.HttpRequestWrapper((HttpRequest)request, route.getTargetHost()), span);
        }
        CloseableHttpResponse response = this.mainExec.execute(route, request, context, execAware);
        if (span != null) {
            if (this.isRemote((HttpContext)context, span)) {
                if (this.serverName != null) {
                    span.remoteServiceName(this.serverName);
                }
                TracingMainExec.parseTargetAddress(route.getTargetHost(), span);
            } else {
                span.kind(null);
            }
        }
        return response;
    }

    boolean isRemote(HttpContext context, Span span) {
        return true;
    }

    static void parseTargetAddress(HttpHost target, Span span) {
        if (target == null) {
            return;
        }
        InetAddress address = target.getAddress();
        if (address != null && span.remoteIpAndPort(address.getHostAddress(), target.getPort())) {
            return;
        }
        span.remoteIpAndPort(target.getHostName(), target.getPort());
    }
}

