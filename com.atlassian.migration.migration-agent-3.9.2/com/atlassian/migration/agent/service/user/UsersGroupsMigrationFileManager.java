/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.BootstrapManager
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.user;

import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.service.execution.UncheckedInterruptedException;
import com.atlassian.migration.agent.service.user.UsersGroupsMigrationFileException;
import com.atlassian.migration.agent.service.user.request.v2.UsersMigrationV2FilePayload;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
public class UsersGroupsMigrationFileManager {
    private static final Logger log = ContextLoggerFactory.getLogger(UsersGroupsMigrationFileManager.class);
    private static final String USERS_V2_MIGRATION_FILE_SUFFIX = "-users-v2-migration";
    private static final String MIGRATION_PATH = "migration";
    private static final String USERS_GROUPS_MIGRATION_PATH = "users-groups-migration";
    private final BootstrapManager bootstrapManager;

    public UsersGroupsMigrationFileManager(BootstrapManager bootstrapManager) {
        this.bootstrapManager = bootstrapManager;
    }

    public void saveUsersMigrationPayloadToFile(String fileId, UsersMigrationV2FilePayload usersMigrationV2FilePayload) {
        log.info("Saving user migration request payload to file for fileId {}", (Object)fileId);
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(this.getUsersMigrationFile(fileId), new OpenOption[0]);){
            bufferedWriter.write(Jsons.valueAsString(usersMigrationV2FilePayload));
        }
        catch (UncheckedInterruptedException e) {
            throw e;
        }
        catch (Exception e) {
            throw new UsersGroupsMigrationFileException("Can't save users migration payload to file", e);
        }
    }

    public void cleanupUsersMigrationPayloadFile(String fileId) {
        try {
            Files.delete(this.getUsersMigrationFile(fileId));
        }
        catch (IOException e) {
            log.error("Failed to clean up user migration payload file. Reason: " + e.getMessage(), (Throwable)e);
        }
    }

    public Path getUsersMigrationFile(String file) {
        return this.getUsersMigrationPath().resolve(file + USERS_V2_MIGRATION_FILE_SUFFIX);
    }

    private Path getUsersMigrationPath() {
        Path path = Paths.get(this.bootstrapManager.getSharedHome().getAbsolutePath(), MIGRATION_PATH, USERS_GROUPS_MIGRATION_PATH);
        if (!path.toFile().exists()) {
            try {
                return Files.createDirectories(path, new FileAttribute[0]);
            }
            catch (IOException e) {
                throw new UsersGroupsMigrationFileException("Unable to create users groups migration directory", e);
            }
        }
        return path;
    }
}

