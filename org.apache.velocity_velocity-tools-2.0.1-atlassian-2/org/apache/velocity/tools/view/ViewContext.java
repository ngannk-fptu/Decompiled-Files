/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.velocity.app.VelocityEngine
 *  org.apache.velocity.context.Context
 */
package org.apache.velocity.tools.view;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.VelocityView;

public interface ViewContext {
    public static final String REQUEST = "request";
    public static final String RESPONSE = "response";
    public static final String SESSION = "session";
    public static final String APPLICATION = "application";
    public static final String SERVLET_CONTEXT_KEY = "servletContext";
    public static final String DEFAULT_TOOLBOX_KEY = VelocityView.DEFAULT_TOOLBOX_KEY;

    public HttpServletRequest getRequest();

    public HttpServletResponse getResponse();

    public ServletContext getServletContext();

    public Object getAttribute(String var1);

    public Context getVelocityContext();

    public VelocityEngine getVelocityEngine();
}

