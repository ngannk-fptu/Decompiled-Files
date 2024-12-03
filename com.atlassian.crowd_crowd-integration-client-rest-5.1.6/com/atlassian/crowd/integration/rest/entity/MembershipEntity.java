/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.DirectoryEntity
 *  com.atlassian.crowd.model.group.Membership
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 */
package com.atlassian.crowd.integration.rest.entity;

import com.atlassian.crowd.integration.rest.entity.GroupEntityList;
import com.atlassian.crowd.integration.rest.entity.UserEntityList;
import com.atlassian.crowd.model.DirectoryEntity;
import com.atlassian.crowd.model.group.Membership;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class MembershipEntity
implements Membership {
    private String group;
    private UserEntityList users;
    private GroupEntityList groups;

    public MembershipEntity() {
        this(new UserEntityList(Collections.emptyList()), new GroupEntityList(Collections.emptyList()));
    }

    public MembershipEntity(UserEntityList users, GroupEntityList groups) {
        this.users = users;
        this.groups = groups;
    }

    public String toString() {
        return this.group + "={users:" + this.users + ",groups:" + this.groups + "}";
    }

    @XmlAttribute(name="group")
    public String getGroupName() {
        return this.group;
    }

    public void setGroupName(String name) {
        this.group = name;
    }

    @XmlElement(name="users")
    public UserEntityList getUsers() {
        return this.users;
    }

    @XmlElement(name="groups")
    public GroupEntityList getGroups() {
        return this.groups;
    }

    public void setUsers(UserEntityList users) {
        this.users = users;
    }

    public void setGroups(GroupEntityList childGroups) {
        this.groups = childGroups;
    }

    public Set<String> getUserNames() {
        return MembershipEntity.namesOf(this.users);
    }

    public Set<String> getChildGroupNames() {
        return MembershipEntity.namesOf(this.groups);
    }

    private static Set<String> namesOf(Iterable<? extends DirectoryEntity> entities) {
        HashSet<String> names = new HashSet<String>();
        for (DirectoryEntity directoryEntity : entities) {
            names.add(directoryEntity.getName());
        }
        return names;
    }
}

