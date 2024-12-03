/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 */
package com.atlassian.streams.confluence;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.net.URI;

public class UriProvider {
    private static final Iterable<String> contentStyles = ImmutableList.of((Object)"master.css", (Object)"wiki-content.css", (Object)"tables.css", (Object)"renderer-macros.css");
    private static final Iterable<String> panelStyles = ImmutableList.of((Object)"panels.css");
    private static final Iterable<String> iconStyles = ImmutableList.of((Object)"icons.css");
    private final WebResourceManager webResourceManager;

    public UriProvider(WebResourceManager webResourceManager) {
        this.webResourceManager = (WebResourceManager)Preconditions.checkNotNull((Object)webResourceManager, (Object)"webResourceManager");
    }

    public URI getEntityUri(URI baseUri, ContentEntityObject entity) {
        if (entity instanceof AbstractPage && !entity.isLatestVersion() && entity.getLatestVersion() instanceof AbstractPage) {
            return URI.create(baseUri.toASCIIString() + ((AbstractPage)entity.getLatestVersion()).getUrlPath());
        }
        return URI.create(baseUri.toASCIIString() + entity.getUrlPath());
    }

    public URI getPageDiffUri(URI baseUri, ContentEntityObject entity, int originalVersion, int newerVersion) {
        Preconditions.checkNotNull((Object)entity, (Object)"entity");
        String diffUrl = baseUri.toASCIIString() + "/pages/diffpagesbyversion.action?pageId=" + entity.getId() + "&originalVersion=" + originalVersion + "&revisedVersion=" + newerVersion;
        return URI.create(diffUrl).normalize();
    }

    public Iterable<URI> getContentCssUris() {
        return Iterables.transform(contentStyles, this.getStaticPluginResource("confluence.web.resources:content-styles"));
    }

    public Iterable<URI> getPanelCssUris() {
        return Iterables.transform(panelStyles, this.getStaticPluginResource("confluence.web.resources:panel-styles"));
    }

    public Iterable<URI> getIconCssUris() {
        return Iterables.transform(iconStyles, this.getStaticPluginResource("confluence.web.resources:master-styles"));
    }

    private Function<String, URI> getStaticPluginResource(String moduleKey) {
        return s -> URI.create(this.webResourceManager.getStaticPluginResource(moduleKey, s, UrlMode.ABSOLUTE)).normalize();
    }
}

