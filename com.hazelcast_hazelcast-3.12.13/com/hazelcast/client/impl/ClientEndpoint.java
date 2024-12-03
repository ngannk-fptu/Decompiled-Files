/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl;

import com.hazelcast.client.impl.client.ClientPrincipal;
import com.hazelcast.core.Client;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.Credentials;
import com.hazelcast.transaction.TransactionContext;
import java.util.Set;
import java.util.concurrent.Callable;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;

public interface ClientEndpoint
extends Client {
    public boolean isAlive();

    public void addListenerDestroyAction(String var1, String var2, String var3);

    public void addDestroyAction(String var1, Callable<Boolean> var2);

    public boolean removeDestroyAction(String var1);

    public Credentials getCredentials();

    public void setTransactionContext(TransactionContext var1);

    public TransactionContext getTransactionContext(String var1);

    public void removeTransactionContext(String var1);

    public boolean isOwnerConnection();

    public Subject getSubject();

    public void clearAllListeners();

    public Connection getConnection();

    public void setLoginContext(LoginContext var1);

    public void authenticated(ClientPrincipal var1, Credentials var2, boolean var3, String var4, long var5, String var7, Set<String> var8);

    public void authenticated(ClientPrincipal var1);

    public boolean isAuthenticated();

    public int getClientVersion();

    public void setClientVersion(String var1);

    public void setClientStatistics(String var1);

    public String getClientStatistics();
}

