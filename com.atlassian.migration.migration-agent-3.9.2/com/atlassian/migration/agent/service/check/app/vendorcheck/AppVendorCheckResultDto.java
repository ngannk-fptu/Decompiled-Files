/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.app.dto.check.CheckDetail
 *  com.atlassian.migration.app.dto.check.CheckStatus
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service.check.app.vendorcheck;

import com.atlassian.migration.agent.service.check.app.vendorcheck.SerializableCsvFileContentDto;
import com.atlassian.migration.app.dto.check.CheckDetail;
import com.atlassian.migration.app.dto.check.CheckStatus;
import java.io.Serializable;
import java.util.Set;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class AppVendorCheckResultDto
implements Serializable {
    private static final long serialVersionUID = 8077302058528187368L;
    @JsonProperty
    public final String title;
    @JsonProperty
    public final String stepsToResolve;
    @JsonProperty
    public final CheckStatus status;
    @JsonProperty
    public final Boolean showCsv;
    @JsonIgnore
    public final String checkId;
    @JsonIgnore
    public final SerializableCsvFileContentDto csvContent;
    @JsonIgnore
    private final Set<CheckDetail> checkDetails;

    @JsonCreator
    public AppVendorCheckResultDto(@JsonProperty(value="title") String title, @JsonProperty(value="stepsToResolve") String stepsToResolve, @JsonProperty(value="status") CheckStatus status, @JsonProperty(value="showCsv") boolean showCsv, String checkId, SerializableCsvFileContentDto serialisedCsvContent, Set<CheckDetail> checkDetails) {
        this.title = title;
        this.stepsToResolve = stepsToResolve;
        this.status = status;
        this.showCsv = showCsv;
        this.checkId = checkId;
        this.csvContent = serialisedCsvContent;
        this.checkDetails = checkDetails;
    }

    public Set<CheckDetail> getCheckDetails() {
        return this.checkDetails;
    }

    @Generated
    public String getTitle() {
        return this.title;
    }

    @Generated
    public String getStepsToResolve() {
        return this.stepsToResolve;
    }

    @Generated
    public CheckStatus getStatus() {
        return this.status;
    }

    @Generated
    public Boolean getShowCsv() {
        return this.showCsv;
    }

    @Generated
    public String getCheckId() {
        return this.checkId;
    }

    @Generated
    public SerializableCsvFileContentDto getCsvContent() {
        return this.csvContent;
    }
}

