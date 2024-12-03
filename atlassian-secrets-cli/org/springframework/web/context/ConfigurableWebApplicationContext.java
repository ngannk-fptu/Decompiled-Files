/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 */
package org.springframework.web.context;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.web.context.WebApplicationContext;

public interface ConfigurableWebApplicationContext
extends WebApplicationContext,
ConfigurableApplicationContext {
    public static final String APPLICATION_CONTEXT_ID_PREFIX = WebApplicationContext.class.getName() + ":";
    public static final String SERVLET_CONFIG_BEAN_NAME = "servletConfig";

    public void setServletContext(@Nullable ServletContext var1);

    public void setServletConfig(@Nullable ServletConfig var1);

    @Nullable
    public ServletConfig getServletConfig();

    public void setNamespace(@Nullable String var1);

    @Nullable
    public String getNamespace();

    public void setConfigLocation(String var1);

    public void setConfigLocations(String ... var1);

    @Nullable
    public String[] getConfigLocations();
}

