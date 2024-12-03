/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package com.sun.xml.ws.transport.http.servlet;

import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.transport.http.HttpAdapterList;
import com.sun.xml.ws.transport.http.servlet.ServletAdapter;
import javax.servlet.ServletContext;

public class ServletAdapterList
extends HttpAdapterList<ServletAdapter> {
    private final ServletContext context;

    @Deprecated
    public ServletAdapterList() {
        this.context = null;
    }

    public ServletAdapterList(ServletContext ctxt) {
        this.context = ctxt;
    }

    ServletContext getServletContext() {
        return this.context;
    }

    @Override
    protected ServletAdapter createHttpAdapter(String name, String urlPattern, WSEndpoint<?> endpoint) {
        return new ServletAdapter(name, urlPattern, endpoint, this);
    }
}

