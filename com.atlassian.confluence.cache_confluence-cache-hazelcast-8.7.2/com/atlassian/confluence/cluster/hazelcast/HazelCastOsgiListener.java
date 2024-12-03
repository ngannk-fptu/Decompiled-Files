/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.atlassian.hazelcast.serialization.OsgiClassLoaderRegistry
 *  com.atlassian.hazelcast.serialization.OsgiClassLoaderRegistrySynchronizer
 *  com.atlassian.util.concurrent.Supplier
 *  com.google.common.base.Preconditions
 *  com.hazelcast.core.HazelcastInstance
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.cluster.hazelcast;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.hazelcast.serialization.OsgiClassLoaderRegistry;
import com.atlassian.hazelcast.serialization.OsgiClassLoaderRegistrySynchronizer;
import com.atlassian.util.concurrent.Supplier;
import com.google.common.base.Preconditions;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class HazelCastOsgiListener
implements InitializingBean,
DisposableBean {
    private final EventListenerRegistrar registrar;
    private final Supplier<HazelcastInstance> instanceSupplier = () -> (HazelcastInstance)Preconditions.checkNotNull((Object)hazelcastInstance);
    private final OsgiClassLoaderRegistry osgiClassLoaderRegistry;
    private final ClusterManager clusterManager;
    private OsgiClassLoaderRegistrySynchronizer synchronizer;

    public HazelCastOsgiListener(EventListenerRegistrar registrar, HazelcastInstance hazelcastInstance, OsgiClassLoaderRegistry osgiClassLoaderRegistry, ClusterManager clusterManager) {
        this.osgiClassLoaderRegistry = (OsgiClassLoaderRegistry)Preconditions.checkNotNull((Object)osgiClassLoaderRegistry);
        this.registrar = (EventListenerRegistrar)Preconditions.checkNotNull((Object)registrar);
        this.clusterManager = clusterManager;
    }

    public void destroy() throws Exception {
        if (this.isInitialized()) {
            this.registrar.unregister((Object)this.synchronizer);
        }
    }

    public void afterPropertiesSet() throws Exception {
        if (this.clusterManager.isClustered()) {
            this.synchronizer = new OsgiClassLoaderRegistrySynchronizer((HazelcastInstance)this.instanceSupplier.get(), this.osgiClassLoaderRegistry);
            this.registrar.register((Object)this.synchronizer);
        }
    }

    private boolean isInitialized() {
        return this.synchronizer != null;
    }
}

