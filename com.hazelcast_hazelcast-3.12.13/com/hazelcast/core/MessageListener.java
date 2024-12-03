/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.Message;
import java.util.EventListener;

public interface MessageListener<E>
extends EventListener {
    public void onMessage(Message<E> var1);
}

