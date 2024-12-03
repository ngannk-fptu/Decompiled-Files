/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.user.provider.CredentialsProvider
 */
package com.atlassian.user.impl.osuser.security.authentication;

import com.atlassian.user.EntityException;
import com.atlassian.user.repository.RepositoryIdentifier;
import com.atlassian.user.security.authentication.Authenticator;
import com.opensymphony.user.provider.CredentialsProvider;
import java.util.List;

public class OSUListOfCredentialProvidersAuthenticator
implements Authenticator {
    private final RepositoryIdentifier repository;
    private final List credentialProviders;

    public OSUListOfCredentialProvidersAuthenticator(RepositoryIdentifier repository, List credentialProviders) {
        this.repository = repository;
        this.credentialProviders = credentialProviders;
    }

    public boolean authenticate(String username, String password) throws EntityException {
        for (CredentialsProvider provider : this.credentialProviders) {
            if (!provider.handles(username)) continue;
            return provider.authenticate(username, password);
        }
        return false;
    }

    public RepositoryIdentifier getRepository() {
        return this.repository;
    }
}

