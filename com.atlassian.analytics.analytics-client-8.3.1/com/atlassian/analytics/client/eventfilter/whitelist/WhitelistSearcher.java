/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.analytics.client.eventfilter.whitelist;

import com.atlassian.analytics.client.eventfilter.whitelist.FilteredEventAttributes;
import com.atlassian.analytics.client.eventfilter.whitelist.Whitelist;
import com.atlassian.analytics.client.eventfilter.whitelist.WhitelistFilter;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

public class WhitelistSearcher {
    private final WhitelistFilter whitelistFilter;

    public WhitelistSearcher(WhitelistFilter whitelistFilter) {
        this.whitelistFilter = whitelistFilter;
    }

    public List<SearchResultItem> search(String query, String whitelistId, int maxResults) {
        String lowercaseQuery = (query == null ? "" : query).toLowerCase();
        ArrayList<SearchResultItem> result = new ArrayList<SearchResultItem>();
        this.search(lowercaseQuery, whitelistId, this.whitelistFilter.getGlobalWhitelist(), result, maxResults);
        for (Whitelist whitelist : this.whitelistFilter.getPluginWhitelists()) {
            if (result.size() >= maxResults) break;
            this.search(lowercaseQuery, whitelistId, whitelist, result, maxResults);
        }
        return result;
    }

    private void search(String lowercaseQuery, String whitelistId, Whitelist whitelist, List<SearchResultItem> result, int maxResults) {
        if (StringUtils.isBlank((CharSequence)whitelistId) || whitelist.getWhitelistId().equals(whitelistId)) {
            for (String eventName : whitelist.getFilteredEventAttributesMap().keySet()) {
                if (result.size() >= maxResults) break;
                if (!eventName.toLowerCase().contains(lowercaseQuery)) continue;
                result.add(new SearchResultItem(whitelist.getWhitelistId(), eventName, whitelist.getFilteredEventAttributesMap().get(eventName)));
            }
        }
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class SearchResultItem {
        @JsonProperty
        private final String whitelistId;
        @JsonProperty
        private final String eventName;
        @JsonProperty
        private final FilteredEventAttributes filteredEventAttributes;

        public SearchResultItem(String whitelistId, String eventName, FilteredEventAttributes filteredEventAttributes) {
            this.whitelistId = whitelistId;
            this.eventName = eventName;
            this.filteredEventAttributes = filteredEventAttributes;
        }

        public String getWhitelistId() {
            return this.whitelistId;
        }

        public String getEventName() {
            return this.eventName;
        }

        public FilteredEventAttributes getFilteredEventAttributes() {
            return this.filteredEventAttributes;
        }
    }
}

