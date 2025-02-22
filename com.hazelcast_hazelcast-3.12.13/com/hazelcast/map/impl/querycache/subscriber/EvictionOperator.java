/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber;

import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.QueryCacheConfig;
import com.hazelcast.internal.config.ConfigValidator;
import com.hazelcast.internal.eviction.EvictionChecker;
import com.hazelcast.internal.eviction.EvictionListener;
import com.hazelcast.internal.eviction.EvictionPolicyEvaluatorProvider;
import com.hazelcast.internal.eviction.impl.evaluator.EvictionPolicyEvaluator;
import com.hazelcast.internal.eviction.impl.strategy.sampling.SamplingEvictionStrategy;
import com.hazelcast.map.impl.querycache.subscriber.QueryCacheRecordHashMap;
import com.hazelcast.map.impl.querycache.subscriber.record.QueryCacheRecord;
import com.hazelcast.nio.serialization.Data;

class EvictionOperator {
    private static final int MAX_EVICTION_ATTEMPTS = 10;
    private final QueryCacheRecordHashMap cache;
    private final EvictionConfig evictionConfig;
    private final EvictionChecker evictionChecker;
    private final EvictionPolicyEvaluator<Data, QueryCacheRecord> evictionPolicyEvaluator;
    private final SamplingEvictionStrategy<Data, QueryCacheRecord, QueryCacheRecordHashMap> evictionStrategy;
    private final EvictionListener<Data, QueryCacheRecord> listener;
    private final ClassLoader classLoader;

    EvictionOperator(QueryCacheRecordHashMap cache, QueryCacheConfig config, EvictionListener<Data, QueryCacheRecord> listener, ClassLoader classLoader) {
        this.cache = cache;
        this.evictionConfig = config.getEvictionConfig();
        this.evictionChecker = this.createCacheEvictionChecker();
        this.evictionPolicyEvaluator = this.createEvictionPolicyEvaluator();
        this.evictionStrategy = SamplingEvictionStrategy.INSTANCE;
        this.listener = listener;
        this.classLoader = classLoader;
    }

    boolean isEvictionEnabled() {
        return this.evictionStrategy != null && this.evictionPolicyEvaluator != null;
    }

    void evictIfRequired() {
        if (!this.isEvictionEnabled()) {
            return;
        }
        for (int i = 0; this.evictionChecker.isEvictionRequired() && i < 10; ++i) {
            this.evictionStrategy.evict(this.cache, this.evictionPolicyEvaluator, EvictionChecker.EVICT_ALWAYS, this.listener);
        }
    }

    private EvictionChecker createCacheEvictionChecker() {
        return new EvictionChecker(){

            @Override
            public boolean isEvictionRequired() {
                return EvictionOperator.this.cache.size() >= EvictionOperator.this.evictionConfig.getSize();
            }
        };
    }

    private EvictionPolicyEvaluator<Data, QueryCacheRecord> createEvictionPolicyEvaluator() {
        ConfigValidator.checkEvictionConfig(this.evictionConfig, false);
        return EvictionPolicyEvaluatorProvider.getEvictionPolicyEvaluator(this.evictionConfig, this.classLoader);
    }
}

