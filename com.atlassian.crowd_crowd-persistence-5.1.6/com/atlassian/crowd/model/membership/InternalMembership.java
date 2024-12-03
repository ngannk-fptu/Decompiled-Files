/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.model.DirectoryEntity
 *  com.atlassian.crowd.model.directory.DirectoryImpl
 *  com.atlassian.crowd.model.group.GroupType
 *  com.atlassian.crowd.model.membership.MembershipType
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.Validate
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.crowd.model.membership;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.model.DirectoryEntity;
import com.atlassian.crowd.model.directory.DirectoryImpl;
import com.atlassian.crowd.model.group.GroupType;
import com.atlassian.crowd.model.group.InternalGroup;
import com.atlassian.crowd.model.membership.MembershipType;
import com.atlassian.crowd.model.user.InternalUser;
import com.atlassian.crowd.model.user.MinimalUser;
import java.io.Serializable;
import java.util.Date;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class InternalMembership
implements Serializable {
    private Long id;
    private Long parentId;
    private Long childId;
    private MembershipType membershipType;
    protected Date createdDate;
    private String parentName;
    private String lowerParentName;
    private String childName;
    private String lowerChildName;
    private GroupType groupType;
    private Directory directory;

    protected InternalMembership() {
    }

    public InternalMembership(Long id, Long parentId, Long childId, MembershipType membershipType, GroupType groupType, String parentName, String childName, Directory directory, @Nullable Date createdDate) {
        this.id = id;
        this.parentId = parentId;
        this.childId = childId;
        this.membershipType = membershipType;
        this.groupType = groupType;
        this.setParentName(parentName);
        this.setChildName(childName);
        this.createdDate = createdDate;
        this.directory = directory;
    }

    private InternalMembership(InternalGroup parent, DirectoryEntity child, Date createdDate, long childId, MembershipType type) {
        Validate.notNull((Object)((Object)parent), (String)"group argument cannot be null", (Object[])new Object[0]);
        Validate.notNull((Object)child, (String)"child argument cannot be null", (Object[])new Object[0]);
        Validate.isTrue((parent.getDirectoryId() == child.getDirectoryId() ? 1 : 0) != 0, (String)"directoryIDs of the parent and child do not match", (Object[])new Object[0]);
        this.parentId = parent.getId();
        this.childId = childId;
        this.membershipType = type;
        this.groupType = parent.getType();
        this.setParentName(parent.getName());
        this.setChildName(child.getName());
        this.createdDate = createdDate;
        this.directory = parent.getDirectory();
    }

    public InternalMembership(InternalGroup parentGroup, InternalUser user, Date createdDate) {
        this(parentGroup, user, createdDate, user.getId(), MembershipType.GROUP_USER);
    }

    public InternalMembership(InternalGroup parentGroup, MinimalUser user, Date createdDate) {
        this(parentGroup, user, createdDate, user.getId(), MembershipType.GROUP_USER);
    }

    public InternalMembership(InternalGroup parentGroup, InternalGroup childGroup, Date createdDate) {
        this(parentGroup, childGroup, createdDate, childGroup.getId(), MembershipType.GROUP_GROUP);
        Validate.isTrue((boolean)parentGroup.getType().equals((Object)childGroup.getType()), (String)"groupTypes of the parent and child group do not match", (Object[])new Object[0]);
    }

    public Long getId() {
        return this.id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return this.parentId;
    }

    public Long getChildId() {
        return this.childId;
    }

    public MembershipType getMembershipType() {
        return this.membershipType;
    }

    public String getParentName() {
        return this.parentName;
    }

    public String getChildName() {
        return this.childName;
    }

    public Directory getDirectory() {
        return this.directory;
    }

    public GroupType getGroupType() {
        return this.groupType;
    }

    public String getLowerParentName() {
        return this.lowerParentName;
    }

    public String getLowerChildName() {
        return this.lowerChildName;
    }

    private void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    private void setChildId(Long childId) {
        this.childId = childId;
    }

    private void setMembershipType(MembershipType membershipType) {
        this.membershipType = membershipType;
    }

    private void setParentName(String parentName) {
        this.parentName = parentName;
        this.lowerParentName = IdentifierUtils.toLowerCase((String)parentName);
    }

    private void setChildName(String childName) {
        this.childName = childName;
        this.lowerChildName = IdentifierUtils.toLowerCase((String)childName);
    }

    private void setDirectory(DirectoryImpl directory) {
        this.directory = directory;
    }

    private void setGroupType(GroupType groupType) {
        this.groupType = groupType;
    }

    private void setLowerParentName(String lowerParentName) {
        this.lowerParentName = lowerParentName;
    }

    private void setLowerChildName(String lowerChildName) {
        this.lowerChildName = lowerChildName;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    protected void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InternalMembership)) {
            return false;
        }
        InternalMembership that = (InternalMembership)o;
        if (this.getChildId() != null ? !this.getChildId().equals(that.getChildId()) : that.getChildId() != null) {
            return false;
        }
        if (this.getParentId() != null ? !this.getParentId().equals(that.getParentId()) : that.getParentId() != null) {
            return false;
        }
        return this.getMembershipType() == that.getMembershipType();
    }

    public int hashCode() {
        int result = this.getParentId() != null ? this.getParentId().hashCode() : 0;
        result = 31 * result + (this.getChildId() != null ? this.getChildId().hashCode() : 0);
        result = 31 * result + (this.getMembershipType() != null ? this.getMembershipType().hashCode() : 0);
        return result;
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("parentId", (Object)this.getParentId()).append("childId", (Object)this.getChildId()).append("membershipType", (Object)this.getMembershipType()).append("groupType", (Object)this.getGroupType()).append("parentName", (Object)this.getParentName()).append("lowerParentName", (Object)this.getLowerParentName()).append("childName", (Object)this.getChildName()).append("lowerChildName", (Object)this.getLowerChildName()).append("directoryId", (Object)this.getDirectory().getId()).toString();
    }
}

