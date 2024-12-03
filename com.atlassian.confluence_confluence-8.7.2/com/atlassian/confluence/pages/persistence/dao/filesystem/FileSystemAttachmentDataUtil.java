/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.FileUtils
 *  com.google.common.io.CountingInputStream
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.persistence.dao.filesystem;

import com.atlassian.confluence.pages.attachments.AttachmentDataStreamSizeMismatchException;
import com.atlassian.core.util.FileUtils;
import com.google.common.io.CountingInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSystemAttachmentDataUtil {
    private static final Logger log = LoggerFactory.getLogger(FileSystemAttachmentDataUtil.class);
    public static final String TEMP_FILE_PREFIX = "data";

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void writeStreamToFile(InputStream data, File destFile, Long expectedFileSize) {
        File parentFolder = FileSystemAttachmentDataUtil.getParentFolder(destFile);
        File tempFile = FileSystemAttachmentDataUtil.createTempFile(parentFolder);
        long byteCount = FileSystemAttachmentDataUtil.writeToTempFile(data, tempFile);
        try {
            if (expectedFileSize != null && byteCount != expectedFileSize) {
                throw new AttachmentDataStreamSizeMismatchException(expectedFileSize, byteCount);
            }
            FileSystemAttachmentDataUtil.copyTempFileToDestination(destFile, tempFile);
        }
        finally {
            tempFile.delete();
        }
    }

    private static long writeToTempFile(InputStream data, File tempFile) {
        CountingInputStream countingInputStream = new CountingInputStream(data);
        try {
            FileUtils.copyFile((InputStream)countingInputStream, (File)tempFile, (boolean)true);
        }
        catch (IOException e) {
            FileSystemAttachmentDataUtil.failedToWriteTempFile(tempFile, e);
        }
        return countingInputStream.getCount();
    }

    private static File createTempFile(File parentFolder) {
        File tempFile;
        try {
            tempFile = File.createTempFile(TEMP_FILE_PREFIX, null, parentFolder);
        }
        catch (IOException e) {
            throw new RuntimeException("Error creating temp file in folder: " + parentFolder, e);
        }
        return tempFile;
    }

    private static File getParentFolder(File destFile) {
        File parentFolder = destFile.getParentFile();
        if (!parentFolder.exists()) {
            parentFolder.mkdirs();
        }
        return parentFolder;
    }

    private static void failedToWriteTempFile(File tempFile, IOException e) {
        log.error("Error writing '" + tempFile + "' to disk.", (Throwable)e);
        tempFile.delete();
        throw new RuntimeException(e);
    }

    private static void copyTempFileToDestination(File destFile, File tempFile) {
        if (destFile.exists()) {
            destFile.delete();
        }
        if (!tempFile.renameTo(destFile)) {
            try {
                FileUtils.copyFile((File)tempFile, (File)destFile);
            }
            catch (IOException e) {
                throw new RuntimeException("Error copying file: " + tempFile + " to: " + destFile, e);
            }
        }
    }

    public static boolean isEmpty(Path path) throws IOException {
        if (Files.isDirectory(path, new LinkOption[0])) {
            try (DirectoryStream<Path> directory = Files.newDirectoryStream(path);){
                boolean bl = !directory.iterator().hasNext();
                return bl;
            }
        }
        return false;
    }

    public static void cleanupEmptyAncestors(File file, File stopAtFile, boolean suppressLogging) {
        File parent = file.getParentFile();
        try {
            while (FileSystemAttachmentDataUtil.isEmpty(parent.toPath()) && !parent.equals(stopAtFile)) {
                if (!parent.delete()) {
                    if (suppressLogging) {
                        log.debug("Failed to delete empty folder with path '{}'", (Object)parent.getAbsolutePath());
                    } else {
                        log.error("Failed to delete empty folder with path '{}'", (Object)parent.getAbsolutePath());
                    }
                }
                parent = parent.getParentFile();
            }
        }
        catch (IOException e) {
            log.error("Exception while trying to access folder with path '{}'", (Object)parent.getAbsolutePath());
        }
    }

    public static void cleanupEmptyAncestors(File file, File stopAtFile) {
        FileSystemAttachmentDataUtil.cleanupEmptyAncestors(file, stopAtFile, false);
    }
}

