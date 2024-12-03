/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.Tracing
 *  brave.http.HttpTracing
 *  org.apache.http.impl.client.HttpClientBuilder
 *  org.apache.http.impl.execchain.ClientExecChain
 */
package brave.httpclient;

import brave.Tracing;
import brave.http.HttpTracing;
import brave.httpclient.TracingMainExec;
import brave.httpclient.TracingProtocolExec;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.execchain.ClientExecChain;

public final class TracingHttpClientBuilder
extends HttpClientBuilder {
    final HttpTracing httpTracing;

    public static HttpClientBuilder create(Tracing tracing) {
        return new TracingHttpClientBuilder(HttpTracing.create((Tracing)tracing));
    }

    public static HttpClientBuilder create(HttpTracing httpTracing) {
        return new TracingHttpClientBuilder(httpTracing);
    }

    TracingHttpClientBuilder(HttpTracing httpTracing) {
        if (httpTracing == null) {
            throw new NullPointerException("HttpTracing == null");
        }
        this.httpTracing = httpTracing;
    }

    protected ClientExecChain decorateProtocolExec(ClientExecChain protocolExec) {
        return new TracingProtocolExec(this.httpTracing, protocolExec);
    }

    protected ClientExecChain decorateMainExec(ClientExecChain exec) {
        return new TracingMainExec(this.httpTracing, exec);
    }
}

