/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.user.provider.hibernate.impl;

import com.opensymphony.user.provider.ejb.util.Base64;
import com.opensymphony.user.provider.ejb.util.PasswordDigester;
import com.opensymphony.user.provider.hibernate.entity.BaseHibernateEntity;
import com.opensymphony.user.provider.hibernate.entity.HibernateGroup;
import com.opensymphony.user.provider.hibernate.entity.HibernateUser;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class HibernateUserImpl
extends BaseHibernateEntity
implements HibernateUser {
    private Set groups;
    private String passwordHash;

    public List getGroupList() {
        ArrayList<HibernateGroup> groupList = new ArrayList<HibernateGroup>();
        if (this.groups != null && this.groups.size() > 0) {
            Iterator groupIter = this.groups.iterator();
            while (groupIter.hasNext()) {
                HibernateGroup group = (HibernateGroup)groupIter.next();
                groupList.add(group);
            }
        }
        return groupList;
    }

    public List getGroupNameList() {
        ArrayList<String> groupNameList = new ArrayList<String>();
        if (this.groups != null && this.groups.size() > 0) {
            Iterator groupIter = this.groups.iterator();
            while (groupIter.hasNext()) {
                HibernateGroup group = (HibernateGroup)groupIter.next();
                groupNameList.add(group.getName());
            }
        }
        return groupNameList;
    }

    public void setGroups(Set groups) {
        this.groups = groups;
    }

    public Set getGroups() {
        return this.groups;
    }

    public void setPassword(String password) {
        this.setPasswordHash(this.createHash(password));
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPasswordHash() {
        return this.passwordHash;
    }

    public void addGroup(HibernateGroup group) {
        if (this.groups == null) {
            this.groups = new HashSet(1);
        }
        this.groups.add(group);
    }

    public boolean authenticate(String password) {
        if (password == null || this.getPasswordHash() == null || password.length() == 0) {
            return false;
        }
        return this.compareHash(this.getPasswordHash(), password);
    }

    public void removeGroup(HibernateGroup group) {
        if (this.groups != null && this.groups.contains(group)) {
            this.groups.remove(group);
        }
    }

    private boolean compareHash(String hashedValue, String unhashedValue) {
        return hashedValue.equals(this.createHash(unhashedValue));
    }

    private String createHash(String original) {
        byte[] digested = PasswordDigester.digest(original.getBytes());
        byte[] encoded = Base64.encode(digested);
        return new String(encoded);
    }
}

