/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.rmi;

import java.rmi.AccessException;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RegistryManager {
    public static Registry ensureRegistry(int n) throws RemoteException {
        Registry registry = RegistryManager.findRegistry(n);
        if (registry == null) {
            registry = LocateRegistry.createRegistry(n);
        }
        return registry;
    }

    public static Registry ensureRegistry() throws RemoteException {
        return RegistryManager.ensureRegistry(1099);
    }

    public static boolean registryAvailable(int n) throws RemoteException, AccessException {
        try {
            Registry registry = LocateRegistry.getRegistry(n);
            registry.list();
            return true;
        }
        catch (ConnectException connectException) {
            return false;
        }
    }

    public static boolean registryAvailable() throws RemoteException, AccessException {
        return RegistryManager.registryAvailable(1099);
    }

    public static Registry findRegistry(int n) throws RemoteException, AccessException {
        if (!RegistryManager.registryAvailable(n)) {
            return null;
        }
        return LocateRegistry.getRegistry(n);
    }

    public static Registry findRegistry() throws RemoteException, AccessException {
        return RegistryManager.findRegistry(1099);
    }
}

