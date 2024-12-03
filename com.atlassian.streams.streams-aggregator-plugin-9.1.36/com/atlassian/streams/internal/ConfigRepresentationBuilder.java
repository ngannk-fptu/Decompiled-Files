/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.application.jira.JiraApplicationType
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.streams.api.common.Either
 *  com.atlassian.streams.spi.FormatPreferenceProvider
 *  com.atlassian.streams.spi.StandardStreamsFilterOption
 *  com.google.common.base.Function
 *  com.google.common.base.Functions
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimaps
 *  org.apache.commons.lang3.StringUtils
 *  org.joda.time.DateTimeZone
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.streams.internal;

import com.atlassian.applinks.api.application.jira.JiraApplicationType;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.streams.api.common.Either;
import com.atlassian.streams.internal.ActivityProvider;
import com.atlassian.streams.internal.ActivityProviderCallable;
import com.atlassian.streams.internal.ActivityProviderWithAnalytics;
import com.atlassian.streams.internal.ActivityProviders;
import com.atlassian.streams.internal.AppLinksActivityProvider;
import com.atlassian.streams.internal.ProviderFilterOrdering;
import com.atlassian.streams.internal.StreamsCompletionService;
import com.atlassian.streams.internal.rest.representations.ConfigPreferencesRepresentation;
import com.atlassian.streams.internal.rest.representations.FilterOptionRepresentation;
import com.atlassian.streams.internal.rest.representations.ProviderFilterRepresentation;
import com.atlassian.streams.internal.rest.representations.StreamsConfigRepresentation;
import com.atlassian.streams.internal.rest.representations.StreamsKeysRepresentation;
import com.atlassian.streams.spi.FormatPreferenceProvider;
import com.atlassian.streams.spi.StandardStreamsFilterOption;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Qualifier;

public final class ConfigRepresentationBuilder {
    private final ActivityProviders activityProviders;
    private final StreamsCompletionService completionService;
    private final I18nResolver i18nResolver;
    private final ApplicationProperties applicationProperties;
    private final FormatPreferenceProvider preferenceProvider;

    ConfigRepresentationBuilder(ActivityProviders activityProviders, StreamsCompletionService completionService, @Qualifier(value="streamsI18nResolver") I18nResolver i18nResolver, ApplicationProperties applicationProperties, FormatPreferenceProvider formatPreferenceProvider) {
        this.activityProviders = (ActivityProviders)Preconditions.checkNotNull((Object)activityProviders, (Object)"activityProviders");
        this.completionService = (StreamsCompletionService)Preconditions.checkNotNull((Object)completionService, (Object)"completionService");
        this.i18nResolver = (I18nResolver)Preconditions.checkNotNull((Object)i18nResolver, (Object)"i18nResolver");
        this.applicationProperties = (ApplicationProperties)Preconditions.checkNotNull((Object)applicationProperties, (Object)"applicationProperties");
        this.preferenceProvider = (FormatPreferenceProvider)Preconditions.checkNotNull((Object)formatPreferenceProvider, (Object)"preferenceProvider");
    }

    public StreamsConfigRepresentation getConfigRepresentation(boolean local) {
        Iterable<ActivityProvider> localProviders = this.activityProviders.get(ActivityProviders.localOnly(true), this.completionService.reachable());
        Iterable<ActivityProvider> applicationProviders = this.activityProviders.get(ActivityProviders.localOnly(local), this.completionService.reachable());
        Map<String, Integer> providerCount = this.getApplicationTypeCount(Iterables.concat(localProviders, applicationProviders));
        return new StreamsConfigRepresentation((Collection<ProviderFilterRepresentation>)ImmutableList.builder().add((Object)this.getStandardFilterOptions(localProviders, applicationProviders)).addAll((Iterable)ProviderFilterOrdering.prioritizing(Iterables.transform(localProviders, this.getName())).sortedCopy(this.getProviderFilters(applicationProviders, providerCount))).build());
    }

    private Map<String, Integer> getApplicationTypeCount(Iterable<ActivityProvider> providers) {
        HashMap<String, Integer> providerMap = new HashMap<String, Integer>();
        for (ActivityProvider provider : providers) {
            int count = 1;
            if (providerMap.containsKey(provider.getType())) {
                count = (Integer)providerMap.get(provider.getType()) + 1;
            }
            providerMap.put(provider.getType(), count);
        }
        return ImmutableMap.copyOf(providerMap);
    }

    public ConfigPreferencesRepresentation getConfigPreferencesRepresentation() {
        return new ConfigPreferencesRepresentation(this.preferenceProvider.getDateFormatPreference(), this.preferenceProvider.getTimeFormatPreference(), this.preferenceProvider.getDateTimeFormatPreference(), this.getUtcOffsetString(this.preferenceProvider.getUserTimeZone()), this.preferenceProvider.getDateRelativizePreference());
    }

