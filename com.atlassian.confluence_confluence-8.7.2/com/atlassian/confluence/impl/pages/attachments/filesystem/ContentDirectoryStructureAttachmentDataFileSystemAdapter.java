/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.unit.DataSize
 */
package com.atlassian.confluence.impl.pages.attachments.filesystem;

import com.atlassian.confluence.cluster.ZduManager;
import com.atlassian.confluence.cluster.ZduStatus;
import com.atlassian.confluence.impl.pages.attachments.filesystem.AttachmentDataFile;
import com.atlassian.confluence.impl.pages.attachments.filesystem.AttachmentDataFileSystem;
import com.atlassian.confluence.impl.pages.attachments.filesystem.ContentDirectoryStructureAttachmentDataFileSystemV003;
import com.atlassian.confluence.impl.pages.attachments.filesystem.ContentDirectoryStructureAttachmentDataFileSystemV004;
import com.atlassian.confluence.impl.pages.attachments.filesystem.model.AttachmentRef;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStream;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStreamType;
import com.atlassian.confluence.pages.persistence.dao.filesystem.AttachmentDataFileSystemException;
import com.atlassian.confluence.web.rangerequest.RangeRequest;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import java.util.Optional;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.unit.DataSize;

public final class ContentDirectoryStructureAttachmentDataFileSystemAdapter
implements AttachmentDataFileSystem {
    private static final Logger log = LoggerFactory.getLogger(ContentDirectoryStructureAttachmentDataFileSystemAdapter.class);
    private final ContentDirectoryStructureAttachmentDataFileSystemV003 structureV003;
    private final ContentDirectoryStructureAttachmentDataFileSystemV004 structureV004;
    private final ZduManager zduManager;

    public static ContentDirectoryStructureAttachmentDataFileSystemAdapter create(ContentDirectoryStructureAttachmentDataFileSystemV003 structureV003, ContentDirectoryStructureAttachmentDataFileSystemV004 structureV004, ZduManager zduManager) {
        return new ContentDirectoryStructureAttachmentDataFileSystemAdapter(structureV003, structureV004, zduManager);
    }

    ContentDirectoryStructureAttachmentDataFileSystemAdapter(ContentDirectoryStructureAttachmentDataFileSystemV003 structureV003, ContentDirectoryStructureAttachmentDataFileSystemV004 structureV004, ZduManager zduManager) {
        this.structureV003 = structureV003;
        this.structureV004 = structureV004;
        this.zduManager = zduManager;
    }

    private boolean dataExistsForAttachmentInVer004(AttachmentRef attachmentRef) {
        return this.structureV004.dataExistsForAttachment(attachmentRef);
    }

    private boolean dataExistsForAttachmentInVer003(AttachmentRef attachmentRef) {
        return this.structureV003.containerPathForAttachmentVersions(attachmentRef.getId(), attachmentRef.getContainer().getId(), ContentDirectoryStructureAttachmentDataFileSystemAdapter.getSpaceIdForContent(attachmentRef.getContainer())).asJavaFile().exists();
    }

    private boolean dataExistsForAttachmentInVer003(AttachmentRef attachmentRef, AttachmentRef.Container originalContent) {
        return this.structureV003.containerPathForAttachmentVersions(attachmentRef.getId(), originalContent.getId(), ContentDirectoryStructureAttachmentDataFileSystemAdapter.getSpaceIdForContent(originalContent)).asJavaFile().exists();
    }

    @Nullable
    private static Long getSpaceIdForContent(AttachmentRef.Container content) {
        return content.getSpace().map(AttachmentRef.Space::getId).orElse(null);
    }

    @Override
    public boolean dataExistsForAttachment(AttachmentRef attachment) {
        return this.structureV004.dataExistsForAttachment(attachment) || this.structureV003.dataExistsForAttachment(attachment);
    }

    @Override
    @Deprecated
    public void moveAttachment(AttachmentRef oldAttachment, AttachmentRef newAttachment, AttachmentRef.Container newContent) {
        if (this.dataExistsForAttachmentInVer003(oldAttachment)) {
            this.structureV003.moveAttachment(oldAttachment, newAttachment, newContent);
        }
    }

    @Override
    public boolean saveAttachmentData(AttachmentRef attachmentVersion, AttachmentDataStream attachmentDataStream, boolean overwrite, DataSize expectedFileSize) {
        if (this.dataExistsForAttachmentInVer003(attachmentVersion) || this.zduManager.getUpgradeStatus().getState().equals((Object)ZduStatus.State.ENABLED) && this.structureV003.getRootPath().asJavaFile().exists()) {
            return this.structureV003.saveAttachmentData(attachmentVersion, attachmentDataStream, overwrite, expectedFileSize);
        }
        return this.structureV004.saveAttachmentData(attachmentVersion, attachmentDataStream, overwrite, expectedFileSize);
    }

    @Override
    public AttachmentDataFile<FilesystemPath> getAttachmentDataFile(long attachmentId, @Nullable Long containerId, @Nullable Long spaceId, Integer attachmentVersion, AttachmentDataStreamType dataStreamType) {
        AttachmentDataFile<FilesystemPath> attachmentDataFileV4 = this.structureV004.getAttachmentDataFile(attachmentId, null, null, attachmentVersion, dataStreamType);
        if (attachmentDataFileV4.exists() || containerId == null) {
            return attachmentDataFileV4;
        }
        AttachmentDataFile<FilesystemPath> attachmentDataFileV3 = this.structureV003.getAttachmentDataFile(attachmentId, containerId, spaceId, attachmentVersion, dataStreamType);
        if (attachmentDataFileV3.exists()) {
            return attachmentDataFileV3;
        }
        return attachmentDataFileV4;
    }

    @Override
    public void deleteAllAttachmentVersions(AttachmentRef attachment, AttachmentRef.Container originalContent) {
        if (this.dataExistsForAttachmentInVer003(attachment, originalContent)) {
            this.structureV003.deleteAllAttachmentVersions(attachment, originalContent);
        }
        if (this.dataExistsForAttachmentInVer004(attachment)) {
            this.structureV004.deleteAllAttachmentVersions(attachment, originalContent);
        }
    }

    @Override
    @Deprecated
    public void moveDataForAttachmentVersion(AttachmentRef sourceAttachmentVersion, AttachmentRef targetAttachmentVersion) {
        if (this.dataExistsForAttachmentInVer003(sourceAttachmentVersion)) {
            this.structureV003.moveDataForAttachmentVersion(sourceAttachmentVersion, targetAttachmentVersion);
        }
    }

    @Override
    public void deleteSingleAttachmentVersion(AttachmentRef attachment, AttachmentRef.Container originalContent) {
        if (this.dataExistsForAttachmentInVer003(attachment, originalContent)) {
            this.structureV003.deleteSingleAttachmentVersion(attachment, originalContent);
        }
        if (this.dataExistsForAttachmentInVer004(attachment)) {
            this.structureV004.deleteSingleAttachmentVersion(attachment, originalContent);
        }
    }

    @Override
    public void deleteSingleAttachmentVersion(AttachmentRef attachment, AttachmentRef.Container originalContent, AttachmentDataStreamType dataStreamType) {
        if (this.dataExistsForAttachmentInVer003(attachment, originalContent)) {
            this.structureV003.deleteSingleAttachmentVersion(attachment, originalContent, dataStreamType);
        }
        if (this.dataExistsForAttachmentInVer004(attachment)) {
            this.structureV004.deleteSingleAttachmentVersion(attachment, originalContent, dataStreamType);
        }
    }

    @Override
    public AttachmentDataStream getAttachmentData(AttachmentRef attachment, AttachmentDataStreamType dataStreamType) {
        return this.getAttachmentData(attachment, dataStreamType, Optional.empty());
    }

    @Override
    public AttachmentDataStream getAttachmentData(AttachmentRef attachment, AttachmentDataStreamType dataStreamType, Optional<RangeRequest> range) {
        try {
            return this.structureV004.getAttachmentData(attachment, dataStreamType, range);
        }
        catch (AttachmentDataFileSystemException v4Exception) {
            try {
                return this.structureV003.getAttachmentData(attachment, dataStreamType, range);
            }
            catch (AttachmentDataFileSystemException v3Exception) {
                v4Exception.addSuppressed(v3Exception);
                throw v4Exception;
            }
        }
    }

    @Override
    @Deprecated
    public void moveAttachments(AttachmentRef.Container contentEntity, AttachmentRef.Space oldSpace, AttachmentRef.Space newSpace) {
        this.structureV003.moveAttachments(contentEntity, oldSpace, newSpace);
    }

    @Override
    @Deprecated
    public void prepareForMigrationTo() {
        this.structureV003.prepareForMigrationTo();
    }
}

