/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditConsumer
 *  com.atlassian.audit.entity.AuditEntity
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.audit.broker;

import com.atlassian.audit.api.AuditConsumer;
import com.atlassian.audit.broker.AuditEntityRejectionHandler;
import com.atlassian.audit.broker.InternalAuditBroker;
import com.atlassian.audit.entity.AuditEntity;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingRejectionHandler
implements AuditEntityRejectionHandler {
    public static final Logger LOGGER = LoggerFactory.getLogger(LoggingRejectionHandler.class);
    private final Logger log;

    public LoggingRejectionHandler(Logger log) {
        this.log = Objects.requireNonNull(log);
    }

    @Override
    public void reject(InternalAuditBroker broker, AuditConsumer consumer, List<AuditEntity> batch) {
        this.log.warn(String.format("%d events have been discarded from %s queue", batch.size(), consumer.getClass().getSimpleName()));
    }
}

