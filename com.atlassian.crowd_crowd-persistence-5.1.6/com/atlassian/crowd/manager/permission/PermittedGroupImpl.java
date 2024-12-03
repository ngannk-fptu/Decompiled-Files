/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.manager.permission.PermittedGroup
 *  com.atlassian.crowd.model.permission.UserPermission
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.crowd.manager.permission;

import com.atlassian.crowd.manager.permission.DirectoryGroupImpl;
import com.atlassian.crowd.manager.permission.PermittedGroup;
import com.atlassian.crowd.model.permission.UserPermission;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PermittedGroupImpl
extends DirectoryGroupImpl
implements PermittedGroup {
    private final UserPermission permission;

    public PermittedGroupImpl(UserPermission permission, Long directoryId, String directoryName, String groupName) {
        super(directoryId, directoryName, groupName);
        this.permission = permission;
    }

    public UserPermission getPermission() {
        return this.permission;
    }

    @Override
    public String toString() {
        return new ToStringBuilder((Object)this).append("directoryId", (Object)this.getDirectoryId()).append("directoryName", (Object)this.getDirectoryName()).append("groupName", (Object)this.getGroupName()).append("permission", (Object)this.permission).build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PermittedGroupImpl that = (PermittedGroupImpl)o;
        if (this.getDirectoryId() != null ? !this.getDirectoryId().equals(that.getDirectoryId()) : that.getDirectoryId() != null) {
            return false;
        }
        if (this.getDirectoryName() != null ? !this.getDirectoryName().equals(that.getDirectoryName()) : that.getDirectoryName() != null) {
            return false;
        }
        if (this.getGroupName() != null ? !this.getGroupName().equals(that.getGroupName()) : that.getGroupName() != null) {
            return false;
        }
        return !(this.permission != null ? !this.permission.equals((Object)that.permission) : that.permission != null);
    }

    @Override
    public int hashCode() {
        int result = this.getDirectoryId() != null ? this.getDirectoryId().hashCode() : 0;
        result = 31 * result + (this.getDirectoryName() != null ? this.getDirectoryName().hashCode() : 0);
        result = 31 * result + (this.getGroupName() != null ? this.getGroupName().hashCode() : 0);
        result = 31 * result + (this.permission != null ? this.permission.hashCode() : 0);
        return result;
    }
}

