/*
 * Decompiled with CFR 0.152.
 */
package javax.ejb;

import java.rmi.RemoteException;
import javax.ejb.EJBException;
import javax.ejb.EnterpriseBean;
import javax.ejb.EntityContext;
import javax.ejb.RemoveException;

public interface EntityBean
extends EnterpriseBean {
    public void setEntityContext(EntityContext var1) throws EJBException, RemoteException;

    public void unsetEntityContext() throws EJBException, RemoteException;

    public void ejbRemove() throws RemoveException, EJBException, RemoteException;

    public void ejbActivate() throws EJBException, RemoteException;

    public void ejbPassivate() throws EJBException, RemoteException;

    public void ejbLoad() throws EJBException, RemoteException;

    public void ejbStore() throws EJBException, RemoteException;
}

