/*
 * Decompiled with CFR 0.152.
 */
package javax.ejb;

import java.rmi.Remote;
import java.rmi.RemoteException;
import javax.ejb.EJBMetaData;
import javax.ejb.Handle;
import javax.ejb.HomeHandle;
import javax.ejb.RemoveException;

public interface EJBHome
extends Remote {
    public void remove(Handle var1) throws RemoteException, RemoveException;

    public void remove(Object var1) throws RemoteException, RemoveException;

    public EJBMetaData getEJBMetaData() throws RemoteException;

    public HomeHandle getHomeHandle() throws RemoteException;
}

