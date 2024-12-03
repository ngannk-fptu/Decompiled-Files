/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.management;

import java.io.Serializable;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.hibernate.management.impl.EhcacheHibernateMbeanNames;
import net.sf.ehcache.management.CacheConfiguration;
import net.sf.ehcache.management.CacheMBean;
import net.sf.ehcache.management.CacheStatistics;
import net.sf.ehcache.management.Store;
import net.sf.ehcache.util.CacheTransactionHelper;

public class Cache
implements CacheMBean,
Serializable {
    private static final long serialVersionUID = 3477287016924524437L;
    private transient Ehcache cache;
    private ObjectName objectName;

    public Cache(Ehcache cache) throws CacheException {
        this.cache = cache;
        this.objectName = Cache.createObjectName(cache.getCacheManager().toString(), cache.getName());
    }

    static ObjectName createObjectName(String cacheManagerName, String cacheName) {
        ObjectName objectName;
        try {
            objectName = new ObjectName("net.sf.ehcache:type=Cache,CacheManager=" + cacheManagerName + ",name=" + EhcacheHibernateMbeanNames.mbeanSafe(cacheName));
        }
        catch (MalformedObjectNameException e) {
            throw new CacheException(e);
        }
        return objectName;
    }

    @Override
    public void removeAll() throws IllegalStateException, CacheException {
        CacheTransactionHelper.beginTransactionIfNeeded(this.cache);
        try {
            this.cache.removeAll();
        }
        finally {
            CacheTransactionHelper.commitTransactionIfNeeded(this.cache);
        }
    }

    @Override
    public void flush() throws IllegalStateException, CacheException {
        this.cache.flush();
    }

    @Override
    public String getStatus() {
        return this.cache.getStatus().toString();
    }

    @Override
    public String getName() {
        return this.cache.getName();
    }

    @Override
    public boolean isTerracottaClustered() {
        return this.cache.getCacheConfiguration().isTerracottaClustered();
    }

    @Override
    public boolean hasAbortedSizeOf() {
        return this.cache.hasAbortedSizeOf();
    }

    @Override
    public CacheConfiguration getCacheConfiguration() {
        return new CacheConfiguration(this.cache);
    }

    @Override
    public CacheStatistics getStatistics() {
        return new CacheStatistics(this.cache);
    }

    Store getStore() throws NotCompliantMBeanException {
        return Store.getBean(this.cache);
    }

    ObjectName getObjectName() {
        return this.objectName;
    }
}

