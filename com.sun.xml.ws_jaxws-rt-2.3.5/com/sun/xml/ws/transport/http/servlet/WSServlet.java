/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.Nullable
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.sun.xml.ws.transport.http.servlet;

import com.sun.istack.Nullable;
import com.sun.xml.ws.transport.http.servlet.WSServletDelegate;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WSServlet
extends HttpServlet {
    private transient WSServletDelegate delegate = null;
    public static final String JAXWS_RI_RUNTIME_INFO = "com.sun.xml.ws.server.http.servletDelegate";
    public static final String JAXWS_RI_PROPERTY_PUBLISH_WSDL = "com.sun.xml.ws.server.http.publishWSDL";
    public static final String JAXWS_RI_PROPERTY_PUBLISH_STATUS_PAGE = "com.sun.xml.ws.server.http.publishStatusPage";

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        this.delegate = this.getDelegate(servletConfig);
    }

    @Nullable
    protected WSServletDelegate getDelegate(ServletConfig servletConfig) {
        return (WSServletDelegate)servletConfig.getServletContext().getAttribute(JAXWS_RI_RUNTIME_INFO);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        if (this.delegate != null) {
            this.delegate.doPost(request, response, this.getServletContext());
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        if (this.delegate != null) {
            this.delegate.doGet(request, response, this.getServletContext());
        }
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        if (this.delegate != null) {
            this.delegate.doPut(request, response, this.getServletContext());
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        if (this.delegate != null) {
            this.delegate.doDelete(request, response, this.getServletContext());
        }
    }

    protected void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        if (this.delegate != null) {
            this.delegate.doHead(request, response, this.getServletContext());
        }
    }
}

