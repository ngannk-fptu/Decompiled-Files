/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.api.common.uri.Uri
 *  com.atlassian.streams.api.common.uri.UriBuilder
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.streams.internal.feed;

import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.api.common.uri.Uri;
import com.atlassian.streams.api.common.uri.UriBuilder;
import com.atlassian.streams.internal.feed.FeedRendererContext;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.net.URI;
import java.util.Iterator;
import org.springframework.beans.factory.annotation.Qualifier;

public class DefaultFeedRendererContext
implements FeedRendererContext {
    public static final String DEFAULT_FEED_AUTHOR = "streams.feed.title.default";
    public static final String DEFAULT_FEED_TITLE = "streams.feed.author.default";
    public static final String ANONYMOUS_USER_NAME = "streams.authors.unknown.capitalized";
    private final I18nResolver i18nResolver;
    private final WebResourceManager webResourceManager;
    private static final Iterable<Integer> PICTURE_SIZES = ImmutableList.of((Object)16, (Object)48);
    private final boolean DEV_MODE = Boolean.getBoolean("atlassian.dev.mode");

    public DefaultFeedRendererContext(@Qualifier(value="streamsI18nResolver") I18nResolver i18nResolver, WebResourceManager webResourceManager) {
        this.i18nResolver = (I18nResolver)Preconditions.checkNotNull((Object)i18nResolver, (Object)"i18nResolver");
        this.webResourceManager = (WebResourceManager)Preconditions.checkNotNull((Object)webResourceManager, (Object)"webResourceManager");
    }

    @Override
    public String getAnonymousUserName() {
        return this.i18nResolver.getText(ANONYMOUS_USER_NAME);
    }

    @Override
    public String getDefaultFeedAuthor() {
        return this.i18nResolver.getText(DEFAULT_FEED_AUTHOR);
    }

    @Override
    public String getDefaultFeedTitle() {
        return this.i18nResolver.getText(DEFAULT_FEED_TITLE);
    }

    @Override
    public Iterable<Integer> getDefaultUserPictureSizes() {
        return PICTURE_SIZES;
    }

    @Override
    public Option<URI> getUserPictureUri(Option<URI> baseUri, int size, String application) {
        if ("com.atlassian.bamboo".equalsIgnoreCase(application)) {
            String uri = this.webResourceManager.getStaticPluginResource("com.atlassian.streams:streamsWebResources", "images/bamboo-logo-" + size + ".png", UrlMode.ABSOLUTE);
            return Option.some((Object)URI.create(uri));
        }
        Iterator iterator = baseUri.iterator();
        if (iterator.hasNext()) {
            URI base = (URI)iterator.next();
            return Option.some((Object)new UriBuilder(Uri.fromJavaUri((URI)base)).addQueryParameter("s", String.valueOf(size)).toUri().toJavaUri());
        }
        return Option.none();
    }

    @Override
    public boolean isDeveloperMode() {
        return this.DEV_MODE;
    }
}

