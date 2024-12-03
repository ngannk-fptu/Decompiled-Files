/*
 * Decompiled with CFR 0.152.
 */
package javax.ejb;

import java.rmi.Remote;
import java.rmi.RemoteException;
import javax.ejb.EJBHome;
import javax.ejb.Handle;
import javax.ejb.RemoveException;

public interface EJBObject
extends Remote {
    public EJBHome getEJBHome() throws RemoteException;

    public Object getPrimaryKey() throws RemoteException;

    public void remove() throws RemoteException, RemoveException;

    public Handle getHandle() throws RemoteException;

    public boolean isIdentical(EJBObject var1) throws RemoteException;
}

