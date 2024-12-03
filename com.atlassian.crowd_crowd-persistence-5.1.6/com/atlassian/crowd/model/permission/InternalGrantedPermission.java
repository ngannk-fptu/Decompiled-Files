/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.manager.permission.PermittedGroup
 *  com.atlassian.crowd.model.application.DirectoryMapping
 *  com.atlassian.crowd.model.permission.UserPermission
 *  com.google.common.base.MoreObjects
 */
package com.atlassian.crowd.model.permission;

import com.atlassian.crowd.manager.permission.PermittedGroup;
import com.atlassian.crowd.model.application.DirectoryMapping;
import com.atlassian.crowd.model.permission.UserPermission;
import com.google.common.base.MoreObjects;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class InternalGrantedPermission
implements Serializable,
PermittedGroup {
    private Long id;
    private UserPermission permission;
    private Date createdDate;
    private DirectoryMapping directoryMapping;
    private String groupName;

    private InternalGrantedPermission() {
    }

    public InternalGrantedPermission(Long id, Date createdDate, UserPermission userPermission, DirectoryMapping directoryMapping, String groupName) {
        this.id = id;
        this.createdDate = createdDate;
        this.permission = userPermission;
        this.directoryMapping = directoryMapping;
        this.groupName = groupName;
    }

    public InternalGrantedPermission(UserPermission userPermission, DirectoryMapping directoryMapping, String groupName) {
        this(null, null, userPermission, directoryMapping, groupName);
    }

    public Long getId() {
        return this.id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public UserPermission getPermission() {
        return this.permission;
    }

    public void setPermission(UserPermission permission) {
        this.permission = permission;
    }

    public Long getDirectoryId() {
        return this.directoryMapping.getDirectory().getId();
    }

    public String getDirectoryName() {
        return this.directoryMapping.getDirectory().getName();
    }

    public String getGroupName() {
        return this.groupName;
    }

    private void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public DirectoryMapping getDirectoryMapping() {
        return this.directoryMapping;
    }

    private void setDirectoryMapping(DirectoryMapping directoryMapping) {
        this.directoryMapping = directoryMapping;
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", (Object)this.id).add("permission", (Object)this.permission).add("createdDate", (Object)this.createdDate).add("directoryMapping", (Object)this.directoryMapping).add("groupName", (Object)this.groupName).toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        InternalGrantedPermission that = (InternalGrantedPermission)o;
        return Objects.equals(this.id, that.id) && this.permission == that.permission && Objects.equals(this.createdDate, that.createdDate) && Objects.equals(this.directoryMapping, that.directoryMapping) && Objects.equals(this.groupName, that.groupName);
    }

    public int hashCode() {
        return Objects.hash(this.id, this.permission, this.createdDate, this.directoryMapping, this.groupName);
    }
}

