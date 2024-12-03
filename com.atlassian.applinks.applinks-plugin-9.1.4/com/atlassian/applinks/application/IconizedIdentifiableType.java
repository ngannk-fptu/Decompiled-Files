/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.spi.application.IdentifiableType
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.application;

import com.atlassian.applinks.core.AppLinkPluginUtil;
import com.atlassian.applinks.spi.application.IdentifiableType;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import java.net.URI;
import java.net.URISyntaxException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class IconizedIdentifiableType
implements IdentifiableType {
    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());
    protected final WebResourceUrlProvider webResourceUrlProvider;
    protected final AppLinkPluginUtil pluginUtil;

    public IconizedIdentifiableType(AppLinkPluginUtil pluginUtil, WebResourceUrlProvider webResourceUrlProvider) {
        this.pluginUtil = pluginUtil;
        this.webResourceUrlProvider = webResourceUrlProvider;
    }

    @Nullable
    public URI getIconUrl() {
        try {
            return new URI(this.webResourceUrlProvider.getStaticPluginResourceUrl(this.pluginUtil.getPluginKey() + ":applinks-images", "images", UrlMode.ABSOLUTE) + "/types/16" + this.getIconKey() + ".png");
        }
        catch (URISyntaxException e) {
            this.LOG.warn("Unable to find the icon for this application type.", (Throwable)e);
            return null;
        }
    }

    public String toString() {
        return this.getId().toString();
    }

    @Nonnull
    protected String getIconKey() {
        return this.getId().get();
    }
}

