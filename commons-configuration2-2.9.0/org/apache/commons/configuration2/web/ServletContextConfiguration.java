/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Servlet
 *  javax.servlet.ServletContext
 */
package org.apache.commons.configuration2.web;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import org.apache.commons.configuration2.web.BaseWebConfiguration;

public class ServletContextConfiguration
extends BaseWebConfiguration {
    protected ServletContext context;

    public ServletContextConfiguration(Servlet servlet) {
        this.context = servlet.getServletConfig().getServletContext();
    }

    public ServletContextConfiguration(ServletContext context) {
        this.context = context;
    }

    @Override
    protected Object getPropertyInternal(String key) {
        return this.handleDelimiters(this.context.getInitParameter(key));
    }

    @Override
    protected Iterator<String> getKeysInternal() {
        Enumeration en = this.context.getInitParameterNames();
        return Collections.list(en).iterator();
    }
}

