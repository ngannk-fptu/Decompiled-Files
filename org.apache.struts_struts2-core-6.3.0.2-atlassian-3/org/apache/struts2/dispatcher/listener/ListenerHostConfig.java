/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package org.apache.struts2.dispatcher.listener;

import java.util.Collections;
import java.util.Iterator;
import javax.servlet.ServletContext;
import org.apache.struts2.dispatcher.HostConfig;

public class ListenerHostConfig
implements HostConfig {
    private ServletContext servletContext;

    public ListenerHostConfig(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public String getInitParameter(String key) {
        return null;
    }

    @Override
    public Iterator<String> getInitParameterNames() {
        return Collections.emptyList().iterator();
    }

    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }
}

