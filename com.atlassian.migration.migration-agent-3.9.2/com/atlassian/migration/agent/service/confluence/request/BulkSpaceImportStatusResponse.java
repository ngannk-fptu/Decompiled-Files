/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service.confluence.request;

import com.atlassian.migration.agent.service.ConfluenceImportExportTaskStatus;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
@ParametersAreNonnullByDefault
public class BulkSpaceImportStatusResponse {
    @JsonProperty
    private Map<String, ConfluenceImportExportTaskStatus> statuses;
    @JsonProperty
    private List<String> unprocessedIds;

    @JsonCreator
    public BulkSpaceImportStatusResponse(@JsonProperty(value="statuses") Map<String, ConfluenceImportExportTaskStatus> statuses, @JsonProperty(value="unprocessedIds") List<String> unprocessedIds) {
        this.statuses = statuses;
        this.unprocessedIds = unprocessedIds;
    }

    public Optional<ConfluenceImportExportTaskStatus> getStatus(String cloudImportTaskId) {
        if (this.statuses == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.statuses.get(cloudImportTaskId));
    }

    @Generated
    public Map<String, ConfluenceImportExportTaskStatus> getStatuses() {
        return this.statuses;
    }

    @Generated
    public List<String> getUnprocessedIds() {
        return this.unprocessedIds;
    }

    @Generated
    public void setStatuses(Map<String, ConfluenceImportExportTaskStatus> statuses) {
        this.statuses = statuses;
    }

    @Generated
    public void setUnprocessedIds(List<String> unprocessedIds) {
        this.unprocessedIds = unprocessedIds;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BulkSpaceImportStatusResponse)) {
            return false;
        }
        BulkSpaceImportStatusResponse other = (BulkSpaceImportStatusResponse)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Map<String, ConfluenceImportExportTaskStatus> this$statuses = this.getStatuses();
        Map<String, ConfluenceImportExportTaskStatus> other$statuses = other.getStatuses();
        if (this$statuses == null ? other$statuses != null : !((Object)this$statuses).equals(other$statuses)) {
            return false;
        }
        List<String> this$unprocessedIds = this.getUnprocessedIds();
        List<String> other$unprocessedIds = other.getUnprocessedIds();
        return !(this$unprocessedIds == null ? other$unprocessedIds != null : !((Object)this$unprocessedIds).equals(other$unprocessedIds));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof BulkSpaceImportStatusResponse;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Map<String, ConfluenceImportExportTaskStatus> $statuses = this.getStatuses();
        result = result * 59 + ($statuses == null ? 43 : ((Object)$statuses).hashCode());
        List<String> $unprocessedIds = this.getUnprocessedIds();
        result = result * 59 + ($unprocessedIds == null ? 43 : ((Object)$unprocessedIds).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "BulkSpaceImportStatusResponse(statuses=" + this.getStatuses() + ", unprocessedIds=" + this.getUnprocessedIds() + ")";
    }
}

