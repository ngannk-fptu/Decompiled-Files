/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.FileUtils
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  io.atlassian.fugue.Either
 *  javax.annotation.Nullable
 *  org.apache.commons.io.FileUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.unit.DataSize
 */
package com.atlassian.confluence.impl.pages.attachments.filesystem;

import com.atlassian.confluence.impl.pages.attachments.filesystem.AttachmentDataFile;
import com.atlassian.confluence.impl.pages.attachments.filesystem.AttachmentDataFileSystem;
import com.atlassian.confluence.impl.pages.attachments.filesystem.AttachmentDataFileSystemMigrationBackupHelper;
import com.atlassian.confluence.impl.pages.attachments.filesystem.AttachmentDataFileSystemV003;
import com.atlassian.confluence.impl.pages.attachments.filesystem.AttachmentDataStorageLocationResolver;
import com.atlassian.confluence.impl.pages.attachments.filesystem.model.AttachmentRef;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStream;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStreamType;
import com.atlassian.confluence.pages.persistence.dao.filesystem.AttachmentDataFileSystemException;
import com.atlassian.confluence.pages.persistence.dao.filesystem.FileSystemAttachmentDataUtil;
import com.atlassian.confluence.pages.persistence.dao.filesystem.HierarchicalContentFileSystemHelper;
import com.atlassian.confluence.schedule.jobs.filedeletion.DeferredFileDeletionQueue;
import com.atlassian.confluence.util.io.ConfluenceFileUtils;
import com.atlassian.confluence.web.rangerequest.RangeRequest;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import io.atlassian.fugue.Either;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.unit.DataSize;

