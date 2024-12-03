/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.CacheException
 */
package com.hazelcast.cache.impl;

import com.hazelcast.util.EmptyStatement;
import java.lang.management.ManagementFactory;
import java.util.Set;
import javax.cache.CacheException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public final class MXBeanUtil {
    private static MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

    private MXBeanUtil() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void registerCacheObject(Object mxbean, String cacheManagerName, String name, boolean stats) {
        MBeanServer mBeanServer = MXBeanUtil.mBeanServer;
        synchronized (mBeanServer) {
            ObjectName registeredObjectName = MXBeanUtil.calculateObjectName(cacheManagerName, name, stats);
            try {
                if (!MXBeanUtil.isRegistered(cacheManagerName, name, stats)) {
                    MXBeanUtil.mBeanServer.registerMBean(mxbean, registeredObjectName);
                }
            }
            catch (Exception e) {
                throw new CacheException("Error registering cache MXBeans for CacheManager " + registeredObjectName + ". Error was " + e.getMessage(), (Throwable)e);
            }
        }
    }

    public static boolean isRegistered(String cacheManagerName, String name, boolean stats) {
        ObjectName objectName = MXBeanUtil.calculateObjectName(cacheManagerName, name, stats);
        Set<ObjectName> registeredObjectNames = mBeanServer.queryNames(objectName, null);
        return !registeredObjectNames.isEmpty();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void unregisterCacheObject(String cacheManagerName, String name, boolean stats) {
        MBeanServer mBeanServer = MXBeanUtil.mBeanServer;
        synchronized (mBeanServer) {
            ObjectName objectName = MXBeanUtil.calculateObjectName(cacheManagerName, name, stats);
            Set<ObjectName> registeredObjectNames = MXBeanUtil.mBeanServer.queryNames(objectName, null);
            if (MXBeanUtil.isRegistered(cacheManagerName, name, stats)) {
                for (ObjectName registeredObjectName : registeredObjectNames) {
                    try {
                        MXBeanUtil.mBeanServer.unregisterMBean(registeredObjectName);
                    }
                    catch (InstanceNotFoundException e) {
                        EmptyStatement.ignore(e);
                    }
                    catch (Exception e) {
                        throw new CacheException("Error unregistering object instance " + registeredObjectName + ". Error was " + e.getMessage(), (Throwable)e);
                    }
                }
            }
        }
    }

    public static ObjectName calculateObjectName(String cacheManagerName, String name, boolean stats) {
        String cacheManagerNameSafe = MXBeanUtil.mbeanSafe(cacheManagerName);
        String cacheName = MXBeanUtil.mbeanSafe(name);
        try {
            String objectNameType = stats ? "Statistics" : "Configuration";
            return new ObjectName("javax.cache:type=Cache" + objectNameType + ",CacheManager=" + cacheManagerNameSafe + ",Cache=" + cacheName);
        }
        catch (MalformedObjectNameException e) {
            throw new CacheException("Illegal ObjectName for Management Bean. CacheManager=[" + cacheManagerNameSafe + "], Cache=[" + cacheName + "]", (Throwable)e);
        }
    }

    private static String mbeanSafe(String string) {
        return string == null ? "" : string.replaceAll(",|:|=|\n", ".");
    }
}

