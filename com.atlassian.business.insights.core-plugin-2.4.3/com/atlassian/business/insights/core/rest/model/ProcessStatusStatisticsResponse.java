/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.business.insights.core.rest.model;

import com.atlassian.business.insights.core.rest.model.NullableRestResponseObject;
import com.atlassian.business.insights.core.service.api.ExportJobState;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ProcessStatusStatisticsResponse
implements NullableRestResponseObject {
    private Integer exportedEntities;
    private Integer writtenRows;

    public ProcessStatusStatisticsResponse() {
    }

    @JsonCreator
    public ProcessStatusStatisticsResponse(@JsonProperty(value="exportedEntities") Integer exportedEntities, @JsonProperty(value="writtenRows") Integer writtenRows) {
        this.exportedEntities = exportedEntities;
        this.writtenRows = writtenRows;
    }

    public ProcessStatusStatisticsResponse(ExportJobState exportJobState) {
        this.exportedEntities = exportJobState.getExportedEntities();
        this.writtenRows = exportJobState.getWrittenRows();
    }

    @JsonProperty
    public Integer getExportedEntities() {
        return this.exportedEntities;
    }

    @JsonProperty
    public Integer getWrittenRows() {
        return this.writtenRows;
    }

    @Override
    public boolean isEmpty() {
        return this.exportedEntities == null && this.writtenRows == null;
    }
}

