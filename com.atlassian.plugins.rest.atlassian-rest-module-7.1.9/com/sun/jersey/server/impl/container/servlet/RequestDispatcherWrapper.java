/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.RequestDispatcher
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 */
package com.sun.jersey.server.impl.container.servlet;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.view.Viewable;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public final class RequestDispatcherWrapper
implements RequestDispatcher {
    private final RequestDispatcher d;
    private final String basePath;
    private final HttpContext hc;
    private final Viewable v;

    public RequestDispatcherWrapper(RequestDispatcher d, String basePath, HttpContext hc, Viewable v) {
        this.d = d;
        this.basePath = basePath;
        this.hc = hc;
        this.v = v;
    }

    public void forward(ServletRequest req, ServletResponse rsp) throws ServletException, IOException {
        Object oldIt = req.getAttribute("it");
        Object oldResolvingClass = req.getAttribute("resolvingClass");
        req.setAttribute("resolvingClass", this.v.getResolvingClass());
        req.setAttribute("it", this.v.getModel());
        req.setAttribute("httpContext", (Object)this.hc);
        req.setAttribute("_basePath", (Object)this.basePath);
        req.setAttribute("_request", (Object)req);
        req.setAttribute("_response", (Object)rsp);
        this.d.forward(req, rsp);
        req.setAttribute("resolvingClass", oldResolvingClass);
        req.setAttribute("it", oldIt);
    }

    public void include(ServletRequest req, ServletResponse rsp) throws ServletException, IOException {
        throw new UnsupportedOperationException();
    }
}

