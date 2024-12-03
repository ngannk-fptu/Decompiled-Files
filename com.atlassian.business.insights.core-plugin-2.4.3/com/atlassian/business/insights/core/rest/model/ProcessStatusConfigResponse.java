/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.business.insights.core.rest.model;

import com.atlassian.business.insights.core.rest.model.NullableRestResponseObject;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ProcessStatusConfigResponse
implements NullableRestResponseObject {
    private String exportFrom;
    private boolean forcedExport;

    public ProcessStatusConfigResponse() {
    }

    @JsonCreator
    public ProcessStatusConfigResponse(@JsonProperty(value="exportFrom") String exportFrom, @JsonProperty(value="forcedExport") boolean forcedExport) {
        this.exportFrom = exportFrom;
        this.forcedExport = forcedExport;
    }

    @JsonProperty
    public String getExportFrom() {
        return this.exportFrom;
    }

    @Override
    public boolean isEmpty() {
        return (this.exportFrom == null || this.exportFrom.isEmpty()) && !this.forcedExport;
    }

    @JsonProperty
    public boolean isForcedExport() {
        return this.forcedExport;
    }
}

