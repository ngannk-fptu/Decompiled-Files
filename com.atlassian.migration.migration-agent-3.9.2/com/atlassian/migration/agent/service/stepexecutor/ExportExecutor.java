/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.setup.BootstrapManager
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.annotation.PostConstruct
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.stepexecutor;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.dto.ConfExportStepConfig;
import com.atlassian.migration.agent.entity.Plan;
import com.atlassian.migration.agent.entity.Task;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.service.ExportDirManager;
import com.atlassian.migration.agent.service.MigrationErrorCode;
import com.atlassian.migration.agent.service.ServiceInitializeException;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.execution.StepExecutor;
import com.atlassian.migration.agent.service.stepexecutor.StepResult;
import com.atlassian.migration.agent.store.StepStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.time.Clock;
import java.time.Instant;
import java.util.function.Supplier;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
public abstract class ExportExecutor
implements StepExecutor {
    private static final Logger log = ContextLoggerFactory.getLogger(ExportExecutor.class);
    @VisibleForTesting
    public static final String TEMP_DIR_PROP = "struts.multipart.saveDir";
    @VisibleForTesting
    public static final String LEGACY_TEMP_DIR_PROP = "webwork.multipart.saveDir";
    public static final String CACHE_FILENAME_SUFFIX = "_cache";
    protected final ExportDirManager exportDirManager;
    protected final BootstrapManager bootstrapManager;
    protected final StepStore stepStore;
    protected final PluginTransactionTemplate ptx;
    protected final Supplier<Instant> instantSupplier;
    protected final AnalyticsEventService analyticsEventService;
    protected final AnalyticsEventBuilder analyticsEventBuilder;
    protected final MigrationDarkFeaturesManager migrationDarkFeaturesManager;
    protected final MigrationAgentConfiguration migrationAgentConfiguration;
    protected final Clock clock;
    protected File tempDirFilePath;

    protected ExportExecutor(ExportDirManager exportDirManager, BootstrapManager bootstrapManager, StepStore stepStore, PluginTransactionTemplate ptx, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, MigrationDarkFeaturesManager migrationDarkFeaturesManager, MigrationAgentConfiguration migrationAgentConfiguration) {
        this(exportDirManager, bootstrapManager, stepStore, ptx, Instant::now, analyticsEventService, analyticsEventBuilder, migrationDarkFeaturesManager, migrationAgentConfiguration, Clock.systemUTC());
    }

    @VisibleForTesting
    protected ExportExecutor(ExportDirManager exportDirManager, BootstrapManager bootstrapManager, StepStore stepStore, PluginTransactionTemplate ptx, Supplier<Instant> instantSupplier, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, MigrationDarkFeaturesManager migrationDarkFeaturesManager, MigrationAgentConfiguration migrationAgentConfiguration, Clock clock) {
        this.bootstrapManager = bootstrapManager;
        this.exportDirManager = exportDirManager;
        this.stepStore = stepStore;
        this.ptx = ptx;
        this.instantSupplier = instantSupplier;
        this.analyticsEventService = analyticsEventService;
        this.analyticsEventBuilder = analyticsEventBuilder;
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
        this.migrationAgentConfiguration = migrationAgentConfiguration;
        this.clock = clock;
    }

    @PostConstruct
    public void initialize() {
        this.tempDirFilePath = new File(ExportExecutor.getFilePathProperty(this.bootstrapManager));
        boolean createTempDirResult = this.tempDirFilePath.mkdirs();
        log.info("Created temporary directory for export, call return {}", (Object)createTempDirResult);
        if (!this.tempDirFilePath.exists()) {
            log.error("Failed to configure temporary directory for path {}.", (Object)this.tempDirFilePath);
            throw new ServiceInitializeException("Failed to configure temporary directory for path " + this.tempDirFilePath);
        }
    }

    public static String getFilePathProperty(BootstrapManager bootstrapManager) {
        String filePathProperty = bootstrapManager.getFilePathProperty(TEMP_DIR_PROP);
        if (filePathProperty == null) {
            filePathProperty = bootstrapManager.getFilePathProperty(LEGACY_TEMP_DIR_PROP);
        }
        if (filePathProperty == null) {
            throw new RuntimeException(String.format("Failed to retrieve temporary directory from neither path property %s nor %s.", TEMP_DIR_PROP, LEGACY_TEMP_DIR_PROP));
        }
        return filePathProperty;
    }

    protected StepResult failedStepResult(Exception ex, String migrationId, ConfExportStepConfig exportStepConfig, MigrationErrorCode errorCode) {
        log.error("{} with exception: {} for migrationId: {}", new Object[]{errorCode.getMessage(), ex, migrationId});
        if (errorCode.equals((Object)MigrationErrorCode.SPACE_EXPORT_INTERRUPTED)) {
            return StepResult.failed(String.format("Export of %s was interrupted", exportStepConfig), ex);
        }
        if (errorCode.equals((Object)MigrationErrorCode.MISSING_WRITE_TO_DIRECTORY_PERMISSIONS)) {
            return StepResult.failed(ex.getMessage(), ex);
        }
        return StepResult.failed(String.format("Failed to export %s. Error: %s", exportStepConfig, ex.getMessage()), ex.getCause() != null ? ex.getCause() : ex);
    }

    protected <T extends Task> boolean containsTask(Plan plan, Class<T> type) {
        return plan.getTasks().stream().anyMatch(type::isInstance);
    }

    protected void createExportDirectoryIfNotExists() {
        Path dir = Paths.get(this.bootstrapManager.getSharedHome().getAbsolutePath(), "migration", "exports");
        try {
            Files.createDirectories(dir, new FileAttribute[0]);
        }
        catch (IOException e) {
            log.error("Failed to create export directory: " + dir, (Throwable)e);
            throw new ServiceInitializeException("Failed to create export directory", e);
        }
    }
}

