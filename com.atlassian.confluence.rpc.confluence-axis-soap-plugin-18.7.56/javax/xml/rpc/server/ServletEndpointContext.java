/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpSession
 */
package javax.xml.rpc.server;

import java.security.Principal;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.xml.rpc.handler.MessageContext;

public interface ServletEndpointContext {
    public MessageContext getMessageContext();

    public Principal getUserPrincipal();

    public HttpSession getHttpSession();

    public ServletContext getServletContext();

    public boolean isUserInRole(String var1);
}

