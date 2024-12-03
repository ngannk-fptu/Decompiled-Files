/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.hazelcast.core.IMap
 */
package com.atlassian.confluence.cache.hazelcast;

import com.atlassian.annotations.Internal;
import com.hazelcast.core.IMap;

@Internal
public interface HazelcastHelper {
    public IMap getHazelcastMapForCache(String var1);

    public IMap getHazelcastMapForCachedReference(String var1);

    public String getHazelcastMapNameForCache(String var1);

    public String getHazelcastMapNameForCachedReference(String var1);

    public String getBaseSharedDataName();
}

