/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBHome
 */
package com.opensymphony.user.provider.ejb;

import com.opensymphony.user.provider.ejb.UserManager;
import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.ejb.EJBHome;

public interface UserManagerHome
extends EJBHome {
    public UserManager create() throws CreateException, RemoteException;
}

