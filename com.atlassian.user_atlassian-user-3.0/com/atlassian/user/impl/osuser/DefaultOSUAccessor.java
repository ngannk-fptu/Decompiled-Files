/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.user.UserManager
 *  com.opensymphony.user.provider.AccessProvider
 *  com.opensymphony.user.provider.CredentialsProvider
 *  com.opensymphony.user.provider.ProfileProvider
 */
package com.atlassian.user.impl.osuser;

import com.atlassian.user.impl.osuser.OSUAccessor;
import com.opensymphony.user.UserManager;
import com.opensymphony.user.provider.AccessProvider;
import com.opensymphony.user.provider.CredentialsProvider;
import com.opensymphony.user.provider.ProfileProvider;
import java.io.Serializable;

public class DefaultOSUAccessor
implements Serializable,
OSUAccessor {
    private AccessProvider accessProvider;
    private CredentialsProvider credentialsProvider;
    private ProfileProvider profileProvider;

    public DefaultOSUAccessor() {
    }

    public DefaultOSUAccessor(AccessProvider accessProvider, CredentialsProvider credentialsProvider, ProfileProvider profileProvider) {
        this.accessProvider = accessProvider;
        this.credentialsProvider = credentialsProvider;
        this.profileProvider = profileProvider;
    }

    public UserManager getUserManager() {
        throw new UnsupportedOperationException(this.getClass().getName() + " does not make use of the osuser UserManager singleton. " + "Use IoC instead");
    }

    public AccessProvider getAccessProvider(String name) {
        if (this.accessProvider.handles(name)) {
            return this.accessProvider;
        }
        return null;
    }

    public CredentialsProvider getCredentialsProvider(String name) {
        if (this.credentialsProvider.handles(name)) {
            return this.credentialsProvider;
        }
        return null;
    }

    public ProfileProvider getProfileProvider(String name) {
        if (this.profileProvider.handles(name)) {
            return this.profileProvider;
        }
        return null;
    }

    public AccessProvider getAccessProvider() {
        return this.accessProvider;
    }

    public CredentialsProvider getCredentialsProvider() {
        return this.credentialsProvider;
    }

    public ProfileProvider getProfileProvider() {
        return this.profileProvider;
    }

    public void setAccessProvider(AccessProvider accessProvider) {
        this.accessProvider = accessProvider;
    }

    public void setCredentialsProvider(CredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
    }

    public void setProfileProvider(ProfileProvider profileProvider) {
        this.profileProvider = profileProvider;
    }
}

