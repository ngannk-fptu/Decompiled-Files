/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.user.provider.hibernate.impl;

import com.opensymphony.user.provider.hibernate.entity.BaseHibernateEntity;
import com.opensymphony.user.provider.hibernate.entity.HibernateGroup;
import com.opensymphony.user.provider.hibernate.entity.HibernateUser;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class HibernateGroupImpl
extends BaseHibernateEntity
implements HibernateGroup {
    private Set users = null;

    public List getUserList() {
        ArrayList userList = new ArrayList();
        if (this.users != null) {
            userList.addAll(this.users);
        }
        return userList;
    }

    public List getUserNameList() {
        ArrayList<String> userNameList = new ArrayList<String>();
        if (this.users != null && this.users.size() > 0) {
            Iterator userIter = this.users.iterator();
            while (userIter.hasNext()) {
                HibernateUser user = (HibernateUser)userIter.next();
                userNameList.add(user.getName());
            }
        }
        return userNameList;
    }

    public void setUsers(Set users) {
        this.users = users;
    }

    public Set getUsers() {
        return this.users;
    }

    public void addUser(HibernateUser user) {
        if (this.users == null) {
            this.users = new HashSet(1);
        }
        this.users.add(user);
    }

    public boolean removeUser(HibernateUser user) {
        if (user != null) {
            return this.users.remove(user);
        }
        return false;
    }

    public boolean removeUser(String username) {
        if (this.users != null) {
            Iterator userIter = this.users.iterator();
            while (userIter.hasNext()) {
                HibernateUser user = (HibernateUser)userIter.next();
                if (!user.getName().equals(username)) continue;
                return this.removeUser(user);
            }
        }
        return false;
    }
}

