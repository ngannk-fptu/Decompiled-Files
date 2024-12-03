/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.jira.model;

public class EntityServerCompositeKey {
    private final long entityId;
    private final String serverId;

    public EntityServerCompositeKey(long entityId, String serverId) {
        this.entityId = entityId;
        this.serverId = serverId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        EntityServerCompositeKey that = (EntityServerCompositeKey)o;
        return this.entityId == that.entityId && this.serverId == null ? that.serverId == null : this.serverId.equals(that.serverId);
    }

    public int hashCode() {
        int result = (int)(this.entityId ^ this.entityId >>> 32);
        result = 31 * result + (this.serverId != null ? this.serverId.hashCode() : 0);
        return result;
    }
}

