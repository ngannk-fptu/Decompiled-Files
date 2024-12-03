/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.user.User
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timers
 *  io.atlassian.util.concurrent.Timeout
 *  org.apache.commons.lang3.StringUtils
 *  org.hibernate.Hibernate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.slf4j.MDC
 */
package com.atlassian.confluence.rss;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.core.Addressable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ContentTypeAware;
import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.core.Versioned;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.rss.FeedProperties;
import com.atlassian.confluence.rss.FeedSupportModuleDescriptor;
import com.atlassian.confluence.rss.FeedTimeoutEvent;
import com.atlassian.confluence.rss.RomeSyndEntry;
import com.atlassian.confluence.rss.RomeSyndFeed;
import com.atlassian.confluence.rss.RssRenderItem;
import com.atlassian.confluence.rss.RssRenderSupport;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.confluence.user.UserPreferencesAccessor;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.user.User;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import io.atlassian.util.concurrent.Timeout;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

final class RomeFeedBuilder {
    private static final Logger log = LoggerFactory.getLogger(RomeFeedBuilder.class);
    private static final String DEFAULT_FEED_TITLE = "Confluence RSS Feed";
    private final SearchManager searchManager;
    private final GlobalSettingsManager settingsManager;
    private final PermissionManager permissionManager;
    private final UserPreferencesAccessor userPreferencesAccessor;
    private final ConfluenceUserResolver userResolver;
    private final FormatSettingsManager formatSettingsManager;
    private final LocaleManager localeManager;
    private final PluginAccessor pluginAccessor;
    private final EventPublisher eventPublisher;
    private final Function<String, RssRenderSupport<ConfluenceEntityObject>> renderSupportLookup;

    public RomeFeedBuilder(SearchManager searchManager, GlobalSettingsManager settingsManager, PermissionManager permissionManager, UserPreferencesAccessor userPreferencesAccessor, ConfluenceUserResolver userResolver, FormatSettingsManager formatSettingsManager, LocaleManager localeManager, PluginAccessor pluginAccessor, EventPublisher eventPublisher, Function<String, RssRenderSupport<ConfluenceEntityObject>> renderSupportLookup) {
        this.searchManager = searchManager;
        this.settingsManager = settingsManager;
        this.permissionManager = permissionManager;
        this.userPreferencesAccessor = userPreferencesAccessor;
        this.userResolver = userResolver;
        this.formatSettingsManager = formatSettingsManager;
        this.localeManager = localeManager;
        this.pluginAccessor = pluginAccessor;
        this.eventPublisher = eventPublisher;
        this.renderSupportLookup = renderSupportLookup;
    }

    private void populateFeedFromSearchResults(RomeSyndFeed feed, List<Searchable> results, FeedProperties feedProperties) {
        try (Ticker ignored = Timers.start((String)"DefaultFeedBuilder.createFeedFromSearchResults()");){
            Timeout timeout = Timeout.getMillisTimeout((long)this.getTimeoutInSeconds(), (TimeUnit)TimeUnit.SECONDS);
            if (log.isDebugEnabled()) {
                this.logFeedCreation(feedProperties);
            }
            if (results == null) {
                return;
            }
            this.populateFeedEntries(feed, feedProperties, results, timeout);
            log.debug("Feed successfully created.");
        }
    }

    private int getTimeoutInSeconds() {
        return this.settingsManager.getGlobalSettings().getRssTimeout();
    }

    public void populateFeed(RomeSyndFeed feed, ISearch search, FeedProperties feedProperties) {
        if (log.isDebugEnabled()) {
            this.logFeedCreation(feedProperties);
        }
        try (Ticker ignored = Timers.start((String)"DefaultFeedBuilder.createFeed()");){
            this.populateEmptyFeed(feed, feedProperties);
            if (search != null) {
                this.populateFeedFromSearchResults(feed, this.searchManager.searchEntities(search, SearchManager.EntityVersionPolicy.INDEXED_VERSION), feedProperties);
            }
        }
        catch (InvalidSearchException | RuntimeException t) {
            log.error("Feed Error:" + t, (Throwable)t);
            this.populateErrorFeed(feed, t);
        }
    }

