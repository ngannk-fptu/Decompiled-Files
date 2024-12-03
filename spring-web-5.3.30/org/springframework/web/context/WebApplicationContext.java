/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.springframework.context.ApplicationContext
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.context;

import javax.servlet.ServletContext;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;

public interface WebApplicationContext
extends ApplicationContext {
    public static final String ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE = WebApplicationContext.class.getName() + ".ROOT";
    public static final String SCOPE_REQUEST = "request";
    public static final String SCOPE_SESSION = "session";
    public static final String SCOPE_APPLICATION = "application";
    public static final String SERVLET_CONTEXT_BEAN_NAME = "servletContext";
    public static final String CONTEXT_PARAMETERS_BEAN_NAME = "contextParameters";
    public static final String CONTEXT_ATTRIBUTES_BEAN_NAME = "contextAttributes";

    @Nullable
    public ServletContext getServletContext();
}

