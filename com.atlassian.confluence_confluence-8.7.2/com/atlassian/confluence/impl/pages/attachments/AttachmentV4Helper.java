/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.FileStore$Path
 */
package com.atlassian.confluence.impl.pages.attachments;

import com.atlassian.confluence.impl.pages.attachments.filesystem.AttachmentDataFile;
import com.atlassian.confluence.impl.pages.attachments.filesystem.model.AttachmentRef;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStreamType;
import com.atlassian.dc.filestore.api.FileStore;

public class AttachmentV4Helper {
    public static final int TOTAL_NUM_OF_SUBFOLDERS = 65535;
    public static final int NUM_OF_SUBFOLDERS = 256;

    public static <T extends FileStore.Path> T getDirectoryV004(T basePath, long id) {
        long domain = id % 65535L;
        long folder1 = domain % 256L;
        long folder2 = domain / 256L;
        return (T)basePath.path(new String[]{String.valueOf(folder1)}).path(new String[]{String.valueOf(folder2)});
    }

    public static <T extends FileStore.Path> T getContainerPathForAttachmentVersions(T basePath, long attachmentId) {
        String attachmentIdString = Long.toString(attachmentId);
        T attachmentDir = AttachmentV4Helper.getDirectoryV004(basePath, attachmentId);
        return (T)attachmentDir.path(new String[]{attachmentIdString});
    }

    public <T extends FileStore.Path> AttachmentDataFile<T> getAttachmentDataFileV004(T basePath, AttachmentRef attachment, AttachmentDataStreamType dataStreamType) {
        return AttachmentDataFile.getAttachmentDataFileV004(basePath, attachment.getId(), attachment.getVersion(), dataStreamType);
    }
}

