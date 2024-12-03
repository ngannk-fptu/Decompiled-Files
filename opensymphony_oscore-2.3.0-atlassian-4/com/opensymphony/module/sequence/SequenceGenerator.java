/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBObject
 */
package com.opensymphony.module.sequence;

import java.rmi.RemoteException;
import javax.ejb.EJBObject;

public interface SequenceGenerator
extends EJBObject {
    public long getCount(String var1) throws RemoteException;
}

