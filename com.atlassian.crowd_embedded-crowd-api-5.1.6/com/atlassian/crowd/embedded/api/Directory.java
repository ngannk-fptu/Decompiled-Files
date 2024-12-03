/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.embedded.api;

import com.atlassian.crowd.embedded.api.Attributes;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.embedded.api.OperationType;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public interface Directory
extends Serializable,
Attributes {
    public Long getId();

    public String getName();

    public boolean isActive();

    public String getEncryptionType();

    public Map<String, String> getAttributes();

    public Set<OperationType> getAllowedOperations();

    public String getDescription();

    public DirectoryType getType();

    public String getImplementationClass();

    public Date getCreatedDate();

    public Date getUpdatedDate();

    default public boolean isPersistable() {
        return true;
    }
}

