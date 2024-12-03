/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.ItemEvent;
import java.util.EventListener;

public interface ItemListener<E>
extends EventListener {
    public void itemAdded(ItemEvent<E> var1);

    public void itemRemoved(ItemEvent<E> var1);
}

