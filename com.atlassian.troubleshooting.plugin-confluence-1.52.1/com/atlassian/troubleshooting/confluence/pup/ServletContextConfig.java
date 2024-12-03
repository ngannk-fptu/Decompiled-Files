/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.servlet.ServletContextFactory
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.confluence.pup;

import com.atlassian.plugin.servlet.ServletContextFactory;
import java.util.Enumeration;
import java.util.Objects;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;

public class ServletContextConfig
implements ServletConfig {
    private static final String EXCEPTION_MESSAGE = "Not implemented. It is not expected this method will be invoked.";
    private final ServletContextFactory contextFactory;

    @Autowired
    public ServletContextConfig(ServletContextFactory contextFactory) {
        this.contextFactory = Objects.requireNonNull(contextFactory);
    }

    public String getServletName() {
        throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
    }

    public ServletContext getServletContext() {
        return this.contextFactory.getServletContext();
    }

    public String getInitParameter(String name) {
        throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
    }

    public Enumeration<String> getInitParameterNames() {
        throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
    }
}

