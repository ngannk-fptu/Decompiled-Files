/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.rometools.rome.feed.synd.SyndFeed
 *  com.sun.syndication.feed.synd.SyndFeed
 */
package com.atlassian.confluence.rss;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.rss.FeedBuilder;
import com.atlassian.confluence.rss.FeedProperties;
import com.atlassian.confluence.rss.LegacyRomeSyndFeed;
import com.atlassian.confluence.rss.RomeFeedBuilder;
import com.atlassian.confluence.rss.RomeToolsSyndFeed;
import com.atlassian.confluence.rss.RssRenderSupport;
import com.atlassian.confluence.rss.SyndFeedService;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.rometools.rome.feed.synd.SyndFeed;
import java.util.Map;
import java.util.function.Function;

public class DefaultFeedBuilder
implements FeedBuilder,
SyndFeedService {
    private final RomeFeedBuilder delegate;

    public DefaultFeedBuilder(SearchManager searchManager, GlobalSettingsManager settingsManager, PermissionManager permissionManager, UserAccessor userAccessor, FormatSettingsManager formatSettingsManager, LocaleManager localeManager, Map<String, RssRenderSupport> renderSupport, PluginAccessor pluginAccessor, EventPublisher eventPublisher) {
        this.delegate = new RomeFeedBuilder(searchManager, settingsManager, permissionManager, userAccessor, userAccessor, formatSettingsManager, localeManager, pluginAccessor, eventPublisher, DefaultFeedBuilder.renderSupportLookup(renderSupport));
    }

    private static Function<String, RssRenderSupport<ConfluenceEntityObject>> renderSupportLookup(Map<String, RssRenderSupport> renderSupport) {
        return renderSupport == null ? className -> null : renderSupport::get;
    }

    @Override
    public com.sun.syndication.feed.synd.SyndFeed createFeed(ISearch search, FeedProperties feedProperties) {
        LegacyRomeSyndFeed feed = new LegacyRomeSyndFeed();
        this.delegate.populateFeed(feed, search, feedProperties);
        return feed.get();
    }

    @Override
    public SyndFeed createSyndFeed(ISearch search, FeedProperties feedProperties) {
        RomeToolsSyndFeed feed = new RomeToolsSyndFeed();
        this.delegate.populateFeed(feed, search, feedProperties);
        return feed.get();
    }
}

