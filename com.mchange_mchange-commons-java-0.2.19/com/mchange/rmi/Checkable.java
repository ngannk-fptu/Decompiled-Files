/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.rmi;

import com.mchange.rmi.ServiceUnavailableException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Checkable
extends Remote {
    public void check() throws ServiceUnavailableException, RemoteException;
}

