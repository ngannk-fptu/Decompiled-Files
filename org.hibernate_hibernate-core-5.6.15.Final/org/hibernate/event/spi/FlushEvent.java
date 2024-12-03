/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import org.hibernate.event.spi.AbstractEvent;
import org.hibernate.event.spi.EventSource;

public class FlushEvent
extends AbstractEvent {
    private int numberOfEntitiesProcessed;
    private int numberOfCollectionsProcessed;

    public FlushEvent(EventSource source) {
        super(source);
    }

    public int getNumberOfEntitiesProcessed() {
        return this.numberOfEntitiesProcessed;
    }

    public void setNumberOfEntitiesProcessed(int numberOfEntitiesProcessed) {
        this.numberOfEntitiesProcessed = numberOfEntitiesProcessed;
    }

    public int getNumberOfCollectionsProcessed() {
        return this.numberOfCollectionsProcessed;
    }

    public void setNumberOfCollectionsProcessed(int numberOfCollectionsProcessed) {
        this.numberOfCollectionsProcessed = numberOfCollectionsProcessed;
    }
}

