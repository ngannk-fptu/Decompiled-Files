/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util;

import com.mchange.util.PasswordManager;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemotePasswordManager
extends PasswordManager,
Remote {
    @Override
    public boolean validate(String var1, String var2) throws RemoteException, IOException;

    @Override
    public boolean updatePassword(String var1, String var2, String var3) throws RemoteException, IOException;
}

