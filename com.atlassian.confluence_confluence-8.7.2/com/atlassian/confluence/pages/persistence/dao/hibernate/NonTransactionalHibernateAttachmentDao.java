/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.persistence.dao.hibernate;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.persistence.dao.NonTransactionalAttachmentDataDao;
import com.atlassian.confluence.pages.persistence.dao.hibernate.AbstractHibernateAttachmentDao;
import java.util.List;

public class NonTransactionalHibernateAttachmentDao
extends AbstractHibernateAttachmentDao {
    public void setDataDao(NonTransactionalAttachmentDataDao dao) {
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
        List<Attachment> removedVersions = this.removeAllAttachmentVersions(attachment, containingContent);
        this.dataDao.removeDataForAttachment(attachment, containingContent);
        return removedVersions;
    }

    @Override
    protected void removeAttachmentVersionFromServer(Attachment attachmentVersionToBeRemoved, Attachment previousAttachmentVersion) {
        Attachment attachmentVersionToBeRemovedClone = this.shallowClone(attachmentVersionToBeRemoved);
        if (previousAttachmentVersion == null) {
            this.removeMetaData(attachmentVersionToBeRemoved);
        } else {
            this.overwriteMetaData(previousAttachmentVersion, attachmentVersionToBeRemoved);
        }
        this.dataDao.removeDataForAttachmentVersion(attachmentVersionToBeRemovedClone, attachmentVersionToBeRemovedClone.getContainer());
    }

    private Attachment shallowClone(Attachment attachment) {
        if (attachment == null) {
            return null;
        }
        return (Attachment)attachment.clone();
    }
}

