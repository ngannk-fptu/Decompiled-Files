/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.confluence.setup.BootstrapManager
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.lingala.zip4j.ZipFile
 *  net.lingala.zip4j.exception.ZipException
 *  org.apache.commons.io.FileUtils
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.log;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.service.ClusterInformationService;
import com.atlassian.migration.agent.service.event.UploadMigLogsToMCSEvent;
import com.atlassian.migration.agent.service.log.MigrationLogException;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.ParametersAreNonnullByDefault;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
public class MigrationLogDirManager {
    public static final String MIGRATION_ERROR_LOG_SUFFIX = "-migration-error-logs";
    public static final String MIGRATION_LOG_SUFFIX = "atlassian-confluence-migrations";
    private static final Logger log = ContextLoggerFactory.getLogger(MigrationLogDirManager.class);
    private static final String MIGRATION_PATH = "migration";
    private static final String LOG_PATH = "log";
    private static final String LOG_EXTENSION = ".log";
    private static final String ZIP_EXTENSION = ".zip";
    private static final String MIG_LOG_FILE = "atlassian-confluence-migrations.log";
    private static final String LOGS_PATH = "logs";
    private static final String MIGRATION_ERROR_LOG_ZIP = "-migration-error-logs.zip";
    private static final String MIGRATION_ERROR_LOG_FILE = "-migration-error-logs.log";
    private static final String MIGRATION_LOG_ZIP = "-atlassian-confluence-migrations.zip";
    private final BootstrapManager bootstrapManager;
    private final EventPublisher eventPublisher;
    private final ClusterInformationService clusterInformationService;
    private final ApplicationConfiguration applicationConfiguration;

    public MigrationLogDirManager(BootstrapManager bootstrapManager, EventPublisher eventPublisher, ClusterInformationService clusterInformationService, ApplicationConfiguration applicationConfiguration) {
        this.bootstrapManager = bootstrapManager;
        this.eventPublisher = eventPublisher;
        this.clusterInformationService = clusterInformationService;
        this.applicationConfiguration = applicationConfiguration;
    }

    public void saveErrorLogsToFile(String migrationId, String message) {
        log.info("Saving migration error log message for migrationId: {} to file", (Object)migrationId);
        try {
            FileUtils.writeStringToFile((File)this.getMigrationErrorLogFile(migrationId).toFile(), (String)message, (Charset)StandardCharsets.UTF_8, (boolean)true);
        }
        catch (IOException e) {
            throw new MigrationLogException("Can't save migration error logs to file for migrationId: " + migrationId, e);
        }
    }

    public void zipMigrationErrorLogFile(String migrationId) {
        log.info("Zipping migration error log file for migrationId: {}", (Object)migrationId);
        Path migrationLogFile = this.getMigrationErrorLogFile(migrationId);
        Path zipfilePath = this.getMigrationErrorLogZipFile(migrationId);
        ZipFile zip = new ZipFile(zipfilePath.toFile());
        try {
            zip.addFile(migrationLogFile.toFile());
        }
        catch (ZipException e) {
            throw new MigrationLogException("Can't zip migration error log file for migrationId: " + migrationId, e);
        }
        finally {
            this.cleanupMigrationErrorLogFile(migrationId);
        }
    }

    public Path getMigrationErrorLogZipFile(String migrationId) {
        return this.getSharedMigrationLogPath().resolve(migrationId + MIGRATION_ERROR_LOG_ZIP);
    }

    public void cleanupMigrationErrorLogZipFile(String migrationId) {
        try {
            Files.delete(this.getMigrationErrorLogZipFile(migrationId));
        }
        catch (IOException e) {
            log.error("Failed to clean up migration error log zip file. Reason: " + e.getMessage(), (Throwable)e);
        }
    }

    public Path getMigrationErrorLogFile(String file) {
        return this.getSharedMigrationLogPath().resolve(file + MIGRATION_ERROR_LOG_FILE);
    }

