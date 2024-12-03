/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.model.event;

import com.atlassian.crowd.model.event.AbstractAttributeEvent;
import com.atlassian.crowd.model.event.Operation;
import com.atlassian.crowd.model.user.User;
import java.util.Map;
import java.util.Set;

public class UserEvent
extends AbstractAttributeEvent {
    private final User user;

    public UserEvent(Operation operation, Long directoryId, User user, Map<String, Set<String>> storedAttributes, Set<String> deletedAttributes) {
        super(operation, directoryId, storedAttributes, deletedAttributes);
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    public String toString() {
        return "UserEvent{operation=" + (Object)((Object)this.getOperation()) + ",directory=" + this.getDirectoryId() + ",storedAttributes=" + this.getStoredAttributes() + ",deletedAttributes=" + this.getDeletedAttributes() + ",user=" + this.user + '}';
    }
}

