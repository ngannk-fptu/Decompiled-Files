/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.crowd.embedded.api.OperationType
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.embedded.api.OperationType;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public abstract class DirectoryWrapper
implements Directory {
    protected Directory delegate;

    public DirectoryWrapper(Directory delegate) {
        this.delegate = delegate;
    }

    @Nullable
    public Set<String> getValues(String key) {
        return this.delegate.getValues(key);
    }

    @Nullable
    public String getValue(String key) {
        return this.delegate.getValue(key);
    }

    public Set<String> getKeys() {
        return this.delegate.getKeys();
    }

    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    public Long getId() {
        return this.delegate.getId();
    }

    public String getName() {
        return this.delegate.getName();
    }

    public boolean isActive() {
        return this.delegate.isActive();
    }

    public String getEncryptionType() {
        return this.delegate.getEncryptionType();
    }

    public Map<String, String> getAttributes() {
        return this.delegate.getAttributes();
    }

    public Set<OperationType> getAllowedOperations() {
        return this.delegate.getAllowedOperations();
    }

    public String getDescription() {
        return this.delegate.getDescription();
    }

    public DirectoryType getType() {
        return this.delegate.getType();
    }

    public String getImplementationClass() {
        return this.delegate.getImplementationClass();
    }

    public Date getCreatedDate() {
        return this.delegate.getCreatedDate();
    }

    public Date getUpdatedDate() {
        return this.delegate.getUpdatedDate();
    }

    public boolean isPersistable() {
        return this.delegate.isPersistable();
    }
}

