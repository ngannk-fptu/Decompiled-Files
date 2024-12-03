/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.setup.BootstrapManager
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.codehaus.jackson.type.TypeReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.stepexecutor.space.helper;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.service.catalogue.model.MigrationCatalogueStorageFile;
import com.atlassian.migration.agent.service.execution.UncheckedInterruptedException;
import com.atlassian.migration.agent.service.stepexecutor.space.helper.SpaceImportConfigFileException;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class SpaceImportConfigFileManager {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(SpaceImportConfigFileManager.class);
    @VisibleForTesting
    static final String SPACE_IMPORT_STEP_CONFIG_FILE_SUFFIX = "-space-import-stepConfig";
    private static final String MIGRATION_PATH = "migration";
    private static final String SPACE_IMPORT_CONFIG_PATH = "space-import-config";
    private final BootstrapManager bootstrapManager;

    public SpaceImportConfigFileManager(BootstrapManager bootstrapManager) {
        this.bootstrapManager = bootstrapManager;
    }

    public void saveSpaceImportStepConfigToFile(String migrationId, String spaceId, List<MigrationCatalogueStorageFile> migrationCatalogueStorageFiles) {
        log.info("Saving spaceImport stepConfig to file for migrationId: {} and spaceId: {}", (Object)migrationId, (Object)spaceId);
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(this.getSpaceImportStepConfigFilePath(migrationId, spaceId), new OpenOption[0]);){
            bufferedWriter.write(Jsons.valueAsString(migrationCatalogueStorageFiles));
        }
        catch (UncheckedInterruptedException e) {
            throw e;
        }
        catch (Exception e) {
            throw new SpaceImportConfigFileException("Can't save space import step config to file", e);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public List<MigrationCatalogueStorageFile> getSpaceImportStepConfigFromFile(String migrationId, String spaceId) {
        try (FileInputStream fileInputStream = new FileInputStream(this.getSpaceImportStepConfigFilePath(migrationId, spaceId).toFile());){
            List list = (List)Jsons.readValue((InputStream)fileInputStream, (TypeReference)new TypeReference<List<MigrationCatalogueStorageFile>>(){});
            return list;
        }
        catch (Exception e) {
            throw new SpaceImportConfigFileException("Can't get space import step config to file", e);
        }
    }

    public void cleanupSpaceImportStepConfigFile(String migrationId, String spaceId) {
        try {
            Files.delete(this.getSpaceImportStepConfigFilePath(migrationId, spaceId));
        }
        catch (IOException e) {
            log.error("Failed to clean up space import step config file. Reason: " + e.getMessage(), (Throwable)e);
        }
    }

    public String getSpaceImportStepConfigFileName(String migrationId, String spaceId) {
        return migrationId + "-" + spaceId + SPACE_IMPORT_STEP_CONFIG_FILE_SUFFIX;
    }

    @VisibleForTesting
    Path getSpaceImportStepConfigFilePath(String migrationId, String spaceId) {
        return this.getSpaceImportStepConfigPath().resolve(this.getSpaceImportStepConfigFileName(migrationId, spaceId));
    }

    private Path getSpaceImportStepConfigPath() {
        Path path = Paths.get(this.bootstrapManager.getSharedHome().getAbsolutePath(), MIGRATION_PATH, SPACE_IMPORT_CONFIG_PATH);
        if (!path.toFile().exists()) {
            try {
                return Files.createDirectories(path, new FileAttribute[0]);
            }
            catch (IOException e) {
                throw new SpaceImportConfigFileException("Unable to create space import step config directory", e);
            }
        }
        return path;
    }
}

