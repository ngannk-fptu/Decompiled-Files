/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.streams.api.ActivityObjectType
 *  com.atlassian.streams.api.ActivityObjectTypes
 *  com.atlassian.streams.api.ActivityVerb
 *  com.atlassian.streams.api.ActivityVerbs
 *  com.atlassian.streams.api.StreamsFilterType
 *  com.atlassian.streams.api.common.Pair
 *  com.atlassian.streams.spi.ActivityOptions
 *  com.atlassian.streams.spi.StreamsFilterOption
 *  com.atlassian.streams.spi.StreamsFilterOption$Builder
 *  com.atlassian.streams.spi.StreamsFilterOptionProvider
 *  com.atlassian.streams.spi.StreamsFilterOptionProvider$ActivityOption
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Iterables
 */
package com.atlassian.streams.confluence;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.streams.api.ActivityObjectType;
import com.atlassian.streams.api.ActivityObjectTypes;
import com.atlassian.streams.api.ActivityVerb;
import com.atlassian.streams.api.ActivityVerbs;
import com.atlassian.streams.api.StreamsFilterType;
import com.atlassian.streams.api.common.Pair;
import com.atlassian.streams.confluence.ConfluenceActivityObjectTypes;
import com.atlassian.streams.spi.ActivityOptions;
import com.atlassian.streams.spi.StreamsFilterOption;
import com.atlassian.streams.spi.StreamsFilterOptionProvider;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import java.util.Map;

public class ConfluenceFilterOptionProvider
implements StreamsFilterOptionProvider {
    public static final String NETWORK_FILTER = "network";
    public static final String NETWORK_FILTER_OPTION_FOLLOWED = "followedByMe";
    public static final Iterable<Pair<ActivityObjectType, ActivityVerb>> activities = ImmutableList.of((Object)Pair.pair((Object)ActivityObjectTypes.article(), (Object)ActivityVerbs.post()), (Object)Pair.pair((Object)ActivityObjectTypes.article(), (Object)ActivityVerbs.update()), (Object)Pair.pair((Object)ConfluenceActivityObjectTypes.page(), (Object)ActivityVerbs.post()), (Object)Pair.pair((Object)ConfluenceActivityObjectTypes.page(), (Object)ActivityVerbs.update()), (Object)Pair.pair((Object)ActivityObjectTypes.comment(), (Object)ActivityVerbs.post()), (Object)Pair.pair((Object)ActivityObjectTypes.file(), (Object)ActivityVerbs.post()), (Object)Pair.pair((Object)ConfluenceActivityObjectTypes.space(), (Object)ActivityVerbs.post()), (Object)Pair.pair((Object)ConfluenceActivityObjectTypes.space(), (Object)ActivityVerbs.update()), (Object)Pair.pair((Object)ConfluenceActivityObjectTypes.personalSpace(), (Object)ActivityVerbs.post()), (Object)Pair.pair((Object)ConfluenceActivityObjectTypes.personalSpace(), (Object)ActivityVerbs.update()));
    private final Function<Pair<ActivityObjectType, ActivityVerb>, StreamsFilterOptionProvider.ActivityOption> toActivityOption;
    private final I18nResolver i18nResolver;

    public ConfluenceFilterOptionProvider(I18nResolver i18nResolver) {
        this.toActivityOption = ActivityOptions.toActivityOption((I18nResolver)((I18nResolver)Preconditions.checkNotNull((Object)i18nResolver, (Object)"i18nResolver")), (String)"streams.filter.confluence");
        this.i18nResolver = i18nResolver;
    }

    public Iterable<StreamsFilterOption> getFilterOptions() {
        ImmutableMap networkFilterOptions = ImmutableMap.of((Object)NETWORK_FILTER_OPTION_FOLLOWED, (Object)this.i18nResolver.getText("streams.filter.confluence.network.followed"));
        StreamsFilterOption filterOption = new StreamsFilterOption.Builder(NETWORK_FILTER, StreamsFilterType.SELECT).displayName(this.i18nResolver.getText("streams.filter.confluence.network")).helpTextI18nKey("streams.filter.confluence.network.help").i18nKey("streams.filter.confluence.network").unique(true).values((Map)networkFilterOptions).build();
        return ImmutableList.of((Object)filterOption);
    }

    public Iterable<StreamsFilterOptionProvider.ActivityOption> getActivities() {
        return Iterables.transform(activities, this.toActivityOption);
    }
}

