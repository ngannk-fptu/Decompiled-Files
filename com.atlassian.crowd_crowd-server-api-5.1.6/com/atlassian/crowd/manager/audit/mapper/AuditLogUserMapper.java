/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.crowd.audit.AuditLogEntry
 *  com.atlassian.crowd.audit.AuditLogEventType
 *  com.atlassian.crowd.model.user.User
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.manager.audit.mapper;

import com.atlassian.annotations.Internal;
import com.atlassian.crowd.audit.AuditLogEntry;
import com.atlassian.crowd.audit.AuditLogEventType;
import com.atlassian.crowd.model.user.User;
import java.util.List;
import javax.annotation.Nullable;

@Internal
public interface AuditLogUserMapper {
    public static final String PASSWORD_CREDENTIAL_PROPERTY_KEY = "Password";

    public AuditLogEntry calculatePasswordDiff();

    public List<AuditLogEntry> calculateDifference(AuditLogEventType var1, @Nullable User var2, @Nullable User var3);
}

