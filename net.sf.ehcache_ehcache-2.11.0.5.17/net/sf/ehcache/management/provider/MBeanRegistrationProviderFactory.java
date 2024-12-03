/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.management.provider;

import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.management.provider.MBeanRegistrationProvider;

public interface MBeanRegistrationProviderFactory {
    public MBeanRegistrationProvider createMBeanRegistrationProvider(Configuration var1);
}

