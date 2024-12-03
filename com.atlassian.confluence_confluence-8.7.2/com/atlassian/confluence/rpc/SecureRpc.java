/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.rpc;

import com.atlassian.confluence.rpc.AuthenticationFailedException;
import com.atlassian.confluence.rpc.RemoteException;

public interface SecureRpc {
    public String login(String var1, String var2) throws AuthenticationFailedException, RemoteException;

    public boolean logout(String var1) throws RemoteException;
}

