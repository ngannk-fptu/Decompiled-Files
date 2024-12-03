/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.business.insights.api.dataset.Dataset
 *  com.atlassian.business.insights.api.schema.SchemaStatus
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.business.insights.core.rest.model;

import com.atlassian.business.insights.api.dataset.Dataset;
import com.atlassian.business.insights.api.schema.SchemaStatus;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class SchemaResponse {
    private int version;
    private SchemaStatus status;

    public SchemaResponse() {
    }

    @JsonCreator
    public SchemaResponse(@JsonProperty(value="version") int version, @JsonProperty(value="status") SchemaStatus status) {
        this.version = version;
        this.status = status;
    }

    @JsonProperty
    public int getVersion() {
        return this.version;
    }

    @JsonProperty
    public SchemaStatus getStatus() {
        return this.status;
    }

    @Nonnull
    public static SchemaResponse fromDataset(@Nonnull Dataset dataset) {
        Objects.requireNonNull(dataset);
        return new SchemaResponse(dataset.getVersion(), dataset.isDeprecated() ? SchemaStatus.DEPRECATED : SchemaStatus.ACTIVE);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SchemaResponse that = (SchemaResponse)o;
        return this.version == that.version && this.status == that.status;
    }

    public int hashCode() {
        return Objects.hash(this.version, this.status);
    }

    public String toString() {
        return "Schema version=" + this.version + ", status=" + this.status;
    }
}

