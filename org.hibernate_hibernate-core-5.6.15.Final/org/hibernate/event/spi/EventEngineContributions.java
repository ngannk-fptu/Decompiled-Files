/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.util.function.Consumer;
import org.hibernate.event.service.spi.EventListenerGroup;
import org.hibernate.event.spi.EventType;

public interface EventEngineContributions {
    public <T> EventType<T> findEventType(String var1);

    public <T> EventType<T> contributeEventType(String var1, Class<T> var2);

    public <T> EventType<T> contributeEventType(String var1, Class<T> var2, T ... var3);

    public <T> void configureListeners(EventType<T> var1, Consumer<EventListenerGroup<T>> var2);
}

