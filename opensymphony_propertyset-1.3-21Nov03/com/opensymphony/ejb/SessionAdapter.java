/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBException
 *  javax.ejb.SessionBean
 *  javax.ejb.SessionContext
 */
package com.opensymphony.ejb;

import java.rmi.RemoteException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

public abstract class SessionAdapter
implements SessionBean {
    protected SessionContext context;

    public void setSessionContext(SessionContext context) throws RemoteException {
        this.context = context;
    }

    public void ejbActivate() throws EJBException, RemoteException {
    }

    public void ejbPassivate() throws EJBException, RemoteException {
    }

    public void ejbRemove() throws EJBException, RemoteException {
    }

    protected SessionContext getSessionContext() throws EJBException, RemoteException {
        return this.context;
    }
}

