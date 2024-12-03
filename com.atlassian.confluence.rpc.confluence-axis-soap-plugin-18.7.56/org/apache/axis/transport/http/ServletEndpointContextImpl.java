/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 */
package org.apache.axis.transport.http;

import java.security.Principal;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.rpc.server.ServletEndpointContext;
import org.apache.axis.MessageContext;
import org.apache.axis.transport.http.HTTPConstants;

public class ServletEndpointContextImpl
implements ServletEndpointContext {
    public HttpSession getHttpSession() {
        HttpServletRequest srvreq = (HttpServletRequest)this.getMessageContext().getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);
        return srvreq == null ? null : srvreq.getSession();
    }

    public javax.xml.rpc.handler.MessageContext getMessageContext() {
        return MessageContext.getCurrentContext();
    }

    public ServletContext getServletContext() {
        HttpServlet srv = (HttpServlet)this.getMessageContext().getProperty(HTTPConstants.MC_HTTP_SERVLET);
        return srv == null ? null : srv.getServletContext();
    }

    public boolean isUserInRole(String role) {
        HttpServletRequest srvreq = (HttpServletRequest)this.getMessageContext().getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);
        return srvreq == null ? false : srvreq.isUserInRole(role);
    }

    public Principal getUserPrincipal() {
        HttpServletRequest srvreq = (HttpServletRequest)this.getMessageContext().getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);
        return srvreq == null ? null : srvreq.getUserPrincipal();
    }
}

