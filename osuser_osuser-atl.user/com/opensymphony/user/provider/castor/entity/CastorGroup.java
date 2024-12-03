/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.user.provider.castor.entity;

import com.opensymphony.user.provider.castor.entity.BaseCastorEntity;
import com.opensymphony.user.provider.castor.entity.CastorUser;
import java.util.ArrayList;
import java.util.Iterator;

public class CastorGroup
extends BaseCastorEntity {
    private ArrayList users = null;

    public void setUsers(ArrayList users) {
        if (users != null) {
            this.users = new ArrayList(users);
        }
    }

    public ArrayList getUsers() {
        return this.users;
    }

    public void addUser(CastorUser user) {
        if (this.users == null) {
            this.users = new ArrayList();
        }
        this.users.add(user);
    }

    public boolean removeUser(CastorUser user) {
        if (user != null) {
            return this.users.remove(user);
        }
        return false;
    }

    public boolean removeUser(String username) {
        if (this.users != null) {
            Iterator userIter = this.users.iterator();
            while (userIter.hasNext()) {
                CastorUser user = (CastorUser)userIter.next();
                if (!user.getName().equals(username)) continue;
                return this.removeUser(user);
            }
        }
        return false;
    }
}

