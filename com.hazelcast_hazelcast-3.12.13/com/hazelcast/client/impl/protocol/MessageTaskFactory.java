/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.task.MessageTask;
import com.hazelcast.nio.Connection;

public interface MessageTaskFactory {
    public MessageTask create(ClientMessage var1, Connection var2);
}

