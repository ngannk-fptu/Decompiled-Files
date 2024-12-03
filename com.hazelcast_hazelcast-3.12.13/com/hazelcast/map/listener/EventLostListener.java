/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.listener;

import com.hazelcast.map.EventLostEvent;
import com.hazelcast.map.listener.MapListener;

public interface EventLostListener
extends MapListener {
    public void eventLost(EventLostEvent var1);
}