    private String getUtcOffsetString(DateTimeZone timeZone) {
        int offset = timeZone.getOffset(new Date().getTime());
        int hour = Math.abs(offset / 3600000);
        int minute = Math.abs(offset / 60000) % 60;
        return (offset > 0 ? "+" : "-") + StringUtils.leftPad((String)String.valueOf(hour), (int)2, (String)"0") + StringUtils.leftPad((String)String.valueOf(minute), (int)2, (String)"0");
    }

    private Function<ActivityProvider, String> getName() {
        return GetName.INSTANCE;
    }

    private ProviderFilterRepresentation getStandardFilterOptions(Iterable<ActivityProvider> providers, Iterable<ActivityProvider> applicationProviders) {
        ByFilterConditions byFilterConditions = new ByFilterConditions(Iterables.concat(providers, applicationProviders));
        return new ProviderFilterRepresentation("streams", "", "", (Collection<FilterOptionRepresentation>)ImmutableList.builder().addAll(Iterables.transform((Iterable)Iterables.filter(Arrays.asList(StandardStreamsFilterOption.values()), (Predicate)byFilterConditions), FilterOptionRepresentation.toFilterOptionEntry(this.i18nResolver))).add((Object)this.newProjectOptionEntry(providers)).build(), null);
    }

    private boolean isLinkTypeJira(AppLinksActivityProvider applinksProvider) {
        return applinksProvider.getApplink().getType() instanceof JiraApplicationType;
    }

    private FilterOptionRepresentation newProjectOptionEntry(Iterable<ActivityProvider> providers) {
        return new FilterOptionRepresentation(this.i18nResolver, StandardStreamsFilterOption.projectKeys((Map)Maps.transformValues((Map)Multimaps.index((Iterable)Iterables.concat((Iterable)Iterables.transform(providers, (Function)Functions.compose(StreamsKeysRepresentation::getKeys, ActivityProvider::getKeys))), StreamsKeysRepresentation.StreamsKeyEntry::getKey).asMap(), (Function)Functions.compose(StreamsKeysRepresentation.StreamsKeyEntry::getLabel, keyEntries -> (StreamsKeysRepresentation.StreamsKeyEntry)Iterables.getFirst((Iterable)keyEntries, null))), (String)this.applicationProperties.getDisplayName()));
    }

    private Collection<ProviderFilterRepresentation> getProviderFilters(Iterable<ActivityProvider> providers, Map<String, Integer> providerCount) {
        Iterable callables = Iterables.transform(providers, this.toFiltersCallable(providerCount));
        return ImmutableList.copyOf((Iterable)Iterables.concat((Iterable)Either.getRights(this.completionService.execute(callables))));
    }

    private final Function<ActivityProvider, ActivityProviderCallable<Either<ActivityProvider.Error, Iterable<ProviderFilterRepresentation>>>> toFiltersCallable(final Map<String, Integer> providerCount) {
        return provider -> new ActivityProviderCallable<Either<ActivityProvider.Error, Iterable<ProviderFilterRepresentation>>>(){

            @Override
            public Either<ActivityProvider.Error, Iterable<ProviderFilterRepresentation>> call() {
                boolean addApplinkName = providerCount.containsKey(provider.getType()) && (Integer)providerCount.get(provider.getType()) > 1;
                return provider.getFilters(addApplinkName);
            }

            @Override
            public ActivityProvider getActivityProvider() {
                return provider;
            }
        };
    }

    private class ByFilterConditions
    implements Predicate<StandardStreamsFilterOption> {
        final boolean containsJira;

        public ByFilterConditions(Iterable<ActivityProvider> providers) {
            this.containsJira = StreamSupport.stream(providers.spliterator(), false).anyMatch(this::providerContainsJira);
        }

        private boolean providerContainsJira(ActivityProvider provider) {
            if (provider instanceof AppLinksActivityProvider) {
                AppLinksActivityProvider appLinksProvider = (AppLinksActivityProvider)provider;
                return ConfigRepresentationBuilder.this.isLinkTypeJira(appLinksProvider);
            }
            if (provider instanceof ActivityProviderWithAnalytics && ((ActivityProviderWithAnalytics)provider).getDelegate() instanceof AppLinksActivityProvider) {
                AppLinksActivityProvider appLinksProvider = (AppLinksActivityProvider)((ActivityProviderWithAnalytics)provider).getDelegate();
                return ConfigRepresentationBuilder.this.isLinkTypeJira(appLinksProvider);
            }
            return ConfigRepresentationBuilder.this.applicationProperties.getDisplayName().equalsIgnoreCase("jira");
        }

        public boolean apply(StandardStreamsFilterOption filterOption) {
            return this.containsJira || !StandardStreamsFilterOption.ISSUE_KEY.equals((Object)filterOption);
        }
    }

    private static enum GetName implements Function<ActivityProvider, String>
    {
        INSTANCE;


        public String apply(ActivityProvider ap) {
            return ap.getName();
        }
    }
}

