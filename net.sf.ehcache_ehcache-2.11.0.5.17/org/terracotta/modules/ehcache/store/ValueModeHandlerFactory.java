/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.store;

import net.sf.ehcache.config.CacheConfiguration;
import org.terracotta.modules.ehcache.store.ClusteredStore;
import org.terracotta.modules.ehcache.store.ValueModeHandler;
import org.terracotta.modules.ehcache.store.ValueModeHandlerSerialization;

public abstract class ValueModeHandlerFactory {
    public static ValueModeHandler createValueModeHandler(ClusteredStore store, CacheConfiguration cacheConfiguration) {
        return new ValueModeHandlerSerialization();
    }
}

