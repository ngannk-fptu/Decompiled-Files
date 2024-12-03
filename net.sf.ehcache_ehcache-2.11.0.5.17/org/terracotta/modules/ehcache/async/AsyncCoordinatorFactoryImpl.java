/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.terracotta.toolkit.collections.ToolkitMap
 */
package org.terracotta.modules.ehcache.async;

import com.terracotta.entity.ehcache.EhcacheEntitiesNaming;
import java.util.HashMap;
import java.util.Map;
import net.sf.ehcache.Ehcache;
import org.terracotta.modules.ehcache.ToolkitInstanceFactory;
import org.terracotta.modules.ehcache.async.AsyncConfig;
import org.terracotta.modules.ehcache.async.AsyncCoordinator;
import org.terracotta.modules.ehcache.async.AsyncCoordinatorFactory;
import org.terracotta.modules.ehcache.async.AsyncCoordinatorImpl;
import org.terracotta.toolkit.collections.ToolkitMap;

public class AsyncCoordinatorFactoryImpl
implements AsyncCoordinatorFactory {
    private final ToolkitInstanceFactory toolkitInstanceFactory;
    private final Map<String, AsyncCoordinatorImpl> localMap;

    public AsyncCoordinatorFactoryImpl(ToolkitInstanceFactory toolkitInstanceFactory) {
        this.toolkitInstanceFactory = toolkitInstanceFactory;
        this.localMap = new HashMap<String, AsyncCoordinatorImpl>();
    }

    private static String getFullAsyncName(String cacheManagerName, String cacheName) {
        return EhcacheEntitiesNaming.getAsyncNameFor(cacheManagerName, cacheName);
    }

    @Override
    public synchronized AsyncCoordinator getOrCreateAsyncCoordinator(Ehcache cache, AsyncConfig config) {
        return this.getOrCreateAsyncCoordinator(cache.getCacheManager().getName(), cache.getName(), config);
    }

    private synchronized AsyncCoordinator getOrCreateAsyncCoordinator(String cacheManagerName, String cacheName, AsyncConfig config) {
        final String fullAsyncName = AsyncCoordinatorFactoryImpl.getFullAsyncName(cacheManagerName, cacheName);
        ToolkitMap<String, AsyncConfig> configMap = this.toolkitInstanceFactory.getOrCreateAsyncConfigMap();
        AsyncConfig oldConfig = (AsyncConfig)configMap.putIfAbsent((Object)fullAsyncName, (Object)config);
        if (oldConfig != null && !oldConfig.equals(config)) {
            throw new IllegalArgumentException("can not get AsyncCoordinator " + fullAsyncName + " for same name but different configs.\nExisting config\n" + oldConfig + "\nNew Config\n" + config);
        }
        AsyncCoordinatorImpl async = this.localMap.get(fullAsyncName);
        if (async != null) {
            if (oldConfig == null) {
                throw new IllegalArgumentException("AsyncCoordinator " + fullAsyncName + " created for this node but entry not present in configMap");
            }
        } else {
            AsyncCoordinatorImpl.Callback stopCallable = new AsyncCoordinatorImpl.Callback(){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public void callback() {
                    AsyncCoordinatorFactoryImpl asyncCoordinatorFactoryImpl = AsyncCoordinatorFactoryImpl.this;
                    synchronized (asyncCoordinatorFactoryImpl) {
                        AsyncCoordinatorFactoryImpl.this.localMap.remove(fullAsyncName);
                    }
                }
            };
            async = new AsyncCoordinatorImpl(fullAsyncName, cacheName, config, this.toolkitInstanceFactory, stopCallable);
            this.localMap.put(fullAsyncName, async);
        }
        return async;
    }

    @Override
    public boolean destroy(String cacheManagerName, String cacheName) {
        AsyncConfig config = (AsyncConfig)this.toolkitInstanceFactory.getOrCreateAsyncConfigMap().get((Object)AsyncCoordinatorFactoryImpl.getFullAsyncName(cacheManagerName, cacheName));
        if (config != null) {
            this.getOrCreateAsyncCoordinator(cacheManagerName, cacheName, config).destroy();
            this.toolkitInstanceFactory.getOrCreateAsyncConfigMap().remove((Object)AsyncCoordinatorFactoryImpl.getFullAsyncName(cacheManagerName, cacheName));
            return true;
        }
        return false;
    }
}

