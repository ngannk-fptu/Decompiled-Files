/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.business.insights.core.rest.model;

import java.io.Serializable;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class OptOutEntityResponse
implements Serializable {
    private String type;
    private String identifier;
    private String key;
    private String displayName;
    private String uri;

    public OptOutEntityResponse() {
    }

    @JsonCreator
    public OptOutEntityResponse(@JsonProperty(value="type") String type, @JsonProperty(value="identifier") String identifier, @JsonProperty(value="key") String key, @JsonProperty(value="displayName") String displayName, @JsonProperty(value="uri") String uri) {
        this.type = type;
        this.identifier = identifier;
        this.key = key;
        this.displayName = displayName;
        this.uri = uri;
    }

    @JsonProperty(value="type")
    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty(value="identifier")
    public String getIdentifier() {
        return this.identifier;
    }

    @JsonProperty(value="key")
    public String getKey() {
        return this.key;
    }

    @JsonProperty(value="displayName")
    public String getDisplayName() {
        return this.displayName;
    }

    @JsonProperty(value="uri")
    public String getUri() {
        return this.uri;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OptOutEntityResponse)) {
            return false;
        }
        OptOutEntityResponse that = (OptOutEntityResponse)o;
        return Objects.equals(this.type, that.type) && Objects.equals(this.identifier, that.identifier) && Objects.equals(this.key, that.key) && Objects.equals(this.displayName, that.displayName) && Objects.equals(this.uri, that.uri);
    }

    public int hashCode() {
        return Objects.hash(this.type, this.identifier, this.key, this.displayName, this.uri);
    }

    public String toString() {
        return "OptOutEntityResponse{type='" + this.type + '\'' + ", identifier='" + this.identifier + '\'' + ", key='" + this.key + '\'' + ", displayName='" + this.displayName + '\'' + ", uri='" + this.uri + '\'' + '}';
    }
}

