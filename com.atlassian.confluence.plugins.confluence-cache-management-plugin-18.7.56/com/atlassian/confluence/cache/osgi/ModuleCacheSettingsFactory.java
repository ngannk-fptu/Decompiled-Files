/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.dom4j.Element
 */
package com.atlassian.confluence.cache.osgi;

import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dom4j.Element;

final class ModuleCacheSettingsFactory {
    ModuleCacheSettingsFactory() {
    }

    static CacheSettings buildCacheSettings(Element moduleConfig) {
        CacheSettingsBuilder settingsBuilder = new CacheSettingsBuilder();
        boolean local = Boolean.parseBoolean(moduleConfig.attributeValue("local", "false"));
        if (local) {
            settingsBuilder.local();
        } else {
            settingsBuilder.remote().replicateAsynchronously().replicateViaInvalidation();
        }
        ModuleCacheSettingsFactory.setDuration(moduleConfig.attributeValue("expire-after-write"), (arg_0, arg_1) -> ((CacheSettingsBuilder)settingsBuilder).expireAfterWrite(arg_0, arg_1));
        ModuleCacheSettingsFactory.setDuration(moduleConfig.attributeValue("expire-after-access"), (arg_0, arg_1) -> ((CacheSettingsBuilder)settingsBuilder).expireAfterAccess(arg_0, arg_1));
        ModuleCacheSettingsFactory.setInt(moduleConfig.attributeValue("max-entries"), arg_0 -> ((CacheSettingsBuilder)settingsBuilder).maxEntries(arg_0));
        return settingsBuilder.build();
    }

    private static void setDuration(@Nullable String attributeValue, BiConsumer<Long, TimeUnit> configurator) {
        Optional.ofNullable(attributeValue).map(ModuleCacheSettingsFactory::parseDuration).ifPresent(expiry -> configurator.accept(expiry.toMillis(), TimeUnit.MILLISECONDS));
    }

    private static void setInt(@Nullable String attributeValue, Consumer<Integer> configurator) {
        Optional.ofNullable(attributeValue).map(Integer::valueOf).ifPresent(configurator);
    }

    private static Duration parseDuration(String text) {
        return Duration.parse(text);
    }
}

