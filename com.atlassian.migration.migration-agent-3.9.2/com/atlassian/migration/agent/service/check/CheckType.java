/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.check;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Generated;

public class CheckType {
    public static final CheckType INVALID_EMAILS = new CheckType("InvalidEmails", true);
    public static final CheckType SHARED_EMAILS = new CheckType("SharedEmails", true);
    public static final CheckType SPACES_INVALID_EMAILS = new CheckType("SpacesInvalidEmails");
    public static final CheckType SPACES_SHARED_EMAILS = new CheckType("SpacesSharedEmails");
    public static final CheckType APP_OUTDATED = new CheckType("AppOutdated", true);
    public static final CheckType SPACE_KEYS_CONFLICT = new CheckType("SpaceKeysConflict", true);
    public static final CheckType GROUP_NAMES_CONFLICT = new CheckType("GroupNamesConflict");
    public static final CheckType CLOUD_FREE_USERS_CONFLICT = new CheckType("CloudFreeUsersConflict", true);
    public static final CheckType SPACE_ANONYMOUS_PERMISSIONS = new CheckType("SpaceAnonymousPermissions", true);
    public static final CheckType APP_ASSESSMENT_COMPLETE = new CheckType("AppAssessmentComplete", true);
    public static final CheckType APP_DATA_MIGRATION_CONSENT = new CheckType("AppDataMigrationConsent", true);
    public static final CheckType SERVER_APPS_OUTDATED = new CheckType("ServerAppsOutdated", true);
    public static final CheckType APPS_NOT_INSTALLED_ON_CLOUD = new CheckType("AppsNotInstalledOnCloud", true);
    public static final CheckType MISSING_ATTACHMENTS = new CheckType("MissingAttachments", true);
    public static final CheckType APP_RELIABILITY = new CheckType("AppsReliabilityCheck");
    public static final CheckType CONFLUENCE_SUPPORTED_VERSION = new CheckType("ConfluenceSupportedVersionCheck");
    public static final CheckType TRUSTED_DOMAINS = new CheckType("TrustedDomains", true);
    public static final CheckType APP_VENDOR_CHECK = new CheckType("AppVendorCheck");
    public static final CheckType APP_WEBHOOK_ENDPOINT_CHECK = new CheckType("AppWebhookEndpoint");
    public static final CheckType APP_LICENSE_CHECK = new CheckType("AppLicenseCheck", false);
    public static final CheckType CONTAINER_TOKEN_EXPIRATION = new CheckType("ContainerTokenExpiration", true);
    public static final CheckType UNKNOWN_CHECK_TYPE = new CheckType("UnknownCheck");
    public static final CheckType NETWORK_HEALTH = new CheckType("NetworkHealthCheck", false);
    public static final CheckType GLOBAL_DATA_TEMPLATE = new CheckType("GlobalDataTemplateConflict", true);
    public static final CheckType MIGRATION_ORCHESTRATOR_MAINTENANCE = new CheckType("MigrationOrchestratorMaintenance", true);
    public static final CheckType CLOUD_PREMIUM_EDITION = new CheckType("CloudPremiumEdition", false);
    public static final CheckType TEAM_CALENDARS_APP_VERSION = new CheckType("TeamCalendarAppVersion", true);
    private static Set<CheckType> staticCheckTypes = new HashSet<CheckType>();
    private final String value;
    private boolean blocksMigration = false;
    public static final List<CheckType> checkTypesForCSV;

    private CheckType(String value) {
        this.value = value;
    }

    private CheckType(String value, boolean blocksMigration) {
        this.value = value;
        this.blocksMigration = blocksMigration;
    }

    public static Set<CheckType> getStaticCheckTypes() {
        return staticCheckTypes;
    }

    public String value() {
        return this.value;
    }

    public static CheckType fromString(String value) {
        return staticCheckTypes.stream().filter(it -> it.value().equals(value)).findAny().orElse(UNKNOWN_CHECK_TYPE);
    }

    public boolean blocksMigration() {
        return this.blocksMigration;
    }

    public static List<String> checkTypeValuesNotFrom(List<String> types) {
        return staticCheckTypes.stream().filter(it -> !types.contains(it.value)).map(it -> it.value).collect(Collectors.toList());
    }

    @Generated
    public String toString() {
        return "CheckType(value=" + this.value + ", blocksMigration=" + this.blocksMigration + ")";
    }

    static {
        staticCheckTypes.addAll(Arrays.asList(INVALID_EMAILS, SHARED_EMAILS, APP_OUTDATED, SPACE_KEYS_CONFLICT, GROUP_NAMES_CONFLICT, CLOUD_FREE_USERS_CONFLICT, SPACE_ANONYMOUS_PERMISSIONS, APP_ASSESSMENT_COMPLETE, APP_DATA_MIGRATION_CONSENT, SERVER_APPS_OUTDATED, APPS_NOT_INSTALLED_ON_CLOUD, MISSING_ATTACHMENTS, APP_RELIABILITY, CONFLUENCE_SUPPORTED_VERSION, TRUSTED_DOMAINS, APP_VENDOR_CHECK, CONTAINER_TOKEN_EXPIRATION, APP_LICENSE_CHECK, APP_WEBHOOK_ENDPOINT_CHECK, UNKNOWN_CHECK_TYPE, NETWORK_HEALTH, GLOBAL_DATA_TEMPLATE, MIGRATION_ORCHESTRATOR_MAINTENANCE, CLOUD_PREMIUM_EDITION, TEAM_CALENDARS_APP_VERSION));
        checkTypesForCSV = ImmutableList.of((Object)SPACE_ANONYMOUS_PERMISSIONS, (Object)MISSING_ATTACHMENTS, (Object)GLOBAL_DATA_TEMPLATE);
    }
}

