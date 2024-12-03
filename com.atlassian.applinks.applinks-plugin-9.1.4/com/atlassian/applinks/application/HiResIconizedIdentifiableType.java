/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.spi.application.IconizedType
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.application;

import com.atlassian.applinks.application.IconizedIdentifiableType;
import com.atlassian.applinks.core.AppLinkPluginUtil;
import com.atlassian.applinks.spi.application.IconizedType;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import java.net.URI;
import java.net.URISyntaxException;
import javax.annotation.Nullable;

public abstract class HiResIconizedIdentifiableType
extends IconizedIdentifiableType
implements IconizedType {
    public HiResIconizedIdentifiableType(AppLinkPluginUtil pluginUtil, WebResourceUrlProvider webResourceUrlProvider) {
        super(pluginUtil, webResourceUrlProvider);
    }

    @Nullable
    public URI getIconUri() {
        try {
            return new URI(this.webResourceUrlProvider.getStaticPluginResourceUrl(this.pluginUtil.getPluginKey() + ":applinks-images", "images", UrlMode.ABSOLUTE) + "/config/logos/128x128/128" + this.getIconKey() + ".png");
        }
        catch (URISyntaxException e) {
            this.LOG.warn("Unable to find the icon for this application type.", (Throwable)e);
            return null;
        }
    }
}

