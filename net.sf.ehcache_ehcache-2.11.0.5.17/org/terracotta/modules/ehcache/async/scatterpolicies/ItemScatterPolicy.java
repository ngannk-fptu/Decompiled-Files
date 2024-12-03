/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.async.scatterpolicies;

import java.io.Serializable;

public interface ItemScatterPolicy<E extends Serializable> {
    public int selectBucket(int var1, E var2);
}

