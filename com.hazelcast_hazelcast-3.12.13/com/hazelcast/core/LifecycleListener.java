/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.LifecycleEvent;
import java.util.EventListener;

public interface LifecycleListener
extends EventListener {
    public void stateChanged(LifecycleEvent var1);
}

