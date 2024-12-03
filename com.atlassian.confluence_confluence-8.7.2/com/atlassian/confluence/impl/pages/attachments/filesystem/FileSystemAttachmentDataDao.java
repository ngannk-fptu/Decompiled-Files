/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.springframework.core.io.InputStreamResource
 *  org.springframework.core.io.InputStreamSource
 *  org.springframework.util.unit.DataSize
 */
package com.atlassian.confluence.impl.pages.attachments.filesystem;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.pages.attachments.filesystem.AttachmentDataFileSystem;
import com.atlassian.confluence.impl.pages.attachments.filesystem.model.Refs;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentDataNotFoundException;
import com.atlassian.confluence.pages.AttachmentDataStorageType;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStream;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStreamType;
import com.atlassian.confluence.pages.persistence.dao.NonTransactionalAttachmentDataDao;
import com.atlassian.confluence.pages.persistence.dao.filesystem.AttachmentDataFileSystemException;
import com.atlassian.confluence.web.rangerequest.RangeRequest;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.util.unit.DataSize;

public class FileSystemAttachmentDataDao
implements NonTransactionalAttachmentDataDao {
    private final AttachmentDataFileSystem fileSystem;

    public FileSystemAttachmentDataDao(AttachmentDataFileSystem fileSystem) {
        this.fileSystem = Objects.requireNonNull(fileSystem);
    }

    @Override
    public InputStream getDataForAttachment(Attachment attachment) throws AttachmentDataNotFoundException {
        try {
            return this.getDataForAttachment(attachment, AttachmentDataStreamType.RAW_BINARY).getInputStream();
        }
        catch (IOException e) {
            throw new AttachmentDataNotFoundException("Failed to obtain InputStream for  " + attachment, e);
        }
    }

    @Override
    public AttachmentDataStream getDataForAttachment(Attachment attachment, AttachmentDataStreamType dataStreamType) throws AttachmentDataNotFoundException {
        return this.getDataForAttachment(attachment, dataStreamType, Optional.empty());
    }

    @Override
    public AttachmentDataStream getDataForAttachment(Attachment attachment, AttachmentDataStreamType dataStreamType, Optional<RangeRequest> range) throws AttachmentDataNotFoundException {
        Preconditions.checkArgument((attachment != null ? 1 : 0) != 0, (Object)"The attachment cannot be null.");
        Preconditions.checkArgument((dataStreamType != null ? 1 : 0) != 0, (Object)"The dataStreamType cannot be null.");
        try {
            return this.fileSystem.getAttachmentData(Refs.ref(attachment), dataStreamType, range);
        }
        catch (AttachmentDataFileSystemException ex) {
            throw new AttachmentDataNotFoundException("Problem while getting attachment stream (" + attachment + ") from file system", ex);
        }
    }

    @Override
    public void removeDataForAttachment(Attachment attachment, ContentEntityObject originalContent) {
        Preconditions.checkArgument((attachment != null ? 1 : 0) != 0, (Object)"The attachment cannot be null.");
        Preconditions.checkArgument((originalContent != null ? 1 : 0) != 0, (Object)"The content cannot be null.");
        if (!attachment.isLatestVersion()) {
            throw new IllegalArgumentException("Attachment must be latest version");
        }
        this.fileSystem.deleteAllAttachmentVersions(Refs.ref(attachment), Refs.ref(originalContent));
    }

    @Override
    public void removeDataForAttachmentVersion(Attachment attachment, ContentEntityObject originalContent) {
        Preconditions.checkArgument((attachment != null ? 1 : 0) != 0, (Object)"The attachment cannot be null.");
        Preconditions.checkArgument((originalContent != null ? 1 : 0) != 0, (Object)"The content cannot be null.");
        this.fileSystem.deleteSingleAttachmentVersion(Refs.ref(attachment), Refs.ref(originalContent));
    }

    @Override
    public void removeDataForAttachmentVersion(Attachment attachment, ContentEntityObject originalContent, AttachmentDataStreamType dataStreamType) {
        Preconditions.checkArgument((attachment != null ? 1 : 0) != 0, (Object)"The attachment cannot be null.");
        Preconditions.checkArgument((originalContent != null ? 1 : 0) != 0, (Object)"The content cannot be null.");
        this.fileSystem.deleteSingleAttachmentVersion(Refs.ref(attachment), Refs.ref(originalContent), dataStreamType);
    }

    @Override
    public void moveDataForAttachmentVersion(Attachment sourceAttachmentVersion, Attachment targetAttachmentVersion) {
        this.fileSystem.moveDataForAttachmentVersion(Refs.ref(sourceAttachmentVersion), Refs.ref(targetAttachmentVersion));
    }

    @Override
    public void saveDataForAttachment(Attachment attachment, InputStream data) {
        this.saveDataForAttachment(attachment, FileSystemAttachmentDataDao.rawBinaryWrapper(data), false);
    }

    private static AttachmentDataStream rawBinaryWrapper(InputStream data) {
        return AttachmentDataStream.create(AttachmentDataStreamType.RAW_BINARY, (InputStreamSource)new InputStreamResource(data));
    }

    @Override
    public void saveDataForAttachment(Attachment attachment, AttachmentDataStream dataStream) {
        this.saveDataForAttachment(attachment, dataStream, false);
    }

    private void saveDataForAttachment(Attachment attachment, AttachmentDataStream dataStream, boolean overwrite) {
        Preconditions.checkArgument((attachment != null ? 1 : 0) != 0, (Object)"The attachment cannot be null");
        Preconditions.checkArgument((dataStream != null ? 1 : 0) != 0, (Object)"The data to be written cannot be null.");
        this.fileSystem.saveAttachmentData(Refs.ref(attachment), dataStream, overwrite, DataSize.ofBytes((long)attachment.getFileSize()));
    }

    @Override
    public void saveDataForAttachmentVersion(Attachment attachment, Attachment previousVersion, InputStream data) {
        this.saveDataForAttachment(attachment, FileSystemAttachmentDataDao.rawBinaryWrapper(data), false);
    }

    @Override
    public void saveDataForAttachmentVersion(Attachment attachment, Attachment previousVersion, AttachmentDataStream dataStream) {
        this.saveDataForAttachment(attachment, dataStream);
    }

    @Override
    public void replaceDataForAttachment(Attachment attachment, InputStream data) {
        this.saveDataForAttachment(attachment, FileSystemAttachmentDataDao.rawBinaryWrapper(data), true);
    }

    @Override
    public void replaceDataForAttachment(Attachment attachment, AttachmentDataStream dataStream) {
        this.saveDataForAttachment(attachment, dataStream, true);
    }

    @Override
    public boolean isAttachmentPresent(Attachment attachment) {
        return this.fileSystem.dataExistsForAttachment(Refs.ref(attachment));
    }

    @Override
    public void moveAttachment(Attachment attachment, Attachment oldAttachment, ContentEntityObject newContent) {
        Preconditions.checkArgument((attachment != null ? 1 : 0) != 0, (Object)"The attachment cannot be null.");
        Preconditions.checkArgument((oldAttachment != null ? 1 : 0) != 0, (Object)"The old attachment cannot be null.");
        Preconditions.checkArgument((newContent != null ? 1 : 0) != 0, (Object)"The new content object of the attachment cannot be null.");
        this.fileSystem.moveAttachment(Refs.ref(oldAttachment), Refs.ref(attachment), Refs.ref(newContent));
    }

    @Override
    public void prepareForMigrationTo() {
        this.fileSystem.prepareForMigrationTo();
    }

    @Override
    public void afterMigrationFrom() {
    }

    @Override
    public final AttachmentDataStorageType getStorageType() {
        return AttachmentDataStorageType.FILE_SYSTEM;
    }
}

