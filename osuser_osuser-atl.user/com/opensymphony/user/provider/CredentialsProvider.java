/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.user.provider;

import com.opensymphony.user.provider.UserProvider;

public interface CredentialsProvider
extends UserProvider {
    public boolean authenticate(String var1, String var2);

    public boolean changePassword(String var1, String var2);
}

