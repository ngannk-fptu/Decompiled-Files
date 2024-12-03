/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.async;

import java.io.Serializable;
import org.terracotta.modules.ehcache.async.ItemProcessor;
import org.terracotta.modules.ehcache.async.ItemsFilter;
import org.terracotta.modules.ehcache.async.scatterpolicies.ItemScatterPolicy;

public interface AsyncCoordinator<E extends Serializable> {
    public void start(ItemProcessor<E> var1, int var2, ItemScatterPolicy<? super E> var3);

    public void add(E var1);

    public void stop();

    public void setOperationsFilter(ItemsFilter<E> var1);

    public long getQueueSize();

    public void destroy();
}

