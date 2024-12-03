/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.model.InternalEntityTemplate
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupComparator
 *  com.atlassian.crowd.model.group.GroupTemplate
 *  com.atlassian.crowd.model.group.GroupType
 *  com.atlassian.crowd.model.group.InternalDirectoryGroup
 *  com.atlassian.crowd.util.InternalEntityUtils
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.crowd.model.group;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.model.InternalDirectoryEntity;
import com.atlassian.crowd.model.InternalEntityTemplate;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupComparator;
import com.atlassian.crowd.model.group.GroupTemplate;
import com.atlassian.crowd.model.group.GroupType;
import com.atlassian.crowd.model.group.InternalDirectoryGroup;
import com.atlassian.crowd.model.group.InternalGroupAttribute;
import com.atlassian.crowd.model.permission.GroupAdministrationGrantToGroup;
import com.atlassian.crowd.model.permission.UserAdministrationGrantToGroup;
import com.atlassian.crowd.util.InternalEntityUtils;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class InternalGroup
extends InternalDirectoryEntity<InternalGroupAttribute>
implements InternalDirectoryGroup {
    private String lowerName;
    private GroupType type;
    private String description;
    private boolean isLocal;
    private String externalId;
    private Set<GroupAdministrationGrantToGroup> grantsToOtherGroups = new HashSet<GroupAdministrationGrantToGroup>();
    private Set<GroupAdministrationGrantToGroup> groupGrantsToThisGroup = new HashSet<GroupAdministrationGrantToGroup>();
    private Set<UserAdministrationGrantToGroup> userGrantsToThisGroup = new HashSet<UserAdministrationGrantToGroup>();

    protected InternalGroup() {
    }

    public InternalGroup(InternalEntityTemplate internalEntityTemplate, Directory directory, GroupTemplate groupTemplate) {
        super(internalEntityTemplate, directory);
        this.type = groupTemplate.getType();
        this.updateDetailsFrom((Group)groupTemplate);
    }

    public InternalGroup(Group group, Directory directory) {
        Validate.notNull((Object)directory, (String)"directory argument cannot be null", (Object[])new Object[0]);
        this.setName(group.getName());
        this.directory = directory;
        this.type = group.getType();
        this.externalId = group.getExternalId();
        this.updateDetailsFrom(group);
    }

    private void validateGroup(Group group) {
        Validate.notNull((Object)group, (String)"group argument cannot be null", (Object[])new Object[0]);
        Validate.notNull((Object)group.getDirectoryId(), (String)"group argument cannot have a null directoryID", (Object[])new Object[0]);
        Validate.notNull((Object)group.getName(), (String)"group argument cannot have a null name", (Object[])new Object[0]);
        Validate.notNull((Object)group.getType(), (String)"type argument cannot be null", (Object[])new Object[0]);
        Validate.isTrue((group.getDirectoryId() == this.getDirectoryId() ? 1 : 0) != 0, (String)"directoryID of updated group does not match the directoryID of the existing group.", (Object[])new Object[0]);
        Validate.isTrue((boolean)group.getName().equals(this.getName()), (String)"group name of updated group does not match the group name of the existing group.", (Object[])new Object[0]);
    }

    public void updateDetailsFrom(Group group) {
        this.validateGroup(group);
        this.active = group.isActive();
        this.description = InternalEntityUtils.truncateValue((String)group.getDescription());
        this.externalId = group.getExternalId();
    }

    public void renameTo(String newName) {
        Validate.isTrue((boolean)StringUtils.isNotBlank((CharSequence)newName), (String)"the new name cannot be null or blank", (Object[])new Object[0]);
        this.setName(newName);
    }

    protected void setName(String name) {
        InternalEntityUtils.validateLength((String)name);
        this.name = name;
        this.lowerName = IdentifierUtils.toLowerCase((String)name);
    }

    public String getDescription() {
        return this.description;
    }

    public GroupType getType() {
        return this.type;
    }

    public String getLowerName() {
        return this.lowerName;
    }

    private void setLowerName(String lowerName) {
        this.lowerName = lowerName;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    private void setType(GroupType type) {
        this.type = type;
    }

    public boolean isLocal() {
        return this.isLocal;
    }

    public void setLocal(boolean local) {
        this.isLocal = local;
    }

    private void setAttributes(Set<InternalGroupAttribute> attributes) {
        this.attributes = attributes;
    }

    @Nullable
    public String getExternalId() {
        return this.externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    private Set<GroupAdministrationGrantToGroup> getGrantsToOtherGroups() {
        return this.grantsToOtherGroups;
    }

    private void setGrantsToOtherGroups(Set<GroupAdministrationGrantToGroup> grantsToOtherGroups) {
        this.grantsToOtherGroups = grantsToOtherGroups;
    }

    private Set<GroupAdministrationGrantToGroup> getGroupGrantsToThisGroup() {
        return this.groupGrantsToThisGroup;
    }

    private void setGroupGrantsToThisGroup(Set<GroupAdministrationGrantToGroup> groupGrantsToThisGroup) {
        this.groupGrantsToThisGroup = groupGrantsToThisGroup;
    }

    private Set<UserAdministrationGrantToGroup> getUserGrantsToThisGroup() {
        return this.userGrantsToThisGroup;
    }

    private void setUserGrantsToThisGroup(Set<UserAdministrationGrantToGroup> userGrantsToThisGroup) {
        this.userGrantsToThisGroup = userGrantsToThisGroup;
    }

    public boolean equals(Object o) {
        return GroupComparator.equalsObject((Group)this, (Object)o);
    }

    public int hashCode() {
        return GroupComparator.hashCode((Group)this);
    }

    public int compareTo(Group o) {
        return GroupComparator.compareTo((Group)this, (Group)o);
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("id", (Object)this.getId()).append("name", (Object)this.getName()).append("type", (Object)this.getType()).append("active", this.isActive()).append("description", (Object)this.getDescription()).append("lowerName", (Object)this.getLowerName()).append("createdDate", (Object)this.getCreatedDate()).append("updatedDate", (Object)this.getUpdatedDate()).append("directoryId", this.getDirectoryId()).append("externalId", (Object)this.getExternalId()).toString();
    }
}

