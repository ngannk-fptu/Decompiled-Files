/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.ejb.AbstractEntityAdapter
 *  com.opensymphony.module.propertyset.PropertySet
 *  javax.ejb.CreateException
 *  javax.ejb.EntityBean
 *  javax.ejb.EntityContext
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.user.provider.ejb.entity;

import com.opensymphony.ejb.AbstractEntityAdapter;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.user.provider.ejb.entity.UserLocal;
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

public abstract class GroupEJB
extends AbstractEntityAdapter
implements EntityBean {
    private static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$user$provider$ejb$entity$GroupEJB == null ? (class$com$opensymphony$user$provider$ejb$entity$GroupEJB = GroupEJB.class$("com.opensymphony.user.provider.ejb.entity.GroupEJB")) : class$com$opensymphony$user$provider$ejb$entity$GroupEJB));
    static /* synthetic */ Class class$com$opensymphony$user$provider$ejb$entity$GroupEJB;

    public void setEntityContext(EntityContext context) {
        this.setContext(context);
    }

    public abstract void setId(Long var1);

    public abstract Long getId();

    public abstract void setName(String var1);

    public abstract String getName();

    public PropertySet getPropertySet() {
        try {
            return this.locatePropertySet(this.getId());
        }
        catch (RemoteException e) {
            log.error((Object)"Unable to look up propertyset", (Throwable)e);
            return null;
        }
    }

    public abstract void setUsers(Set var1);

    public abstract Set getUsers();

    public List getUserNames() {
        Iterator iter = this.getUsers().iterator();
        ArrayList<String> list = new ArrayList<String>();
        while (iter.hasNext()) {
            UserLocal user = (UserLocal)iter.next();
            list.add(user.getName());
        }
        return list;
    }

    public Long ejbCreate(String name) throws CreateException {
        try {
            this.setId(new Long(this.nextLong()));
        }
        catch (RemoteException e) {
            throw new CreateException("Unable to obtain id:" + e);
        }
        this.setName(name);
        return null;
    }

    public void ejbPostCreate(String name) {
    }

    public void unsetEntityContext() {
        this.context = null;
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

