/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package com.sun.xml.ws.transport.httpspi.servlet;

import com.sun.xml.ws.transport.httpspi.servlet.ResourceLoader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import javax.servlet.ServletContext;

public final class ServletResourceLoader
implements ResourceLoader {
    private final ServletContext context;

    public ServletResourceLoader(ServletContext context) {
        this.context = context;
    }

    @Override
    public URL getResource(String path) throws MalformedURLException {
        return this.context.getResource(path);
    }

    @Override
    public URL getCatalogFile() throws MalformedURLException {
        return this.getResource("/WEB-INF/jax-ws-catalog.xml");
    }

    @Override
    public Set<String> getResourcePaths(String path) {
        return this.context.getResourcePaths(path);
    }
}

