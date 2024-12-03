/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.crowd.embedded.api.OperationType
 *  com.google.common.base.Preconditions
 */
package com.atlassian.crowd.manager.recovery;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.embedded.api.OperationType;
import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public final class RecoveryModeDirectory
implements Directory {
    private static final long serialVersionUID = 439749327493246L;
    static final long ID = -2L;
    private final String username;
    private final String password;
    private final long createdTimeMillis = System.currentTimeMillis();
    private final Map<String, String> attributes = Collections.emptyMap();

    public RecoveryModeDirectory(String username, String password) {
        this.username = (String)Preconditions.checkNotNull((Object)username, (Object)"username");
        this.password = (String)Preconditions.checkNotNull((Object)password, (Object)"password");
    }

    public Long getId() {
        return -2L;
    }

    public String getName() {
        return "Recovery Mode Directory";
    }

    public boolean isActive() {
        return true;
    }

    public String getEncryptionType() {
        return null;
    }

    public Map<String, String> getAttributes() {
        return this.attributes;
    }

    public Set<OperationType> getAllowedOperations() {
        return Collections.emptySet();
    }

    public String getDescription() {
        return "Holds a single unmodifiable user that has system-wide permissions in the system. To use for recovering from configuration issues that prevent any of the existing users from logging in.";
    }

    public DirectoryType getType() {
        return DirectoryType.INTERNAL;
    }

    public String getImplementationClass() {
        return this.getClass().getName();
    }

    public Date getCreatedDate() {
        return new Date(this.createdTimeMillis);
    }

    public Date getUpdatedDate() {
        return new Date(this.createdTimeMillis);
    }

    public Set<String> getValues(String key) {
        return this.attributes.containsKey(key) ? Collections.singleton(this.attributes.get(key)) : null;
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

    public boolean isPersistable() {
        return false;
    }

    public String getRecoveryUsername() {
        return this.username;
    }

    public String getRecoveryPassword() {
        return this.password;
    }
}

