/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.osgi.container.OsgiContainerStartedEvent
 *  com.hazelcast.config.Config
 *  com.hazelcast.config.InMemoryFormat
 *  com.hazelcast.config.MapConfig
 *  com.hazelcast.config.MergePolicyConfig
 *  com.hazelcast.config.NearCacheConfig
 *  com.hazelcast.core.EntryEvent
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.IAtomicLong
 *  com.hazelcast.core.IMap
 *  com.hazelcast.core.MemberAttributeEvent
 *  com.hazelcast.core.MembershipEvent
 *  com.hazelcast.core.MembershipListener
 *  com.hazelcast.map.listener.EntryAddedListener
 *  com.hazelcast.map.listener.MapListener
 *  javax.annotation.Nonnull
 *  javax.annotation.PreDestroy
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleEvent
 *  org.osgi.framework.BundleListener
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.hazelcast.serialization;

import com.atlassian.hazelcast.serialization.BundleKey;
import com.atlassian.hazelcast.serialization.OsgiBundleIdMergePolicy;
import com.atlassian.hazelcast.serialization.OsgiClassLoaderRegistry;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.osgi.container.OsgiContainerStartedEvent;
import com.hazelcast.config.Config;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MemberAttributeEvent;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.MapListener;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.PreDestroy;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OsgiClassLoaderRegistrySynchronizer {
    private static final Logger log = LoggerFactory.getLogger(OsgiClassLoaderRegistrySynchronizer.class);
    public static final String OSGI_BUNDLE_TO_ID_MAP = "atl.extras.osgi.bundle.map";
    public static final String OSGI_BUNDLE_ID_SEQUENCE = "atl.extras.osgi.bundle.id.sequence";
    private final IMap<BundleKey, Integer> clusterBundleToId;
    private final IMap<Integer, BundleKey> clusterIdToBundle;
    private final IAtomicLong clusterIdSequence;
    private final String entryListenerId;
    private final HazelcastInstance hazelcast;
    private final String membershipListenerId;
    private final OsgiClassLoaderRegistry registry;

    public OsgiClassLoaderRegistrySynchronizer(HazelcastInstance hazelcast, OsgiClassLoaderRegistry registry) {
        this.hazelcast = hazelcast;
        this.registry = registry;
        this.clusterIdSequence = hazelcast.getAtomicLong(OSGI_BUNDLE_ID_SEQUENCE);
        int randomOffset = new Random(System.nanoTime()).nextInt(214748) * 10000;
        this.clusterIdSequence.compareAndSet(0L, (long)randomOffset);
        this.clusterBundleToId = hazelcast.getMap(OSGI_BUNDLE_TO_ID_MAP);
        this.clusterIdToBundle = hazelcast.getMap(OSGI_BUNDLE_TO_ID_MAP);
        this.entryListenerId = this.clusterIdToBundle.addEntryListener((MapListener)new RegistryUpdatingEntryAddedListener(), true);
        this.membershipListenerId = hazelcast.getCluster().addMembershipListener((MembershipListener)new RegistryUpdatingMembershipListener());
    }

    @PreDestroy
    public void destroy() {
        this.clusterIdToBundle.removeEntryListener(this.entryListenerId);
        this.hazelcast.getCluster().removeMembershipListener(this.membershipListenerId);
    }

    @PluginEventListener
    public void onContainerStarted(OsgiContainerStartedEvent event) {
        Bundle[] bundles = event.getOsgiContainerManager().getBundles();
        if (bundles == null || bundles.length == 0) {
            return;
        }
        Bundle systemBundle = bundles[0].getBundleContext().getBundle(0L);
        systemBundle.getBundleContext().addBundleListener(new BundleListener(){

            public void bundleChanged(BundleEvent event) {
                Bundle bundle = event.getBundle();
                switch (event.getType()) {
                    case 4: 
                    case 16: {
                        OsgiClassLoaderRegistrySynchronizer.this.unregisterBundle(bundle);
                        log.debug("Unregistered bundle {}:{}", (Object)bundle.getSymbolicName(), (Object)bundle.getVersion());
                        break;
                    }
                    case 2: {
                        OsgiClassLoaderRegistrySynchronizer.this.registerBundle(bundle);
                    }
                }
            }
        });
        for (Bundle b : bundles) {
            this.registerBundle(b);
        }
        this.synchronizeRegistry();
    }

    public static void configure(Config config) {
        config.addMapConfig(new MapConfig(OSGI_BUNDLE_TO_ID_MAP).setMergePolicyConfig(new MergePolicyConfig().setPolicy(OsgiBundleIdMergePolicy.class.getName())).setNearCacheConfig(new NearCacheConfig().setInMemoryFormat(InMemoryFormat.OBJECT).setInvalidateOnChange(true).setCacheLocalEntries(true)));
    }

    private void registerBundle(Bundle bundle) {
        BundleKey key = new BundleKey(bundle);
        Integer id = (Integer)this.clusterBundleToId.get((Object)key);
        while (id == null) {
            BundleKey existing;
            id = (int)this.clusterIdSequence.incrementAndGet();
            Integer current = (Integer)this.clusterBundleToId.putIfAbsent((Object)key, (Object)id);
            if (current != null) {
                id = current;
            }
            if ((existing = (BundleKey)this.clusterIdToBundle.putIfAbsent((Object)id, (Object)key)) == null || existing.equals(key)) continue;
            log.warn("Conflict: bundle ID '{}' could not be assigned to '{}' because it is already assigned to '{}'. Discarding the ID and generating a new ID.", new Object[]{id, key, existing});
            id = null;
            this.clusterBundleToId.remove((Object)key);
        }
        this.registry.register(id, bundle);
    }

    private void registerMapping(Object key, Object value) {
        if (key instanceof BundleKey) {
            this.registry.registerMapping((Integer)value, (BundleKey)key, true);
        } else {
            this.registry.registerMapping((Integer)key, (BundleKey)value, false);
        }
    }

    private void synchronizeRegistry() {
        IMap<Integer, BundleKey> map = this.clusterIdToBundle;
        map.forEach(this::registerMapping);
    }

    private void unregisterBundle(@Nonnull Bundle bundle) {
        this.registry.unregister(bundle);
    }

    private class RegistryUpdatingMembershipListener
    implements MembershipListener {
        private RegistryUpdatingMembershipListener() {
        }

        public void memberAdded(MembershipEvent membershipEvent) {
            OsgiClassLoaderRegistrySynchronizer.this.synchronizeRegistry();
        }

        public void memberRemoved(MembershipEvent membershipEvent) {
        }

        public void memberAttributeChanged(MemberAttributeEvent memberAttributeEvent) {
        }
    }

    private class RegistryUpdatingEntryAddedListener
    implements EntryAddedListener<Object, Object> {
        private RegistryUpdatingEntryAddedListener() {
        }

        public void entryAdded(EntryEvent<Object, Object> event) {
            if (!event.getMember().localMember()) {
                OsgiClassLoaderRegistrySynchronizer.this.registerMapping(event.getKey(), event.getValue());
            }
        }
    }
}

