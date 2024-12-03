/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.merge;

import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.merge.DiscardMergePolicy;
import com.hazelcast.spi.merge.ExpirationTimeMergePolicy;
import com.hazelcast.spi.merge.HigherHitsMergePolicy;
import com.hazelcast.spi.merge.HyperLogLogMergePolicy;
import com.hazelcast.spi.merge.LatestAccessMergePolicy;
import com.hazelcast.spi.merge.LatestUpdateMergePolicy;
import com.hazelcast.spi.merge.PassThroughMergePolicy;
import com.hazelcast.spi.merge.PutIfAbsentMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class SplitBrainMergePolicyProvider {
    private static final Map<String, SplitBrainMergePolicy> OUT_OF_THE_BOX_MERGE_POLICIES = new HashMap<String, SplitBrainMergePolicy>();
    private final NodeEngine nodeEngine;
    private final ConcurrentMap<String, SplitBrainMergePolicy> mergePolicyMap = new ConcurrentHashMap<String, SplitBrainMergePolicy>();
    private final ConstructorFunction<String, SplitBrainMergePolicy> policyConstructorFunction = new ConstructorFunction<String, SplitBrainMergePolicy>(){

        @Override
        public SplitBrainMergePolicy createNew(String className) {
            try {
                return (SplitBrainMergePolicy)ClassLoaderUtil.newInstance(SplitBrainMergePolicyProvider.this.nodeEngine.getConfigClassLoader(), className);
            }
            catch (Exception e) {
                throw new InvalidConfigurationException("Invalid SplitBrainMergePolicy: " + className, e);
            }
        }
    };

    public SplitBrainMergePolicyProvider(NodeEngine nodeEngine) {
        this.nodeEngine = nodeEngine;
        this.mergePolicyMap.putAll(OUT_OF_THE_BOX_MERGE_POLICIES);
    }

    public SplitBrainMergePolicy getMergePolicy(String className) {
        if (className == null) {
            throw new InvalidConfigurationException("Class name is mandatory!");
        }
        return ConcurrencyUtil.getOrPutIfAbsent(this.mergePolicyMap, className, this.policyConstructorFunction);
    }

    private static <T extends SplitBrainMergePolicy> void addPolicy(Class<T> clazz, T policy) {
        OUT_OF_THE_BOX_MERGE_POLICIES.put(clazz.getName(), policy);
        OUT_OF_THE_BOX_MERGE_POLICIES.put(clazz.getSimpleName(), policy);
    }

    static {
        SplitBrainMergePolicyProvider.addPolicy(DiscardMergePolicy.class, new DiscardMergePolicy());
        SplitBrainMergePolicyProvider.addPolicy(ExpirationTimeMergePolicy.class, new ExpirationTimeMergePolicy());
        SplitBrainMergePolicyProvider.addPolicy(HigherHitsMergePolicy.class, new HigherHitsMergePolicy());
        SplitBrainMergePolicyProvider.addPolicy(HyperLogLogMergePolicy.class, new HyperLogLogMergePolicy());
        SplitBrainMergePolicyProvider.addPolicy(LatestAccessMergePolicy.class, new LatestAccessMergePolicy());
        SplitBrainMergePolicyProvider.addPolicy(LatestUpdateMergePolicy.class, new LatestUpdateMergePolicy());
        SplitBrainMergePolicyProvider.addPolicy(PassThroughMergePolicy.class, new PassThroughMergePolicy());
        SplitBrainMergePolicyProvider.addPolicy(PutIfAbsentMergePolicy.class, new PutIfAbsentMergePolicy());
    }
}

