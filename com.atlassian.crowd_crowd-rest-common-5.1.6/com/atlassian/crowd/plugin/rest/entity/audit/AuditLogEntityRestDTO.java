/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.audit.AuditLogEntity
 *  com.atlassian.crowd.audit.AuditLogEntityType
 *  com.atlassian.crowd.audit.ImmutableAuditLogEntity$Builder
 *  com.google.common.base.MoreObjects
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.plugin.rest.entity.audit;

import com.atlassian.crowd.audit.AuditLogEntity;
import com.atlassian.crowd.audit.AuditLogEntityType;
import com.atlassian.crowd.audit.ImmutableAuditLogEntity;
import com.google.common.base.MoreObjects;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class AuditLogEntityRestDTO {
    @JsonProperty(value="id")
    private final Long id;
    @JsonProperty(value="name")
    private final String name;
    @JsonProperty(value="displayName")
    private final String displayName;
    @JsonProperty(value="originalName")
    private final String originalName;
    @JsonProperty(value="type")
    private final AuditLogEntityType type;
    @JsonProperty(value="subtype")
    private final String subtype;
    @JsonProperty(value="primary")
    private final Boolean primary;

    public AuditLogEntity toEntity() {
        ImmutableAuditLogEntity.Builder builder = new ImmutableAuditLogEntity.Builder().setEntityId(this.id).setEntityName(this.name).setEntityType(this.type);
        if (this.primary != null && this.primary.booleanValue()) {
            builder.setPrimary();
        }
        return builder.build();
    }

    public AuditLogEntityRestDTO(Long id, String name, String displayName, String originalName, AuditLogEntityType type, Boolean primary) {
        this(id, name, displayName, originalName, type, null, primary);
    }

    @JsonCreator
    public AuditLogEntityRestDTO(@JsonProperty(value="id") Long id, @JsonProperty(value="name") String name, @JsonProperty(value="displayName") String displayName, @JsonProperty(value="originalName") String originalName, @JsonProperty(value="type") AuditLogEntityType type, @JsonProperty(value="subtype") String subtype, @JsonProperty(value="primary") Boolean primary) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.originalName = originalName;
        this.type = type;
        this.subtype = subtype;
        this.primary = primary;
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

    public AuditLogEntityType getType() {
        return this.type;
    }

    public String getSubtype() {
        return this.subtype;
    }

    public Boolean getPrimary() {
        return this.primary;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(AuditLogEntityRestDTO data) {
        return new Builder(data);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AuditLogEntityRestDTO that = (AuditLogEntityRestDTO)o;
        return Objects.equals(this.getId(), that.getId()) && Objects.equals(this.getName(), that.getName()) && Objects.equals(this.getDisplayName(), that.getDisplayName()) && Objects.equals(this.getOriginalName(), that.getOriginalName()) && Objects.equals(this.getType(), that.getType()) && Objects.equals(this.getSubtype(), that.getSubtype()) && Objects.equals(this.getPrimary(), that.getPrimary());
    }

    public int hashCode() {
        return Objects.hash(this.getId(), this.getName(), this.getDisplayName(), this.getOriginalName(), this.getType(), this.getSubtype(), this.getPrimary());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", (Object)this.getId()).add("name", (Object)this.getName()).add("displayName", (Object)this.getDisplayName()).add("originalName", (Object)this.getOriginalName()).add("type", (Object)this.getType()).add("subtype", (Object)this.getSubtype()).add("primary", (Object)this.getPrimary()).toString();
    }

    public static final class Builder {
        private Long id;
        private String name;
        private String displayName;
        private String originalName;
        private AuditLogEntityType type;
        private String subtype;
        private Boolean primary;

        private Builder() {
        }

        private Builder(AuditLogEntityRestDTO initialData) {
            this.id = initialData.getId();
            this.name = initialData.getName();
            this.displayName = initialData.getDisplayName();
            this.originalName = initialData.getOriginalName();
            this.type = initialData.getType();
            this.subtype = initialData.getSubtype();
            this.primary = initialData.getPrimary();
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

        public Builder setType(AuditLogEntityType type) {
            this.type = type;
            return this;
        }

        public Builder setSubtype(String subtype) {
            this.subtype = subtype;
            return this;
        }

        public Builder setPrimary(Boolean primary) {
            this.primary = primary;
            return this;
        }

        public AuditLogEntityRestDTO build() {
            return new AuditLogEntityRestDTO(this.id, this.name, this.displayName, this.originalName, this.type, this.subtype, this.primary);
        }
    }
}

