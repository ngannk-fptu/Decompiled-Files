/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.BootstrapManager
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.apache.commons.io.FileUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.migration.agent.export.MigrationExportException;
import com.atlassian.migration.agent.service.ExportDirManager;
import com.google.common.annotations.VisibleForTesting;
import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class DefaultExportDirManager
implements ExportDirManager {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(DefaultExportDirManager.class);
    private final BootstrapManager bootstrapManager;
    private static final String MIGRATION_DIRECTORY = "migration";
    private static final String EXPORTS_DIRECTORY = "exports";
    private static final String DOMAINS_DIRECTORY = "domains";

    public DefaultExportDirManager(BootstrapManager bootstrapManager) {
        this.bootstrapManager = bootstrapManager;
    }

    @Override
    public Path getExportFilePath(String fileId) {
        return this.getExportPath().resolve(fileId);
    }

    @Override
    public long getExportSize(String location) {
        if (Files.isDirectory(this.getExportFilePath(location), new LinkOption[0])) {
            return FileUtils.sizeOfDirectory((File)this.getExportFilePath(location).toFile());
        }
        File exportFile = this.getValidExportFile(location);
        return exportFile.length();
    }

    @Override
    public String copyExportedFileToSharedHome(String exportedFile, String destinationFileName) {
        try {
            Path destinationPath = this.getExportFilePath(destinationFileName);
            Files.copy(Paths.get(exportedFile, new String[0]), destinationPath, new CopyOption[0]);
            return destinationPath.toString();
        }
        catch (IOException e) {
            log.error("Error copying exported file: {} to destination {}", new Object[]{exportedFile, destinationFileName, e});
            throw new MigrationExportException("Unable to copy export file to shared home", e);
        }
    }

    @Override
    public void moveCompressedFilesToSharedHome(String csvExportDir, String destinationDirectory) {
        try {
            this.moveDirectory(Paths.get(csvExportDir, new String[0]), this.getExportFilePath(destinationDirectory));
        }
        catch (IOException e) {
            log.error("Unable to move compressed export file, csvExportDir: {}, destinationDirectory: {}, Reason: {}", new Object[]{csvExportDir, destinationDirectory, e.getMessage(), e});
            String userMessage = String.format("Unable to move compressed export file to shared home, Reason: %s. Ensure that the shared home directory %s has the file write permissions for it\u2019s parent directory and any nested directories within it.", e.getMessage(), this.getExportPath());
            throw new MigrationExportException(userMessage, e);
        }
    }

    @VisibleForTesting
    void moveDirectory(Path srcPath, Path destPath) throws IOException {
        log.info("Moving compressed files from csvExportDir: {} to destinationDirectory: {}", (Object)srcPath, (Object)destPath);
        if (!srcPath.toFile().renameTo(destPath.toFile())) {
            FileUtils.copyDirectory((File)srcPath.toFile(), (File)destPath.toFile(), (boolean)false);
            this.cleanupExportFile(srcPath);
        }
    }

    @VisibleForTesting
    File validateExportFile(File exportFile) {
        if (!exportFile.isFile()) {
            throw new IllegalArgumentException("Export file is not a file.");
        }
        if (!exportFile.canRead()) {
            throw new IllegalArgumentException("Export file cannot be read.");
        }
        if (exportFile.length() <= 0L) {
            throw new IllegalArgumentException("Export file has no content.");
        }
        return exportFile;
    }

    @Override
    public void cleanupExportFile(Path path) {
        try {
            if (Files.isDirectory(path, new LinkOption[0])) {
                File filePath = path.toFile();
                FileUtils.deleteDirectory((File)filePath);
            } else {
                Files.delete(path);
            }
        }
        catch (IOException e) {
            log.error("Failed to clean up export file. Reason: " + e.getMessage(), (Throwable)e);
        }
    }

    private File getValidExportFile(String fileId) {
        return this.validateExportFile(this.getExportFilePath(fileId).toFile());
    }

    private Path getExportPath() {
        return Paths.get(this.bootstrapManager.getSharedHome().getAbsolutePath(), MIGRATION_DIRECTORY, EXPORTS_DIRECTORY);
    }

    public Path getMigrationDomainsDirectory() {
        return Paths.get(this.bootstrapManager.getSharedHome().getAbsolutePath(), MIGRATION_DIRECTORY, EXPORTS_DIRECTORY, DOMAINS_DIRECTORY);
    }
}

