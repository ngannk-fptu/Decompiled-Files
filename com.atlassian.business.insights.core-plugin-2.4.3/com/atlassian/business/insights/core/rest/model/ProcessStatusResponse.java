/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.business.insights.api.schema.SchemaStatus
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.springframework.util.CollectionUtils
 */
package com.atlassian.business.insights.core.rest.model;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.business.insights.api.schema.SchemaStatus;
import com.atlassian.business.insights.core.ao.dao.entity.ExportProgressStatus;
import com.atlassian.business.insights.core.ao.dao.entity.Metadata;
import com.atlassian.business.insights.core.rest.model.ProcessStatusConfigResponse;
import com.atlassian.business.insights.core.rest.model.ProcessStatusStatisticsResponse;
import com.atlassian.business.insights.core.rest.model.RestOptedOutEntitiesByType;
import com.atlassian.business.insights.core.rest.model.SchemaResponse;
import com.atlassian.business.insights.core.rest.validation.DiagnosticDescription;
import com.atlassian.business.insights.core.service.api.ExportJobState;
import com.atlassian.business.insights.core.service.api.OptedOutEntity;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.util.CollectionUtils;

public class ProcessStatusResponse {
    @VisibleForTesting
    static final int MAX_ENTITIES_PER_TYPE = 100;
    private String startTime;
    private String cancelledTime;
    private String completedTime;
    private String nodeId;
    private Integer jobId;
    private ExportProgressStatus status;
    private SchemaResponse schema;
    private ProcessStatusConfigResponse config;
    private ProcessStatusStatisticsResponse statistics;
    private List<DiagnosticDescription> warnings;
    private List<DiagnosticDescription> errors;
    private String rootExportPath;
    private List<RestOptedOutEntitiesByType> optedOutEntities;

    public ProcessStatusResponse() {
    }

    @JsonCreator
    public ProcessStatusResponse(@JsonProperty(value="startTime") String startTime, @JsonProperty(value="cancelledTime") String cancelledTime, @JsonProperty(value="completedTime") String completedTime, @JsonProperty(value="nodeId") String nodeId, @JsonProperty(value="jobId") Integer jobId, @JsonProperty(value="status") ExportProgressStatus status, @JsonProperty(value="schema") SchemaResponse schema, @JsonProperty(value="config") ProcessStatusConfigResponse config, @JsonProperty(value="statistics") ProcessStatusStatisticsResponse statistics, @JsonProperty(value="warnings") List<DiagnosticDescription> warnings, @JsonProperty(value="errors") List<DiagnosticDescription> errors, @JsonProperty(value="rootExportPath") String rootExportPath, @JsonProperty(value="optedOutEntities") List<RestOptedOutEntitiesByType> optedOutEntities) {
        this.startTime = startTime;
        this.cancelledTime = cancelledTime;
        this.completedTime = completedTime;
        this.nodeId = nodeId;
        this.jobId = jobId;
        this.status = status;
        this.schema = schema;
        this.config = config;
        this.statistics = statistics;
        this.warnings = warnings;
        this.errors = errors;
        this.rootExportPath = rootExportPath;
        this.optedOutEntities = optedOutEntities;
    }

    public ProcessStatusResponse(@Nonnull ExportJobState exportJobState, @Nonnull Function<Instant, String> instantToStringConverter, @Nonnull SchemaStatus schemaStatus) {
        Objects.requireNonNull(exportJobState);
        Objects.requireNonNull(instantToStringConverter);
        Objects.requireNonNull(schemaStatus);
        this.status = exportJobState.getStatus();
        this.startTime = instantToStringConverter.apply(exportJobState.getCreatedTime());
        this.cancelledTime = Optional.ofNullable(exportJobState.getCancelledTime()).map(instantToStringConverter).orElse(null);
        this.completedTime = Optional.ofNullable(exportJobState.getFinishedTime()).map(instantToStringConverter).orElse(null);
        this.nodeId = Optional.ofNullable(exportJobState.getMetadata()).map(Metadata::getNodeId).orElse(null);
        this.jobId = exportJobState.getId();
        this.config = new ProcessStatusConfigResponse(instantToStringConverter.apply(exportJobState.getJobConfiguredFromTime()), exportJobState.isExportForced());
        this.statistics = exportJobState.getStatus() != ExportProgressStatus.STARTED ? new ProcessStatusStatisticsResponse(exportJobState) : null;
        this.warnings = exportJobState.getWarnings();
        this.errors = exportJobState.getErrors();
        this.rootExportPath = Optional.ofNullable(exportJobState.getRootExportPath()).map(path -> path.toAbsolutePath().toString()).orElse(null);
        this.schema = new SchemaResponse(exportJobState.getSchemaVersion(), schemaStatus);
        this.optedOutEntities = this.groupOptedOutEntities(exportJobState.getOptedOutEntities());
    }

    public static ProcessStatusResponse getEmptyResponse() {
        return new ProcessStatusResponse(null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    @JsonProperty
    public List<RestOptedOutEntitiesByType> getOptedOutEntities() {
        return this.optedOutEntities;
    }

    @JsonProperty
    public ExportProgressStatus getStatus() {
        return this.status;
    }

    @JsonProperty
    public String getStartTime() {
        return this.startTime;
    }

    @JsonProperty
    public String getCancelledTime() {
        return this.cancelledTime;
    }

    @JsonProperty
    public String getCompletedTime() {
        return this.completedTime;
    }

    @JsonProperty
    public String getNodeId() {
        return this.nodeId;
    }

    @JsonProperty
    public Integer getJobId() {
        return this.jobId;
    }

    @JsonProperty
    public SchemaResponse getSchema() {
        return this.schema;
    }

    @JsonProperty
    public ProcessStatusConfigResponse getConfig() {
        if (this.config == null || this.config.isEmpty()) {
            return null;
        }
        return this.config;
    }

    @JsonProperty
    public ProcessStatusStatisticsResponse getStatistics() {
        if (this.statistics == null || this.statistics.isEmpty()) {
            return null;
        }
        return this.statistics;
    }

    @JsonProperty
    public List<DiagnosticDescription> getWarnings() {
        return CollectionUtils.isEmpty(this.warnings) ? null : this.warnings;
    }

    @JsonProperty
    public List<DiagnosticDescription> getErrors() {
        return CollectionUtils.isEmpty(this.errors) ? null : this.errors;
    }

    @JsonProperty
    public String getRootExportPath() {
        return this.rootExportPath;
    }

    private List<RestOptedOutEntitiesByType> groupOptedOutEntities(List<OptedOutEntity> optedOutEntities) {
        if (CollectionUtils.isEmpty(optedOutEntities)) {
            return null;
        }
        return optedOutEntities.stream().map(OptedOutEntity::getType).distinct().map(type -> this.getOptedOutEntitiesByType(optedOutEntities, (String)type)).collect(Collectors.toList());
    }

    private RestOptedOutEntitiesByType getOptedOutEntitiesByType(List<OptedOutEntity> optedOutEntities, String type) {
        List<String> entitiesByType = optedOutEntities.stream().filter(e -> e.getType().equals(type)).map(OptedOutEntity::getKey).filter(Objects::nonNull).limit(101L).collect(Collectors.toList());
        if (entitiesByType.size() == 101) {
            entitiesByType.set(100, "...");
        }
        return new RestOptedOutEntitiesByType(type, entitiesByType);
    }
}