    private void populateEmptyFeed(RomeSyndFeed feed, FeedProperties feedProperties) {
        feed.setTitle(StringUtils.isBlank((CharSequence)feedProperties.getTitle()) ? DEFAULT_FEED_TITLE : feedProperties.getTitle());
        feed.setLink(this.getDomainName());
        feed.setUri(this.getDomainName());
        feed.setDescription(feedProperties.getDescription());
    }

    void populateFeedEntries(RomeSyndFeed feed, FeedProperties feedProperties, List<Searchable> searchables, Timeout timeout) {
        if (timeout.isExpired()) {
            log.warn(MessageFormat.format("Feed generation timed out {0} {1} beyond {2} {1} before rendering any entries. All {3} entries will be omitted from the feed.", new Object[]{-timeout.getTime(), timeout.getUnit(), timeout.getTimeoutPeriod(), searchables.size()}));
            return;
        }
        for (Searchable searchable : searchables) {
            if (!(searchable instanceof ConfluenceEntityObject)) {
                log.debug("Searchable '{}' is not an instance of {}. Skipped.", (Object)searchable.getClass().getName(), (Object)ConfluenceEntityObject.class.getName());
                continue;
            }
            ConfluenceEntityObject entity = (ConfluenceEntityObject)searchable;
            User permissionUser = RomeFeedBuilder.getPermissionUser(feedProperties);
            if (!this.permissionManager.hasPermissionNoExemptions(permissionUser, Permission.VIEW, entity)) {
                log.debug("User is not permitted to view content, despite finding it in the search index. Omitting content from feed: {}", (Object)entity);
                continue;
            }
            RssRenderSupport<ConfluenceEntityObject> renderer = this.getRenderSupport(entity);
            if (renderer == null) {
                log.error("No RSS renderer found for :" + entity);
                continue;
            }
            this.populateEntryForEntity(feed.addEntry(), entity, feedProperties, renderer, timeout);
            if (!timeout.isExpired()) continue;
            this.eventPublisher.publish((Object)FeedTimeoutEvent.createForTimeout(this, entity, timeout, searchables.size(), feed.getEntryCount()));
            log.warn(MessageFormat.format("Feed generation timed out {0} {1} beyond {2} {1}.  {3} remaining entries will be omitted from the feed. Last rendered item was {4}", new Object[]{-timeout.getTime(), timeout.getUnit(), timeout.getTimeoutPeriod(), searchables.size() - feed.getEntryCount(), entity}));
            break;
        }
    }

    private RssRenderSupport<ConfluenceEntityObject> getRenderSupport(Object entity) {
        Class entityClass = Hibernate.getClass((Object)entity);
        RssRenderSupport<ConfluenceEntityObject> renderSupport = this.renderSupportLookup.apply(entityClass.getName());
        if (renderSupport != null) {
            return renderSupport;
        }
        String lookupKey = entityClass.getName();
        if (entity instanceof CustomContentEntityObject) {
            lookupKey = ((CustomContentEntityObject)entity).getPluginModuleKey();
        }
        for (FeedSupportModuleDescriptor descriptor : this.pluginAccessor.getEnabledModuleDescriptorsByClass(FeedSupportModuleDescriptor.class)) {
            if (!descriptor.getRenders().equals(lookupKey)) continue;
            return descriptor.getModule();
        }
        return null;
    }

