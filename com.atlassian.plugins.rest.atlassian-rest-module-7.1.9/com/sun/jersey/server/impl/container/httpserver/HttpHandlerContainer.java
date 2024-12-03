/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.container.httpserver;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.core.header.InBoundHeaders;
import com.sun.jersey.spi.container.ContainerListener;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseWriter;
import com.sun.jersey.spi.container.ReloadListener;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.UriBuilder;

public class HttpHandlerContainer
implements HttpHandler,
ContainerListener {
    private WebApplication application;

    public HttpHandlerContainer(WebApplication app) throws ContainerException {
        this.application = app;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        WebApplication _application = this.application;
        URI exchangeUri = exchange.getRequestURI();
        String decodedBasePath = exchange.getHttpContext().getPath();
        if (!decodedBasePath.endsWith("/")) {
            if (decodedBasePath.equals(exchangeUri.getPath())) {
                exchangeUri = UriBuilder.fromUri(exchangeUri).path("/").build(new Object[0]);
            }
            decodedBasePath = decodedBasePath + "/";
        }
        String scheme = exchange instanceof HttpsExchange ? "https" : "http";
        URI baseUri = null;
        try {
            Object hostHeader = exchange.getRequestHeaders().get("Host");
            if (hostHeader != null) {
                StringBuilder sb = new StringBuilder(scheme);
                sb.append("://").append((String)hostHeader.get(0)).append(decodedBasePath);
                baseUri = new URI(sb.toString());
            } else {
                InetSocketAddress addr = exchange.getLocalAddress();
                baseUri = new URI(scheme, null, addr.getHostName(), addr.getPort(), decodedBasePath, null, null);
            }
        }
        catch (URISyntaxException ex) {
            throw new IllegalArgumentException(ex);
        }
        URI requestUri = baseUri.resolve(exchangeUri);
        ContainerRequest cRequest = new ContainerRequest(_application, exchange.getRequestMethod(), baseUri, requestUri, this.getHeaders(exchange), exchange.getRequestBody());
        try {
            _application.handleRequest(cRequest, new Writer(exchange));
        }
        catch (RuntimeException e) {
            e.printStackTrace();
            exchange.getResponseHeaders().clear();
            exchange.sendResponseHeaders(500, -1L);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            exchange.getResponseHeaders().clear();
            exchange.sendResponseHeaders(500, -1L);
            throw ex;
        }
        exchange.getResponseBody().flush();
        exchange.close();
    }

    private InBoundHeaders getHeaders(HttpExchange exchange) {
        InBoundHeaders rh = new InBoundHeaders();
        Headers eh = exchange.getRequestHeaders();
        for (Map.Entry<String, List<String>> e : eh.entrySet()) {
            rh.put(e.getKey(), e.getValue());
        }
        return rh;
    }

    @Override
    public void onReload() {
        WebApplication oldApplication = this.application;
        this.application = this.application.clone();
        if (this.application.getFeaturesAndProperties() instanceof ReloadListener) {
            ((ReloadListener)((Object)this.application.getFeaturesAndProperties())).onReload();
        }
        oldApplication.destroy();
    }

    private static final class Writer
    implements ContainerResponseWriter {
        final HttpExchange exchange;

        Writer(HttpExchange exchange) {
            this.exchange = exchange;
        }

        @Override
        public OutputStream writeStatusAndHeaders(long contentLength, ContainerResponse cResponse) throws IOException {
            Headers eh = this.exchange.getResponseHeaders();
            for (Map.Entry e : cResponse.getHttpHeaders().entrySet()) {
                ArrayList<String> values = new ArrayList<String>();
                for (Object v : (List)e.getValue()) {
                    values.add(ContainerResponse.getHeaderValue(v));
                }
                eh.put((String)e.getKey(), (List<String>)values);
            }
            if (cResponse.getStatus() == 204) {
                this.exchange.sendResponseHeaders(cResponse.getStatus(), -1L);
            } else {
                this.exchange.sendResponseHeaders(cResponse.getStatus(), this.getResponseLength(contentLength));
            }
            return this.exchange.getResponseBody();
        }

        @Override
        public void finish() throws IOException {
        }

        private long getResponseLength(long contentLength) {
            if (contentLength == 0L) {
                return -1L;
            }
            if (contentLength < 0L) {
                return 0L;
            }
            return contentLength;
        }
    }
}

