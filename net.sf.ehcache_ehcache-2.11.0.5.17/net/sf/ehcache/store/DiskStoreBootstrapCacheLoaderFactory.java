/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.store;

import java.util.Properties;
import net.sf.ehcache.bootstrap.BootstrapCacheLoaderFactory;
import net.sf.ehcache.store.DiskStoreBootstrapCacheLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiskStoreBootstrapCacheLoaderFactory
extends BootstrapCacheLoaderFactory<DiskStoreBootstrapCacheLoader> {
    private static final Logger LOG = LoggerFactory.getLogger(DiskStoreBootstrapCacheLoader.class);

    @Override
    public DiskStoreBootstrapCacheLoader createBootstrapCacheLoader(Properties properties) {
        return new DiskStoreBootstrapCacheLoader(this.extractBootstrapAsynchronously(properties));
    }
}

