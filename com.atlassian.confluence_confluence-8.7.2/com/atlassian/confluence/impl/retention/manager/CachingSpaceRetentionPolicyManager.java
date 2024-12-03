/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Supplier
 *  com.atlassian.confluence.api.model.retention.SpaceRetentionPolicy
 */
package com.atlassian.confluence.impl.retention.manager;

import com.atlassian.cache.Supplier;
import com.atlassian.confluence.api.model.retention.SpaceRetentionPolicy;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.impl.cache.tx.TransactionAwareCache;
import com.atlassian.confluence.impl.cache.tx.TransactionAwareCacheFactory;
import com.atlassian.confluence.impl.retention.manager.SpaceRetentionPolicyManager;
import java.util.Optional;

public class CachingSpaceRetentionPolicyManager
implements SpaceRetentionPolicyManager {
    private final SpaceRetentionPolicyManager delegate;
    private final TransactionAwareCache<String, Optional<SpaceRetentionPolicy>> spaceKeyToPolicyCache;

    public CachingSpaceRetentionPolicyManager(SpaceRetentionPolicyManager delegate, TransactionAwareCacheFactory cacheFactory) {
        this.delegate = delegate;
        this.spaceKeyToPolicyCache = CoreCache.SPACE_RETENTION_POLICY_BY_SPACE_KEY.resolve(cacheFactory::getTxCache);
    }

    @Override
    public void deletePolicy(String spaceKey) {
        this.delegate.deletePolicy(spaceKey);
        this.spaceKeyToPolicyCache.remove(spaceKey);
    }

    @Override
    public void savePolicy(String spaceKey, SpaceRetentionPolicy newPolicy) {
        this.delegate.savePolicy(spaceKey, newPolicy);
        this.spaceKeyToPolicyCache.remove(spaceKey);
    }

    @Override
    public Optional<SpaceRetentionPolicy> getPolicy(String spaceKey) {
        return this.spaceKeyToPolicyCache.get(spaceKey, (Supplier<Optional<SpaceRetentionPolicy>>)((Supplier)() -> this.delegate.getPolicy(spaceKey)));
    }

    @Override
    public Optional<SpaceRetentionPolicy> getPolicy(long spaceId) {
        return this.delegate.getPolicy(spaceId);
    }
}

