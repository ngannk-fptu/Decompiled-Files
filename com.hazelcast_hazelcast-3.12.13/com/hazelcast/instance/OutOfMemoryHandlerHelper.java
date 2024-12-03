/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.instance;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.HazelcastInstanceImpl;
import com.hazelcast.spi.annotation.PrivateApi;
import com.hazelcast.util.EmptyStatement;

@PrivateApi
public final class OutOfMemoryHandlerHelper {
    private OutOfMemoryHandlerHelper() {
    }

    public static void tryCloseConnections(HazelcastInstance hazelcastInstance) {
        if (hazelcastInstance == null) {
            return;
        }
        HazelcastInstanceImpl factory = (HazelcastInstanceImpl)hazelcastInstance;
        OutOfMemoryHandlerHelper.closeSockets(factory);
    }

    private static void closeSockets(HazelcastInstanceImpl factory) {
        if (factory.node.networkingService != null) {
            try {
                factory.node.networkingService.shutdown();
            }
            catch (Throwable ignored) {
                EmptyStatement.ignore(ignored);
            }
        }
    }

    public static void tryShutdown(HazelcastInstance hazelcastInstance) {
        if (hazelcastInstance == null) {
            return;
        }
        HazelcastInstanceImpl factory = (HazelcastInstanceImpl)hazelcastInstance;
        OutOfMemoryHandlerHelper.closeSockets(factory);
        try {
            factory.node.shutdown(true);
        }
        catch (Throwable ignored) {
            EmptyStatement.ignore(ignored);
        }
    }
}

