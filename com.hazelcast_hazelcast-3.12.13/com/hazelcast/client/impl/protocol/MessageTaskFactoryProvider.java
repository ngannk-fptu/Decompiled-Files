/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol;

import com.hazelcast.client.impl.protocol.MessageTaskFactory;

public interface MessageTaskFactoryProvider {
    public MessageTaskFactory[] getFactories();
}

