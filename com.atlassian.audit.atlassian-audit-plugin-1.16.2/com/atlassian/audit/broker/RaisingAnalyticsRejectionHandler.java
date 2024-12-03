/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditConsumer
 *  com.atlassian.audit.entity.AuditEntity
 *  com.atlassian.event.api.EventPublisher
 */
package com.atlassian.audit.broker;

import com.atlassian.audit.analytics.DiscardEvent;
import com.atlassian.audit.ao.consumer.DatabaseAuditConsumer;
import com.atlassian.audit.api.AuditConsumer;
import com.atlassian.audit.broker.AuditEntityRejectionHandler;
import com.atlassian.audit.broker.InternalAuditBroker;
import com.atlassian.audit.entity.AuditEntity;
import com.atlassian.audit.file.FileAuditConsumer;
import com.atlassian.audit.plugin.AuditPluginInfo;
import com.atlassian.event.api.EventPublisher;
import java.util.List;

public class RaisingAnalyticsRejectionHandler
implements AuditEntityRejectionHandler {
    private static final String DB_CONSUMER = "db";
    private static final String FILE_CONSUMER = "file";
    private static final String ECOSYSTEM_CONSUMER = "ecosystem";
    private final EventPublisher eventPublisher;
    private final AuditPluginInfo auditPluginInfo;

    public RaisingAnalyticsRejectionHandler(EventPublisher eventPublisher, AuditPluginInfo auditPluginInfo) {
        this.eventPublisher = eventPublisher;
        this.auditPluginInfo = auditPluginInfo;
    }

    @Override
    public void reject(InternalAuditBroker broker, AuditConsumer consumer, List<AuditEntity> batch) {
        this.eventPublisher.publish((Object)new DiscardEvent(batch.size(), consumer instanceof DatabaseAuditConsumer ? DB_CONSUMER : (consumer instanceof FileAuditConsumer ? FILE_CONSUMER : ECOSYSTEM_CONSUMER), this.auditPluginInfo.getPluginVersion()));
    }
}

