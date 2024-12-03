/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.common.lang.NonNullFields
 *  io.micrometer.common.lang.Nullable
 *  javax.cache.Cache
 *  javax.cache.CacheManager
 */
package io.micrometer.core.instrument.binder.cache;

import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.NonNullFields;
import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.cache.CacheMeterBinder;
import io.micrometer.core.instrument.config.InvalidConfigurationException;
import java.util.ArrayList;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

@NonNullApi
@NonNullFields
public class JCacheMetrics<K, V, C extends Cache<K, V>>
extends CacheMeterBinder<C> {
    @Nullable
    ObjectName objectName;

    public static <K, V, C extends Cache<K, V>> C monitor(MeterRegistry registry, C cache, String ... tags) {
        return JCacheMetrics.monitor(registry, cache, Tags.of(tags));
    }

    public static <K, V, C extends Cache<K, V>> C monitor(MeterRegistry registry, C cache, Iterable<Tag> tags) {
        new JCacheMetrics<K, V, C>(cache, tags).bindTo(registry);
        return cache;
    }

    public JCacheMetrics(C cache, Iterable<Tag> tags) {
        super(cache, cache.getName(), tags);
        try {
            CacheManager cacheManager = cache.getCacheManager();
            if (cacheManager != null) {
                String cacheManagerUri = cacheManager.getURI().toString().replace(':', '.');
                this.objectName = new ObjectName("javax.cache:type=CacheStatistics,CacheManager=" + cacheManagerUri + ",Cache=" + cache.getName());
            }
        }
        catch (MalformedObjectNameException ignored) {
            throw new InvalidConfigurationException("Cache name '" + cache.getName() + "' results in an invalid JMX name");
        }
    }

    @Override
    protected Long size() {
        return null;
    }

    @Override
    protected long hitCount() {
        return this.lookupStatistic("CacheHits");
    }

    @Override
    protected Long missCount() {
        return this.lookupStatistic("CacheMisses");
    }

    @Override
    protected Long evictionCount() {
        return this.lookupStatistic("CacheEvictions");
    }

    @Override
    protected long putCount() {
        return this.lookupStatistic("CachePuts");
    }

    @Override
    protected void bindImplementationSpecificMetrics(MeterRegistry registry) {
        if (this.objectName != null) {
            Gauge.builder("cache.removals", this.objectName, objectName -> this.lookupStatistic("CacheRemovals").longValue()).tags(this.getTagsWithCacheName()).description("Cache removals").register(registry);
        }
    }

    private Long lookupStatistic(String name) {
        if (this.objectName != null) {
            try {
                ArrayList<MBeanServer> mBeanServers = MBeanServerFactory.findMBeanServer(null);
                for (MBeanServer mBeanServer : mBeanServers) {
                    try {
                        return (Long)mBeanServer.getAttribute(this.objectName, name);
                    }
                    catch (AttributeNotFoundException | InstanceNotFoundException operationsException) {
                    }
                }
            }
            catch (MBeanException | ReflectionException ex) {
                throw new IllegalStateException(ex);
            }
        }
        return 0L;
    }
}

