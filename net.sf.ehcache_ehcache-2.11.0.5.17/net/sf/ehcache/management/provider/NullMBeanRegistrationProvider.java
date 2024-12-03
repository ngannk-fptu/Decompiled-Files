/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.management.provider;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.management.provider.MBeanRegistrationProvider;
import net.sf.ehcache.management.provider.MBeanRegistrationProviderException;
import net.sf.ehcache.terracotta.ClusteredInstanceFactory;

public class NullMBeanRegistrationProvider
implements MBeanRegistrationProvider {
    @Override
    public void initialize(CacheManager cacheManager, ClusteredInstanceFactory clusteredInstanceFactory) {
    }

    @Override
    public void reinitialize(ClusteredInstanceFactory clusteredInstanceFactory) throws MBeanRegistrationProviderException {
    }

    @Override
    public boolean isInitialized() {
        return false;
    }
}

