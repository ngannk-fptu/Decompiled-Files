/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.user.provider.hibernate;

import com.opensymphony.user.provider.AccessProvider;
import com.opensymphony.user.provider.hibernate.HibernateBaseProvider;
import com.opensymphony.user.provider.hibernate.entity.HibernateGroup;
import com.opensymphony.user.provider.hibernate.entity.HibernateUser;
import com.opensymphony.user.provider.hibernate.impl.HibernateGroupImpl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class HibernateAccessProvider
extends HibernateBaseProvider
implements AccessProvider {
    public boolean addToGroup(String username, String groupname) {
        boolean result = false;
        HibernateUser user = this.getUserDAO().findUserByUsername(username);
        HibernateGroup group = this.getGroupDAO().findGroupByGroupname(groupname);
        if (user != null && group != null) {
            user.addGroup(group);
            result = this.getUserDAO().updateUser(user);
        }
        return result;
    }

    public boolean create(String name) {
        HibernateGroupImpl group = new HibernateGroupImpl();
        group.setName(name);
        return this.getGroupDAO().saveGroup(group);
    }

    public void flushCaches() {
    }

    public boolean handles(String name) {
        HibernateUser user = this.getUserDAO().findUserByUsername(name);
        if (user == null) {
            HibernateGroup group = this.getGroupDAO().findGroupByGroupname(name);
            return group != null;
        }
        return true;
    }

    public boolean inGroup(String username, String groupname) {
        boolean result = false;
        HibernateUser user = this.getUserDAO().findUserByUsernameAndGroupname(username, groupname);
        if (user != null) {
            result = true;
        }
        return result;
    }

    public List list() {
        List groups = this.getGroupDAO().findGroups();
        if (groups != null) {
            ArrayList<String> ret = new ArrayList<String>();
            for (int i = 0; i < groups.size(); ++i) {
                HibernateGroup hibernateGroup = (HibernateGroup)groups.get(i);
                ret.add(hibernateGroup.getName());
            }
            return Collections.unmodifiableList(ret);
        }
        return null;
    }

    public List listGroupsContainingUser(String username) {
        HibernateUser user = this.getUserDAO().findUserByUsername(username);
        return Collections.unmodifiableList(user.getGroupNameList());
    }

    public List listUsersInGroup(String groupname) {
        HibernateGroup group = this.getGroupDAO().findGroupByGroupname(groupname);
        return Collections.unmodifiableList(group.getUserNameList());
    }

    public boolean remove(String name) {
        int numberDeleted = this.getGroupDAO().deleteGroupByGroupname(name);
        return numberDeleted > 0;
    }

    public boolean removeFromGroup(String username, String groupname) {
        boolean result = false;
        HibernateUser user = this.getUserDAO().findUserByUsernameAndGroupname(username, groupname);
        if (user != null) {
            Iterator groupsIter = user.getGroups().iterator();
            while (groupsIter.hasNext() && !result) {
                HibernateGroup group = (HibernateGroup)groupsIter.next();
                if (!group.getName().equals(groupname)) continue;
                user.getGroups().remove(group);
                result = this.getUserDAO().updateUser(user);
            }
        } else {
            result = true;
        }
        return result;
    }
}

