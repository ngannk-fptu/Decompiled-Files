/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.api.log;

import com.atlassian.upm.api.log.AuditLogEntry;
import com.atlassian.upm.api.log.EntryType;
import java.util.Set;

public interface PluginLogService {
    public Iterable<AuditLogEntry> getLogEntries();

    public Iterable<AuditLogEntry> getLogEntries(Integer var1, Integer var2);

    public Iterable<AuditLogEntry> getLogEntries(Set<EntryType> var1);

    public Iterable<AuditLogEntry> getLogEntries(Integer var1, Integer var2, Set<EntryType> var3);
}

