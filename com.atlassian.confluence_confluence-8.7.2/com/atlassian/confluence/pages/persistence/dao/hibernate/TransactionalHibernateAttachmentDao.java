/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.persistence.dao.hibernate;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.persistence.dao.TransactionalAttachmentDataDao;
import com.atlassian.confluence.pages.persistence.dao.hibernate.AbstractHibernateAttachmentDao;
import java.util.List;

public class TransactionalHibernateAttachmentDao
extends AbstractHibernateAttachmentDao {
    public void setDataDao(TransactionalAttachmentDataDao dao) {
        this.dataDao = dao;
    }

    @Override
    public void removeAttachmentFromServer(Attachment attachment) {
        this.removeAllVersionsFromServer(attachment);
    }

    @Override
    public List<Attachment> removeAllVersionsFromServer(Attachment attachment) {
        if (!attachment.isLatestVersion()) {
            attachment = (Attachment)attachment.getLatestVersion();
        }
        ContentEntityObject containingContent = attachment.getContainer();
        this.dataDao.removeDataForAttachment(attachment, containingContent);
        return this.removeAllAttachmentVersions(attachment, containingContent);
    }

    @Override
    protected void removeAttachmentVersionFromServer(Attachment attachmentVersionToBeRemoved, Attachment previousAttachmentVersion) {
        if (previousAttachmentVersion == null) {
            this.dataDao.removeDataForAttachmentVersion(attachmentVersionToBeRemoved, attachmentVersionToBeRemoved.getContainer());
            this.removeMetaData(attachmentVersionToBeRemoved);
        } else {
            this.dataDao.moveDataForAttachmentVersion(previousAttachmentVersion, attachmentVersionToBeRemoved);
            this.overwriteMetaData(previousAttachmentVersion, attachmentVersionToBeRemoved);
        }
    }
}

