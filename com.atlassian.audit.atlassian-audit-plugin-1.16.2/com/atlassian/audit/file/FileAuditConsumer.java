/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditConsumer
 *  com.atlassian.audit.entity.AuditEntity
 *  com.atlassian.audit.spi.feature.FileAuditingFeature
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.audit.file;

import com.atlassian.audit.api.AuditConsumer;
import com.atlassian.audit.entity.AuditEntity;
import com.atlassian.audit.file.FileMessagePublisher;
import com.atlassian.audit.rest.v1.utils.AuditEntitySerializer;
import com.atlassian.audit.spi.feature.FileAuditingFeature;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileAuditConsumer
implements AuditConsumer {
    public static final String DEFAULT_AUDIT_FILE_DIR = "log/audit";
    private static final Logger log = LoggerFactory.getLogger(FileAuditConsumer.class);
    private final FileAuditingFeature fileAuditingFeature;
    private final FileMessagePublisher fileHandler;

    public FileAuditConsumer(FileAuditingFeature fileAuditingFeature, FileMessagePublisher fileHandler) {
        this.fileAuditingFeature = fileAuditingFeature;
        this.fileHandler = fileHandler;
    }

    public void accept(List<AuditEntity> entities) {
        Objects.requireNonNull(entities, "entities");
        log.trace("#accept entities.size={}, entities={}", (Object)entities.size(), entities);
        this.fileHandler.publish((String[])entities.stream().filter(Objects::nonNull).map(AuditEntitySerializer::serialize).toArray(String[]::new));
    }

    public boolean isEnabled() {
        return this.fileAuditingFeature.isEnabled();
    }
}

