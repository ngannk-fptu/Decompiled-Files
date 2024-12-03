/*
 * Decompiled with CFR 0.152.
 */
package javax.ejb;

import java.rmi.RemoteException;
import javax.ejb.EJBException;
import javax.ejb.EnterpriseBean;
import javax.ejb.SessionContext;

public interface SessionBean
extends EnterpriseBean {
    public void setSessionContext(SessionContext var1) throws EJBException, RemoteException;

    public void ejbRemove() throws EJBException, RemoteException;

    public void ejbActivate() throws EJBException, RemoteException;

    public void ejbPassivate() throws EJBException, RemoteException;
}

