/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.instance;

import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.core.LifecycleListener;
import com.hazelcast.core.LifecycleService;
import com.hazelcast.spi.annotation.PrivateApi;

@PrivateApi
public final class TerminatedLifecycleService
implements LifecycleService {
    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void terminate() {
    }

    @Override
    public String addLifecycleListener(LifecycleListener lifecycleListener) {
        throw new HazelcastInstanceNotActiveException();
    }

    @Override
    public boolean removeLifecycleListener(String registrationId) {
        throw new HazelcastInstanceNotActiveException();
    }
}

