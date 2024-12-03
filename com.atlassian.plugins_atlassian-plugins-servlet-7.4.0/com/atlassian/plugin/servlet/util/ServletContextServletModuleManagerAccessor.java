/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package com.atlassian.plugin.servlet.util;

import com.atlassian.plugin.servlet.ServletModuleManager;
import javax.servlet.ServletContext;

public class ServletContextServletModuleManagerAccessor {
    private static final String SERVLET_MODULE_MANAGER_KEY = ServletContextServletModuleManagerAccessor.class.getPackage() + ".servletModuleManager";

    public static ServletModuleManager getServletModuleManager(ServletContext servletContext) {
        return (ServletModuleManager)servletContext.getAttribute(SERVLET_MODULE_MANAGER_KEY);
    }

    public static void setServletModuleManager(ServletContext servletContext, ServletModuleManager servletModuleManager) {
        servletContext.setAttribute(SERVLET_MODULE_MANAGER_KEY, (Object)servletModuleManager);
    }
}

