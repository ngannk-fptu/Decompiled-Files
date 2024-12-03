/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.DataSize
 *  com.atlassian.dc.filestore.api.FileStore
 *  com.atlassian.dc.filestore.api.compat.FilesystemAccess$AccessDenied
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.atlassian.dc.filestore.impl.filesystem;

import com.atlassian.dc.filestore.api.DataSize;
import com.atlassian.dc.filestore.api.FileStore;
import com.atlassian.dc.filestore.api.compat.FilesystemAccess;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import com.atlassian.dc.filestore.impl.filesystem.FileStorePathTraversalException;
import com.atlassian.dc.filestore.impl.filesystem.FilesystemPathImpl;
import com.atlassian.dc.filestore.impl.filesystem.FilesystemSpaceCalculator;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

public final class FilesystemFileStore
implements FileStore {
    private final FilesystemPathImpl root;

    public FilesystemFileStore(Path root) {
        this(root, true);
    }

    public FilesystemFileStore(Path root, boolean allowLegacyFilesystemApiAccess) {
        this(root, FilesystemFileStore.verifyLegacyFilesystemApiAccess(allowLegacyFilesystemApiAccess));
    }

    public FilesystemFileStore(Path root, Consumer<Path> legacyFilesystemApiAccessCheck) {
        this.root = new FilesystemPathImpl(root, root, path -> FileStorePathTraversalException.sanitise(root, path), legacyFilesystemApiAccessCheck);
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
    public FilesystemPath root() {
        return this.root;
    }

    public FilesystemPath path(String ... pathComponents) {
        return this.root().path(pathComponents);
    }

    public static FilesystemPath forPath(Path path) {
        return new FilesystemFileStore(path).root();
    }

    public static FilesystemPath forFile(File file) {
        return FilesystemFileStore.forPath(file.toPath());
    }

    public Optional<DataSize> getAvailableSpace() {
        return this.spaceCalc().flatMap(FilesystemSpaceCalculator::getAvailableSpace);
    }

    public Optional<DataSize> getTotalSpace() {
        return this.spaceCalc().flatMap(FilesystemSpaceCalculator::getTotalSpace);
    }

    private Optional<FilesystemSpaceCalculator> spaceCalc() {
        return FilesystemSpaceCalculator.createFor(this.root.getFilePath());
    }

    private static Consumer<Path> verifyLegacyFilesystemApiAccess(boolean allowLegacyFilesystemApiAccess) {
        return path -> {
            if (!allowLegacyFilesystemApiAccess) {
                throw new FilesystemAccess.AccessDenied(path);
            }
        };
    }
}

