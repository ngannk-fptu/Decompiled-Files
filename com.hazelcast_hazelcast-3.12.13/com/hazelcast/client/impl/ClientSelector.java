/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl;

import com.hazelcast.core.Client;

public interface ClientSelector {
    public boolean select(Client var1);
}

