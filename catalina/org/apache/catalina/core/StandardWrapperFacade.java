/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 */
package org.apache.catalina.core;

import java.util.Enumeration;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.StandardWrapper;

public final class StandardWrapperFacade
implements ServletConfig {
    private final ServletConfig config;
    private ServletContext context = null;

    public StandardWrapperFacade(StandardWrapper config) {
        this.config = config;
    }

    public String getServletName() {
        return this.config.getServletName();
    }

    public ServletContext getServletContext() {
        if (this.context == null) {
            this.context = this.config.getServletContext();
            if (this.context instanceof ApplicationContext) {
                this.context = ((ApplicationContext)this.context).getFacade();
            }
        }
        return this.context;
    }

    public String getInitParameter(String name) {
        return this.config.getInitParameter(name);
    }

    public Enumeration<String> getInitParameterNames() {
        return this.config.getInitParameterNames();
    }
}

