/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.model.event;

import com.atlassian.crowd.model.event.AbstractOperationEvent;
import com.atlassian.crowd.model.event.Operation;
import java.util.Map;
import java.util.Set;

public abstract class AbstractAttributeEvent
extends AbstractOperationEvent {
    private final Map<String, Set<String>> storedAttributes;
    private final Set<String> deletedAttributes;

    public AbstractAttributeEvent(Operation operation, Long directoryId, Map<String, Set<String>> storedAttributes, Set<String> deletedAttributes) {
        super(operation, directoryId);
        this.storedAttributes = storedAttributes;
        this.deletedAttributes = deletedAttributes;
    }

    public Map<String, Set<String>> getStoredAttributes() {
        return this.storedAttributes;
    }

    public Set<String> getDeletedAttributes() {
        return this.deletedAttributes;
    }
}