@Deprecated
public class ContentDirectoryStructureAttachmentDataFileSystemV003
implements AttachmentDataFileSystem,
AttachmentDataFileSystemV003 {
    private static final Logger log = LoggerFactory.getLogger(ContentDirectoryStructureAttachmentDataFileSystemV003.class);
    public static final String NON_SPACED_DIRECTORY_NAME = "nonspaced";
    private final HierarchicalContentFileSystemHelper fileSystemHelper = new HierarchicalContentFileSystemHelper();
    private final Supplier<FilesystemPath> rootDir;
    private final DeferredFileDeletionQueue deferredFileDeletionQueue;

    public static ContentDirectoryStructureAttachmentDataFileSystemV003 create(AttachmentDataStorageLocationResolver rootDir, DeferredFileDeletionQueue deferredFileDeletionQueue) {
        return new ContentDirectoryStructureAttachmentDataFileSystemV003(rootDir::getFileLocation, deferredFileDeletionQueue);
    }

    ContentDirectoryStructureAttachmentDataFileSystemV003(Supplier<FilesystemPath> rootDir, DeferredFileDeletionQueue deferredFileDeletionQueue) {
        this.rootDir = rootDir;
        this.deferredFileDeletionQueue = deferredFileDeletionQueue;
    }

    @Override
    public boolean dataExistsForAttachment(AttachmentRef attachment) {
        return this.getContainerDirectoryForAttachmentVersions(attachment.getContainer(), attachment).exists();
    }

    @Override
    public void moveAttachment(AttachmentRef oldAttachment, AttachmentRef newAttachment, AttachmentRef.Container newContent) {
        File originalAttachmentFolder = this.getContainerDirectoryForAttachmentVersions(oldAttachment.getContainer(), oldAttachment);
        File newAttachmentFolder = this.getContainerDirectoryForAttachmentVersions(newContent, newAttachment);
        try {
            Either container = Either.left((Object)newAttachment);
            ConfluenceFileUtils.moveDirWithCopyFallback((Either<AttachmentRef, AttachmentRef.Container>)container, originalAttachmentFolder, newAttachmentFolder, this.deferredFileDeletionQueue);
            FileSystemAttachmentDataUtil.cleanupEmptyAncestors(originalAttachmentFolder, this.getRootDirectory());
        }
        catch (IOException e) {
            throw new AttachmentDataFileSystemException("Unable to move attachment (" + newAttachment + ") from [" + originalAttachmentFolder + "] to [" + newAttachmentFolder + "] - check folder permissions.", e);
        }
    }

    @Override
    public boolean saveAttachmentData(AttachmentRef attachmentVersion, AttachmentDataStream attachmentDataStream, boolean overwrite, DataSize expectedFileSize) {
        File destFile = this.getAttachmentDataFile(attachmentVersion, attachmentDataStream.getType()).getFilePath().asJavaFile();
        if (destFile.exists() && !overwrite) {
            return false;
        }
        if (!destFile.getParentFile().isDirectory() && !destFile.getParentFile().mkdirs()) {
            log.warn("Failed to create target dir [{}] for [{}]", (Object)destFile.getParentFile().getAbsolutePath(), (Object)attachmentVersion);
        }
        Long expectedFileSizeBytes = attachmentDataStream.getType() == AttachmentDataStreamType.RAW_BINARY ? Long.valueOf(expectedFileSize.toBytes()) : null;
        try {
            try (InputStream stream = attachmentDataStream.getInputStream();){
                FileSystemAttachmentDataUtil.writeStreamToFile(stream, destFile, expectedFileSizeBytes);
            }
            return true;
        }
        catch (IOException ex) {
            throw new AttachmentDataFileSystemException("Failed to open InputStream for new " + attachmentVersion, ex);
        }
    }

    private File getContainerDirectoryForAttachmentVersions(AttachmentRef.Container contentEntity, AttachmentRef attachment) {
        return this.getContainerPathForAttachmentVersions(contentEntity, attachment).asJavaFile();
    }

    private FilesystemPath getContainerPathForAttachmentVersions(AttachmentRef.Container contentEntity, AttachmentRef attachment) {
        return this.containerPathForAttachmentVersions(attachment.getId(), contentEntity.getId(), ContentDirectoryStructureAttachmentDataFileSystemV003.getSpaceIdForContent(contentEntity));
    }

    @Override
    public FilesystemPath containerPathForAttachmentVersions(long latestVersionAttachmentId, long contentId, @Nullable Long spaceId) {
        FilesystemPath contentDir = this.getContentEntityDirectory(contentId, spaceId);
        return contentDir.path(new String[]{Long.toString(latestVersionAttachmentId)});
    }

    @Override
    public AttachmentDataFile<FilesystemPath> getAttachmentDataFile(long attachmentId, Long containerId, @Nullable Long spaceId, Integer attachmentVersion, AttachmentDataStreamType dataStreamType) {
        FilesystemPath v003Path = this.containerPathForAttachmentVersions(attachmentId, containerId, spaceId);
        return AttachmentDataFile.getAttachmentDataFile(v003Path, attachmentVersion, dataStreamType);
    }

    @Nullable
    private static Long getSpaceIdForContent(AttachmentRef.Container content) {
        return content.getSpace().map(AttachmentRef.Space::getId).orElse(null);
    }

    @Override
    public void deleteAllAttachmentVersions(AttachmentRef attachment, AttachmentRef.Container originalContent) {
        File attachmentDirectory = this.getContainerDirectoryForAttachmentVersions(originalContent, attachment);
        if (!attachmentDirectory.exists()) {
            log.error("Could not find attachment folder to remove at [{}] for title [{}].", (Object)attachmentDirectory.getAbsolutePath(), (Object)originalContent);
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
    public void moveDataForAttachmentVersion(AttachmentRef sourceAttachmentVersion, AttachmentRef targetAttachmentVersion) {
        FilesystemPath targetAttachmentDirectory;
        FilesystemPath sourceAttachmentDirectory = this.getContainerPathForAttachmentVersions(sourceAttachmentVersion.getContainer(), sourceAttachmentVersion);
        if (!sourceAttachmentDirectory.equals(targetAttachmentDirectory = this.getContainerPathForAttachmentVersions(targetAttachmentVersion.getContainer(), targetAttachmentVersion))) {
            throw new IllegalArgumentException(String.format("Expected source [%s] and target [%s] to resolve to the same attachment directory, but source resolved to [%s] and target resolved to [%s].", sourceAttachmentVersion, targetAttachmentVersion, sourceAttachmentDirectory, targetAttachmentDirectory));
        }
        for (AttachmentDataFile<FilesystemPath> existingTargetAttachmentFile : ContentDirectoryStructureAttachmentDataFileSystemV003.getFilesForAttachmentVersion(targetAttachmentDirectory, targetAttachmentVersion.getVersion())) {
            try {
                existingTargetAttachmentFile.delete();
            }
            catch (IOException e) {
                String message = String.format("Could delete file [%s] in order to prepare moving (rename or copy) [%s] to it, see cause.", existingTargetAttachmentFile, sourceAttachmentVersion);
                throw new IllegalStateException(message, e);
            }
        }
        for (AttachmentDataFile<FilesystemPath> sourceAttachmentVersionFile : ContentDirectoryStructureAttachmentDataFileSystemV003.getFilesForAttachmentVersion(sourceAttachmentDirectory, sourceAttachmentVersion.getVersion())) {
            AttachmentDataFile<FilesystemPath> targetFile = sourceAttachmentVersionFile.withVersion(targetAttachmentVersion.getVersion());
            try {
                sourceAttachmentVersionFile.moveTo(targetFile);
            }
            catch (IOException e) {
                String message = String.format("Could not move (rename or copy) file [%s] to [%s], see cause.", sourceAttachmentVersion, targetFile);
                throw new IllegalStateException(message, e);
            }
        }
    }

    @Override
    public void deleteSingleAttachmentVersion(AttachmentRef attachment, AttachmentRef.Container originalContent) {
        FilesystemPath attachmentPath = this.getContainerPathForAttachmentVersions(originalContent, attachment);
        File attachmentFolder = attachmentPath.asJavaFile();
        if (!attachmentFolder.exists()) {
            log.error("Could not find attachment folder at [{}] in order to remove the file representing [{}].", (Object)attachmentFolder, (Object)attachment);
            return;
        }
        for (AttachmentDataFile<FilesystemPath> attachmentFile : ContentDirectoryStructureAttachmentDataFileSystemV003.getFilesForAttachmentVersion(attachmentPath, attachment.getVersion())) {
            try {
                attachmentFile.delete();
            }
            catch (IOException ex) {
                log.warn("Failed to delete file for version {} of attachment {}: {}", new Object[]{attachment.getVersion(), attachment.getId(), attachmentFile});
            }
        }
        if (attachmentFolder.listFiles().length == 0) {
            FileSystemAttachmentDataUtil.cleanupEmptyAncestors(attachmentFolder, this.getRootDirectory());
        }
    }

    @Override
    public void deleteSingleAttachmentVersion(AttachmentRef attachment, AttachmentRef.Container originalContent, AttachmentDataStreamType dataStreamType) {
        File attachmentFolder = this.getContainerDirectoryForAttachmentVersions(originalContent, attachment);
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

    private FilesystemPath getContentEntityDirectory(long contentId, @Nullable Long spaceId) {
        return this.fileSystemHelper.getDirectory(this.getPathForSpace(spaceId), contentId);
    }

    @Override
    public FilesystemPath getPathForSpace(@Nullable Long spaceId) {
        return spaceId != null ? this.fileSystemHelper.getDirectory(this.getRootPath(), (long)spaceId) : this.getRootPath().path(new String[]{NON_SPACED_DIRECTORY_NAME});
    }

    public FilesystemPath getRootPath() {
        return this.rootDir.get();
    }

    private File getRootDirectory() {
        return this.getRootPath().asJavaFile();
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

    private AttachmentDataFile<FilesystemPath> getAttachmentDataFile(AttachmentRef attachmentVersion, AttachmentDataStreamType dataStreamType) {
        FilesystemPath attachmentBaseDirectory = this.getContainerPathForAttachmentVersions(attachmentVersion.getContainer(), attachmentVersion);
        return AttachmentDataFile.getAttachmentDataFile(attachmentBaseDirectory, attachmentVersion, dataStreamType);
    }

    @Override
    public void moveAttachments(AttachmentRef.Container contentEntity, AttachmentRef.Space oldSpace, AttachmentRef.Space newSpace) {
        File oldLocation = this.getContentEntityDirectory(contentEntity.getId(), oldSpace.getId()).asJavaFile();
        if (!oldLocation.exists()) {
            return;
        }
        File newLocation = this.getContentEntityDirectory(contentEntity.getId(), newSpace.getId()).asJavaFile();
        if (newLocation.exists()) {
            File movedOutOfWay = new File(newLocation.getParent(), newLocation.getName() + "_moved_out_of_the_way");
            log.warn("Attachment directory '{}' unexpectedly already exists.  Renaming to '{}'.  This probably implies a previous failed move.", (Object)newLocation, (Object)movedOutOfWay);
            try {
                if (movedOutOfWay.exists()) {
                    FileUtils.deleteDirectory((File)movedOutOfWay);
                }
                FileUtils.moveDirectory((File)newLocation, (File)movedOutOfWay);
            }
            catch (IOException e) {
                throw new AttachmentDataFileSystemException("Failed to move directory out of the way", e);
            }
        }
        try {
            boolean isRename;
            boolean bl = isRename = contentEntity.tryRenameOnMove() && oldLocation.renameTo(newLocation);
            if (!isRename) {
                log.debug("Could not rename to new dir going to copy directory");
                com.atlassian.core.util.FileUtils.copyDirectory((File)oldLocation, (File)newLocation, (boolean)true);
                Either container = Either.right((Object)contentEntity);
                this.deferredFileDeletionQueue.offer((Either<AttachmentRef, AttachmentRef.Container>)container, oldLocation);
            }
        }
        catch (IOException ex) {
            String msg = "Could not move the attachment data along with the page for move of " + contentEntity;
            log.error(msg, (Throwable)ex);
            throw new AttachmentDataFileSystemException("Could not move the attachment data along with the page for move of " + contentEntity, ex);
        }
    }

    @Override
    public void prepareForMigrationTo() {
        new AttachmentDataFileSystemMigrationBackupHelper(this.getRootDirectory()).backupAttachments();
    }

    private static Collection<AttachmentDataFile<FilesystemPath>> getFilesForAttachmentVersion(FilesystemPath containerDir, int versionNumber) {
        try {
            return AttachmentDataFile.fromContainer(containerDir).filter(dataFile -> dataFile.matchesVersion(versionNumber)).collect(Collectors.toList());
        }
        catch (IOException e) {
            log.warn("Failed to determine descendents of {}", (Object)containerDir, (Object)e);
            return Collections.emptyList();
        }
    }
}

