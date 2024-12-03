/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.core.util.zip;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.core.util.zip.FilePathUtils;
import com.atlassian.core.util.zip.ZipArchiver;
import java.io.File;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;

public class FolderArchiver {
    private final File folderToArchive;
    private final File archiveFile;

    public FolderArchiver(File folderToArchive, File archiveFile) {
        this.folderToArchive = folderToArchive;
        this.archiveFile = archiveFile;
    }

    public void doArchive() throws IOException {
        this.doFolderArchive("");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @VisibleForTesting
    void doFolderArchive(String archiveFolderToCreate) throws IOException {
        try (ZipArchiver zipArchiver = new ZipArchiver(this.archiveFile);){
            String cleanedArchiveFolder = StringUtils.stripToEmpty((String)FilePathUtils.stripSlashes(archiveFolderToCreate));
            zipArchiver.addFolder(this.folderToArchive, cleanedArchiveFolder);
        }
    }
}

