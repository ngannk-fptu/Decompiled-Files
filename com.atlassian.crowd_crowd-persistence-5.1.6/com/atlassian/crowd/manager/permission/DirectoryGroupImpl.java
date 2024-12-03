/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.manager.permission.DirectoryGroup
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.crowd.manager.permission;

import com.atlassian.crowd.manager.permission.DirectoryGroup;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class DirectoryGroupImpl
implements DirectoryGroup {
    private final Long directoryId;
    private final String directoryName;
    private final String groupName;

    public DirectoryGroupImpl(Long directoryId, String directoryName, String groupName) {
        this.directoryId = directoryId;
        this.directoryName = directoryName;
        this.groupName = groupName;
    }

    public Long getDirectoryId() {
        return this.directoryId;
    }

    public String getDirectoryName() {
        return this.directoryName;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("directoryId", (Object)this.directoryId).append("directoryName", (Object)this.directoryName).append("groupName", (Object)this.groupName).build();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DirectoryGroupImpl that = (DirectoryGroupImpl)o;
        if (this.directoryId != null ? !this.directoryId.equals(that.directoryId) : that.directoryId != null) {
            return false;
        }
        if (this.directoryName != null ? !this.directoryName.equals(that.directoryName) : that.directoryName != null) {
            return false;
        }
        return !(this.groupName != null ? !this.groupName.equals(that.groupName) : that.groupName != null);
    }

    public int hashCode() {
        int result = this.directoryId != null ? this.directoryId.hashCode() : 0;
        result = 31 * result + (this.directoryName != null ? this.directoryName.hashCode() : 0);
        result = 31 * result + (this.groupName != null ? this.groupName.hashCode() : 0);
        return result;
    }
}

