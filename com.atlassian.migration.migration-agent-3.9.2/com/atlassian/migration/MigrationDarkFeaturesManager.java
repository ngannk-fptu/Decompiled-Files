/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.features.DarkFeatureManager
 */
package com.atlassian.migration;

import com.atlassian.migration.agent.service.featureflag.FeatureFlagClient;
import com.atlassian.sal.api.features.DarkFeatureManager;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class MigrationDarkFeaturesManager {
    public static final String PLUGIN_FLAG_PREFIX = "migration-assistant.";
    public static final String HANDLE_INVALID_DUPLICATE_EMAIL_USERS = "migration-assistant.handle-invalid-duplicate-email-users";
    public static final String APP_MIGRATION_DEV_MODE = "migration-assistant.app-migration.dev-mode";
    public static final String ENABLE_EXPORT_CACHING = "migration-assistant.enable.export-caching";
    public static final String DISABLE_MISSING_ATTACHMENTS_CHECK = "migration-assistant.disable.missing-attachments-check";
    public static final String DISABLE_NETWORK_HEALTH_PREFLIGHT_CHECK = "migration-assistant.disable.network-health-check";
    public static final String DISABLE_APP_OUTDATED_CHECK = "migration-assistant.disable.app-outdated-check";
    public static final String ENABLE_SPACE_FILTERS = "migration-assistant.enable.space-filters";
    public static final String DISABLE_TASK_LIST_FEATURE = "migration-assistant.disable.task-list.feature";
    public static final String ENABLE_EXPORT_ONLY = "migration-assistant.enable.export-only";
    public static final String ENABLE_UPLOAD_ONLY = "migration-assistant.enable.upload-only";
    public static final String SKIP_ATTACHMENTS_UPLOAD = "migration-assistant.skip.attachments.upload";
    public static final String DISABLED_PLAN_EDIT_FEATURES = "migration-assistant.plan.edit.features.disabled";
    public static final String DISABLE_APP_VENDOR_CHECK = "migration-assistant.disable.app-vendor-check";
    public static final String ENABLE_APP_VENDOR_CHECK_FILTER = "enable.app-vendor-check-filter";
    public static final String ENABLE_BROWSER_METRICS = "migration-assistant.enable.assess-l1-cloud-tooling.feature";
    public static final String APP_MIGRATION_DATA_UPLOAD_CHUNK_MB_SIZE = "migration-assistant.app.upload.chunk.mb-size";
    public static final String ACTIVATE_TURBO_CHUNK_SIZE = "migration-assistant.app.upload.chunk.turbo-size";
    public static final String DISABLE_CHECKS_BEFORE_RUN = "migration-assistant.disable.checks-before-run";
    public static final String ENABLE_SPACE_USERS_MIGRATIONS_STEP = "migration-assistant.enable.space-users-migration-step";
    public static final String DISABLE_LICENCE_CHECK = "migration-assistant.disable.licence-check";
    public static final String DISABLE_NEW_SPACE_SELECTOR = "migration-assistant.disable.new-space-selector-feature";
    public static final String ENABLE_UNLIMITED_SPACE_IMPORT_CONCURRENCY = "migration-assistant.enable.unlimited-space-import-concurrency";
    public static final String ENABLE_FED_RAMP = "migration-assistant.enable.fedRAMP";
    public static final String ENABLE_GLOBAL_ENTITIES = "migration-assistant.enable.global-entities";
    public static final String ENABLE_FORCE_RESET_FLAG = "migration-assistant.enable.force-reset-flag";
    public static final String ENABLE_TOKEN_ENCRYPTION = "migration-assistant.enable.token.encryption";
    public static final String DISABLE_TEAM_CALENDARS_MIGRATION = "migration-assistant.disable.team-calendars-migration";
    public static final String ENABLE_RELATIONS_ANALYSIS = "migration-assistant.enable.relations.analysis";
    public static final String ENABLE_RELATIONS_ANALYSIS_USER_GROUP_KEYS = "migration-assistant.enable.relations.analysis.user-group-keys";
    public static final String DISABLE_RELATIONS_ANALYSIS_USER_GROUP_KEYS_HASHING = "migration-assistant.disable.relations.analysis.user-group-keys.hashing";
    public static final String ENABLE_FORGE_MIGRATION_PATH = "migration-assistant.enable.forge.migration.path";
    public static final String ENABLE_PARALLEL_APP_DATA_UPLOADS = "migration-assistant.enable.parallel.app.data.uploads";
    public static final String ENABLE_CLOUD_FIRST_MIGRATION = "migration-assistant.enable.cloud.first.migration";
    public static final String DISABLE_SCOPED_GROUPS_MIGRATION = "migration-assistant.macquarie.viper.disable.scoped.groups.migration";
    private boolean batchingEnabled;
    private boolean spaceUsersMigrationEnabled;
    public static final String MIGRATION_ASSISTANT_HANDLE_INVALID_DUPLICATE_EMAIL_USERS = "migration-assistant.handle-invalid-duplicate-email-users";
    public static final String MIGRATION_ASSISTANT_DISABLE_GLOBAL_EMAIL_FIXES_FEATURE = "migration-assistant.disable.global.email.fixes.feature";
    public static final String MIGRATION_ASSISTANT_DISABLE_GLOBAL_EMAIL_FIXES_SEND_TOMBSTONES_FEATURE = "migration-assistant.disable.global.email.fixes.send-tombstones.feature";
    public static final String MIGRATION_ASSISTANT_ENABLE_NEW_EMAILS_FROM_DB = "migration-assistant.global.email.fixes.new-email-from-db.feature";
    public static final String MIGRATION_ASSISTANT_DISABLE_UMS_CHECK_FOR_TRUSTED_DOMAINS = "migration-assistant.disable.ums.blocked.domain.preflight.check";
    public static final String ENABLE_ASSESS_INSTANCE_DATA = "migration-assistant.enable.assess-tooling-cloud.feature";
    private final DarkFeatureManager darkFeatureManager;
    private final FeatureFlagClient featureFlagClient;

    public MigrationDarkFeaturesManager(DarkFeatureManager darkFeatureManager, FeatureFlagClient featureFlagClient) {
        this.darkFeatureManager = darkFeatureManager;
        this.featureFlagClient = featureFlagClient;
    }

    private boolean isFeatureEnabled(String key) {
        return this.darkFeatureManager.isFeatureEnabledForAllUsers(key);
    }

    public Set<String> getAllEnabledFeatures() {
        HashSet<String> enabledOnFeatureFlagService = new HashSet<String>(this.featureFlagClient.getAllEnabledFeatureFlags());
        Set enabledOnServer = this.darkFeatureManager.getFeaturesEnabledForAllUsers().getFeatureKeys().stream().filter(featureKey -> featureKey.startsWith(PLUGIN_FLAG_PREFIX)).collect(Collectors.toSet());
        HashSet<String> result = new HashSet<String>();
        result.addAll(enabledOnFeatureFlagService);
        result.addAll(enabledOnServer);
        return result;
    }

    public boolean appMigrationDevMode() {
        return this.isFeatureEnabled(APP_MIGRATION_DEV_MODE);
    }

    public boolean isInstanceAssessmentEnabled() {
        return this.isFeatureEnabled(ENABLE_ASSESS_INSTANCE_DATA);
    }

    public boolean isBrowserMetricsEnabled() {
        return this.isFeatureEnabled(ENABLE_BROWSER_METRICS);
    }

    public boolean exportCachingEnabled() {
        return this.isFeatureEnabled(ENABLE_EXPORT_CACHING);
    }

    public boolean missingAttachmentsCheckDisabled() {
        return this.isFeatureEnabled(DISABLE_MISSING_ATTACHMENTS_CHECK);
    }

    public boolean networkHealthCheckDisabled() {
        return this.isFeatureEnabled(DISABLE_NETWORK_HEALTH_PREFLIGHT_CHECK);
    }

    public boolean appOutdatedCheckDisabled() {
        return this.isFeatureEnabled(DISABLE_APP_OUTDATED_CHECK);
    }

    public boolean spaceFiltersEnabled() {
        return this.isFeatureEnabled(ENABLE_SPACE_FILTERS);
    }

    public boolean isPlanEditFeaturesDisabled() {
        return this.isFeatureEnabled(DISABLED_PLAN_EDIT_FEATURES);
    }

    public boolean skipAttachmentUploadEnabled() {
        return this.isFeatureEnabled(SKIP_ATTACHMENTS_UPLOAD);
    }

    public boolean shouldHandleInvalidAndDuplicateEmailUsers() {
        return this.isFeatureEnabled("migration-assistant.handle-invalid-duplicate-email-users");
    }

    public boolean shouldHandleGlobalEmailFixes() {
        return this.isGlobalEmailFixesFeatureEnabled();
    }

    boolean isGlobalEmailFixesFeatureEnabled() {
        return !this.isFeatureEnabled(MIGRATION_ASSISTANT_DISABLE_GLOBAL_EMAIL_FIXES_FEATURE);
    }

    public boolean isGlobalEmailFixesSendTombstonesFeatureDisabled() {
        return this.isFeatureEnabled(MIGRATION_ASSISTANT_DISABLE_GLOBAL_EMAIL_FIXES_SEND_TOMBSTONES_FEATURE) || this.featureFlagClient.isFeatureEnabled(MIGRATION_ASSISTANT_DISABLE_GLOBAL_EMAIL_FIXES_SEND_TOMBSTONES_FEATURE);
    }

    public boolean isGlobalEmailFixesNewEmailsFromDbEnabled() {
        return this.isFeatureEnabled(MIGRATION_ASSISTANT_ENABLE_NEW_EMAILS_FROM_DB);
    }

    public boolean isUmsCheckForTrustedDomainsDisabled() {
        return this.isFeatureEnabled(MIGRATION_ASSISTANT_DISABLE_UMS_CHECK_FOR_TRUSTED_DOMAINS);
    }

    public boolean isExportOnlyEnabled() {
        return this.isFeatureEnabled(ENABLE_EXPORT_ONLY);
    }

    public boolean isTasklistFeatureDisabled() {
        return this.isFeatureEnabled(DISABLE_TASK_LIST_FEATURE);
    }

    public boolean appVendorCheckDisabled() {
        return this.isFeatureEnabled(DISABLE_APP_VENDOR_CHECK);
    }

    public boolean appVendorChecksFilterEnabled() {
        return this.isFeatureEnabled(ENABLE_APP_VENDOR_CHECK_FILTER);
    }

    public boolean isUploadOnlyEnabled() {
        return this.isFeatureEnabled(ENABLE_UPLOAD_ONLY);
    }

    public boolean isPreflightChecksDisabledBeforeRun() {
        return this.isFeatureEnabled(DISABLE_CHECKS_BEFORE_RUN);
    }

    public boolean isSpaceUsersMigrationStepEnabled() {
        return this.spaceUsersMigrationEnabled;
    }

    public boolean isLicenceCheckDisabled() {
        return this.isFeatureEnabled(DISABLE_LICENCE_CHECK);
    }

    public boolean isNewSpaceSelectorEnabled() {
        return !this.isFeatureEnabled(DISABLE_NEW_SPACE_SELECTOR);
    }

    public boolean isUnlimitedSpaceImportConcurrencyEnabled() {
        return this.batchingEnabled;
    }

    public void refreshFeatureFlags() {
        this.batchingEnabled = this.isFeatureEnabled(ENABLE_UNLIMITED_SPACE_IMPORT_CONCURRENCY) || this.featureFlagClient.isFeatureEnabled(ENABLE_UNLIMITED_SPACE_IMPORT_CONCURRENCY);
        this.spaceUsersMigrationEnabled = this.isFeatureEnabled(ENABLE_SPACE_USERS_MIGRATIONS_STEP) || this.featureFlagClient.isFeatureEnabled(ENABLE_SPACE_USERS_MIGRATIONS_STEP);
    }

    public boolean fedRAMPEnabled() {
        return this.isFeatureEnabled(ENABLE_FED_RAMP);
    }

    public boolean isGlobalEntitiesMigrationEnabled() {
        return this.isFeatureEnabled(ENABLE_GLOBAL_ENTITIES) || this.featureFlagClient.isFeatureEnabled(ENABLE_GLOBAL_ENTITIES);
    }

    public boolean isForceResetFlagEnabled() {
        return this.isFeatureEnabled(ENABLE_FORCE_RESET_FLAG);
    }

    public boolean isTokenEncryptionEnabled() {
        return this.isFeatureEnabled(ENABLE_TOKEN_ENCRYPTION);
    }

    public boolean isTeamCalendarsMigrationDisabled() {
        return this.isFeatureEnabled(DISABLE_TEAM_CALENDARS_MIGRATION) || this.featureFlagClient.isFeatureEnabled(DISABLE_TEAM_CALENDARS_MIGRATION);
    }

    public boolean isRelationsAnalysisEnabled() {
        return this.isFeatureEnabled(ENABLE_RELATIONS_ANALYSIS);
    }

    public boolean isRelationsAnalysisUserGroupKeysEnabled() {
        return this.isFeatureEnabled(ENABLE_RELATIONS_ANALYSIS_USER_GROUP_KEYS);
    }

    public boolean isRelationsAnalysisUserGroupKeysHashingDisabled() {
        return this.isFeatureEnabled(DISABLE_RELATIONS_ANALYSIS_USER_GROUP_KEYS_HASHING);
    }

    public boolean isTurboChunkSizeEnabled() {
        return this.isFeatureEnabled(ACTIVATE_TURBO_CHUNK_SIZE);
    }

    public boolean isParallelAppDataUploadsEnabled() {
        return this.featureFlagClient.isFeatureEnabled(ENABLE_PARALLEL_APP_DATA_UPLOADS);
    }

    public boolean isForgeMigrationPathEnabled() {
        return this.featureFlagClient.isFeatureEnabled(ENABLE_FORGE_MIGRATION_PATH);
    }

    public boolean isCloudFirstMigrationEnabled() {
        return this.isFeatureEnabled(ENABLE_CLOUD_FIRST_MIGRATION) || this.featureFlagClient.isFeatureEnabled(ENABLE_CLOUD_FIRST_MIGRATION);
    }

    public boolean disableScopedGroupMigration() {
        return this.isFeatureEnabled(DISABLE_SCOPED_GROUPS_MIGRATION) || this.featureFlagClient.isFeatureEnabled(DISABLE_SCOPED_GROUPS_MIGRATION);
    }
}

