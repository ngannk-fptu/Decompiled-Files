/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.mobile.dto.metadata;

import java.util.Objects;
import org.codehaus.jackson.annotate.JsonProperty;

public class ChildrenMetadataDto {
    @JsonProperty
    private Integer count;

    public Integer getCount() {
        return this.count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ChildrenMetadataDto that = (ChildrenMetadataDto)o;
        return Objects.equals(this.count, that.count);
    }

    public int hashCode() {
        return this.count != null ? this.count : -1;
    }
}

