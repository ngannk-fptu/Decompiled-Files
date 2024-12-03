/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  javax.annotation.Nullable
 *  org.apache.commons.io.FileUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.unit.DataSize
 */
package com.atlassian.confluence.impl.pages.attachments.filesystem;

import com.atlassian.confluence.impl.pages.attachments.AttachmentV4Helper;
import com.atlassian.confluence.impl.pages.attachments.filesystem.AttachmentDataFile;
import com.atlassian.confluence.impl.pages.attachments.filesystem.AttachmentDataFileSystem;
import com.atlassian.confluence.impl.pages.attachments.filesystem.AttachmentDataFileSystemMigrationBackupHelper;
import com.atlassian.confluence.impl.pages.attachments.filesystem.AttachmentDataStorageLocationResolver;
import com.atlassian.confluence.impl.pages.attachments.filesystem.model.AttachmentRef;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStream;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStreamType;
import com.atlassian.confluence.pages.persistence.dao.filesystem.AttachmentDataFileSystemException;
import com.atlassian.confluence.pages.persistence.dao.filesystem.FileSystemAttachmentDataUtil;
import com.atlassian.confluence.web.rangerequest.RangeRequest;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.unit.DataSize;

public class ContentDirectoryStructureAttachmentDataFileSystemV004
implements AttachmentDataFileSystem {
    private static final Logger log = LoggerFactory.getLogger(ContentDirectoryStructureAttachmentDataFileSystemV004.class);
    private final Supplier<FilesystemPath> rootDir;
    private final AttachmentV4Helper attachmentV4Helper;

    public static ContentDirectoryStructureAttachmentDataFileSystemV004 create(AttachmentDataStorageLocationResolver rootDir) {
        return new ContentDirectoryStructureAttachmentDataFileSystemV004(rootDir::getFileLocation, new AttachmentV4Helper());
    }

    ContentDirectoryStructureAttachmentDataFileSystemV004(Supplier<FilesystemPath> rootDir, AttachmentV4Helper attachmentV4Helper) {
        this.rootDir = rootDir;
        this.attachmentV4Helper = attachmentV4Helper;
    }

    @Override
    public boolean dataExistsForAttachment(AttachmentRef attachment) {
        return this.getContainerDirectoryForAttachmentVersions(attachment).exists();
    }

    @Override
    public boolean saveAttachmentData(AttachmentRef attachmentVersion, AttachmentDataStream attachmentDataStream, boolean overwrite, DataSize expectedFileSize) {
        boolean bl;
        block10: {
            File destFile = this.getAttachmentDataFile(attachmentVersion, attachmentDataStream.getType()).getFilePath().asJavaFile();
            if (destFile.exists() && !overwrite) {
                return false;
            }
            if (!destFile.getParentFile().isDirectory() && !destFile.getParentFile().mkdirs()) {
                log.warn("Failed to create target dir [{}] for [{}]", (Object)destFile.getParentFile().getAbsolutePath(), (Object)attachmentVersion);
            }
            Long expectedFileSizeBytes = attachmentDataStream.getType() == AttachmentDataStreamType.RAW_BINARY ? Long.valueOf(expectedFileSize.toBytes()) : null;
            InputStream stream = attachmentDataStream.getInputStream();
            try {
                FileSystemAttachmentDataUtil.writeStreamToFile(stream, destFile, expectedFileSizeBytes);
                bl = true;
                if (stream == null) break block10;
            }
            catch (Throwable throwable) {
                try {
                    if (stream != null) {
                        try {
                            stream.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException ex) {
                    throw new AttachmentDataFileSystemException("Failed to open InputStream for new " + attachmentVersion, ex);
                }
            }
            stream.close();
        }
        return bl;
    }

    @Override
    public AttachmentDataFile<FilesystemPath> getAttachmentDataFile(long attachmentId, @Nullable Long containerId, @Nullable Long spaceId, Integer attachmentVersion, AttachmentDataStreamType dataStreamType) {
        return AttachmentDataFile.getAttachmentDataFileV004(this.rootDir.get(), attachmentId, attachmentVersion, dataStreamType);
    }

    @Override
    public void deleteAllAttachmentVersions(AttachmentRef attachment, AttachmentRef.Container originalContent) {
        File attachmentDirectory = this.getContainerDirectoryForAttachmentVersions(attachment);
        if (!attachmentDirectory.exists()) {
            log.error("Could not find attachment folder to remove at [{}].", (Object)attachmentDirectory.getAbsolutePath());
            return;
        }
        try {
            FileUtils.deleteDirectory((File)attachmentDirectory);
            FileSystemAttachmentDataUtil.cleanupEmptyAncestors(attachmentDirectory, this.getRootDirectory());
        }
        catch (IOException ex) {
            log.error("Error removing the attachment directory with path [{}].", (Object)attachmentDirectory.getAbsolutePath());
        }
    }

    @Override
    public void deleteSingleAttachmentVersion(AttachmentRef attachment, AttachmentRef.Container originalContent) {
        FilesystemPath attachmentPath = AttachmentV4Helper.getContainerPathForAttachmentVersions(this.rootDir.get(), attachment.getId());
        File attachmentFolder = attachmentPath.asJavaFile();
        if (!attachmentFolder.exists()) {
            log.warn("Could not find attachment folder at [{}] in order to remove the file representing [{}].", (Object)attachmentFolder, (Object)attachment);
            return;
        }
        for (AttachmentDataStreamType dataStreamType : AttachmentDataStreamType.values()) {
            AttachmentDataFile<FilesystemPath> attachmentFile = this.getAttachmentDataFile(attachment, dataStreamType);
            try {
                attachmentFile.delete();
            }
            catch (IOException ex) {
                log.warn("Failed to delete file for version {} of attachment {}: {}. DataStreamType: {}", new Object[]{attachment.getVersion(), attachment.getId(), attachmentFile, dataStreamType});
            }
        }
        if (attachmentFolder.listFiles().length == 0) {
            FileSystemAttachmentDataUtil.cleanupEmptyAncestors(attachmentFolder, this.getRootDirectory());
        }
    }

    @Override
    public void deleteSingleAttachmentVersion(AttachmentRef attachment, AttachmentRef.Container originalContent, AttachmentDataStreamType dataStreamType) {
        File attachmentFolder = this.getContainerDirectoryForAttachmentVersions(attachment);
        if (!attachmentFolder.exists()) {
            log.error("Could not find attachment folder at [{}] in order to remove the file representing [{}].", (Object)attachmentFolder.getAbsolutePath(), (Object)attachment);
            return;
        }
        AttachmentDataFile<FilesystemPath> attachmentFile = this.getAttachmentDataFile(attachment, dataStreamType);
        try {
            attachmentFile.delete();
        }
        catch (IOException ex) {
            log.warn("Failed to delete file for version {} of attachment {}: {}", new Object[]{attachment.getVersion(), attachment.getId(), attachmentFile});
        }
        if (attachmentFolder.listFiles().length == 0) {
            FileSystemAttachmentDataUtil.cleanupEmptyAncestors(attachmentFolder, this.getRootDirectory());
        }
    }

    @Override
    public AttachmentDataStream getAttachmentData(AttachmentRef attachment, AttachmentDataStreamType dataStreamType) {
        return this.getAttachmentData(attachment, dataStreamType, Optional.empty());
    }

    @Override
    public AttachmentDataStream getAttachmentData(AttachmentRef attachment, AttachmentDataStreamType dataStreamType, Optional<RangeRequest> range) {
        AttachmentDataFile<FilesystemPath> dataFile = this.getAttachmentDataFile(attachment, dataStreamType);
        if (dataFile.exists()) {
            AttachmentDataStream dataStream = AttachmentDataStream.create(dataStreamType, dataFile);
            return range.map(dataStream::limit).orElse(dataStream);
        }
        throw new AttachmentDataFileSystemException("No such file for " + attachment + ". Were looking at " + dataFile);
    }

    private FilesystemPath getRootPath() {
        return this.rootDir.get();
    }

    private File getRootDirectory() {
        return this.getRootPath().asJavaFile();
    }

    private File getContainerDirectoryForAttachmentVersions(AttachmentRef attachment) {
        return AttachmentV4Helper.getContainerPathForAttachmentVersions(this.rootDir.get(), attachment.getId()).asJavaFile();
    }

    protected AttachmentDataFile<FilesystemPath> getAttachmentDataFile(AttachmentRef attachmentVersion, AttachmentDataStreamType dataStreamType) {
        return this.attachmentV4Helper.getAttachmentDataFileV004(this.rootDir.get(), attachmentVersion, dataStreamType);
    }

    @Override
    @Deprecated
    public void moveAttachment(AttachmentRef oldAttachment, AttachmentRef newAttachment, AttachmentRef.Container newContent) {
    }

    @Override
    @Deprecated
    public void moveAttachments(AttachmentRef.Container contentEntity, AttachmentRef.Space oldSpace, AttachmentRef.Space newSpace) {
    }

    @Override
    @Deprecated
    public void prepareForMigrationTo() {
        new AttachmentDataFileSystemMigrationBackupHelper(this.getRootDirectory()).backupAttachments();
    }

    @Override
    @Deprecated
    public void moveDataForAttachmentVersion(AttachmentRef sourceAttachmentVersion, AttachmentRef targetAttachmentVersion) {
    }
}

