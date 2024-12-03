/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.elements.ResourceLocation
 *  com.atlassian.plugin.util.PluginUtils
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.servlet;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.servlet.DownloadException;
import com.atlassian.plugin.servlet.DownloadableResource;
import com.atlassian.plugin.servlet.util.LastModifiedHandler;
import com.atlassian.plugin.util.PluginUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractDownloadableResource
implements DownloadableResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDownloadableResource.class);
    private static final String ATLASSIAN_WEB_RESOURCE_DISABLE_MINIFICATION = "atlassian.webresource.disable.minification";
    protected final Plugin plugin;
    protected final String extraPath;
    protected final ResourceLocation resourceLocation;
    private final String location;
    private final boolean disableMinification;

    public AbstractDownloadableResource(Plugin plugin, ResourceLocation resourceLocation, String extraPath) {
        this(plugin, resourceLocation, extraPath, false);
    }

    public AbstractDownloadableResource(Plugin plugin, ResourceLocation resourceLocation, String extraPath, boolean disableMinification) {
        if (extraPath != null && !"".equals(extraPath.trim()) && !resourceLocation.getLocation().endsWith("/")) {
            extraPath = "/" + extraPath;
        }
        this.disableMinification = disableMinification;
        this.plugin = plugin;
        this.extraPath = extraPath;
        this.resourceLocation = resourceLocation;
        this.location = resourceLocation.getLocation() + extraPath;
    }

    @Override
    public void serveResource(HttpServletRequest request, HttpServletResponse response) throws DownloadException {
        ServletOutputStream out;
        LOGGER.debug("Serving: {}", (Object)this);
        InputStream resourceStream = this.getResourceAsStreamViaMinificationStrategy();
        if (resourceStream == null) {
            LOGGER.warn("Resource not found: {}", (Object)this);
            return;
        }
        String contentType = this.getContentType();
        if (StringUtils.isNotBlank((CharSequence)contentType)) {
            response.setContentType(contentType);
        }
        try {
            out = response.getOutputStream();
        }
        catch (IOException e) {
            throw new DownloadException(e);
        }
        this.streamResource(resourceStream, (OutputStream)out);
        LOGGER.debug("Serving file done.");
    }

    @Override
    public void streamResource(OutputStream out) throws DownloadException {
        InputStream resourceStream = this.getResourceAsStreamViaMinificationStrategy();
        if (resourceStream == null) {
            LOGGER.warn("Resource not found: {}", (Object)this);
            return;
        }
        this.streamResource(resourceStream, out);
    }

    private void streamResource(InputStream in, OutputStream out) throws DownloadException {
        try (InputStream inputStream = in;){
            IOUtils.copy((InputStream)inputStream, (OutputStream)out);
        }
        catch (IOException e) {
            throw new DownloadException(e);
        }
        finally {
            try {
                out.flush();
            }
            catch (IOException e) {
                LOGGER.debug("Error flushing output stream", (Throwable)e);
            }
        }
    }

    @Override
    public boolean isResourceModified(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        LastModifiedHandler cacheHandler = new LastModifiedHandler(this.plugin.getDateLoaded());
        cacheHandler.setCacheHeadersIfCacheable(httpServletRequest, httpServletResponse);
        return cacheHandler.isNotCacheableResponse(httpServletRequest);
    }

    @Override
    public String getContentType() {
        return this.resourceLocation.getContentType();
    }

    protected abstract InputStream getResourceAsStream(String var1);

    protected String getLocation() {
        return this.location;
    }

    public String toString() {
        String pluginKey = this.plugin != null ? this.plugin.getKey() : "";
        return "Resource: " + pluginKey + " " + this.getLocation() + " (" + this.getContentType() + ")";
    }

    private InputStream getResourceAsStreamViaMinificationStrategy() {
        InputStream inputStream = null;
        String location = this.getLocation();
        if (this.minificationStrategyInPlay(location)) {
            String minifiedLocation = this.getMinifiedLocation(location);
            inputStream = this.getResourceAsStream(minifiedLocation);
        }
        if (inputStream == null) {
            inputStream = this.getResourceAsStream(location);
        }
        return inputStream;
    }

    private boolean minificationStrategyInPlay(String resourceLocation) {
        if (this.disableMinification) {
            return false;
        }
        try {
            if (Boolean.getBoolean(ATLASSIAN_WEB_RESOURCE_DISABLE_MINIFICATION) || PluginUtils.isAtlassianDevMode()) {
                return false;
            }
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        if (resourceLocation.endsWith(".js")) {
            return !resourceLocation.endsWith("-min.js") && !resourceLocation.endsWith(".min.js");
        }
        if (resourceLocation.endsWith(".css")) {
            return !resourceLocation.endsWith("-min.css") && !resourceLocation.endsWith(".min.css");
        }
        return false;
    }

    private String getMinifiedLocation(String location) {
        int lastDot = location.lastIndexOf(46);
        return location.substring(0, lastDot) + "-min" + location.substring(lastDot);
    }
}

