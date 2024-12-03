/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.spi.EventFilter;

public interface ListenerWrapperEventFilter
extends EventFilter {
    public Object getListener();
}

