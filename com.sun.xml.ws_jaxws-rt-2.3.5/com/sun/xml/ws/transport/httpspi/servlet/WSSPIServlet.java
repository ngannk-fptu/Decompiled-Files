/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.sun.xml.ws.transport.httpspi.servlet;

import com.sun.xml.ws.transport.httpspi.servlet.WSServletDelegate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WSSPIServlet
extends HttpServlet {
    private transient WSServletDelegate delegate = null;
    private static final Logger LOGGER = Logger.getLogger(WSSPIServlet.class.getName());
    public static final String JAXWS_RI_RUNTIME_INFO = "com.sun.xml.ws.server.http.servletDelegate";
    public static final String JAXWS_RI_PROPERTY_PUBLISH_WSDL = "com.sun.xml.ws.server.http.publishWSDL";
    public static final String JAXWS_RI_PROPERTY_PUBLISH_STATUS_PAGE = "com.sun.xml.ws.server.http.publishStatusPage";

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        this.delegate = this.getDelegate(servletConfig);
    }

    protected WSServletDelegate getDelegate(ServletConfig servletConfig) {
        return (WSServletDelegate)servletConfig.getServletContext().getAttribute(JAXWS_RI_RUNTIME_INFO);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        if (this.delegate != null) {
            this.delegate.doPost(request, response, this.getServletContext());
        } else if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "No delegate for {0} to invoke post method.", (Object)this);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        if (this.delegate != null) {
            this.delegate.doGet(request, response, this.getServletContext());
        } else if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "No delegate for {0} to invoke get method.", (Object)this);
        }
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        if (this.delegate != null) {
            this.delegate.doPut(request, response, this.getServletContext());
        } else if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "No delegate for {0} to invoke put method.", (Object)this);
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        if (this.delegate != null) {
            this.delegate.doDelete(request, response, this.getServletContext());
        } else if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "No delegate for {0} to invoke delete method.", (Object)this);
        }
    }
}

