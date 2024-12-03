/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.terracotta;

import java.util.Properties;
import net.sf.ehcache.bootstrap.BootstrapCacheLoaderFactory;
import net.sf.ehcache.terracotta.TerracottaBootstrapCacheLoader;
import net.sf.ehcache.util.PropertyUtil;

public class TerracottaBootstrapCacheLoaderFactory
extends BootstrapCacheLoaderFactory<TerracottaBootstrapCacheLoader> {
    @Override
    public TerracottaBootstrapCacheLoader createBootstrapCacheLoader(Properties properties) {
        boolean asynchronous = this.extractBootstrapAsynchronously(properties);
        String directory = PropertyUtil.extractAndLogProperty("directory", properties);
        TerracottaBootstrapCacheLoader cacheLoader = this.extractBoolean(properties, "doKeySnapshot", true) ? new TerracottaBootstrapCacheLoader(asynchronous, directory, this.extractLong(properties, "interval", 600L), this.extractBoolean(properties, "useDedicatedThread", false)) : new TerracottaBootstrapCacheLoader(asynchronous, directory, false);
        cacheLoader.setImmediateShutdown(this.extractBoolean(properties, "immediateShutdown", true));
        cacheLoader.setSnapshotOnDispose(this.extractBoolean(properties, "doKeySnapshotOnDispose", false));
        return cacheLoader;
    }
}

