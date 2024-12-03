/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.business.insights.core.service;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.business.insights.core.ao.dao.entity.ExportProgressStatus;
import com.atlassian.business.insights.core.rest.validation.DiagnosticDescription;
import com.atlassian.business.insights.core.service.ExportCounter;
import com.atlassian.business.insights.core.service.api.OptedOutEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

public class ExportStatusContext {
    @VisibleForTesting
    public static final String PRE_VALIDATION_FAILED_KEY = "export.pre.validation.failed";
    @VisibleForTesting
    static final String SCHEMA_VERSION_DEPRECATED_KEY = "export.schema.version.deprecated";
    @VisibleForTesting
    static final String SCHEMA_VERSION_MISSING_KEY = "export.schema.version.missing";
    @VisibleForTesting
    static final String UNEXPECTED_EXCEPTION_KEY = "unexpected.exception";
    @VisibleForTesting
    static final int DIAGNOSTIC_MESSAGE_MAX_LENGTH = 255;
    private ExportProgressStatus exportProgressStatus;
    private final ExportCounter exportCounter;
    private final boolean forcedExport;
    private final List<DiagnosticDescription> errors;
    private final List<DiagnosticDescription> warnings;
    private List<OptedOutEntity> optedOutEntities = new ArrayList<OptedOutEntity>();

    public ExportStatusContext(@Nonnull ExportProgressStatus exportProgressStatus, boolean forcedExport) {
        this.exportProgressStatus = Objects.requireNonNull(exportProgressStatus);
        this.exportCounter = new ExportCounter();
        this.forcedExport = forcedExport;
        this.errors = new ArrayList<DiagnosticDescription>();
        this.warnings = new ArrayList<DiagnosticDescription>();
    }

    @Nonnull
    public ExportProgressStatus getExportProgressStatus() {
        return this.exportProgressStatus;
    }

    public void setExportProgressStatus(@Nonnull ExportProgressStatus exportProgressStatus) {
        this.exportProgressStatus = exportProgressStatus;
    }

    public void setExportProgressStatus(@Nonnull ExportProgressStatus exportProgressStatus, @Nonnull Exception exception) {
        this.exportProgressStatus = exportProgressStatus;
        String message = StringUtils.truncate((String)Optional.ofNullable(exception.getMessage()).orElse(exception.toString()), (int)255);
        this.addError(UNEXPECTED_EXCEPTION_KEY, message);
    }

    public void addPreValidationError(String message, boolean forcedExport) {
        if (forcedExport) {
            this.addWarning(PRE_VALIDATION_FAILED_KEY, message);
        } else {
            this.addError(PRE_VALIDATION_FAILED_KEY, message);
        }
    }

    public void addError(@Nonnull String key, @Nonnull String message) {
        this.errors.add(new DiagnosticDescription(Objects.requireNonNull(key), StringUtils.truncate((String)Objects.requireNonNull(message), (int)255)));
    }

    public void addWarning(@Nonnull String key, @Nonnull String message) {
        this.warnings.add(new DiagnosticDescription(Objects.requireNonNull(key), StringUtils.truncate((String)Objects.requireNonNull(message), (int)255)));
    }

    public void incrementRowCounter(int rowCount) {
        this.exportCounter.incrementRowCounter(rowCount);
    }

    public void incrementExportedEntitiesCounter() {
        this.exportCounter.incrementExportedEntitiesCounter();
    }

    public int getRowCount() {
        return this.exportCounter.getRowCounter().get();
    }

    public int getExportedEntities() {
        return this.exportCounter.getExportedEntitiesCounter().get();
    }

    public boolean isForcedExport() {
        return this.forcedExport;
    }

    @Nonnull
    public List<DiagnosticDescription> getErrors() {
        return this.errors;
    }

    @Nonnull
    public List<DiagnosticDescription> getWarnings() {
        return this.warnings;
    }

    @Nonnull
    public List<OptedOutEntity> getOptedOutEntities() {
        return this.optedOutEntities;
    }

    public void setOptedOutEntities(@Nonnull List<OptedOutEntity> optedOutEntities) {
        this.optedOutEntities = Objects.requireNonNull(optedOutEntities, "optedOutEntities");
    }

    public void addOptedOutEntity(@Nonnull OptedOutEntity optedOutEntity) {
        Objects.requireNonNull(optedOutEntity, "optedOutEntity");
        this.optedOutEntities.add(optedOutEntity);
    }
}

