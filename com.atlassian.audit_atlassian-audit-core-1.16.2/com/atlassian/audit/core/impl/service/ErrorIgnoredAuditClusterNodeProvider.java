/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.audit.core.impl.service;

import com.atlassian.audit.core.spi.service.ClusterNodeProvider;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorIgnoredAuditClusterNodeProvider
implements ClusterNodeProvider {
    private static final Logger log = LoggerFactory.getLogger(ErrorIgnoredAuditClusterNodeProvider.class);
    private final ClusterNodeProvider delegate;

    public ErrorIgnoredAuditClusterNodeProvider(ClusterNodeProvider delegate) {
        this.delegate = delegate;
    }

    @Override
    @Nonnull
    public Optional<String> currentNodeId() {
        try {
            return this.delegate.currentNodeId();
        }
        catch (RuntimeException e) {
            log.error("Fail to determine current node id.", (Throwable)e);
            return Optional.empty();
        }
    }
}

