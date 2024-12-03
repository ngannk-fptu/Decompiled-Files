/*
 * Decompiled with CFR 0.152.
 */
package javax.ejb;

import java.rmi.RemoteException;
import javax.ejb.EJBException;

public interface SessionSynchronization {
    public void afterBegin() throws EJBException, RemoteException;

    public void beforeCompletion() throws EJBException, RemoteException;

    public void afterCompletion(boolean var1) throws EJBException, RemoteException;
}

