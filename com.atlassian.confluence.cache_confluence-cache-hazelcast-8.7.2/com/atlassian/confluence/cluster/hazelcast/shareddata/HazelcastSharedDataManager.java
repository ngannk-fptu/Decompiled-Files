/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.cluster.shareddata.SharedData
 *  com.atlassian.confluence.cluster.shareddata.SharedDataManager
 *  com.hazelcast.core.HazelcastInstance
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.cluster.hazelcast.shareddata;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.cluster.hazelcast.shareddata.HazelcastSharedDataSupport;
import com.atlassian.confluence.cluster.shareddata.SharedData;
import com.atlassian.confluence.cluster.shareddata.SharedDataManager;
import com.hazelcast.core.HazelcastInstance;
import org.checkerframework.checker.nullness.qual.NonNull;

@Deprecated(since="8.2", forRemoval=true)
@Internal
public class HazelcastSharedDataManager
implements SharedDataManager {
    private static final String MAP_NAME_PREFIX = HazelcastSharedDataManager.class.getSimpleName();
    private final HazelcastSharedDataSupport support;

    public HazelcastSharedDataManager(HazelcastInstance hazelcastInstance) {
        this.support = new HazelcastSharedDataSupport(MAP_NAME_PREFIX, hazelcastInstance);
    }

    public @NonNull SharedData getSharedData(String name) {
        return this.support.getSharedData(name);
    }
}

