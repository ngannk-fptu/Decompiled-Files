/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.business.insights.core.rest.model;

import com.atlassian.business.insights.core.rest.model.OptOutEntityResponse;
import java.util.List;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class OptOutSummaryResponse {
    private List<OptOutEntityResponse> optedOutEntities;

    public OptOutSummaryResponse() {
    }

    @JsonCreator
    public OptOutSummaryResponse(@JsonProperty(value="optedOutEntities") List<OptOutEntityResponse> optedOutEntities) {
        this.optedOutEntities = optedOutEntities;
    }

    @JsonProperty(value="optedOutEntities")
    public List<OptOutEntityResponse> getOptedOutEntities() {
        return this.optedOutEntities;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OptOutSummaryResponse)) {
            return false;
        }
        OptOutSummaryResponse that = (OptOutSummaryResponse)o;
        return Objects.equals(this.optedOutEntities, that.optedOutEntities);
    }

    public int hashCode() {
        return Objects.hash(this.optedOutEntities);
    }

    public String toString() {
        return "OptOutSummaryResponse{optedOutEntities=" + this.optedOutEntities + '}';
    }
}

