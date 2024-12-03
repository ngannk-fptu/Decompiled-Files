/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheEntryAdapter
 *  com.atlassian.cache.CacheEntryEvent
 *  com.atlassian.cache.CacheEntryListener
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.cluster.ClusterNodeInformation
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.annotation.Nonnull
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.gatekeeper.evaluator.cache;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheEntryAdapter;
import com.atlassian.cache.CacheEntryEvent;
import com.atlassian.cache.CacheEntryListener;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusterNodeInformation;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.DistributedQueue;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DistributedCacheQueue<V extends Serializable>
implements DistributedQueue<V> {
    private static final Logger log = LoggerFactory.getLogger(DistributedCacheQueue.class);
    private static final String CACHE_NAME = "com.atlassian.confluence.plugins.gatekeeper.evaluator.cache";
    static final int CACHE_SIZE = 100000;
    private final Cache<String, V> cache;
    private final ClusterNodeInformation clusterNode;
    private long counter = 1L;
    private final Collection<Runnable> shutdownTasks = new ArrayList<Runnable>();

    @Autowired
    public DistributedCacheQueue(@ComponentImport CacheManager cacheManager, @ComponentImport ClusterManager clusterManager) {
        this(DistributedCacheQueue.getCache((CacheFactory)cacheManager), clusterManager.getThisNodeInformation());
    }

    DistributedCacheQueue(Cache<String, V> cache, ClusterNodeInformation clusterNode) {
        this.cache = cache;
        this.clusterNode = clusterNode;
    }

    @PreDestroy
    void clear() {
        this.shutdownTasks.forEach(Runnable::run);
        this.cache.removeAll();
    }

    @Override
    public Consumer<V> sender() {
        return value -> this.cache.put((Object)this.generateKey(), value);
    }

    private String generateKey() {
        String keyPrefix = Optional.ofNullable(this.clusterNode).map(ClusterNodeInformation::getAnonymizedNodeIdentifier).orElse("local");
        return keyPrefix + "." + this.counter++;
    }

    @Override
    public void registerReceiver(Consumer<V> receiver) {
        CacheEntryListener<String, V> listener = this.getListener(receiver);
        this.cache.addListener(listener, false);
        this.shutdownTasks.add(() -> this.cache.removeListener(listener));
    }

    private CacheEntryListener<String, V> getListener(final Consumer<V> receiver) {
        return new CacheEntryAdapter<String, V>(){

            public void onAdd(@Nonnull CacheEntryEvent<String, V> event) {
                log.debug("onAdd() entry [{}]->[{}]", event.getKey(), event.getValue());
                receiver.accept((Serializable)DistributedCacheQueue.this.cache.get((Object)((String)event.getKey())));
            }
        };
    }

    private static <V extends Serializable> Cache<String, V> getCache(CacheFactory cacheFactory) {
        CacheSettings cacheSettings = new CacheSettingsBuilder().unflushable().remote().replicateViaCopy().replicateSynchronously().expireAfterWrite(5L, TimeUnit.MINUTES).maxEntries(100000).build();
        return cacheFactory.getCache(CACHE_NAME, null, cacheSettings);
    }
}

