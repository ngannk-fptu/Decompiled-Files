/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import org.hibernate.event.spi.AbstractEvent;
import org.hibernate.event.spi.EventSource;

public class DeleteEvent
extends AbstractEvent {
    private Object object;
    private String entityName;
    private boolean cascadeDeleteEnabled;
    private boolean orphanRemovalBeforeUpdates;

    public DeleteEvent(Object object, EventSource source) {
        super(source);
        if (object == null) {
            throw new IllegalArgumentException("attempt to create delete event with null entity");
        }
        this.object = object;
    }

    public DeleteEvent(String entityName, Object object, EventSource source) {
        this(object, source);
        this.entityName = entityName;
    }

    public DeleteEvent(String entityName, Object object, boolean cascadeDeleteEnabled, EventSource source) {
        this(object, source);
        this.entityName = entityName;
        this.cascadeDeleteEnabled = cascadeDeleteEnabled;
    }

    public DeleteEvent(String entityName, Object object, boolean cascadeDeleteEnabled, boolean orphanRemovalBeforeUpdates, EventSource source) {
        this(object, source);
        this.entityName = entityName;
        this.cascadeDeleteEnabled = cascadeDeleteEnabled;
        this.orphanRemovalBeforeUpdates = orphanRemovalBeforeUpdates;
    }

    public Object getObject() {
        return this.object;
    }

    public String getEntityName() {
        return this.entityName;
    }

    public boolean isCascadeDeleteEnabled() {
        return this.cascadeDeleteEnabled;
    }

    public boolean isOrphanRemovalBeforeUpdates() {
        return this.orphanRemovalBeforeUpdates;
    }
}

