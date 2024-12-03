/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.google.common.base.Throwables
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.Maps
 *  io.atlassian.util.concurrent.LazyReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.analytics.client.eventfilter.whitelist;

import com.atlassian.analytics.client.eventfilter.AllowedWordFilter;
import com.atlassian.analytics.client.eventfilter.whitelist.AggregatedWhitelist;
import com.atlassian.analytics.client.eventfilter.whitelist.Whitelist;
import com.atlassian.analytics.client.eventfilter.whitelist.WhitelistCollector;
import com.atlassian.analytics.client.logger.EventAnonymizer;
import com.atlassian.analytics.client.pipeline.serialize.properties.extractors.mau.IsMauEventAvailable;
import com.atlassian.analytics.event.RawEvent;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import io.atlassian.util.concurrent.LazyReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WhitelistFilter {
    private static final Logger LOG = LoggerFactory.getLogger(WhitelistFilter.class);
    private static final Pattern EXPERIMENT_PATTERN = Pattern.compile("^grow-?\\d+[^a-zA-Z0-9].+$", 2);
    static final String DARK_FEATURE_AUTO_WHITELIST_GROW_EVENTS_KEY = "com.atlassian.analytics.auto.whitelist.grow.events";
    private final LoadingCache<EventKey, Boolean> hashedCache = CacheBuilder.newBuilder().build((CacheLoader)new CacheLoader<EventKey, Boolean>(){

        public Boolean load(EventKey key) throws Exception {
            return WhitelistFilter.this.globalWhitelist.shouldAttributeBeHashed(key.event, key.property) || WhitelistFilter.this.pluginWhitelists.shouldAttributeBeHashed(key.event, key.property);
        }
    });
    private final LoadingCache<EventKey, Boolean> dictionaryFilteredCache = CacheBuilder.newBuilder().build((CacheLoader)new CacheLoader<EventKey, Boolean>(){

        public Boolean load(EventKey key) throws Exception {
            return WhitelistFilter.this.globalWhitelist.shouldAttributeBeDictionaryFiltered(key.event, key.property) || WhitelistFilter.this.pluginWhitelists.shouldAttributeBeDictionaryFiltered(key.event, key.property);
        }
    });
    private final LoadingCache<EventKey, Boolean> whitelistedCache = CacheBuilder.newBuilder().build((CacheLoader)new CacheLoader<EventKey, Boolean>(){

        public Boolean load(EventKey key) throws Exception {
            return WhitelistFilter.this.globalWhitelist.shouldAttributeBeWhitelisted(key.event, key.property) || WhitelistFilter.this.pluginWhitelists.shouldAttributeBeWhitelisted(key.event, key.property);
        }
    });
    private final AllowedWordFilter allowedWordFilter;
    private final WhitelistCollector whitelistCollector;
    private final EventAnonymizer eventAnonymizer;
    private final Whitelist globalWhitelist;
    private final AggregatedWhitelist pluginWhitelists;
    private final DarkFeatureManager darkFeatureManager;
    private final LazyReference<Boolean> isMauEventAvailable = new IsMauEventAvailable();
    private Boolean autoWhitelistGrow = null;

    public WhitelistFilter(AllowedWordFilter allowedWordFilter, WhitelistCollector whitelistCollector, EventAnonymizer eventAnonymizer, DarkFeatureManager darkFeatureManager) {
        this.allowedWordFilter = allowedWordFilter;
        this.whitelistCollector = whitelistCollector;
        this.eventAnonymizer = eventAnonymizer;
        this.globalWhitelist = Whitelist.createEmptyWhitelist();
        this.pluginWhitelists = AggregatedWhitelist.createEmptyAggregate();
        this.darkFeatureManager = darkFeatureManager;
    }

    private void initialiseWhitelists() {
        List<Whitelist> externalWhitelists = this.whitelistCollector.collectExternalWhitelists();
        Whitelist globalWhitelist = this.getGlobalWhitelist(externalWhitelists);
        if (globalWhitelist != null) {
            this.globalWhitelist.initialiseFrom(globalWhitelist);
            externalWhitelists.remove(globalWhitelist);
        }
        this.pluginWhitelists.initialiseFrom(externalWhitelists);
        this.hashedCache.invalidateAll();
        this.dictionaryFilteredCache.invalidateAll();
        this.whitelistedCache.invalidateAll();
    }

    private Whitelist getGlobalWhitelist(List<Whitelist> externalWhitelists) {
        for (Whitelist whitelist : externalWhitelists) {
            if (!whitelist.isGlobalWhitelist()) continue;
            return whitelist;
        }
        return null;
    }

    public boolean isEventWhitelisted(RawEvent event) {
        String eventName = event.getName();
        return this.isEventAlwaysWhitelisted(eventName) || this.isEventWhitelisted(eventName);
    }

    private boolean isEventAlwaysWhitelisted(String eventName) {
        if (this.autoWhitelistGrow == null) {
            this.autoWhitelistGrow = this.darkFeatureManager.isFeatureEnabledForCurrentUser(DARK_FEATURE_AUTO_WHITELIST_GROW_EVENTS_KEY);
        }
        boolean isGrowthExperimentAllowed = this.autoWhitelistGrow != false && EXPERIMENT_PATTERN.matcher(eventName).find();
        boolean isMauEvent = (Boolean)this.isMauEventAvailable.get() != false && "UserActivity".equals(eventName);
        return isGrowthExperimentAllowed || isMauEvent;
    }

    public Map<String, Object> applyWhitelistToEvent(String eventName, Map<String, Object> rawProperties) {
        HashMap processedProperties = Maps.newHashMap(rawProperties);
        if (this.isEventAlwaysWhitelisted(eventName) && !this.isEventWhitelisted(eventName)) {
            return processedProperties;
        }
        HashSet<String> removeProperties = new HashSet<String>();
        for (Map.Entry<String, Object> property : rawProperties.entrySet()) {
            String propertyName = property.getKey();
            if (this.isAttributeOnLocalWhitelist(eventName, propertyName)) {
                Object originalPropertyValue = property.getValue();
                if (originalPropertyValue == null || originalPropertyValue instanceof Number || originalPropertyValue instanceof Boolean) continue;
                if (originalPropertyValue instanceof Enum) {
                    processedProperties.put(propertyName, ((Enum)originalPropertyValue).name());
                    continue;
                }
                String propertyValue = String.valueOf(originalPropertyValue);
                if (this.shouldAttributeValueBeWhitelisted(eventName, propertyName, propertyValue)) continue;
                if (this.shouldAttributeBeHashed(eventName, propertyName)) {
                    this.hashProperty(processedProperties, propertyName, propertyValue);
                    continue;
                }
                if (this.shouldAttributeBeWhitelisted(eventName, propertyName)) continue;
                if (originalPropertyValue instanceof CharSequence) {
                    this.applyAllowedWordsFiltering(eventName, processedProperties, propertyName, propertyValue);
                    continue;
                }
            }
            removeProperties.add(propertyName);
        }
        removeProperties.forEach(processedProperties::remove);
        return processedProperties;
    }

    private boolean isEventWhitelisted(String eventName) {
        return this.globalWhitelist.isEventWhitelisted(eventName) || this.pluginWhitelists.isEventWhitelisted(eventName);
    }

    private boolean shouldAttributeValueBeWhitelisted(String eventName, String propertyName, String propertyValue) {
        return this.pluginWhitelists.isAttributeValueInAllowedList(eventName, propertyName, propertyValue);
    }

    private boolean shouldAttributeBeHashed(String eventName, String propertyName) {
        final EventKey key = EventKey.eventKey(eventName, propertyName);
        try {
            return (Boolean)this.hashedCache.get((Object)key, (Callable)new Callable<Boolean>(){

                @Override
                public Boolean call() throws Exception {
                    return WhitelistFilter.this.globalWhitelist.shouldAttributeBeHashed(key.event, key.property) || WhitelistFilter.this.pluginWhitelists.shouldAttributeBeHashed(key.event, key.property);
                }
            });
        }
        catch (ExecutionException e) {
            throw Throwables.propagate((Throwable)e);
        }
    }

    private boolean isAttributeOnLocalWhitelist(String eventName, String propertyName) {
        final EventKey key = EventKey.eventKey(eventName, propertyName);
        try {
            return (Boolean)this.dictionaryFilteredCache.get((Object)key, (Callable)new Callable<Boolean>(){

                @Override
                public Boolean call() throws Exception {
                    return WhitelistFilter.this.globalWhitelist.shouldAttributeBeDictionaryFiltered(key.event, key.property) || WhitelistFilter.this.pluginWhitelists.shouldAttributeBeDictionaryFiltered(key.event, key.property);
                }
            });
        }
        catch (ExecutionException e) {
            throw Throwables.propagate((Throwable)e);
        }
    }

    private boolean shouldAttributeBeWhitelisted(String eventName, String propertyName) {
        final EventKey key = EventKey.eventKey(eventName, propertyName);
        try {
            return (Boolean)this.whitelistedCache.get((Object)key, (Callable)new Callable<Boolean>(){

                @Override
                public Boolean call() throws Exception {
                    return WhitelistFilter.this.globalWhitelist.shouldAttributeBeWhitelisted(key.event, key.property) || WhitelistFilter.this.pluginWhitelists.shouldAttributeBeWhitelisted(key.event, key.property);
                }
            });
        }
        catch (ExecutionException e) {
            throw Throwables.propagate((Throwable)e);
        }
    }

    private void hashProperty(Map<String, Object> properties, String propertyName, String propertyValue) {
        properties.put(propertyName, this.eventAnonymizer.hashEventProperty(propertyValue));
    }

    private void applyAllowedWordsFiltering(String eventName, Map<String, Object> properties, String propertyName, String propertyValue) {
        String processedPropertyValue = this.allowedWordFilter.processAllowedWords(propertyValue);
        if (LOG.isDebugEnabled() && processedPropertyValue.isEmpty()) {
            LOG.debug("Discarded value for property {} of event {}", (Object)propertyName, (Object)eventName);
        }
        properties.put(propertyName, processedPropertyValue);
    }

    public void collectExternalWhitelists() {
        this.initialiseWhitelists();
    }

    public Whitelist getGlobalWhitelist() {
        return this.globalWhitelist;
    }

    public List<Whitelist> getPluginWhitelists() {
        return this.pluginWhitelists.getWhitelists();
    }

    public List<Whitelist.WhitelistBean> toWhitelistBeans() {
        ArrayList<Whitelist.WhitelistBean> whitelistBeans = new ArrayList<Whitelist.WhitelistBean>();
        whitelistBeans.add(this.globalWhitelist.toWhitelistBean());
        whitelistBeans.addAll(this.pluginWhitelists.toWhitelistBeans());
        return whitelistBeans;
    }

    static class EventKey {
        final String event;
        final String property;

        static EventKey eventKey(String event, String property) {
            return new EventKey(event, property);
        }

        private EventKey(String event, String property) {
            this.event = event;
            this.property = property;
        }

        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (this == o) {
                return true;
            }
            if (!(o instanceof EventKey)) {
                return false;
            }
            EventKey that = (EventKey)o;
            return this.event.equals(that.event) && this.property.equals(that.property);
        }

        public int hashCode() {
            int eh = this.event.hashCode();
            int ph = this.property.hashCode();
            return 31 * eh + ph;
        }
    }
}

