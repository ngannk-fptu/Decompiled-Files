/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.management;

import java.util.ArrayList;
import java.util.List;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.hibernate.management.impl.EhcacheHibernateMbeanNames;
import net.sf.ehcache.management.Cache;
import net.sf.ehcache.management.CacheManagerMBean;

public class CacheManager
implements CacheManagerMBean {
    private net.sf.ehcache.CacheManager cacheManager;
    private ObjectName objectName;

    public CacheManager(net.sf.ehcache.CacheManager cacheManager) throws CacheException {
        this.cacheManager = cacheManager;
        this.objectName = CacheManager.createObjectName(cacheManager);
    }

    static ObjectName createObjectName(net.sf.ehcache.CacheManager cacheManager) {
        ObjectName objectName;
        try {
            objectName = new ObjectName("net.sf.ehcache:type=CacheManager,name=" + EhcacheHibernateMbeanNames.mbeanSafe(cacheManager.getName()));
        }
        catch (MalformedObjectNameException e) {
            throw new CacheException(e);
        }
        return objectName;
    }

    @Override
    public String getStatus() {
        return this.cacheManager.getStatus().toString();
    }

    @Override
    public String getName() {
        return this.cacheManager.getName();
    }

    @Override
    public void shutdown() {
        this.cacheManager.shutdown();
    }

    @Override
    public void clearAll() {
        this.cacheManager.clearAll();
    }

    @Override
    public Cache getCache(String name) {
        return new Cache(this.cacheManager.getEhcache(name));
    }

    @Override
    public String[] getCacheNames() throws IllegalStateException {
        return this.cacheManager.getCacheNames();
    }

    @Override
    public List getCaches() {
        String[] caches;
        ArrayList<Cache> cacheList = new ArrayList<Cache>();
        for (String cacheName : caches = this.getCacheNames()) {
            Cache cache = this.getCache(cacheName);
            cacheList.add(cache);
        }
        return cacheList;
    }

    @Override
    public long getTransactionCommittedCount() {
        return this.cacheManager.getTransactionController().getTransactionCommittedCount();
    }

    @Override
    public long getTransactionRolledBackCount() {
        return this.cacheManager.getTransactionController().getTransactionRolledBackCount();
    }

    @Override
    public long getTransactionTimedOutCount() {
        return this.cacheManager.getTransactionController().getTransactionTimedOutCount();
    }

    ObjectName getObjectName() {
        return this.objectName;
    }
}

