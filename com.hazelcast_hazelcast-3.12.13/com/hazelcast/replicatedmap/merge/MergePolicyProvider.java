/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.merge;

import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.replicatedmap.merge.HigherHitsMapMergePolicy;
import com.hazelcast.replicatedmap.merge.LatestUpdateMapMergePolicy;
import com.hazelcast.replicatedmap.merge.PassThroughMergePolicy;
import com.hazelcast.replicatedmap.merge.PutIfAbsentMapMergePolicy;
import com.hazelcast.replicatedmap.merge.ReplicatedMapMergePolicy;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.merge.SplitBrainMergePolicyProvider;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class MergePolicyProvider {
    private final ConcurrentMap<String, ReplicatedMapMergePolicy> mergePolicyMap = new ConcurrentHashMap<String, ReplicatedMapMergePolicy>();
    private final ConstructorFunction<String, ReplicatedMapMergePolicy> policyConstructorFunction = new ConstructorFunction<String, ReplicatedMapMergePolicy>(){

        @Override
        public ReplicatedMapMergePolicy createNew(String className) {
            try {
                return (ReplicatedMapMergePolicy)ClassLoaderUtil.newInstance(MergePolicyProvider.this.nodeEngine.getConfigClassLoader(), className);
            }
            catch (Exception e) {
                throw new InvalidConfigurationException("Invalid ReplicatedMapMergePolicy: " + className, e);
            }
        }
    };
    private final NodeEngine nodeEngine;
    private final SplitBrainMergePolicyProvider policyProvider;

    public MergePolicyProvider(NodeEngine nodeEngine) {
        this.nodeEngine = nodeEngine;
        this.policyProvider = nodeEngine.getSplitBrainMergePolicyProvider();
        this.addOutOfBoxPolicies();
    }

    private void addOutOfBoxPolicies() {
        this.mergePolicyMap.put(PutIfAbsentMapMergePolicy.class.getName(), PutIfAbsentMapMergePolicy.INSTANCE);
        this.mergePolicyMap.put(HigherHitsMapMergePolicy.class.getName(), HigherHitsMapMergePolicy.INSTANCE);
        this.mergePolicyMap.put(PassThroughMergePolicy.class.getName(), PassThroughMergePolicy.INSTANCE);
        this.mergePolicyMap.put(LatestUpdateMapMergePolicy.class.getName(), LatestUpdateMapMergePolicy.INSTANCE);
    }

    public Object getMergePolicy(String className) {
        if (className == null) {
            throw new InvalidConfigurationException("Class name is mandatory!");
        }
        try {
            return this.policyProvider.getMergePolicy(className);
        }
        catch (InvalidConfigurationException e) {
            return ConcurrencyUtil.getOrPutIfAbsent(this.mergePolicyMap, className, this.policyConstructorFunction);
        }
    }
}

