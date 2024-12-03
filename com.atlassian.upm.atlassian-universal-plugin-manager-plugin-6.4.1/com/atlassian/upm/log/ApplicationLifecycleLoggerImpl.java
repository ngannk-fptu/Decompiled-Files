/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.log;

import com.atlassian.upm.core.log.AuditLogService;
import com.atlassian.upm.log.ApplicationLifecycleLogger;
import java.util.Objects;

public class ApplicationLifecycleLoggerImpl
implements ApplicationLifecycleLogger {
    private final AuditLogService auditLog;

    public ApplicationLifecycleLoggerImpl(AuditLogService auditLog) {
        this.auditLog = Objects.requireNonNull(auditLog, "auditLog");
    }

    public void onStart() {
        this.auditLog.logI18nMessageWithCurrentApplication("upm.auditLog.upm.startup", new String[0]);
    }

    public void onStop() {
    }
}

