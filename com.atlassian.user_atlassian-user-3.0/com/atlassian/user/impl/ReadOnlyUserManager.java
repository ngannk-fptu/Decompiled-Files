/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl;

import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import com.atlassian.user.UserManager;
import com.atlassian.user.security.password.Credential;

public abstract class ReadOnlyUserManager
implements UserManager {
    public User createUser(String username) throws EntityException {
        throw new UnsupportedOperationException("Cannot write to read-only UserManager [" + this.getIdentifier().getKey() + "]");
    }

    public User createUser(User userTemplate, Credential credential) throws EntityException {
        throw new UnsupportedOperationException("Cannot write to read-only UserManager [" + this.getIdentifier().getKey() + "]");
    }

    public void alterPassword(User user, String plainTextPass) throws EntityException {
        throw new UnsupportedOperationException("Cannot write to read-only UserManager [" + this.getIdentifier().getKey() + "]");
    }

    public void saveUser(User user) throws EntityException {
        throw new UnsupportedOperationException("Cannot write to read-only UserManager [" + this.getIdentifier().getKey() + "]");
    }

    public void removeUser(User user) throws EntityException {
        throw new UnsupportedOperationException("Cannot write to read-only UserManager [" + this.getIdentifier().getKey() + "]");
    }

    public boolean isReadOnly(User user) throws EntityException {
        return this.getUser(user.getName()) != null;
    }

    public boolean isCreative() {
        return false;
    }
}

