/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.audit.AuditLogAuthor
 *  com.atlassian.crowd.audit.AuditLogAuthorType
 *  com.atlassian.crowd.audit.ImmutableAuditLogAuthor
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Preconditions
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.plugin.rest.entity.audit;

import com.atlassian.crowd.audit.AuditLogAuthor;
import com.atlassian.crowd.audit.AuditLogAuthorType;
import com.atlassian.crowd.audit.ImmutableAuditLogAuthor;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class AuditLogAuthorRestDTO {
    @JsonProperty(value="id")
    private final Long id;
    @JsonProperty(value="name")
    private final String name;
    @JsonProperty(value="displayName")
    private final String displayName;
    @JsonProperty(value="originalName")
    private final String originalName;
    @JsonProperty(value="type")
    private final AuditLogAuthorType type;
    @JsonProperty(value="subtype")
    private final String subtype;

    public AuditLogAuthor toAuthor() {
        Preconditions.checkState((this.type != null ? 1 : 0) != 0, (Object)"Author type not set");
        return new ImmutableAuditLogAuthor(this.id, this.name, this.type);
    }

    public AuditLogAuthorRestDTO(Long id, String name, String displayName, String originalName, AuditLogAuthorType type) {
        this(id, name, displayName, originalName, type, null);
    }

    @JsonCreator
    public AuditLogAuthorRestDTO(@JsonProperty(value="id") Long id, @JsonProperty(value="name") String name, @JsonProperty(value="displayName") String displayName, @JsonProperty(value="originalName") String originalName, @JsonProperty(value="type") AuditLogAuthorType type, @JsonProperty(value="subtype") String subtype) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.originalName = originalName;
        this.type = type;
        this.subtype = subtype;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getOriginalName() {
        return this.originalName;
    }

    public AuditLogAuthorType getType() {
        return this.type;
    }

    public String getSubtype() {
        return this.subtype;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(AuditLogAuthorRestDTO data) {
        return new Builder(data);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AuditLogAuthorRestDTO that = (AuditLogAuthorRestDTO)o;
        return Objects.equals(this.getId(), that.getId()) && Objects.equals(this.getName(), that.getName()) && Objects.equals(this.getDisplayName(), that.getDisplayName()) && Objects.equals(this.getOriginalName(), that.getOriginalName()) && Objects.equals(this.getType(), that.getType()) && Objects.equals(this.getSubtype(), that.getSubtype());
    }

    public int hashCode() {
        return Objects.hash(this.getId(), this.getName(), this.getDisplayName(), this.getOriginalName(), this.getType(), this.getSubtype());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", (Object)this.getId()).add("name", (Object)this.getName()).add("displayName", (Object)this.getDisplayName()).add("originalName", (Object)this.getOriginalName()).add("type", (Object)this.getType()).add("subtype", (Object)this.getSubtype()).toString();
    }

    public static final class Builder {
        private Long id;
        private String name;
        private String displayName;
        private String originalName;
        private AuditLogAuthorType type;
        private String subtype;

        private Builder() {
        }

        private Builder(AuditLogAuthorRestDTO initialData) {
            this.id = initialData.getId();
            this.name = initialData.getName();
            this.displayName = initialData.getDisplayName();
            this.originalName = initialData.getOriginalName();
            this.type = initialData.getType();
            this.subtype = initialData.getSubtype();
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder setOriginalName(String originalName) {
            this.originalName = originalName;
            return this;
        }

        public Builder setType(AuditLogAuthorType type) {
            this.type = type;
            return this;
        }

        public Builder setSubtype(String subtype) {
            this.subtype = subtype;
            return this;
        }

        public AuditLogAuthorRestDTO build() {
            return new AuditLogAuthorRestDTO(this.id, this.name, this.displayName, this.originalName, this.type, this.subtype);
        }
    }
}

