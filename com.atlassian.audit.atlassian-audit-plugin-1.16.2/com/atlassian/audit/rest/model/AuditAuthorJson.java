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
public class AuditAuthorJson {
    @Nonnull
    private final String name;
    @Nonnull
    private final String type;
    @Nullable
    private final String id;
    @Nullable
    private final String uri;
    @Nullable
    private final String avatarUri;

    @JsonCreator
    public AuditAuthorJson(@JsonProperty(value="name") @Nonnull String name, @JsonProperty(value="type") @Nonnull String type, @JsonProperty(value="id") @Nullable String id, @JsonProperty(value="uri") @Nullable String uri, @JsonProperty(value="avatarUri") @Nullable String avatarUri) {
        this.name = name;
        this.type = type;
        this.id = id;
        this.uri = uri;
        this.avatarUri = avatarUri;
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
    @JsonProperty(value="id")
    public String getId() {
        return this.id;
    }

    @Nullable
    @JsonProperty(value="uri")
    public String getUri() {
        return this.uri;
    }

    @Nonnull
    @JsonProperty(value="avatarUri")
    public String getAvatarUri() {
        return this.avatarUri;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AuditAuthorJson that = (AuditAuthorJson)o;
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
        return "AuditAuthorJson{name='" + this.name + '\'' + ", type='" + this.type + '\'' + ", uri='" + this.uri + '\'' + ", id='" + this.id + '\'' + '}';
    }
}

