/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.event.spi.AbstractCollectionEvent;
import org.hibernate.event.spi.EventSource;

public class InitializeCollectionEvent
extends AbstractCollectionEvent {
    public InitializeCollectionEvent(PersistentCollection collection, EventSource source) {
        super(InitializeCollectionEvent.getLoadedCollectionPersister(collection, source), collection, source, InitializeCollectionEvent.getLoadedOwnerOrNull(collection, source), InitializeCollectionEvent.getLoadedOwnerIdOrNull(collection, source));
    }
}

