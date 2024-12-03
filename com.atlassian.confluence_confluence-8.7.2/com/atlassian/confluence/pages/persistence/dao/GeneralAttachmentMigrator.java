/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.persistence.dao;

import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDao;
import com.atlassian.confluence.pages.persistence.dao.FlushableCachingDao;
import com.atlassian.confluence.pages.persistence.dao.GeneralAttachmentCopier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeneralAttachmentMigrator
extends GeneralAttachmentCopier
implements AttachmentDao.AttachmentMigrator {
    private static final Logger log = LoggerFactory.getLogger(GeneralAttachmentMigrator.class);

    public GeneralAttachmentMigrator(AttachmentManager sourceAttachmentManager, AttachmentManager destinationAttachmentManager) {
        super(sourceAttachmentManager, destinationAttachmentManager);
    }

    @Override
    public void migrate() {
        log.info("Performing pre-migration tasks.");
        this.destinationAttachmentManager.getAttachmentDao().prepareForMigrationTo();
        super.copy();
        log.info("Performing post-migration tasks.");
        this.sourceAttachmentManager.getAttachmentDao().afterMigrationFrom();
        log.info("Flushing the DAOs (if necessary).");
        this.flushDaoIfNecessary(this.sourceAttachmentManager.getAttachmentDao());
        this.flushDaoIfNecessary(this.destinationAttachmentManager.getAttachmentDao());
    }

    private void flushDaoIfNecessary(AttachmentDao attachmentDao) {
        if (!(attachmentDao instanceof FlushableCachingDao)) {
            return;
        }
        ((FlushableCachingDao)((Object)attachmentDao)).flush();
    }
}

