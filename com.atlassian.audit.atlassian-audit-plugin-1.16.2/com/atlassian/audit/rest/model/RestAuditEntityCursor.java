/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditEntityCursor
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.rest.model;

import com.atlassian.audit.api.AuditEntityCursor;
import com.atlassian.audit.rest.model.RestPageCursor;
import java.util.Objects;
import javax.annotation.Nonnull;

public class RestAuditEntityCursor
implements RestPageCursor {
    private final AuditEntityCursor auditEntityCursor;

    public RestAuditEntityCursor(@Nonnull AuditEntityCursor auditEntityCursor) {
        this.auditEntityCursor = Objects.requireNonNull(auditEntityCursor, "auditEntityCursor");
    }

    @Override
    public String getCursor() {
        return String.format("%s,%s", this.auditEntityCursor.getTimestamp().toEpochMilli(), this.auditEntityCursor.getId());
    }
}