    private static User getPermissionUser(FeedProperties feedProperties) {
        return feedProperties.isPublicFeed() ? null : AuthenticatedUserThreadLocal.get();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private <T extends ConfluenceEntityObject> void populateEntryForEntity(RomeSyndEntry entry, T entity, FeedProperties feedProperties, RssRenderSupport<T> renderer, Timeout timeout) {
        long start = 0L;
        if (log.isDebugEnabled()) {
            start = System.currentTimeMillis();
        }
        ConfluenceUser modifier = this.userResolver.getUserByName(entity.getLastModifierName());
        User recipient = RomeFeedBuilder.getPermissionUser(feedProperties);
        RssRenderItem<T> item = new RssRenderItem<T>(entity, feedProperties, modifier, recipient, this.getDateFormatter(recipient));
        String title = renderer.getTitle(item);
        entry.setTitle(title);
        entry.setLink(this.getDomainName() + renderer.getLink(item));
        entry.setUri(this.constructUri(entity));
        try {
            MDC.put((String)"rssItemTitle", (String)title);
            entry.setDescription("html", RomeFeedBuilder.renderContent(item, renderer, timeout));
        }
        finally {
            MDC.remove((String)"rssItemTitle");
        }
        if (entity.getLastModifier() != null) {
            entry.setAuthor(entity.getLastModifier().getFullName());
        }
        entry.setPublishedDate(entity.getCreationDate());
        entry.setUpdatedDate(entity.getLastModificationDate());
        entry.setCategoryNames(renderer.getCategoryNames(item));
        if (start != 0L) {
            this.logFeedEntryCreation(entity, start);
        }
    }

    private static <T> String renderContent(RssRenderItem<T> item, RssRenderSupport<T> renderer, Timeout timeout) {
        String renderedContent = renderer.renderedContext(item, timeout).replaceAll("\\r", "");
        return GeneralUtil.replaceInvalidXmlCharacters(renderedContent);
    }

    private void logFeedCreation(FeedProperties feedProperties) {
        StringBuilder message = new StringBuilder("Creating Feed showing ");
        if (feedProperties.isShowContent()) {
            message.append("content.");
        }
        log.debug(message.toString());
    }

    private void logFeedEntryCreation(ConfluenceEntityObject entity, long start) {
        long millis = System.currentTimeMillis() - start;
        if (millis > 500L) {
            StringBuilder message = new StringBuilder("Rendered ");
            String entityClassName = entity.getClass().getName();
            message.append(entityClassName.substring(entityClassName.lastIndexOf(46) + 1));
            message.append(" #");
            message.append(entity.getId());
            message.append(" in ");
            message.append(millis);
            message.append(" ms.");
            log.debug(message.toString());
        }
    }

    String constructUri(ConfluenceEntityObject entity) {
        return "tag:" + this.getDomain() + ",2009:" + this.getSpecific(entity);
    }

    private String getSpecific(ConfluenceEntityObject entity) {
        String type = entity instanceof ContentTypeAware ? ((Addressable)((Object)entity)).getType() : "generic";
        if (entity instanceof Versioned) {
            Versioned currentVersion = (Versioned)((Object)entity);
            Versioned latestVersion = currentVersion.getLatestVersion();
            return type + "-" + ((ConfluenceEntityObject)((Object)latestVersion)).getId() + "-" + currentVersion.getVersion();
        }
        return type + "-" + entity.getId();
    }

    private String getDomain() {
        String baseUrl = this.settingsManager.getGlobalSettings().getBaseUrl().toLowerCase().trim();
        try {
            return new URI(baseUrl).getHost();
        }
        catch (URISyntaxException e) {
            String message = "Invalid server base URL: " + baseUrl + ". You can correct this by going to Administration > General Configuration.";
            log.warn(message);
            throw new RuntimeException(message, e);
        }
    }

    private DateFormatter getDateFormatter(User user) {
        return this.userPreferencesAccessor.getConfluenceUserPreferences(user).getDateFormatter(this.formatSettingsManager, this.localeManager);
    }

    private void populateErrorFeed(RomeSyndFeed feed, Throwable t) {
        feed.setTitle("Confluence RSS Error Report");
        feed.setLink(this.getDomainName());
        feed.setDescription(t.toString());
        RomeSyndEntry entry = feed.addEntry();
        entry.setTitle("Confluence RSS Error!");
        entry.setLink("http://www.atlassian.com/support");
        String errorDescription = this.makeErrorDescription(t);
        String renderedErrorDescription = this.renderErrorDescription(errorDescription);
        entry.setDescription("html", renderedErrorDescription);
    }

    private String renderErrorDescription(String errorDescription) {
        Map<String, Object> contextMap = MacroUtils.defaultVelocityContext();
        contextMap.put("errorDescription", errorDescription);
        contextMap.put("i18n", new ConfluenceActionSupport());
        return VelocityUtils.getRenderedTemplate("templates/rss/error-rss-content.vm", contextMap);
    }

    private String makeErrorDescription(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString().replaceAll("\n", "<br />");
    }

    private String getDomainName() {
        return this.settingsManager.getGlobalSettings().getBaseUrl();
    }
}