    @VisibleForTesting
    void cleanupMigrationErrorLogFile(String file) {
        try {
            Files.delete(this.getMigrationErrorLogFile(file));
        }
        catch (IOException e) {
            log.error("Failed to clean up migration error log file. Reason: " + e.getMessage(), (Throwable)e);
        }
    }

    @VisibleForTesting
    Path getSharedMigrationLogPath() {
        Path path = Paths.get(this.bootstrapManager.getSharedHome().getAbsolutePath(), MIGRATION_PATH, LOG_PATH);
        if (!path.toFile().exists()) {
            try {
                return Files.createDirectories(path, new FileAttribute[0]);
            }
            catch (IOException e) {
                throw new MigrationLogException("Unable to create migration error logs directory", e);
            }
        }
        return path;
    }

    public void uploadClusteredMigrationLogFiles(String cloudId, String migrationId, String planId) {
        log.info("Publishing UploadMigLogsToMCSEvent for migrationId: {}", (Object)migrationId);
        this.eventPublisher.publish((Object)new UploadMigLogsToMCSEvent(this, cloudId, migrationId, planId));
    }

    public Optional<Path> zipMigrationLogFiles(String migrationId, String planId) {
        try {
            List<Path> migrationLogPaths = this.getMigrationLogsContainingId(migrationId, planId);
            if (migrationLogPaths.isEmpty()) {
                return Optional.empty();
            }
            Path zipfilePath = this.getMigrationLogZipFile(migrationId);
            ZipFile migLogZip = new ZipFile(zipfilePath.toFile());
            for (Path migrationLogPath : migrationLogPaths) {
                migLogZip.addFile(migrationLogPath.toFile());
            }
            return Optional.ofNullable(zipfilePath);
        }
        catch (IOException e) {
            throw new MigrationLogException("Can't zip migration log file for migrationId: " + migrationId, e);
        }
    }

    public Path getMigrationLogZipFile(String migrationId) {
        String thisNodeId = "";
        if (this.clusterInformationService.isClustered()) {
            thisNodeId = "-" + this.clusterInformationService.getCurrentNodeId();
        }
        return this.getSharedMigrationLogPath().resolve(migrationId + thisNodeId + MIGRATION_LOG_ZIP);
    }

    public void cleanupMigrationLogZipFile(String migrationId) {
        try {
            Files.deleteIfExists(this.getMigrationLogZipFile(migrationId));
        }
        catch (IOException e) {
            log.error("Failed to clean up migration log zip file. Reason: " + e.getMessage(), (Throwable)e);
        }
    }

    @VisibleForTesting
    protected Path getApplicationLogPath() {
        return Paths.get(this.applicationConfiguration.getApplicationHome(), LOGS_PATH);
    }

    @VisibleForTesting
    public List<Path> getMigrationLogsContainingId(String migrationId, String planId) throws IOException {
        ArrayList<Path> files = new ArrayList<Path>();
        this.addRelatedLogFilesToList(MIG_LOG_FILE, files, migrationId, planId);
        for (int fileIndex = 1; fileIndex <= 9; ++fileIndex) {
            this.addRelatedLogFilesToList("atlassian-confluence-migrations.log." + fileIndex, files, migrationId, planId);
        }
        return files;
    }

    private void addRelatedLogFilesToList(String migLogFile, List<Path> files, String migrationId, String planId) throws IOException {
        Path logFile = this.getApplicationLogPath().resolve(migLogFile);
        if (Files.exists(logFile, new LinkOption[0]) && this.containsPlanIdOrMigId(logFile, migrationId, planId)) {
            files.add(logFile);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private boolean containsPlanIdOrMigId(Path filePath, String migrationId, String planId) throws IOException {
        try (Stream<String> lines = Files.lines(filePath);){
            boolean bl = lines.anyMatch(line -> line.contains(planId) || line.contains(migrationId));
            return bl;
        }
        catch (UncheckedIOException e) {
            log.error("Failed to check migration log file. Reason: " + e.getMessage(), (Throwable)e);
            throw e.getCause();
        }
    }
}

