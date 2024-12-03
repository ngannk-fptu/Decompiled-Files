/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.streams.api.StreamsFilterType
 *  com.atlassian.streams.spi.StreamsFilterOption
 *  com.atlassian.streams.spi.StreamsFilterOption$Builder
 *  com.atlassian.streams.spi.StreamsFilterOptionProvider
 *  com.atlassian.streams.spi.StreamsFilterOptionProvider$ActivityOption
 *  com.atlassian.streams.spi.StreamsI18nResolver
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Ordering
 */
package com.atlassian.streams.thirdparty;

import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.streams.api.StreamsFilterType;
import com.atlassian.streams.spi.StreamsFilterOption;
import com.atlassian.streams.spi.StreamsFilterOptionProvider;
import com.atlassian.streams.spi.StreamsI18nResolver;
import com.atlassian.streams.thirdparty.ThirdPartyStreamsEntryBuilder;
import com.atlassian.streams.thirdparty.api.ActivityService;
import com.atlassian.streams.thirdparty.api.Application;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import java.util.Map;

public final class ThirdPartyStreamsFilterOptionProvider
implements StreamsFilterOptionProvider {
    public static final String PROVIDER_NAME = "provider_name";
    private final StreamsI18nResolver i18nResolver;
    private final ActivityService service;
    private final TransactionTemplate transactionTemplate;
    private static final Function<Application, String> appIdAndName = new Function<Application, String>(){

        public String apply(Application app) {
            return ThirdPartyStreamsEntryBuilder.getProviderIdAndName(app);
        }
    };
    private static final Function<Application, String> toDisplayName = new Function<Application, String>(){

        public String apply(Application app) {
            return app.getDisplayName();
        }
    };
    private static final Ordering<Application> applicationAlphaSorter = new Ordering<Application>(){

        public int compare(Application app1, Application app2) {
            return app1.getDisplayName().compareTo(app2.getDisplayName());
        }
    };

    public ThirdPartyStreamsFilterOptionProvider(ActivityService service, StreamsI18nResolver i18nResolver, TransactionTemplate transactionTemplate) {
        this.service = (ActivityService)Preconditions.checkNotNull((Object)service, (Object)"service");
        this.i18nResolver = (StreamsI18nResolver)Preconditions.checkNotNull((Object)i18nResolver, (Object)"i18nResolver");
        this.transactionTemplate = (TransactionTemplate)Preconditions.checkNotNull((Object)transactionTemplate, (Object)"transactionTemplate");
    }

    public Iterable<StreamsFilterOption> getFilterOptions() {
        return ImmutableList.of((Object)this.getThirdPartyProviderFilter());
    }

    private StreamsFilterOption getThirdPartyProviderFilter() {
        Map values = (Map)this.transactionTemplate.execute((TransactionCallback)new TransactionCallback<Map<String, String>>(){

            public Map<String, String> doInTransaction() {
                return Maps.transformValues((Map)Maps.uniqueIndex((Iterable)applicationAlphaSorter.sortedCopy(ThirdPartyStreamsFilterOptionProvider.this.service.applications()), (Function)appIdAndName), (Function)toDisplayName);
            }
        });
        return new StreamsFilterOption.Builder(PROVIDER_NAME, StreamsFilterType.SELECT).displayName(this.i18nResolver.getText("streams.filter.thirdparty.provider.name")).helpTextI18nKey("streams.filter.help.thirdparty.provider.name").i18nKey("streams.filter.thirdparty.provider.name").unique(true).providerAlias(true).values(values).build();
    }

    public Iterable<StreamsFilterOptionProvider.ActivityOption> getActivities() {
        return ImmutableList.of();
    }
}

