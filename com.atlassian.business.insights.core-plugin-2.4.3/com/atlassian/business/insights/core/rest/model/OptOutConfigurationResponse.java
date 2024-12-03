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

public class OptOutConfigurationResponse {
    private List<OptOutEntityResponse> entitiesProcessed;
    private List<OptOutEntityResponse> entitiesNotFound;

    public OptOutConfigurationResponse() {
    }

    @JsonCreator
    public OptOutConfigurationResponse(@JsonProperty(value="entitiesProcessed") List<OptOutEntityResponse> entitiesProcessed, @JsonProperty(value="entitiesNotFound") List<OptOutEntityResponse> entitiesNotFound) {
        this.entitiesProcessed = entitiesProcessed;
        this.entitiesNotFound = entitiesNotFound;
    }

    @JsonProperty(value="entitiesProcessed")
    public List<OptOutEntityResponse> getEntitiesProcessed() {
        return this.entitiesProcessed;
    }

    @JsonProperty(value="entitiesNotFound")
    public List<OptOutEntityResponse> getEntitiesNotFound() {
        return this.entitiesNotFound;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OptOutConfigurationResponse)) {
            return false;
        }
        OptOutConfigurationResponse that = (OptOutConfigurationResponse)o;
        return Objects.equals(this.entitiesProcessed, that.entitiesProcessed) && Objects.equals(this.entitiesNotFound, that.entitiesNotFound);
    }

    public int hashCode() {
        return Objects.hash(this.entitiesProcessed, this.entitiesNotFound);
    }

    public String toString() {
        return "OptOutConfigurationResponse{entitiesProcessed=" + this.entitiesProcessed + ", entitiesNotFound=" + this.entitiesNotFound + '}';
    }
}

