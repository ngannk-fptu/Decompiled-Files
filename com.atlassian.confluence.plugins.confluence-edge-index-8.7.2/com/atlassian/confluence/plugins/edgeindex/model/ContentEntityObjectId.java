/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.confluence.plugins.edgeindex.model;

import com.atlassian.confluence.plugins.edgeindex.model.EdgeTargetId;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class ContentEntityObjectId
implements EdgeTargetId {
    private final long id;

    public ContentEntityObjectId(long id) {
        if (id <= 0L) {
            throw new IllegalArgumentException("id must be greater than 0. Received: " + id);
        }
        this.id = id;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ContentEntityObjectId)) {
            return false;
        }
        ContentEntityObjectId that = (ContentEntityObjectId)obj;
        return new EqualsBuilder().append(this.id, that.id).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder().append(this.id).toHashCode();
    }

    public String toString() {
        return "ContentEntityObjectId{id=" + this.id + "}";
    }
}

