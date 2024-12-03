/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.ejb.ExceptionlessEntityAdapter
 *  com.opensymphony.module.propertyset.PropertySet
 *  javax.ejb.CreateException
 *  javax.ejb.EntityBean
 *  javax.ejb.EntityContext
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.user.provider.ejb.entity;

import com.opensymphony.ejb.ExceptionlessEntityAdapter;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.user.provider.ejb.entity.GroupLocal;
import com.opensymphony.user.provider.ejb.util.Base64;
import com.opensymphony.user.provider.ejb.util.PasswordDigester;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.ejb.CreateException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class UserEJB
extends ExceptionlessEntityAdapter
implements EntityBean {
    private static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$user$provider$ejb$entity$UserEJB == null ? (class$com$opensymphony$user$provider$ejb$entity$UserEJB = UserEJB.class$("com.opensymphony.user.provider.ejb.entity.UserEJB")) : class$com$opensymphony$user$provider$ejb$entity$UserEJB));
    static /* synthetic */ Class class$com$opensymphony$user$provider$ejb$entity$UserEJB;

    public void setEntityContext(EntityContext context) {
        this.setContext(context);
    }

    public abstract void setId(Long var1);

    public abstract Long getId();

    public abstract void setName(String var1);

    public abstract String getName();

    public abstract void setPasswordHash(String var1);

    public abstract String getPasswordHash();

    public List getGroupNames() {
        Iterator iter = this.getGroups().iterator();
        ArrayList<String> list = new ArrayList<String>();
        while (iter.hasNext()) {
            GroupLocal group = (GroupLocal)iter.next();
            list.add(group.getName());
        }
        return list;
    }

    public abstract void setGroups(Set var1);

    public abstract Set getGroups();

    public void setPassword(String password) {
        this.setPasswordHash(this.createHash(password));
    }

    public PropertySet getPropertySet() {
        try {
            return this.locatePropertySet(this.getId());
        }
        catch (RemoteException e) {
            log.error((Object)"Unable to look up propertyset", (Throwable)e);
            return null;
        }
    }

    public boolean authenticate(String password) {
        if (password == null || this.getPasswordHash() == null || password.length() == 0) {
            return false;
        }
        return this.compareHash(this.getPasswordHash(), password);
    }

    public Long ejbCreate(String name) throws CreateException {
        try {
            Long id = new Long(this.nextLong());
            this.setId(id);
        }
        catch (RemoteException e) {
            throw new CreateException("Unable to obtain id:" + e);
        }
        this.setName(name);
        return null;
    }

    public void ejbPostCreate(String name) {
    }

    public boolean inGroup(String groupName) {
        Iterator iter = this.getGroups().iterator();
        while (iter.hasNext()) {
            GroupLocal group = (GroupLocal)iter.next();
            if (!group.getName().equals(groupName)) continue;
            return true;
        }
        return false;
    }

    public boolean removeGroup(String name) {
        Iterator iter = this.getGroups().iterator();
        while (iter.hasNext()) {
            GroupLocal group = (GroupLocal)iter.next();
            if (!group.getName().equals(name)) continue;
            iter.remove();
            return true;
        }
        return false;
    }

    public void unsetEntityContext() {
        this.context = null;
    }

    private boolean compareHash(String hashedValue, String unhashedValue) {
        return hashedValue.equals(this.createHash(unhashedValue));
    }

    private String createHash(String original) {
        byte[] digested = PasswordDigester.digest(original.getBytes());
        byte[] encoded = Base64.encode(digested);
        return new String(encoded);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

