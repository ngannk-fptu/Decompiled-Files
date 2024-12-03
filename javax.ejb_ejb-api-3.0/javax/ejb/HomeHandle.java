/*
 * Decompiled with CFR 0.152.
 */
package javax.ejb;

import java.io.Serializable;
import java.rmi.RemoteException;
import javax.ejb.EJBHome;

public interface HomeHandle
extends Serializable {
    public EJBHome getEJBHome() throws RemoteException;
}

