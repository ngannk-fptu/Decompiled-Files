/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.instance;

import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleListener;
import com.hazelcast.core.LifecycleService;
import com.hazelcast.instance.HazelcastInstanceFactory;
import com.hazelcast.instance.HazelcastInstanceImpl;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.jmx.ManagementService;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.annotation.PrivateApi;
import com.hazelcast.util.UuidUtil;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@PrivateApi
public class LifecycleServiceImpl
implements LifecycleService {
    private final HazelcastInstanceImpl instance;
    private final ConcurrentMap<String, LifecycleListener> lifecycleListeners = new ConcurrentHashMap<String, LifecycleListener>();
    private final Object lifecycleLock = new Object();

    public LifecycleServiceImpl(HazelcastInstanceImpl instance) {
        this.instance = instance;
    }

    private ILogger getLogger() {
        return this.instance.node.getLogger(LifecycleService.class.getName());
    }

    @Override
    public String addLifecycleListener(LifecycleListener lifecycleListener) {
        String id = UuidUtil.newUnsecureUuidString();
        this.lifecycleListeners.put(id, lifecycleListener);
        return id;
    }

    @Override
    public boolean removeLifecycleListener(String registrationId) {
        return this.lifecycleListeners.remove(registrationId) != null;
    }

    public void fireLifecycleEvent(LifecycleEvent.LifecycleState lifecycleState) {
        this.fireLifecycleEvent(new LifecycleEvent(lifecycleState));
    }

    public void fireLifecycleEvent(LifecycleEvent lifecycleEvent) {
        this.getLogger().info(this.instance.node.getThisAddress() + " is " + (Object)((Object)lifecycleEvent.getState()));
        for (LifecycleListener lifecycleListener : this.lifecycleListeners.values()) {
            lifecycleListener.stateChanged(lifecycleEvent);
        }
    }

    @Override
    public boolean isRunning() {
        return this.instance.node.isRunning();
    }

    @Override
    public void shutdown() {
        this.shutdown(false);
    }

    @Override
    public void terminate() {
        this.shutdown(true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void shutdown(boolean terminate) {
        Object object = this.lifecycleLock;
        synchronized (object) {
            Node node;
            this.fireLifecycleEvent(LifecycleEvent.LifecycleState.SHUTTING_DOWN);
            ManagementService managementService = this.instance.managementService;
            if (managementService != null) {
                managementService.destroy();
            }
            if ((node = this.instance.node) != null) {
                node.shutdown(terminate);
            }
            HazelcastInstanceFactory.remove(this.instance);
            this.fireLifecycleEvent(LifecycleEvent.LifecycleState.SHUTDOWN);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void runUnderLifecycleLock(Runnable runnable) {
        Object object = this.lifecycleLock;
        synchronized (object) {
            runnable.run();
        }
    }
}

