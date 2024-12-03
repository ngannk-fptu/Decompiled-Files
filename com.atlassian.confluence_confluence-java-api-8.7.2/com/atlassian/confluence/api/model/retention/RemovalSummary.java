/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.retention;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.retention.RemovalCount;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
public class RemovalSummary {
    @JsonProperty
    private RemovalCount global = new RemovalCount();
    @JsonProperty
    private RemovalCount space = new RemovalCount();

    public RemovalCount getGlobal() {
        return this.global;
    }

    public void setGlobal(RemovalCount global) {
        this.global = global;
    }

    public RemovalCount getSpace() {
        return this.space;
    }

    public void setSpace(RemovalCount space) {
        this.space = space;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RemovalSummary that = (RemovalSummary)o;
        return Objects.equals(this.global, that.global) && Objects.equals(this.space, that.space);
    }

    public int hashCode() {
        return Objects.hash(this.global, this.space);
    }

    public String toString() {
        return "RemovalSummary{global=" + this.global + ", space=" + this.space + '}';
    }
}

