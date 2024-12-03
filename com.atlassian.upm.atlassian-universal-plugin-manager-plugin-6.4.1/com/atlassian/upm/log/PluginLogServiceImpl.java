/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.log;

import com.atlassian.upm.api.log.AuditLogEntry;
import com.atlassian.upm.api.log.EntryType;
import com.atlassian.upm.api.log.PluginLogService;
import com.atlassian.upm.core.log.AuditLogService;
import java.util.Objects;
import java.util.Set;

public class PluginLogServiceImpl
implements PluginLogService {
    private final AuditLogService auditLog;

    public PluginLogServiceImpl(AuditLogService auditLog) {
        this.auditLog = Objects.requireNonNull(auditLog, "auditLog");
    }

    @Override
    public Iterable<AuditLogEntry> getLogEntries() {
        return this.getLogEntries(null, null);
    }

    @Override
    public Iterable<AuditLogEntry> getLogEntries(Integer maxResults, Integer startIndex) {
        return this.auditLog.getLogEntries(maxResults, startIndex);
    }

    @Override
    public Iterable<AuditLogEntry> getLogEntries(Set<EntryType> entryTypes) {
        return this.getLogEntries(null, null, entryTypes);
    }

    @Override
    public Iterable<AuditLogEntry> getLogEntries(Integer maxResults, Integer startIndex, Set<EntryType> entryTypes) {
        return this.auditLog.getLogEntries(maxResults, startIndex, entryTypes);
    }
}

