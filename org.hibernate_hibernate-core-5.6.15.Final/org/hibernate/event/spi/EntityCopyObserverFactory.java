/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import org.hibernate.event.spi.EntityCopyObserver;
import org.hibernate.service.Service;

@FunctionalInterface
public interface EntityCopyObserverFactory
extends Service {
    public EntityCopyObserver createEntityCopyObserver();
}

