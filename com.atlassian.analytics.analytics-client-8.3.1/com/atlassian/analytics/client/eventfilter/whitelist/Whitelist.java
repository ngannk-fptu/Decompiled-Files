/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.analytics.client.eventfilter.whitelist;

import com.atlassian.analytics.client.eventfilter.whitelist.FilteredEventAttributes;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

public class Whitelist {
    private String whitelistId;
    private final boolean isGlobalWhitelist;
    private volatile ImmutableMap<String, FilteredEventAttributes> filteredEventAttributesMap = ImmutableMap.of();

    public static Whitelist createEmptyWhitelist() {
        return new Whitelist("", new HashMap<String, FilteredEventAttributes>(), false);
    }

    public Whitelist(String whitelistId, Map<String, FilteredEventAttributes> filteredEventAttributesMap, boolean isGlobalWhitelist) {
        this.isGlobalWhitelist = isGlobalWhitelist;
        this.initialiseFrom(whitelistId, filteredEventAttributesMap);
    }

    public void initialiseFrom(Whitelist globalWhitelist) {
        this.initialiseFrom(globalWhitelist.whitelistId, (Map<String, FilteredEventAttributes>)globalWhitelist.filteredEventAttributesMap);
    }

    private void initialiseFrom(String whitelistId, Map<String, FilteredEventAttributes> filteredEventAttributesMap) {
        this.whitelistId = whitelistId;
        this.initialiseFrom(filteredEventAttributesMap);
    }

    public void initialiseFrom(Map<String, FilteredEventAttributes> filteredEventAttributesMap) {
        if (filteredEventAttributesMap != null) {
            this.filteredEventAttributesMap = ImmutableMap.copyOf(filteredEventAttributesMap);
        }
    }

    public boolean isEventWhitelisted(String eventName) {
        return this.filteredEventAttributesMap.containsKey((Object)eventName);
    }

    public boolean shouldAttributeBeHashed(String eventName, String attributeName) {
        FilteredEventAttributes filteredEventAttributes = (FilteredEventAttributes)this.filteredEventAttributesMap.get((Object)eventName);
        return filteredEventAttributes != null && filteredEventAttributes.hasHashedAttribute(attributeName);
    }

    public boolean shouldAttributeBeDictionaryFiltered(String eventName, String attributeName) {
        FilteredEventAttributes filteredEventAttributes = (FilteredEventAttributes)this.filteredEventAttributesMap.get((Object)eventName);
        return filteredEventAttributes != null && filteredEventAttributes.hasDictionaryFilteredAttribute(attributeName);
    }

    public boolean shouldAttributeBeWhitelisted(String eventName, String attributeName) {
        FilteredEventAttributes filteredEventAttributes = (FilteredEventAttributes)this.filteredEventAttributesMap.get((Object)eventName);
        return filteredEventAttributes != null && filteredEventAttributes.hasWhitelistedAttribute(attributeName);
    }

    FilteredEventAttributes getFilteredEventAttributes(String eventName) {
        return (FilteredEventAttributes)this.filteredEventAttributesMap.get((Object)eventName);
    }

    public Map<String, FilteredEventAttributes> getFilteredEventAttributesMap() {
        return this.filteredEventAttributesMap;
    }

    public boolean isGlobalWhitelist() {
        return this.isGlobalWhitelist;
    }

    public String getWhitelistId() {
        return this.whitelistId;
    }

    public WhitelistBean toWhitelistBean() {
        return new WhitelistBean(this);
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class WhitelistBean {
        @JsonProperty
        private final String whitelistId;
        @JsonProperty
        private final Map<String, FilteredEventAttributes> filteredEvents;

        public WhitelistBean(Whitelist whitelist) {
            this.whitelistId = whitelist.whitelistId;
            this.filteredEvents = whitelist.filteredEventAttributesMap;
        }
    }
}

