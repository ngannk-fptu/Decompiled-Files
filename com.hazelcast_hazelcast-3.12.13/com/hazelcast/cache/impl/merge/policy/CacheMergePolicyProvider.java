/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.merge.policy;

import com.hazelcast.cache.BuiltInCacheMergePolicies;
import com.hazelcast.cache.CacheMergePolicy;
import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.merge.SplitBrainMergePolicyProvider;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class CacheMergePolicyProvider {
    private final ConcurrentMap<String, CacheMergePolicy> mergePolicyMap = new ConcurrentHashMap<String, CacheMergePolicy>();
    private final ConstructorFunction<String, CacheMergePolicy> policyConstructorFunction = new ConstructorFunction<String, CacheMergePolicy>(){

        @Override
        public CacheMergePolicy createNew(String className) {
            try {
                return (CacheMergePolicy)ClassLoaderUtil.newInstance(CacheMergePolicyProvider.this.nodeEngine.getConfigClassLoader(), className);
            }
            catch (Exception e) {
                CacheMergePolicyProvider.this.nodeEngine.getLogger(this.getClass()).severe(e);
                throw new InvalidConfigurationException("Invalid cache merge policy: " + className, e);
            }
        }
    };
    private final NodeEngine nodeEngine;
    private final SplitBrainMergePolicyProvider policyProvider;

    public CacheMergePolicyProvider(NodeEngine nodeEngine) {
        this.nodeEngine = nodeEngine;
        this.policyProvider = nodeEngine.getSplitBrainMergePolicyProvider();
        this.addOutOfBoxPolicies();
    }

    private void addOutOfBoxPolicies() {
        for (BuiltInCacheMergePolicies mergePolicy : BuiltInCacheMergePolicies.values()) {
            CacheMergePolicy cacheMergePolicy = mergePolicy.newInstance();
            this.mergePolicyMap.put(mergePolicy.name(), cacheMergePolicy);
            this.mergePolicyMap.put(mergePolicy.getImplementationClassName(), cacheMergePolicy);
        }
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

