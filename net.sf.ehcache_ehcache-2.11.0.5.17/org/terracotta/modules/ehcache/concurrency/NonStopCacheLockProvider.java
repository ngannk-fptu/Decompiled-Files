/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.terracotta.toolkit.feature.NonStopFeature
 *  org.terracotta.toolkit.nonstop.NonStopConfiguration
 *  org.terracotta.toolkit.nonstop.NonStopException
 */
package org.terracotta.modules.ehcache.concurrency;

import net.sf.ehcache.concurrent.CacheLockProvider;
import net.sf.ehcache.concurrent.Sync;
import net.sf.ehcache.config.NonstopConfiguration;
import net.sf.ehcache.constructs.nonstop.NonStopCacheException;
import org.terracotta.modules.ehcache.ToolkitInstanceFactory;
import org.terracotta.modules.ehcache.concurrency.NonStopSyncWrapper;
import org.terracotta.modules.ehcache.store.ToolkitNonStopExceptionOnTimeoutConfiguration;
import org.terracotta.toolkit.feature.NonStopFeature;
import org.terracotta.toolkit.nonstop.NonStopConfiguration;
import org.terracotta.toolkit.nonstop.NonStopException;

public class NonStopCacheLockProvider
implements CacheLockProvider {
    private volatile CacheLockProvider delegate;
    private final NonStopFeature nonStop;
    private final ToolkitNonStopExceptionOnTimeoutConfiguration toolkitNonStopConfiguration;
    private final ToolkitInstanceFactory toolkitInstanceFactory;

    public NonStopCacheLockProvider(NonStopFeature nonStop, NonstopConfiguration nonstopConfiguration, ToolkitInstanceFactory toolkitInstanceFactory) {
        this.nonStop = nonStop;
        this.toolkitInstanceFactory = toolkitInstanceFactory;
        this.toolkitNonStopConfiguration = nonstopConfiguration == null ? null : new ToolkitNonStopExceptionOnTimeoutConfiguration(nonstopConfiguration);
    }

    @Override
    public Sync getSyncForKey(Object key) {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.throwNonStopExceptionWhenClusterNotInit();
            NonStopSyncWrapper nonStopSyncWrapper = new NonStopSyncWrapper(this.delegate.getSyncForKey(key), this.toolkitInstanceFactory, this.toolkitNonStopConfiguration);
            return nonStopSyncWrapper;
        }
        catch (NonStopException e) {
            throw new NonStopCacheException(e);
        }
        finally {
            this.nonStop.finish();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void init(CacheLockProvider cacheLockProviderParam) {
        this.delegate = cacheLockProviderParam;
        NonStopCacheLockProvider nonStopCacheLockProvider = this;
        synchronized (nonStopCacheLockProvider) {
            this.notifyAll();
        }
    }

    private void throwNonStopExceptionWhenClusterNotInit() throws NonStopException {
        if (this.delegate == null && this.toolkitNonStopConfiguration != null && this.toolkitNonStopConfiguration.isEnabled()) {
            if (this.toolkitNonStopConfiguration.isImmediateTimeoutEnabled()) {
                throw new NonStopException("Cluster not up OR still in the process of connecting ");
            }
            long timeout = this.toolkitNonStopConfiguration.getTimeoutMillis();
            this.waitForTimeout(timeout);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void waitForTimeout(long timeout) {
        NonStopCacheLockProvider nonStopCacheLockProvider = this;
        synchronized (nonStopCacheLockProvider) {
            while (this.delegate == null) {
                try {
                    this.wait(timeout);
                }
                catch (InterruptedException e) {
                    throw new NonStopException("Cluster not up OR still in the process of connecting ");
                }
            }
        }
    }
}

