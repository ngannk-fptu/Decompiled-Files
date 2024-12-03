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

public class AuditAuthor {
    public static final AuditAuthor SYSTEM_AUTHOR = AuditAuthor.builder().id("-1").type("system").name("System").build();
    public static final AuditAuthor ANONYMOUS_AUTHOR = AuditAuthor.builder().id("-2").type("user").name("Anonymous").build();
    public static final AuditAuthor UNKNOWN_AUTHOR = AuditAuthor.builder().id("-3").type("user").name("Unknown").build();
    private final String id;
    private final String type;
    private String name;
    private String uri;

    private AuditAuthor(Builder builder) {
        this.id = builder.id;
        this.type = builder.type;
        this.name = builder.name;
        this.uri = builder.uri;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Nonnull
    public String getId() {
        return this.id;
    }

    @Nonnull
    public String getType() {
        return this.type;
    }

    @Nullable
    public String getName() {
        return this.name;
    }

    @Nullable
    public String getUri() {
        return this.uri;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AuditAuthor that = (AuditAuthor)o;
        return this.id.equals(that.id) && Objects.equals(this.type, that.type) && Objects.equals(this.uri, that.uri);
    }

    public int hashCode() {
        return Objects.hash(this.id, this.type);
    }

    public String toString() {
        return "AuditAuthor{key='" + this.id + '\'' + ", type='" + this.type + '\'' + ", name='" + this.name + '\'' + ", uri='" + this.uri + '\'' + '}';
    }

    public static class Builder {
        private String id;
        private String type;
        private String name;
        private String uri;

        public Builder() {
        }

        public Builder(AuditAuthor author) {
            this.id = author.id;
            this.type = author.type;
            this.name = author.name;
            this.uri = author.uri;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder uri(String uri) {
            this.uri = uri;
            return this;
        }

        public AuditAuthor build() {
            Objects.requireNonNull(this.id);
            Objects.requireNonNull(this.type);
            return new AuditAuthor(this);
        }
    }
}

