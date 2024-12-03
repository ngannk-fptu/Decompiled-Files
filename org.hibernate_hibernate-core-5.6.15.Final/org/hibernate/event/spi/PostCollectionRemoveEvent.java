/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.event.spi.AbstractCollectionEvent;
import org.hibernate.event.spi.EventSource;
import org.hibernate.persister.collection.CollectionPersister;

public class PostCollectionRemoveEvent
extends AbstractCollectionEvent {
    public PostCollectionRemoveEvent(CollectionPersister collectionPersister, PersistentCollection collection, EventSource source, Object loadedOwner) {
        super(collectionPersister, collection, source, loadedOwner, PostCollectionRemoveEvent.getOwnerIdOrNull(loadedOwner, source));
    }
}

