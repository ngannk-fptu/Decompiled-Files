/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.compat.FilesystemAccess$AccessDenied
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  com.atlassian.dc.filestore.impl.filesystem.FilesystemFileStore
 */
package com.atlassian.confluence.impl.filestore;

import com.atlassian.dc.filestore.api.compat.FilesystemAccess;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import com.atlassian.dc.filestore.impl.filesystem.FilesystemFileStore;
import java.nio.file.Path;
import java.util.function.Consumer;

abstract class AbstractFileStoreFactory {
    public static final String SHARED_HOME_FILESYSTEM_API_ACCESS_DENIED = "confluence.sharedHome.filesystemApiAccessDenied";

    AbstractFileStoreFactory() {
    }

    protected abstract Path getConfluenceHomePath();

    protected abstract Path getLocalHomePath();

    protected abstract Path getSharedHomePath();

    public FilesystemFileStore getSharedHomeFileStore() {
        return new FilesystemFileStore(this.getSharedHomePath(), AbstractFileStoreFactory.checkFilesystemApiAccess());
    }

    public FilesystemFileStore getLocalHomeFileStore() {
        return new FilesystemFileStore(this.getLocalHomePath());
    }

    public FilesystemFileStore getConfluenceHomeFileStore() {
        return new FilesystemFileStore(this.getConfluenceHomePath(), AbstractFileStoreFactory.checkFilesystemApiAccess());
    }

    public FilesystemPath getSharedHome() {
        return this.getSharedHomeFileStore().root();
    }

    public FilesystemPath getLocalHome() {
        return this.getLocalHomeFileStore().root();
    }

    public FilesystemPath getConfluenceHome() {
        return this.getConfluenceHomeFileStore().root();
    }

    private static Consumer<Path> checkFilesystemApiAccess() {
        return path -> {
            boolean denied = Boolean.getBoolean(SHARED_HOME_FILESYSTEM_API_ACCESS_DENIED);
            if (denied) {
                throw new FilesystemAccess.AccessDenied(path);
            }
        };
    }
}

