/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 */
package org.tuckey.web.filters.urlrewrite;

import java.util.Enumeration;
import java.util.Hashtable;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class RunConfig
implements ServletConfig,
FilterConfig {
    private ServletContext servletContext;
    private Hashtable initParameters;

    public RunConfig(ServletContext servletContext, Hashtable initParameters) {
        this.servletContext = servletContext;
        this.initParameters = new Hashtable(initParameters);
    }

    public String getServletName() {
        return null;
    }

    public String getFilterName() {
        return null;
    }

    public ServletContext getServletContext() {
        return this.servletContext;
    }

    public String getInitParameter(String s) {
        return (String)this.initParameters.get(s);
    }

    public Enumeration getInitParameterNames() {
        return this.initParameters.keys();
    }
}

