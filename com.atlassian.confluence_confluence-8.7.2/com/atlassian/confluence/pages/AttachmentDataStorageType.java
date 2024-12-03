/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum AttachmentDataStorageType {
    FILE_SYSTEM("file.system.based.attachments.storage"),
    DATABASE("database.based.attachments.storage");

    private static final Logger log;
    private String configurationKey;

    private AttachmentDataStorageType(String configurationKey) {
        this.configurationKey = configurationKey;
    }

    public Optional<AttachmentDataStorageType> createFromConfigurationString(String configurationKey) {
        if (AttachmentDataStorageType.FILE_SYSTEM.configurationKey.equals(configurationKey)) {
            return Optional.of(FILE_SYSTEM);
        }
        if (AttachmentDataStorageType.DATABASE.configurationKey.equals(configurationKey)) {
            return Optional.of(DATABASE);
        }
        if (log.isDebugEnabled()) {
            log.debug("Given configuration string {} could not be mapped to a storage type.", (Object)configurationKey);
        }
        return Optional.empty();
    }

    static {
        log = LoggerFactory.getLogger(AttachmentDataStorageType.class);
    }
}

