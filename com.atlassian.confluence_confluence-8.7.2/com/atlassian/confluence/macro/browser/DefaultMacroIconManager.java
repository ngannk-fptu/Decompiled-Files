/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.servlet.DownloadException
 *  com.atlassian.plugin.servlet.DownloadableResource
 *  com.atlassian.plugin.servlet.ServletContextFactory
 *  com.atlassian.plugin.webresource.PluginResourceLocator
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.net.ResponseException
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.macro.browser;

import com.atlassian.confluence.macro.browser.MacroIconManager;
import com.atlassian.confluence.macro.browser.beans.MacroIcon;
import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.plugin.servlet.DownloadException;
import com.atlassian.plugin.servlet.DownloadableResource;
import com.atlassian.plugin.servlet.ServletContextFactory;
import com.atlassian.plugin.webresource.PluginResourceLocator;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.ResponseException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultMacroIconManager
implements MacroIconManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultMacroIconManager.class);
    private final RequestFactory<?> requestFactory;
    private final ServletContextFactory servletContextFactory;
    private final WebResourceUrlProvider webResourceUrlProvider;
    private final PluginResourceLocator pluginResourceLocator;

    public DefaultMacroIconManager(RequestFactory<?> requestFactory, ServletContextFactory servletContextFactory, WebResourceUrlProvider webResourceUrlProvider, PluginResourceLocator pluginResourceLocator) {
        this.requestFactory = requestFactory;
        this.servletContextFactory = servletContextFactory;
        this.webResourceUrlProvider = webResourceUrlProvider;
        this.pluginResourceLocator = pluginResourceLocator;
    }

    @Override
    public String getExternalSmallIconUrl(MacroMetadata macroMetadata) {
        if (macroMetadata == null) {
            return this.getAbsoluteUrl("/images/icons/macrobrowser/macro-placeholder-default.png");
        }
        if (macroMetadata.getIcon() == null) {
            String path = "/images/icons/macrobrowser/dropdown/" + macroMetadata.getMacroName() + ".png";
            try {
                URL resource = this.servletContextFactory.getServletContext().getResource(path);
                if (resource != null) {
                    return this.getAbsoluteUrl(path);
                }
            }
            catch (MalformedURLException e) {
                log.warn("MalformedURLException when expected a URL or null");
            }
            return this.getAbsoluteUrl("/images/icons/macrobrowser/macro-placeholder-default.png");
        }
        return this.getAbsoluteUrl("/plugins/servlet/confluence/placeholder/macro-icon?name=" + macroMetadata.getMacroName());
    }

    @Override
    public InputStream getIconStream(MacroMetadata macroMetadata) {
        InputStream result = null;
        if (macroMetadata != null) {
            if (macroMetadata.getIcon() != null) {
                MacroIcon icon = macroMetadata.getIcon();
                if (icon.isRelative()) {
                    result = this.servletContextFactory.getServletContext().getResourceAsStream(icon.getLocation());
                    if (result == null) {
                        result = this.getFromPluginResource(icon);
                    }
                } else {
                    result = this.retrieveFromOtherServer(macroMetadata, icon.getLocation());
                }
            }
            if (result == null) {
                result = this.servletContextFactory.getServletContext().getResourceAsStream("/images/icons/macrobrowser/dropdown/" + macroMetadata.getMacroName() + ".png");
            }
            if (result == null) {
                result = this.servletContextFactory.getServletContext().getResourceAsStream("/images/icons/macrobrowser/" + macroMetadata.getMacroName() + ".png");
            }
        }
        if (result == null) {
            result = this.servletContextFactory.getServletContext().getResourceAsStream("/images/icons/macrobrowser/macro-placeholder-default.png");
        }
        return result;
    }

    private InputStream getFromPluginResource(MacroIcon icon) {
        try {
            DownloadableResource downloadableResource = this.pluginResourceLocator.getDownloadableResource(icon.getLocation(), null);
            return new ByteArrayInputStream(this.streamResourceToBytes(downloadableResource));
        }
        catch (DownloadException e) {
            log.warn("Exception occurred while streaming image from " + icon.getLocation(), (Throwable)e);
            return null;
        }
    }

    private byte[] streamResourceToBytes(DownloadableResource downloadableResource) throws DownloadException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        downloadableResource.streamResource((OutputStream)outputStream);
        return outputStream.toByteArray();
    }

    private InputStream retrieveFromOtherServer(MacroMetadata macroMetadata, String location) {
        AtomicReference byteArray = new AtomicReference();
        try {
            Request request = this.requestFactory.createRequest(Request.MethodType.GET, StringEscapeUtils.unescapeHtml4((String)location));
            request.execute(response -> {
                if (response.getStatusCode() < 200 || response.getStatusCode() > 299) {
                    throw new ResponseException("Invalid response code: " + response.getStatusCode());
                }
                try {
                    byteArray.set(IOUtils.toByteArray((InputStream)response.getResponseBodyAsStream()));
                }
                catch (IOException e) {
                    throw new ResponseException((Throwable)e);
                }
            });
        }
        catch (ResponseException e) {
            log.info("The custom icon for macro {} could not be retrieved from {}. Error: {}", new Object[]{macroMetadata.getMacroName(), location, e.getMessage()});
            return null;
        }
        return new ByteArrayInputStream((byte[])byteArray.get());
    }

    private String getAbsoluteUrl(String url) {
        return this.webResourceUrlProvider.getStaticResourcePrefix(UrlMode.ABSOLUTE) + url;
    }
}

