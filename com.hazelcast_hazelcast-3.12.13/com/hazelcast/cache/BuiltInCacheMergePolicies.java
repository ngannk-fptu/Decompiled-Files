/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache;

import com.hazelcast.cache.CacheMergePolicy;
import com.hazelcast.cache.merge.HigherHitsCacheMergePolicy;
import com.hazelcast.cache.merge.LatestAccessCacheMergePolicy;
import com.hazelcast.cache.merge.PassThroughCacheMergePolicy;
import com.hazelcast.cache.merge.PutIfAbsentCacheMergePolicy;

public enum BuiltInCacheMergePolicies {
    PASS_THROUGH(PassThroughCacheMergePolicy.class, new CacheMergePolicyInstanceFactory(){

        @Override
        public CacheMergePolicy create() {
            return new PassThroughCacheMergePolicy();
        }
    }),
    PUT_IF_ABSENT(PutIfAbsentCacheMergePolicy.class, new CacheMergePolicyInstanceFactory(){

        @Override
        public CacheMergePolicy create() {
            return new PutIfAbsentCacheMergePolicy();
        }
    }),
    HIGHER_HITS(HigherHitsCacheMergePolicy.class, new CacheMergePolicyInstanceFactory(){

        @Override
        public CacheMergePolicy create() {
            return new HigherHitsCacheMergePolicy();
        }
    }),
    LATEST_ACCESS(LatestAccessCacheMergePolicy.class, new CacheMergePolicyInstanceFactory(){

        @Override
        public CacheMergePolicy create() {
            return new LatestAccessCacheMergePolicy();
        }
    });

    private Class<? extends CacheMergePolicy> implClass;
    private CacheMergePolicyInstanceFactory instanceFactory;

    private BuiltInCacheMergePolicies(Class<? extends CacheMergePolicy> implClass, CacheMergePolicyInstanceFactory instanceFactory) {
        this.implClass = implClass;
        this.instanceFactory = instanceFactory;
    }

    public Class<? extends CacheMergePolicy> getImplementationClass() {
        return this.implClass;
    }

    public String getImplementationClassName() {
        return this.implClass.getName();
    }

    public CacheMergePolicy newInstance() {
        return this.instanceFactory.create();
    }

    public static BuiltInCacheMergePolicies getDefault() {
        return PUT_IF_ABSENT;
    }

    private static interface CacheMergePolicyInstanceFactory {
        public CacheMergePolicy create();
    }
}

