/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service.stepexecutor.space;

import java.util.List;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class TombstoneAccountsResponse {
    private List<String> tombstoneAccountIds;

    @JsonCreator
    public TombstoneAccountsResponse(@JsonProperty(value="tombstoneAccountIds") List<String> tombstoneAccountIds) {
        this.tombstoneAccountIds = tombstoneAccountIds;
    }

    @Generated
    public List<String> getTombstoneAccountIds() {
        return this.tombstoneAccountIds;
    }

    @Generated
    public void setTombstoneAccountIds(List<String> tombstoneAccountIds) {
        this.tombstoneAccountIds = tombstoneAccountIds;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TombstoneAccountsResponse)) {
            return false;
        }
        TombstoneAccountsResponse other = (TombstoneAccountsResponse)o;
        if (!other.canEqual(this)) {
            return false;
        }
        List<String> this$tombstoneAccountIds = this.getTombstoneAccountIds();
        List<String> other$tombstoneAccountIds = other.getTombstoneAccountIds();
        return !(this$tombstoneAccountIds == null ? other$tombstoneAccountIds != null : !((Object)this$tombstoneAccountIds).equals(other$tombstoneAccountIds));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof TombstoneAccountsResponse;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        List<String> $tombstoneAccountIds = this.getTombstoneAccountIds();
        result = result * 59 + ($tombstoneAccountIds == null ? 43 : ((Object)$tombstoneAccountIds).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "TombstoneAccountsResponse(tombstoneAccountIds=" + this.getTombstoneAccountIds() + ")";
    }
}

