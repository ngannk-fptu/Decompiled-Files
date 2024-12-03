/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.streams.api.StreamsFilterType
 *  com.atlassian.streams.spi.ActivityOptions
 *  com.atlassian.streams.spi.StreamsFilterOption
 *  com.atlassian.streams.spi.StreamsFilterOption$Builder
 *  com.atlassian.streams.spi.StreamsFilterOptionProvider
 *  com.atlassian.streams.spi.StreamsFilterOptionProvider$ActivityOption
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Ordering
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.streams.internal.rest.representations;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.streams.api.StreamsFilterType;
import com.atlassian.streams.internal.rest.representations.FilterOptionRepresentation;
import com.atlassian.streams.spi.ActivityOptions;
import com.atlassian.streams.spi.StreamsFilterOption;
import com.atlassian.streams.spi.StreamsFilterOptionProvider;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import java.util.Collection;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ProviderFilterRepresentation {
    private static final String KEY = "key";
    @JsonProperty
    final String key;
    @JsonProperty
    final String name;
    @JsonProperty
    final String applinkName;
    @JsonProperty
    final Collection<FilterOptionRepresentation> options;
    @JsonProperty
    final String providerAliasOptionKey;
    private static final Ordering<FilterOptionRepresentation> optionAlphaSorter = new Ordering<FilterOptionRepresentation>(){

        public int compare(FilterOptionRepresentation option1, FilterOptionRepresentation option2) {
            if (option1.getKey().equals(ProviderFilterRepresentation.KEY) && option2.getKey().equals(ProviderFilterRepresentation.KEY)) {
                return 0;
            }
            if (option1.getKey().equals(ProviderFilterRepresentation.KEY)) {
                return -1;
            }
            if (option2.getKey().equals(ProviderFilterRepresentation.KEY)) {
                return 1;
            }
            return option1.getName().compareTo(option2.getName());
        }
    };
    private static final Ordering<StreamsFilterOptionProvider.ActivityOption> activityAlphaSorter = new Ordering<StreamsFilterOptionProvider.ActivityOption>(){

        public int compare(StreamsFilterOptionProvider.ActivityOption option1, StreamsFilterOptionProvider.ActivityOption option2) {
            return option1.getDisplayName().compareTo(option2.getDisplayName());
        }
    };

    @JsonCreator
    public ProviderFilterRepresentation(@JsonProperty(value="key") String key, @JsonProperty(value="name") String name, @JsonProperty(value="applinkName") String applinkName, @JsonProperty(value="options") Collection<FilterOptionRepresentation> options, @JsonProperty(value="providerAliasOptionKey") String providerAliasOptionKey) {
        this.key = key;
        this.name = name;
        this.applinkName = applinkName;
        this.options = optionAlphaSorter.sortedCopy(options);
        this.providerAliasOptionKey = providerAliasOptionKey;
    }

    public ProviderFilterRepresentation(String key, String name, String applinkName, StreamsFilterOptionProvider provider, I18nResolver i18nResolver) {
        this.key = key;
        this.name = name;
        this.applinkName = applinkName;
        if (provider.getFilterOptions() == null) {
            this.options = null;
            this.providerAliasOptionKey = null;
        } else {
            Iterable options = provider.getFilterOptions();
            this.options = optionAlphaSorter.sortedCopy(Iterables.concat((Iterable)Iterables.transform((Iterable)options, FilterOptionRepresentation.toFilterOptionEntry(i18nResolver)), (Iterable)ImmutableList.of((Object)this.activityOptionEntry(provider, i18nResolver))));
            this.providerAliasOptionKey = this.findProviderAliasOptionKey(options);
        }
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public String getApplinkName() {
        return this.applinkName;
    }

    public Collection<FilterOptionRepresentation> getOptions() {
        return this.options;
    }

    public String getProviderAliasOptionKey() {
        return this.providerAliasOptionKey;
    }

    public String toString() {
        if (StringUtils.isEmpty((CharSequence)this.applinkName)) {
            return this.name;
        }
        return this.name + "@" + this.applinkName;
    }

    private String findProviderAliasOptionKey(Iterable<StreamsFilterOption> options) {
        for (StreamsFilterOption option : options) {
            if (!option.isProviderAlias()) continue;
            return option.getKey();
        }
        return null;
    }

    private FilterOptionRepresentation activityOptionEntry(StreamsFilterOptionProvider provider, I18nResolver i18nResolver) {
        StreamsFilterOption activityOption = new StreamsFilterOption.Builder("activity", StreamsFilterType.SELECT).displayName(i18nResolver.getText("streams.filter.option.activity")).helpTextI18nKey("streams.filter.option.help.activity").i18nKey("streams.filter.option.activity").unique(true).values(Maps.transformValues((Map)Maps.uniqueIndex((Iterable)activityAlphaSorter.sortedCopy(provider.getActivities()), (Function)ActivityOptions.toActivityOptionKey()), this.toDisplayName())).build();
        return new FilterOptionRepresentation(i18nResolver, activityOption);
    }

    private Function<StreamsFilterOptionProvider.ActivityOption, String> toDisplayName() {
        return ActivityOptionToDisplayName.INSTANCE;
    }

    private static enum ActivityOptionToDisplayName implements Function<StreamsFilterOptionProvider.ActivityOption, String>
    {
        INSTANCE;


        public String apply(StreamsFilterOptionProvider.ActivityOption a) {
            return a.getDisplayName();
        }
    }
}

