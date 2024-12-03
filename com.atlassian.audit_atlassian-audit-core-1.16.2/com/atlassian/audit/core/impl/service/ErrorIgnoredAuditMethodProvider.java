/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.audit.core.impl.service;

import com.atlassian.audit.core.spi.AuditMethods;
import com.atlassian.audit.core.spi.service.AuditMethodProvider;
import java.util.Optional;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorIgnoredAuditMethodProvider
implements AuditMethodProvider {
    private static final Logger log = LoggerFactory.getLogger(ErrorIgnoredAuditMethodProvider.class);
    private final AuditMethodProvider delegate;

    public ErrorIgnoredAuditMethodProvider(AuditMethodProvider delegate) {
        this.delegate = delegate;
    }

    @Override
    @Nullable
    public String currentMethod() {
        try {
            return Optional.ofNullable(this.delegate.currentMethod()).orElse(AuditMethods.unknown());
        }
        catch (RuntimeException e) {
            log.error("Fail to determine current method.", (Throwable)e);
            return AuditMethods.unknown();
        }
    }
}

