/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.audit.entity.AuditAttribute
 *  com.atlassian.audit.entity.AuditAttribute$Builder
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditType
 *  com.atlassian.audit.entity.ChangedValue
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 */
package com.atlassian.business.insights.core.audit;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.audit.entity.AuditAttribute;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import java.util.Collections;

public final class AuditEventFactory {
    @VisibleForTesting
    static final String I18N_EXPORT_PATH_KEY = "data-pipeline.config.event.new.path";
    @VisibleForTesting
    static final String I18N_JOB_ID_KEY = "data-pipeline.audit.action.full.export.job.id";
    @VisibleForTesting
    static final String I18N_UNAUTHORIZED_EXPORT_REQUEST_KEY = "data-pipeline.audit.action.full.export.unauthorized.attribute.request";
    @VisibleForTesting
    static final String I18N_SCHEDULE_CONFIG_KEY = "data-pipeline.config.event.schedule.configuration";
    @VisibleForTesting
    static final String I18N_SCHEDULE_CONFIG_CREATED_KEY = "data-pipeline.config.event.schedule.configuration.created";
    @VisibleForTesting
    static final String I18N_SCHEDULE_CONFIG_DELETED_KEY = "data-pipeline.config.event.schedule.configuration.deleted";
    private static final String I18N_DELETED_PATH_KEY = "data-pipeline.config.event.custom.export.path.removed";
    private static final String I18N_EXPORT_TRIGGERED_KEY = "data-pipeline.audit.action.full.export.triggered";
    private static final String I18N_AUDIT_CATEGORY_KEY = "data-pipeline.audit.category";
    private static final String I18N_EXPORT_CANCELLED_KEY = "data-pipeline.audit.action.full.export.cancelled";
    private static final String I18N_EXPORT_FAILED_KEY = "data-pipeline.audit.action.full.export.failed";
    private static final String I18N_CUSTOM_EXPORT_PATH_SET_KEY = "data-pipeline.config.event.custom.export.path.set";
    private static final String I18N_UNAUTHORIZED_EXPORT_KEY = "data-pipeline.audit.action.full.export.unauthorized";

    private AuditEventFactory() {
    }

    public static AuditEvent createFullExportTriggeredAuditEvent(int jobId) {
        return AuditEvent.builder((AuditType)AuditType.fromI18nKeys((CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION, (CoverageLevel)CoverageLevel.ADVANCED, (String)I18N_AUDIT_CATEGORY_KEY, (String)I18N_EXPORT_TRIGGERED_KEY).build()).extraAttribute(AuditAttribute.fromI18nKeys((String)I18N_JOB_ID_KEY, (String)Integer.toString(jobId)).build()).build();
    }

    public static AuditEvent createFullExportCancelledAuditEvent(int jobId) {
        return AuditEvent.builder((AuditType)AuditType.fromI18nKeys((CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION, (CoverageLevel)CoverageLevel.ADVANCED, (String)I18N_AUDIT_CATEGORY_KEY, (String)I18N_EXPORT_CANCELLED_KEY).build()).extraAttribute(AuditAttribute.fromI18nKeys((String)I18N_JOB_ID_KEY, (String)Integer.toString(jobId)).build()).build();
    }

    public static AuditEvent createCustomExportPathSetAuditEvent(String previousExportPath, String customExportPath) {
        return AuditEvent.builder((AuditType)AuditType.fromI18nKeys((CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION, (CoverageLevel)CoverageLevel.ADVANCED, (String)I18N_AUDIT_CATEGORY_KEY, (String)I18N_CUSTOM_EXPORT_PATH_SET_KEY).build()).changedValue(ChangedValue.fromI18nKeys((String)I18N_EXPORT_PATH_KEY).from(previousExportPath).to(customExportPath).build()).build();
    }

    public static AuditEvent createCustomExportPathDeletedAuditEvent(String previousExportPath, String defaultRootExportPath) {
        return AuditEvent.builder((AuditType)AuditType.fromI18nKeys((CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION, (CoverageLevel)CoverageLevel.ADVANCED, (String)I18N_AUDIT_CATEGORY_KEY, (String)I18N_DELETED_PATH_KEY).build()).changedValue(ChangedValue.fromI18nKeys((String)I18N_EXPORT_PATH_KEY).from(previousExportPath).to(defaultRootExportPath).build()).build();
    }

    public static AuditEvent createUnauthorizedExportAttemptAuditEvent(String request) {
        return AuditEvent.builder((AuditType)AuditType.fromI18nKeys((CoverageArea)CoverageArea.END_USER_ACTIVITY, (CoverageLevel)CoverageLevel.ADVANCED, (String)I18N_AUDIT_CATEGORY_KEY, (String)I18N_UNAUTHORIZED_EXPORT_KEY).build()).appendExtraAttributes(Collections.singletonList(new AuditAttribute.Builder(I18N_UNAUTHORIZED_EXPORT_REQUEST_KEY, request).build())).build();
    }

    public static AuditEvent createScheduleSetAuditEvent(String scheduleConfig) {
        return AuditEvent.builder((AuditType)AuditType.fromI18nKeys((CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION, (CoverageLevel)CoverageLevel.ADVANCED, (String)I18N_AUDIT_CATEGORY_KEY, (String)I18N_SCHEDULE_CONFIG_CREATED_KEY).build()).extraAttribute(AuditAttribute.fromI18nKeys((String)I18N_SCHEDULE_CONFIG_KEY, (String)scheduleConfig).build()).build();
    }

    public static AuditEvent createScheduleDeletedAuditEvent() {
        return AuditEvent.builder((AuditType)AuditType.fromI18nKeys((CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION, (CoverageLevel)CoverageLevel.ADVANCED, (String)I18N_AUDIT_CATEGORY_KEY, (String)I18N_SCHEDULE_CONFIG_DELETED_KEY).build()).build();
    }
}

