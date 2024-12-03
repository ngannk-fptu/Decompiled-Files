/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  net.jcip.annotations.NotThreadSafe
 */
package com.atlassian.confluence.plugin.webresource;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.cache.ThreadLocalCacheAccessor;
import com.atlassian.confluence.plugin.webresource.Counter;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.tenant.TenantRegistry;
import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public class DefaultCounter
implements Counter {
    private static final ThreadLocalCacheAccessor<Keys, Integer> CACHE_ACCESSOR = ThreadLocalCacheAccessor.newInstance();
    private final BandanaManager bandanaManager;
    private final String key;

    public DefaultCounter(String key, BandanaManager bandanaManager) {
        this.key = key;
        this.bandanaManager = bandanaManager;
    }

    @Deprecated(forRemoval=true)
    public DefaultCounter(String key, BandanaManager bandanaManager, TenantRegistry ignored) {
        this.key = key;
        this.bandanaManager = bandanaManager;
    }

    @Override
    public int getCounter() {
        Integer value = null;
        if (CACHE_ACCESSOR.isInit()) {
            value = CACHE_ACCESSOR.get(Keys.KEY);
        }
        if (null == value) {
            value = (Integer)this.bandanaManager.getValue((BandanaContext)new ConfluenceBandanaContext(), this.key, false);
            if (null == value) {
                value = 1;
            }
            if (CACHE_ACCESSOR.isInit()) {
                CACHE_ACCESSOR.put(Keys.KEY, value);
            }
        }
        return value;
    }

    @Override
    public void updateCounter() {
        this.bandanaManager.setValue((BandanaContext)new ConfluenceBandanaContext(), this.key, (Object)(this.getCounter() + 1));
    }

    private static enum Keys {
        KEY;

    }
}

