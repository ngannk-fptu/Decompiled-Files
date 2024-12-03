/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.crowd.embedded.api.OperationType
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.model.directory;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.embedded.api.OperationType;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;

public class ImmutableDirectory
implements Directory {
    private final Long id;
    private final String name;
    private final boolean active;
    private final String encryptionType;
    private final Map<String, String> attributes;
    private final Set<OperationType> allowedOperations;
    private final String description;
    private final DirectoryType type;
    private final String implementationClass;
    private final Date createdDate;
    private final Date updatedDate;

    private ImmutableDirectory(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.active = builder.active;
        this.encryptionType = builder.encryptionType;
        this.attributes = Collections.unmodifiableMap(new HashMap(builder.attributes));
        this.allowedOperations = Collections.unmodifiableSet(new HashSet(builder.allowedOperations));
        this.description = builder.description;
        this.type = builder.type;
        this.implementationClass = builder.implementationClass;
        this.createdDate = builder.createdDate;
        this.updatedDate = builder.updatedDate;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public boolean isActive() {
        return this.active;
    }

    public String getEncryptionType() {
        return this.encryptionType;
    }

    public Map<String, String> getAttributes() {
        return this.attributes;
    }

    public Set<OperationType> getAllowedOperations() {
        return this.allowedOperations;
    }

    public String getDescription() {
        return this.description;
    }

    public DirectoryType getType() {
        return this.type;
    }

    public String getImplementationClass() {
        return this.implementationClass;
    }

    public Date getCreatedDate() {
        return this.createdDate == null ? null : new Date(this.createdDate.getTime());
    }

    public Date getUpdatedDate() {
        return this.updatedDate == null ? null : new Date(this.updatedDate.getTime());
    }

    @Nullable
    public Set<String> getValues(String key) {
        String value = this.attributes.get(key);
        return value == null ? null : Collections.singleton(value);
    }

    @Nullable
    public String getValue(String key) {
        return this.attributes.get(key);
    }

    public Set<String> getKeys() {
        return this.attributes.keySet();
    }

    public boolean isEmpty() {
        return this.attributes.isEmpty();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ImmutableDirectory that = (ImmutableDirectory)o;
        return this.active == that.active && Objects.equals(this.id, that.id) && Objects.equals(this.name, that.name) && Objects.equals(this.encryptionType, that.encryptionType) && Objects.equals(this.attributes, that.attributes) && Objects.equals(this.allowedOperations, that.allowedOperations) && Objects.equals(this.description, that.description) && this.type == that.type && Objects.equals(this.implementationClass, that.implementationClass) && Objects.equals(this.createdDate, that.createdDate) && Objects.equals(this.updatedDate, that.updatedDate);
    }

    public int hashCode() {
        return Objects.hash(this.id, this.name, this.active, this.encryptionType, this.attributes, this.allowedOperations, this.description, this.type, this.implementationClass, this.createdDate, this.updatedDate);
    }

    public static ImmutableDirectory from(Directory directory) {
        return directory instanceof ImmutableDirectory ? (ImmutableDirectory)directory : ImmutableDirectory.builder(directory).build();
    }

    public static Builder builder(String name, DirectoryType type, String implementationClass) {
        return new Builder(name, type, implementationClass);
    }

    public static Builder builder(Directory directory) {
        return new Builder(directory);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", (Object)this.id).add("name", (Object)this.name).add("type", (Object)this.type).toString();
    }

    public static class Builder {
        private Long id;
        private String name;
        private boolean active = true;
        private String encryptionType;
        private Map<String, String> attributes = new HashMap<String, String>();
        private Set<OperationType> allowedOperations = new HashSet<OperationType>();
        private String description;
        private DirectoryType type;
        private String implementationClass;
        private Date createdDate;
        private Date updatedDate;

        public Builder(String name, DirectoryType type, String implementationClass) {
            this.name = name;
            this.type = type;
            this.implementationClass = implementationClass;
        }

        public Builder(Directory directory) {
            Preconditions.checkNotNull((Object)directory, (Object)"directory");
            this.setName(directory.getName()).setType(directory.getType()).setImplementationClass(directory.getImplementationClass()).setId(directory.getId()).setActive(directory.isActive()).setEncryptionType(directory.getEncryptionType()).setAttributes(new HashMap<String, String>(directory.getAttributes())).setAllowedOperations(directory.getAllowedOperations()).setDescription(directory.getDescription()).setCreatedDate(directory.getCreatedDate()).setUpdatedDate(directory.getUpdatedDate());
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setActive(boolean active) {
            this.active = active;
            return this;
        }

        public Builder setEncryptionType(String encryptionType) {
            this.encryptionType = encryptionType;
            return this;
        }

        public Builder setAttributes(Map<String, String> attributes) {
            this.attributes = new HashMap<String, String>(attributes);
            return this;
        }

        public Builder setAttribute(String key, String value) {
            this.attributes.put(key, value);
            return this;
        }

        public Builder setAllowedOperations(Set<OperationType> allowedOperations) {
            this.allowedOperations = allowedOperations;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setType(DirectoryType type) {
            this.type = type;
            return this;
        }

        public Builder setImplementationClass(String implementationClass) {
            this.implementationClass = implementationClass;
            return this;
        }

        public Builder setCreatedDate(Date createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder setUpdatedDate(Date updatedDate) {
            this.updatedDate = updatedDate;
            return this;
        }

        public ImmutableDirectory build() {
            return new ImmutableDirectory(this);
        }
    }
}

