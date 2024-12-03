/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.spi.EventFilter;
import com.hazelcast.spi.EventRegistration;
import java.util.Collection;

public interface EventService {
    public int getEventThreadCount();

    public int getEventQueueCapacity();

    public int getEventQueueSize();

    public EventRegistration registerLocalListener(String var1, String var2, Object var3);

    public EventRegistration registerLocalListener(String var1, String var2, EventFilter var3, Object var4);

    public EventRegistration registerListener(String var1, String var2, Object var3);

    public EventRegistration registerListener(String var1, String var2, EventFilter var3, Object var4);

    public boolean deregisterListener(String var1, String var2, Object var3);

    public void deregisterAllListeners(String var1, String var2);

    public Collection<EventRegistration> getRegistrations(String var1, String var2);

    public EventRegistration[] getRegistrationsAsArray(String var1, String var2);

    public boolean hasEventRegistration(String var1, String var2);

    public void publishEvent(String var1, String var2, Object var3, int var4);

    public void publishEvent(String var1, EventRegistration var2, Object var3, int var4);

    public void publishEvent(String var1, Collection<EventRegistration> var2, Object var3, int var4);

    public void publishRemoteEvent(String var1, Collection<EventRegistration> var2, Object var3, int var4);

    public void executeEventCallback(Runnable var1);
}

