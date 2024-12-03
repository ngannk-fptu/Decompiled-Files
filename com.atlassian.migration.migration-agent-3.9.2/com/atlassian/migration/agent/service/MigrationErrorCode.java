/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service;

import com.atlassian.migration.agent.service.analytics.ErrorContainerType;
import lombok.Generated;

public enum MigrationErrorCode {
    USER_MIGRATION_ERROR(100, "User migration failed", SloEvent.BAD),
    ATTACHMENT_MIGRATION_CANCELLED(101, "Attachments migration cancelled", SloEvent.GOOD),
    ATTACHMENT_MIGRATION_EXECUTION_ERROR(102, "Attachments migration execution error", SloEvent.BAD),
    ATTACHMENT_MIGRATION_INTERRUPTED(103, "Attachments migration interrupted", SloEvent.BAD),
    ATTACHMENT_MIGRATION_PRODUCE_CONSUME_ERROR(104, "Failed to produce or consume attachments", SloEvent.BAD),
    FAILED_TO_READ_ATTACHMENTS(105, "Failed to read attachments", SloEvent.GOOD),
    SPACE_EXPORT_NO_SPACE_KEY(106, "No Space key for space export", SloEvent.BAD),
    SPACE_EXPORT_NO_CLOUD_ID(108, "No cloud ID for space export", SloEvent.BAD),
    SPACE_EXPORT_INTERRUPTED(110, "Space export interrupted", SloEvent.BAD),
    SPACE_EXPORT_EXECUTION_ERROR(111, "Space export execution error", SloEvent.BAD),
    SPACE_EXPORT_FILE_EXPORT_FAILED(117, "Space export file creation failed", SloEvent.BAD),
    SPACE_EXPORT_DIRECTORY_CREATION_FAILED(118, "Space export directory creation failed", SloEvent.BAD),
    SPACE_UPLOAD_FAILED(112, "Space Upload Generic Error", SloEvent.BAD),
    SPACE_IMPORT_FAILED(113, "Failed to import space in cloud", SloEvent.BAD),
    MEDIA_UPLOAD_ERROR(114, "Media upload error", SloEvent.BAD),
    XML_TO_CSV_CONVERSION_ERROR(115, "XML to CSV conversion error", SloEvent.BAD),
    SPACE_EXPORT_SPACE_DOES_NOT_EXIST(116, "A space with the given space key does not exist", SloEvent.BAD),
    SPACE_IMPORT_INITIATE_FAILED(117, "Failed to initiate space import on cloud", SloEvent.BAD),
    SPACE_IMPORT_MISSING_CONFIG(122, "Blank stepConfig for upload file", SloEvent.BAD),
    SPACE_IMPORT_TASK_NOT_CONFLUENCE(123, "Task is not an instance of ConfluenceSpaceTask", SloEvent.BAD),
    SPACE_IMPORT_MISSING_SPACE_KEY(124, "Missing Space key", SloEvent.BAD),
    SPACE_IMPORT_UNFORMATTED_JSON(125, "Unexpected json format for step config", SloEvent.BAD),
    MCS_API_ERROR(126, "CCMA upload data error", SloEvent.BAD),
    MULTIPLE_USERS_MIGRATIONS_STEP_RUNNING(1001, "Illegal state multiple users migrations step running at the same time", SloEvent.GOOD),
    ATTACHMENT_MIGRATION_GENERIC_ERROR(127, "Attachment Migration generic error", SloEvent.BAD),
    ATTACHMENT_MIGRATION_FILE_NOT_READABLE(128, "Failed to read attachments directory", SloEvent.BAD),
    ATTACHMENT_MIGRATION_SERVER_SPACE_NOT_PRESENT(129, "Space is no longer present at the server side", SloEvent.GOOD),
    GLOBAL_ENTITIES_EXPORT_NO_FILE_ID(131, "No file ID for global templates export", SloEvent.BAD),
    GLOBAL_ENTITIES_EXPORT_NO_CLOUD_ID(132, "No cloud ID for global templates export", SloEvent.BAD),
    GLOBAL_ENTITIES_EXPORT_FILE_EXPORT_FAILED(133, "Global entities export file creation failed", SloEvent.BAD),
    GLOBAL_ENTITIES_EXPORT_DIRECTORY_CREATION_FAILED(134, "Global entities export directory creation failed", SloEvent.BAD),
    GLOBAL_ENTITIES_EXPORT_EXECUTION_ERROR(135, "Global entities export execution error", SloEvent.BAD),
    GLOBAL_ENTITIES_UPLOAD_FAILED(136, "Global entities upload generic error", SloEvent.BAD),
    GLOBAL_ENTITIES_IMPORT_FAILED(137, "Failed to import global templates in cloud", SloEvent.BAD),
    GLOBAL_ENTITIES_IMPORT_MISSING_CONFIG(138, "Blank stepConfig for upload file", SloEvent.BAD),
    GLOBAL_ENTITIES_IMPORT_INCORRECT_TASK_TYPE(139, "Task is not an instance of MigrateGlobalEntitiesTask", SloEvent.BAD),
    GLOBAL_ENTITIES_IMPORT_UNFORMATTED_JSON(140, "Unexpected json format for stepConfig", SloEvent.BAD),
    GLOBAL_ENTITIES_IMPORT_INITIATE_FAILED(141, "Failed to initiate global templates import on cloud", SloEvent.BAD),
    SPACE_USER_MIGRATION_ERROR(142, "Error when performing space users migration", SloEvent.BAD),
    CREATE_AND_PUBLISHING_TOMBSTONE_MAPPINGS(143, "Error when creating and publishing tombstone mappings", SloEvent.BAD),
    USERS_MIGRATION_PROGRESS_CHECK_FOR_SPACE_USERS_MIGRATION_ERROR(144, "Error when checking the progress for users migration for space users migration", SloEvent.BAD),
    USERS_MIGRATION_FOR_SPACE_USERS_MIGRATION_ERROR_DURING_CANCELLATION(145, "Error when cancelling users migration for space users migration", SloEvent.BAD),
    SPACE_USERS_MIGRATION_RETRIEVE_MAPPINGS_ERROR(146, "Error when retrieving mappinge for space users migration", SloEvent.BAD),
    UNHANDLED_ERROR(147, "Unhandled error", SloEvent.BAD),
    MISSING_WRITE_TO_DIRECTORY_PERMISSIONS(148, "Error when creating the required directory/file due to missing write permissions", SloEvent.BAD);

    private final int code;
    private final String message;
    private final SloEvent sloEvent;
    private final ErrorContainerType containerType = ErrorContainerType.MIGRATION_ERROR;

    public boolean shouldBeTreatedAsGoodEventInReliabilitySlo() {
        return this.sloEvent == SloEvent.GOOD;
    }

    @Generated
    public int getCode() {
        return this.code;
    }

    @Generated
    public String getMessage() {
        return this.message;
    }

    @Generated
    public SloEvent getSloEvent() {
        return this.sloEvent;
    }

    @Generated
    public ErrorContainerType getContainerType() {
        return this.containerType;
    }

    @Generated
    private MigrationErrorCode(int code, String message, SloEvent sloEvent) {
        this.code = code;
        this.message = message;
        this.sloEvent = sloEvent;
    }

    public static enum SloEvent {
        GOOD,
        BAD;

    }
}

