/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.management.provider;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.management.provider.MBeanRegistrationProviderException;
import net.sf.ehcache.terracotta.ClusteredInstanceFactory;

public interface MBeanRegistrationProvider {
    public void initialize(CacheManager var1, ClusteredInstanceFactory var2) throws MBeanRegistrationProviderException;

    public void reinitialize(ClusteredInstanceFactory var1) throws MBeanRegistrationProviderException;

    public boolean isInitialized();
}

