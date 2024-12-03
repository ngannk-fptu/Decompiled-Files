/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.terracotta.toolkit.Toolkit
 */
package org.terracotta.modules.ehcache;

import net.sf.ehcache.concurrent.CacheLockProvider;
import net.sf.ehcache.concurrent.Sync;
import org.terracotta.modules.ehcache.ToolkitLookup;
import org.terracotta.toolkit.Toolkit;

public class ClusteredCacheInternalContext
implements ToolkitLookup,
CacheLockProvider {
    private final Toolkit toolkit;
    private final CacheLockProvider cacheLockProvider;

    public ClusteredCacheInternalContext(Toolkit toolkit, CacheLockProvider cacheLockProvider) {
        this.toolkit = toolkit;
        this.cacheLockProvider = cacheLockProvider;
    }

    @Override
    public Sync getSyncForKey(Object key) {
        return this.cacheLockProvider.getSyncForKey(key);
    }

    @Override
    public Toolkit getToolkit() {
        return this.toolkit;
    }

    public CacheLockProvider getCacheLockProvider() {
        return this.cacheLockProvider;
    }
}

