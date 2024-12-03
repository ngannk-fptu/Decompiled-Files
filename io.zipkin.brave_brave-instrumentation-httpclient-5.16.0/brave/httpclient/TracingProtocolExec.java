/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.Span
 *  brave.Tracer
 *  brave.Tracer$SpanInScope
 *  brave.http.HttpClientHandler
 *  brave.http.HttpClientRequest
 *  brave.http.HttpClientResponse
 *  brave.http.HttpRequest
 *  brave.http.HttpTracing
 *  brave.internal.Nullable
 *  brave.sampler.SamplerFunction
 *  org.apache.http.Header
 *  org.apache.http.HttpException
 *  org.apache.http.HttpHost
 *  org.apache.http.HttpRequest
 *  org.apache.http.HttpResponse
 *  org.apache.http.StatusLine
 *  org.apache.http.client.methods.CloseableHttpResponse
 *  org.apache.http.client.methods.HttpExecutionAware
 *  org.apache.http.client.methods.HttpRequestWrapper
 *  org.apache.http.client.protocol.HttpClientContext
 *  org.apache.http.conn.routing.HttpRoute
 *  org.apache.http.impl.execchain.ClientExecChain
 */
package brave.httpclient;

import brave.Span;
import brave.Tracer;
import brave.http.HttpClientHandler;
import brave.http.HttpClientRequest;
import brave.http.HttpClientResponse;
import brave.http.HttpRequest;
import brave.http.HttpTracing;
import brave.internal.Nullable;
import brave.sampler.SamplerFunction;
import java.io.IOException;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpExecutionAware;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.execchain.ClientExecChain;

final class TracingProtocolExec
implements ClientExecChain {
    final Tracer tracer;
    final SamplerFunction<HttpRequest> httpSampler;
    final HttpClientHandler<HttpClientRequest, HttpClientResponse> handler;
    final ClientExecChain protocolExec;

    TracingProtocolExec(HttpTracing httpTracing, ClientExecChain protocolExec) {
        this.tracer = httpTracing.tracing().tracer();
        this.httpSampler = httpTracing.clientRequestSampler();
        this.handler = HttpClientHandler.create((HttpTracing)httpTracing);
        this.protocolExec = protocolExec;
    }

    /*
     * Loose catch block
     */
    public CloseableHttpResponse execute(HttpRoute route, org.apache.http.client.methods.HttpRequestWrapper req, HttpClientContext context, HttpExecutionAware execAware) throws IOException, HttpException {
        CloseableHttpResponse closeableHttpResponse;
        Tracer.SpanInScope ws;
        Throwable error;
        CloseableHttpResponse response;
        Span span;
        block10: {
            HttpRequestWrapper request = new HttpRequestWrapper((org.apache.http.HttpRequest)req, context.getTargetHost());
            span = this.tracer.nextSpan(this.httpSampler, (Object)request);
            context.setAttribute(Span.class.getName(), (Object)span);
            response = null;
            error = null;
            ws = this.tracer.withSpanInScope(span);
            closeableHttpResponse = response = this.protocolExec.execute(route, req, context, execAware);
            if (ws == null) break block10;
            ws.close();
        }
        this.handler.handleReceive((HttpClientResponse)new HttpResponseWrapper((HttpResponse)response, context, error), span);
        return closeableHttpResponse;
        {
            catch (Throwable throwable) {
                try {
                    try {
                        if (ws != null) {
                            try {
                                ws.close();
                            }
                            catch (Throwable throwable2) {
                            }
                        }
                        throw throwable;
                    }
                    catch (Throwable e) {
                        error = e;
                        throw e;
                    }
                }
                catch (Throwable throwable3) {
                    this.handler.handleReceive((HttpClientResponse)new HttpResponseWrapper((HttpResponse)response, context, error), span);
                    throw throwable3;
                }
            }
        }
    }

    static final class HttpResponseWrapper
    extends HttpClientResponse {
        @Nullable
        final HttpRequestWrapper request;
        @Nullable
        final HttpResponse response;
        @Nullable
        final Throwable error;

        HttpResponseWrapper(@Nullable HttpResponse response, HttpClientContext context, @Nullable Throwable error) {
            org.apache.http.HttpRequest request = context.getRequest();
            HttpHost target = context.getTargetHost();
            this.request = request != null ? new HttpRequestWrapper(request, target) : null;
            this.response = response;
            this.error = error;
        }

        public Object unwrap() {
            return this.response;
        }

        @Nullable
        public HttpRequestWrapper request() {
            return this.request;
        }

        public Throwable error() {
            return this.error;
        }

        public int statusCode() {
            if (this.response == null) {
                return 0;
            }
            StatusLine statusLine = this.response.getStatusLine();
            return statusLine != null ? statusLine.getStatusCode() : 0;
        }
    }

    static final class HttpRequestWrapper
    extends HttpClientRequest {
        final org.apache.http.HttpRequest request;
        @Nullable
        final HttpHost target;

        HttpRequestWrapper(org.apache.http.HttpRequest request, @Nullable HttpHost target) {
            this.request = request;
            this.target = target;
        }

        public Object unwrap() {
            return this.request;
        }

        public String method() {
            return this.request.getRequestLine().getMethod();
        }

        public String path() {
            if (this.request instanceof org.apache.http.client.methods.HttpRequestWrapper) {
                return ((org.apache.http.client.methods.HttpRequestWrapper)this.request).getURI().getPath();
            }
            String result = this.request.getRequestLine().getUri();
            int queryIndex = result.indexOf(63);
            return queryIndex == -1 ? result : result.substring(0, queryIndex);
        }

        public String url() {
            if (this.target != null && this.request instanceof org.apache.http.client.methods.HttpRequestWrapper) {
                org.apache.http.client.methods.HttpRequestWrapper wrapper = (org.apache.http.client.methods.HttpRequestWrapper)this.request;
                return this.target.toURI() + wrapper.getURI();
            }
            return this.request.getRequestLine().getUri();
        }

        public String header(String name) {
            Header result = this.request.getFirstHeader(name);
            return result != null ? result.getValue() : null;
        }

        public void header(String name, String value) {
            this.request.setHeader(name, value);
        }
    }
}

