/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.cache.CachedReference
 */
package com.atlassian.confluence.core.persistence.hibernate;

import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.cache.CachedReference;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.core.SynchronizationManager;
import com.atlassian.confluence.core.VersionHistory;
import com.atlassian.confluence.internal.persistence.DelegatingObjectDaoInternal;
import com.atlassian.confluence.internal.persistence.VersionHistoryDaoInternal;
import java.util.List;

public class CachingVersionHistoryDao
extends DelegatingObjectDaoInternal<VersionHistory>
implements VersionHistoryDaoInternal {
    private final VersionHistoryDaoInternal delegate;
    private final CachedReference<List<VersionHistory>> cachedFullUpgradeHistory;
    private final CachedReference<Integer> cachedFinalizedBuildNumber;
    private final SynchronizationManager synchronizationManager;

    public CachingVersionHistoryDao(VersionHistoryDaoInternal delegate, CacheFactory cacheFactory, SynchronizationManager synchronizationManager) {
        super(delegate);
        this.delegate = delegate;
        this.synchronizationManager = synchronizationManager;
        this.cachedFullUpgradeHistory = CoreCache.UPGRADE_HISTORY.resolve(name -> cacheFactory.getCachedReference(name, delegate::getFullUpgradeHistory, new CacheSettingsBuilder().replicateViaInvalidation().replicateAsynchronously().build()));
        this.cachedFinalizedBuildNumber = CoreCache.FINALIZED_BUILD_NUMBER.resolve(name -> cacheFactory.getCachedReference(name, delegate::getFinalizedBuildNumber, new CacheSettingsBuilder().replicateViaInvalidation().replicateAsynchronously().build()));
    }

    @Override
    public int getLatestBuildNumber() {
        return this.delegate.getLatestBuildNumber();
    }

    @Override
    public void addBuildToHistory(int buildNumber) {
        try {
            this.delegate.addBuildToHistory(buildNumber);
        }
        finally {
            this.synchronizationManager.runOnSuccessfulCommit(() -> this.cachedFullUpgradeHistory.reset());
        }
    }

    @Override
    public VersionHistory getVersionHistory(int buildNumber) {
        return this.getFullUpgradeHistory().stream().filter(versionHistory -> versionHistory.getBuildNumber() == buildNumber).findFirst().orElse(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean tagBuild(int buildNumber, String tag) {
        try {
            boolean bl = this.delegate.tagBuild(buildNumber, tag);
            return bl;
        }
        finally {
            this.synchronizationManager.runOnSuccessfulCommit(() -> this.cachedFullUpgradeHistory.reset());
        }
    }

    @Override
    public List<VersionHistory> getFullUpgradeHistory() {
        return (List)this.cachedFullUpgradeHistory.get();
    }

    @Override
    public int getFinalizedBuildNumber() {
        return (Integer)this.cachedFinalizedBuildNumber.get();
    }

    @Override
    public void finalizeBuild(int buildNumber) {
        try {
            this.delegate.finalizeBuild(buildNumber);
        }
        finally {
            this.synchronizationManager.runOnSuccessfulCommit(() -> {
                this.cachedFullUpgradeHistory.reset();
                this.cachedFinalizedBuildNumber.reset();
            });
        }
    }
}

