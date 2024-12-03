/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service;

import lombok.Generated;

public enum PreflightErrorCode {
    GENERIC_ERROR(100, "An error occurred during execution"),
    SPACE_CONFLICTS_CHECK_ERROR(150, "Error executing space keys conflict check."),
    GROUP_CONFLICT_CHECK_ERROR(151, "An error occurred when starting the group conflict check."),
    CLOUD_FREE_USERS_CONFLICT_CHECK_ERROR(152, "Error executing cloud licence check."),
    APP_INFO_ERROR(157, "Error executing apps not installed on cloud check."),
    INVALID_EMAILS_CHECK_ERROR(158, "Error execution invalid emails check."),
    MIGRATION_SCOPE_CREATION_ERROR(159, "Error creating migration scope id."),
    GET_DOWNLOAD_URL_FOR_INVALID_EMAIL_CHECKS_ERROR(160, "Error when fetching the download url for the result for invalid email checks"),
    DOWNLOADING_INVALID_EMAIL_CHECKS_RESULT(161, "Error when downloading the result file for invalid emails check"),
    UPLOAD_FILE_TO_MCS_ERROR(162, "Error uploading file to MCS for invalid emails checker"),
    INVALID_EMAILS_CHECK_STATUS_ERROR(163, "Error when checking the status for emails check"),
    LICENSE_CHECK_STATUS_ERROR(164, "Error when checking for the license check status"),
    LICENSE_CHECK_ERROR(165, "Error executing the license check"),
    CONTAINER_TOKEN_EXPIRY_CHECK_ERROR(166, "An error occurred when executing the Container Token Preflight Check."),
    APP_LICENSE_CHECK_ERROR(167, "An error occurred when executing the App License Check."),
    APP_WEBHOOK_CHECK_ERROR(168, "An error occurred when executing App Webhook Check."),
    GLOBAL_SYSTEM_TEMPLATE_CHECK_ERROR(169, "An error occurred when executing Global and Edited System templates conflict Check."),
    MIGRATION_ORCHESTRATOR_MAINTENANCE_CHECK_ERROR(170, "An error occurred when executing the Migration Orchestrator Maintenance Preflight Check."),
    CLOUD_EDITION_CHECK_ERROR(171, "An error occurred when executing the Cloud Edition Check."),
    CLOUD_ERROR(200, "Cloud site not found.");

    private final int code;
    private final String message;

    @Generated
    public int getCode() {
        return this.code;
    }

    @Generated
    public String getMessage() {
        return this.message;
    }

    @Generated
    private PreflightErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}

