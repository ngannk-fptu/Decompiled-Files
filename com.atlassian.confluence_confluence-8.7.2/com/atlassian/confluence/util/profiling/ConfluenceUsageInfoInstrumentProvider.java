/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.cache.CachedReference
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.instrumentation.ExternalCounter
 *  com.atlassian.instrumentation.ExternalValue
 *  com.atlassian.instrumentation.Instrument
 *  com.atlassian.tenancy.api.TenantAccessor
 *  com.atlassian.tenancy.api.helper.PerTenantInitialiser
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.cache.CachedReference;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.status.service.systeminfo.UsageInfoDTO;
import com.atlassian.confluence.util.profiling.ConfluenceInstrumentRegistry;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.instrumentation.ExternalCounter;
import com.atlassian.instrumentation.ExternalValue;
import com.atlassian.instrumentation.Instrument;
import com.atlassian.tenancy.api.TenantAccessor;
import com.atlassian.tenancy.api.helper.PerTenantInitialiser;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class ConfluenceUsageInfoInstrumentProvider
implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(ConfluenceUsageInfoInstrumentProvider.class);
    private static final String CACHE_NAME = "confluence.profiling.ConfluenceUsageInfoInstrumentProvider";
    @Deprecated
    public static final String CONFLUENCE_USAGE_INSTRUMENT_CACHE_KEY = "confluence.profiling.ConfluenceUsageInfoInstrumentProvider";
    private static final CacheSettings CACHE_SETTINGS = new CacheSettingsBuilder().expireAfterWrite((long)Integer.getInteger("confluence.jmx.instrumentation.cache.expire.mins", 5).intValue(), TimeUnit.MINUTES).maxEntries(1).flushable().local().build();
    private final CachedReference<UsageInfoDTO> usageInfoCachedRef;
    private final PerTenantInitialiser perTenantInitialiser;

    public ConfluenceUsageInfoInstrumentProvider(EventPublisher eventPublisher, TenantAccessor tenantAccessor, SystemInformationService systemInformationService, CacheManager cacheManager, ConfluenceInstrumentRegistry confluenceInstrumentRegistry) {
        this.usageInfoCachedRef = cacheManager.getCachedReference("confluence.profiling.ConfluenceUsageInfoInstrumentProvider", () -> systemInformationService.getUsageInfo().getUsageInfoDTO(), CACHE_SETTINGS);
        this.perTenantInitialiser = new PerTenantInitialiser(eventPublisher, tenantAccessor, () -> this.registerInstruments(confluenceInstrumentRegistry));
    }

    private synchronized void registerInstruments(ConfluenceInstrumentRegistry registry) {
        logger.info("Register Confluence usage instruments");
        this.registerInstrument(registry, "Confluence.Usage.TotalSpace", () -> this.getUsageInfo().getTotalSpaces());
        this.registerInstrument(registry, "Confluence.Usage.PersonalSpaces", () -> this.getUsageInfo().getPersonalSpaces());
        this.registerInstrument(registry, "Confluence.Usage.GlobalSpaces", () -> this.getUsageInfo().getGlobalSpaces());
        this.registerInstrument(registry, "Confluence.Usage.AllContent", () -> this.getUsageInfo().getAllContent());
        this.registerInstrument(registry, "Confluence.Usage.CurrentContent", () -> this.getUsageInfo().getCurrentContent());
        this.registerInstrument(registry, "Confluence.Usage.LocalGroups", () -> this.getUsageInfo().getLocalGroups());
        this.registerInstrument(registry, "Confluence.Usage.LocalUsers", () -> this.getUsageInfo().getLocalUsers());
        logger.info("Finish register Confluence usage instruments");
    }

    private void registerInstrument(ConfluenceInstrumentRegistry registry, String name, ExternalValue value) {
        registry.putIfAbsent(name, (Instrument)new ExternalCounter(name, value));
    }

    private UsageInfoDTO getUsageInfo() {
        return (UsageInfoDTO)this.usageInfoCachedRef.get();
    }

    public void afterPropertiesSet() throws Exception {
        this.perTenantInitialiser.init();
    }
}

