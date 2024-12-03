/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.audit.rest.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AuditResourceJson {
    @Nonnull
    private final String name;
    @Nonnull
    private final String type;
    @Nullable
    private final String uri;
    @Nullable
    private final String id;

    @JsonCreator
    public AuditResourceJson(@JsonProperty(value="name") @Nonnull String name, @JsonProperty(value="type") @Nonnull String type, @JsonProperty(value="uri") @Nullable String uri, @JsonProperty(value="id") @Nullable String id) {
        this.name = name;
        this.type = type;
        this.uri = uri;
        this.id = id;
    }

    @Nonnull
    @JsonProperty(value="name")
    public String getName() {
        return this.name;
    }

    @Nonnull
    @JsonProperty(value="type")
    public String getType() {
        return this.type;
    }

    @Nullable
    @JsonProperty(value="uri")
    public String getUri() {
        return this.uri;
    }

    @Nullable
    @JsonProperty(value="id")
    public String getId() {
        return this.id;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AuditResourceJson that = (AuditResourceJson)o;
        if (!this.name.equals(that.name)) {
            return false;
        }
        if (!this.type.equals(that.type)) {
            return false;
        }
        if (this.uri != null ? !this.uri.equals(that.uri) : that.uri != null) {
            return false;
        }
        return this.id != null ? this.id.equals(that.id) : that.id == null;
    }

    public int hashCode() {
        int result = this.name.hashCode();
        result = 31 * result + this.type.hashCode();
        result = 31 * result + (this.uri != null ? this.uri.hashCode() : 0);
        result = 31 * result + (this.id != null ? this.id.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "AuditResourceJson{name='" + this.name + '\'' + ", type='" + this.type + '\'' + ", uri='" + this.uri + '\'' + ", id='" + this.id + '\'' + '}';
    }
}

