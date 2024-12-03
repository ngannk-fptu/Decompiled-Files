/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.management.sampled;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import net.sf.ehcache.hibernate.management.impl.EhcacheHibernateMbeanNames;

public abstract class SampledEhcacheMBeans {
    public static final String SAMPLED_CACHE_MANAGER_TYPE = "SampledCacheManager";
    public static final String SAMPLED_CACHE_TYPE = "SampledCache";
    public static final String STORE_TYPE = "Store";
    public static final String GROUP_ID = "net.sf.ehcache";

    public static ObjectName getCacheManagerObjectName(String clientUUID, String cacheManagerName) throws MalformedObjectNameException {
        ObjectName objectName = new ObjectName("net.sf.ehcache:type=SampledCacheManager,name=" + EhcacheHibernateMbeanNames.mbeanSafe(cacheManagerName) + SampledEhcacheMBeans.getBeanNameSuffix(clientUUID));
        return objectName;
    }

    private static String getBeanNameSuffix(String clientUUID) {
        Object suffix = "";
        if (clientUUID != null && !clientUUID.trim().equals("")) {
            suffix = ",node=" + clientUUID;
        }
        return suffix;
    }

    public static ObjectName getCacheObjectName(String clientUUID, String cacheManagerName, String cacheName) throws MalformedObjectNameException {
        ObjectName objectName = new ObjectName("net.sf.ehcache:type=SampledCache,SampledCacheManager=" + EhcacheHibernateMbeanNames.mbeanSafe(cacheManagerName) + ",name=" + EhcacheHibernateMbeanNames.mbeanSafe(cacheName) + SampledEhcacheMBeans.getBeanNameSuffix(clientUUID));
        return objectName;
    }

    static ObjectName getStoreObjectName(String clientUUID, String cacheManagerName, String cacheName) throws MalformedObjectNameException {
        ObjectName objectName = new ObjectName("net.sf.ehcache:type=Store,SampledCacheManager=" + EhcacheHibernateMbeanNames.mbeanSafe(cacheManagerName) + ",name=" + EhcacheHibernateMbeanNames.mbeanSafe(cacheName) + SampledEhcacheMBeans.getBeanNameSuffix(clientUUID));
        return objectName;
    }

    public static ObjectName getQueryCacheManagerObjectName(String clientUUID, String cacheManagerName) throws MalformedObjectNameException {
        ObjectName objectName = new ObjectName("net.sf.ehcache:*,SampledCacheManager=" + EhcacheHibernateMbeanNames.mbeanSafe(cacheManagerName) + SampledEhcacheMBeans.getBeanNameSuffix(clientUUID));
        return objectName;
    }

    public static ObjectName getQueryCacheManagersObjectName(String clientUUID) throws MalformedObjectNameException {
        ObjectName objectName = new ObjectName("net.sf.ehcache:type=SampledCacheManager,*" + SampledEhcacheMBeans.getBeanNameSuffix(clientUUID));
        return objectName;
    }
}

