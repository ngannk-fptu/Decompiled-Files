/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.module.propertyset.PropertySet
 *  javax.ejb.CreateException
 *  javax.ejb.EJBObject
 */
package com.opensymphony.user.provider.ejb;

import com.opensymphony.module.propertyset.PropertySet;
import java.rmi.RemoteException;
import java.util.List;
import javax.ejb.CreateException;
import javax.ejb.EJBObject;

public interface UserManager
extends EJBObject {
    public List getGroupNames() throws RemoteException;

    public PropertySet getGroupPropertySet(String var1) throws RemoteException;

    public List getUserGroups(String var1) throws RemoteException;

    public boolean isUserInGroup(String var1, String var2) throws RemoteException;

    public List getUserNames() throws RemoteException;

    public PropertySet getUserPropertySet(String var1) throws RemoteException;

    public List getUsersInGroup(String var1) throws RemoteException;

    public boolean addToGroup(String var1, String var2) throws RemoteException;

    public boolean authenticate(String var1, String var2) throws RemoteException;

    public boolean changePassword(String var1, String var2) throws RemoteException;

    public boolean createGroup(String var1) throws CreateException, RemoteException;

    public boolean createUser(String var1) throws CreateException, RemoteException;

    public boolean groupExists(String var1) throws RemoteException;

    public boolean removeFromGroup(String var1, String var2) throws RemoteException;

    public boolean removeGroup(String var1) throws RemoteException;

    public boolean removeUser(String var1) throws RemoteException;

    public boolean userExists(String var1) throws RemoteException;
}

