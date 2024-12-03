/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.core.util.zip;

import com.atlassian.core.util.zip.FilePathUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class FileArchiver {
    private final ZipOutputStream zipOutputStream;

    public FileArchiver(ZipOutputStream zipOutputStream) {
        if (zipOutputStream == null) {
            throw new NullPointerException("output stream can't be null");
        }
        this.zipOutputStream = zipOutputStream;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addToArchive(File file, String path, String archiveFolderToCreate) throws IOException {
        if (file == null || !file.exists()) {
            return;
        }
        String escapedPath = FileArchiver.getEntryPath(FilePathUtils.stripSlashes(path), archiveFolderToCreate);
        ZipEntry entry = new ZipEntry(escapedPath);
        entry.setTime(file.lastModified());
        this.zipOutputStream.putNextEntry(entry);
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
        try {
            IOUtils.copyLarge((InputStream)in, (OutputStream)this.zipOutputStream);
        }
        finally {
            IOUtils.closeQuietly((InputStream)in);
        }
    }

    public void addDirectoryToArchive(File file, String path, String archiveFolderName) throws IOException {
        if (file == null || !file.exists() || !file.isDirectory()) {
            return;
        }
        String escapedPath = FileArchiver.getEntryPath(FilePathUtils.stripSlashes(path), archiveFolderName);
        String directoryEscapedPath = escapedPath.endsWith("/") ? escapedPath : escapedPath + "/";
        ZipEntry entry = new ZipEntry(directoryEscapedPath);
        entry.setTime(file.lastModified());
        this.zipOutputStream.putNextEntry(entry);
    }

    private static String getEntryPath(String path, String archiveFolderToCreate) {
        String sanitisedPath = path;
        if ((sanitisedPath = sanitisedPath.replaceAll("\\\\", "/")).length() > 0 && sanitisedPath.charAt(0) == '/') {
            sanitisedPath = sanitisedPath.substring(1);
        }
        if (StringUtils.isNotBlank((CharSequence)archiveFolderToCreate)) {
            sanitisedPath = archiveFolderToCreate + "/" + path;
        }
        return sanitisedPath;
    }
}

