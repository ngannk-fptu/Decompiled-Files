/*
 * Decompiled with CFR 0.152.
 */
package javax.ejb;

import java.io.Serializable;
import java.rmi.RemoteException;
import javax.ejb.EJBObject;

public interface Handle
extends Serializable {
    public EJBObject getEJBObject() throws RemoteException;
}

