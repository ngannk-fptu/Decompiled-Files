/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.bootstrap.AtlassianBootstrapManager
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  com.atlassian.dc.filestore.impl.filesystem.FilesystemFileStore
 */
package com.atlassian.confluence.impl.pages.attachments.filesystem;

import com.atlassian.config.bootstrap.AtlassianBootstrapManager;
import com.atlassian.confluence.impl.filestore.HomePathPlaceholderResolver;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import com.atlassian.dc.filestore.impl.filesystem.FilesystemFileStore;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class AttachmentDataStorageLocationResolver {
    private final AtlassianBootstrapManager bootstrapManager;
    private final HomePathPlaceholderResolver filesystemPathResolver;
    private final String attachmentSubdirectoryName;

    public AttachmentDataStorageLocationResolver(String attachmentSubdirectoryName, AtlassianBootstrapManager bootstrapManager, HomePathPlaceholderResolver filesystemPathResolver) {
        this.attachmentSubdirectoryName = attachmentSubdirectoryName;
        this.bootstrapManager = bootstrapManager;
        this.filesystemPathResolver = filesystemPathResolver;
    }

    public FilesystemPath getFileLocation() {
        FilesystemPath baseDir = this.resolveLocation(Objects.requireNonNull(this.bootstrapManager.getString("attachments.dir")));
        return baseDir.path(new String[]{this.attachmentSubdirectoryName});
    }

    private FilesystemPath resolveLocation(String directoryLocation) {
        return this.filesystemPathResolver.resolveFileStorePlaceHolders(directoryLocation).orElseGet(() -> FilesystemFileStore.forPath((Path)Paths.get(directoryLocation, new String[0])));
    }
}

