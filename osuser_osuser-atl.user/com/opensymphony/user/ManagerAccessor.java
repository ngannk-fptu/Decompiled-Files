/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.user;

import com.opensymphony.user.UserManager;
import com.opensymphony.user.provider.AccessProvider;
import com.opensymphony.user.provider.CredentialsProvider;
import com.opensymphony.user.provider.ProfileProvider;
import java.io.Serializable;

public interface ManagerAccessor
extends Serializable {
    public UserManager getUserManager();

    public AccessProvider getAccessProvider(String var1);

    public CredentialsProvider getCredentialsProvider(String var1);

    public ProfileProvider getProfileProvider(String var1);
}

