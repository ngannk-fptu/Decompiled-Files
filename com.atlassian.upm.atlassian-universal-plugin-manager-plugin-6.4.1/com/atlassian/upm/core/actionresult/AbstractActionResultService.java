/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 */
package com.atlassian.upm.core.actionresult;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.actionresult.ActionResultCollection;
import com.atlassian.upm.core.actionresult.ActionResultService;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public abstract class AbstractActionResultService
implements ActionResultService {
    private static final CacheSettings CACHE_SETTINGS = new CacheSettingsBuilder().remote().replicateViaCopy().expireAfterWrite(10L, TimeUnit.MINUTES).build();
    private final Cache<String, ActionResultCollection> cache;

    public AbstractActionResultService(CacheFactory cacheFactory) {
        this.cache = cacheFactory.getCache(this.getCacheName(), null, CACHE_SETTINGS);
    }

    @Override
    public Option<ActionResultCollection> getAndClearResults(String resultKey) {
        Option<Object> results = Option.option(this.cache.get((Object)resultKey));
        if (results.isDefined()) {
            this.cache.remove((Object)resultKey);
            return results;
        }
        return Option.none();
    }

    @Override
    public String storeResults(ActionResultCollection results) {
        String uniqueKey = UUID.randomUUID().toString();
        this.cache.put((Object)uniqueKey, (Object)results);
        return uniqueKey;
    }

    protected abstract String getCacheName();
}

