/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.servlet.ServletContextFactory
 */
package com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.stream;

import com.atlassian.plugin.servlet.ServletContextFactory;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.stream.StreamStrategy;
import java.io.InputStream;

public class TomcatStreamStrategy
implements StreamStrategy {
    private final ServletContextFactory servletContextFactory;

    TomcatStreamStrategy(ServletContextFactory servletContextFactory) {
        this.servletContextFactory = servletContextFactory;
    }

    @Override
    public InputStream getInputStream(String path) {
        String pathWithSlash = path.startsWith("/") ? path : "/" + path;
        return this.servletContextFactory.getServletContext().getResourceAsStream(pathWithSlash);
    }
}

