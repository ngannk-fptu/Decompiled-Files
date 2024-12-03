/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.Client;
import java.util.EventListener;

public interface ClientListener
extends EventListener {
    public void clientConnected(Client var1);

    public void clientDisconnected(Client var1);
}

