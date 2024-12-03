/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.event.spi.AbstractCollectionEvent;
import org.hibernate.event.spi.EventSource;
import org.hibernate.persister.collection.CollectionPersister;

public class PreCollectionUpdateEvent
extends AbstractCollectionEvent {
    public PreCollectionUpdateEvent(CollectionPersister collectionPersister, PersistentCollection collection, EventSource source) {
        super(collectionPersister, collection, source, PreCollectionUpdateEvent.getLoadedOwnerOrNull(collection, source), PreCollectionUpdateEvent.getLoadedOwnerIdOrNull(collection, source));
    }
}

