/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.OutOfMemoryHandler;
import com.hazelcast.instance.HazelcastInstanceFactory;
import com.hazelcast.instance.OutOfMemoryErrorDispatcher;
import java.util.Set;

public final class Hazelcast {
    private Hazelcast() {
    }

    public static void shutdownAll() {
        HazelcastInstanceFactory.shutdownAll();
    }

    public static HazelcastInstance newHazelcastInstance(Config config) {
        return HazelcastInstanceFactory.newHazelcastInstance(config);
    }

    public static HazelcastInstance newHazelcastInstance() {
        return HazelcastInstanceFactory.newHazelcastInstance(null);
    }

    public static HazelcastInstance getHazelcastInstanceByName(String instanceName) {
        return HazelcastInstanceFactory.getHazelcastInstance(instanceName);
    }

    public static HazelcastInstance getOrCreateHazelcastInstance() {
        return HazelcastInstanceFactory.getOrCreateHazelcastInstance(null);
    }

    public static HazelcastInstance getOrCreateHazelcastInstance(Config config) {
        return HazelcastInstanceFactory.getOrCreateHazelcastInstance(config);
    }

    public static Set<HazelcastInstance> getAllHazelcastInstances() {
        return HazelcastInstanceFactory.getAllHazelcastInstances();
    }

    public static void setOutOfMemoryHandler(OutOfMemoryHandler outOfMemoryHandler) {
        OutOfMemoryErrorDispatcher.setServerHandler(outOfMemoryHandler);
    }
}

