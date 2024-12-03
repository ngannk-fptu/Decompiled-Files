/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.ApplicationDefaultGroupMembershipConfiguration
 *  com.atlassian.crowd.model.application.DirectoryMapping
 */
package com.atlassian.crowd.model.application;

import com.atlassian.crowd.model.application.ApplicationDefaultGroupMembershipConfiguration;
import com.atlassian.crowd.model.application.DirectoryMapping;
import java.io.Serializable;
import java.util.Objects;

public class InternalApplicationDefaultGroupMembershipConfiguration
implements Serializable,
ApplicationDefaultGroupMembershipConfiguration {
    private Long id;
    private DirectoryMapping directoryMapping;
    private String groupName;

    public InternalApplicationDefaultGroupMembershipConfiguration() {
    }

    public InternalApplicationDefaultGroupMembershipConfiguration(DirectoryMapping directoryMapping, String groupName) {
        this(null, directoryMapping, groupName);
    }

    public InternalApplicationDefaultGroupMembershipConfiguration(Long id, DirectoryMapping directoryMapping, String groupName) {
        this.id = id;
        this.directoryMapping = directoryMapping;
        this.groupName = groupName;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DirectoryMapping getDirectoryMapping() {
        return this.directoryMapping;
    }

    public void setDirectoryMapping(DirectoryMapping directoryMapping) {
        this.directoryMapping = directoryMapping;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InternalApplicationDefaultGroupMembershipConfiguration)) {
            return false;
        }
        InternalApplicationDefaultGroupMembershipConfiguration that = (InternalApplicationDefaultGroupMembershipConfiguration)o;
        if (this.directoryMapping != null && that.directoryMapping != null ? !Objects.equals(this.directoryMapping.getId(), that.directoryMapping.getId()) : this.directoryMapping != that.directoryMapping) {
            return false;
        }
        return Objects.equals(this.groupName, that.groupName);
    }

    public int hashCode() {
        return Objects.hash(this.directoryMapping == null ? 0L : this.directoryMapping.getId(), this.groupName);
    }
}

