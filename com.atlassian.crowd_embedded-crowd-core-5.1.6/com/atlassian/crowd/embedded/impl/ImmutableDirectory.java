/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.crowd.embedded.api.OperationType
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.embedded.impl;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.embedded.api.OperationType;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ImmutableDirectory
implements Directory,
Serializable {
    private static final long serialVersionUID = -8196445895525985343L;
    private final Long id;
    private final String name;
    private final boolean active;
    private final String encryptionType;
    private final String description;
    private final DirectoryType type;
    private final String implementationClass;
    private final long createdDate;
    private final long updatedDate;
    private final Set<OperationType> allowedOperations;
    private final Map<String, String> attributes;

    public ImmutableDirectory(Long id, String name, boolean active, String description, String encryptionType, DirectoryType type, String implementationClass, @Nonnull Date createdDate, @Nonnull Date updatedDate, @Nullable Set<OperationType> allowedOperations, @Nullable Map<String, String> attributes) {
        this.id = id;
        this.name = name;
        this.active = active;
        this.description = description;
        this.encryptionType = encryptionType;
        this.type = type;
        this.implementationClass = implementationClass;
        this.createdDate = createdDate.getTime();
        this.updatedDate = updatedDate.getTime();
        this.allowedOperations = ImmutableDirectory.immutableCopyOf(allowedOperations);
        this.attributes = ImmutableDirectory.immutableCopyOf(attributes);
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
        return new Date(this.createdDate);
    }

    public Date getUpdatedDate() {
        return new Date(this.updatedDate);
    }

    public Set<OperationType> getAllowedOperations() {
        return this.allowedOperations;
    }

    public Map<String, String> getAttributes() {
        return this.attributes;
    }

    public Set<String> getValues(String key) {
        String value = this.getValue(key);
        if (value == null) {
            return null;
        }
        return Collections.singleton(value);
    }

    public String getValue(String key) {
        return this.attributes.get(key);
    }

    public Set<String> getKeys() {
        return this.attributes.keySet();
    }

    public boolean isEmpty() {
        return this.attributes.isEmpty();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(Directory directory) {
        Builder builder = new Builder();
        builder.setId(directory.getId());
        builder.setActive(directory.isActive());
        builder.setName(directory.getName());
        builder.setDescription(directory.getDescription());
        builder.setEncryptionType(directory.getEncryptionType());
        builder.setType(directory.getType());
        builder.setImplementationClass(directory.getImplementationClass());
        builder.setCreatedDate(directory.getCreatedDate());
        builder.setUpdatedDate(directory.getUpdatedDate());
        builder.setAllowedOperations(new HashSet<OperationType>(directory.getAllowedOperations()));
        builder.setAttributes(new HashMap<String, String>(directory.getAttributes()));
        return builder;
    }

    private static <E> Set<E> immutableCopyOf(@Nullable Set<E> set) {
        if (set == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(new HashSet<E>(set));
    }

    private static <K, V> Map<K, V> immutableCopyOf(@Nullable Map<K, V> map) {
        if (map == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(new HashMap<K, V>(map));
    }

    public static final class Builder {
        private Long id;
        private String name;
        private boolean active = true;
        private String encryptionType;
        private String description;
        private DirectoryType type;
        private String implementationClass;
        private Date createdDate = new Date();
        private Date updatedDate = new Date();
        private Set<OperationType> allowedOperations;
        private Map<String, String> attributes;

        public Directory toDirectory() {
            return new ImmutableDirectory(this.id, this.name, this.active, this.description, this.encryptionType, this.type, this.implementationClass, this.createdDate, this.updatedDate, this.allowedOperations, this.attributes);
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public void setEncryptionType(String encryptionType) {
            this.encryptionType = encryptionType;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setType(DirectoryType type) {
            this.type = type;
        }

        public void setImplementationClass(String implementationClass) {
            this.implementationClass = implementationClass;
        }

        public void setCreatedDate(Date createdDate) {
            this.createdDate = createdDate;
        }

        public void setUpdatedDate(Date updatedDate) {
            this.updatedDate = updatedDate;
        }

        public void setAllowedOperations(Set<OperationType> allowedOperations) {
            this.allowedOperations = allowedOperations;
        }

        public void setAttributes(Map<String, String> attributes) {
            this.attributes = attributes;
        }
    }
}

