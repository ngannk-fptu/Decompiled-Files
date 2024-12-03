/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBException
 *  javax.ejb.EntityBean
 *  javax.ejb.EntityContext
 *  javax.ejb.RemoveException
 */
package com.opensymphony.ejb;

import com.opensymphony.ejb.AbstractEntityAdapter;
import java.rmi.RemoteException;
import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.RemoveException;

public abstract class EntityAdapter
extends AbstractEntityAdapter
implements EntityBean {
    public void setEntityContext(EntityContext context) throws EJBException, RemoteException {
        this.setContext(context);
    }

    public void ejbActivate() throws EJBException, RemoteException {
    }

    public void ejbLoad() throws EJBException, RemoteException {
    }

    public void ejbPassivate() throws EJBException, RemoteException {
    }

    public void ejbRemove() throws RemoveException, EJBException, RemoteException {
    }

    public void ejbStore() throws EJBException, RemoteException {
    }

    public void unsetEntityContext() throws EJBException, RemoteException {
        this.context = null;
    }

    protected EntityContext getEntityContext() {
        return this.context;
    }
}

