/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.async;

import net.sf.ehcache.Ehcache;
import org.terracotta.modules.ehcache.async.AsyncConfig;
import org.terracotta.modules.ehcache.async.AsyncCoordinator;

public interface AsyncCoordinatorFactory {
    public AsyncCoordinator getOrCreateAsyncCoordinator(Ehcache var1, AsyncConfig var2);

    public boolean destroy(String var1, String var2);
}

