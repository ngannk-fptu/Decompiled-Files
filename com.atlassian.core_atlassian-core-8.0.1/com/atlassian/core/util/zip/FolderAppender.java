/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.core.util.zip;

import com.atlassian.core.util.zip.ArchiveParams;
import com.atlassian.core.util.zip.FileArchiver;
import java.io.File;
import java.io.IOException;
import javax.annotation.Nonnull;

public class FolderAppender {
    private final FileArchiver fileArchiver;
    private final File archiveFile;
    private final File rootFolderToCompress;
    private final ArchiveParams archiveParams;

    public FolderAppender(FileArchiver fileArchiver, File rootFolderToCompress, String targetRootArchivePath, File archiveFile) {
        this.fileArchiver = fileArchiver;
        this.rootFolderToCompress = rootFolderToCompress;
        this.archiveParams = ArchiveParams.builder().withArchiveFolderName(targetRootArchivePath).build();
        this.archiveFile = archiveFile;
    }

    public FolderAppender(FileArchiver fileArchiver, File rootFolderToCompress, ArchiveParams archiveParams, File archiveFile) {
        this.fileArchiver = fileArchiver;
        this.rootFolderToCompress = rootFolderToCompress;
        this.archiveParams = archiveParams;
        this.archiveFile = archiveFile;
    }

    public void append() throws IOException {
        this.append(this.rootFolderToCompress);
    }

    private void append(File file) throws IOException {
        if (file != null && file.exists() && (this.archiveParams.isIncludeHiddenFiles() || !file.isHidden())) {
            if (file.isFile()) {
                this.appendFile(file);
            } else if (file.isDirectory()) {
                this.appendFolder(file);
            }
        }
    }

    private void appendFolder(@Nonnull File file) throws IOException {
        this.fileArchiver.addDirectoryToArchive(file, this.getPathInsideArchive(file), this.archiveParams.getArchiveFolderName());
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                this.append(f);
            }
        }
    }

    private void appendFile(@Nonnull File file) throws IOException {
        if (file.isFile() && !file.equals(this.archiveFile)) {
            this.fileArchiver.addToArchive(file, this.getPathInsideArchive(file), this.archiveParams.getArchiveFolderName());
        }
    }

    private String getPathInsideArchive(@Nonnull File file) {
        return file.getPath().substring(this.rootFolderToCompress.getPath().length());
    }
}

