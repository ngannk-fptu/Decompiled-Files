/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.AuditAuthor
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.audit.core.impl.service;

import com.atlassian.audit.core.spi.service.CurrentUserProvider;
import com.atlassian.audit.entity.AuditAuthor;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorIgnoredAuditCurrentUserProvider
implements CurrentUserProvider {
    private static final Logger log = LoggerFactory.getLogger(ErrorIgnoredAuditCurrentUserProvider.class);
    private final CurrentUserProvider delegate;

    public ErrorIgnoredAuditCurrentUserProvider(CurrentUserProvider delegate) {
        this.delegate = delegate;
    }

    @Override
    @Nonnull
    public AuditAuthor currentUser() {
        try {
            return this.delegate.currentUser();
        }
        catch (RuntimeException e) {
            log.error("Fail to determine current user.", (Throwable)e);
            return AuditAuthor.UNKNOWN_AUTHOR;
        }
    }
}

