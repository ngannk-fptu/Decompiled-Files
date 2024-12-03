/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.rmi;

import com.mchange.rmi.ServiceUnavailableException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CallingCard {
    public Remote findRemote() throws ServiceUnavailableException, RemoteException;

    public boolean equals(Object var1);

    public int hashCode();

    public String toString();
}

