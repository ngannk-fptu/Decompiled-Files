/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.client.eventfilter.whitelist;

import com.atlassian.analytics.client.eventfilter.whitelist.FilteredEventAttributes;
import com.atlassian.analytics.client.eventfilter.whitelist.Whitelist;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AggregatedWhitelist {
    private volatile List<Whitelist> externalWhitelists = Collections.emptyList();

    public static AggregatedWhitelist createEmptyAggregate() {
        return new AggregatedWhitelist();
    }

    public void initialiseFrom(List<Whitelist> externalWhitelists) {
        this.externalWhitelists = Collections.unmodifiableList(new ArrayList<Whitelist>(externalWhitelists));
    }

    public boolean isEventWhitelisted(String eventName) {
        return this.getFilteredEventAttributes(eventName).findAny().isPresent();
    }

    public boolean isAttributeValueInAllowedList(String eventName, String attributeName, String attributeValue) {
        return this.getFilteredEventAttributes(eventName).anyMatch(fea -> fea.hasAllowedAttributeValue(attributeName, attributeValue));
    }

    public boolean shouldAttributeBeHashed(String eventName, String attributeName) {
        return this.getFilteredEventAttributes(eventName).anyMatch(fea -> fea.hasHashedAttribute(attributeName));
    }

    public boolean shouldAttributeBeDictionaryFiltered(String eventName, String attributeName) {
        return this.getFilteredEventAttributes(eventName).anyMatch(fea -> fea.hasDictionaryFilteredAttribute(attributeName));
    }

    public boolean shouldAttributeBeWhitelisted(String eventName, String attributeName) {
        return this.getFilteredEventAttributes(eventName).anyMatch(fea -> fea.hasWhitelistedAttribute(attributeName));
    }

    public List<Whitelist> getWhitelists() {
        return this.externalWhitelists;
    }

    public Collection<Whitelist.WhitelistBean> toWhitelistBeans() {
        return this.externalWhitelists.stream().map(Whitelist.WhitelistBean::new).collect(Collectors.toList());
    }

    private Stream<FilteredEventAttributes> getFilteredEventAttributes(String eventName) {
        return this.externalWhitelists.stream().map(whitelist -> whitelist.getFilteredEventAttributes(eventName)).filter(Objects::nonNull);
    }
}

