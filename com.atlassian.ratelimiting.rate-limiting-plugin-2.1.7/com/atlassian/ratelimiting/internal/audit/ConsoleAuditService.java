/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.ratelimiting.internal.audit;

import com.atlassian.ratelimiting.audit.AuditEntry;
import com.atlassian.ratelimiting.audit.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleAuditService
implements AuditService {
    private static final Logger logger = LoggerFactory.getLogger(ConsoleAuditService.class);

    @Override
    public void store(AuditEntry auditEntry) {
        logger.debug("Got event: {}", (Object)auditEntry);
    }
}

