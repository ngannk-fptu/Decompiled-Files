/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.servlet.ServletContext
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.transport.http.servlet;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.ResourceLoader;
import com.sun.xml.ws.api.server.BoundEndpoint;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.transport.http.servlet.ServletModule;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceException;

class ServletContainer
extends Container {
    private final ServletContext servletContext;
    private final ServletModule module = new ServletModule(){
        private final List<BoundEndpoint> endpoints = new ArrayList<BoundEndpoint>();

        @Override
        @NotNull
        public List<BoundEndpoint> getBoundEndpoints() {
            return this.endpoints;
        }

        @Override
        @NotNull
        public String getContextPath() {
            throw new WebServiceException("Container " + ServletContainer.class.getName() + " doesn't support getContextPath()");
        }
    };
    private final ResourceLoader loader = new ResourceLoader(){

        @Override
        public URL getResource(String resource) throws MalformedURLException {
            return ServletContainer.this.servletContext.getResource("/WEB-INF/" + resource);
        }
    };

    ServletContainer(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public <T> T getSPI(Class<T> spiType) {
        if (spiType == ServletContext.class) {
            return spiType.cast(this.servletContext);
        }
        if (spiType.isAssignableFrom(ServletModule.class)) {
            return spiType.cast(this.module);
        }
        if (spiType == ResourceLoader.class) {
            return spiType.cast(this.loader);
        }
        return null;
    }
}

