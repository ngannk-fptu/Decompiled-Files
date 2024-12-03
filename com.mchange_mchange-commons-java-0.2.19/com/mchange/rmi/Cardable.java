/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.rmi;

import com.mchange.rmi.CallingCard;
import com.mchange.rmi.ServiceUnavailableException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Cardable
extends Remote {
    public CallingCard getCallingCard() throws ServiceUnavailableException, RemoteException;
}

