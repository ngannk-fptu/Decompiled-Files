/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.event.spi.AbstractCollectionEvent;
import org.hibernate.event.spi.EventSource;
import org.hibernate.persister.collection.CollectionPersister;

public class PostCollectionUpdateEvent
extends AbstractCollectionEvent {
    public PostCollectionUpdateEvent(CollectionPersister collectionPersister, PersistentCollection collection, EventSource source) {
        super(collectionPersister, collection, source, PostCollectionUpdateEvent.getLoadedOwnerOrNull(collection, source), PostCollectionUpdateEvent.getLoadedOwnerIdOrNull(collection, source));
    }
}

