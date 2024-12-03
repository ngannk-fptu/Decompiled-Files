/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.user.provider.hibernate;

import com.opensymphony.user.provider.CredentialsProvider;
import com.opensymphony.user.provider.hibernate.HibernateBaseProvider;
import com.opensymphony.user.provider.hibernate.entity.HibernateUser;
import com.opensymphony.user.provider.hibernate.impl.HibernateUserImpl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HibernateCredentialsProvider
extends HibernateBaseProvider
implements CredentialsProvider {
    public boolean authenticate(String name, String password) {
        boolean result = false;
        HibernateUser user = this.getUserDAO().findUserByUsername(name);
        result = user != null ? user.authenticate(password.trim()) : false;
        return result;
    }

    public boolean changePassword(String name, String password) {
        boolean result = false;
        HibernateUser user = this.getUserDAO().findUserByUsername(name);
        if (user != null) {
            user.setPassword(password.trim());
            result = this.getUserDAO().updateUser(user);
        } else {
            result = false;
        }
        return result;
    }

    public boolean create(String name) {
        HibernateUserImpl user = new HibernateUserImpl();
        user.setName(name);
        return this.getUserDAO().saveUser(user);
    }

    public void flushCaches() {
    }

    public boolean handles(String name) {
        boolean result = false;
        HibernateUser user = this.getUserDAO().findUserByUsername(name);
        if (user != null) {
            result = true;
        }
        return result;
    }

    public List list() {
        List users = this.getUserDAO().findUsers();
        ArrayList<String> ret = new ArrayList<String>();
        for (int i = 0; i < users.size(); ++i) {
            HibernateUser user = (HibernateUser)users.get(i);
            ret.add(user.getName());
        }
        return Collections.unmodifiableList(ret);
    }

    public boolean remove(String name) {
        int numberDeleted = this.getUserDAO().deleteUserByUsername(name);
        return numberDeleted > 0;
    }
}

