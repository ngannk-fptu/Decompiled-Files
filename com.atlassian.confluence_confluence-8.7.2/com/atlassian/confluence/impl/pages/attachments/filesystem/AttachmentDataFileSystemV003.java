/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.impl.pages.attachments.filesystem;

import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import java.io.File;
import java.util.Optional;
import javax.annotation.Nullable;

@Deprecated(since="8.1")
public interface AttachmentDataFileSystemV003 {
    @Deprecated
    default public File containerDirectoryForAttachmentVersions(long attachmentId, long containerId, Optional<Long> spaceId) {
        return this.containerPathForAttachmentVersions(attachmentId, containerId, spaceId.orElse(null)).asJavaFile();
    }

    public FilesystemPath containerPathForAttachmentVersions(long var1, long var3, @Nullable Long var5);

    @Deprecated
    default public File getDirectoryForSpace(Optional<Long> spaceId) {
        return this.getPathForSpace(spaceId.orElse(null)).asJavaFile();
    }

    @Deprecated
    public FilesystemPath getPathForSpace(@Nullable Long var1);
}

