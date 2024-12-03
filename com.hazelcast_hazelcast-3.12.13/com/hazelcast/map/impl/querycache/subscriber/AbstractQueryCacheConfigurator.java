/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber;

import com.hazelcast.config.EntryListenerConfig;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.PredicateConfig;
import com.hazelcast.config.QueryCacheConfig;
import com.hazelcast.map.impl.EntryEventFilter;
import com.hazelcast.map.impl.querycache.QueryCacheConfigurator;
import com.hazelcast.map.impl.querycache.QueryCacheEventService;
import com.hazelcast.map.listener.MapListener;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.SqlPredicate;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.StringUtil;
import java.util.EventListener;

public abstract class AbstractQueryCacheConfigurator
implements QueryCacheConfigurator {
    private final ClassLoader configClassLoader;
    private final QueryCacheEventService eventService;

    public AbstractQueryCacheConfigurator(ClassLoader configClassLoader, QueryCacheEventService eventService) {
        this.configClassLoader = configClassLoader;
        this.eventService = eventService;
    }

    protected void setEntryListener(String mapName, String cacheId, QueryCacheConfig config) {
        for (EntryListenerConfig listenerConfig : config.getEntryListenerConfigs()) {
            MapListener listener = (MapListener)this.getListener(listenerConfig);
            if (listener == null) continue;
            EntryEventFilter filter = new EntryEventFilter(listenerConfig.isIncludeValue(), null);
            this.eventService.addListener(mapName, cacheId, listener, filter);
        }
    }

    protected void setPredicateImpl(QueryCacheConfig config) {
        PredicateConfig predicateConfig = config.getPredicateConfig();
        if (predicateConfig.getImplementation() != null) {
            return;
        }
        Predicate predicate = this.getPredicate(predicateConfig);
        if (predicate == null) {
            return;
        }
        predicateConfig.setImplementation(predicate);
    }

    private Predicate getPredicate(PredicateConfig predicateConfig) {
        if (!StringUtil.isNullOrEmpty(predicateConfig.getClassName())) {
            try {
                return (Predicate)ClassLoaderUtil.newInstance(this.configClassLoader, predicateConfig.getClassName());
            }
            catch (Exception e) {
                throw ExceptionUtil.rethrow(e);
            }
        }
        if (!StringUtil.isNullOrEmpty(predicateConfig.getSql())) {
            String sql = predicateConfig.getSql();
            return new SqlPredicate(sql);
        }
        return null;
    }

    private <T extends EventListener> T getListener(ListenerConfig listenerConfig) {
        EventListener listener = null;
        if (listenerConfig.getImplementation() != null) {
            listener = listenerConfig.getImplementation();
        } else if (listenerConfig.getClassName() != null) {
            try {
                return (T)((EventListener)ClassLoaderUtil.newInstance(this.configClassLoader, listenerConfig.getClassName()));
            }
            catch (Exception e) {
                throw ExceptionUtil.rethrow(e);
            }
        }
        return (T)listener;
    }
}

