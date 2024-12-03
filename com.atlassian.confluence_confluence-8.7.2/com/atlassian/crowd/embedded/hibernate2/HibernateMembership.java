/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.group.InternalGroup
 *  com.atlassian.crowd.model.user.InternalUser
 */
package com.atlassian.crowd.embedded.hibernate2;

import com.atlassian.crowd.model.group.InternalGroup;
import com.atlassian.crowd.model.user.InternalUser;
import java.io.Serializable;

@Deprecated
public class HibernateMembership
implements Serializable {
    private Long id;
    private InternalGroup parentGroup;
    private InternalUser userMember;
    private InternalGroup groupMember;

    public static HibernateMembership groupUserMembership(InternalGroup parent, InternalUser member) {
        HibernateMembership membership = new HibernateMembership();
        membership.setParentGroup(parent);
        membership.setUserMember(member);
        return membership;
    }

    public static HibernateMembership groupGroupMembership(InternalGroup parent, InternalGroup member) {
        HibernateMembership membership = new HibernateMembership();
        membership.setParentGroup(parent);
        membership.setGroupMember(member);
        return membership;
    }

    public InternalGroup getParentGroup() {
        return this.parentGroup;
    }

    public void setParentGroup(InternalGroup parentGroup) {
        this.parentGroup = parentGroup;
    }

    public InternalUser getUserMember() {
        return this.userMember;
    }

    public void setUserMember(InternalUser userMember) {
        this.userMember = userMember;
    }

    public InternalGroup getGroupMember() {
        return this.groupMember;
    }

    public void setGroupMember(InternalGroup groupMember) {
        this.groupMember = groupMember;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String toString() {
        return "HibernateMembership{groupMember=" + this.groupMember + ", id=" + this.id + ", parentGroup=" + this.parentGroup + ", userMember=" + this.userMember + "}";
    }
}

