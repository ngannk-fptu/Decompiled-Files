/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.management;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.ManagementRESTServiceConfiguration;

public interface ManagementServer {
    public void start();

    public void stop();

    public void register(CacheManager var1);

    public void unregister(CacheManager var1);

    public boolean hasRegistered();

    public void initialize(ManagementRESTServiceConfiguration var1);

    public void registerClusterRemoteEndpoint(String var1);

    public void unregisterClusterRemoteEndpoint(String var1);
}

