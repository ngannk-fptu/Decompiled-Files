/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.security.authentication;

import com.atlassian.user.EntityException;
import com.atlassian.user.repository.RepositoryIdentifier;

public interface Authenticator {
    public boolean authenticate(String var1, String var2) throws EntityException;

    public RepositoryIdentifier getRepository();
}

