/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.pats.events.audit;

import com.atlassian.pats.events.token.TokenEvent;

public interface AuditLogHandler {
    public void logTokenCreated(TokenEvent var1);

    public void logTokenDeleted(TokenEvent var1);
}

