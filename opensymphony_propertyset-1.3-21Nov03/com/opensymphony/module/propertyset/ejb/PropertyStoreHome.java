/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBHome
 */
package com.opensymphony.module.propertyset.ejb;

import com.opensymphony.module.propertyset.ejb.PropertyStore;
import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.ejb.EJBHome;

public interface PropertyStoreHome
extends EJBHome {
    public PropertyStore create() throws CreateException, RemoteException;
}

