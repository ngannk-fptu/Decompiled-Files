/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.xml.ws.Endpoint
 *  javax.xml.ws.spi.http.HttpContext
 */
package com.sun.xml.ws.transport.httpspi.servlet;

import com.sun.xml.ws.transport.httpspi.servlet.EndpointHttpContext;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Endpoint;
import javax.xml.ws.spi.http.HttpContext;

public final class EndpointAdapter {
    private final Endpoint endpoint;
    private final String urlPattern;
    private final EndpointHttpContext httpContext;

    public EndpointAdapter(Endpoint endpoint, String urlPattern) {
        this.endpoint = endpoint;
        this.urlPattern = urlPattern;
        this.httpContext = new EndpointHttpContext(urlPattern);
    }

    public Endpoint getEndpoint() {
        return this.endpoint;
    }

    public HttpContext getContext() {
        return this.httpContext;
    }

    public void publish() {
        this.endpoint.publish((HttpContext)this.httpContext);
    }

    public void dispose() {
        this.endpoint.stop();
    }

    public String getUrlPattern() {
        return this.urlPattern;
    }

    public void handle(ServletContext context, HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.httpContext.handle(context, request, response);
    }

    public String getValidPath() {
        if (this.urlPattern.endsWith("/*")) {
            return this.urlPattern.substring(0, this.urlPattern.length() - 2);
        }
        return this.urlPattern;
    }
}

