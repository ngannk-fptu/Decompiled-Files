/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.ChangeRate
 *  com.atlassian.vcache.ExternalCacheSettings
 *  com.atlassian.vcache.ExternalCacheSettingsBuilder
 *  com.atlassian.vcache.JvmCacheSettings
 *  com.atlassian.vcache.JvmCacheSettingsBuilder
 *  com.atlassian.vcache.VCacheException
 *  com.atlassian.vcache.internal.ExternalCacheDetails
 *  com.atlassian.vcache.internal.JvmCacheDetails
 *  com.atlassian.vcache.internal.VCacheCreationHandler
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.vcache.internal.core;

import com.atlassian.vcache.ChangeRate;
import com.atlassian.vcache.ExternalCacheSettings;
import com.atlassian.vcache.ExternalCacheSettingsBuilder;
import com.atlassian.vcache.JvmCacheSettings;
import com.atlassian.vcache.JvmCacheSettingsBuilder;
import com.atlassian.vcache.VCacheException;
import com.atlassian.vcache.internal.ExternalCacheDetails;
import com.atlassian.vcache.internal.JvmCacheDetails;
import com.atlassian.vcache.internal.VCacheCreationHandler;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultVCacheCreationHandler
implements VCacheCreationHandler {
    private static final Logger log = LoggerFactory.getLogger(DefaultVCacheCreationHandler.class);
    private final int maxEntries;
    private final Duration maxDefaultTtl;
    private final int maxEntryCountHint;
    private final ChangeRate defaultDataChangeRateHint;
    private final ChangeRate defaultEntryGrowthRateHint;

    public DefaultVCacheCreationHandler(int maxEntries, Duration maxDefaultTtl, int maxEntryCountHint, ChangeRate defaultDataChangeRateHint, ChangeRate defaultEntryGrowthRateHint) {
        this.maxEntries = maxEntries;
        this.maxDefaultTtl = Objects.requireNonNull(maxDefaultTtl);
        this.maxEntryCountHint = maxEntryCountHint;
        this.defaultDataChangeRateHint = Objects.requireNonNull(defaultDataChangeRateHint);
        this.defaultEntryGrowthRateHint = Objects.requireNonNull(defaultEntryGrowthRateHint);
    }

    public JvmCacheSettings jvmCacheCreation(JvmCacheDetails details) throws VCacheException {
        JvmCacheSettings candidateSettings = details.getSettings();
        JvmCacheSettingsBuilder bob = new JvmCacheSettingsBuilder(candidateSettings);
        this.enforceInteger(candidateSettings.getMaxEntries(), this.maxEntryCountHint, () -> {
            log.trace("Cache {}: forcing maxEntries to be {}", (Object)details.getName(), (Object)this.maxEntries);
            bob.maxEntries(this.maxEntries);
        });
        this.enforceDuration(candidateSettings.getDefaultTtl(), this.maxDefaultTtl, () -> {
            log.trace("Cache {}: forcing defaultTtl to be {}", (Object)details.getName(), (Object)this.maxDefaultTtl);
            bob.defaultTtl(this.maxDefaultTtl);
        });
        return bob.build();
    }

    public void requestCacheCreation(String name) {
    }

    public ExternalCacheSettings externalCacheCreation(ExternalCacheDetails details) {
        ExternalCacheSettings candidateSettings = details.getSettings();
        ExternalCacheSettingsBuilder bob = new ExternalCacheSettingsBuilder(candidateSettings);
        this.enforceDuration(candidateSettings.getDefaultTtl(), this.maxDefaultTtl, () -> {
            log.trace("Cache {}: forcing defaultTtl to be {}", (Object)details.getName(), (Object)this.maxDefaultTtl);
            bob.defaultTtl(this.maxDefaultTtl);
        });
        this.enforceInteger(candidateSettings.getEntryCountHint(), this.maxEntryCountHint, () -> {
            log.trace("Cache {}: forcing entryCountHint to be {}", (Object)details.getName(), (Object)this.maxEntryCountHint);
            bob.entryCountHint(this.maxEntryCountHint);
        });
        this.enforceRate(candidateSettings.getDataChangeRateHint(), () -> {
            log.trace("Cache {}: forcing dataChangeRateHint to be {}", (Object)details.getName(), (Object)this.defaultDataChangeRateHint);
            bob.dataChangeRateHint(this.defaultDataChangeRateHint);
        });
        this.enforceRate(candidateSettings.getEntryGrowthRateHint(), () -> {
            log.trace("Cache {}: forcing entryGrowthRateHint to be {}", (Object)details.getName(), (Object)this.defaultEntryGrowthRateHint);
            bob.entryGrowthRateHint(this.defaultEntryGrowthRateHint);
        });
        return bob.build();
    }

    private void enforceDuration(Optional<Duration> opt, Duration max, Runnable handler) {
        if (!opt.isPresent() || opt.get().compareTo(max) > 0) {
            handler.run();
        }
    }

    private void enforceInteger(Optional<Integer> opt, int max, Runnable handler) {
        if (!opt.isPresent() || opt.get() > max) {
            handler.run();
        }
    }

    private void enforceRate(Optional<ChangeRate> opt, Runnable handler) {
        if (!opt.isPresent()) {
            handler.run();
        }
    }
}

