/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.elements.ResourceLocation
 *  javax.servlet.ServletContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.servlet;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.servlet.AbstractDownloadableResource;
import java.io.InputStream;
import javax.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadableWebResource
extends AbstractDownloadableResource {
    private static final Logger log = LoggerFactory.getLogger(DownloadableWebResource.class);
    private final ServletContext servletContext;

    public DownloadableWebResource(Plugin plugin, ResourceLocation resourceLocation, String extraPath, ServletContext servletContext, boolean disableMinification) {
        super(plugin, resourceLocation, extraPath, disableMinification);
        this.servletContext = servletContext;
    }

    @Override
    protected InputStream getResourceAsStream(String resourceLocation) {
        String fixedResourceLocation = this.fixResourceLocation(resourceLocation);
        return this.servletContext.getResourceAsStream(fixedResourceLocation);
    }

    private String fixResourceLocation(String resourceLocation) {
        if (!resourceLocation.startsWith("/")) {
            String resourceLocationWithSlash = "/" + resourceLocation;
            log.debug("ResourceLocation: {}, does not start with slash. Location was modified to: {}", (Object)resourceLocation, (Object)resourceLocationWithSlash);
            return resourceLocationWithSlash;
        }
        return resourceLocation;
    }
}

