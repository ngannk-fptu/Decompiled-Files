/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.FileStore$Path
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.unit.DataSize
 */
package com.atlassian.confluence.impl.pages.attachments.objectstorage;

import com.atlassian.confluence.core.service.NotValidException;
import com.atlassian.confluence.impl.pages.attachments.AttachmentV4Helper;
import com.atlassian.confluence.impl.pages.attachments.filesystem.AttachmentDataFile;
import com.atlassian.confluence.impl.pages.attachments.filesystem.AttachmentDataFileSystem;
import com.atlassian.confluence.impl.pages.attachments.filesystem.model.AttachmentRef;
import com.atlassian.confluence.impl.pages.attachments.objectstorage.ObjectStorageAttachmentDataUtil;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStream;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStreamType;
import com.atlassian.confluence.pages.persistence.dao.filesystem.AttachmentDataFileSystemException;
import com.atlassian.confluence.web.rangerequest.RangeRequest;
import com.atlassian.dc.filestore.api.FileStore;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.unit.DataSize;

public class ObjectStorageAttachmentDataFileSystem
implements AttachmentDataFileSystem {
    private static final Logger log = LoggerFactory.getLogger(ObjectStorageAttachmentDataFileSystem.class);
    private final FileStore.Path rootDir;

    public ObjectStorageAttachmentDataFileSystem(FileStore.Path rootDir) {
        this.rootDir = rootDir;
        log.info("The ObjectStorageAttachmentDataFileSystem has been initialised with the path [{}]", (Object)rootDir.getPathName());
    }

    public FileStore.Path getRootDir() {
        return this.rootDir;
    }

    @Override
    public boolean dataExistsForAttachment(AttachmentRef attachment) {
        AttachmentDataFile<FileStore.Path> dataFile = AttachmentDataFile.getAttachmentDataFileV004(this.rootDir, attachment, AttachmentDataStreamType.RAW_BINARY);
        FileStore.Path destinationPath = dataFile.getFilePath();
        try {
            return destinationPath.fileExists();
        }
        catch (IOException ex) {
            throw new AttachmentDataFileSystemException("Failed to check if data exists for attachment in S3 object storage: " + destinationPath.getPathName(), ex);
        }
    }

    @Override
    public boolean saveAttachmentData(AttachmentRef attachment, AttachmentDataStream attachmentDataStream, boolean overwrite, DataSize expectedFileSize) {
        AttachmentDataFile<FileStore.Path> dataFile = AttachmentDataFile.getAttachmentDataFileV004(this.rootDir, attachment, attachmentDataStream.getType());
        FileStore.Path destinationPath = dataFile.getFilePath();
        try {
            if (!overwrite && destinationPath.fileExists()) {
                log.info("Attachment [{}] already exists in S3 object storage, moving on", (Object)destinationPath.getPathName());
                return false;
            }
            Long expectedFileSizeBytes = attachmentDataStream.getType() == AttachmentDataStreamType.RAW_BINARY ? Long.valueOf(expectedFileSize.toBytes()) : null;
            InputStream stream = attachmentDataStream.getInputStream();
            ObjectStorageAttachmentDataUtil.writeStreamToPath(stream, destinationPath, expectedFileSizeBytes);
            return true;
        }
        catch (IOException ex) {
            throw new AttachmentDataFileSystemException("Failed to open InputStream for new S3 object storage attachment: " + destinationPath.getPathName(), ex);
        }
    }

    @Override
    public void deleteAllAttachmentVersions(AttachmentRef attachment, AttachmentRef.Container contentEntity) {
        FileStore.Path attachmentPath = AttachmentV4Helper.getContainerPathForAttachmentVersions(this.rootDir, attachment.getId());
        try {
            attachmentPath.getFileDescendents().forEach(version -> {
                try {
                    version.deleteFile();
                }
                catch (IOException ex) {
                    log.error("Failed to delete version [{}] of attachment [{}] from path [{}] in S3 object storage. Caused by: [{}]", new Object[]{attachment.getVersion(), attachment.getId(), version.getPathName(), ex.getMessage()});
                }
            });
        }
        catch (IOException ex) {
            log.error("Error obtaining list of versions to delete for attachment from S3 object storage: [{}]", (Object)attachmentPath.getPathName());
        }
    }

    @Override
    public void deleteSingleAttachmentVersion(AttachmentRef attachment, AttachmentRef.Container contentEntity) {
        for (AttachmentDataStreamType type : AttachmentDataStreamType.values()) {
            this.deleteSingleAttachmentVersion(attachment, contentEntity, type);
        }
    }

    @Override
    public void deleteSingleAttachmentVersion(AttachmentRef attachment, AttachmentRef.Container contentEntity, AttachmentDataStreamType dataStreamType) {
        AttachmentDataFile<FileStore.Path> dataFile = AttachmentDataFile.getAttachmentDataFileV004(this.rootDir, attachment, dataStreamType);
        FileStore.Path attachmentType = dataFile.getFilePath();
        try {
            if (!attachmentType.fileExists()) {
                log.warn("Could not find version [{}] of attachment [{}] when trying to delete it from path [{}] in S3 object storage", new Object[]{attachment.getVersion(), attachment.getId(), attachmentType.getPathName()});
                return;
            }
            attachmentType.deleteFile();
        }
        catch (IOException ex) {
            log.error("Failed to delete version [{}] of attachment [{}] from path [{}] in S3 object storage. Caused by: [{}]", new Object[]{attachment.getVersion(), attachment.getId(), attachmentType.getPathName(), ex.getMessage()});
        }
    }

    @Override
    public AttachmentDataStream getAttachmentData(AttachmentRef attachment, AttachmentDataStreamType dataStreamType) {
        return this.getAttachmentData(attachment, dataStreamType, Optional.empty());
    }

    @Override
    public AttachmentDataStream getAttachmentData(AttachmentRef attachment, final AttachmentDataStreamType dataStreamType, Optional<RangeRequest> range) {
        AttachmentDataFile<FileStore.Path> dataFile = AttachmentDataFile.getAttachmentDataFileV004(this.rootDir, attachment, dataStreamType);
        final FileStore.Path destinationPath = dataFile.getFilePath();
        try {
            if (!destinationPath.fileExists()) {
                throw new AttachmentDataFileSystemException("No such attachment in S3 object storage: " + destinationPath.getPathName());
            }
            AttachmentDataStream dataStream = new AttachmentDataStream(){

                @Override
                public AttachmentDataStreamType getType() {
                    return dataStreamType;
                }

                @Override
                public InputStream getInputStream() throws IOException {
                    return destinationPath.fileReader().openInputStream();
                }
            };
            return range.map((dataStream)::limit).orElse(dataStream);
        }
        catch (IOException exception) {
            throw new AttachmentDataFileSystemException("Failed to read data for attachment from S3 object storage: " + destinationPath.getPathName(), exception);
        }
    }

    @Override
    @Deprecated(since="8.3.0")
    public AttachmentDataFile<FilesystemPath> getAttachmentDataFile(long attachmentId, @Nullable Long containerId, @Nullable Long spaceId, Integer attachmentVersion, AttachmentDataStreamType dataStreamType) {
        throw new NotValidException();
    }

    @Override
    @Deprecated
    public void prepareForMigrationTo() {
    }

    @Override
    @Deprecated
    public void moveDataForAttachmentVersion(AttachmentRef sourceAttachmentVersion, AttachmentRef targetAttachmentVersion) {
    }

    @Override
    @Deprecated
    public void moveAttachment(AttachmentRef oldAttachment, AttachmentRef newAttachment, AttachmentRef.Container newContent) {
    }

    @Override
    @Deprecated
    public void moveAttachments(AttachmentRef.Container contentEntity, AttachmentRef.Space oldSpace, AttachmentRef.Space newSpace) {
    }
}

