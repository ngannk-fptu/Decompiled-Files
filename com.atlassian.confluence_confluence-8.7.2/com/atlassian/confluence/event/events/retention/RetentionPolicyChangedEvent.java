/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.retention.RetentionPolicy
 *  org.codehaus.jackson.map.ObjectMapper
 */
package com.atlassian.confluence.event.events.retention;

import com.atlassian.confluence.api.model.retention.RetentionPolicy;
import java.io.IOException;
import java.util.Objects;
import org.codehaus.jackson.map.ObjectMapper;

public abstract class RetentionPolicyChangedEvent {
    private final RetentionPolicy oldPolicy;
    private final RetentionPolicy newPolicy;

    public RetentionPolicyChangedEvent(RetentionPolicy oldPolicy, RetentionPolicy newPolicy) {
        this.oldPolicy = oldPolicy;
        this.newPolicy = newPolicy;
    }

    public RetentionPolicy getOldPolicy() {
        return this.oldPolicy;
    }

    public RetentionPolicy getNewPolicy() {
        return this.newPolicy;
    }

    public int hashCode() {
        return Objects.hash(this.oldPolicy, this.newPolicy);
    }

    public boolean equals(Object obj) {
        if (obj instanceof RetentionPolicyChangedEvent) {
            return Objects.equals(this.oldPolicy, ((RetentionPolicyChangedEvent)obj).oldPolicy) && Objects.equals(this.newPolicy, ((RetentionPolicyChangedEvent)obj).newPolicy);
        }
        return false;
    }

    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString((Object)this);
        }
        catch (IOException e) {
            return super.toString();
        }
    }
}

