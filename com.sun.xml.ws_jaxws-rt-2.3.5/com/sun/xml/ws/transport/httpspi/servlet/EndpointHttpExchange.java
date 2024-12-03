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

import com.sun.xml.ws.transport.httpspi.servlet.ExchangeRequestHeaders;
import com.sun.xml.ws.transport.httpspi.servlet.ExchangeResponseHeaders;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.spi.http.HttpContext;
import javax.xml.ws.spi.http.HttpExchange;

final class EndpointHttpExchange
extends HttpExchange {
    private final HttpServletRequest req;
    private final HttpServletResponse res;
    private final ExchangeRequestHeaders reqHeaders;
    private final ExchangeResponseHeaders resHeaders;
    private final ServletContext servletContext;
    private final HttpContext httpContext;
    private static final Set<String> attributes = new HashSet<String>();

    EndpointHttpExchange(HttpServletRequest req, HttpServletResponse res, ServletContext servletContext, HttpContext httpContext) {
        this.req = req;
        this.res = res;
        this.servletContext = servletContext;
        this.httpContext = httpContext;
        this.reqHeaders = new ExchangeRequestHeaders(req);
        this.resHeaders = new ExchangeResponseHeaders(res);
    }

    public Map<String, List<String>> getRequestHeaders() {
        return this.reqHeaders;
    }

    public Map<String, List<String>> getResponseHeaders() {
        return this.resHeaders;
    }

    public String getRequestURI() {
        return this.req.getRequestURI();
    }

    public String getContextPath() {
        return this.req.getContextPath();
    }

    public String getRequestMethod() {
        return this.req.getMethod();
    }

    public HttpContext getHttpContext() {
        return this.httpContext;
    }

    public void close() throws IOException {
    }

    public String getRequestHeader(String name) {
        return this.reqHeaders.getFirst(name);
    }

    public void addResponseHeader(String name, String value) {
        this.resHeaders.add(name, value);
    }

    public InputStream getRequestBody() throws IOException {
        return this.req.getInputStream();
    }

    public OutputStream getResponseBody() throws IOException {
        return this.res.getOutputStream();
    }

    public void setStatus(int rCode) {
        this.res.setStatus(rCode);
    }

    public InetSocketAddress getRemoteAddress() {
        return null;
    }

    public InetSocketAddress getLocalAddress() {
        return InetSocketAddress.createUnresolved(this.req.getServerName(), this.req.getServerPort());
    }

    public String getProtocol() {
        return this.req.getProtocol();
    }

    public Object getAttribute(String name) {
        if (name.equals("javax.xml.ws.servlet.context")) {
            return this.servletContext;
        }
        if (name.equals("javax.xml.ws.servlet.request")) {
            return this.req;
        }
        if (name.equals("javax.xml.ws.servlet.response")) {
            return this.res;
        }
        return null;
    }

    public Set<String> getAttributeNames() {
        return attributes;
    }

    public Principal getUserPrincipal() {
        return this.req.getUserPrincipal();
    }

    public boolean isUserInRole(String role) {
        return this.req.isUserInRole(role);
    }

    public String getScheme() {
        return this.req.getScheme();
    }

    public String getPathInfo() {
        return this.req.getPathInfo();
    }

    public String getQueryString() {
        return this.req.getQueryString();
    }

    static {
        attributes.add("javax.xml.ws.servlet.context");
        attributes.add("javax.xml.ws.servlet.request");
        attributes.add("javax.xml.ws.servlet.response");
    }
}

