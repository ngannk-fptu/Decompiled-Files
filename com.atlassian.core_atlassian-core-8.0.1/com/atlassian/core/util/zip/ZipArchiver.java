/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.NotThreadSafe
 *  org.apache.commons.io.IOUtils
 */
package com.atlassian.core.util.zip;

import com.atlassian.core.util.zip.ArchiveParams;
import com.atlassian.core.util.zip.FileArchiver;
import com.atlassian.core.util.zip.FilePathUtils;
import com.atlassian.core.util.zip.FolderAppender;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import org.apache.commons.io.IOUtils;

@NotThreadSafe
public class ZipArchiver {
    @Nonnull
    private final File archiveFile;
    @Nullable
    private FileArchiver fileArchiver;
    @Nullable
    private ZipOutputStream zipOutputStream;

    public ZipArchiver(File archiveFile) {
        this.archiveFile = archiveFile;
    }

    public void addFolder(File folderToCompress, String archiveFolderName) throws IOException {
        FolderAppender folderAppender = new FolderAppender(this.getFileArchiver(), folderToCompress, archiveFolderName, this.archiveFile);
        folderAppender.append();
    }

    public void addFolder(File folderToCompress, ArchiveParams archiveParams) throws IOException {
        FolderAppender folderAppender = new FolderAppender(this.getFileArchiver(), folderToCompress, archiveParams, this.archiveFile);
        folderAppender.append();
    }

    public void addFile(File sourceFile, String targetPath) throws IOException {
        this.getFileArchiver().addToArchive(sourceFile, targetPath, "");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addEntry(InputStream content, String entryPath) throws IOException {
        ZipEntry entry = new ZipEntry(FilePathUtils.stripSlashes(entryPath));
        this.getZipOutputStream().putNextEntry(entry);
        try {
            IOUtils.copyLarge((InputStream)content, (OutputStream)this.zipOutputStream);
        }
        finally {
            this.getZipOutputStream().closeEntry();
            IOUtils.closeQuietly((InputStream)content);
        }
    }

    public OutputStream addNextEntry(String entryName) throws IOException {
        this.getZipOutputStream().putNextEntry(new ZipEntry(entryName));
        return this.getZipOutputStream();
    }

    public void close() throws IOException {
        if (this.zipOutputStream != null) {
            this.zipOutputStream.closeEntry();
            this.zipOutputStream.close();
        }
    }

    private FileArchiver getFileArchiver() throws FileNotFoundException {
        if (this.fileArchiver == null) {
            ZipOutputStream zipOutputStream = this.getZipOutputStream();
            this.fileArchiver = new FileArchiver(zipOutputStream);
        }
        return this.fileArchiver;
    }

    private ZipOutputStream getZipOutputStream() throws FileNotFoundException {
        if (this.zipOutputStream == null) {
            this.zipOutputStream = new ZipOutputStream(new FileOutputStream(this.archiveFile));
        }
        return this.zipOutputStream;
    }
}

