/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 */
package com.atlassian.crowd.model.application;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.DirectoryMapping;
import java.util.Comparator;

@Deprecated
public class GroupMapping {
    private Long id;
    private DirectoryMapping directoryMapping;
    private String groupName;
    private Application application;
    private Directory directory;

    protected GroupMapping() {
    }

    public GroupMapping(Long id, DirectoryMapping directoryMapping, String groupName) {
        this(directoryMapping, groupName);
        this.id = id;
    }

    public GroupMapping(DirectoryMapping directoryMapping, String groupName) {
        this.directoryMapping = directoryMapping;
        this.groupName = groupName;
        this.application = directoryMapping.getApplication();
        this.directory = directoryMapping.getDirectory();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Application getApplication() {
        return this.application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Directory getDirectory() {
        return this.directory;
    }

    public void setDirectory(Directory directory) {
        this.directory = directory;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public DirectoryMapping getDirectoryMapping() {
        return this.directoryMapping;
    }

    public void setDirectoryMapping(DirectoryMapping directoryMapping) {
        this.directoryMapping = directoryMapping;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GroupMapping)) {
            return false;
        }
        GroupMapping that = (GroupMapping)o;
        if (this.getDirectoryMapping().getId() != null ? !this.getDirectoryMapping().getId().equals(that.getDirectoryMapping().getId()) : that.getDirectoryMapping().getId() != null) {
            return false;
        }
        return !(this.getGroupName() != null ? !this.getGroupName().equals(that.getGroupName()) : that.getGroupName() != null);
    }

    public int hashCode() {
        int result = this.getDirectoryMapping().getId() != null ? this.getDirectoryMapping().getId().hashCode() : 0;
        result = 31 * result + (this.getGroupName() != null ? this.getGroupName().hashCode() : 0);
        return result;
    }

    public static final class COMPARATOR
    implements Comparator<GroupMapping> {
        @Override
        public int compare(GroupMapping firstMapping, GroupMapping secondMapping) {
            return IdentifierUtils.compareToInLowerCase((String)firstMapping.getGroupName(), (String)secondMapping.getGroupName());
        }
    }
}

