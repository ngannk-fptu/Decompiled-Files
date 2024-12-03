/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.RequestDispatcher
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.sun.jersey.server.impl.container.servlet;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.server.impl.container.servlet.RequestDispatcherWrapper;
import com.sun.jersey.spi.template.ViewProcessor;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;

public class JSPTemplateProcessor
implements ViewProcessor<String> {
    @Context
    private HttpContext hc;
    @Context
    private ServletContext servletContext;
    @Context
    private ThreadLocal<HttpServletRequest> requestInvoker;
    @Context
    private ThreadLocal<HttpServletResponse> responseInvoker;
    private final String basePath;

    public JSPTemplateProcessor(@Context ResourceConfig resourceConfig) {
        String path = (String)resourceConfig.getProperties().get("com.sun.jersey.config.property.JSPTemplatesBasePath");
        this.basePath = path == null ? "" : (path.charAt(0) == '/' ? path : "/" + path);
    }

    @Override
    public String resolve(String path) {
        if (this.servletContext == null) {
            return null;
        }
        if (this.basePath != "") {
            path = this.basePath + path;
        }
        try {
            if (this.servletContext.getResource(path) != null) {
                return path;
            }
            if (!path.endsWith(".jsp") && this.servletContext.getResource(path = path + ".jsp") != null) {
                return path;
            }
        }
        catch (MalformedURLException malformedURLException) {
            // empty catch block
        }
        return null;
    }

    @Override
    public void writeTo(String resolvedPath, Viewable viewable, OutputStream out) throws IOException {
        if (this.hc.isTracingEnabled()) {
            this.hc.trace(String.format("forwarding view to JSP page: \"%s\", it = %s", resolvedPath, ReflectionHelper.objectToString(viewable.getModel())));
        }
        out.flush();
        RequestDispatcher d = this.servletContext.getRequestDispatcher(resolvedPath);
        if (d == null) {
            throw new ContainerException("No request dispatcher for: " + resolvedPath);
        }
        d = new RequestDispatcherWrapper(d, this.basePath, this.hc, viewable);
        try {
            d.forward((ServletRequest)this.requestInvoker.get(), (ServletResponse)this.responseInvoker.get());
        }
        catch (Exception e) {
            throw new ContainerException(e);
        }
    }
}

