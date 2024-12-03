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
import com.atlassian.audit.broker.AuditConsumerExceptionHandler;
import com.atlassian.audit.entity.AuditEntity;
import com.atlassian.audit.plugin.configuration.PropertiesProvider;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingAuditConsumerExceptionHandler
implements AuditConsumerExceptionHandler {
    public static final Logger LOGGER = LoggerFactory.getLogger(LoggingAuditConsumerExceptionHandler.class);
    private static final String LOGGED_COUNT_THRESHOLD_KEY = "plugin.audit.broker.exception.loggedCount";
    private static final int LOGGED_COUNT_DEFAULT = 3;
    private final Logger log;
    private final int threshold;

    public LoggingAuditConsumerExceptionHandler(Logger log, PropertiesProvider propertiesProvider) {
        this.log = Objects.requireNonNull(log);
        this.threshold = propertiesProvider.getInteger(LOGGED_COUNT_THRESHOLD_KEY, 3);
    }

    @Override
    public void handle(AuditConsumer auditConsumer, RuntimeException exception, List<AuditEntity> batch) {
        this.log.error("Error occurred in {} while processing {} events {}", new Object[]{auditConsumer, batch == null ? 0 : batch.size(), this.log.isDebugEnabled() ? this.toString(batch) : "", exception});
    }

    private String toString(List<AuditEntity> batch) {
        if (batch == null) {
            return null;
        }
        return batch.stream().limit(this.threshold).map(this::toString).collect(Collectors.joining("," + System.lineSeparator(), "[" + System.lineSeparator(), "]"));
    }

    private String toString(AuditEntity auditEntity) {
        return auditEntity == null ? "null" : "AuditEntity{version='" + auditEntity.getVersion() + '\'' + ", timestamp=" + auditEntity.getTimestamp() + ", author=" + auditEntity.getAuthor() + ", auditType='" + auditEntity.getAuditType() + '\'' + ", source='" + auditEntity.getSource() + '\'' + ", system='" + auditEntity.getSystem() + '\'' + ", node='" + auditEntity.getNode() + '\'' + ", method=" + auditEntity.getMethod() + '}';
    }
}

