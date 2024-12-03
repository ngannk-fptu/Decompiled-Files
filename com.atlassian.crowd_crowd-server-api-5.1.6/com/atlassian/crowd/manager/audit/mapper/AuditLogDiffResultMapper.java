/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.crowd.audit.AuditLogEntry
 *  org.apache.commons.lang3.builder.DiffResult
 */
package com.atlassian.crowd.manager.audit.mapper;

import com.atlassian.annotations.Internal;
import com.atlassian.crowd.audit.AuditLogEntry;
import java.util.List;
import org.apache.commons.lang3.builder.DiffResult;

@Internal
public interface AuditLogDiffResultMapper {
    public static final String SANITIZED_VALUE = "*****";

    public <T> List<AuditLogEntry> mapDiffResult(DiffResult<T> var1);
}

