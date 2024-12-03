/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 */
package org.apache.struts2.dispatcher.servlet;

import java.util.Iterator;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import org.apache.struts2.dispatcher.HostConfig;
import org.apache.struts2.util.MakeIterator;

public class ServletHostConfig
implements HostConfig {
    private ServletConfig config;

    public ServletHostConfig(ServletConfig config) {
        this.config = config;
    }

    @Override
    public String getInitParameter(String key) {
        return this.config.getInitParameter(key);
    }

    @Override
    public Iterator<String> getInitParameterNames() {
        return MakeIterator.convert(this.config.getInitParameterNames());
    }

    @Override
    public ServletContext getServletContext() {
        return this.config.getServletContext();
    }
}

