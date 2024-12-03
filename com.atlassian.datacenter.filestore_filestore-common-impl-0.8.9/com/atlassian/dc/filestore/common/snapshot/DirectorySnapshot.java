/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.Snapshot
 */
package com.atlassian.dc.filestore.common.snapshot;

import com.atlassian.dc.filestore.api.Snapshot;
import com.atlassian.dc.filestore.common.snapshot.FileTreeUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Objects;

public final class DirectorySnapshot
implements Snapshot {
    private final Path rootDir;

    private DirectorySnapshot(Path rootDir) {
        this.rootDir = Objects.requireNonNull(rootDir);
    }

    public static DirectorySnapshot copyOf(Path sourceRoot) throws IOException {
        Path tempDirectory = Files.createTempDirectory(DirectorySnapshot.class.getSimpleName(), new FileAttribute[0]);
        FileTreeUtils.copyFileTree(sourceRoot, tempDirectory);
        return new DirectorySnapshot(tempDirectory);
    }

    public void unpack(Path destination) throws IOException {
        FileTreeUtils.copyFileTree(this.rootDir, destination);
    }

    public void close() throws IOException {
        FileTreeUtils.deleteFileTree(this.rootDir);
    }
}

