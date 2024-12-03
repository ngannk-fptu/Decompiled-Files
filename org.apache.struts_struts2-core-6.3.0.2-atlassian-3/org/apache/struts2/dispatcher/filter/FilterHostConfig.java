/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletContext
 */
package org.apache.struts2.dispatcher.filter;

import java.util.Iterator;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import org.apache.struts2.dispatcher.HostConfig;
import org.apache.struts2.util.MakeIterator;

public class FilterHostConfig
implements HostConfig {
    private FilterConfig config;

    public FilterHostConfig(FilterConfig config) {
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

