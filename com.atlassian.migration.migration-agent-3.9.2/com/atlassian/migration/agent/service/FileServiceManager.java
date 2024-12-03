/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.BootstrapManager
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service;

import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.service.FileServiceManagerException;
import com.atlassian.migration.agent.service.execution.UncheckedInterruptedException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileServiceManager {
    private static final Logger log = LoggerFactory.getLogger(FileServiceManager.class);
    private final BootstrapManager bootstrapManager;

    public Path saveToFileInSharedHome(String path, String fileName, Object content) {
        log.info("Saving file: {} at path: {} in shared home.", (Object)path, (Object)fileName);
        Path directory = this.createDirectoriesInShareHome(path);
        Path file = directory.resolve(fileName).normalize();
        if (!file.startsWith(directory)) {
            throw new FileServiceManagerException(String.format("Path %s is outside of root %s", file, directory));
        }
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(file, new OpenOption[0]);){
            bufferedWriter.write(Jsons.valueAsString(content));
        }
        catch (UncheckedInterruptedException e) {
            log.error("Error when saving file: {} in shared home", (Object)fileName);
            throw e;
        }
        catch (Exception e) {
            throw new FileServiceManagerException(String.format("Can't save file: %s in shared home.", fileName), e);
        }
        return file;
    }

    private Path createDirectoriesInShareHome(String path) {
        Path basePath = Paths.get(this.bootstrapManager.getSharedHome().getAbsolutePath(), new String[0]).normalize();
        Path filePath = basePath.resolve(path).normalize();
        if (!filePath.startsWith(basePath)) {
            throw new FileServiceManagerException(String.format("Path %s is outside of root %s", path, basePath));
        }
        if (!filePath.toFile().exists()) {
            try {
                return Files.createDirectories(filePath, new FileAttribute[0]);
            }
            catch (IOException e) {
                throw new FileServiceManagerException(String.format("Unable to create directories: %s in shared home", path), e);
            }
        }
        return filePath;
    }

    public void cleanUp(Path path) {
        try {
            Files.delete(path);
        }
        catch (IOException e) {
            log.error("An error occurred while deleting the file: {}", (Object)path);
        }
    }

    @Generated
    public FileServiceManager(BootstrapManager bootstrapManager) {
        this.bootstrapManager = bootstrapManager;
    }
}

