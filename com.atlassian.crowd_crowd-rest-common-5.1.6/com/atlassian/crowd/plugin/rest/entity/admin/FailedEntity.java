/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.plugin.rest.entity.admin;

import com.google.common.base.MoreObjects;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class FailedEntity<T> {
    @JsonProperty(value="entity")
    private final T entity;
    @JsonProperty(value="reason")
    private final String reason;

    @JsonCreator
    public FailedEntity(@JsonProperty(value="entity") T entity, @JsonProperty(value="reason") String reason) {
        this.entity = entity;
        this.reason = reason;
    }

    public T getEntity() {
        return this.entity;
    }

    public String getReason() {
        return this.reason;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FailedEntity that = (FailedEntity)o;
        return Objects.equals(this.entity, that.entity) && Objects.equals(this.reason, that.reason);
    }

    public int hashCode() {
        return Objects.hash(this.entity, this.reason);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("entity", this.entity).add("reason", (Object)this.reason).toString();
    }
}

