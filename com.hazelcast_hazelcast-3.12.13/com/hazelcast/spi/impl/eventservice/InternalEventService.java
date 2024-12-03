/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.eventservice;

import com.hazelcast.nio.Packet;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.EventService;
import com.hazelcast.spi.PostJoinAwareService;
import com.hazelcast.spi.PreJoinAwareService;
import com.hazelcast.util.function.Consumer;

public interface InternalEventService
extends EventService,
Consumer<Packet>,
PreJoinAwareService,
PostJoinAwareService {
    public void close(EventRegistration var1);
}

