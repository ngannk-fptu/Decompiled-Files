/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.nearcache.invalidation;

import com.hazelcast.internal.nearcache.impl.invalidation.Invalidation;
import com.hazelcast.map.listener.MapListener;

public interface InvalidationListener
extends MapListener {
    public void onInvalidate(Invalidation var1);
}

