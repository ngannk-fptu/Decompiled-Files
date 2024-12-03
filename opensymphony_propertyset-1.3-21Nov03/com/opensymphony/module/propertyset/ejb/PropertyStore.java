/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBObject
 */
package com.opensymphony.module.propertyset.ejb;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Collection;
import javax.ejb.EJBObject;

public interface PropertyStore
extends EJBObject {
    public Collection getKeys(String var1, long var2, String var4, int var5) throws RemoteException;

    public int getType(String var1, long var2, String var4) throws RemoteException;

    public boolean exists(String var1, long var2, String var4) throws RemoteException;

    public Serializable get(String var1, long var2, int var4, String var5) throws RemoteException;

    public void removeEntry(String var1, long var2, String var4) throws RemoteException;

    public void set(String var1, long var2, int var4, String var5, Serializable var6) throws RemoteException;
}

