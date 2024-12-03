/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTime
 */
package com.atlassian.upm.core.log;

import com.atlassian.upm.api.log.AuditLogEntry;
import com.atlassian.upm.api.util.Option;
import org.joda.time.DateTime;

public interface PluginInstallerPluginLogAccessor {
    public Iterable<AuditLogEntry> getLogEntries(Option<DateTime> var1);
}

