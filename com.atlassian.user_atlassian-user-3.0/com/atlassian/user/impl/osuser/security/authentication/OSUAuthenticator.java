/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.user.authenticator.AuthenticationException
 *  com.opensymphony.user.authenticator.Authenticator
 *  com.opensymphony.user.provider.CredentialsProvider
 */
package com.atlassian.user.impl.osuser.security.authentication;

import com.atlassian.user.EntityException;
import com.atlassian.user.repository.RepositoryIdentifier;
import com.atlassian.user.security.authentication.EntityAuthenticationException;
import com.opensymphony.user.authenticator.AuthenticationException;
import com.opensymphony.user.authenticator.Authenticator;
import com.opensymphony.user.provider.CredentialsProvider;

public class OSUAuthenticator
implements com.atlassian.user.security.authentication.Authenticator {
    private final RepositoryIdentifier repository;
    private final CredentialsProvider credentialsProvider;
    private final Authenticator osuserAuthenticator;

    public OSUAuthenticator(RepositoryIdentifier repository, CredentialsProvider credentialsProvider) {
        this(repository, credentialsProvider, null);
    }

    public OSUAuthenticator(RepositoryIdentifier repository, Authenticator osuserAuthenticator) {
        this(repository, null, osuserAuthenticator);
    }

    private OSUAuthenticator(RepositoryIdentifier repository, CredentialsProvider credentialsProvider, Authenticator osuserAuthenticator) {
        this.repository = repository;
        this.credentialsProvider = credentialsProvider;
        this.osuserAuthenticator = osuserAuthenticator;
    }

    public boolean authenticate(String username, String password) throws EntityException {
        if (this.isWrappingCredentialProvider()) {
            return this.credentialsProvider.authenticate(username, password);
        }
        try {
            return this.osuserAuthenticator.login(username, password);
        }
        catch (AuthenticationException e) {
            throw new EntityAuthenticationException(e);
        }
    }

    public RepositoryIdentifier getRepository() {
        return this.repository;
    }

    public boolean isWrappingCredentialProvider() {
        return this.credentialsProvider != null;
    }
}

