/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.google.common.base.Preconditions
 */
package com.atlassian.crowd.audit.query;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.crowd.audit.query.AbstractAuditLogQueryRestriction;
import com.google.common.base.Preconditions;

@ExperimentalApi
public class AuditLogQueryEntityRestriction
extends AbstractAuditLogQueryRestriction {
    public static final AuditLogQueryEntityRestriction NULL_RESTRICTION = new AuditLogQueryEntityRestriction(null, null, null);

    AuditLogQueryEntityRestriction(Long id, String name, String namePrefix) {
        super(name, id, namePrefix);
    }

    public static AuditLogQueryEntityRestriction id(Long id) {
        return new AuditLogQueryEntityRestriction((Long)Preconditions.checkNotNull((Object)id), null, null);
    }

    public static AuditLogQueryEntityRestriction name(String name) {
        return new AuditLogQueryEntityRestriction(null, (String)Preconditions.checkNotNull((Object)name), null);
    }

    public static AuditLogQueryEntityRestriction namePrefix(String namePrefix) {
        return new AuditLogQueryEntityRestriction(null, null, (String)Preconditions.checkNotNull((Object)namePrefix));
    }
}

