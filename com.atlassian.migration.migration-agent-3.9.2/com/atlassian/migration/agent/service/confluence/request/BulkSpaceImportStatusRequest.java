/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonInclude
 *  com.fasterxml.jackson.annotation.JsonInclude$Include
 *  com.fasterxml.jackson.annotation.JsonProperty
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.confluence.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Generated;

@JsonInclude(value=JsonInclude.Include.NON_NULL)
public class BulkSpaceImportStatusRequest {
    @JsonProperty(value="ids")
    public final List<String> ids;

    @Generated
    public BulkSpaceImportStatusRequest(List<String> ids) {
        this.ids = ids;
    }

    @Generated
    public List<String> getIds() {
        return this.ids;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BulkSpaceImportStatusRequest)) {
            return false;
        }
        BulkSpaceImportStatusRequest other = (BulkSpaceImportStatusRequest)o;
        if (!other.canEqual(this)) {
            return false;
        }
        List<String> this$ids = this.getIds();
        List<String> other$ids = other.getIds();
        return !(this$ids == null ? other$ids != null : !((Object)this$ids).equals(other$ids));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof BulkSpaceImportStatusRequest;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        List<String> $ids = this.getIds();
        result = result * 59 + ($ids == null ? 43 : ((Object)$ids).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "BulkSpaceImportStatusRequest(ids=" + this.getIds() + ")";
    }
}

