/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.servlet.ContentTypeResolver
 *  com.atlassian.plugin.servlet.DownloadException
 *  com.atlassian.plugin.servlet.DownloadStrategy
 *  com.atlassian.plugin.servlet.util.LastModifiedHandler
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.webresource.servlet;

import com.atlassian.plugin.servlet.ContentTypeResolver;
import com.atlassian.plugin.servlet.DownloadException;
import com.atlassian.plugin.servlet.DownloadStrategy;
import com.atlassian.plugin.servlet.util.LastModifiedHandler;
import com.atlassian.plugin.webresource.PluginResourceLocator;
import com.atlassian.plugin.webresource.impl.Globals;
import com.atlassian.plugin.webresource.impl.support.http.Request;
import com.atlassian.plugin.webresource.impl.support.http.Response;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginResourceDownload
implements DownloadStrategy {
    private static final Logger log = LoggerFactory.getLogger(PluginResourceDownload.class);
    private Globals globals;
    private String characterEncoding = "UTF-8";

    public PluginResourceDownload() {
    }

    public PluginResourceDownload(PluginResourceLocator pluginResourceLocator, ContentTypeResolver contentTypeResolver, String characterEncoding) {
        this.characterEncoding = characterEncoding;
        this.globals = pluginResourceLocator.temporaryWayToGetGlobalsDoNotUseIt();
        this.globals.getConfig().setContentTypeResolver(contentTypeResolver);
    }

    public boolean matches(String urlPath) {
        return this.globals.getRouter().canDispatch(urlPath);
    }

    public void serveFile(HttpServletRequest originalRequest, HttpServletResponse originalResponse) throws DownloadException {
        LastModifiedHandler lastModifiedHandler;
        Request request = new Request(this.globals, originalRequest, this.characterEncoding);
        Response response = new Response(request, originalResponse);
        log.debug("WRM serving plugin resource before dispatch with request URL {} and original response status code {}", (Object)request.getPath(), (Object)response.getStatus());
        if (request.isCacheable() && response.checkRequestHelper(lastModifiedHandler = this.getLastModifiedHandler(originalRequest))) {
            return;
        }
        this.globals.getRouter().dispatch(request, response);
    }

    private LastModifiedHandler getLastModifiedHandler(HttpServletRequest originalRequest) {
        Date lastModifiedDate = null;
        try {
            long ifModifiedSinceValue = originalRequest.getDateHeader("If-Modified-Since");
            if (ifModifiedSinceValue >= 0L) {
                lastModifiedDate = new Date(ifModifiedSinceValue);
            }
        }
        catch (IllegalArgumentException illegalArgumentException) {
            // empty catch block
        }
        return lastModifiedDate != null ? new LastModifiedHandler(lastModifiedDate) : new LastModifiedHandler();
    }

    public void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    public void setContentTypeResolver(ContentTypeResolver contentTypeResolver) {
        this.globals.getConfig().setContentTypeResolver(contentTypeResolver);
    }

    public void setPluginResourceLocator(PluginResourceLocator pluginResourceLocator) {
        this.globals = pluginResourceLocator.temporaryWayToGetGlobalsDoNotUseIt();
    }
}

