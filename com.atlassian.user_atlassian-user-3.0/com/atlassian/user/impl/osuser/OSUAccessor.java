/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.user.ManagerAccessor
 *  com.opensymphony.user.provider.AccessProvider
 *  com.opensymphony.user.provider.CredentialsProvider
 *  com.opensymphony.user.provider.ProfileProvider
 */
package com.atlassian.user.impl.osuser;

import com.opensymphony.user.ManagerAccessor;
import com.opensymphony.user.provider.AccessProvider;
import com.opensymphony.user.provider.CredentialsProvider;
import com.opensymphony.user.provider.ProfileProvider;

public interface OSUAccessor
extends ManagerAccessor {
    public AccessProvider getAccessProvider();

    public CredentialsProvider getCredentialsProvider();

    public ProfileProvider getProfileProvider();

    public void setCredentialsProvider(CredentialsProvider var1);

    public void setAccessProvider(AccessProvider var1);

    public void setProfileProvider(ProfileProvider var1);
}

