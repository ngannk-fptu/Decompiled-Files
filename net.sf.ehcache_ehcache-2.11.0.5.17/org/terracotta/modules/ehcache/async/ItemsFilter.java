/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.async;

import java.io.Serializable;
import java.util.List;

public interface ItemsFilter<E extends Serializable> {
    public void filter(List<E> var1);
}

