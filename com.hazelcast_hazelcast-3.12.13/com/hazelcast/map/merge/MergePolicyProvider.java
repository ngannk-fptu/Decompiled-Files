/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.merge;

import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.map.merge.HigherHitsMapMergePolicy;
import com.hazelcast.map.merge.LatestUpdateMapMergePolicy;
import com.hazelcast.map.merge.MapMergePolicy;
import com.hazelcast.map.merge.PassThroughMergePolicy;
import com.hazelcast.map.merge.PutIfAbsentMapMergePolicy;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.merge.SplitBrainMergePolicyProvider;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class MergePolicyProvider {
    private final ConcurrentMap<String, MapMergePolicy> mergePolicyMap = new ConcurrentHashMap<String, MapMergePolicy>();
    private final ConstructorFunction<String, MapMergePolicy> policyConstructorFunction = new ConstructorFunction<String, MapMergePolicy>(){

        @Override
        public MapMergePolicy createNew(String className) {
            try {
                return (MapMergePolicy)ClassLoaderUtil.newInstance(MergePolicyProvider.this.nodeEngine.getConfigClassLoader(), className);
            }
            catch (Exception e) {
                throw new InvalidConfigurationException("Invalid MapMergePolicy: " + className, e);
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
        this.mergePolicyMap.put(PutIfAbsentMapMergePolicy.class.getName(), new PutIfAbsentMapMergePolicy());
        this.mergePolicyMap.put(HigherHitsMapMergePolicy.class.getName(), new HigherHitsMapMergePolicy());
        this.mergePolicyMap.put(PassThroughMergePolicy.class.getName(), new PassThroughMergePolicy());
        this.mergePolicyMap.put(LatestUpdateMapMergePolicy.class.getName(), new LatestUpdateMapMergePolicy());
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

