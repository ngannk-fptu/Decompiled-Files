/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.security;

import com.hazelcast.security.Credentials;
import com.hazelcast.security.Parameters;
import java.security.AccessControlException;

public interface SecurityInterceptor {
    public void before(Credentials var1, String var2, String var3, String var4, Parameters var5) throws AccessControlException;

    public void after(Credentials var1, String var2, String var3, String var4, Parameters var5);
}

