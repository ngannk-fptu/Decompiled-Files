/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.async.scatterpolicies;

import java.io.Serializable;
import org.terracotta.modules.ehcache.async.scatterpolicies.ItemScatterPolicy;

public class HashCodeScatterPolicy<E extends Serializable>
implements ItemScatterPolicy<E> {
    @Override
    public int selectBucket(int count, E item) {
        return Math.abs(item.hashCode() % count);
    }
}

