/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.business.insights.core.rest.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonProperty;

public class RestOptedOutEntitiesByType
implements Serializable {
    private String type;
    private List<String> identifiers;

    public RestOptedOutEntitiesByType() {
    }

    public RestOptedOutEntitiesByType(@JsonProperty(value="type") String type, @JsonProperty(value="identifiers") List<String> identifiers) {
        this.type = type;
        this.identifiers = identifiers;
    }

    @JsonProperty(value="type")
    public String getType() {
        return this.type;
    }

    @JsonProperty(value="identifiers")
    public List<String> getIdentifiers() {
        return this.identifiers;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RestOptedOutEntitiesByType)) {
            return false;
        }
        RestOptedOutEntitiesByType that = (RestOptedOutEntitiesByType)o;
        return Objects.equals(this.type, that.type) && Objects.equals(this.identifiers, that.identifiers);
    }

    public int hashCode() {
        return Objects.hash(this.type, this.identifiers);
    }

    public String toString() {
        return "RestOptedOutEntitiesByType{type='" + this.type + '\'' + ", identifiers=" + this.identifiers + '}';
    }
}

