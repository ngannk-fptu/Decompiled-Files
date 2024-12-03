/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Servlet
 *  javax.servlet.ServletConfig
 */
package org.apache.commons.configuration2.web;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import org.apache.commons.configuration2.web.BaseWebConfiguration;

public class ServletConfiguration
extends BaseWebConfiguration {
    protected ServletConfig config;

    public ServletConfiguration(Servlet servlet) {
        this(servlet.getServletConfig());
    }

    public ServletConfiguration(ServletConfig config) {
        this.config = config;
    }

    @Override
    protected Object getPropertyInternal(String key) {
        return this.handleDelimiters(this.config.getInitParameter(key));
    }

    @Override
    protected Iterator<String> getKeysInternal() {
        Enumeration en = this.config.getInitParameterNames();
        return Collections.list(en).iterator();
    }
}

