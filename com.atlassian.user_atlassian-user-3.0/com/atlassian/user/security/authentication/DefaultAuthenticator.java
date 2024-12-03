/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.security.authentication;

import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import com.atlassian.user.UserManager;
import com.atlassian.user.impl.DefaultUser;
import com.atlassian.user.repository.RepositoryIdentifier;
import com.atlassian.user.security.authentication.Authenticator;
import com.atlassian.user.security.authentication.EntityAuthenticationException;
import com.atlassian.user.security.password.PasswordEncryptor;

public class DefaultAuthenticator
implements Authenticator {
    private final UserManager userManager;
    private final PasswordEncryptor encryptor;

    public DefaultAuthenticator(UserManager userManager, PasswordEncryptor encryptor) {
        this.userManager = userManager;
        this.encryptor = encryptor;
    }

    public boolean authenticate(String username, String password) throws EntityException {
        User user = this.userManager.getUser(username);
        if (user == null) {
            return false;
        }
        if (!(user instanceof DefaultUser)) {
            return false;
        }
        DefaultUser defaultUser = (DefaultUser)user;
        try {
            return defaultUser.getPassword() != null && defaultUser.getPassword().equals(this.encryptor.encrypt(password));
        }
        catch (Exception e) {
            throw new EntityAuthenticationException(e);
        }
    }

    public RepositoryIdentifier getRepository() {
        return this.userManager.getIdentifier();
    }
}

