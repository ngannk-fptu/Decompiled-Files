/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Ordering
 *  com.rometools.rome.feed.atom.Entry
 *  com.rometools.rome.feed.atom.Feed
 *  com.rometools.rome.feed.atom.Link
 *  com.rometools.rome.feed.atom.Person
 *  org.codehaus.jackson.JsonFactory
 *  org.codehaus.jackson.map.MappingJsonFactory
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.jdom2.Element
 *  org.joda.time.DateTime
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.core.log;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.upm.api.log.AuditLogEntry;
import com.atlassian.upm.api.log.EntryType;
import com.atlassian.upm.core.impl.NamespacedPluginSettings;
import com.atlassian.upm.core.log.AuditLogEntryImpl;
import com.atlassian.upm.core.rest.BaseUriBuilder;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.atom.Link;
import com.rometools.rome.feed.atom.Person;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.jdom2.Element;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PluginSettingsAuditLogAccessor {
    static final String UPM_AUDIT_LOG = "upm_audit_log_v3";
    protected static final String UPM_AUDIT_LOG_MAX_ENTRIES = "upm_audit_log_max_entries";
    protected static final String UPM_AUDIT_LOG_PURGE_AFTER = "upm_audit_log_purge_after";
    protected static final String UPM_AUDIT_LOG_LAST_RETRIEVED = "upm_audit_log_last_retrieved";
    private static final int DEFAULT_MAX_ENTRIES = 5000;
    private static final int DEFAULT_PURGE_AFTER = 90;
    private static final Logger log = LoggerFactory.getLogger((String)PluginSettingsAuditLogAccessor.class.getName());
    protected final I18nResolver i18nResolver;
    protected final ApplicationProperties applicationProperties;
    protected final UserManager userManager;
    private final PluginSettingsFactory pluginSettingsFactory;
    protected final ObjectMapper mapper;
    protected final Predicate<AuditLogEntry> purgePolicy;
    private final BaseUriBuilder uriBuilder;
    protected final Clock clock;
    private final String keyPrefix;

    public PluginSettingsAuditLogAccessor(I18nResolver i18nResolver, ApplicationProperties applicationProperties, UserManager userManager, PluginSettingsFactory pluginSettingsFactory, BaseUriBuilder uriBuilder, Clock clock, String keyPrefix) {
        this.i18nResolver = Objects.requireNonNull(i18nResolver, "i18nResolver");
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
        this.userManager = Objects.requireNonNull(userManager, "userManager");
        this.pluginSettingsFactory = Objects.requireNonNull(pluginSettingsFactory, "pluginSettingsFactory");
        this.purgePolicy = new Predicate<AuditLogEntry>(){

            public boolean apply(AuditLogEntry input) {
                DateTime nDaysAgo = new DateTime().minusDays(PluginSettingsAuditLogAccessor.this.getPurgeAfter());
                return input.getDate().after(nDaysAgo.toDate());
            }
        };
        this.mapper = new ObjectMapper((JsonFactory)new MappingJsonFactory());
        this.uriBuilder = uriBuilder;
        this.clock = Objects.requireNonNull(clock);
        this.keyPrefix = keyPrefix;
    }

    public PluginSettingsAuditLogAccessor(I18nResolver i18nResolver, ApplicationProperties applicationProperties, UserManager userManager, PluginSettingsFactory pluginSettingsFactory, BaseUriBuilder uriBuilder, String keyPrefix) {
        this(i18nResolver, applicationProperties, userManager, pluginSettingsFactory, uriBuilder, new SystemClock(), keyPrefix);
    }

    public synchronized Iterable<AuditLogEntry> getLogEntries() {
        return this.getLogEntries(null, null);
    }

    public synchronized Collection<AuditLogEntry> getLogEntries(Integer maxResults, Integer startIndex) {
        return this.getFeedData(maxResults, startIndex).entries;
    }

    public synchronized Iterable<AuditLogEntry> getLogEntries(Integer maxResults, Integer startIndex, Set<EntryType> entryTypes) {
        return this.getFeedData(maxResults, startIndex, entryTypes).entries;
    }

    private synchronized FeedData getFeedData(Integer maxResults, Integer startIndex) {
        return this.getFeedData(maxResults, startIndex, Arrays.stream(EntryType.values()).collect(Collectors.toSet()));
    }

    protected void setLastRetrievedToCurrentTime() {
        this.setLastRetrieved(new DateTime(this.clock.currentTimeMillis()));
    }

    private void setLastRetrieved(DateTime lastRetrieved) {
        this.getPluginSettings().put(UPM_AUDIT_LOG_LAST_RETRIEVED, (Object)Long.toString(lastRetrieved.getMillis()));
    }

    protected DateTime getLastRetrieved() {
        String lastRetrieved = (String)this.getPluginSettings().get(UPM_AUDIT_LOG_LAST_RETRIEVED);
        if (lastRetrieved == null) {
            return new DateTime(0L);
        }
        return new DateTime((Object)Long.valueOf(lastRetrieved));
    }

    private synchronized FeedData getFeedData(Integer maxResults, Integer startIndex, Set<EntryType> entryTypes) {
        this.setLastRetrievedToCurrentTime();
        ImmutableList.Builder entries = ImmutableList.builder();
        ImmutableList log = ImmutableList.copyOf((Iterable)Iterables.filter(this.purgeEntryStringsAndTransform(this.getSavedEntriesAsStrings()), (Predicate)new EntryWithTypes(entryTypes)));
        int totalEntries = log.size();
        if (startIndex == null) {
            startIndex = 0;
        }
        if (maxResults == null) {
            maxResults = totalEntries;
        }
        try {
            entries.addAll((Iterable)log.subList(startIndex.intValue(), Math.min(maxResults + startIndex, totalEntries)));
        }
        catch (IllegalArgumentException illegalArgumentException) {
            // empty catch block
        }
        return new FeedData(startIndex, maxResults, totalEntries, (Collection<AuditLogEntry>)entries.build());
    }

    public synchronized Feed getFeed() {
        return this.getFeed(null, null);
    }

    public synchronized Feed getFeed(Integer maxResults, Integer startIndex) {
        Feed feed = new Feed();
        feed.setTitle("Plugin management log for " + this.applicationProperties.getDisplayName() + " (" + this.applicationProperties.getBaseUrl() + ")");
        feed.setModified(this.getLastModified());
        this.addLink(feed, this.applicationProperties.getBaseUrl(), "base");
        this.addAuditLogEntries(feed, maxResults, startIndex);
        return feed;
    }

    protected Date getLastModified() {
        Iterator<AuditLogEntry> iterator = this.getLogEntries(1, 0).iterator();
        if (iterator.hasNext()) {
            AuditLogEntry entry = iterator.next();
            return entry.getDate();
        }
        return new Date();
    }

    public int getMaxEntries() {
        String maxEntries = (String)this.getPluginSettings().get(UPM_AUDIT_LOG_MAX_ENTRIES);
        if (maxEntries == null) {
            return 5000;
        }
        return Integer.valueOf(maxEntries);
    }

    public int getPurgeAfter() {
        String purgeAfter = (String)this.getPluginSettings().get(UPM_AUDIT_LOG_PURGE_AFTER);
        if (purgeAfter == null) {
            return 90;
        }
        return Integer.valueOf(purgeAfter);
    }

    private void addAuditLogEntries(Feed feed, Integer maxResults, Integer startIndex) {
        FeedData feedData = this.getFeedData(maxResults, startIndex);
        this.addTotalEntriesMarkup(feed, feedData.totalEntries);
        this.addStartIndexMarkup(feed, feedData.startIndex);
        int nextPageStartIndex = feedData.startIndex + feedData.maxResults;
        int previousPageStartIndex = Math.max(feedData.startIndex - feedData.maxResults, 0);
        int firstPageStartIndex = 0;
        int lastPageStartIndex = (int)Math.floor((feedData.totalEntries - 1) / feedData.maxResults) * feedData.maxResults;
        if (nextPageStartIndex < feedData.totalEntries) {
            this.addLink(feed, this.uriBuilder.buildAuditLogFeedUri(feedData.maxResults, nextPageStartIndex), "next");
            this.addLink(feed, this.uriBuilder.buildAuditLogFeedUri(feedData.maxResults, lastPageStartIndex), "last");
        }
        if (feedData.startIndex > 0) {
            this.addLink(feed, this.uriBuilder.buildAuditLogFeedUri(feedData.maxResults, firstPageStartIndex), "first");
            this.addLink(feed, this.uriBuilder.buildAuditLogFeedUri(feedData.maxResults, previousPageStartIndex), "previous");
        }
        feed.getEntries().addAll(ImmutableList.copyOf((Iterable)Iterables.transform((Iterable)feedData.entries, this.auditLogEntryToFeedEntryFn())));
    }

    private void addTotalEntriesMarkup(Feed feed, int totalEntries) {
        this.addForeignMarkup(feed, "totalEntries", String.valueOf(totalEntries));
    }

    private void addStartIndexMarkup(Feed feed, int startIndex) {
        this.addForeignMarkup(feed, "startIndex", String.valueOf(startIndex));
    }

    private void addForeignMarkup(Feed feed, String name, String value) {
        Element elem = new Element(name);
        elem.setText(String.valueOf(value));
        feed.getForeignMarkup().add(elem);
    }

    private Function<AuditLogEntry, Entry> auditLogEntryToFeedEntryFn() {
        return new Function<AuditLogEntry, Entry>(){

            public Entry apply(AuditLogEntry from) {
                Entry entry = new Entry();
                entry.setUpdated(from.getDate());
                entry.setTitle(from.getTitle(PluginSettingsAuditLogAccessor.this.i18nResolver));
                String userKey = from.getUserKey();
                Person person = PluginSettingsAuditLogAccessor.this.generatePerson(userKey);
                entry.setAuthors((List)ImmutableList.of((Object)person));
                return entry;
            }
        };
    }

    private Person generatePerson(String userKey) {
        String product = this.applicationProperties.getDisplayName();
        Person person = new Person();
        if (this.i18nResolver.getText("upm.auditLog.anonymous").equals(userKey) || product.equals(userKey)) {
            person.setName(userKey);
        } else {
            UserProfile userProfile = this.userManager.getUserProfile(new UserKey(userKey));
            if (userProfile != null) {
                String userFullname = userProfile.getFullName();
                person.setName(userFullname != null ? userFullname : userProfile.getUsername());
                URI userUri = this.uriBuilder.buildAbsoluteProfileUri(userProfile);
                if (userUri != null) {
                    person.setUrl(userUri.toString());
                }
            } else {
                person.setName(userKey);
            }
        }
        return person;
    }

    private void addLink(Feed feed, String url, String rel) {
        Link link = new Link();
        link.setHref(url);
        link.setRel(rel);
        feed.getOtherLinks().add(link);
    }

    private void addLink(Feed feed, URI uri, String rel) {
        this.addLink(feed, uri.toString(), rel);
    }

    private Iterable<String> getSavedEntriesAsStrings() {
        Object entries = this.getPluginSettings().get(UPM_AUDIT_LOG);
        if (entries == null) {
            return Collections.emptyList();
        }
        if (!(entries instanceof List)) {
            log.error("Invalid audit log storage has been detected: " + entries);
            return Collections.emptyList();
        }
        return ImmutableList.copyOf((Collection)((List)entries));
    }

    private Collection<AuditLogEntry> purgeEntryStringsAndTransform(Iterable<String> stringEntries) {
        ImmutableList entries = ImmutableList.copyOf((Iterable)Iterables.transform(stringEntries, (Function)new Function<String, AuditLogEntry>(){

            public AuditLogEntry apply(String from) {
                try {
                    return (AuditLogEntry)PluginSettingsAuditLogAccessor.this.mapper.readValue(from, AuditLogEntryImpl.class);
                }
                catch (IOException e) {
                    throw new RuntimeException("Failed to parse AuditLogEntry from JSON string: " + from, e);
                }
            }
        }));
        return this.purgeEntriesAndTransform((Iterable<AuditLogEntry>)entries);
    }

    private Collection<AuditLogEntry> purgeEntriesAndTransform(Iterable<AuditLogEntry> entries) {
        entries = Iterables.filter(entries, this.purgePolicy);
        return Ordering.natural().reverse().sortedCopy(entries);
    }

    protected PluginSettings getPluginSettings() {
        return new NamespacedPluginSettings(this.pluginSettingsFactory.createGlobalSettings(), this.keyPrefix);
    }

    public static class SystemClock
    implements Clock {
        @Override
        public long currentTimeMillis() {
            return System.currentTimeMillis();
        }
    }

    public static interface Clock {
        public long currentTimeMillis();
    }

    private static class EntryWithTypes
    implements Predicate<AuditLogEntry> {
        private final Set<EntryType> entryTypes;

        public EntryWithTypes(Set<EntryType> entryTypes) {
            this.entryTypes = ImmutableSet.copyOf(entryTypes);
        }

        public boolean apply(AuditLogEntry entry) {
            return this.entryTypes.contains((Object)entry.getEntryType());
        }
    }

    private class FeedData {
        private int startIndex;
        private int maxResults;
        private int totalEntries;
        private Collection<AuditLogEntry> entries;

        FeedData(int startIndex, int maxResults, int totalEntries, Collection<AuditLogEntry> entries) {
            this.startIndex = startIndex;
            this.maxResults = maxResults;
            this.totalEntries = totalEntries;
            this.entries = entries;
        }
    }
}

