/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.OperationType
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.model.application;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.OperationType;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.ApplicationDirectoryMapping;
import com.atlassian.crowd.model.application.GroupMapping;
import com.google.common.collect.Sets;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

@Deprecated
public class DirectoryMapping
implements Serializable,
ApplicationDirectoryMapping {
    private Long id;
    private Application application;
    private Directory directory;
    private boolean allowAllToAuthenticate;
    private Set<GroupMapping> authorisedGroups = new HashSet<GroupMapping>();
    private Set<OperationType> allowedOperations = new HashSet<OperationType>();

    protected DirectoryMapping() {
    }

    public DirectoryMapping(Long id, Application application, Directory directory, boolean allowAllToAuthenticate) {
        this(application, directory, allowAllToAuthenticate);
        this.id = id;
    }

    public DirectoryMapping(Application application, Directory directory, boolean allowAllToAuthenticate) {
        this.application = application;
        this.directory = directory;
        this.allowAllToAuthenticate = allowAllToAuthenticate;
    }

    public DirectoryMapping(Application application, Directory directory, boolean allowAllToAuthenticate, Set<OperationType> allowedOperations) {
        this(application, directory, allowAllToAuthenticate);
        this.allowedOperations.addAll(allowedOperations);
    }

    public Long getId() {
        return this.id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    public Application getApplication() {
        return this.application;
    }

    private void setApplication(Application application) {
        this.application = application;
    }

    @Override
    public Directory getDirectory() {
        return this.directory;
    }

    private void setDirectory(Directory directory) {
        this.directory = directory;
    }

    @Override
    public boolean isAllowAllToAuthenticate() {
        return this.allowAllToAuthenticate;
    }

    public void setAllowAllToAuthenticate(boolean allowAllToAuthenticate) {
        this.allowAllToAuthenticate = allowAllToAuthenticate;
    }

    @Override
    public Set<String> getAuthorisedGroupNames() {
        return this.getAuthorisedGroups().stream().map(GroupMapping::getGroupName).collect(Collectors.toSet());
    }

    public Set<GroupMapping> getAuthorisedGroups() {
        return this.authorisedGroups;
    }

    public void setAuthorisedGroups(Set<GroupMapping> authorisedGroups) {
        this.authorisedGroups = authorisedGroups;
    }

    public boolean isAuthorised(String groupName) {
        return this.getGroupMapping(groupName) != null;
    }

    @Nullable
    public GroupMapping getGroupMapping(String groupName) {
        for (GroupMapping mapping : this.getAuthorisedGroups()) {
            if (!mapping.getGroupName().equals(groupName)) continue;
            return mapping;
        }
        return null;
    }

    public void addGroupMapping(String groupName) {
        this.authorisedGroups.add(new GroupMapping(this, groupName));
    }

    public void removeGroupMapping(String groupName) {
        this.authorisedGroups.remove(new GroupMapping(this, groupName));
    }

    public void addAllowedOperations(OperationType ... operationTypes) {
        this.allowedOperations.addAll(Sets.newHashSet((Object[])operationTypes));
    }

    public void addAllowedOperation(OperationType operationType) {
        this.allowedOperations.add(operationType);
    }

    @Override
    public Set<OperationType> getAllowedOperations() {
        return this.allowedOperations;
    }

    public void setAllowedOperations(Set<OperationType> allowedOperations) {
        this.allowedOperations = allowedOperations;
    }

    public boolean isAllowed(OperationType operation) {
        return this.allowedOperations.contains(operation);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DirectoryMapping)) {
            return false;
        }
        DirectoryMapping that = (DirectoryMapping)o;
        if (this.getApplication() != null && this.getApplication().getId() != null ? !this.getApplication().getId().equals(that.getApplication().getId()) : that.getApplication() != null && that.getApplication().getId() != null) {
            return false;
        }
        return !(this.getDirectory() != null ? !this.getDirectory().getId().equals(that.getDirectory().getId()) : that.getDirectory() != null);
    }

    public int hashCode() {
        int result = this.getApplication() != null && this.getApplication().getId() != null ? this.getApplication().getId().hashCode() : 0;
        result = 31 * result + (this.getDirectory() != null ? this.getDirectory().getId().hashCode() : 0);
        return result;
    }

    public static DirectoryMapping fromApplicationDirectoryMapping(Application application, ApplicationDirectoryMapping applicationDirectoryMapping) {
        DirectoryMapping mappping = new DirectoryMapping(application, applicationDirectoryMapping.getDirectory(), applicationDirectoryMapping.isAllowAllToAuthenticate(), applicationDirectoryMapping.getAllowedOperations());
        mappping.setAuthorisedGroups(applicationDirectoryMapping.getAuthorisedGroupNames().stream().map(groupName -> new GroupMapping(mappping, (String)groupName)).collect(Collectors.toSet()));
        return mappping;
    }
}

