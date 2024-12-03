/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.service.spi;

import java.io.Serializable;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.event.service.spi.DuplicationStrategy;
import org.hibernate.event.service.spi.EventListenerGroup;
import org.hibernate.event.spi.EventType;
import org.hibernate.service.Service;

public interface EventListenerRegistry
extends Service,
Serializable {
    @Deprecated
    default public void prepare(MetadataImplementor metadata) {
    }

    public <T> EventListenerGroup<T> getEventListenerGroup(EventType<T> var1);

    public void addDuplicationStrategy(DuplicationStrategy var1);

    public <T> void setListeners(EventType<T> var1, Class<? extends T> ... var2);

    public <T> void setListeners(EventType<T> var1, T ... var2);

    public <T> void appendListeners(EventType<T> var1, Class<? extends T> ... var2);

    public <T> void appendListeners(EventType<T> var1, T ... var2);

    public <T> void prependListeners(EventType<T> var1, Class<? extends T> ... var2);

    public <T> void prependListeners(EventType<T> var1, T ... var2);
}

