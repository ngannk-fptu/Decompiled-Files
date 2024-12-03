/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.audit.AuditLogAuthorType
 *  com.google.common.base.MoreObjects
 */
package com.atlassian.crowd.audit;

import com.atlassian.crowd.audit.AuditLogAuthor;
import com.atlassian.crowd.audit.AuditLogAuthorType;
import com.google.common.base.MoreObjects;
import java.util.Objects;

public class ImmutableAuditLogAuthor
implements AuditLogAuthor {
    private final Long id;
    private final String name;
    private final AuditLogAuthorType type;

    public ImmutableAuditLogAuthor(Long id, String name, AuditLogAuthorType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public ImmutableAuditLogAuthor(AuditLogAuthor author) {
        this(author.getId(), author.getName(), author.getType());
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public AuditLogAuthorType getType() {
        return this.type;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ImmutableAuditLogAuthor that = (ImmutableAuditLogAuthor)o;
        return Objects.equals(this.id, that.id) && Objects.equals(this.name, that.name) && this.type == that.type;
    }

    public int hashCode() {
        return Objects.hash(this.id, this.name, this.type);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", (Object)this.id).add("name", (Object)this.name).add("type", (Object)this.type).toString();
    }
}

