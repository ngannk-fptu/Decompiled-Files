/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.management.provider;

import java.util.concurrent.atomic.AtomicBoolean;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.management.provider.MBeanRegistrationProvider;
import net.sf.ehcache.management.provider.MBeanRegistrationProviderException;
import net.sf.ehcache.management.sampled.SampledMBeanRegistrationProvider;
import net.sf.ehcache.terracotta.ClusteredInstanceFactory;

public class MBeanRegistrationProviderImpl
implements MBeanRegistrationProvider {
    private final Configuration.Monitoring monitoring;
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private SampledMBeanRegistrationProvider sampledProvider;
    private CacheManager cachedCacheManager;

    public MBeanRegistrationProviderImpl(Configuration configuration) {
        this.monitoring = configuration.getMonitoring();
    }

    private synchronized SampledMBeanRegistrationProvider getSampledMBeanRegistrationProvider() {
        if (this.sampledProvider == null) {
            this.sampledProvider = new SampledMBeanRegistrationProvider();
        }
        return this.sampledProvider;
    }

    @Override
    public void initialize(CacheManager cacheManager, ClusteredInstanceFactory clusteredInstanceFactory) throws MBeanRegistrationProviderException {
        if (!this.initialized.getAndSet(true)) {
            if (this.shouldRegisterMBeans()) {
                this.getSampledMBeanRegistrationProvider().initialize(cacheManager, clusteredInstanceFactory);
            }
        } else {
            throw new IllegalStateException("MBeanRegistrationProvider is already initialized");
        }
        this.cachedCacheManager = cacheManager;
    }

    @Override
    public void reinitialize(ClusteredInstanceFactory clusteredInstanceFactory) throws MBeanRegistrationProviderException {
        if (this.shouldRegisterMBeans()) {
            if (this.getSampledMBeanRegistrationProvider().isAlive()) {
                this.getSampledMBeanRegistrationProvider().reinitialize(clusteredInstanceFactory);
            } else {
                this.getSampledMBeanRegistrationProvider().initialize(this.cachedCacheManager, clusteredInstanceFactory);
            }
        }
    }

    private boolean shouldRegisterMBeans() {
        switch (this.monitoring) {
            case AUTODETECT: {
                return this.isTcActive();
            }
            case ON: {
                return true;
            }
            case OFF: {
                return false;
            }
        }
        throw new IllegalArgumentException("Unknown type of monitoring specified in config: " + this.monitoring);
    }

    private boolean isTcActive() {
        return Boolean.getBoolean("tc.active");
    }

    @Override
    public boolean isInitialized() {
        return this.initialized.get();
    }
}

