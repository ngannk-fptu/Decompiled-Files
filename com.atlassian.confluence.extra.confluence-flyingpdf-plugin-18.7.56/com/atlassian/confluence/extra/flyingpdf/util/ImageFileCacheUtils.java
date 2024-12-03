/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.FileUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.flyingpdf.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageFileCacheUtils {
    private static final Logger log = LoggerFactory.getLogger(ImageFileCacheUtils.class);
    private static final String TEMP_EXPORT_FOLDER_PREFIX = "pdf-export";
    private static final ThreadLocal<File> threadLocal = new ThreadLocal();

    public static void initializeConfluenceTempExportDirectory(Path tempDir) {
        File tempExportDirectory = ImageFileCacheUtils.createTempDirectoryInConfluenceTemp(TEMP_EXPORT_FOLDER_PREFIX, Objects.requireNonNull(tempDir).toFile());
        if (!tempExportDirectory.mkdir()) {
            log.error("Unable to create temp folder {}", (Object)tempExportDirectory);
        } else {
            log.debug("Temp directory {} for pdf export was created", (Object)tempExportDirectory.getAbsolutePath());
        }
        threadLocal.set(tempExportDirectory.getAbsoluteFile());
    }

    public static void initializeSandboxTempExportDirectory() {
        File tempExportDirectory = ImageFileCacheUtils.createTempDirectoryInCurrentFolder(TEMP_EXPORT_FOLDER_PREFIX);
        if (!tempExportDirectory.mkdir()) {
            log.error("Unable to create temp folder {}", (Object)tempExportDirectory);
        } else {
            log.debug("Temp directory {} for pdf export was created", (Object)tempExportDirectory.getAbsolutePath());
        }
        threadLocal.set(tempExportDirectory.getAbsoluteFile());
    }

    public static void removeTempDirectory() {
        try {
            if (threadLocal.get() != null) {
                FileUtils.deleteDirectory((File)threadLocal.get());
            }
        }
        catch (IOException e) {
            log.warn("Cannot remove temporary directory: " + threadLocal.get());
        }
        finally {
            threadLocal.remove();
        }
    }

    public static File createTempFile(InputStream inputStream) throws IOException {
        File tempFile = ImageFileCacheUtils.createTempFile(threadLocal.get());
        FileUtils.copyInputStreamToFile((InputStream)inputStream, (File)tempFile);
        return tempFile;
    }

    public static File createTempFile(byte[] bytes) throws IOException {
        File tempFile = ImageFileCacheUtils.createTempFile(threadLocal.get());
        FileUtils.writeByteArrayToFile((File)tempFile, (byte[])bytes);
        return tempFile;
    }

    private static File createTempDirectoryInConfluenceTemp(String prefix, File tempDir) {
        String uniqueRandomFileName = UUID.randomUUID().toString();
        return new File(tempDir, prefix + uniqueRandomFileName);
    }

    private static File createTempDirectoryInCurrentFolder(String prefix) {
        String uniqueRandomFileName = UUID.randomUUID().toString();
        Path currentRelativePath = Paths.get("", new String[0]);
        String currentFolder = currentRelativePath.toAbsolutePath().toString();
        return new File(currentFolder, prefix + uniqueRandomFileName);
    }

    private static File createTempFile(File directory) {
        String uniqueRandomFileName = UUID.randomUUID().toString();
        return new File(directory, uniqueRandomFileName);
    }
}

