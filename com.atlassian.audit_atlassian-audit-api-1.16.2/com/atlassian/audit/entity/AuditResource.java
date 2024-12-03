/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.audit.entity;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AuditResource {
    private final String name;
    private final String type;
    private String uri;
    private String id;

    private AuditResource(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.uri = builder.uri;
        this.type = builder.type;
    }

    @Nonnull
    public String getName() {
        return this.name;
    }

    @Nonnull
    public String getType() {
        return this.type;
    }

    @Nullable
    public String getUri() {
        return this.uri;
    }

    @Nullable
    public String getId() {
        return this.id;
    }

    public String toString() {
        return "AuditResource{name='" + this.name + '\'' + ", type='" + this.type + '\'' + ", uri='" + this.uri + '\'' + ", id='" + this.id + '\'' + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AuditResource that = (AuditResource)o;
        return this.name.equals(that.name) && this.type.equals(that.type) && Objects.equals(this.uri, that.uri) && Objects.equals(this.id, that.id);
    }

    public int hashCode() {
        return Objects.hash(this.name, this.type, this.uri, this.id);
    }

    public static Builder builder(@Nonnull String name, @Nonnull String type) {
        return new Builder(name, type);
    }

    public static class Builder {
        private String name;
        private String type;
        private String uri;
        private String id;

        private Builder(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public Builder(AuditResource resource) {
            this.id = resource.id;
            this.name = resource.name;
            this.type = resource.type;
            this.uri = resource.uri;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder uri(String uri) {
            this.uri = uri;
            return this;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public AuditResource build() {
            Objects.requireNonNull(this.name);
            Objects.requireNonNull(this.type);
            return new AuditResource(this);
        }
    }
}

