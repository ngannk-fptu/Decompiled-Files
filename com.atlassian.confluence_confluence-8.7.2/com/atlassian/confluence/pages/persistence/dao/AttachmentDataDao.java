/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.persistence.dao;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentDataNotFoundException;
import com.atlassian.confluence.pages.AttachmentDataStorageType;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStream;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStreamType;
import com.atlassian.confluence.web.rangerequest.RangeRequest;
import java.io.InputStream;
import java.util.Optional;

public interface AttachmentDataDao {
    @Deprecated
    public InputStream getDataForAttachment(Attachment var1) throws AttachmentDataNotFoundException;

    public AttachmentDataStream getDataForAttachment(Attachment var1, AttachmentDataStreamType var2) throws AttachmentDataNotFoundException;

    public AttachmentDataStream getDataForAttachment(Attachment var1, AttachmentDataStreamType var2, Optional<RangeRequest> var3) throws AttachmentDataNotFoundException;

    public void removeDataForAttachment(Attachment var1, ContentEntityObject var2);

    public void removeDataForAttachmentVersion(Attachment var1, ContentEntityObject var2);

    public void removeDataForAttachmentVersion(Attachment var1, ContentEntityObject var2, AttachmentDataStreamType var3);

    public void moveDataForAttachmentVersion(Attachment var1, Attachment var2);

    @Deprecated
    public void saveDataForAttachment(Attachment var1, InputStream var2);

    public void saveDataForAttachment(Attachment var1, AttachmentDataStream var2);

    @Deprecated
    public void saveDataForAttachmentVersion(Attachment var1, Attachment var2, InputStream var3);

    public void saveDataForAttachmentVersion(Attachment var1, Attachment var2, AttachmentDataStream var3);

    @Deprecated
    public void replaceDataForAttachment(Attachment var1, InputStream var2);

    public void replaceDataForAttachment(Attachment var1, AttachmentDataStream var2);

    public boolean isAttachmentPresent(Attachment var1);

    public void moveAttachment(Attachment var1, Attachment var2, ContentEntityObject var3);

    public void prepareForMigrationTo();

    public void afterMigrationFrom();

    public AttachmentDataStorageType getStorageType();
}

