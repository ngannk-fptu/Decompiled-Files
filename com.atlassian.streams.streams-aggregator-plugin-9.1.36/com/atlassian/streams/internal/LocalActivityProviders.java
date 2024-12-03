/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.streams.spi.ActivityProviderModuleDescriptor
 *  com.atlassian.streams.spi.SessionManager
 *  com.atlassian.streams.spi.StreamsI18nResolver
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Supplier
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.streams.internal;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.streams.internal.ActivityProvider;
import com.atlassian.streams.internal.ActivityProviderWithAnalytics;
import com.atlassian.streams.internal.CallThrottler;
import com.atlassian.streams.internal.LocalActivityProvider;
import com.atlassian.streams.spi.ActivityProviderModuleDescriptor;
import com.atlassian.streams.spi.SessionManager;
import com.atlassian.streams.spi.StreamsI18nResolver;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.time.Duration;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;

class LocalActivityProviders
implements Supplier<Iterable<ActivityProvider>> {
    private final PluginAccessor pluginAccessor;
    private final StreamsI18nResolver i18nResolver;
    private final TransactionTemplate transactionTemplate;
    private final SessionManager sessionManager;
    private final ApplicationProperties applicationProperties;
    private final EventPublisher eventPublisher;
    private final CallThrottler callThrottler = new CallThrottler(Duration.ofSeconds(30L), LocalActivityProviders.getAllowedWallClockPercentage());

    LocalActivityProviders(PluginAccessor pluginAccessor, StreamsI18nResolver i18nResolver, @Qualifier(value="sessionManager") SessionManager sessionManager, TransactionTemplate transactionTemplate, ApplicationProperties applicationProperties, EventPublisher eventPublisher) {
        this.pluginAccessor = (PluginAccessor)Preconditions.checkNotNull((Object)pluginAccessor, (Object)"pluginAccessor");
        this.i18nResolver = (StreamsI18nResolver)Preconditions.checkNotNull((Object)i18nResolver, (Object)"i18nResolver");
        this.sessionManager = (SessionManager)Preconditions.checkNotNull((Object)sessionManager, (Object)"sessionManager");
        this.transactionTemplate = (TransactionTemplate)Preconditions.checkNotNull((Object)transactionTemplate, (Object)"transactionTemplate");
        this.applicationProperties = (ApplicationProperties)Preconditions.checkNotNull((Object)applicationProperties, (Object)"applicationProperties");
        this.eventPublisher = (EventPublisher)Preconditions.checkNotNull((Object)eventPublisher, (Object)"eventPublisher");
    }

    public Iterable<ActivityProvider> get() {
        return this.pluginAccessor.getEnabledModuleDescriptorsByClass(ActivityProviderModuleDescriptor.class).stream().map(descriptor -> {
            LocalActivityProvider provider = new LocalActivityProvider((ActivityProviderModuleDescriptor)descriptor, this.sessionManager, this.transactionTemplate, this.i18nResolver, this.applicationProperties, this.callThrottler);
            return new ActivityProviderWithAnalytics(provider, this.eventPublisher);
        }).collect(Collectors.toList());
    }

    private static int getAllowedWallClockPercentage() {
        String property = System.getProperty("com.atlassian.streams.internal.LocalActivityProviders.allowed.wallclock.percentage");
        if (StringUtils.isBlank((CharSequence)property)) {
            return 10;
        }
        return Integer.parseInt(property);
    }
}

