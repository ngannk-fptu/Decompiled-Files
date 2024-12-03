/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl;

import com.hazelcast.nio.Address;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskHandlerImpl;

public final class ScheduledTaskHandlerAccessor {
    private ScheduledTaskHandlerAccessor() {
    }

    public static void setAddress(ScheduledTaskHandler handler, Address newAddress) {
        ((ScheduledTaskHandlerImpl)handler).setAddress(newAddress);
    }
}

