/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.management.provider;

import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.management.provider.MBeanRegistrationProvider;
import net.sf.ehcache.management.provider.MBeanRegistrationProviderFactory;
import net.sf.ehcache.management.provider.MBeanRegistrationProviderImpl;

public class MBeanRegistrationProviderFactoryImpl
implements MBeanRegistrationProviderFactory {
    @Override
    public MBeanRegistrationProvider createMBeanRegistrationProvider(Configuration config) {
        if (null == config) {
            throw new IllegalArgumentException("Configuration cannot be null.");
        }
        return new MBeanRegistrationProviderImpl(config);
    }
}

