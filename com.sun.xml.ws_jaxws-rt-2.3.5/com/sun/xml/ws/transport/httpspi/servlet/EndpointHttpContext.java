/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.xml.ws.spi.http.HttpContext
 *  javax.xml.ws.spi.http.HttpExchange
 */
package com.sun.xml.ws.transport.httpspi.servlet;

import com.sun.xml.ws.transport.httpspi.servlet.EndpointHttpExchange;
import java.io.IOException;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.spi.http.HttpContext;
import javax.xml.ws.spi.http.HttpExchange;

public final class EndpointHttpContext
extends HttpContext {
    private final String urlPattern;

    public EndpointHttpContext(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    void handle(ServletContext context, HttpServletRequest request, HttpServletResponse response) throws IOException {
        EndpointHttpExchange exchange = new EndpointHttpExchange(request, response, context, this);
        this.handler.handle((HttpExchange)exchange);
    }

    public String getPath() {
        return this.urlPattern;
    }

    public Object getAttribute(String name) {
        return null;
    }

    public Set<String> getAttributeNames() {
        return null;
    }
}

