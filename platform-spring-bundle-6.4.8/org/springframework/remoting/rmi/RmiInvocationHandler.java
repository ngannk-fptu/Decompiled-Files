/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.remoting.rmi;

import java.lang.reflect.InvocationTargetException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import org.springframework.lang.Nullable;
import org.springframework.remoting.support.RemoteInvocation;

@Deprecated
public interface RmiInvocationHandler
extends Remote {
    @Nullable
    public String getTargetInterfaceName() throws RemoteException;

    @Nullable
    public Object invoke(RemoteInvocation var1) throws RemoteException, NoSuchMethodException, IllegalAccessException, InvocationTargetException;
}

