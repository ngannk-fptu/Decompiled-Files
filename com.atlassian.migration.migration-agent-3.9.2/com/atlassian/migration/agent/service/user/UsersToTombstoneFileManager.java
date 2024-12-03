/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.validation.IdentityAcceptedEmailValidator
 *  com.atlassian.confluence.setup.BootstrapManager
 *  com.google.common.reflect.TypeToken
 *  com.google.gson.Gson
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.user;

import com.atlassian.cmpt.validation.IdentityAcceptedEmailValidator;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.service.execution.UncheckedInterruptedException;
import com.atlassian.migration.agent.service.impl.MigrationUser;
import com.atlassian.migration.agent.service.user.UsersToTombstoneFileException;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
public class UsersToTombstoneFileManager {
    private static final Logger log = ContextLoggerFactory.getLogger(UsersToTombstoneFileManager.class);
    private static final String JSON_EXTENSION = ".json";
    private static final String MIGRATION_PATH = "migration";
    private static final String USERS_TO_TOMBSTONE_PATH = "users-to-tombstone";
    private final Gson gson = new Gson();
    private final BootstrapManager bootstrapManager;

    public UsersToTombstoneFileManager(BootstrapManager bootstrapManager) {
        this.bootstrapManager = bootstrapManager;
    }

    public void saveToFile(String planId, Collection<MigrationUser> users) {
        log.info("Saving users to tombstone to file for planId {} with size {}", (Object)planId, (Object)users.size());
        try (FileOutputStream fos = new FileOutputStream(this.getUsersToTombstoneFile(planId).toFile());
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter((OutputStream)fos, StandardCharsets.UTF_8);){
            this.gson.toJson(users, (Appendable)outputStreamWriter);
        }
        catch (UncheckedInterruptedException e) {
            throw e;
        }
        catch (Exception e) {
            throw new UsersToTombstoneFileException("Can't save users to tombstone to file", e);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public List<MigrationUser> getUsersToTombstoneFromFile(String planId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(this.getUsersToTombstoneFile(planId).toFile()));){
            List<MigrationUser> list = ((List)this.gson.fromJson((Reader)reader, new TypeToken<List<MigrationUser>>(){}.getType())).stream().map(u -> new MigrationUser(u.getUserKey(), u.getUsername(), u.getFullName(), IdentityAcceptedEmailValidator.cleanse((String)u.getEmail()), u.isActive())).collect(Collectors.toList());
            return list;
        }
        catch (Exception e) {
            log.error("Failed to read users to tombstone for planId: " + planId, (Throwable)e);
            return Collections.emptyList();
        }
    }

    public void cleanupUsersToTombstoneFile(String planId) {
        try {
            Files.deleteIfExists(this.getUsersToTombstoneFile(planId));
        }
        catch (IOException e) {
            log.error("Failed to clean up users to tombstone file. Reason: " + e.getMessage(), (Throwable)e);
        }
    }

    public Path getUsersToTombstoneFile(String planId) {
        return this.getUsersToTombstonePath().resolve(planId + JSON_EXTENSION);
    }

    private Path getUsersToTombstonePath() {
        Path path = Paths.get(this.bootstrapManager.getSharedHome().getAbsolutePath(), MIGRATION_PATH, USERS_TO_TOMBSTONE_PATH);
        if (!path.toFile().exists()) {
            try {
                return Files.createDirectories(path, new FileAttribute[0]);
            }
            catch (IOException e) {
                throw new UsersToTombstoneFileException("Unable to create users to tombstone directory", e);
            }
        }
        return path;
    }
}

