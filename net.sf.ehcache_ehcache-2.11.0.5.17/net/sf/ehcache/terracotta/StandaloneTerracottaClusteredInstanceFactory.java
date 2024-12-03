/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.terracotta;

import net.sf.ehcache.config.TerracottaClientConfiguration;
import org.terracotta.modules.ehcache.store.TerracottaClusteredInstanceFactory;

public class StandaloneTerracottaClusteredInstanceFactory
extends TerracottaClusteredInstanceFactory {
    public StandaloneTerracottaClusteredInstanceFactory(TerracottaClientConfiguration terracottaConfig, String cacheManagerName, ClassLoader loader) {
        super(terracottaConfig, cacheManagerName, loader);
    }
}

