/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.typesafe.config.Config
 *  com.typesafe.config.ConfigFactory
 *  io.atlassian.fugue.Functions
 */
package com.atlassian.confluence.impl.cache.whitelist;

import com.atlassian.confluence.impl.cache.whitelist.CacheOperationsWhitelist;
import com.atlassian.confluence.impl.cache.whitelist.CacheOperationsWhitelistImpl;
import com.google.common.collect.ImmutableSet;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.atlassian.fugue.Functions;
import java.util.Collection;
import java.util.Set;

final class CacheOperationsWhitelistReader {
    CacheOperationsWhitelistReader() {
    }

    public CacheOperationsWhitelist loadWhiteList(String whitelistPath) {
        Config config = ConfigFactory.parseResources((String)whitelistPath);
        return this.createWhitelist(config);
    }

    private CacheOperationsWhitelist createWhitelist(Config config) {
        return new CacheOperationsWhitelistImpl(Functions.weakMemoize(operation -> CacheOperationsWhitelistReader.getPermittedCacheNames(config, operation)));
    }

    private static Set<String> getPermittedCacheNames(Config config, CacheOperationsWhitelist.Operation operation) {
        return ImmutableSet.copyOf((Collection)config.getConfig("operations").getStringList(operation.name()));
    }
}

