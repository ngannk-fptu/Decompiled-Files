/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.model.event;

import com.atlassian.crowd.model.event.Operation;
import com.atlassian.crowd.model.event.OperationEvent;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import java.util.Objects;
import javax.annotation.Nullable;

public class AliasEvent
implements OperationEvent {
    private final Operation operation;
    private final long applicationId;
    private final String username;
    @Nullable
    private final String newAlias;

    private AliasEvent(Operation operation, long applicationId, String username, @Nullable String newAlias) {
        this.operation = (Operation)((Object)Preconditions.checkNotNull((Object)((Object)operation)));
        this.applicationId = applicationId;
        this.username = (String)Preconditions.checkNotNull((Object)username);
        this.newAlias = newAlias;
    }

    @Override
    public Operation getOperation() {
        return this.operation;
    }

    public long getApplicationId() {
        return this.applicationId;
    }

    public String getUsername() {
        return this.username;
    }

    @Nullable
    public String getNewAlias() {
        return this.newAlias;
    }

    @Override
    public Long getDirectoryId() {
        return null;
    }

    public static AliasEvent created(long applicationId, String user, String newAlias) {
        return new AliasEvent(Operation.CREATED, applicationId, user, newAlias);
    }

    public static AliasEvent updated(long applicationId, String user, String newAlias) {
        return new AliasEvent(Operation.UPDATED, applicationId, user, newAlias);
    }

    public static AliasEvent deleted(String user, long applicationId) {
        return new AliasEvent(Operation.DELETED, applicationId, user, null);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AliasEvent that = (AliasEvent)o;
        return this.applicationId == that.applicationId && this.operation == that.operation && Objects.equals(this.username, that.username) && Objects.equals(this.newAlias, that.newAlias);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.operation, this.applicationId, this.username, this.newAlias});
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("operation", (Object)this.operation).add("applicationId", this.applicationId).add("username", (Object)this.username).add("newAlias", (Object)this.newAlias).toString();
    }
}

