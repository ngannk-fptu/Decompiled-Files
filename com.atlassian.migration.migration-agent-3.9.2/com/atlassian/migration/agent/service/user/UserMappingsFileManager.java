/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.BootstrapManager
 *  com.google.gson.Gson
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.user;

import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.service.execution.UncheckedInterruptedException;
import com.atlassian.migration.agent.service.user.UserMappingFileException;
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
import java.util.Collections;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
public class UserMappingsFileManager {
    private static final Logger log = ContextLoggerFactory.getLogger(UserMappingsFileManager.class);
    private static final String JSON_EXTENSION = ".json";
    private static final String MIGRATION_PATH = "migration";
    private static final String USER_MAPPINGS_PATH = "user-mappings";
    private final Gson gson = new Gson();
    private final BootstrapManager bootstrapManager;

    public UserMappingsFileManager(BootstrapManager bootstrapManager) {
        this.bootstrapManager = bootstrapManager;
    }

    public void saveToFile(String planId, Map<String, String> mappings) {
        log.info("Saving user mappings to file for planId {} with size {}", (Object)planId, (Object)mappings.size());
        try (FileOutputStream fos = new FileOutputStream(this.getUserMappingsFile(planId).toFile());
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter((OutputStream)fos, StandardCharsets.UTF_8);){
            this.gson.toJson(mappings, (Appendable)outputStreamWriter);
        }
        catch (UncheckedInterruptedException e) {
            throw e;
        }
        catch (Exception e) {
            throw new UserMappingFileException("Can't save user mappings to file", e);
        }
    }

    public Map<String, String> getUserMappingsFromFile(String planId) {
        try {
            return (Map)this.gson.fromJson((Reader)new BufferedReader(new FileReader(this.getUserMappingsFile(planId).toFile())), Map.class);
        }
        catch (Exception e) {
            log.error("Fail to read user mappings for plan: " + planId, (Throwable)e);
            return Collections.emptyMap();
        }
    }

    public void cleanupUserMappingsFile(String fileId) {
        try {
            Files.deleteIfExists(this.getUserMappingsFile(fileId));
        }
        catch (IOException e) {
            log.error("Failed to clean up user mappings file. Reason: " + e.getMessage(), (Throwable)e);
        }
    }

    private Path getUserMappingsFile(String file) {
        return this.getUserMappingsPath().resolve(file + JSON_EXTENSION);
    }

    private Path getUserMappingsPath() {
        Path path = Paths.get(this.bootstrapManager.getSharedHome().getAbsolutePath(), MIGRATION_PATH, USER_MAPPINGS_PATH);
        if (!path.toFile().exists()) {
            try {
                return Files.createDirectories(path, new FileAttribute[0]);
            }
            catch (IOException e) {
                throw new UserMappingFileException("Unable to create user mappings directory", e);
            }
        }
        return path;
    }
}

